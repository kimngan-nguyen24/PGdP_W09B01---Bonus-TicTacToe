package pgdp.tictactoe.ai;

import java.util.*;

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
        Game.printBoard(board);
        boolean[] playedPieces = (firstPlayer)? firstPlayedPieces : secondPlayedPieces;
        boolean[] otherPlayedPieces = (firstPlayer)? secondPlayedPieces : firstPlayedPieces;
        boolean[][] playerBoard = new boolean[3][3];
        boolean[][] otherPlayerBoard = new boolean[3][3];
        int xRes = -1, yRes = -1, minValue = 0;
        while (playedPieces[minValue]) { // minValue is the smallest stone
           minValue++;
        }
        int maxValue = 8;
        while (maxValue > minValue && playedPieces[maxValue]) { // maxValue is the biggest stone
            maxValue--;
        }

        int otherMaxValue = 8;
        while (otherMaxValue >= 0 && otherPlayedPieces[otherMaxValue]) otherMaxValue--;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (board[x][y] == null) {xRes = x; yRes = y;} 
                else if (board[x][y].firstPlayer() == firstPlayer) playerBoard[x][y] = true;
                else {
                    otherPlayerBoard[x][y] = true;
                }
            }
        }

        // Falls ein Zug dazu führen würde, dass deine KI direkt gewinnt
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

        // Falls der Gegner im nächsten Zug gewinnen kann und deine KI dies durch einen Zug verhindern kann
        Set<Integer> blockPositions = blockPositions(otherPlayerBoard);
        int notPrefer = -1;
        for (Integer i : blockPositions) {
            int[] encode = encodeBlockPositions(i);
            int x0 = -1, y0 = -1, value0 = maxValue + 1;
            for (int j : encode) {
                int x = j % 3, y = j / 3;
                if (board[x][y] == null) {
                    // Das Feld ist frei
                    if (otherMaxValue <= maxValue) { // AI can "actually" block
                        int value = otherMaxValue;
                        while (value <= maxValue && playedPieces[value]) value++;
                        if (value < value0) {
                            x0 = x;
                            y0 = y;
                            value0 = value;
                        }
                    }
                    else { // cannot actually block, otherMaxValue > maxValue
                        notPrefer = y*3 + x;
                    }
                }
                else if (board[x][y].firstPlayer() != firstPlayer) {
                    if (board[x][y].value() < maxValue) { // can overlap
                        int value = board[x][y].value() + 1;
                        while (value <= maxValue && playedPieces[value]) value++;
                        if (value < value0) { // ersetze den Steinen nur, wenn der benötigte Wert value < value0
                            x0 = x;
                            y0 = y;
                            value0 = value;
                        }
                    }
                }
                else { // board[x][y].firstPlayer() == firstPlayer
                    if (board[x][y].value() >= otherMaxValue) { // AI has already blocked with a big enough stone
                                                    // this cannot be replaced -> the opponent cannot use this way
                        x0 = -1; // reset x0
                        break;
                    }
                }
            }
            if (x0 != -1) return new Move(x0, y0, value0);
        }
        if (notPrefer != -1) return new Move(notPrefer%3, notPrefer/3, maxValue);

        // normaler Fall
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

    protected static Set<Integer> movePositions (boolean[][] pBoard) {
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

    /**
     *  0 1 2       0 = (0, 3, 6)    1 = (1, 4, 7)   2 = (2, 5, 8)
     *  3 4 5       3 = (0, 1, 2)    4 = (3, 4, 5)   5 = (6, 7, 8)
     *  6 7 8       6 = (0, 4, 8)    7 = (2, 4, 6)
     * @param pBoard boolean[][]
     * @return Set
     */
    protected static Set<Integer> blockPositions (boolean[][] pBoard) {
        Set<Integer> result = new HashSet<>();
        int count1 = 0, count2 = 0; // count1, count2 sind für Diagonale
        for (int y = 0; y < 3; y++) {
            int countX = 0;
            int countY = 0;
            for (int x = 0; x < 3; x++) {
                // check row
                if (pBoard[x][y]) countX++;
                // check column
                if (pBoard[y][x]) countY++; // first position is fixed
            }
            if (countX == 2) result.add(y + 3);
            if (countY == 2) result.add(y);

            // check diagonals
            if (pBoard[y][y]) count1++;
            if (pBoard[2 - y][y]) count2++;
        }
        if (count1 == 2) result.add(6);
        if (count2 == 2) result.add(7);
        return result;
    }

    protected int[] encodeBlockPositions (int n) {
        switch (n) {
            case 0:
            case 1:
            case 2:
                return new int[]{n, n + 3, n + 6};
            case 3:
            case 4:
            case 5:
                n =  (n % 3) * 3;
                return new int[]{n, n + 1, n + 2};
            case 6: return new int[]{0, 4, 8};
            case 7: return new int[]{2, 4, 6};
            default: return new int[]{};
        }
    }
}
