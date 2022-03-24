package com.example.familymapclient.Tasks;

import android.os.Handler;
import android.os.Message;

import com.example.familymapclient.DataCache;

import Requests.LoginRequest;
import Results.LoginResult;

public class LoginTask extends Task{
    private final LoginRequest req;
    public LoginTask(Handler messageHandler, LoginRequest req, String serverHost, String serverPort){
        super(messageHandler, serverHost, serverPort);
        this.req = req;
    }
    @Override
    public void run() {
        LoginResult result = proxy.login(req);
        DataCache.setCurrentUser(result.getPersonID());
        fillData(result.getAuthtoken());
    }
}
