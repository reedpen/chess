package service;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import requestsandresults.*;

import java.util.Objects;

import static service.AuthService.createAuthToken;

public class UserService {

    private final AuthDAO authDAO;
    private final UserDAO userDAO;
    public UserService(AuthDAO authDAO, UserDAO userDAO){
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }


    private boolean isRegRequestBad(RegisterRequest request) {
        return request.username() == null || request.username().isEmpty() ||
                request.email() == null || request.email().isEmpty() ||
                request.password() == null || request.password().isEmpty();
    }
    private boolean isLoginRequestBad(LoginRequest request) {
        return request.username() == null || request.username().isEmpty() ||
                request.password() == null || request.password().isEmpty();
    }

    public UserResult register(RegisterRequest request) throws ResponseException{


        if (isRegRequestBad(request)) {
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
        if (isLoginRequestBad(request)) {
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



}
