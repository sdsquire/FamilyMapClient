package com.example.familymapclient.Tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.familymapclient.DataCache;

public class FillDataTask extends Task {
    private final String authtoken;

    public FillDataTask(Handler messageHandler, String serverHost, String serverProxy, String authtoken) {
        super(messageHandler, serverHost, serverProxy);
        this.authtoken = authtoken;
    }

    @Override
    public void run() {
        boolean success = proxy.getPersons(authtoken);
        if (success)
            success = proxy.getEvents(authtoken);

        Message message = Message.obtain();
        Bundle messageBundle = new Bundle();
        messageBundle.putBoolean("success", success);
        message.setData(messageBundle);

        messageHandler.sendMessage(message);
    }
}