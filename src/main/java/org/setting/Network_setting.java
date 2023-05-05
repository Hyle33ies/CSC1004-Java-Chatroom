package org.setting;

import java.sql.*;

/**
 * User: HP
 * Date: 2023/4/15
 * WELCOME!
 */
@SuppressWarnings("all")
public class Network_setting {
    public static class DatabaseInitializer {
        private static final ConfigReader configReader = ConfigReader.getInstance();
        private static String JDBC_URL = configReader.getProperty("jdbc.url");
        private static String DATABASE_NAME = configReader.getProperty("database.name");
        private static String USERNAME = configReader.getProperty("username");
        private static String PASSWORD = configReader.getProperty("password");

        public static String getJdbcUrl() {
            return JDBC_URL;
        }

        public static String getDatabaseName() {
            return DATABASE_NAME;
        }

        public static String getUSERNAME() {
            return USERNAME;
        }

        public static String getPASSWORD() {
            return PASSWORD;
        }


        public static String ToString() {
            return "JDBC_URL: " + JDBC_URL + "\n" +
                    "DATABASE_NAME: " + DATABASE_NAME + "\n" +
                    "USERNAME: " + USERNAME + "\n" +
                    "PASSWORD: " + PASSWORD + "\n";
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
