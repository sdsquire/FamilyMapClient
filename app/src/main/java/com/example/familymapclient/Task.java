package com.example.familymapclient;

import android.os.*;
import com.example.familymapclient.*;
import java.util.concurrent.Executors;
import Requests.*;
import Results.LoginResult;
import Results.RegisterResult;
import Results.Result;

public class Task implements Runnable {
    protected final Handler messageHandler;
    protected final ServerProxy proxy;
    private final LoginRequest loginRequest;
    private final RegisterRequest registerRequest;
    protected String authtoken;

    public Task(Handler messageHandler, String serverHost, String serverPort, LoginRequest loginRequest) {
        this.messageHandler = messageHandler;
        this.proxy = new ServerProxy(serverHost, serverPort);
        this.loginRequest = loginRequest;
        this.registerRequest = null;
        this.authtoken = null;
    }

    public Task(Handler messageHandler, String serverHost, String serverPort, RegisterRequest registerRequest) {
        this.messageHandler = messageHandler;
        this.proxy = new ServerProxy(serverHost, serverPort);
        this.registerRequest= registerRequest;
        this.loginRequest = null;
        this.authtoken = null;
    }

    public Task(Handler messageHandler, String serverHost, String serverPort, String authtoken) {
        this.messageHandler = messageHandler;
        this.proxy = new ServerProxy(serverHost, serverPort);
        this.registerRequest= null;
        this.loginRequest = null;
        this.authtoken = authtoken;
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

        // SEND MESSAGE //
        Message message = Message.obtain();
        Bundle messageBundle = new Bundle();
        messageBundle.putBoolean("success", success);
        message.setData(messageBundle);
        messageHandler.sendMessage(message);
    }
}