package ui;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.ResponseException;

import java.net.*;
import java.net.http.*;
import java.util.Collection;

public class ServerFacade {
    private final Gson gson;
    private final HttpClient client;
    private final String serverUrl;

    public ServerFacade(String url) {
        this.serverUrl = url;
        this.gson = new Gson();
        this.client = HttpClient.newHttpClient();
    }

    public UserData register(UserData data) throws ResponseException {
        String jsonBody = gson.toJson(data);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return executeRequest(request, UserData.class);
    }

    public UserData login(UserData data) throws ResponseException {
        String jsonBody = gson.toJson(data);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return executeRequest(request, UserData.class);
    }

    public void logout(AuthData data) throws ResponseException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .header("authorization", data.authToken())
                .DELETE()
                .build();

        executeRequest(request, UserData.class);
    }

    public Collection listGames(AuthData data) throws ResponseException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .header("authorization", data.authToken())
                .GET()
                .build();

        return executeRequest(request, Collection.class);
    }


    public void createGame(AuthData authData, GameData gameRequest) throws ResponseException {
        String jsonBody = gson.toJson(gameRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .header("authorization", authData.authToken())
                .header("Content-Type", "application/json") // Required when sending a JSON body
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        executeRequest(request, GameData.class);
    }

    public void joinGame(AuthData authData, GameData gameRequest) throws ResponseException {
        String jsonBody = gson.toJson(gameRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .header("authorization", authData.authToken())
                .header("Content-Type", "application/json") // Required when sending a JSON body
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        executeRequest(request, GameData.class);
    }



    private <T> T executeRequest(HttpRequest request, Class<T> responseClass) throws ResponseException {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                if (response.body() == null || response.body().isEmpty()) {
                    return null;
                }
                return gson.fromJson(response.body(), responseClass);
            } else {
                throw new ResponseException(response.statusCode(), "Error: " + response.body());
            }
        } catch (Exception e) {
            throw new ResponseException(500, "Network error: " + e.getMessage());
        }
    }

}
