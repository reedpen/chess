package client;

import chess.*;
import model.AuthData;
import ui.ServerFacade;

import java.util.Scanner;
import static ui.EscapeSequences.*;
public class ClientMain {
    private final ServerFacade server;
    private final Prelogin prelogin;
    private Postlogin postlogin;
    private AuthData currentAuth = null;

    public ClientMain(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
        this.prelogin = new Prelogin(this);
    }

    public static void main(String[] args) {
        ClientMain repl = new ClientMain("http://localhost:8080");
        repl.run();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        String result = "";
        while (!result.equalsIgnoreCase("quit")) {
            String logged = (currentAuth != null) ? "LOGGED IN" : "LOGGED OUT";
            System.out.print(SET_TEXT_COLOR_WHITE + "[" + logged + "] >>> ");

            String line = scanner.nextLine();

            try {
                if (currentAuth == null) {
                    result = prelogin.eval(line, server);
                } else {
                    result = postlogin.eval(line, server);
                }

                System.out.println(SET_TEXT_COLOR_BLUE + result);

                if (result != null && result.contains("Logged out")) {
                    currentAuth = null;
                    postlogin = null;
                }

            } catch (Throwable e) {
                System.out.println(SET_TEXT_COLOR_RED + (e.getMessage() != null ? e.getMessage() : e.toString()));
            }
        }
    }

    public void setAuthData(AuthData authData) {
        this.currentAuth = authData;
        this.postlogin = new Postlogin(this.currentAuth);
    }

}
