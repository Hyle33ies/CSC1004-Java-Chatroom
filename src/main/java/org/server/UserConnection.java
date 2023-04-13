package org.server;

import org.client.tools.User;

import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * User: HP
 * Date: 2023/4/6
 * WELCOME!
 * This class is to maintain a user connection, it includes a user and its network setting
 * Used in Chatting Socket Programming
 */
public class UserConnection implements Serializable {
    public static final long serialVersionUID = 1L;
    private User user;
    private String ipAddress;
    private int port;
    private transient ObjectOutputStream outputStream;

    @Override
    public String toString() {
        return "UserConnection{" +
                "user=" + user +
                ", ipAddress='" + ipAddress + '\'' +
                ", port=" + port +
                '}';
    }

    public UserConnection(User user, String ipAddress, int port) {
        this.user = user;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public UserConnection(User user, String ipAddress, int port, ObjectOutputStream outputStream) {
        this.user = user;
        this.ipAddress = ipAddress;
        this.port = port;
        this.outputStream = outputStream;
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

    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(ObjectOutputStream outputStream) {
        this.outputStream = outputStream;
    }
}

