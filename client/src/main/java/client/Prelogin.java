package client;

import service.ResponseException;

import java.util.Arrays;

public class Prelogin {

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException{
        return toString();
    }


    public String register(String... params) throws ResponseException{
        return toString();
    }
    public String help() {
        return """
                    - register <username> <password> <email>
                    - signIn <username> <password>
                    - quit
                """;
    }
}
