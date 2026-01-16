package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private final int row;
    private final int col;
    public ChessPosition(int row, int col) {
        // subtract one to convert from chess position to array indexing
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }
    /**
     * @return which row this position is in using 0 indexing
     * 0 codes for the bottom row
     */
    public int getArrayRow() {
        return row-1;
    }

    /**
     * @return which column this position is in using 0 indexing
     * 0 codes for the left row
     */
    public int getArrayColumn() {
        return col-1;
    }



}
