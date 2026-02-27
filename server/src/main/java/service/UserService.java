package service;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import requestsandresults.*;

import java.util.Objects;
import java.util.UUID;



public class UserService {

    private final AuthDAO authDAO;
    private final UserDAO userDAO;
    public UserService(AuthDAO authDAO, UserDAO userDAO){
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }
    public static String createAuthToken(){
        return UUID.randomUUID().toString();
    }
    public UserResult register(RegisterRequest request) throws ResponseException{

        if (request.username() == null || request.username().isEmpty() ||
                request.email() == null || request.email().isEmpty() ||
                request.password() == null || request.password().isEmpty()) {
            throw new ResponseException(400, "Error: bad request");
        }

        try {
            if (userDAO.getUserData(request.username()) != null) {
                throw new ResponseException(403, "Already Taken");
            }
            UserData userData = new UserData(request.username(), request.password(), request.email());
            userDAO.createUser(userData);
            String token = createAuthToken();
            authDAO.createAuth(new AuthData(token, request.username()));
            return new UserResult(request.username(), token);
        }
        catch (DataAccessException e) {
            throw new ResponseException(500,e.getMessage());
        }
    }

    public UserResult login(LoginRequest request) throws ResponseException{
        if (request.username() == null || request.username().isEmpty() ||
                request.password() == null || request.password().isEmpty()) {
            throw new ResponseException(400, "Error: bad request");
        }

        try {
            UserData data = userDAO.getUserData(request.username());
            if (data == null || !Objects.equals(request.password(), data.password())){
                throw new ResponseException(401, "Error: unauthorized");
            }
            String token = createAuthToken();
            authDAO.createAuth(new AuthData(token, data.username()));
            return new UserResult(request.username(), token);
        }
        catch(DataAccessException e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }

    }

    public void logout(String authToken) throws ResponseException {
        try {
            authDAO.deleteAuth(authToken);
        }
        catch(DataAccessException e) {
            throw new ResponseException(401, "Error: unauthorized");
        }

    }



}
