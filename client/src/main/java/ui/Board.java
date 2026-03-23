package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.List;

import static ui.EscapeSequences.*;

public class Board {
    // Use List instead of ArrayList, and explicitly define the reversed list
    private static final List<Character> WHITE_COLS = List.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h');
    private static final List<Character> BLACK_COLS = List.of('h', 'g', 'f', 'e', 'd', 'c', 'b', 'a');
    private static final String BORDER_COLOR = SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE;

    public static void printWhiteBoard(ChessBoard board) {
        System.out.println();
        printBorder(WHITE_COLS);
        printBoard(board, true);
        printBorder(WHITE_COLS);
        System.out.println();
    }

    public static void printBlackBoard(ChessBoard board) {
        System.out.println();
        printBorder(BLACK_COLS);
        printBoard(board, false);
        printBorder(BLACK_COLS);
        System.out.println();
    }

    public static void printBorder(List<Character> chars) {
        System.out.print(BORDER_COLOR + EMPTY + " " + EMPTY);

        for (Character ch : chars) {

            System.out.print(BORDER_COLOR + ch + "   ");
        }

        System.out.println(BORDER_COLOR + EMPTY + "  " + RESET_BG_COLOR + RESET_TEXT_COLOR);
    }

    public static void printBoard(ChessBoard board, boolean white) {
        int startRow = white ? 8 : 1;
        int endRow   = white ? 0 : 9;
        int rowStep  = white ? -1 : 1;

        int startCol = white ? 1 : 8;
        int endCol   = white ? 9 : 0;
        int colStep  = white ? 1 : -1;

        for (int row = startRow; row != endRow; row += rowStep) {
            // Left row number
            System.out.print(BORDER_COLOR + EMPTY + row + EMPTY);

            for (int col = startCol; col != endCol; col += colStep) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                boolean isLightSquare = (row + col) % 2 != 0;
                String bgColor = isLightSquare ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;
                String pieceStr = getPieceString(piece);

                System.out.print(bgColor + pieceStr);
            }

            // Right row number and reset background for the newline
            System.out.println(BORDER_COLOR + EMPTY + row + EMPTY + RESET_BG_COLOR + RESET_TEXT_COLOR);
        }
    }

    private static String getPieceString(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        }

        String textColor = (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                ? SET_TEXT_COLOR_WHITE
                : SET_TEXT_COLOR_BLACK;

        String symbol = switch (piece.getPieceType()) {
            case KING -> BLACK_KING;
            case QUEEN -> BLACK_QUEEN;
            case BISHOP -> BLACK_BISHOP;
            case KNIGHT -> BLACK_KNIGHT;
            case ROOK -> BLACK_ROOK;
            case PAWN -> BLACK_PAWN;
        };

        return textColor + symbol;
    }
}