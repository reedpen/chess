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

public class Postlogin {

    private final AuthData authData;

    public Postlogin(AuthData authData) {
        this.authData = authData;
    }

    public String eval(String input, ServerFacade server) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "create" -> createGame(server, params);
                case "logout" -> logout(server);
                case "list" -> listGames(server);
                case "join" -> joinGame(server, params);
                case "observe" -> observeGame(server, params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        return """
                - create <game name>
                - list
                - join <color> <gameID>
                - observe <gameID>
                - logout
                - quit
                """;
    }

    public String joinGame(ServerFacade server, String... params) throws ResponseException {
        if (params.length != 2) {
            throw new ResponseException(400, "Expected: join <color> <gameID>");
        }

        int gameID = parseGameID(params[1]);
        String color = params[0].toUpperCase();

        server.joinGame(authData, new JoinGameRequest(color, gameID));
        ChessGame targetGame = findGame(server, gameID);

        if (color.equals("WHITE")) {
            printWhiteBoard(targetGame.getBoard());
        } else if (color.equals("BLACK")) {
            printBlackBoard(targetGame.getBoard());
        } else {
            throw new ResponseException(400, "Error: Color must be WHITE or BLACK.");
        }

        return String.format("Successfully joined game %d as %s.", gameID, color);
    }

    public String observeGame(ServerFacade server, String... params) throws ResponseException {
        if (params.length != 1) {
            throw new ResponseException(400, "Expected: observe <gameID>");
        }

        int gameID = parseGameID(params[0]);

        server.joinGame(authData, new JoinGameRequest("", gameID));
        ChessGame targetGame = findGame(server, gameID);

        printWhiteBoard(targetGame.getBoard());
        return String.format("Now observing game %d.", gameID);
    }

    public String listGames(ServerFacade server) throws ResponseException {
        Collection<GameData> games = server.listGames(authData).games();
        if (games == null || games.isEmpty()) {
            return "No active games.";
        }

        StringBuilder sb = new StringBuilder("Active Games:\n");
        for (GameData game : games) {
            sb.append(String.format("ID: %d | Name: %s | White: %s | Black: %s\n",
                    game.gameID(), game.gameName(),
                    formatUser(game.whiteUsername()), formatUser(game.blackUsername())));
        }
        return sb.toString();
    }

    public String logout(ServerFacade server) throws ResponseException {
        server.logout(authData);
        return "Logged out.";
    }

    public String createGame(ServerFacade server, String... params) throws ResponseException {
        if (params.length != 1) {
            throw new ResponseException(400, "Expected: create <game name>");
        }

        server.createGame(authData, new CreateGameRequest(params[0]));
        return String.format("Game '%s' created.", params[0]);
    }


    private int parseGameID(String idStr) throws ResponseException {
        try {
            return Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            throw new ResponseException(400, "Error: gameID must be a number.");
        }
    }

    private ChessGame findGame(ServerFacade server, int gameID) throws ResponseException {
        Collection<GameData> games = server.listGames(authData).games();
        if (games != null) {
            for (GameData game : games) {
                if (game.gameID() == gameID) return game.game();
            }
        }
        throw new ResponseException(400, "Error: Could not retrieve game state.");
    }

    private String formatUser(String user) {
        return user == null ? "Empty" : user;
    }
}