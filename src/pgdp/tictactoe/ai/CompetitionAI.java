package pgdp.tictactoe.ai;

import pgdp.tictactoe.Field;
import pgdp.tictactoe.Game;
import pgdp.tictactoe.Move;
import pgdp.tictactoe.PenguAI;

import java.util.*;

public class CompetitionAI extends SimpleAI {
    @Override
    public Move makeMove(Field[][] board, boolean firstPlayer, boolean[] firstPlayedPieces,
                         boolean[] secondPlayedPieces) {
        Move move = super.makeMove(board, firstPlayer, firstPlayedPieces, secondPlayedPieces);
        if (!super.important || super.block.size() > 0) {
            // normaler Fall, d.h. es gibt kein 2 Marken des Gegners in einer Reihe/Spalte/Diagonale
            boolean[] playedPieces = (firstPlayer) ? firstPlayedPieces : secondPlayedPieces;
            boolean[] otherPlayedPieces = (firstPlayer)? secondPlayedPieces : firstPlayedPieces;
            boolean needToBlock = (block.size() > 0);
            int sum1 = 0, sum2 = 0;
            int minValue = 0;
            while (playedPieces[minValue]) { // minValue is the smallest stone
                minValue++;
            }
            int maxValue = 8;
            while (maxValue > minValue && playedPieces[maxValue]) { // maxValue is the biggest stone
                maxValue--;
            }
            int otherMaxValue = 8; // otherMaxValue is the biggest stone of other player
            while (otherMaxValue >= 0 && otherPlayedPieces[otherMaxValue]) otherMaxValue--;

            List<Integer> playedPosition = new ArrayList<>();
            List<Integer> otherPlayedPosition = new ArrayList<>();
            boolean[] wayCannotGo = new boolean[8];
            // update wayCanGo
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    if (board[x][y] == null) ;
                    else if (board[x][y].firstPlayer() == firstPlayer) {
                        playedPosition.add(y*3 + x);
                        sum1 += board[x][y].value();
                    } else {
                        otherPlayedPosition.add(y*3 + x);
                        if (board[x][y].value() >= maxValue) {
                            wayCannotGo[x] = true;
                            wayCannotGo[y + 3] = true;
                            if (x == y) wayCannotGo[6] = true;
                            if (x + y == 2) wayCannotGo[7] = true;
                        }
                        sum2 += board[x][y].value();
                    }
                }
            }

            if (otherMaxValue == -1) { // otherPlayer hat keinen Stein mehr, wir haben noch einen Stein
                int x0 = 0, y0 = 0, value = maxValue;
                for (int y = 0; y < 3; y++) {
                    for (int x = 0; x < 3; x++) {
                        if (board[x][y] == null) {
                            return new Move(x, y, minValue);
                        }
                        else if (board[x][y].firstPlayer() != firstPlayer) {
                            if (board[x][y].value() < value) {
                                x0 = x; y0 = y;
                                value = board[x][y].value() + 1;
                            }
                        }
                    }
                }
                System.out.println(x0 + " " + y0);
                return new Move(x0, y0, minValue);
            }

            // try out strategy
            if (playedPosition.size() >= 2) {
                for (Integer i : playedPosition) {
                    for (Integer j : playedPosition) {
                        if (i >= j) ;
                        else {
                            int x1 = i%3, y1 = i/3, x2 = j%3, y2 = j/3;
                            if (x1 != x2 && y1 != y2) {
                                // try to place at the position (x1, y2) or (x2, y1)
                                // x1 y2
                                if (!wayCannotGo[x1] && !wayCannotGo[y2]) {
                                    int val = tryPosition(board, x1, y2, maxValue);
                                    if (val != -1 && (!needToBlock || block.contains(y2*3 + x1)))
                                        return new Move(x1, y2, val);
                                }
                                if (!wayCannotGo[x2] && !wayCannotGo[y1]) {
                                    int val = tryPosition(board, x2, y1, maxValue);
                                    if (val != -1 && (!needToBlock || block.contains(y1*3 + x2)))
                                        return new Move(x2, y1, val);
                                }
                            }
                            else if (x1 == x2 && x1 != 1) {
                                // i < j => y1 < y2
                                if (y1 + y2 == 2) { // (0, 2)
                                    if (!wayCannotGo[6] && !wayCannotGo[7]) {
                                        int val = tryPosition(board, 1, 1, maxValue);
                                        if (val != -1 && (!needToBlock || block.contains(4)))
                                            return new Move(1, 1, val);
                                    }
                                }
                                else if (!wayCannotGo[4]) {
                                    if (x1 == y1 || x2 == y2) {
                                        if (!wayCannotGo[6]) {
                                            int val = tryPosition(board, 1, 1, maxValue);
                                            if (val != -1 && (!needToBlock || block.contains(4)))
                                                return new Move(1, 1, val);
                                        }
                                    }
                                    else {
                                        if (!wayCannotGo[7]) {
                                            int val = tryPosition(board, 1, 1, maxValue);
                                            if (val != -1 && (!needToBlock || block.contains(4)))
                                                return new Move(1, 1, val);
                                        }
                                    }
                                }
                            }
                            else if (y1 == y2 && y1 != 1) {
                                // i < j => x1 < x2
                                if (x1 + x2 == 2) { // (0, 2)
                                    if (!wayCannotGo[6] && !wayCannotGo[7]) {
                                        int val = tryPosition(board, 1, 1, maxValue);
                                        if (val != -1 && (!needToBlock || block.contains(4)))
                                            return new Move(1, 1, val);
                                    }
                                }
                                else if (!wayCannotGo[1]) {
                                    if (x1 == y1 || x2 == y2) {
                                        if (!wayCannotGo[6]) {
                                            int val = tryPosition(board, 1, 1, maxValue);
                                            if (val != -1 && (!needToBlock || block.contains(4)))
                                                return new Move(1, 1, val);
                                        }
                                    }
                                    else {
                                        if (!wayCannotGo[7]) {
                                            int val = tryPosition(board, 1, 1, maxValue);
                                            if (val != -1 && (!needToBlock || block.contains(4)))
                                                return new Move(1, 1, val);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (block.size() == 1) return move;

            // block.size() >= 2, otherPlayedPosition.size() >= 2
            if ((otherMaxValue >= maxValue && otherPlayedPosition.size() >= 2) || needToBlock) {
                int i;
                int j;
                if (block.size() >= 3) {
                    // X X -
                    int b1 = block.get(0), b2 = block.get(1), b3 = block.get(2);
                    if (board[b1%3][b1/3] == null) {
                        i = b2; j = b3;
                    }
                    else if (board[b2%3][b2/3] == null) {
                        i = b1; j = b3;
                    }
                    else {
                        i = b1; j = b2;
                    }
                }
                else if (block.size() == 2) {
                    i = block.get(0);
                    j = block.get(1);
                    if (board[i%3][i/3] == null || board[j%3][j/3] == null) return move;
                }
                else {
                    i = otherPlayedPosition.get(0);
                    j = otherPlayedPosition.get(1);
                }
                int x, y, value;
                int value1 = board[i%3][i/3].value() + 1;
                int value2 = board[j%3][j/3].value() + 1;
                while (value1 <= maxValue && playedPieces[value1]) value1++;
                while (value2 <= maxValue && playedPieces[value2]) value2++;
                int diff1 = value1 + board[i%3][i/3].value();
                int diff2 = value2 + board[j%3][j/3].value();
                if (Math.abs(value1 - value2) <= 1) {
                    if (Math.abs(diff1 - diff2) <= 1) {
                        // value1 = value2, board[i] = board[j] ±1
                        // value1 = value2 + 1, board[i] = board[j] - 1
                        // value1 = value2 - 1, board[i] = board[j] + 1
                        boolean isThere1 = false;
                        boolean isThere2 = false;
                        for (int yi = 0; yi < 3; yi++) {
                            if (yi != i / 3) { // check y, same x of i
                                if (board[i%3][yi] != null && board[i%3][yi].firstPlayer() == firstPlayer) {
                                    isThere1 = true; break;
                                }
                            }
                            if (yi != i % 3) { // check x
                                if (board[i%3][yi] != null && board[yi][i/3].firstPlayer() == firstPlayer) {
                                    isThere1 = true; break;
                                }
                            }
                            if (yi != j / 3) { // check y, same x of j
                                if (board[i%3][yi] != null && board[j%3][yi].firstPlayer() == firstPlayer) {
                                    isThere2 = true; break;
                                }
                            }
                            if (yi != j % 3) { // check x
                                if (board[i%3][yi] != null && board[yi][j/3].firstPlayer() == firstPlayer) {
                                    isThere2 = true; break;
                                }
                            }
                        }
                        if (isThere1) return new Move(i%3, i/3, value1);
                        else if (isThere2) return new Move(j%3, j/3, value2);
                    }
                    if (diff1 < diff2) {
                        x = i % 3;
                        y = i / 3;
                        value = value1;
                    }
                    else {
                        x = j % 3;
                        y = j / 3;
                        value = value2;
                    }
                }
                else if (value1 < value2) {
                    x = i % 3; y = i / 3;
                    value = value1;
                }
                else {
                    x = j % 3; y = j / 3;
                    value = value2;
                }
                return new Move(x, y, value); // check here
            }

            if (playedPosition.size() == 1) {
                int i = playedPosition.get(0);
                int j1, j2;
                switch(i) {
                    case 1:
                    case 7: j1 = 3; j2 = 5; break;
                    case 3:
                    default: j1 = 1; j2 = 7; break;
                }
                int x, y, val = -1;
                int x1 = j1%3, y1 = j1/3, x2 = j2%3, y2 = j2/3, indexNull = 0, indexOther = 0;
                if (board[x1][y1] == null) indexNull = 1;
                else if (board[x1][y1].value() < minValue) indexOther = 1;

                if (board[x2][y2] == null) indexNull = 2;
                else if (board[x2][y2].value() < minValue) indexOther = 2;

                if (((!firstPlayer && sum1 + minValue < sum2 ) || (firstPlayer && sum1 < sum2))
                        && indexOther != 0) {
                    // overlap
                    return (indexOther == 1) ? new Move(x1, y1, minValue) : new Move(x2, y2, minValue);
                }
                else {
                    return (indexNull == 1) ? new Move(x1, y1, minValue) : new Move(x2, y2, minValue);
                }
            }
            else if (playedPosition.size() == 0){ // playedPosition.size() == 0
                int indexNull = 0;
                if (board[1][0] == null) indexNull = 1;
                else if (board[1][0].value() < minValue) return new Move(1, 0, minValue);

                if (board[0][1] == null) indexNull = 3;
                else if (board[0][1].value() < minValue) return new Move(0, 1, minValue);

                if (board[2][1] == null) indexNull = 5;
                else if (board[2][1].value() < minValue) return new Move(2, 1, minValue);

                if (board[1][2] == null) indexNull = 7;
                else if (board[1][2].value() < minValue) return new Move(1, 2, minValue);

                return new Move(indexNull%3, indexNull/3, minValue);
            }
        }
        return move;
    }

    private int tryPosition (Field[][] board, int x, int y, int maxValue) {
        int res = -1;
        if (board[x][y] == null) {
            return maxValue;
        }
        else if (board[x][y].value() < maxValue) {
            return maxValue;
        }
        return res;
    }
}
