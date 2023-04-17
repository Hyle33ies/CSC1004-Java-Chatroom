package org.client;

import org.setting.Network_setting;
import org.setting.Network_setting.Network_Setting;
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
//        frame.setAlwaysOnTop(true);

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
        forgetPasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame verifyFrame = new JFrame("Verification");
                verifyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                verifyFrame.setSize(400, 150);
                verifyFrame.setLayout(new GridLayout(3, 2));

                // Add components to the verifyFrame
                JLabel usernameLabel = new JLabel("Username:");
                JTextField usernameField = new JTextField();
                JLabel emailLabel = new JLabel("Email:");
                JTextField emailField = new JTextField();
                JButton submitButton = new JButton("Submit");
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        verifyFrame.dispose();
                    }
                });

                verifyFrame.add(usernameLabel);
                verifyFrame.add(usernameField);
                verifyFrame.add(emailLabel);
                verifyFrame.add(emailField);
                verifyFrame.add(submitButton);
                verifyFrame.add(cancelButton);

                verifyFrame.setVisible(true);

                submitButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String username = usernameField.getText();
                        String email = emailField.getText();

                        try {
                            String sql = "SELECT * FROM QandA WHERE username = ? AND email = ?";
                            PreparedStatement statement = connection.prepareStatement(sql);
                            statement.setString(1, username);
                            statement.setString(2, email);
                            ResultSet result = statement.executeQuery();

                            if (!result.next()) {
                                JOptionPane.showMessageDialog(null,
                                        "No such combination of username and email found / you do not set security problems.", "Error", JOptionPane.ERROR_MESSAGE);
                                usernameField.setText("");
                                emailField.setText("");
                            } else {
                                verifyFrame.dispose();
                                String question1 = result.getString("question1");
                                String answer1 = result.getString("answer1");
                                String question2 = result.getString("question2");
                                String answer2 = result.getString("answer2");

                                if (question1 == null && question2 == null) {
                                    JOptionPane.showMessageDialog(null, "No security questions were set for this account.", "Information", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    // Create a new JFrame to display the questions and get the user's answers
                                    JFrame questionFrame = new JFrame("Security Questions");
                                    questionFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                    questionFrame.setSize(400, 150);
                                    questionFrame.setLayout(new GridLayout(3, 2));

                                    JLabel question1Label = new JLabel(question1 != null ? question1 : "You don't set question 1");
                                    JTextField answer1Field = new JTextField();
                                    JLabel question2Label = new JLabel(question2 != null ? question2 : "You don't set question 2");
                                    JTextField answer2Field = new JTextField();
                                    JButton verifyButton = new JButton("Verify");
                                    JButton cancelQuestionButton = new JButton("Cancel");

                                    questionFrame.add(question1Label);
                                    questionFrame.add(answer1Field);
                                    questionFrame.add(question2Label);
                                    questionFrame.add(answer2Field);
                                    questionFrame.add(verifyButton);
                                    questionFrame.add(cancelQuestionButton);

                                    questionFrame.setVisible(true);

                                    verifyButton.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            String userAnswer1 = answer1Field.getText();
                                            String userAnswer2 = answer2Field.getText();

                                            boolean answer1Correct = question1 == null || userAnswer1.equals(answer1);
                                            boolean answer2Correct = question2 == null || userAnswer2.equals(answer2);

                                            if (answer1Correct && answer2Correct) {
                                                questionFrame.dispose();
                                                JFrame successFrame = new JFrame("Success");
                                                successFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                                successFrame.setSize(400, 350);
                                                successFrame.setLayout(new GridLayout(7, 2));

                                                JLabel successLabel = new JLabel("Your answers are correct!", SwingConstants.CENTER);
                                                JLabel usernameLabel = new JLabel("Username: " + username);
                                                JLabel emailLabel = new JLabel("Email: " + email);
                                                JLabel oldPasswordLabel = new JLabel("Old Password:");
                                                JPasswordField oldPasswordField = new JPasswordField();
                                                JLabel newPasswordLabel = new JLabel("New Password:");
                                                JPasswordField newPasswordField = new JPasswordField();
                                                JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
                                                JPasswordField confirmPasswordField = new JPasswordField();
                                                JButton confirmButton = new JButton("Confirm");
                                                JButton cancelButton = new JButton("Cancel");

                                                successFrame.add(successLabel);
                                                successFrame.add(new JLabel());
                                                successFrame.add(usernameLabel);
                                                successFrame.add(emailLabel);
                                                successFrame.add(oldPasswordLabel);
                                                successFrame.add(oldPasswordField);
                                                successFrame.add(newPasswordLabel);
                                                successFrame.add(newPasswordField);
                                                successFrame.add(confirmPasswordLabel);
                                                successFrame.add(confirmPasswordField);
                                                successFrame.add(confirmButton);
                                                successFrame.add(cancelButton);

                                                successFrame.setVisible(true);

                                                confirmButton.addActionListener(new ActionListener() {
                                                    @Override
                                                    public void actionPerformed(ActionEvent e) {
                                                        String oldPassword = new String(oldPasswordField.getPassword());
                                                        String newPassword = new String(newPasswordField.getPassword());
                                                        String confirmPassword = new String(confirmPasswordField.getPassword());

                                                        try {
                                                            // Check if the old password is correct
                                                            String checkOldPasswordSql = "SELECT password FROM users WHERE username = ?";
                                                            PreparedStatement checkOldPasswordStatement = connection.prepareStatement(checkOldPasswordSql);
                                                            checkOldPasswordStatement.setString(1, username);
                                                            ResultSet resultSet = checkOldPasswordStatement.executeQuery();

                                                            if (resultSet.next()) {
                                                                String storedPassword = resultSet.getString("password");

                                                                if (!oldPassword.equals(storedPassword)) {
                                                                    JOptionPane.showMessageDialog(null, "Old password is incorrect. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                                                                    return;
                                                                }
                                                            }

                                                            if (!newPassword.equals(confirmPassword)) {
                                                                JOptionPane.showMessageDialog(null, "Passwords do not match. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                                                                return;
                                                            }

                                                            if (newPassword.length() < 6 || newPassword.length() > 20) {
                                                                JOptionPane.showMessageDialog(null, "Password must be between 6 and 20 characters.", "Error", JOptionPane.ERROR_MESSAGE);
                                                                return;
                                                            }

                                                            // Update the password in the database
                                                            String updateSql = "UPDATE users SET password = ? WHERE username = ?";
                                                            PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                                                            updateStatement.setString(1, newPassword);
                                                            updateStatement.setString(2, username);
                                                            updateStatement.executeUpdate();

                                                            JOptionPane.showMessageDialog(null, "Password updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                                                            successFrame.dispose();

                                                        } catch (SQLException ex) {
                                                            ex.printStackTrace();
                                                            JOptionPane.showMessageDialog(null, "Failed to update the password. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                                                        }
                                                    }
                                                });

                                                cancelButton.addActionListener(new ActionListener() {
                                                    @Override
                                                    public void actionPerformed(ActionEvent e) {
                                                        successFrame.dispose();
                                                    }
                                                });
                                            } else {
                                                JOptionPane.showMessageDialog(null, "Your answers are incorrect. Please think through.", "Error", JOptionPane.ERROR_MESSAGE);
                                            }
                                        }
                                    });

                                    cancelQuestionButton.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            questionFrame.dispose();
                                        }
                                    });
                                }
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        });
        loginPanel.add(forgetPasswordButton, c);

        registerButton.addActionListener(e -> {
            try {
                openRegisterWindow();
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
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

            confirmButton.addActionListener(e1 -> {
                String setting = settingField.getText();
                String username = usernameField1.getText();
                String password = new String(passwordField1.getPassword());

                Network_Setting ns = new Network_Setting(setting, username, password);
//                try {
//                    Network_setting.DatabaseInitializer.init();
//                } catch (SQLException ex) {
//                    throw new RuntimeException(ex);
//                }
                databaseSettingDialog.dispose();
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

    private boolean loadRememberedUser(JTextField field1, JTextField field2) throws SQLException {
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

        private final java.util.List<Ball> balls = new ArrayList<>();
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
                g.setColor(new Color(0, 255, 255, 90));
                g.fillOval(x, y, size, size);
            }
        }
    }
}