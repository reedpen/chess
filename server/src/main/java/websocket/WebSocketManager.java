package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;

import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ErrorMessage;


import javax.xml.crypto.Data;
import java.io.IOException;

public class WebSocketManager implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final ConnectionManager connections = new ConnectionManager();
    private final Gson gson = new Gson();

    public WebSocketManager(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws Exception {
        Session session = ctx.session;
        String rawMessage = ctx.message();

        try {
            UserGameCommand baseCommand = gson.fromJson(rawMessage, UserGameCommand.class);

            // TODO: Validate auth token here using your UserService/AuthDAO.
            // String username = getUsername(baseCommand.getAuthToken());
            String username = "temp";

            connections.saveSession(baseCommand.getGameID(), session);

            switch (baseCommand.getCommandType()) {
                case CONNECT -> {connect(session, username, baseCommand);}
                case MAKE_MOVE -> {
                    // deserialize TWICE
                    MakeMoveCommand moveCommand = gson.fromJson(rawMessage, MakeMoveCommand.class);
                    makeMove(session, username, moveCommand);
                }
                case LEAVE -> leaveGame(session, username, baseCommand);
                case RESIGN -> resign(session, username, baseCommand);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            sendErrorMessage(session, "Error: " + ex.getMessage());
        }
    }

    // actions

    private void connect(Session session, String username, UserGameCommand command) throws IOException {
        int gameId = command.getGameID();
        String token = command.getAuthToken();

        try {
            AuthData authData = authDAO.getAuth(token);
            GameData gameData = gameDAO.getGame(command.getGameID());
            if (authData == null) {
                sendErrorMessage(session, "Error: Unauthorized");
                return;
            }
            if (gameData == null) {
                sendErrorMessage(session, "Error: Game does not exist.");
                return;
            }

            String role = "an observer";
            if (username.equals(gameData.whiteUsername())) {
                role = "White";
            } else if (username.equals(gameData.blackUsername())) {
                role = "Black";
            }
            LoadGameMessage loadMessage = new LoadGameMessage(gameData.game());
            String jsonLoadMessage = new Gson().toJson(loadMessage);
            session.getRemote().sendString(jsonLoadMessage);
            String message = String.format("%s joined the game as %s.", username, role);
            NotificationMessage notification = new NotificationMessage(message);
            connections.broadcast(gameId, session, notification);
        } catch (DataAccessException e) {sendErrorMessage(session, "Error: Database error - " + e.getMessage());}
    }

    private void makeMove(Session session, String username, MakeMoveCommand command) {
        int gameId = command.getGameID();
        ChessMove move = command.getMove();
        ChessGame.TeamColor color = command.get
        try {
            GameData game = gameDAO.getGame(gameId);

            String message = String.format("%s moved %s to location %s.", username, role);
            NotificationMessage notification = new NotificationMessage(message);
            connections.broadcast(gameId, session, notification);
        } catch (DataAccessException e){
            sendErrorMessage(session, "Error: Database error - " + e.getMessage());
        }


    }

    private void leaveGame(Session session, String username, UserGameCommand command) {
lea

        connections.remove(command.getGameID(), session);
    }

    private void resign(Session session, String username, UserGameCommand command) {

    }

    // helpers

    private void sendErrorMessage(Session session, String errorMessage) {
        try {
            ErrorMessage error = new ErrorMessage(errorMessage);
            session.getRemote().sendString(gson.toJson(error));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}