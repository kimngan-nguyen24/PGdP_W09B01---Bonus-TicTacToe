package pgdp.tictactoe.ai;

import pgdp.tictactoe.Field;
import pgdp.tictactoe.Game;
import pgdp.tictactoe.Move;
import pgdp.tictactoe.PenguAI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class CompetitionAI extends SimpleAI {
    @Override
    public Move makeMove(Field[][] board, boolean firstPlayer, boolean[] firstPlayedPieces,
                         boolean[] secondPlayedPieces) {
        Game.printBoard(board);
        boolean[] playedPieces = (firstPlayer)? firstPlayedPieces : secondPlayedPieces;
        boolean[] otherPlayedPieces = (firstPlayer)? secondPlayedPieces : firstPlayedPieces;
        boolean[][] playerBoard = new boolean[3][3];
        boolean[][] otherPlayerBoard = new boolean[3][3];
        List<Integer> list = new ArrayList<>(); int countOfFree = 0;
        int xRes = -1, yRes = -1, valRes = 9;
        int minValue = 0;
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
                if (board[x][y] == null) {
                    list.add(y*3 + x);
                }
                else if (board[x][y].firstPlayer() == firstPlayer) playerBoard[x][y] = true;
                else {
                    otherPlayerBoard[x][y] = true;
                    if (list.isEmpty() && board[x][y].value() < maxValue) {
                        int val = board[x][y].value() + 1;
                        while (val <= maxValue && playedPieces[val]) val++;
                        if (val < valRes) {
                            xRes = x; yRes = y; valRes = val;
                        }
                    }
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

        // normaler Fall
        if (list.isEmpty()) return new Move(xRes, yRes, valRes);
        else {
            Random random = new Random();
            int n = random.nextInt(list.size());
            int index = list.get(n);
            return new Move(index%3, index/3, minValue);
        }
    }
}
