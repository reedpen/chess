package client;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import requestsandresults.*;
import chess.ResponseException;
import ui.ServerFacade;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static ui.Board.printBlackBoard;
import static ui.Board.printWhiteBoard;
import static ui.EscapeSequences.*;

public class Postlogin {

    private final AuthData authData;
    private List<GameData> gameCache = new java.util.ArrayList<>();
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
            throw new ResponseException(400, "Invalid arguments. Usage: join <WHITE|BLACK> <LIST_INDEX>");
        }

        String colorInput = params[0].toUpperCase();
        if (!colorInput.equals("WHITE") && !colorInput.equals("BLACK")) {
            throw new ResponseException(400, "Invalid color: " + colorInput + ". Must be 'WHITE' or 'BLACK'.");
        }
        GameData gameData = getGameFromCache(params[1]);
        int actualGameID = gameData.gameID();
        server.joinGame(authData, new JoinGameRequest(colorInput, actualGameID));

        if (colorInput.equals("WHITE")) {
            printWhiteBoard(gameData.game().getBoard());
        } else {
            printBlackBoard(gameData.game().getBoard());
        }

        return String.format("Successfully joined game '%s' as %s.", gameData.gameName(), colorInput);
    }

    public String observeGame(ServerFacade server, String... params) throws ResponseException {
        if (params.length != 1) {
            throw new ResponseException(400, "Invalid arguments. Usage: observe <LIST_INDEX>");
        }

        GameData gameData = getGameFromCache(params[0]);
        printWhiteBoard(gameData.game().getBoard());

        return String.format("Now observing game: %s (Index: %s)", gameData.gameName(), params[0]);
    }

    public String listGames(ServerFacade server) throws ResponseException {
        Collection<GameData> games = server.listGames(authData).games();
        gameCache.clear();

        if (games == null || games.isEmpty()) {
            return "No active games found on server.";
        }

        gameCache.addAll(games);

        StringBuilder sb = new StringBuilder("--- Active Games ---\n");
        for (int i = 0; i < gameCache.size(); i++) {
            GameData game = gameCache.get(i);
            int displayIndex = i + 1;

            sb.append(String.format("[%d] %-15s | White: %-10s | Black: %-10s\n",
                    displayIndex,
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
    private GameData validateAndGetGame(ServerFacade server, int gameID) throws ResponseException {
        Collection<GameData> games = server.listGames(authData).games();

        if (games == null || games.isEmpty()) {
            throw new ResponseException(400, "No active games found. Use 'create' to start a new game first.");
        }

        return games.stream()
                .filter(g -> g.gameID() == gameID)
                .findFirst()
                .orElseThrow(() -> new ResponseException(400,
                        String.format("Game ID %d not found. Type 'list' to see all available games.", gameID)));
    }
    private String formatUser(String user) {
        return (user == null || user.isBlank()) ? "---" : user;
    }

    private GameData getGameFromCache(String indexStr) throws ResponseException {
        int index;
        try {
            index = Integer.parseInt(indexStr) - 1;
        } catch (NumberFormatException e) {
            throw new ResponseException(400, "Error: Please provide a valid list number (e.g., 1).");
        }

        if (index < 0 || index >= gameCache.size()) {
            throw new ResponseException(400, "Error: No game found at index " + (index + 1) + ". Run 'list' to see current options.");
        }

        return gameCache.get(index);
    }
}