package server;

import dataaccess.*;
import handlers.ClearHandler;
import handlers.GameHandler;
import handlers.UserHandler;
import io.javalin.*;
import service.ClearService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {

        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        UserService userService = new UserService(authDAO, userDAO);
        GameService gameService = new GameService(authDAO, gameDAO);
        ClearService clearService = new ClearService(authDAO, userDAO, gameDAO);

        UserHandler userHandler = new UserHandler(userService);
        GameHandler gameHandler = new GameHandler(gameService);
        ClearHandler clearHandler = new ClearHandler(clearService);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.post("/user", userHandler::register);
        javalin.post("/session", userHandler::login);
        javalin.delete("/session", userHandler::logout);
        javalin.get("/game", gameHandler::listGames);
        javalin.post("/game", gameHandler::createGame);
        javalin.put("/game", gameHandler::joinGame);
        javalin.delete("/db", clearHandler::clear);

        // Register your endpoints and exception handlers here.
        javalin.exception(service.ResponseException.class, (ex, ctx) -> {
            ctx.status(ex.statusCode());
            ctx.result(new com.google.gson.Gson().toJson(java.util.Map.of("message", ex.getMessage())));
        });

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);

        return javalin.port();

    }

    public void stop() {
        javalin.stop();
    }
}
