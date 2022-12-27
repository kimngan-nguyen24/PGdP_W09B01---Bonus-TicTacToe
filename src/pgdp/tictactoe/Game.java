package pgdp.tictactoe;

import pgdp.tictactoe.ai.CompetitionAI;
import pgdp.tictactoe.ai.HumanPlayer;
import pgdp.tictactoe.ai.SimpleAI;

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
     * Gewinnen wenn: eine Reihe, Spalte oder Diagonale hat
     * Verlieren wenn: 1. falscher Zug,
     *      2. Sollte ein Spieler noch Spielsteine haben, allerdings diese nicht mehr legen können, hat er verloren.
     * Unentschieden: wenn beide Spieler all ihre Spielsteine gelegt haben und es keinen Gewinner gibt.
     */
    public void playGame() {
        if (played) return;
        played = true;
        Field[][] board = new Field[3][3];
        boolean[] firstPlayedPieces = new boolean[9];
        boolean[] secondPlayedPieces = new boolean[9];
        boolean firstPlayerTurn = true;
        int count = 0; // count <= 18
        int countOfFields = 0;
        PenguAI player; PenguAI otherPlayer;
        boolean[] playedPieces;
        do {
            if (firstPlayerTurn) {
                // check if firstPlayer can continue to play
                player = firstPlayer; otherPlayer = secondPlayer;
                playedPieces = firstPlayedPieces;
            }
            else {
                player = secondPlayer; otherPlayer = firstPlayer;
                playedPieces = secondPlayedPieces;
            }

            // check if the player can continue to move
            if (countOfFields == 9 && !canMove(board, firstPlayerTurn, playedPieces)) {
                winner = otherPlayer; return;
            }

            // player is the one doing the move
            Move move = player.makeMove(board, firstPlayerTurn, firstPlayedPieces, secondPlayedPieces);
            int x = move.x(); int y = move.y(); int value = move.value();

            // check verbotener, falscher oder ungültiger Zug
            if (x < 0 || x > 8 || y < 0 || y > 8 || value < 0 || value > 8) {winner = otherPlayer; return;}
            if (playedPieces[value]) {winner = otherPlayer; return;} // Der Player hat diesen Stein schon gespielt.
            if (board[x][y] != null) { // Das Feld ist schon belegt
                if (board[x][y].firstPlayer() == firstPlayerTurn || board[x][y].value() >= value) {
                    winner = otherPlayer; return;
                }
            } // end checking

            else { // board[x][y] = null
                countOfFields++;
            }
            board[x][y] = new Field(value, firstPlayerTurn);
            playedPieces[value] = true;
            if (check(board, firstPlayerTurn, x, y)) {
                winner = player; return;
            }
            count++;
            firstPlayerTurn = !firstPlayerTurn;
        } while (count < 18); // break wenn count == 18

        // TASK Competition
        // Wenn alle Steine gelegt wurden, ist es nicht direkt ein Unentschieden
        // sondern jeder Pinguin zählt die Summe von seinen sichtbaren Steinen auf dem Spielfeld.
        // Der Pinguin mit der kleineren Summe hat dann gewonnen.
        int count1 = 0;
        int count2 = 0;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (board[x][y] == null) ;
                else if (board[x][y].firstPlayer()) count1+= board[x][y].value();
                else count2+= board[x][y].value();
            }
        }
        if (count1 < count2) winner = firstPlayer;
        if (count2 < count1) winner = secondPlayer;
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

    /**
     * kann sich nicht mehr bewegen, wenn kein Feld mehr frei ist (hat sicher bei der aufrufenden Methode festgestellt),
     * und hat keinen größeren Stein als Spielsteinen des Gegners
     * @param board Field[][]
     * @param firstPlayer boolean
     * @param pieces array
     * @return boolean
     */
    private boolean canMove (Field[][] board, boolean firstPlayer, boolean[] pieces) {
        int max = 0;
        for (int i = 8; i >= 0; i--) {
            if (!pieces[i]) {
                max = i; break;
            }
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (board[x][y].firstPlayer() != firstPlayer && board[x][y].value() < max) return true;
            }
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
        PenguAI firstPlayer = new CompetitionAI();
        PenguAI secondPlayer = new SimpleAI();
        int win = 0, lose = 0, draw = 0;
        for (int i = 0; i < 10; i++) {
            Game game = new Game(firstPlayer, secondPlayer);
            game.playGame();
            if (firstPlayer == game.getWinner()) {
                //System.out.println("Herzlichen Glückwunsch erster Spieler");
                win++;
            } else if (secondPlayer == game.getWinner()) {
                //System.out.println("Herzlichen Glückwunsch zweiter Spieler");
                lose++;
            } else {
                //System.out.println("Unentschieden");
                draw++;
            }

            game = new Game(secondPlayer, firstPlayer);
            game.playGame();
            if (firstPlayer == game.getWinner()) {
                //System.out.println("Herzlichen Glückwunsch erster Spieler");
                win++;
            } else if (secondPlayer == game.getWinner()) {
                //System.out.println("Herzlichen Glückwunsch zweiter Spieler");
                lose++;
            } else {
                //System.out.println("Unentschieden");
                draw++;
            }
        }

        System.out.println("Win: " + win);
        System.out.println("Lose: " + lose);
        System.out.println("Draw: " + draw);
    }
}
