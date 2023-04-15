package org.client;

import org.Setting.Network_setting.Network_Setting;
import org.client.tools.MessageType;
import org.client.tools.User;
import org.client.tools.Message;

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
import java.util.ArrayList;
import java.util.Random;

public class Login {
    protected JFrame frame;
    private JDialog dialog;
    private final Connection connection;

    public Login() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Network_Setting ns = new Network_Setting();
        System.out.println("Network Setting: " + ns);
        connection = DriverManager.getConnection(ns.getPersonalized_setting(), ns.getPersonalized_username(), ns.getPersonalized_password());
    }

    public void start() {
        frame = new JFrame("Welcome to Chatroom v1.1.15");

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ExitConfirmDialog.showExitConfirmDialog(frame);
            }
        });

        frame.setSize(500, 300);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);

        initComponents();

        frame.setVisible(true);
    }

    protected void initComponents() {
        frame.setLayout(new GridBagLayout());
        AnimatedBackgroundPanel backgroundPanel = new AnimatedBackgroundPanel();
        backgroundPanel.setOpaque(false);
        frame.setContentPane(backgroundPanel);

        // Create a transparent panel for login components
        JPanel loginPanel = new JPanel();
        loginPanel.setOpaque(false);
        frame.add(loginPanel);

        // Update the layout manager for loginPanel
        loginPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // Username label and text field
        JLabel usernameLabel = new JLabel("Username:");
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10, 10, 10, 10);
        loginPanel.add(usernameLabel, c);

        JTextField usernameField = new JTextField(20);
        c.gridx = 1;
        c.gridy = 0;
        loginPanel.add(usernameField, c);

        // Password label and text field
        JLabel passwordLabel = new JLabel("Password:");
        c.gridx = 0;
        c.gridy = 1;
        loginPanel.add(passwordLabel, c);

        JPasswordField passwordField = new JPasswordField(20);
        c.gridx = 1;
        c.gridy = 1;
        loginPanel.add(passwordField, c);

        // Remember me checkbox
        JCheckBox rememberMeCheckBox = new JCheckBox("Remember The Password");
        c.gridx = 2;
        c.gridy = 2;
        loginPanel.add(rememberMeCheckBox, c);

        JCheckBox animationCheckBox = new JCheckBox("Enable Animation");
        animationCheckBox.setSelected(true);
        c.gridx = 1;
        c.gridy = 2;
        loginPanel.add(animationCheckBox, c);

        animationCheckBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                backgroundPanel.setAnimationEnabled(true);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                backgroundPanel.setAnimationEnabled(false);
            }
        });

        try {
            boolean rememberedUserExists = loadRememberedUser(usernameField, passwordField);
            rememberMeCheckBox.setSelected(rememberedUserExists);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Login and Register buttons
        JButton loginButton = new JButton("Login");
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 1;
        loginPanel.add(loginButton, c);

        JButton registerButton = new JButton("Register");
        c.gridx = 1;
        c.gridy = 4;
        loginPanel.add(registerButton, c);

        JButton forgetPasswordButton = new JButton("Password Recovery");
        c.gridx = 2;
        c.gridy = 4;
        loginPanel.add(forgetPasswordButton, c);

        registerButton.addActionListener(e -> {
            try {
                openRegisterWindow();
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        forgetPasswordButton.addActionListener(e -> {
            //                openForgetPasswordWindow(); TODO
            showErrorDialog("This feature is not available yet. Stay tuned!");
        });

        // Add Verification Code label, text field, and displayed verification code
        JLabel verificationCodeLabel = new JLabel("Verification:");
        c.gridx = 0;
        c.gridy = 3;
        loginPanel.add(verificationCodeLabel, c);

        JTextField verificationCodeField = new JTextField(10);
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 1;
        loginPanel.add(verificationCodeField, c);

        JLabel displayedVerificationCode = new JLabel();
        displayedVerificationCode.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        displayedVerificationCode.setHorizontalAlignment(JLabel.CENTER);
        displayedVerificationCode.setPreferredSize(new Dimension(100, 30));
        String[] initialVerificationCode = new String[]{generateVerificationCode()};
        displayedVerificationCode.setText(initialVerificationCode[0]);
        c.gridx = 2;
        c.gridy = 3;
        loginPanel.add(displayedVerificationCode, c);

        displayedVerificationCode.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                initialVerificationCode[0] = generateVerificationCode();
                displayedVerificationCode.setText(initialVerificationCode[0]);
            }
        });

        loginButton.addActionListener(e -> handleLogin(usernameField, passwordField, rememberMeCheckBox, verificationCodeField, initialVerificationCode[0]));

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu helpMenu = new JMenu("Help");

        //Database Setting Menu Item
        JMenuItem databaseSettingMenuItem = new JMenuItem("Database Setting");

        databaseSettingMenuItem.addActionListener(e -> {
            JDialog databaseSettingDialog = new JDialog();
            databaseSettingDialog.setAlwaysOnTop(true);
            databaseSettingDialog.setTitle("Database Setting");
            databaseSettingDialog.setModal(false);
            databaseSettingDialog.setLayout(new BorderLayout());

            JTextArea noticeArea = new JTextArea(
                    "1. Please enter your database settings in JDBC form. For example, I use mySQL as my database and the port number is initially 3306, the information should be stored in chatroom_users database, then the setting should be \"jdbc:mysql://localhost:3306/chatroom_users\".\n" +
                    "2. Enter your user and password correctly!"
            );
            noticeArea.setEditable(false);
            noticeArea.setLineWrap(true);
            noticeArea.setWrapStyleWord(true);

            JScrollPane noticeScrollPane = new JScrollPane(noticeArea);
            noticeScrollPane.setPreferredSize(new Dimension(400, 100));
            databaseSettingDialog.add(noticeScrollPane, BorderLayout.NORTH);

            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

            JPanel settingPanel = new JPanel(new BorderLayout());
            JLabel settingLabel = new JLabel("Setting (JDBC URL):");
            JTextField settingField = new JTextField();
            settingPanel.add(settingLabel, BorderLayout.NORTH);
            settingPanel.add(settingField, BorderLayout.CENTER);
            contentPanel.add(settingPanel);

            JPanel usernamePanel = new JPanel(new BorderLayout());
            JLabel usernameLabel1 = new JLabel("Username:");
            JTextField usernameField1 = new JTextField();
            usernamePanel.add(usernameLabel1, BorderLayout.NORTH);
            usernamePanel.add(usernameField1, BorderLayout.CENTER);
            contentPanel.add(usernamePanel);

            JPanel passwordPanel = new JPanel(new BorderLayout());
            JLabel passwordLabel1 = new JLabel("Password:");
            JPasswordField passwordField1 = new JPasswordField();
            passwordPanel.add(passwordLabel1, BorderLayout.NORTH);
            passwordPanel.add(passwordField1, BorderLayout.CENTER);
            contentPanel.add(passwordPanel);

            JButton confirmButton = new JButton("Confirm");
            contentPanel.add(confirmButton);

            confirmButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String setting = settingField.getText();
                    String username = usernameField1.getText();
                    String password = new String(passwordField1.getPassword());

                    Network_Setting ns = new Network_Setting(setting,username,password);
                    // Store the collected information into variables as needed
                    // For example:
                    // dbSetting = setting;
                    // dbUsername = username;
                    // dbPassword = password;

                    databaseSettingDialog.dispose();
                }
            });

            databaseSettingDialog.add(contentPanel, BorderLayout.CENTER);
            databaseSettingDialog.pack();
            databaseSettingDialog.setLocationRelativeTo(null);
            databaseSettingDialog.setVisible(true);
        });

        //Exit Item
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(e -> ExitConfirmDialog.showExitConfirmDialog(frame));
        //Readme Item
        JMenuItem readmeMenuItem = new JMenuItem("ReadMe");
        readmeMenuItem.addActionListener(e -> {
            try {
                File readmeFile = Paths.get("resources", "readme.md").toFile();
                Desktop.getDesktop().open(readmeFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        helpMenu.add(exitMenuItem);
        helpMenu.add(readmeMenuItem);
        helpMenu.add(databaseSettingMenuItem);

        JMenu functionMenu = new JMenu("Function");
        JMenuItem registerMenuItem = new JMenuItem("Register a new user for free");
        registerMenuItem.addActionListener(e -> {
            try {
                openRegisterWindow();
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        JMenuItem forgetPasswordMenuItem = new JMenuItem("I forgot my password!");

        functionMenu.add(registerMenuItem);
        functionMenu.add(forgetPasswordMenuItem);

        menuBar.add(helpMenu);
        menuBar.add(functionMenu);
        frame.setJMenuBar(menuBar);
    }

    private void handleLogin(JTextField usernameField, JPasswordField passwordField, JCheckBox rememberMeCheckBox, JTextField verificationCodeField, String verificationCode) {
        // Check if the entered verification code is correct
        if (!verificationCodeField.getText().equalsIgnoreCase(verificationCode)) {
            showErrorDialog("Incorrect verification code!");
            return;
        }
        // Verify the user's password!
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        Message messageLogin = new Message();
        messageLogin.setSender(username);
        messageLogin.setMesType(MessageType.MESSAGE_LOGIN_ATTEMPT);
        messageLogin.setContent(password);

        // Send message to server
        try (Socket socket = new Socket("localhost", 8889)) {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            oos.writeObject(messageLogin);
            oos.flush();

            // Receive message from server
            Message messageLogin1 = (Message) ois.readObject();
            if (messageLogin1.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCESSFUL)) {
                User loginUser = messageLogin1.getUser();
                Chatroom chatroom = new Chatroom(loginUser);
                chatroom.start();
                frame.setVisible(false);
                try {
                    if (rememberMeCheckBox.isSelected()) {
                        // If client ticks the checkbox, delete the former stored information,
                        deleteRememberedUser();
                        // Save the new one
                        saveRememberedUser(username, password);
                    } else {
                        // If no, delete the information formerly stored if exists
                        deleteRememberedUser();
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                showErrorDialog("Wrong username or password!");
            }
        } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void openRegisterWindow() throws ClassNotFoundException {
        RegisterWindow register;
        register = new RegisterWindow();
        register.start();
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    private void saveRememberedUser(String username, String password) throws SQLException {
        String sql = "INSERT INTO user_remember (username, password) VALUES (?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, username);
        statement.setString(2, password);
        statement.executeUpdate();
    }

    @SuppressWarnings("All")
    private void deleteRememberedUser() throws SQLException {
        String sql = "DELETE FROM user_remember"; // Exactly delete everything
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
    }

    private boolean loadRememberedUser(JTextField field1,JTextField field2) throws SQLException {
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
    @Deprecated
    static class ErrorDialog extends JDialog {
        // Just a simple Dialog
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
    /*
        * This class is used to show a confirmation dialog when user tries to exit the application.
        * If user clicks "Yes", the application will exit.
        * If user clicks "No", the dialog will be closed.
     */
    public static class ExitConfirmDialog {
        public static void main(String[] args) {
            JFrame frame = new JFrame("My Application");
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.setSize(300, 200);
            frame.setLocationRelativeTo(null);

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    showExitConfirmDialog(frame);
                }
            });

            frame.setVisible(true);
        }

        protected static void showExitConfirmDialog(JFrame frame) {
            int option = JOptionPane.showConfirmDialog(
                    frame,
                    "Do you want to exit?",
                    "Exit Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (option == JOptionPane.YES_OPTION) {
                frame.dispose();
            }
        }
    }

    // An old version of ExitConfirmDialog, but too ugly
    @Deprecated
    private void createExitConfirmDialog() {
        dialog = new JDialog(frame, "Close Chatroom?", true);
        dialog.setSize(200, 100);
        dialog.setLocationRelativeTo(frame);
        dialog.setResizable(false);
        dialog.setLayout(new BorderLayout());

        JLabel questionLabel = new JLabel("Do you want to exit?", JLabel.CENTER);
        dialog.add(questionLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton exitYesButton = new JButton("Yes");
        exitYesButton.setPreferredSize(new Dimension(100, 50));
        exitYesButton.addActionListener(e -> {
            frame.dispose();
            System.exit(0);
        });

        JButton exitNoButton = new JButton("No");
        exitNoButton.setPreferredSize(new Dimension(100, 50));
        exitNoButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(exitYesButton);
        buttonPanel.add(exitNoButton);
        dialog.add(buttonPanel, BorderLayout.CENTER);
    }

    private String generateVerificationCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int length = 5;
        Random random = new Random();
        StringBuilder verificationCode = new StringBuilder();

        for (int i = 0; i < length; i++) {
            verificationCode.append(characters.charAt(random.nextInt(characters.length())));
        }

        return verificationCode.toString();
    }

    // A custom JPanel class for the background animation
    static class AnimatedBackgroundPanel extends JPanel {
        private static final int BALL_COUNT = 50;
        private static final int MAX_SPEED = 5;

        private java.util.List<Ball> balls = new ArrayList<>();
        private boolean animationEnabled = true;

        public AnimatedBackgroundPanel() {
            for (int i = 0; i < BALL_COUNT; i++) {
                balls.add(new Ball(getRandomNumber(0, getWidth()), getRandomNumber(0, getHeight())));
            }

            Timer timer = new Timer(20, e -> moveBallsAndUpdateUI());
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (animationEnabled) {
                for (Ball ball : balls) {
                    ball.paint(g);
                }
            }
        }

        private void moveBallsAndUpdateUI() {
            for (Ball ball : balls) {
                ball.move();
                ball.checkBounds(getWidth(), getHeight());
            }
            repaint();
        }

        public void setAnimationEnabled(boolean enabled) {
            animationEnabled = enabled;
            repaint();
        }

        private int getRandomNumber(int min, int max) {
            return (int) (Math.random() * (max - min + 1) + min);
        }

        class Ball {
            int x;
            int y;
            int speedX;
            int speedY;
            int size;

            public Ball(int x, int y) {
                this.x = x;
                this.y = y;
                this.speedX = getRandomNumber(-MAX_SPEED, MAX_SPEED);
                this.speedY = getRandomNumber(-MAX_SPEED, MAX_SPEED);
                this.size = getRandomNumber(5, 10);
            }

            public void move() {
                x += speedX;
                y += speedY;
            }

            public void checkBounds(int width, int height) {
                if (x < 0 || x + size > width) {
                    speedX = -speedX;
                }

                if (y < 0 || y + size > height) {
                    speedY = -speedY;
                }
            }

            public void paint(Graphics g) {
                g.setColor(new Color(0,255,255,90));
                g.fillOval(x, y, size, size);
            }
        }
    }
}