package org.setting;

import java.sql.*;

/**
 * User: HP
 * Date: 2023/4/15
 * WELCOME!
 */
public class Network_setting {
    public static class Network_Setting{
        private static String Personalized_setting = "jdbc:mysql://localhost:3306/chatroom_users";
        private static String Personalized_username = "root";
        private static String Personalized_password = "root";

        @Override
        public String toString() {
            return "Network_Setting{" +
                    "Personalized_setting='" + Personalized_setting + '\'' +
                    ", Personalized_username='" + Personalized_username + '\'' +
                    ", Personalized_password='" + Personalized_password + '\'' +
                    '}';
        }

        public Network_Setting(String personalized_setting, String personalized_username, String personalized_password) {
            Personalized_setting = personalized_setting;
            Personalized_username = personalized_username;
            Personalized_password = personalized_password;
        }

        public Network_Setting() {
        }

        public String getPersonalized_setting() {
            return Personalized_setting;
        }

        public String getPersonalized_username() {
            return Personalized_username;
        }


        public String getPersonalized_password() {
            return Personalized_password;
        }

    }

    public static class DatabaseInitializer {

        private static final String JDBC_URL = "jdbc:mysql://localhost:3306";
        private static final String DATABASE_NAME = "chatroom_users";
        private static final String USERNAME = Network_Setting.Personalized_username;
        private static final String PASSWORD = Network_Setting.Personalized_password;

        public static void main(String[] args) {
            try {
                init();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static void init() throws SQLException {
            try (Connection serverConnection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
                try (Statement serverStatement = serverConnection.createStatement()) {
                    ResultSet resultSet = serverStatement.executeQuery("SHOW DATABASES LIKE '" + DATABASE_NAME + "'");
                    if (resultSet.next()) {
                        // If the "chatroom_users" database exists, delete it along with all its data
                        serverStatement.executeUpdate("DROP DATABASE `" + DATABASE_NAME + "`");
                    }
                    serverStatement.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + DATABASE_NAME + "`");
                }
            }

            // Connect to the newly created database
            try (Connection connection = DriverManager.getConnection(JDBC_URL + "/" + DATABASE_NAME, USERNAME, PASSWORD)) {
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate("USE `" + DATABASE_NAME + "`");

                    statement.executeUpdate("CREATE TABLE `users` (" +
                            "  `id` int NOT NULL AUTO_INCREMENT," +
                            "  `username` varchar(255) NOT NULL," +
                            "  `password` varchar(255) NOT NULL," +
                            "  `age` int DEFAULT NULL," +
                            "  `sex` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL," +
                            "  `email` varchar(255) NOT NULL," +
                            "  `country` varchar(255) DEFAULT NULL," +
                            "  `city` varchar(255) DEFAULT NULL," +
                            "  `introduction` text," +
                            "  PRIMARY KEY (`id`)," +
                            "  UNIQUE KEY `username` (`username`)," +
                            "  UNIQUE KEY `email` (`email`)" +
                            ")");

                    statement.executeUpdate("CREATE TABLE `message_history` (" +
                            "  `id` int NOT NULL AUTO_INCREMENT," +
                            "  `sender` varchar(255) NOT NULL," +
                            "  `getter` varchar(255) NOT NULL," +
                            "  `content` text NOT NULL," +
                            "  `send_time` datetime NOT NULL," +
                            "  PRIMARY KEY (`id`)" +
                            ")");

                    statement.executeUpdate("CREATE TABLE `qanda` (" +
                            "  `id` int NOT NULL AUTO_INCREMENT," +
                            "  `username` varchar(30) NOT NULL," +
                            "  `email` text NOT NULL," +
                            "  `question1` text," +
                            "  `answer1` text," +
                            "  `question2` text," +
                            "  `answer2` text," +
                            "  PRIMARY KEY (`id`)," +
                            "  UNIQUE KEY `username` (`username`)," +
                            "  UNIQUE KEY `email` (`email`(255))" +
                            ")");

                    statement.executeUpdate("CREATE TABLE `user_remember` (" +
                            "  `username` varchar(100) DEFAULT NULL," +
                            "  `password` varchar(100) DEFAULT NULL" +
                            ")");
                }
            }
        }
    }
}
