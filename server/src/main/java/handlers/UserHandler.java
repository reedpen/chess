package handlers;

import com.google.gson.Gson;

import io.javalin.http.Context;
import requestsandresults.LoginRequest;
import requestsandresults.RegisterRequest;
import requestsandresults.UserResult;
import service.ResponseException;
import service.UserService;


public class UserHandler {
    private final UserService userService;
    private final Gson gson = new Gson();

    public UserHandler(UserService service) {
        this.userService = service;
    }

    public void register(Context ctx) throws ResponseException {
        RegisterRequest registerRequest = gson.fromJson(ctx.body(), RegisterRequest.class);

        UserResult result = userService.register(registerRequest);

        ctx.status(200);
        ctx.result(gson.toJson(result));

    }

    public void login(Context ctx) throws ResponseException {
        LoginRequest loginRequest = gson.fromJson(ctx.body(), LoginRequest.class);

        UserResult result = userService.login(loginRequest);
        ctx.status(200);
        ctx.result(gson.toJson(result));
    }

    public void logout(Context ctx) throws ResponseException {
        String token = ctx.header("authorization");
        userService.logout(token);
        ctx.status(200);
        ctx.result("{}");
    }


}
