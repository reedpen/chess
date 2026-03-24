package client;

import model.AuthData;
import requestsandresults.LoginRequest;
import requestsandresults.RegisterRequest;
import requestsandresults.UserResult;
import chess.ResponseException;
import ui.ServerFacade;
import ui.EscapeSequences.*;
import java.util.Arrays;

import static ui.EscapeSequences.*;

public class Prelogin{

    private final ClientMain client;
    public Prelogin(ClientMain client) {
        this.client = client;
    }
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
                UserResult result = server.login(new LoginRequest(params[0], params[1]));
                AuthData authData = new AuthData(result.authToken(), result.username());
                client.setAuthData(authData);
                return String.format("You have successfully signed in as %s.", params[0]);
            }
        } catch (Exception e) {
            throw new ResponseException(401, "Incorrect password or username");
        }
        throw new ResponseException(401, "Expected: <username> <password>");
    }


    public String register(ServerFacade server, String... params) throws ResponseException {
        // 1. Guard against wrong number of arguments
        if (params.length < 3) {
            throw new ResponseException(400, "Expected: register <username> <password> <email>");
        }

        String username = params[0];
        String password = params[1];
        String email = params[2];

        // 2. Just call the server.
        // If it fails, the Facade throws a ResponseException with the REAL message.
        UserResult result = server.register(new RegisterRequest(username, password, email));

        // 3. If we get here, it was successful.
        AuthData authData = new AuthData(result.authToken(), result.username());
        client.setAuthData(authData);

        return String.format("Registered and logged in as %s.", username);
    }

    public String help() {
        return String.format("""
            %s--- COMMANDS ---%s
            %sregister <U> <P> <E>%s - create an account
            login <U> <P>        - sign in
            quit                 - exit
            help                 - show this menu
            """,
                SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA, RESET_TEXT_BOLD_FAINT,
                SET_TEXT_COLOR_WHITE, RESET_TEXT_COLOR
                );
    }
}
