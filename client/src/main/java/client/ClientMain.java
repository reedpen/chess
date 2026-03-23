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

    public void run(){
        Scanner scanner = new Scanner(System.in);
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        var result = "";
        while (!result.equals("quit")) {
            String logged = (currentAuth != null)  ? "LOGGED IN" : "LOGGED OUT";
            String inputQuery = String.format(SET_TEXT_COLOR_WHITE+"[%s] >>> ", logged);
            System.out.print(inputQuery);
            String line = scanner.nextLine();
            try {
            if (currentAuth == null) {
                result = prelogin.eval(line, server);
            } else {
                result = postlogin.eval(line, server);

                if (result.equals("Logged out.")) {
                    currentAuth = null;
                    postlogin = null;
                }
            }
            System.out.println(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                System.out.println(SET_TEXT_COLOR_RED + e.getMessage());
            }
        }
    }

    public void setAuthData(AuthData authData) {
        this.currentAuth = authData;
        this.postlogin = new Postlogin(this.currentAuth);
    }

}
