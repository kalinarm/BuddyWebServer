package com.bfr.BuddyWebServer;

public interface WebhookCallback {
    void onWebhookReceived(String route, String data);
}