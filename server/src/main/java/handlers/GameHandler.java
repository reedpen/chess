package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import requestsandresults.*;
import service.GameService;
import service.ResponseException;


public class GameHandler {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public GameHandler(GameService service) {
        this.gameService = service;
    }

    public void listGames(Context ctx) throws ResponseException {
        String token = ctx.header("authorization");

        ListGamesResult result = gameService.listGames(token);

        ctx.status(200);
        ctx.result(gson.toJson(result));
    }
    public void createGame(Context ctx) throws ResponseException {
        String token = ctx.header("authorization");
        CreateGameRequest req = gson.fromJson(ctx.body(), CreateGameRequest.class);
        CreateGameResult result = gameService.createGame(token, req);

        ctx.status(200);
        ctx.result(gson.toJson(result));
    }
    public void joinGame(Context ctx) throws ResponseException {
        String token = ctx.header("authorization");
        JoinGameRequest req = gson.fromJson(ctx.body(), JoinGameRequest.class);
        gameService.joinGame(token, req);

        ctx.status(200);
        ctx.result("{}");
    }


}
