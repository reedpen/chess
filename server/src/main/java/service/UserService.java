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

    public static RegisterResult register(RegisterRequest request) throws ResponseException{


        if (request.username() == null || request.email() == null || request.password() == null) {
            throw new ResponseException(400, "Bad Request");
        }

        try {
            if (userDAO.getUserData(request.username()) != null) {
                throw new ResponseException(403, "Already Taken");
            }
            UserData userData = new UserData(request.username(), request.password(), request.email());
            userDAO.createUser(userData);
            String token = createAuthToken();
            authDAO.createAuth(token, request.username());
            return new RegisterResult(request.username(), token);
        }
        catch (DataAccessException  e) {
            throw new ResponseException(500,e.getMessage());
        }
    }


}
