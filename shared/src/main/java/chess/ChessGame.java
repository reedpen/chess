package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor currentTeam;
    ChessBoard board;

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */


    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        // is there a piece at startPos?
        if (board.getPiece(startPosition) != null){
            Collection<ChessMove> validMoves = new HashSet<>();
            ChessPiece piece = board.getPiece(startPosition);
            TeamColor color = piece.getTeamColor();
            Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
            ChessBoard ogBoard = board.deepCopy();
            // check if move leaves king in check
            for(ChessMove move : moves) {
                ChessPosition pos = move.endPos;
                ChessBoard copyBoard = ogBoard.deepCopy();
                copyBoard.addPiece(pos, null);
                copyBoard.addPiece(pos, piece);
                copyBoard.addPiece(startPosition, null);
                setBoard(copyBoard);
                if (!isInCheck(color)){
                    validMoves.add(move);
                }
                setBoard(ogBoard);
            }
            return validMoves;
        } else {
            return null;
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Returns a collection of moves a certain color's opposing team
     *
     * @param teamColor the team color whose enemy you want to see the moves of.
     * @return a set of all the enemy's possible ChessMoves
     */

    public Collection<ChessMove> getEnemyMoves(TeamColor teamColor) {
        Collection<ChessMove> enemyMoves = new HashSet<>();
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++){
                ChessPosition currPos = new ChessPosition(row, col);
                if (board.getPiece(currPos) != null) {
                    ChessPiece currPiece = board.getPiece(currPos);
                    if (currPiece.getTeamColor() != teamColor) {
                        enemyMoves.addAll(currPiece.pieceMoves(board, currPos));
                    }
                }
            }
        }
        return enemyMoves;
    }

    /**
     * Returns a set of end positions from a given set of moves.
     *
     * @param moves the set of ChessMove objects to get the positions from
     * @return a set of ChessMove objects
     */
    public Collection<ChessPosition> getEndPositions(Collection<ChessMove> moves) {
        Collection<ChessPosition> positions = new HashSet<>();
        for (ChessMove move : moves){
            positions.add(move.getEndPosition());
        }
        return positions;
    }
    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessPosition> enemyEndPositions = getEndPositions(getEnemyMoves(teamColor));
        return enemyEndPositions.contains(getKingPos(teamColor));
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public ChessPosition getKingPos(TeamColor teamColor){
        for (int row = 1; row<9; row++) {
            for (int col = 1; col<9; col++){
                ChessPosition pos = new ChessPosition(row, col);
                if (board.getPiece(pos) == null) {
                    continue;
                }
                ChessPiece piece = board.getPiece(pos);
                TeamColor color = piece.getTeamColor();
                if (color == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING){
                    return pos;
                }
            }
        }
        return null;
    }
}
