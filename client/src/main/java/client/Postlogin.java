package client;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import requestsandresults.CreateGameRequest;
import requestsandresults.JoinGameRequest;
import requestsandresults.ListGamesResult;
import service.ResponseException;
import ui.ServerFacade;

import java.util.Arrays;
import java.util.Collection;

import static ui.Board.printBlackBoard;
import static ui.Board.printWhiteBoard;

public class Postlogin{


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

    public String joinGame(ServerFacade server, String... params) throws ResponseException{
        if (params.length == 2) {
            try {
                String color = params[0].toUpperCase();
                int gameID = Integer.parseInt(params[1]);

                server.joinGame(authData, new JoinGameRequest(color, gameID));
                ListGamesResult result = server.listGames(authData);
                ChessGame targetGame = null;
                if (result.games() != null) {
                    for (GameData game : result.games()) {
                        if (game.gameID() == gameID) {
                            targetGame = game.game();
                            break;
                        }
                    }
                }
                if (targetGame == null) {
                    throw new ResponseException(400, "Error: Could not retrieve game state.");
                }
                if (color.equals("WHITE")) {
                    printWhiteBoard(targetGame.getBoard());
                } else if (color.equals("BLACK")) {
                    printBlackBoard(targetGame.getBoard());
                } else {
                    throw new ResponseException(400, "Error: Color must be WHITE or BLACK.");
                }
                return String.format("Successfully joined game %d as %s.", gameID, color);
            } catch (NumberFormatException e) {
                throw new ResponseException(400, "Error: gameID must be a number.");
            }
        }
        throw new ResponseException(400, "Expected: join <color> <gameID>");

    }
    public String observeGame(ServerFacade server, String... params) throws ResponseException {
        if (params.length == 1) {
            try {
                int gameID = Integer.parseInt(params[0]);

                // 1. Join as an observer (empty color)
                server.joinGame(authData, new JoinGameRequest("", gameID));

                // 2. Fetch the game state to get the board
                ListGamesResult result = server.listGames(authData);
                chess.ChessGame targetGame = null;

                if (result.games() != null) {
                    for (GameData game : result.games()) {
                        if (game.gameID() == gameID) {
                            targetGame = game.game();
                            break;
                        }
                    }
                }

                if (targetGame == null) {
                    throw new ResponseException(400, "Error: Could not retrieve game state.");
                }

                // 3. Draw the board (Observers default to White perspective)
                printWhiteBoard(targetGame.getBoard());

                return String.format("Now observing game %d.", gameID);

            } catch (NumberFormatException e) {
                throw new ResponseException(400, "Error: gameID must be a number.");
            }
        }
        throw new ResponseException(400, "Expected: observe <gameID>");
    }
    public String listGames(ServerFacade server) throws ResponseException{
        ListGamesResult result =  server.listGames(authData);
        Collection<GameData> games = result.games();
        if (games == null || games.isEmpty()) {
            return "No active games.";
        }
        StringBuilder sb = new StringBuilder("Active Games:\n");
        for (GameData game : games) {
            sb.append(String.format("ID: %d | Name: %s | White: %s | Black: %s\n",
                    game.gameID(),
                    game.gameName(),
                    game.whiteUsername() == null ? "Empty" : game.whiteUsername(),
                    game.blackUsername() == null ? "Empty" : game.blackUsername()));
        }
        return sb.toString();
    }
    public String logout(ServerFacade server) throws ResponseException{
        server.logout(authData);
        return "Logged out.";
    }
    public String createGame(ServerFacade server, String... params) throws ResponseException{
        if (params.length == 1) {
            String gameName = params[0];
            server.createGame(authData, new CreateGameRequest(gameName));
            return String.format("Game '%s' created.", gameName);
        }
        throw new ResponseException(400, "Expected: create <game name>");
    }
}
