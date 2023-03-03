package org.example;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * User: HP
 * Date: 2023/3/3
 * WELCOME!
 */
public class RegisterWindow {
    Frame frame;
    public void start(){
        frame = new Frame();
        frame.setBackground(Color.gray);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((screenSize.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((screenSize.getHeight() - frame.getHeight()) / 2);
        frame.setBounds(x - 200, y - 100, 500, 300);
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);
        frame.setCursor(new Cursor(Cursor.HAND_CURSOR));
        frame.setTitle("Register for new");
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
            }
        });
        //information fill-up
    }
}
