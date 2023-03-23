package org.example.tools;

import java.io.Serial;
import java.io.Serializable;

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
