package org.client.tools;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * User: HP
 * Date: 2023/3/23
 * WELCOME!
 */
public class User implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        private String username;
        private String passwd;
        private int age;
        private String sex;
        private String email;
        private String country = "";
        private String city = "";
        private String intro = "";
    public void updateUser() throws SQLException, ClassNotFoundException {
        if (!"admin".equalsIgnoreCase(getUsername())) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatroom_users", "root", "@Frankett2004")) {
                String query = "UPDATE users SET age = ?, sex = ?, country = ?, city = ?, introduction = ? WHERE username = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setInt(1, getAge());
                    statement.setString(2, getSex());
                    statement.setString(3, getCountry());
                    statement.setString(4, getCity());
                    statement.setString(5, getIntro());
                    statement.setString(6, getUsername());
                    statement.executeUpdate();
                }
            }
        }
    }

    public User() {
    }

    public User(String username, String passwd) {
        this.username = username;
        this.passwd = passwd;
        this.age = -1;
        this.sex = "prefer not to say";
        this.email = "12345@163.com";
    }

    public User(String username, String passwd, int age, String sex, String email, String country, String city, String intro) {
            this.username = username;
            this.passwd = passwd;
            this.age = age;
            this.sex = sex;
            this.email = email;
            this.country = country;
            this.city = city;
            this.intro = intro;
        }

        public User(String username, String passwd, int age, String sex, String email) {
            this.username = username;
            this.passwd = passwd;
            this.age = age;
            this.sex = sex;
            this.email = email;
        }

        @Override
        public String toString() {
            return "User{" +
                    "username='" + username + '\'' +
                    ", passwd='" + passwd + '\'' +
                    ", age=" + age +
                    ", sex=" + sex +
                    ", email='" + email + '\'' +
                    ", country='" + country + '\'' +
                    ", city='" + city + '\'' +
                    ", intro='" + intro + '\'' +
                    '}';
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPasswd() {
            return passwd;
        }

        public void setPasswd(String passwd) {
            this.passwd = passwd;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }

}
