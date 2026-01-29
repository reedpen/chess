package chess;
import java.util.Collection;
import java.util.HashSet;

public class MoveCalculator {


    /**
     * Calculates and returns a set of possible moves across a particular row.
     */

    private static boolean isOnBoard(ChessPosition pos) {
        return pos.getRow() >= 1 && pos.getRow() <= 8 & pos.getColumn() >= 1 && pos.getColumn() <= 8;
    }

    public static HashSet<ChessMove> crawlCalculator(ChessBoard board, ChessPosition startPos, int[][] directions) {
        HashSet<ChessMove> moves = new HashSet<>();
        ChessPiece myPiece = board.getPiece(startPos);
        ChessGame.TeamColor color = myPiece.getTeamColor();

        for (int[] dir : directions) {
            int rowChange = dir[0];
            int colChange = dir[1];

            int currentRow = startPos.getRow() + rowChange;
            int currentCol = startPos.getColumn() + colChange;

            while (isOnBoard(new ChessPosition(currentRow, currentCol))) {
                ChessPosition currPos = new ChessPosition(currentRow, currentCol);
                ChessPiece pieceAtPos = board.getPiece(currPos);

                if (pieceAtPos == null) {
                    moves.add(new ChessMove(startPos, currPos, null));
                } else {
                    if (pieceAtPos.getTeamColor() != color) {
                        moves.add(new ChessMove(startPos, currPos, null));
                    }
                    break;
                }
                currentRow += rowChange;
                currentCol += colChange;
            }
        }
        return moves;
    }

    public static HashSet<ChessMove> hopCalculator(ChessBoard board, ChessPosition startPos,  int[][] directions){
        HashSet<ChessMove> moves = new HashSet<>();
        ChessPiece myPiece = board.getPiece(startPos);
        ChessGame.TeamColor color = myPiece.getTeamColor();
        for (int[] dir : directions) {
            int rowChange = dir[0];
            int colChange = dir[1];

            int currentRow = startPos.getRow() + rowChange;
            int currentCol = startPos.getColumn() + colChange;

            ChessPosition currPos = new ChessPosition(currentRow, currentCol);

            if (isOnBoard(currPos)) {
                ChessPiece pieceAtPos = board.getPiece(currPos);
                if (pieceAtPos == null || pieceAtPos.getTeamColor() != color){
                    moves.add(new ChessMove(startPos, currPos, null));
                }
            }
        }
        return moves;
    }

    public static Collection<ChessMove> bishopCalculator(ChessBoard board, ChessPosition startPos){
        int[][] directions = {{1, 1}, {-1, -1}, {-1, 1}, {1, -1}};
        return crawlCalculator(board, startPos, directions);
    }

    public static Collection<ChessMove> rookCalculator(ChessBoard board, ChessPosition startPos){
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        return crawlCalculator(board, startPos, directions);
    }

    public static Collection<ChessMove> knightCalculator(ChessBoard board, ChessPosition startPos){
        int[][] directions = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};
        return hopCalculator(board, startPos, directions);
    }

    public static Collection<ChessMove> kingCalculator(ChessBoard board, ChessPosition startPos){
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        return hopCalculator(board, startPos, directions);
    }
    public static Collection<ChessMove> queenCalculator(ChessBoard board, ChessPosition startPos){
        Collection<ChessMove> moves = bishopCalculator(board,startPos);
        moves.addAll(rookCalculator(board, startPos));
        return moves;
    }

    public static Collection<ChessMove> pawnCalculator(ChessBoard board, ChessPosition myPosition) {

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

    private static void addPromotionMoves(HashSet<ChessMove> moves, ChessPosition start, ChessPosition end) {
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
    }

}
