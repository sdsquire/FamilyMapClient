package com.example.familymapclient;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import Results.*;
import Requests.*;
import Models.*;

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
    public RegisterResult register(RegisterRequest request){ return null; }
    GetPersonsResult getPersons() { return null; }

    GetEventsResult getEvents() { return null; }
}