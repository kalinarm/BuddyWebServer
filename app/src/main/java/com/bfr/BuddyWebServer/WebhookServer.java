package com.bfr.BuddyWebServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import fi.iki.elonen.NanoHTTPD;




public class WebhookServer extends NanoHTTPD {

    private WebhookCallback callback;

    public WebhookServer(int port, WebhookCallback callback) {
        super(port);
        this.callback = callback;
    }

    @Override
    public Response serve(IHTTPSession session) {
        // Handle incoming webhook request here
        Method method = session.getMethod();
        String uri = session.getUri();
        String body = ""; // Initialize with request body
        String joinedMessage = "";

        // Check if the request method is POST
        if (Method.POST.equals(method)) {
            try {
                // Read the input stream to get the request body
                session.parseBody(new HashMap<>());
                joinedMessage = session.getQueryParameterString();
                if (joinedMessage.startsWith("=")) {
                    // Supprimez le '=' en le remplaçant par une chaîne vide ""
                    joinedMessage = joinedMessage.substring(1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ResponseException e) {
                e.printStackTrace();
            }
        }else {
            List<String> messages = session.getParameters().get("str");
            joinedMessage = String.join(", ", messages); // Change ", " to your desired delimiter
        }

        // Call the callback function with the received data
        if (callback != null) {
            callback.onWebhookReceived(uri, joinedMessage);
        }


        // Process the request and send a response
        String response = "Received " + method + " request to " + uri + "\n" + body;

        // You can customize the response as needed
        return newFixedLengthResponse(response);
    }
}
