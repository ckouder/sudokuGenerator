import java.util.ArrayList;

public class SudokuGenerator {

    private static final int INITIALIZATION_LIMIT = 10000;

    private static final int TOKEN_START_POINT = 65;

    private int blockSize;

    private int size;

    private char[] tokens;

    private char[][] paper;

    private Chain<ArrayList<Character>> posTokensChain;

    SudokuGenerator(final int setBlockSize) throws Exception {
        if (setBlockSize > 16) {
            throw new Exception("block size [" + setBlockSize + "] is too big");
        }

        blockSize = setBlockSize;
        size = (int) Math.pow(blockSize, 2);
        tokens = new char[size];

        // fill up tokens list
        for (int i = 0; i < size; i++) {
            char token = (char) (i + TOKEN_START_POINT);
            tokens[i] = token;
        }
    }
}