package pgdp.tictactoe.ai;

import pgdp.tictactoe.Field;
import pgdp.tictactoe.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimpleAI2 extends SimpleAI {
    @Override
    public Move makeMove(Field[][] board, boolean firstPlayer, boolean[] firstPlayedPieces,
                         boolean[] secondPlayedPieces) {
        Move move = super.makeMove(board, firstPlayer, firstPlayedPieces, secondPlayedPieces);
        if (!important) {
            List<Integer> nullIndex = new ArrayList<>();
            int x0 = 0, y0 = 0;
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    if (board[x][y] == null) nullIndex.add(y * 3 + x);
                }
            }
            if (nullIndex.size() != 0) {
                Random random = new Random();
                int r = random.nextInt(nullIndex.size());
                int i = nullIndex.get(r);
                return new Move(i%3, i/3, move.value());
            }
        }
        return move;
    }
}
