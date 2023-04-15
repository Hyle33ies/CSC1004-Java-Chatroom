package org.client;

import java.sql.SQLException;

public class Client {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Login login = new Login();
        login.start();
    }
}