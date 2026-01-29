package chess;

import java.util.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] Board;
    public HashMap<String, Object[]> kingMap;

    public ChessBoard() {
        // Create 8*8 empty board to populate with pieces
        this.Board = new ChessPiece[8][8];
    }
    public ChessBoard deepCopy() {
        ChessBoard cloneBoard = new ChessBoard();
        for(int row = 0; row <8; row++){
            for(int col = 0; col<8; col++){
                ChessPiece ogPiece = this.Board[row][col];
                if (ogPiece != null){
                    ChessPiece newPiece = new ChessPiece(ogPiece.getTeamColor(), ogPiece.getPieceType());
                    ChessPosition currPos = new ChessPosition(row+1, col+1);
                    cloneBoard.addPiece(currPos, newPiece);
                    if (ogPiece.getPieceType() == ChessPiece.PieceType.KING) {
                        String key = (ogPiece.getTeamColor() == ChessGame.TeamColor.WHITE) ? "whiteKing" : "blackKing";
                        Object[] posAndPiece = {currPos, newPiece};
                        if (cloneBoard.kingMap == null){
                            cloneBoard.kingMap = new HashMap<>();
                        }
                        cloneBoard.kingMap.put(key, posAndPiece);
                    }
                }
            }
        }

        return cloneBoard;

    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "Board=" + Arrays.toString(Board) +
                '}';
    }

    @Override
    public boolean equals(Object o) {

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(Board, that.Board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(Board);
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        Board[position.getArrayRow()][position.getArrayColumn()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return Board[position.getArrayRow()][position.getArrayColumn()];
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
            ChessPiece WhitePawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            ChessPosition pos = new ChessPosition(2, i);
            addPiece(pos, WhitePawn);
        }
        // set black pawns

        for (int i = 1; i<9; i++){
            ChessPiece BlackPawn = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            ChessPosition pos = new ChessPosition(7, i);
            addPiece(pos, BlackPawn);
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
            if (i == 4){
                Object[] posAndPiece = {pos, whitePiece};
                kingMap.put("whiteKing", posAndPiece);
            }
        }
        for (int i = 0; i<8; i++){
            ChessPiece blackPiece = new ChessPiece(ChessGame.TeamColor.BLACK, pieceRowOrder[i]);
            ChessPosition pos = new ChessPosition(8, i+1);
            addPiece(pos, blackPiece);
            if (i == 4){
                Object[] posAndPiece = {pos, blackPiece};
                kingMap.put("blackKing", posAndPiece);
            }
        }
    }
    public ChessPosition getKingPos(ChessGame.TeamColor teamColor) {
        Object objPos = (teamColor== ChessGame.TeamColor.WHITE) ? kingMap.get("whiteKing")[0] : kingMap.get("blackKing")[0];
        return (ChessPosition) objPos;
    }
}
