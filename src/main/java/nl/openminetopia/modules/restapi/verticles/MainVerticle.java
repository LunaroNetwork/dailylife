package nl.openminetopia.modules.restapi.verticles;

import io.vertx.core.Promise;
import io.vertx.ext.web.RoutingContext;
import nl.openminetopia.DailyLife;
import nl.openminetopia.modules.restapi.base.BaseVerticle;

public class MainVerticle extends BaseVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        /*
         * Mount the handler for all incoming requests at every path and HTTP method
         * Validate the API key before handling the request
         */
        router.route().handler(this::validateApiKey);

        // Create the HTTP server
        vertx.createHttpServer()
                // Handle every request using the router
                .requestHandler(router)
                // Start listening
                .listen(
                        DailyLife.getDefaultConfiguration().getRestApiPort(),
                        DailyLife.getDefaultConfiguration().getRestApiHost()
                )
                // Print the port on success
                .onSuccess(server -> {
                    DailyLife.getInstance().getLogger().info("HTTP server started on port " + server.actualPort() + " and host " + DailyLife.getDefaultConfiguration().getRestApiHost());
                    startPromise.complete();
                })
                // Print the problem on failure
                .onFailure(throwable -> {
                    throwable.printStackTrace();
                    startPromise.fail(throwable);
                });
    }

    private void validateApiKey(RoutingContext context) {
        String apiKey = context.request().getHeader("X-API-Key");
        if (apiKey == null || !apiKey.equals(DailyLife.getDefaultConfiguration().getRestApiKey())) {
            context.response().setStatusCode(401).end("Unauthorized request");
        } else {
            context.next();
        }
    }
}
