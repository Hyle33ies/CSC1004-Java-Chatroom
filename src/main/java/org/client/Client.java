package org.client;

import java.sql.SQLException;

public class Client {
    public static void main(String[] args) throws SQLException {
        Login login = new Login();
        login.start();
    }
}