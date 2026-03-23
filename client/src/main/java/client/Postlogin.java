package client;

import requestsandresults.JoinGameRequest;
import requestsandresults.LoginRequest;
import server.Server;
import service.ResponseException;
import ui.ServerFacade;

import java.util.Arrays;

public class Postlogin{



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
                    - join <gameID>
                    - observe <gameID>
                    - logout
                    - quit
                """;
    }

    public String joinGame(ServerFacade server, String... params) throws ResponseException{
        try {
            if (params.length == 2) {
                server.joinGame(new JoinGameRequest(params[0], Integer.parseInt(params[1])));
                return String.format("You joined game %s in as %s.", params[1], params[0]);
            }
        } catch (Exception e) {
            throw new ResponseException(401, e.getLocalizedMessage());
        }
        throw new ResponseException(401, "Expected: <color> <game name>");
    }
    public String observeGame(ServerFacade server, String... params) throws ResponseException{
        server.joinGame(new JoinGameRequest());
    }
    public String listGames(ServerFacade server) throws ResponseException{
        return server.listGames()
    }
    public String logout(ServerFacade server) throws ResponseException{
        return toString();
    }
    public String createGame(ServerFacade server, String... params) throws ResponseException{
        return toString();
    }
}
