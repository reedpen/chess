package service;

import java.util.UUID;

public class AuthService {
    public static String createAuthToken(){
        return UUID.randomUUID().toString();
    }

}
