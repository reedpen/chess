package handlers;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.ClearService;

public class ClearHandler {

    private final ClearService clearService;
    public ClearHandler(ClearService service) {
        this.clearService = service;
    }

    public void clear(Context ctx) throws DataAccessException {
        try {
            clearService.clear();
            ctx.status(200).result("{}");
        } catch (RuntimeException e) { // Catch the exception thrown by your service
            ctx.status(500);
            ctx.result(String.format("{ \"message\": \"%s\" }", e.getMessage()));
        }

    }
}
