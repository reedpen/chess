package client;

import model.UserData;
import requestsandresults.LoginRequest;
import requestsandresults.RegisterRequest;
import server.Server;
import service.ResponseException;
import ui.ServerFacade;

import java.util.Arrays;

public class Prelogin{


    public String eval(String input, ServerFacade server) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(server, params);
                case "login" -> login(server, params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(ServerFacade server, String... params) throws ResponseException{
        try {
            if (params.length >= 2) {
                server.login(new LoginRequest(params[0], params[1]));
                return String.format("You signed in as %s.", params[0]);
            }
        } catch (Exception e) {
            throw new ResponseException(401, "Incorrect password or username");
        }
        throw new ResponseException(401, "Expected: <username> <password>");
    }


    public String register(ServerFacade server, String... params) throws ResponseException{
        try{
            if (params.length >= 3) {
                server.register(new RegisterRequest(params[0], params[1], params[2]));

                return String.format("You are registered as %s.", params[0]);
            }
        } catch (Exception e) {
            throw new ResponseException(401, "Username already taken.");
        }

        throw new ResponseException(401, "Expected: <username> <password> <email>");
    }

    public String help() {
        return """
                    - register <username> <password> <email>
                    - login <username> <password>
                    - quit
                """;
    }
}
