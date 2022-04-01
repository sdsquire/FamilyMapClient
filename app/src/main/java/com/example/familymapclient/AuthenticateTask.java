package com.example.familymapclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import Requests.LoginRequest;
import Requests.RegisterRequest;
import Results.Result;

public class AuthenticateTask implements Runnable {
    protected final Handler messageHandler;
    protected final ServerProxy proxy;
    private final LoginRequest loginRequest;
    private final RegisterRequest registerRequest;
    protected String authtoken;

    public AuthenticateTask(Handler messageHandler, String serverHost, String serverPort, LoginRequest loginRequest) {
        this.messageHandler = messageHandler;
        this.proxy = new ServerProxy(serverHost, serverPort);
        this.loginRequest = loginRequest;
        this.registerRequest = null;
        this.authtoken = null;
    }

    public AuthenticateTask(Handler messageHandler, String serverHost, String serverPort, RegisterRequest registerRequest) {
        this.messageHandler = messageHandler;
        this.proxy = new ServerProxy(serverHost, serverPort);
        this.registerRequest= registerRequest;
        this.loginRequest = null;
        this.authtoken = null;
    }

    public void run() {
        // AUTHENTICATE USER //
        Result result = loginRequest != null ? proxy.login(loginRequest) : proxy.register(registerRequest);
        if (!result.isSuccess()) {
            messageHandler.sendMessage(Message.obtain());
            return;
        }
        DataCache.setCurrentUser(result.getPersonID());
        authtoken = result.getAuthtoken();

        // FILL DATA //
        boolean success = proxy.getPersons(authtoken) && proxy.getEvents(authtoken);
        assert DataCache.getInstance().getPeople().size() != 0;
        assert DataCache.getInstance().getEvents().size() != 0;
//        this.CacheUser();

        // SEND MESSAGE //
        Message message = Message.obtain();
        Bundle messageBundle = new Bundle();
        messageBundle.putBoolean("success", success);
        message.setData(messageBundle);
        messageHandler.sendMessage(message);
    }
}