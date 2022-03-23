package com.example.familymapclient;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.*;
import java.net.*;
import java.net.URL;
import Results.*;
import Requests.*;

public class ServerProxy {
    private String serverHost;
    private String serverPort;

    public ServerProxy(String serverHost, String serverPort){
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public LoginResult login(LoginRequest request) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.connect();

            String req = null;

            new OutputStreamWriter(http.getOutputStream()).write(req);
            http.getOutputStream().close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK)
                System.out.println("Login Successful!");

            else
                System.out.println("Login Unsuccessful");



        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; }
    public RegisterResult register(RegisterRequest req){
        try {
            // PREPARE URL //
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.connect();

            // SUBMIT REGISTER REQUEST //
            OutputStreamWriter reqBody = new OutputStreamWriter(http.getOutputStream());
            new Gson().toJson(req, reqBody);
            reqBody.close();

            // RETURN REGISTER RESULT //
            Reader resultBody = new InputStreamReader(http.getInputStream());
            RegisterResult result = new Gson().fromJson(resultBody, RegisterResult.class);
            return result;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    GetPersonsResult getPersons() { return null; }

    GetEventsResult getEvents() { return null; }
}