package org.example;

import java.awt.*;
import java.awt.event.*;

/**
 * User: HP
 * Date: 2023/3/3
 * WELCOME!
 */
public class Login {
    private Frame frame;
    protected Dialog dialog;
    private void ExitConfirm(){
        //Dialog of closing
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((screenSize.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((screenSize.getHeight() - frame.getHeight()) / 2);
        dialog = new Dialog(frame, "Do you really wanna close Chatroom?", true);
        dialog.setBounds(x-50, y-50, 200, 100);
        dialog.setResizable(false);
        dialog.add(new Label("Do you want to exit?"), BorderLayout.NORTH);
        Button ExitYes = new Button("Yes");
        ExitYes.setPreferredSize(new Dimension(100, 50));
        Button ExitNo = new Button("No");
        ExitNo.setPreferredSize(new Dimension(100, 50));
        ExitYes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.dispose();
            }
        });
        ExitNo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dialog.dispose();
            }
        });
        dialog.add(ExitYes, BorderLayout.WEST);
        dialog.add(ExitNo, BorderLayout.EAST);
    }
    public void start() {
        frame = new Frame();
        ExitConfirm();//init confirm information
        //basic setup
        frame.setBackground(Color.gray);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((screenSize.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((screenSize.getHeight() - frame.getHeight()) / 2);
        frame.setBounds(x - 250, y - 150, 500, 300);
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);
        frame.setCursor(new Cursor(Cursor.HAND_CURSOR));
        frame.setTitle("Welcome to Chatroom v1.0");
        frame.setVisible(true);
        //listener
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {

            }

            @Override
            public void windowClosed(WindowEvent e) {
                frame.dispose();
            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
        //layout
        //login and register button
        frame.setLayout(null);
        Label label1 = new Label("Login", Label.CENTER);
        Label label2 = new Label("Register", Label.CENTER);
        label2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                RegisterWindow Register = new RegisterWindow();
                Register.start();
            }
        });
        label1.setBackground(Color.DARK_GRAY);
        label2.setBackground(Color.DARK_GRAY);
        label1.setForeground(Color.WHITE);
        label2.setForeground(Color.WHITE);
        label1.setLocation(100, 200);
        label2.setLocation(300, 200);
        label1.setSize(100, 30);
        label2.setSize(100, 30);
        frame.add(label1);
        frame.add(label2);
        //user and password
        TextField field1 = new TextField();
        field1.setBounds(170, 100, 200, 25);
        frame.add(field1);
        TextField field2 = new TextField();
        field2.setEchoChar('*');
        field2.setBounds(170, 150, 200, 25);
        frame.add(field2);
        Label label3 = new Label("username", Label.CENTER);
        label3.setBounds(100, 100, 80, 25);
        label3.setBackground(Color.lightGray);
        frame.add(label3);
        Label label4 = new Label("password", Label.CENTER);
        label4.setBounds(100, 150, 80, 25);
        label4.setBackground(Color.lightGray);
        frame.add(label4);
        //checkbox
        Checkbox checkbox = new Checkbox("remember me");
        checkbox.setBounds(390, 140, 100, 50);
        frame.add(checkbox);
        //Menubar
        MenuBar bar = new MenuBar();
        Menu menu1 = new Menu("HELP");
        MenuItem menuExit = new MenuItem("Exit");
        menuExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(true);
            }
        });
        MenuItem menuReadMe = new MenuItem("ReadMe");
        menu1.add(menuExit);
        menu1.add(menuReadMe);
        Menu menu2 = new Menu("FUNCTION");
        MenuItem menuRegister = new MenuItem("Register a new user for free");
        menuRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisterWindow Register = new RegisterWindow();
                Register.start();
            }
        });
        MenuItem menuForgetPassword = new MenuItem("I forget my password!");
        menu2.add(menuRegister);
        menu2.add(menuForgetPassword);
        bar.add(menu1);
        bar.add(menu2);
        frame.setMenuBar(bar);
        //PopMenu
        PopupMenu popMenu = new PopupMenu();
        MenuItem menuExitPop = new MenuItem("Exit");
        menuExitPop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(true);
            }
        });
        MenuItem menuReadMePop = new MenuItem("ReadMe");
        popMenu.add(menuExitPop);
        popMenu.add(menuReadMePop);
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3)
                    popMenu.show(frame, e.getX(), e.getY());
            }
        });
        frame.add(popMenu);
        //close
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dialog.setVisible(true);
            }
        });
    }
}
