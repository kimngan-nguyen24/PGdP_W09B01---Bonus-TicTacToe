package pgdp.tictactoe.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import pgdp.tictactoe.Field;
import pgdp.tictactoe.Game;
import pgdp.tictactoe.Move;
import pgdp.tictactoe.PenguAI;

public class SimpleAI extends PenguAI {
    /**
     * Die KI soll nur gültige Züge zurückgeben.
     * Wenn ein Zug dazu führen würde, dass deine KI direkt gewinnt, muss dieser zurückgegeben werden.
     * Wenn der Gegner im nächsten Zug gewinnen kann und deine KI dies durch einen Zug verhindern kann,
     *  soll dieser gewählt werden, außer deine KI kann selbst direkt gewinnen.
     */

    @Override
    public Move makeMove(Field[][] board, boolean firstPlayer, boolean[] firstPlayedPieces,
            boolean[] secondPlayedPieces) {

        boolean[] playedPieces = (firstPlayer)? firstPlayedPieces : secondPlayedPieces;
        boolean[][] playerBoard = new boolean[3][3];
        boolean[][] otherPlayerBoard = new boolean[3][3];
        int xRes = -1, yRes = -1, minValue = 0;
        while (playedPieces[minValue]) { //minValue is the smallest stone
           minValue++;
        }
        int maxValue = 8;
        while (maxValue > minValue && playedPieces[maxValue]) { // maxValue is the biggest stone
            maxValue--;
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (board[x][y] == null) {xRes = x; yRes = y;} 
                else if (board[x][y].firstPlayer() == firstPlayer) playerBoard[x][y] = true;
                else {
                    otherPlayerBoard[x][y] = true;
                }
            }
        }
        Set<Integer> movePositions = movePositions(playerBoard);
        for (Integer i : movePositions) {
            int x = i % 3, y = i / 3;
            if (board[x][y] == null) { // Das Feld ist frei, value ist nicht notwendig
                return new Move(x, y, minValue);
            }
            else {
                int value = board[x][y].value() + 1;
                while (value < 9 && playedPieces[value]) value++;
                if (value < 9) return new Move(x, y, value);
            }
        }

        Set<Integer> blockPositions = movePositions(otherPlayerBoard);
        for (Integer i : blockPositions) {
            int x = i % 3, y = i / 3;
            if (board[x][y] == null) {
                // Falls das Feld nicht null, kann es nur zum Player gehören, gdw kann sich nicht dahin bewegen
                return new Move(x, y, maxValue);
            }
        }
        if (xRes != -1) return new Move(xRes, yRes, minValue); // bewege sich zum freien Field
        else {
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    if (otherPlayerBoard[x][y] && board[x][y].value() < maxValue) {
                        int value = board[x][y].value();
                        while (value < maxValue && playedPieces[value]) value++;
                        return new Move(x, y, value);
                    }
                }
            }
        }
        return new Move(0, 0, 0);
    }

    private static Set<Integer> movePositions (boolean[][] pBoard) {
        Set<Integer> result = new HashSet<>();
        int count1 = 0, count2 = 0; // count1, count2 sind für Diagonale
        int f1 = 0, f2 = 0;
        for (int y = 0; y < 3; y++) {
            int countX = 0;
            int countY = 0;
            int xF = 0; int yF = 0;
            for (int x = 0; x < 3; x++) {
                // check row
                if (pBoard[x][y]) countX++;
                else xF = x;
                // check column
                if (pBoard[y][x]) countY++; // first position is fixed
                else yF = x;
            }
            if (countX == 2) result.add(y*3 + xF);
            if (countY == 2) result.add(yF*3 + y);

            // check diagonals
            if (pBoard[y][y]) count1++;
            else f1 = y;
            if (pBoard[2 - y][y]) count2++;
            else f2 = y;
        }
        if (count1 == 2) result.add(f1*3 + f1);
        if (count2 == 2) result.add(f2*3 + 2 - f2);
        return result;
    }

    /*public static void main(String[] args) {
        boolean[][] test = new boolean[3][3];
        test[0][0] = true; test[1][0] = true;
        Set<Integer> set = movePositions(test);
        System.out.println(Arrays.toString(set.toArray()));
    }*/
}
