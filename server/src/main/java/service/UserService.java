package service;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import requestsandresults.*;

import java.util.Objects;

import static service.AuthService.createAuthToken;

public class UserService {

    static AuthDAO authDAO;
    static UserDAO userDAO;
    public UserService(AuthDAO authDAO, UserDAO userDAO){
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }


    private boolean isRequestBad(UserRequest request) {
        return request.username() == null || request.username().isEmpty() ||
                request.email() == null || request.email().isEmpty() ||
                request.password() == null || request.password().isEmpty();
    }

    private boolean isGoodPassword()

    public UserResult register(UserRequest request) throws ResponseException{


        if (isRequestBad(request)) {
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

    public UserResult login(UserRequest request) throws ResponseException{
        if (isRequestBad(request)) {
            throw new ResponseException(400, "Error: bad request");
        }

        try {
            userDAO.getUserData(request.username());
            if (Objects.equals(request.password(), userDAO.getUserData(request.username()).password())){
                UserData data = userDAO.getUserData(request.username());
                String token = createAuthToken();
                authDAO.createAuth(new AuthData(token, data.username()));
                return new UserResult(request.username(), token);
            } else {
                throw new ResponseException(401, "Error: bad username or password");
            }

        }
        catch(DataAccessException e) {
            throw new ResponseException(401, "Error: bad username or password");
        }

    }


}
