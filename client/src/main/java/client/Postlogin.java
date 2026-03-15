package client;

import service.ResponseException;

import java.util.Arrays;

public class Postlogin{



    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> createGame(params);
                case "logout" -> logout();
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
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

    public String joinGame(String... params) throws ResponseException{
        return toString();
    }
    public String observeGame(String... params) throws ResponseException{
        return toString();
    }
    public String listGames() throws ResponseException{
        return toString();
    }
    public String logout() throws ResponseException{
        return toString();
    }
    public String createGame(String... params) throws ResponseException{
        return toString();
    }
}
