package client;

import chess.*;
import ui.ServerFacade;

import java.util.Scanner;
import static ui.EscapeSequences.*;
public class ClientMain {

    private static String url;
    private static boolean auth;
    private static final Postlogin postlogin = new Postlogin();
    private static final Prelogin prelogin = new Prelogin();

    public ClientMain(String serverUrl) {
        this.url = serverUrl;


    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ServerFacade server = new ServerFacade("http://localhost:8080");
        preRepl(scanner, server);
        System.out.println();
    }

    public static void preRepl(Scanner scanner, ServerFacade server){
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        var result = "";
        while (!result.equals("quit")) {
            String logged = (auth)  ? "LOGGED IN" : "LOGGED OUT";
            String inputQuery = String.format(SET_TEXT_COLOR_WHITE+"[%s] >>> ", logged);
            System.out.print(inputQuery);
            String line = scanner.nextLine();

            try {
                result = prelogin.eval(line, server);

                System.out.println(SET_TEXT_COLOR_BLUE + result);


            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
    }
    public static void postRepl(Scanner scanner){

    }

}
