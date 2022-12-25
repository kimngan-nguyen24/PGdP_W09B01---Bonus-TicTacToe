package pgdp.tictactoe;

import pgdp.tictactoe.ai.HumanPlayer;

public class Game {
    private final PenguAI firstPlayer;
    private final PenguAI secondPlayer;
    private PenguAI winner = null;
    private boolean played = false;

    public Game(PenguAI first, PenguAI second) {
        firstPlayer = first;
        secondPlayer = second;
    }

    /**
     * getWinner() gibt den Gewinner des Spiels zurück.
     * wenn noch kein Gewinner feststeht oder das Spiel unentschieden ausging, soll null zurückgegeben werden.
     * @return PenguAI
     */
    public PenguAI getWinner() {
        return winner;
    }

    /**
     * playGame() trägt das Spiel zwischen den beiden KIs aus und wird dabei garantiert nur einmal pro Game-Objekt aufgerufen.
     * Methode makeMove() gibt ein Move(int x, int y, int value) zurück.
     */
    public void playGame() {
        if (played) return;
        played = true;
        Field[][] board = new Field[3][3];
        boolean[] firstPlayedPieces = new boolean[9];
        boolean[] secondPlayedPieces = new boolean[9];
        boolean firstPlayerTurn = true;
        int count = 0; // count <= 9*2
        PenguAI player1; PenguAI player2;
        boolean[] player1Pieces; boolean[] player2Pieces;
        do {
            if (firstPlayerTurn) {
                player1 = firstPlayer; player2 = secondPlayer;
                player1Pieces = firstPlayedPieces; player2Pieces = secondPlayedPieces;
            }
            else {
                player1 = secondPlayer; player2 = firstPlayer;
                player1Pieces = secondPlayedPieces; player2Pieces = firstPlayedPieces;
            }

            /**
             * player1 is the one doing the move
             */
            Move move = player1.makeMove(board, firstPlayerTurn, player1Pieces, player2Pieces);
            int x = move.x(); int y = move.y(); int value = move.value();

            // check verbotener, falscher oder ungültiger Zug
            if (x < 0 || x > 8 || y < 0 || y > 8 || value < 0 || value > 8) {winner = player2; return;}
            if (player1Pieces[value]) {winner = player2; return;} // Der Player hat diesen Stein schon gespielt.
            if (board[x][y] != null) { // Das Feld ist schon belegt
                if (board[x][y].firstPlayer() == firstPlayerTurn || board[x][y].value() >= value) {
                    winner = player2; return;
                }
            }
            //*****

            board[x][y] = new Field(value, firstPlayerTurn);
            player1Pieces[value] = true;
            if (check(board, firstPlayerTurn, x, y)) {
                winner = player1; return;
            }
            firstPlayerTurn = !firstPlayerTurn;
            count++;
        } while (count <= 18);
        // wenn count == 18, d.h. das beide Spieler all ihre Spielsteine gelegt haben und es keinen Gewinner gibt.
    }

    /**
     * @param board Field[][]
     * @param firstPlayer true: firstPlayer, false: secondPlayer
     * @param newX int
     * @param newY int: x, y geben die Koordinaten neues Moves an.
     * @return if the player has won
     */
    private boolean check (Field[][] board, boolean firstPlayer, int newX, int newY) {
        if ((board[newX][0] != null && board[newX][0].firstPlayer() == firstPlayer)
                && (board[newX][1] != null && board[newX][1].firstPlayer() == firstPlayer)
                && (board[newX][2] != null && board[newX][2].firstPlayer() == firstPlayer)) return true; // check column
        if ((board[0][newY] != null && board[0][newY].firstPlayer() == firstPlayer)
                && (board[1][newY] != null && board[1][newY].firstPlayer() == firstPlayer)
                && (board[2][newY] != null && board[2][newY].firstPlayer() == firstPlayer)) return true; // check row
        if (newX == newY) { // check 1. diagonal
            if ((board[0][0] != null && board[0][0].firstPlayer() == firstPlayer)
                    && (board[1][1] != null && board[1][1].firstPlayer() == firstPlayer)
                    && (board[2][2] != null && board[2][2].firstPlayer() == firstPlayer)) return true;
        }
        if (newX + newY == 2) { // check 2. diagonal
            if ((board[0][2] != null && board[0][2].firstPlayer() == firstPlayer)
                    && (board[1][1] != null && board[1][1].firstPlayer() == firstPlayer)
                    && (board[2][0] != null && board[2][0].firstPlayer() == firstPlayer)) return true;
        }
        return false;
    }

    public static void printBoard(Field[][] board) {
        System.out.println("┏━━━┳━━━┳━━━┓");
        for (int y = 0; y < board.length; y++) {
            System.out.print("┃");
            for (int x = 0; x < board.length; x++) {
                if (board[x][y] != null) {
                    System.out.print(board[x][y] + "┃");
                } else {
                    System.out.print("   ┃");
                }
            }
            System.out.println();
            if (y != board.length - 1) {
                System.out.println("┣━━━╋━━━╋━━━┫");
            }
        }
        System.out.println("┗━━━┻━━━┻━━━┛");
    }

    public static void main(String[] args) {
        PenguAI firstPlayer = new HumanPlayer();
        PenguAI secondPlayer = new HumanPlayer();
        Game game = new Game(firstPlayer, secondPlayer);
        game.playGame();
        if(firstPlayer == game.getWinner()) {
            System.out.println("Herzlichen Glückwunsch erster Spieler");
        } else if(secondPlayer == game.getWinner()) {
            System.out.println("Herzlichen Glückwunsch zweiter Spieler");
        } else {
            System.out.println("Unentschieden");
        }
    }
}
