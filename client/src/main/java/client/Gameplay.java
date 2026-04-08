package client;

import chess.ChessGame;
import chess.ChessPosition;
import chess.ResponseException;
import model.AuthData;
import model.GameData;
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

    public Gameplay(String authToken, int gameId, ChessGame.TeamColor playerColor String serverUrl) {
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
                System.out.println("\n[Board updated]");
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
    public String eval(String input, ServerFacade server) {
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

    private String makeMove(String param) {
        return toString();
    }

    private String highlightLegalMoves(ChessPosition pos) {
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
    }

    private String resign() throws ResponseException {
        ws.resign(authToken, gameId);
        return "Resignation request sent.";
    }

    private String leave() throws ResponseException {
        ws.leaveGame(authToken, gameId);dd
        return "LEAVE";
    }

}
