package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;
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
            GameData gameData = gameDAO.getGame(gameId);
            if (authData == null) {
                sendErrorMessage(session, "Error: Unauthorized");
                return;
            }
            if (gameData == null) {
                sendErrorMessage(session, "Error: Game does not exist.");
                return;
            }

            LoadGameMessage loadMessage = new LoadGameMessage(gameData.game());
            String jsonLoadMessage = new Gson().toJson(loadMessage);
            session.getRemote().sendString(jsonLoadMessage);

            String role = "an observer";
            if (username.equals(gameData.whiteUsername())) {
                role = "White";
            } else if (username.equals(gameData.blackUsername())) {
                role = "Black";
            }
            String message = String.format("%s joined the game as %s.", username, role);

            NotificationMessage notification = new NotificationMessage(message);
            connections.broadcast(gameId, session, notification);

        } catch (DataAccessException e) {sendErrorMessage(session, "Error: Database error - " + e.getMessage());}
    }

    private void makeMove(Session session, String username, MakeMoveCommand command) throws IOException{
        int gameId = command.getGameID();
        ChessMove move = command.getMove();
        try {
            GameData game = gameDAO.getGame(gameId);
            if (game.game().isGameOver()) {
                sendErrorMessage(session, "Error: The game is already over.");
                return;
            }
            ChessGame.TeamColor color = game.game().getTeamTurn();
            // check for correct turn order
            if ((username.equals(game.whiteUsername()) && color == ChessGame.TeamColor.WHITE) ||
                    (username.equals(game.blackUsername()) && color == ChessGame.TeamColor.BLACK)) {
                try {
                    game.game().makeMove(move);

                } catch (InvalidMoveException e) {
                    sendErrorMessage(session, "Error: Invalid move" + e.getMessage());return;
                }
                gameDAO.updateGame(game);
                // update all sessions with new board
                LoadGameMessage loadMessage = new LoadGameMessage(game.game());
                String jsonLoadMessage = new Gson().toJson(loadMessage);
                for (Session s : connections.connections.get(gameId)) {
                    if (s.isOpen()) {
                        s.getRemote().sendString(jsonLoadMessage);
                    }
                }
                String message = String.format("%s moved from %s to %s.", username, move.getStartPosition(), move.getEndPosition());
                NotificationMessage notification = new NotificationMessage(message);
                connections.broadcast(gameId, session, notification);

                // check game end conditions
                ChessGame.TeamColor opposingColor = (color == ChessGame.TeamColor.WHITE) ?
                        ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

                if (game.game().isInCheckmate(opposingColor)) {
                    connections.broadcast(gameId, null, new NotificationMessage(opposingColor.toString() + " is in checkmate."));
                } else if (game.game().isInCheck(opposingColor)) {
                    connections.broadcast(gameId, null, new NotificationMessage(opposingColor.toString() + " is in check."));
                } else if (game.game().isInStalemate(opposingColor)) {
                    connections.broadcast(gameId, null, new NotificationMessage("Game has reached a stalemate."));
                }
            }

        } catch (DataAccessException e){
            sendErrorMessage(session, "Error: Database error - " + e.getMessage());
        }

    }

    private void leaveGame(Session session, String username, UserGameCommand command) throws IOException{
        int gameId = command.getGameID();
        try {


            GameData game = gameDAO.getGame(gameId);
            if (game == null) {
                sendErrorMessage(session, "Error: Game does not exist.");
                return;
            }


            String whitePlayer = game.whiteUsername() ;
            String blackPlayer = game.blackUsername();

            if (username.equals(whitePlayer)) {
                whitePlayer = null;
            } else if (username.equals(blackPlayer)) {
                blackPlayer = null;
            }

            gameDAO.updateGame(new GameData(gameId, whitePlayer, blackPlayer, game.gameName(), game.game()));
            connections.remove(command.getGameID(), session);
            String message = String.format("%s left the game.", username);
            NotificationMessage notification = new NotificationMessage(message);
            connections.broadcast(gameId, session, notification);
        } catch (DataAccessException e) {
            sendErrorMessage(session, "Error: Database error - " + e.getMessage());
        }
    }

    private void resign(Session session, String username, UserGameCommand command) throws IOException {
        int gameId = command.getGameID();

        try {
            GameData game = gameDAO.getGame(gameId);
            if (gameData == null) {
                sendErrorMessage(session, "Error: Game does not exist.");
                return;
            }
            boolean isPlayer = username.equals(game.whiteUsername()) || username.equals(game.blackUsername());
            if (!isPlayer) {
                sendErrorMessage(session, "Error: Observers cannot resign.");
                return;
            }
            if (gameData.game().isGameOver()) {
                sendErrorMessage(session, "Error: The game is already over.");
                return;
            }
            game.game().setGameOver(true);
            gameDAO.updateGame(game);


            String message = String.format("%s has resigned. The game is over.", username);
            NotificationMessage notification = new NotificationMessage(message);
            connections.broadcast(gameId, session, notification);

        } catch (DataAccessException e) {
            sendErrorMessage(session,"Error: Database error - " + e.getMessage());
        }
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