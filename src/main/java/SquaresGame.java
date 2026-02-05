import java.util.*;

@SuppressWarnings("unused")
public class SquaresGame {
    private static final int MAX_SIZE = 20;
    private static final int MIN_SIZE = 3;

    private enum PlayerType { USER, COMP }
    private enum Cell { EMPTY, WHITE, BLACK }

    private static class Player {
        PlayerType type;
        char color;
        public Player(PlayerType type, char color) {
            this.type = type;
            this.color = color;
        }
    }

    private int size;
    private Cell[][] board;
    private Player player1, player2;
    private boolean gameStarted;
    private final Scanner scanner;

    public SquaresGame() {
        scanner = new Scanner(System.in);
        gameStarted = false;
    }

    private void printHelp() {
        System.out.println("Available commands:");
        System.out.println("GAME N, TYPE1 C1, TYPE2 C2 - start new game (N > 2, TYPE: user/comp, C: W/B)");
        System.out.println("MOVE X, Y - make a move (0 <= X,Y < N)");
        System.out.println("EXIT - exit the program");
        System.out.println("HELP - show this help");
    }

    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < size && y >= 0 && y < size;
    }

    private boolean isCellEmpty(int x, int y) {
        return board[x][y] == Cell.EMPTY;
    }

    private void initializeBoard() {
        board = new Cell[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = Cell.EMPTY;
            }
        }
    }

    private void printBoard() {
        System.out.print("  ");
        for (int j = 0; j < size; j++) System.out.print(j + " ");
        System.out.println();
        for (int i = 0; i < size; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < size; j++) {
                switch (board[i][j]) {
                    case WHITE -> System.out.print("W ");
                    case BLACK -> System.out.print("B ");
                    default -> System.out.print(". ");
                }
            }
            System.out.println();
        }
    }

    private char getCurrentPlayerColor() {
        int movesCount = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] != Cell.EMPTY) movesCount++;
            }
        }
        return movesCount % 2 == 0 ? player1.color : player2.color;
    }

    private boolean checkSquare(char color) {
        Cell target = color == 'W' ? Cell.WHITE : Cell.BLACK;

        for (int x1 = 0; x1 < size; x1++) {
            for (int y1 = 0; y1 < size; y1++) {
                if (board[x1][y1] != target) continue;

                for (int x2 = x1 + 1; x2 < size; x2++) {
                    for (int y2 = y1 + 1; y2 < size; y2++) {
                        if (board[x2][y2] != target) continue;

                        int dx = x2 - x1;
                        int dy = y2 - y1;

                        // проверка вершин квадрата
                        int x3 = x1 + dy;
                        int y3 = y1 - dx;
                        int x4 = x2 + dy;
                        int y4 = y2 - dx;

                        if (isValidPosition(x3, y3) && isValidPosition(x4, y4) &&
                                board[x3][y3] == target && board[x4][y4] == target) {
                            return true;
                        }

                        // поворот на 90 градусов
                        x3 = x1 - dy;
                        y3 = y1 + dx;
                        x4 = x2 - dy;
                        y4 = y2 + dx;

                        if (isValidPosition(x3, y3) && isValidPosition(x4, y4) &&
                                board[x3][y3] == target && board[x4][y4] == target) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == Cell.EMPTY) return false;
            }
        }
        return true;
    }

    private void makeMove(int x, int y, char color) {
        board[x][y] = color == 'W' ? Cell.WHITE : Cell.BLACK;
        System.out.printf("%c (%d, %d)%n", color, x, y);

        if (checkSquare(color)) {
            System.out.printf("Game finished. %c wins!%n", color);
            gameStarted = false;
            return;
        }

        if (isBoardFull()) {
            System.out.println("Game finished. Draw");
            gameStarted = false;
            return;
        }
    }

    private void computerMove(Player player) {
        // первая клетка
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (isCellEmpty(i, j)) {
                    makeMove(i, j, player.color);
                    return;
                }
            }
        }
    }

    private void processGameCommand(String[] args) {
        if (args.length != 3) {
            System.out.println("Incorrect command");
            return;
        }

        try {
            size = Integer.parseInt(args[0]);
            if (size < MIN_SIZE || size > MAX_SIZE) {
                System.out.println("Board size must be between " + MIN_SIZE + " and " + MAX_SIZE);
                return;
            }

            String[] p1Args = args[1].split(" ");
            String[] p2Args = args[2].split(" ");

            if (p1Args.length != 2 || p2Args.length != 2) {
                System.out.println("Incorrect command");
                return;
            }

            PlayerType type1 = p1Args[0].equals("user") ? PlayerType.USER : PlayerType.COMP;
            PlayerType type2 = p2Args[0].equals("user") ? PlayerType.USER : PlayerType.COMP;

            char color1 = p1Args[1].charAt(0);
            char color2 = p2Args[1].charAt(0);

            if ((color1 != 'W' && color1 != 'B') || (color2 != 'W' && color2 != 'B')) {
                System.out.println("Color must be W or B");
                return;
            }

            if (color1 == color2) {
                System.out.println("Players must have different colors");
                return;
            }

            player1 = new Player(type1, color1);
            player2 = new Player(type2, color2);

            initializeBoard();
            gameStarted = true;
            System.out.println("New game started");

            // Игрок компьютер, то ход сразу
            if (player1.type == PlayerType.COMP) {
                computerMove(player1);
            }

        } catch (NumberFormatException e) {
            System.out.println("Incorrect command");
        }
    }

    private void processMoveCommand (String[]args){
        if (!gameStarted) {
            System.out.println("Game not started");
            return;
        }

        if (args.length != 2) {
            System.out.println("Incorrect command");
            return;
        }

        try {
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);

            if (!isValidPosition(x, y)) {
                System.out.println("Invalid position");
                return;
            }

            if (!isCellEmpty(x, y)) {
                System.out.println("Cell is not empty");
                return;
            }

            char currentColor = getCurrentPlayerColor();
            makeMove(x, y, currentColor);

            // Если след. ход у компьютера, то ход сразу
            Player nextPlayer = (currentColor == player1.color) ? player2 : player1;
            if (nextPlayer.type == PlayerType.COMP && gameStarted) {
                computerMove(nextPlayer);
            }

        } catch (NumberFormatException e) {
            System.out.println("Incorrect command");
        }
    }

    public void run () {
        System.out.println("Squares Game. Type HELP for commands.");

        while (true) {
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) continue;

            String[] parts = input.split("\\s+", 2);
            String command = parts[0].toUpperCase();

            switch (command) {
                case "GAME" -> {
                    if (parts.length < 2) {
                        System.out.println("Incorrect command");
                        continue;
                    }
                    String[] gameArgs = parts[1].split(",\\s*");
                    processGameCommand(gameArgs);
                }
                case "MOVE" -> {
                    if (parts.length < 2) {
                        System.out.println("Incorrect command");
                        continue;
                    }
                    String[] moveArgs = parts[1].split(",\\s*");
                    processMoveCommand(moveArgs);
                }
                case "EXIT" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                case "HELP" -> printHelp();
                default -> System.out.println("Incorrect command");
            }
        }
    }

    public static void main(String[] args) {
        SquaresGame game = new SquaresGame();
        game.run();
    }
}





