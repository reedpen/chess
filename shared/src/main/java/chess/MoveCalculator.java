package chess;
import java.util.Set;
import java.util.HashSet;

public class MoveCalculator {


    /**
     * Calculates and returns a set of possible moves across a particular row.
     */
    public Set<ChessMove> HorizontalMove(ChessBoard Board, ChessPosition startPos, ChessPiece piece) {
        Set<ChessMove> moves = new HashSet<>();
        for (int i = startPos.getColumn() + 1; i < 9; i++) {
            ChessPosition nextPos = new ChessPosition(startPos.getRow(), i);
            if (Board.getPiece(nextPos).getTeamColor() == piece.getTeamColor()){
                break;
            }
            if (Board.getPiece(nextPos).getTeamColor() != piece.getTeamColor() || Board.getPiece(nextPos) == null) {
                ChessMove newMove = new ChessMove(startPos, nextPos, null);
                moves.add(newMove);
            }
        }
        for (int i = startPos.getColumn() - 1; i > 0; i--) {
            ChessPosition nextPos = new ChessPosition(startPos.getRow(), i);
            if (Board.getPiece(nextPos).getTeamColor() == piece.getTeamColor()){
                break;
            }
            if (Board.getPiece(nextPos).getTeamColor() != piece.getTeamColor() || Board.getPiece(nextPos) == null) {
                ChessMove newMove = new ChessMove(startPos, nextPos, null);
                moves.add(newMove);
            }
        }
        return moves;
    }

    public Set<ChessMove> VerticalMove(ChessBoard Board, ChessPosition startPos, ChessPiece piece) {
        Set<ChessMove> moves = new HashSet<>();
        for (int i = startPos.getRow() + 1; i < 9; i++) {
            ChessPosition nextPos = new ChessPosition(i, startPos.getColumn());
            if (Board.getPiece(nextPos).getTeamColor() == piece.getTeamColor()){
                break;
            }
            if (Board.getPiece(nextPos).getTeamColor() != piece.getTeamColor() || Board.getPiece(nextPos) == null & nextPos.getRow() != 8) {
                ChessMove newMove = new ChessMove(startPos, nextPos, null);
                moves.add(newMove);
            }
        }
        for (int i = startPos.getColumn() - 1; i > 0; i--) {
            ChessPosition nextPos = new ChessPosition(startPos.getRow(), i);
            if (Board.getPiece(nextPos).getTeamColor() == piece.getTeamColor()){
                break;
            }
            if (Board.getPiece(nextPos).getTeamColor() != piece.getTeamColor() || Board.getPiece(nextPos) == null) {
                ChessMove newMove = new ChessMove(startPos, nextPos, null);
                moves.add(newMove);
            }
        }
        return moves;
    }



}
