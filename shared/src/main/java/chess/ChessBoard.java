package chess;

import java.util.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] board;

    public ChessBoard() {
        // Create 8*8 empty board to populate with pieces
        this.board = new ChessPiece[8][8];

    }
    public ChessBoard deepCopy() {
        ChessBoard cloneBoard = new ChessBoard();
        for(int row = 0; row <8; row++){
            for(int col = 0; col<8; col++){
                ChessPiece ogPiece = this.board[row][col];
                if (ogPiece != null){
                    ChessPiece newPiece = new ChessPiece(ogPiece.getTeamColor(), ogPiece.getPieceType());
                    ChessPosition currPos = new ChessPosition(row+1, col+1);
                    cloneBoard.addPiece(currPos, newPiece);
                }
            }
        }

        return cloneBoard;

    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "Board=" + Arrays.toString(board) +
                '}';
    }

    @Override
    public boolean equals(Object o) {

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getArrayRow()][position.getArrayColumn()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getArrayRow()][position.getArrayColumn()];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        setBackRows();
        setPawns();
    }


    /**
     * Sets the pawns to their default starting position
     */
    public void setPawns() {
        // set white pawns

        for (int i = 1; i<9; i++){
            ChessPiece whitePawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            ChessPosition pos = new ChessPosition(2, i);
            addPiece(pos, whitePawn);
        }
        // set black pawns

        for (int i = 1; i<9; i++){
            ChessPiece blackPawn = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            ChessPosition pos = new ChessPosition(7, i);
            addPiece(pos, blackPawn);
        }
    }

    /**
     * Sets the back row of pieces to their default starting position.
     */
    public void setBackRows() {
        ChessPiece.PieceType[] pieceRowOrder = {
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.KING,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.ROOK
        };
        // add back row of white pieces
        for (int i = 0; i<8; i++){
            ChessPiece whitePiece = new ChessPiece(ChessGame.TeamColor.WHITE, pieceRowOrder[i]);
            ChessPosition pos = new ChessPosition(1, i+1);
            addPiece(pos, whitePiece);
        }
        for (int i = 0; i<8; i++){
            ChessPiece blackPiece = new ChessPiece(ChessGame.TeamColor.BLACK, pieceRowOrder[i]);
            ChessPosition pos = new ChessPosition(8, i+1);
            addPiece(pos, blackPiece);

        }
    }

}
