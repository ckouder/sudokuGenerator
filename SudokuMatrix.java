import java.util.ArrayList;
import java.util.Random;

/**
 * generate a sudoku matrix
 */
public class SudokuMatrix {

    private class NoAvailableSlotException extends Exception {

        NoAvailableSlotException(String string) {
            super(string);
        }
    }

    private class Chain<E> {

        private class Node {
    
            private final String id;

            private E value;

            private Node previous;

            Node(final String setId, final E setValue, final Node setPrevious) {
                id = setId;
                value = setValue;
                previous = setPrevious;
            }

            public String getId() {
                return id;
            }

            public E getValue() {
                return value;
            }

            public void setValue(final E setValue) {
                value = setValue;
            }

            public Node getPrevious() {
                return previous;
            }

            public void setPrevious(final Node setPrevious) {
                previous = setPrevious;
            }
        }

        private Node root;

        Chain() { }

        public Node add(final String id, final E value) {
            final Node node = new Node(id, value, root);
            root = node;

            return node;
        }

        private Node getFromId(final Node current, final String id) {
            if (current == null || current.getId().equals(id)) {
                return current;
            }

            return getFromId(current.previous, id);
        }

        public Node getById(final String id) {
            return getFromId(root, id);
        }
    }

    private static final int INITIALIZATION_LIMIT = 10000;

    private static final int TOKEN_START_POINT = 65;

    private final int blockSize;

    private final int size;

    private final char[] tokens;

    private char[][] paper;

    private int counter;

    private Chain<ArrayList<Character>> posTokensChain = new Chain<>();

    SudokuMatrix(final int setBlockSize) throws Exception {
        if (setBlockSize > 16) {
            throw new Exception("block size [" + setBlockSize + "] is too large");
        }

        blockSize = setBlockSize;
        size = (int) Math.pow(blockSize, 2);
        tokens = new char[size];

        // fill up tokens list
        for (int i = 0; i < size; i++) {
            final char token = (char) (i + TOKEN_START_POINT);
            tokens[i] = token;
        }

        initialize();
    }

    void initialize() {

        // initizalize paper with placeholder tokens
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                paper[i][j] = ' ';
            }
        }

        // initialize posTokenChain
        posTokensChain = new Chain<>();

        // initialize counter to zero
        counter = 0;

        // fill up diagonal blocks

        // fill up remaining blocks

    }

    /**
     * get a line of the matrix.
     * 
     * @param pos      position of the line in the matrix
     * @param vertical if the wanted line is a column
     * @return a line in the matrix at position [pos]
     */
    private char[] getLine(final int pos, final boolean vertical) {
        char[] line = new char[size];

        if (vertical) {
            for (int i = 0; i < size; i++) {
                line[i] = paper[i][pos];
            }

        } else {
            line = paper[pos];
        }

        return line;
    }

    /**
     * get values within a unit block at the given coordinate.
     * 
     * @param coordinate [int col, int row] pair
     * @return values in a block
     */
    private char[] getBlock(final int[] coordinate) {
        final char[] block = new char[size];

        int j = 0;
        for (int i = coordinate[0] * blockSize; i < (coordinate[0] + 1) * blockSize; i++) {

            for (int k = coordinate[1] * blockSize; k < (coordinate[1] + 1) * blockSize; k++) {

                block[j] = paper[i][k];
                j++;
            }

        }

        return block;
    }

    /**
     * fill up a unit sudoku block.
     * 
     * @param values     values fill to the block
     * @param coordinate [int col, int row] pair
     */
    private void setBlock(final char[] values, final int[] coordinate) {
        int j = 0;

        for (int i = coordinate[0] * blockSize; i < (coordinate[0] + 1) * blockSize; i++) {

            for (int k = coordinate[1] * blockSize; k < (coordinate[1] + 1) * blockSize; k++) {

                paper[i][k] = values[j];
                j++;
            }

        }
    }

    private boolean fillOne(final int[] coordinate) {
        ArrayList<Character> tokenList = getAvailableTokensForSlot(coordinate);

        if (tokenList.size() == 0) {
            posTokensChain.root = posTokensChain.root.previous;
            paper[coordinate[0]][coordinate[1]] = ' ';
            return false;

        } else {
            int i = new Random().nextInt(tokenList.size());
            paper[coordinate[0]][coordinate[1]] = tokenList.get(i);
            
            tokenList.remove(i);
            return true;
        }
    }

    /**
     * fill up diagonal blocks with random tokens.
     */
    private void fillDiagWithRandomTokens() {
        for (int i = 0; i < blockSize; i++) {
            final char[] ts = Helper.randomize(tokens);
            setBlock(ts, new int[] { i, i });
        }
    }

    private void fillAll() {
        int[] coordinate;
        try {
            coordinate = getNextSlot(new int[] { 0, 0 });

        } catch (NoAvailableSlotException e1) {
            e1.printStackTrace();
        }

        while (true) {
            try {
                if (fillOne(coordinate)) {
                    coordinate = getNextSlot(coordinate);

                } else {
                    coordinate = getPreviousSlot(coordinate);
                }

                counter++;

                if (counter > INITIALIZATION_LIMIT) {
                    initialize();
                    return;
                }

            } catch (final NoAvailableSlotException e) {
                break;
            }
        }
    }

    private int[] getPreviousSlot(final int[] coordinate) throws NoAvailableSlotException{
        if (coordinate[0] == blockSize && coordinate[1] == 0) {
            throw new NoAvailableSlotException(
                "No available slot before [" + coordinate[1] + ", " + coordinate[0] + "].");
        }

        int[] c = coordinate;
        while (true) {
            if (c[0] == 0 && c[1] > 0) {
                c[0] = size - 1;
                c[1] -= 1;
            }

            else if (c[1] >= 0) {
                c[0] -= 1;
            }

            if (!isPosInDiagonalBlocks(c)) {
                break;
            }
        }

        return c;
    }

    private int[] getNextSlot(final int[] coordinate) throws NoAvailableSlotException {
        if (coordinate[1] == size - 1 && coordinate[0] == size - blockSize - 1) {
            throw new NoAvailableSlotException(
                "No available slot after [" + coordinate[1] + ", " + coordinate[0] + "].");
        }

        int[] c = coordinate;
        while (true) {
            if (c[0] == size - 1 && c[1] < size - 1) {
                c[0] = 0;
                c[1] += 1;
            }

            else if (c[1] <= size - 1) {
                c[0] += 1;
            }

            if (!isPosInDiagonalBlocks(c)) {
                break;
            }
        }

        return c;
    }

    private boolean isPosInDiagonalBlocks(final int[] coordinate) {
        for (int i = 0; i < blockSize; i++) {
            if ((coordinate[0] >= blockSize * i) && (coordinate[0] <= blockSize * (i + 1))
                && (coordinate[1] >= blockSize * i) && (coordinate[1] <= blockSize * (i + 1))) {
                
                return true;
            }
        }
        return false;
    }

    private ArrayList<Character> getAvailableTokensForSlot(final int[] coordinate) {
        SudokuMatrix.Chain.Node ts = posTokensChain
            .getById("" + coordinate[0] + " " + coordinate[1]);
        
        if (ts == null) {
            ArrayList<Character> tokenValues = new ArrayList<>();
            char[] colValues = getLine(coordinate[0], true);
            char[] rowValues = getLine(coordinate[1], false);
            char[] blockValues = getBlock(new int[] {
                coordinate[0] / blockSize,
                coordinate[1] / blockSize,
            });
            
            for (char t : tokens) {
                tokenValues.add(t);
            }

            for (char c : colValues) {
                tokenValues.remove(c);
            }

            for (char r : rowValues) {
                tokenValues.remove(r);
            }

            for (char b : blockValues) {
                tokenValues.remove(b);
            }

            ts = posTokensChain.add("" + coordinate[0] + " " + coordinate[1], tokenValues);
        }

        return (ArrayList<Character>) ts.getValue();
    }
}