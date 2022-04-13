package com.example.familymapclient;

public class LoginInfo {
    private final String serverHost;
    private final String serverPort;
    private final String username;
    private final String password;

    public LoginInfo(String serverHost, String serverPort, String username, String password) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.username = username;
        this.password = password;
    }

    public String getServerHost() { return serverHost; }
    public String getServerPort() { return serverPort; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
