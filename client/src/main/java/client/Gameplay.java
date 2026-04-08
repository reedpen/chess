package client;

import chess.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.server.Response;
import requestsandresults.CreateGameRequest;
import requestsandresults.JoinGameRequest;
import ui.ServerMessageHandler;
import ui.ServerFacade;
import ui.WebSocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.Scanner;

import static ui.Board.*;
import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_WHITE;

public class Gameplay implements ServerMessageHandler {

    private final String authToken;
    private final int gameId;
    private final WebSocketFacade ws;
    private ChessGame currentGame;
    private final ChessGame.TeamColor playerColor;

    public Gameplay(String authToken, int gameId, ChessGame.TeamColor playerColor, String serverUrl) {
        this.authToken = authToken;
        this.gameId = gameId;
        this.playerColor = playerColor;
        WebSocketFacade tempWs = null;
        try {
            tempWs = new WebSocketFacade(serverUrl, this);

            tempWs.connect(authToken, gameId);
        } catch (Exception e) {
            System.out.println("Network error: " + e.getMessage());
        }
        this.ws = tempWs;
    }


    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGameMessage loadMsg = (LoadGameMessage) message;
                this.currentGame = loadMsg.getGame();
                redrawBoard();
            }
            case NOTIFICATION -> {
                NotificationMessage notifMsg = (NotificationMessage) message;
                System.out.println("\n" + notifMsg.getMessage());
            }
            case ERROR -> {
                ErrorMessage errMsg = (ErrorMessage) message;
                System.out.println("\n" + errMsg.getErrorMessage());
            }
        }

    }
    public String eval(String input) {
        if (input == null || input.isBlank()) {
            return help();
        }

        try {
            String[] tokens = input.trim().split("\\s+");
            String cmd = tokens[0].toLowerCase();
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "redraw" -> redrawBoard();
                case "move" -> makeMove(params);
                case "highlight" -> highlightLegalMoves(params);
                case "resign" -> resign();
                case "leave" -> leave();
                case "help" -> help();
                default -> "Unknown command: " + cmd + "\n" + help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        return String.format("""
            %s--- COMMANDS ---%s
            %sredraw%s                   - redraws the chess board
            %smove <LETTER NUMBER>%s     - perform a chess move
            %shighlight <LETTER NUMBER>%s- join as WHITE or BLACK
            %sresign%s                   - forfeit game
            %sleave%s                    - remove current user from game
            %shelp%s                     - show this menu
            """,
                SET_TEXT_BOLD + SET_TEXT_COLOR_GREEN, RESET_TEXT_BOLD_FAINT,
                SET_TEXT_COLOR_WHITE, RESET_TEXT_COLOR,
                SET_TEXT_COLOR_WHITE, RESET_TEXT_COLOR,
                SET_TEXT_COLOR_WHITE, RESET_TEXT_COLOR,
                SET_TEXT_COLOR_WHITE, RESET_TEXT_COLOR,
                SET_TEXT_COLOR_WHITE, RESET_TEXT_COLOR,
                SET_TEXT_COLOR_WHITE, RESET_TEXT_COLOR,
                SET_TEXT_COLOR_WHITE, RESET_TEXT_COLOR);
    }


    // call websocketfacade

    private String redrawBoard() {
        if (currentGame == null) {
            return "Board has not loaded yet.";
        }

        if ("BLACK".equalsIgnoreCase(String.valueOf(playerColor))) {
            printBlackBoard(currentGame.getBoard());
        } else {

            printWhiteBoard(currentGame.getBoard());
        }
        return "";
    }

    private String makeMove(String... params) throws ResponseException {
        if (params.length < 2 || params.length > 3) {
            return "Usage: move <STARTING_POS> <ENDING_POS> <PROMO_PIECE> (e.g. 'move e7 e8 queen')";
        }

        ChessPosition start = parsePosition(params[0]);
        ChessPosition end = parsePosition(params[1]);

        if (currentGame == null) return "Error: Game board hasn't loaded yet.";
        ChessPiece piece = currentGame.getBoard().getPiece(start);
        if (piece == null) return "Error: No piece at starting position.";

        boolean isPawn = piece.getPieceType() == ChessPiece.PieceType.PAWN;
        boolean isPromotionRank = (piece.getTeamColor() == ChessGame.TeamColor.WHITE && end.getRow() == 8) ||
                (piece.getTeamColor() == ChessGame.TeamColor.BLACK && end.getRow() == 1);
        ChessPiece.PieceType promoPiece = null;
        if (isPawn && isPromotionRank) {
            if (params.length == 3) {
                promoPiece = parsePromotionPiece(params[2]);
            } else {
                System.out.print("Pawn promotion! Enter piece (queen, rook, bishop, knight): ");
                Scanner scanner = new Scanner(System.in);
                String input = scanner.nextLine();
                promoPiece = parsePromotionPiece(input);
            }

            if (promoPiece == null) {
                return "Error: Invalid promotion piece. Move cancelled.";
            }
        } else if (params.length == 3) {
            return "Error: This move is not eligible for promotion.";
        }

        // 4. Send the move (passing promoPiece, which is null for normal moves, or the enum for promotions)
        ws.makeMove(authToken, gameId, new ChessMove(start, end, promoPiece));
        return "Move command sent.";
    }

    private String highlightLegalMoves(String... params) throws ResponseException {
        if (params.length != 1) {
            return "Usage: highlight <POSITION> (e.g. 'highlight e2')";
        }
        ChessPosition pos = parsePosition(params[0]);
        if (currentGame == null) {
            return "Board has not loaded yet.";
        }

        if ("BLACK".equalsIgnoreCase(String.valueOf(playerColor))) {
            printBlackBoardHighlight(currentGame.getBoard(), pos);
        } else {
            printWhiteBoardHighlight(currentGame.getBoard(), pos);
        }
        return "";
    }
    private String resign() throws ResponseException {
        ws.resign(authToken, gameId);
        return "Resignation request sent.";
    }

    private String leave() throws ResponseException {
        ws.leaveGame(authToken, gameId);
        return "LEAVE";
    }


    private ChessPosition parsePosition(String pos) throws ResponseException {
        if (pos == null || pos.length() != 2) {
            throw new ResponseException(400, "Invalid position format. Use <letter><number> (e.g., e2).");
        }

        pos = pos.toLowerCase();
        char colChar = pos.charAt(0);
        char rowChar = pos.charAt(1);

        if (colChar < 'a' || colChar > 'h' || rowChar < '1' || rowChar > '8') {
            throw new ResponseException(400, "Position out of bounds. Must be between a1 and h8.");
        }

        int col = colChar - 'a' + 1;
        int row = rowChar - '0';

        return new ChessPosition(row, col);
    }

    private ChessPiece.PieceType parsePromotionPiece(String pieceStr) {
        if (pieceStr == null) return null;

        return switch (pieceStr.toLowerCase()) {
            case "queen", "q" -> ChessPiece.PieceType.QUEEN;
            case "rook", "r" -> ChessPiece.PieceType.ROOK;
            case "bishop", "b" -> ChessPiece.PieceType.BISHOP;
            case "knight", "n" -> ChessPiece.PieceType.KNIGHT;
            default -> null;
        };
    }
    }

