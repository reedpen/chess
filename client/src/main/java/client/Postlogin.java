package client;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import requestsandresults.*;
import chess.ResponseException;
import ui.ServerFacade;

import java.util.Arrays;
import java.util.Collection;

import static ui.Board.printBlackBoard;
import static ui.Board.printWhiteBoard;
import static ui.EscapeSequences.*;

public class Postlogin {

    private final AuthData authData;

    public Postlogin(AuthData authData) {
        this.authData = authData;
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
                case "create" -> createGame(server, params);
                case "logout" -> logout(server);
                case "list" -> listGames(server);
                case "join" -> joinGame(server, params);
                case "observe" -> observeGame(server, params);
                case "help" -> help();
                case "quit" -> "quit";
                default -> "Unknown command: " + cmd + "\n" + help();
            };
        } catch (ResponseException ex) {
            return "Error: " + ex.getMessage();
        }
    }

    public String help() {
        return String.format("""
            %s--- COMMANDS ---%s
            %screate <NAME>%s         - start a new game
            %slist%s                  - list all current games
            %sjoin <COLOR> <ID>%s     - join as WHITE or BLACK
            %sobserve <ID>%s          - watch a game
            %slogout%s                - return to start
            %squit%s                  - exit application
            %shelp%s                  - show this menu
            """,
                SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA, RESET_TEXT_BOLD_FAINT,
                SET_TEXT_COLOR_WHITE, RESET_TEXT_COLOR,
                SET_TEXT_COLOR_WHITE, RESET_TEXT_COLOR,
                SET_TEXT_COLOR_WHITE, RESET_TEXT_COLOR,
                SET_TEXT_COLOR_WHITE, RESET_TEXT_COLOR,
                SET_TEXT_COLOR_WHITE, RESET_TEXT_COLOR,
                SET_TEXT_COLOR_WHITE, RESET_TEXT_COLOR,
                SET_TEXT_COLOR_WHITE, RESET_TEXT_COLOR);
    }

    public String joinGame(ServerFacade server, String... params) throws ResponseException {
        if (params.length != 2) {
            throw new ResponseException(400, "Invalid arguments. Usage: join <WHITE|BLACK> <ID>");
        }

        String colorInput = params[0].toUpperCase();
        if (!colorInput.equals("WHITE") && !colorInput.equals("BLACK")) {
            throw new ResponseException(400, "Invalid color: " + colorInput + ". Must be 'WHITE' or 'BLACK'.");
        }

        int gameID = parseGameID(params[1]);

        server.joinGame(authData, new JoinGameRequest(colorInput, gameID));
        ChessGame targetGame = findGame(server, gameID);

        if (colorInput.equals("WHITE")) {
            printWhiteBoard(targetGame.getBoard());
        } else {
            printBlackBoard(targetGame.getBoard());
        }

        return String.format("Joined game %d as %s.", gameID, colorInput);
    }

    public String observeGame(ServerFacade server, String... params) throws ResponseException {
        if (params.length != 1) {
            throw new ResponseException(400, "Invalid arguments. Usage: observe <ID>");
        }

        int gameID = parseGameID(params[0]);

        server.joinGame(authData, new JoinGameRequest(null, gameID));
        ChessGame targetGame = findGame(server, gameID);

        printWhiteBoard(targetGame.getBoard());
        return "Observing game " + gameID + ".";
    }

    public String listGames(ServerFacade server) throws ResponseException {
        Collection<GameData> games = server.listGames(authData).games();
        if (games == null || games.isEmpty()) {
            return "No active games found on server.";
        }

        StringBuilder sb = new StringBuilder("--- Active Games ---\n");
        for (GameData game : games) {
            sb.append(String.format("[%d] %-15s | White: %-10s | Black: %-10s\n",
                    game.gameID(),
                    game.gameName(),
                    formatUser(game.whiteUsername()),
                    formatUser(game.blackUsername())));
        }
        return sb.toString();
    }

    public String logout(ServerFacade server) throws ResponseException {
        server.logout(authData);

        return "Logged out successfully.";
    }

    public String createGame(ServerFacade server, String... params) throws ResponseException {
        if (params.length < 1) {
            throw new ResponseException(400, "Invalid arguments. Usage: create <NAME>");
        }

        String gameName = String.join(" ", params);
        if (gameName.isBlank()) {
            throw new ResponseException(400, "Game name cannot be empty.");
        }

        server.createGame(authData, new CreateGameRequest(gameName));
        return "Game '" + gameName + "' created.";
    }


    private int parseGameID(String idStr) throws ResponseException {
        try {
            return Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            throw new ResponseException(400, "'" + idStr + "' is not a valid number. Please use a numeric Game ID.");
        }
    }

    private ChessGame findGame(ServerFacade server, int gameID) throws ResponseException {
        Collection<GameData> games = server.listGames(authData).games();
        if (games == null) {
            throw new ResponseException(500, "Failed to retrieve game list from server.");
        }

        return games.stream()
                .filter(g -> g.gameID() == gameID)
                .map(GameData::game)
                .findFirst()
                .orElseThrow(() -> new ResponseException(400, "Game ID " + gameID + " no longer exists."));
    }

    private String formatUser(String user) {
        return (user == null || user.isBlank()) ? "---" : user;
    }
}