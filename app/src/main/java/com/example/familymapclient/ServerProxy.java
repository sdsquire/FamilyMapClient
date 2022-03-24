package com.example.familymapclient;
import com.google.gson.Gson;
import java.io.*;
import java.net.*;

import Models.EventModel;
import Models.PersonModel;
import Results.*;
import Requests.*;

public class ServerProxy {
    private final String serverHost;
    private final String serverPort;

    public ServerProxy(String serverHost, String serverPort){
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public String getServerHost() { return serverHost; }
    public String getServerPort() { return serverPort; }

    public LoginResult login(LoginRequest req) {
        try {
            // PREPARE URL //
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");
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
            return new Gson().fromJson(resultBody, LoginResult.class);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
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
            return new Gson().fromJson(resultBody, RegisterResult.class);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean getPersons(String authtoken) {
        try {
            // PREPARE URL //
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", authtoken);
            http.connect();
            // WRITE RESULT TO DATACACHE //
            DataCache dataCache = DataCache.getInstance();
            Reader resultBody = new InputStreamReader(http.getInputStream());
            GetPersonsResult result = new Gson().fromJson(resultBody, GetPersonsResult.class);
            for (PersonModel person : result.getData())
                dataCache.addPerson(person);

            return result.isSuccess();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean getEvents(String authtoken) {
        try {
            // PREPARE URL //
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/event");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", authtoken);
            http.connect();
            // WRITE RESULT TO DATACACHE //
            DataCache dataCache = DataCache.getInstance();
            Reader resultBody = new InputStreamReader(http.getInputStream());
            GetEventsResult result = new Gson().fromJson(resultBody, GetEventsResult.class);
            for (EventModel event: result.getData())
                dataCache.addEvent(event);

            return result.isSuccess();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }
}