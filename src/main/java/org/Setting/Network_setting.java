package org.Setting;

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

        public void setPersonalized_setting(String personalized_setting) {
            Personalized_setting = personalized_setting;
        }

        public String getPersonalized_username() {
            return Personalized_username;
        }

        public void setPersonalized_username(String personalized_username) {
            Personalized_username = personalized_username;
        }

        public String getPersonalized_password() {
            return Personalized_password;
        }

        public void setPersonalized_password(String personalized_password) {
            Personalized_password = personalized_password;
        }
    }

}
