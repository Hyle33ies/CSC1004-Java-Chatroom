package org.client;

import java.sql.SQLException;

public class Client {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        // The entrance of the chatroom, start a login window
        Login login = new Login();
        login.start();
    }
}