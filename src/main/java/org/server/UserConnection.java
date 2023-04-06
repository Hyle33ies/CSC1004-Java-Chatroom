package org.server;

import org.client.tools.User;

/**
 * User: HP
 * Date: 2023/4/6
 * WELCOME!
 */
public class UserConnection {
    private User user;
    private String ipAddress;
    private int port;

    public UserConnection(User user, String ipAddress, int port) {
        this.user = user;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}

