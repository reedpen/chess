package client;

import model.AuthData;
import org.junit.jupiter.api.*;
import requestsandresults.*;
import server.Server;
import chess.ResponseException;
import ui.ServerFacade;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static String serverUrl;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        serverUrl = "http://localhost:" + port;
        facade = new ServerFacade(serverUrl);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clear() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/db"))
                .DELETE()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }


    @Test
    public void registerPositive() throws Exception {
        RegisterRequest req = new RegisterRequest("player1", "password", "p1@email.com");
        UserResult authData = facade.register(req);

        assertNotNull(authData.authToken());
        assertTrue(authData.authToken().length() > 10);
        assertEquals("player1", authData.username());
    }

    @Test
    public void registerNegativeDuplicate() throws Exception {
        RegisterRequest req = new RegisterRequest("player1", "password", "p1@email.com");
        facade.register(req); // First registration succeeds

        assertThrows(ResponseException.class, () -> facade.register(req));
    }


    @Test
    public void loginPositive() throws Exception {
        facade.register(new RegisterRequest("player1", "password", "p1@email.com"));

        LoginRequest req = new LoginRequest("player1", "password");
        UserResult authData = facade.login(req);

        assertNotNull(authData.authToken());
        assertEquals("player1", authData.username());
    }

    @Test
    public void loginNegativeBadPassword() throws Exception {
        facade.register(new RegisterRequest("player1", "password", "p1@email.com"));

        LoginRequest req = new LoginRequest("player1", "wrongpassword");
        assertThrows(ResponseException.class, () -> facade.login(req));
    }


    @Test
    public void logoutPositive() throws Exception {
        UserResult result = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        AuthData authData = new AuthData(result.authToken(), result.username());

        assertDoesNotThrow(() -> facade.logout(authData));

        assertThrows(ResponseException.class, () -> facade.listGames(authData));
    }

    @Test
    public void logoutNegativeBadToken() {
        AuthData fakeAuth = new AuthData("fakeToken", "fakeUser");
        assertThrows(ResponseException.class, () -> facade.logout(fakeAuth));
    }


    @Test
    public void createGamePositive() throws Exception {
        UserResult result = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        AuthData authData = new AuthData(result.authToken(), result.username());

        CreateGameResult gameResult = facade.createGame(authData, new CreateGameRequest("MyGame"));
        assertTrue(gameResult.gameID() > 0);
    }

    @Test
    public void createGameNegativeBadToken() {
        AuthData fakeAuth = new AuthData("fakeToken", "fakeUser");
        assertThrows(ResponseException.class, () -> facade.createGame(fakeAuth, new CreateGameRequest("MyGame")));
    }


    @Test
    public void listGamesPositive() throws Exception {
        UserResult result = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        AuthData authData = new AuthData(result.authToken(), result.username());

        facade.createGame(authData, new CreateGameRequest("Game1"));
        facade.createGame(authData, new CreateGameRequest("Game2"));

        ListGamesResult listResult = facade.listGames(authData);
        assertNotNull(listResult.games());
        assertEquals(2, listResult.games().size());
    }

    @Test
    public void listGamesNegativeBadToken() {
        AuthData fakeAuth = new AuthData("fakeToken", "fakeUser");
        assertThrows(ResponseException.class, () -> facade.listGames(fakeAuth));
    }


    @Test
    public void joinGamePositive() throws Exception {
        UserResult result = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        AuthData authData = new AuthData(result.authToken(), result.username());

        CreateGameResult gameResult = facade.createGame(authData, new CreateGameRequest("MyGame"));

        JoinGameRequest joinReq = new JoinGameRequest("WHITE", gameResult.gameID());
        assertDoesNotThrow(() -> facade.joinGame(authData, joinReq));
    }

    @Test
    public void joinGameNegativeInvalidId() throws Exception {
        UserResult result = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        AuthData authData = new AuthData(result.authToken(), result.username());

        JoinGameRequest joinReq = new JoinGameRequest("WHITE", 9999);
        assertThrows(ResponseException.class, () -> facade.joinGame(authData, joinReq));
    }
}