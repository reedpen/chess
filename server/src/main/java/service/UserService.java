package service;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Response;
import requestsandresults.*;

import static service.AuthService.createAuthToken;

public class UserService {

    static AuthDAO authDAO;
    static UserDAO userDAO;
    public UserService(AuthDAO authDAO, UserDAO userDAO){
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public RegisterResult register(RegisterRequest request) throws ResponseException{


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
            return new RegisterResult(request.username(), token);
        }
        catch (DataAccessException  e) {
            throw new ResponseException(500,e.getMessage());
        }
    }


}
