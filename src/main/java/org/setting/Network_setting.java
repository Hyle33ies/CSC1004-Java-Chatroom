package org.setting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * User: HP
 * Date: 2023/4/15
 * WELCOME!
 */
public class Network_setting {
    public static class Network_Setting{
        private static String Personalized_setting = "jdbc:mysql://localhost:3306/chatroom_users";
        private static String Personalized_username = "root";
        private static String Personalized_password = "@Frankett2004";

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

        private static final String JDBC_URL = Network_Setting.Personalized_setting;
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
            // Create the needed database and tables, if failed, please check if
            // your chatroom_users database exists, if yes, please drop it.
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate("CREATE DATABASE `chatroom_users`");

                    statement.executeUpdate("USE `chatroom_users`");

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
