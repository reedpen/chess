package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final PieceType type;
    private final ChessGame.TeamColor pieceColor;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        this.pieceColor = pieceColor;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "type=" + type +
                ", pieceColor=" + pieceColor +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return type == that.type && pieceColor == that.pieceColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, pieceColor);
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }

    public Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {

        HashSet<ChessMove> moves = new HashSet<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);
        ChessGame.TeamColor color = myPiece.getTeamColor();

        int direction = (color == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (color == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (color == ChessGame.TeamColor.WHITE) ? 8 : 1;
        ChessPosition forwardPos = new ChessPosition(row + direction, col);

        // non capture moves and promotion
        if (isOnBoard(forwardPos) && board.getPiece(forwardPos) == null) {
            if (row+direction == promotionRow){
                addPromotionMoves(moves, myPosition, forwardPos);
            } else {
                moves.add(new ChessMove(myPosition, forwardPos, null));
                ChessPosition doublePos = new ChessPosition(row + (direction*2), col);
                if (board.getPiece(doublePos) == null && row == startRow){
                    moves.add(new ChessMove(myPosition, doublePos, null));
                }

            }

        }
        int[] captureCols = {col - 1, col + 1};

        for (int capCol : captureCols){
            ChessPosition capturePos = new ChessPosition(row + direction, capCol);
            if (isOnBoard(capturePos)) {
                ChessPiece targetPiece = board.getPiece(capturePos);
                if (targetPiece != null && targetPiece.getTeamColor() != color) {
                    if (row + direction == promotionRow){
                        addPromotionMoves(moves, myPosition, capturePos);
                    } else {
                        moves.add(new ChessMove(myPosition, capturePos, null));
                    }

                }
            }
        }

        return moves;
    }


    private boolean isOnBoard(ChessPosition pos) {
        return pos.getRow() >= 1 && pos.getRow() <= 8 & pos.getColumn() >= 1 && pos.getColumn() <= 8;
    }
    private void addPromotionMoves(HashSet<ChessMove> moves, ChessPosition start, ChessPosition end) {
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
    }
}
