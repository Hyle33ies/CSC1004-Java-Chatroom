package org.client;

import org.client.tools.MessageType;
import org.client.tools.User;
import org.client.tools.Message;

import java.awt.Checkbox;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Paths;
import java.sql.*;

/**
 * User: HP
 * Date: 2023/3/3
 * WELCOME!
 */
public class Login {
    private Frame frame;
    protected JDialog dialog;
    Connection connection;

    public Login() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatroom_users", "root", "@Frankett2004");
    }

    private void ExitConfirm() {
        //Dialog of closing
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((screenSize.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((screenSize.getHeight() - frame.getHeight()) / 2);
        dialog = new JDialog(frame, "Close Chatroom?", true);
        dialog.setBounds(x - 50, y - 50, 200, 100);
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
                System.exit(0);
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
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                frame.dispose();
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
                RegisterWindow Register;
                try {
                    Register = new RegisterWindow();
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
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
        try {
            boolean rememberedUserExists = loadRememberedUser(field1, field2);
            checkbox.setState(rememberedUserExists);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        //Menubar
        MenuBar bar = new MenuBar();
        Menu menu1 = new Menu("HELP");
        MenuItem menuExit = new MenuItem("Exit");
        menuExit.addActionListener(e -> dialog.setVisible(true));
        MenuItem menuReadMe = new MenuItem("ReadMe");
        menuReadMe.addActionListener(e -> {
            try {
                File readmeFile = Paths.get("resources", "readme.md").toFile();
                Desktop.getDesktop().open(readmeFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        menu1.add(menuExit);
        menu1.add(menuReadMe);
        Menu menu2 = new Menu("FUNCTION");
        MenuItem menuRegister = new MenuItem("Register a new user for free");
        menuRegister.addActionListener(e -> {
            RegisterWindow Register;
            try {
                Register = new RegisterWindow();
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            Register.start();
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
        menuExitPop.addActionListener(e -> dialog.setVisible(true));
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
        menuReadMePop.addActionListener(e -> {
            try {
                File readmeFile = Paths.get("resources", "readme.md").toFile();
                Desktop.getDesktop().open(readmeFile);
            } catch (IOException ex) {
                ex.printStackTrace();
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

        //part2:login verification
        label1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Verify the user's password!
                String s1 = field1.getText(); // Username
                String s2 = field2.getText(); // Password
                Message messageLogin = new Message();
                messageLogin.setSender(s1);
                messageLogin.setMesType(MessageType.MESSAGE_LOGIN_ATTEMPT);
                messageLogin.setContent(s2);
                //send message to server
                try (Socket socket = new Socket("localhost", 8889)) {
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    oos.writeObject(messageLogin);
                    oos.flush();
                    //receive message from server
                    Message messageLogin1 = (Message) ois.readObject();
                    if (messageLogin1.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCESSFUL)) {
                        User loginUser = messageLogin1.getUser();
                        Chatroom chatroom = new Chatroom(loginUser);
                        chatroom.start();
                        frame.setVisible(false);
                        try {
                            if (checkbox.getState()) {
                                deleteRememberedUser();
                                saveRememberedUser(s1, s2);
                            } else {
                                deleteRememberedUser();
                            }
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }else{
                        new ErrorDialog(frame, "Wrong username or password!");
                    }
                } catch (IOException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
    static class ErrorDialog extends JDialog {

        public ErrorDialog(Frame parent, String message) {
            super(parent, "Error", true);
            JPanel panel = new JPanel();
            JLabel label = new JLabel(message);
            panel.add(label);

            JButton button = new JButton("Get it!");
            button.addActionListener(e -> dispose());
            panel.add(button);

            getContentPane().add(panel);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }

    // Add these methods to the Login class
    private void saveRememberedUser(String username, String password) throws SQLException {
        String sql = "INSERT INTO user_remember (username, password) VALUES (?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, username);
        statement.setString(2, password);
        statement.executeUpdate();
        System.out.println("Saved user");
    }

    private void deleteRememberedUser() throws SQLException {
        String sql = "DELETE FROM user_remember";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
        System.out.println("Deleted user");
    }

    private boolean loadRememberedUser(TextField field1,TextField field2) throws SQLException {
        String sql = "SELECT * FROM user_remember";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            field1.setText(result.getString("username"));
            field2.setText(result.getString("password"));
            System.out.println("Loaded user");
            return true;
        }
        System.out.println("No remembered user");
        return false;
    }

}
