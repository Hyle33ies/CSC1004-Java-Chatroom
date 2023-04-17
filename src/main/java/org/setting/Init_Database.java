package org.setting;

import static org.setting.Network_setting.DatabaseInitializer.init;

/**
 * User: HP
 * Date: 2023/4/17
 * WELCOME!
 */
public class Init_Database {
    public static void main(String[] args) {
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
