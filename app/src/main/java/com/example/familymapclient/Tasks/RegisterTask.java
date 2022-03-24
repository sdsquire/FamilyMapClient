package com.example.familymapclient.Tasks;

import android.os.Handler;
import android.os.Message;

import com.example.familymapclient.DataCache;

import Requests.RegisterRequest;
import Results.RegisterResult;

public class RegisterTask extends Task{
    private final RegisterRequest req;
    public RegisterTask(Handler messageHandler, RegisterRequest req, String serverHost, String serverPort){
        super(messageHandler, serverHost, serverPort);
        this.req = req;
    }
    @Override
    public void run() {
        RegisterResult result = proxy.register(req);
        DataCache.setCurrentUser(result.getPersonID());
        fillData(result.getAuthtoken());
    }
}