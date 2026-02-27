package handlers;
import io.javalin.http.Context;
import service.ClearService;

public class ClearHandler {

    private final ClearService clearService;
    public ClearHandler(ClearService service) {
        this.clearService = service;
    }

    public void clear(Context ctx) {
        clearService.clear();
        ctx.status(200);
        ctx.result("{}");
    }
}
