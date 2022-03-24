package com.example.familymapclient.Tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.example.familymapclient.DataCache;
import com.example.familymapclient.ServerProxy;

import java.util.concurrent.Executors;

public class Task implements Runnable {
    protected final Handler messageHandler;
    protected final ServerProxy proxy;

    protected Task(Handler messageHandler, String serverHost, String serverPort) {
        this.messageHandler = messageHandler;
        this.proxy = new ServerProxy(serverHost, serverPort);
    }
    public void run() {} //Implemented when inherited
    protected void sendMessage(boolean success) {
        // GET MESSAGE //
        Message message = Message.obtain();
        // SET MESSAGE DATA //
        Bundle messageBundle = new Bundle();
        messageBundle.putBoolean("success", success);
        message.setData(messageBundle);
        // SEND MESSAGE TO UI THREAD //
        messageHandler.sendMessage(message);
    }

    protected void fillData(String authtoken) {
        FillDataTask task = new FillDataTask(messageHandler, proxy.getServerHost(), proxy.getServerPort(), authtoken);
        Executors.newSingleThreadExecutor().submit(task);
        assert DataCache.getInstance().getPeople().size() != 0;
        assert DataCache.getInstance().getEvents().size() != 0;
    }
}