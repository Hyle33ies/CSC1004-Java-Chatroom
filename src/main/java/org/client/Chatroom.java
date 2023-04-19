package org.client;

import org.setting.Network_setting.Network_Setting;
import org.client.tools.Message;
import org.client.tools.MessageType;
import org.client.tools.User;
import org.client.tools.UserConnection;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Chatroom extends JFrame {

    private JFrame mainFrame;
    private JList<User> friendsList;
    private ArrayList<UserConnection> friendsListConnection;
    private DefaultListModel<User> friendsListModel = new DefaultListModel<>();
    private JScrollPane messageScrollPane;
    private JTextPane messagePane;
    private JTextArea messageField;
    private JLabel chatWithLabel;
    private JPanel chatWithPanel;
    private Map<String, JTextPane> userChatPanes;
    private final User current_user;
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private final java.sql.Connection connection;


    public Chatroom(User current_user) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Network_Setting ns = new Network_Setting();
        System.out.println("Network Setting: " + ns);
        try {
            connection = DriverManager.getConnection(ns.getPersonalized_setting(), ns.getPersonalized_username(), ns.getPersonalized_password());
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database", e);
        }

        this.current_user = current_user;
        System.out.println("Current user: " + current_user.getUsername());
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            sendExitMessage();
        }
        super.processWindowEvent(e);
    }

    @SuppressWarnings("All")
    private void connectToServer(String serverAddress, int serverPort) {
        try {
            socket = new Socket(serverAddress, serverPort);
            System.out.println("Connected to server: " + socket.getInetAddress().getHostAddress() + " on port: " + socket.getPort());
            System.out.println("Local port: " + socket.getLocalPort());
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            Message updateOutputStreamMessage = new Message();
            updateOutputStreamMessage.setSender(current_user.getUsername());
            updateOutputStreamMessage.setMesType(MessageType.MESSAGE_UPDATE_OUTPUT_STREAM);
            // Tell the server this user is online
            output.writeObject(updateOutputStreamMessage);
            sendUpdatedPort(); // update the port number
            listenToIncomingMessages();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error connecting to the server. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("All")
    protected void start() {
        mainFrame = new JFrame("Chat Application");
        mainFrame.setSize(800, 600);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setAlwaysOnTop(false);

        //connect to server
        connectToServer("localhost", 8889);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainFrame.add(mainPanel);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        mainFrame.setJMenuBar(menuBar);
        // First Menu: Actions
        JMenu actionsMenu = new JMenu("Actions");
    /*
        // Add a Friend menu item, no long needed since we have "Get Online Users" button
        JMenuItem addFriendMenuItem = new JMenuItem("Add a Friend");
        addFriendMenuItem.addActionListener(e -> {
            AddUserDialog addUserDialog = new AddUserDialog(mainFrame);
            addUserDialog.setVisible(true);

            String newUser = addUserDialog.getNewUserName();
            if (newUser != null) {
                friendsListModel.addElement(newUser);
            }
        });
    */
        // new user, but do not close the current window
        JMenuItem newUserLoginItem = new JMenuItem("New User Login");
        actionsMenu.add(newUserLoginItem);
        newUserLoginItem.addActionListener(e -> {
            try {
                Login newLogin = new Login();
                newLogin.start();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
        // Exit menu item
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(mainFrame, "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                sendExitMessage();
                mainFrame.dispose();
            }
        });
        actionsMenu.add(exitMenuItem);
        // new window, but close the current user window
        JMenuItem switchUserMenuItem = new JMenuItem("Login with another user");
        switchUserMenuItem.addActionListener(e -> {
            sendExitMessage(); // exit the current user
            mainFrame.dispose();
            Login login;
            try {
                login = new Login();// new window
            } catch (SQLException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            login.start();
        });
        actionsMenu.add(switchUserMenuItem);

        menuBar.add(actionsMenu);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.addActionListener(e -> {
            // open the readme file
            try {
                File readmeFile = Paths.get("resources", "readme.md").toFile();
                Desktop.getDesktop().open(readmeFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        helpMenu.add(aboutMenuItem);
        menuBar.add(helpMenu);

        // update your information
        JMenuItem updateProfileMenuItem = new JMenuItem("Update Profile");
        updateProfileMenuItem.addActionListener(e -> {
            UpdateProfileDialog updateProfileDialog = new UpdateProfileDialog(mainFrame, current_user, this);
            updateProfileDialog.setVisible(true);
        });
        actionsMenu.add(updateProfileMenuItem);

        // add emoji menu item, do not confuse it with addEmojis() method!
        JMenuItem addEmojiMenuItem = new JMenuItem("Add a new emoji");
        addEmojiMenuItem.addActionListener(e -> {
            JDialog fileChooserDialog = new JDialog((Frame) null, "Select Emoji", Dialog.ModalityType.APPLICATION_MODAL);
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "png", "jpg", "jpeg", "gif", "bmp", "tiff", "tif");
            fileChooser.setFileFilter(filter);

            fileChooser.addActionListener(e1 -> {
                if (e1.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        Files.copy(selectedFile.toPath(), new File("resources/emoji/" + selectedFile.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                        JOptionPane.showMessageDialog(fileChooserDialog, "New emoji added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(fileChooserDialog, "Failed to add the new emoji. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    fileChooserDialog.dispose();
                }
            });

            fileChooserDialog.add(fileChooser);
            fileChooserDialog.pack();
            fileChooserDialog.setVisible(true);
        });

        actionsMenu.add(addEmojiMenuItem);
        // Friends list panel
        JPanel friendsListPanel = new JPanel(new BorderLayout());
        mainPanel.add(friendsListPanel, BorderLayout.WEST);

        // Friends list title
        JPanel friendsPanel = new JPanel(new BorderLayout());
        JLabel friendsListTitle = new JLabel("FRIENDS");
        friendsListTitle.setHorizontalAlignment(SwingConstants.CENTER);
        friendsPanel.add(friendsListTitle, BorderLayout.NORTH);
    /*
    Former "Add User" button, not used anymore!

        JButton addUserButton = new JButton("Add User");
        friendsPanel.add(addUserButton, BorderLayout.SOUTH);

        addUserButton.addActionListener(e -> {
            // Show a dialog to add a new user
            AddUserDialog addedUserDialog = new AddUserDialog(mainFrame);
            addedUserDialog.setVisible(true);

            String newUser = addedUserDialog.getNewUserName();
            if (newUser != null) {
                friendsListModel.addElement(newUser);
            }
        });
    */
        JButton getOnlineFriendsButton = new JButton("Get Online Friends");
        friendsPanel.add(getOnlineFriendsButton, BorderLayout.SOUTH);
        getOnlineFriendsButton.addActionListener(e -> {
            try {
                // Send
                Message getOnlineFriendsMessage = new Message();
                getOnlineFriendsMessage.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
                getOnlineFriendsMessage.setSender(current_user.getUsername());
                output.writeObject(getOnlineFriendsMessage);
                // Get, implemented in the listenToIncomingMessages() method
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });


        // Friends list
        friendsListModel = new DefaultListModel<>();

        friendsList = new JList<>(friendsListModel);
        friendsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        friendsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                User user = (User) value;
                String displayText = user.getUsername(); // TODO (may never) customize the display text to include more information about the user
                return super.getListCellRendererComponent(list, displayText, index, isSelected, cellHasFocus);
            }
        });

        JScrollPane friendsScrollPane = new JScrollPane(friendsList);
        friendsScrollPane.setPreferredSize(new Dimension(200, 0));

        // The information of selected user
        JPopupMenu userInfoMenu = new JPopupMenu();
        JMenuItem showUserInfoItem = new JMenuItem("Show User Information");
        userInfoMenu.add(showUserInfoItem);

        // Look up user's information
        showUserInfoItem.addActionListener(e -> {
            // Get the selected user from the friends list
            User selectedUser = friendsList.getSelectedValue();
            if (selectedUser != null) {
                // Search for the User in the ArrayList
                UserConnection userConnection = friendsListConnection.stream()
                        .filter(con -> con.getUser().getUsername().equals(selectedUser.getUsername()))
                        .findFirst().orElse(null);

                if (userConnection != null) {
                    User user = userConnection.getUser();
                    // Show a dialog with user information
                    JDialog userInfoDialog = new JDialog(mainFrame, "User Information", true);
                    userInfoDialog.setLayout(new GridLayout(6, 2));

                    userInfoDialog.add(new JLabel("Username: "));
                    userInfoDialog.add(new JLabel(user.getUsername()));
                    userInfoDialog.add(new JLabel("Age: "));
                    userInfoDialog.add(new JLabel(String.valueOf(user.getAge())));
                    userInfoDialog.add(new JLabel("Sex: "));
                    userInfoDialog.add(new JLabel(user.getSex()));
                    userInfoDialog.add(new JLabel("Country: "));
                    userInfoDialog.add(new JLabel(user.getCountry()));
                    userInfoDialog.add(new JLabel("City: "));
                    userInfoDialog.add(new JLabel(user.getCity()));
                    userInfoDialog.add(new JLabel("Introduction: "));
                    userInfoDialog.add(new JLabel(user.getIntro()));

                    userInfoDialog.pack();
                    userInfoDialog.setLocationRelativeTo(mainFrame);
                    userInfoDialog.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "User not found in the friends list connection.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // The "Chat with" part
        friendsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                friendsList.requestFocusInWindow(); // Add this line
                if (e.getClickCount() == 2) {
                    // Show chat panel with selected user
                    User selectedUser = friendsList.getSelectedValue();
                    String selectedUsername = selectedUser != null ? selectedUser.getUsername() : null;
                    chatWithLabel.setText("Chat with: " + selectedUsername);
                    showChatPaneForUser(selectedUsername);
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    userInfoMenu.show(friendsList, e.getX(), e.getY());
                }
            }
        });


        friendsListPanel.add(friendsScrollPane, BorderLayout.CENTER);
        friendsPanel.add(friendsScrollPane, BorderLayout.CENTER);
        mainPanel.add(friendsPanel, BorderLayout.WEST);

        // Add "About the developer" menu item
        JMenuItem aboutDeveloperMenuItem = new JMenuItem("About the developer");
        aboutDeveloperMenuItem.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://www.example.com"));
                // TODO update the website
            } catch (IOException | URISyntaxException ex) {
                ex.printStackTrace();
            }
        });
        helpMenu.add(aboutDeveloperMenuItem);

        menuBar.add(helpMenu);

        // Add User menu
        JMenu userMenu = new JMenu("User");
        JMenuItem currentUserMenuItem = new JMenuItem("Current User");
        currentUserMenuItem.addActionListener(e -> JOptionPane.showMessageDialog(mainFrame, "Username: " + current_user.getUsername() + "\nAge: " + current_user.getAge() + "\nSex: " + current_user.getSex() + "\nCountry: " + current_user.getCountry() + "\nCity: " + current_user.getCity() + "\nIntroduction: " + current_user.getIntro(), "User Information", JOptionPane.INFORMATION_MESSAGE));
        userMenu.add(currentUserMenuItem);
        menuBar.add(userMenu);

        // Biggest Part: Chat panel
        chatWithPanel = new JPanel(new BorderLayout());
        JPanel userInfoPanel = new JPanel(new BorderLayout());
        JLabel currentUserLabel = new JLabel("Current User @" + current_user.getUsername());
        userInfoPanel.add(currentUserLabel, BorderLayout.WEST);

        chatWithLabel = new JLabel("Chat with: ");
        chatWithLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        userInfoPanel.add(chatWithLabel, BorderLayout.EAST);

        chatWithPanel.add(userInfoPanel, BorderLayout.NORTH);

        // Sent Message
        messagePane = new JTextPane();
        messagePane.setEditable(false);
        messageScrollPane = new JScrollPane(messagePane);
        chatWithPanel.add(messageScrollPane, BorderLayout.CENTER);

        // Typing area
        JPanel typingPanel = new JPanel(new BorderLayout());
        messageField = new JTextArea(3, 50);
        JScrollPane typingScrollPane = new JScrollPane(messageField);
        typingPanel.add(typingScrollPane, BorderLayout.CENTER);

        // Submit Button
        JButton submitButton = new JButton("Submit");
        typingPanel.add(submitButton, BorderLayout.EAST);
        submitButton.addActionListener(e -> submitMessage());
        submitButton.setPreferredSize(new Dimension(80, 40));
        //Add a keyboard shortcut "ctrl + Enter" for submitButton
        submitButton.registerKeyboardAction(e -> submitMessage(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);

        JButton sendFilesButton = new JButton("Files");
        sendFilesButton.setPreferredSize(new Dimension(80, 20));
        sendFilesButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(mainFrame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                sendFile(selectedFile);
            }
        });

        // Emoji Function
        JButton emojiButton = new JButton("Emoji");
        emojiButton.setPreferredSize(new Dimension(80, 20));
        // Add actionListener for emojiButton here
        emojiButton.addActionListener(e -> {
            JDialog emojiDialog = new JDialog(mainFrame, "Emoji", false);
            emojiDialog.setSize(430, 300); // Adjust the width to accommodate four emojis per row
            emojiDialog.setLocationRelativeTo(mainFrame);

            JPanel emojiPanel = new JPanel();
            emojiPanel.setLayout(new GridLayout(0, 4)); // Set the number of columns to 4

            // Add emojis to the emojiPanel
            addEmojis(emojiPanel);

            JScrollPane scrollPane = new JScrollPane(emojiPanel);
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // Prevent horizontal scrolling
            emojiDialog.add(scrollPane);
            emojiDialog.setVisible(true);
        });

        //Message History
        JButton historyButton = new JButton("History");
        historyButton.addActionListener(e -> {
            JFrame historyFrame = new JFrame("History");
            historyFrame.setAlwaysOnTop(true);
            historyFrame.setSize(300, 600);
            historyFrame.setLocationRelativeTo(mainFrame);

            // Create a JTextPane to display the message history
            JTextPane historyPane = new JTextPane();
            historyPane.setContentType("text/html");
            historyPane.setEditable(false);
            JScrollPane historyScrollPane = new JScrollPane(historyPane);
            historyFrame.add(historyScrollPane);

            // Fetch the message history for the current users
            User selectedUser = friendsList.getSelectedValue();
            if (selectedUser != null) {
                try {
                    PreparedStatement statement = connection.prepareStatement("SELECT sender, getter, content, send_time FROM message_history WHERE (sender = ? AND getter = ?) OR (sender = ? AND getter = ?) ORDER BY send_time");
                    statement.setString(1, current_user.getUsername());
                    statement.setString(2, selectedUser.getUsername());
                    statement.setString(3, selectedUser.getUsername());
                    statement.setString(4, current_user.getUsername());

                    ResultSet resultSet = statement.executeQuery();
                    StringBuilder historyText = new StringBuilder("<html><body>");

                    // Add messages to the historyPane
                    while (resultSet.next()) {
                        String sender = resultSet.getString("sender");
                        String getter = resultSet.getString("getter");
                        String content = resultSet.getString("content");
                        String sendTime = resultSet.getString("send_time");

                        String color = sender.equals(current_user.getUsername()) ? "blue" : "red";
                        String formattedMessage = "<font color=\"" + color + "\"><b>" + sender + " [" + sendTime + "]:</b></font> " + content.replace("\n", "<br>") + "<br>";
                        historyText.append(formattedMessage);
                    }
                    historyText.append("</body></html>");
                    historyPane.setText(historyText.toString());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showWarningMessage("Failed to fetch message history. Please try again.");
                    return;
                }
            }

            historyFrame.setVisible(true);
        });

        Dimension buttonSize = new Dimension(80, 22);
        sendFilesButton.setMaximumSize(buttonSize);
        emojiButton.setMaximumSize(buttonSize);
        historyButton.setMaximumSize(buttonSize);

        JPanel leftButtonPanel = new JPanel();
        leftButtonPanel.setLayout(new GridLayout(3, 1));
        leftButtonPanel.add(sendFilesButton);
        leftButtonPanel.add(emojiButton);
        leftButtonPanel.add(historyButton);

        typingPanel.add(leftButtonPanel, BorderLayout.WEST);
        chatWithPanel.add(typingPanel, BorderLayout.SOUTH);

        mainPanel.add(chatWithPanel, BorderLayout.CENTER);

        // Initialize userChatPanes
        userChatPanes = new HashMap<>();

        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(mainFrame, "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    sendExitMessage();
                    mainFrame.dispose();
                }
            }
        });

        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.setResizable(false);
        mainFrame.setSize(800, 600);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private void showChatPaneForUser(String username) {
        JTextPane chatPane = userChatPanes.get(username);
        if (chatPane == null) {
            chatPane = new JTextPane();
            chatPane.setEditable(false);
            userChatPanes.put(username, chatPane);
        }
        chatWithPanel.remove(messageScrollPane);
        messageScrollPane = new JScrollPane(chatPane);
        chatWithPanel.add(messageScrollPane, BorderLayout.CENTER);
        chatWithPanel.revalidate();
        chatWithPanel.repaint();
    }

    private void submitMessage() {
        String message = messageField.getText().trim();
        if (message.isEmpty()) {
            return;
        }

        User selectedUser = friendsList.getSelectedValue();
        String selectedUsername = selectedUser != null ? selectedUser.getUsername() : null;
        if (selectedUsername == null) {
            showWarningMessage("Please select a friend before sending a message.");
            return;
        }

        JTextPane chatPane = userChatPanes.get(selectedUser.getUsername());

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String sendTime = now.format(formatter);

        // Create a Message object
        Message msg = new Message();
        msg.setSender(current_user.getUsername());
        msg.setGetter(selectedUsername);
        msg.setContent(message);
        msg.setSendTime(sendTime);
        msg.setMesType(MessageType.MESSAGE_COMM_MES);

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO message_history(sender, getter, content, send_time) VALUES (?, ?, ?, ?)");
            statement.setString(1, msg.getSender());
            statement.setString(2, msg.getGetter());
            statement.setString(3, msg.getContent());
            statement.setString(4, msg.getSendTime());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            showWarningMessage("Failed to save the message. Please try again.");
            return;
        }

        // Send the message to the server
        try {
            output.writeObject(msg);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
            showWarningMessage("Failed to send the message. Please try again.");
            return;
        }

        // Update the local chat pane
        appendMessageToChatPane(current_user.getUsername(), chatPane, message, sendTime, true);

        messageField.setText("");
        messageField.setLineWrap(true);
        messageField.setWrapStyleWord(true);
    }

    @SuppressWarnings("All")
    private void appendMessageToChatPane(String sender, JTextPane chatPane, String message, String sendTime, boolean isSender) {
        chatPane.setContentType("text/html");
        HTMLEditorKit editorKit = (HTMLEditorKit) chatPane.getEditorKit();
        HTMLDocument doc = (HTMLDocument) chatPane.getDocument();
        String currentText = chatPane.getText().replaceAll("</body>", "").replaceAll("</html>", "");

        String senderColor = isSender ? "blue" : "red";
        String formattedMessage = "<font color=\"" + senderColor + "\"><b>" + sender + " [" + sendTime + "]:</b></font> " + message.replace("\n", "<br>") + "<br>";

        try {
            editorKit.insertHTML(doc, doc.getLength(), formattedMessage, 0, 0, null);
        } catch (BadLocationException | IOException e) {
            e.printStackTrace();
        }
    }



    @SuppressWarnings("All")
    @Deprecated
    private String replaceEmojiWithHTML(String content) {
        // This method is used to replace the emoji with respective HTML code to display it
        // Now in another way, I use StyledDocument to append the emoji/picture in the chat box
        String regex = "emoji_\\d+\\.(?:png|jpg|jpeg|gif|bmp|tiff|tif)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String emojiFilename = matcher.group();
            String emojiPath = "resources/emoji/" + emojiFilename;
            String imgTag = "<img src=\"" + emojiPath + "\" width=\"20\" height=\"20\">";
            matcher.appendReplacement(result, imgTag);
        }
        matcher.appendTail(result);
        return result.toString();
    }


    @Deprecated
    @SuppressWarnings("All")
    // No more needed, used to add a friend manually
    static class AddUserDialog extends JDialog {
        private final JTextField userNameField;
        private final JTextField ipAddressField;
        private final JTextField portField;
        private final JButton addButton;

        public AddUserDialog(JFrame owner) {
            super(owner, "Add User", true);
            setLayout(new GridLayout(4, 2));

            userNameField = new JTextField(10);
            ipAddressField = new JTextField(10);
            portField = new JTextField(10);
            addButton = new JButton("Add");

            add(new JLabel("Username:"));
            add(userNameField);
            add(new JLabel("IP Address:"));
            add(ipAddressField);
            add(new JLabel("Port:"));
            add(portField);
            add(addButton);

            addButton.addActionListener(e -> {
                // newUserName = userNameField.getText().trim();
                dispose();
            });

            pack();
            setLocationRelativeTo(owner);
        }
    }

    static class UpdateProfileDialog extends JDialog {
        private final JTextField ageField;
        private final JTextField sexField;
        private final JTextField countryField;
        private final JTextField cityField;
        private final JTextField introField;

        public UpdateProfileDialog(JFrame owner, User currentUser, Chatroom mainClassReference) {
            super(owner, "Update Profile", false);
            setLayout(new GridLayout(6, 2));
            // dummy codes about update your information
            ageField = new JTextField(10);
            sexField = new JTextField(10);
            countryField = new JTextField(10);
            cityField = new JTextField(10);
            introField = new JTextField(10);
            JButton updateButton = new JButton("Update");

            ageField.setText(String.valueOf(currentUser.getAge()));
            sexField.setText(currentUser.getSex());
            countryField.setText(currentUser.getCountry());
            cityField.setText(currentUser.getCity());
            introField.setText(currentUser.getIntro());

            add(new JLabel("Age:"));
            add(ageField);
            add(new JLabel("Sex:"));
            add(sexField);
            add(new JLabel("Country:"));
            add(countryField);
            add(new JLabel("City:"));
            add(cityField);
            add(new JLabel("Introduction:"));
            add(introField);
            add(updateButton);

            updateButton.addActionListener(e -> {
                String ageText = ageField.getText().trim();
                String sexText = sexField.getText().trim();

                if (ageText.isEmpty() || sexText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Age and Sex fields cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    currentUser.setAge(Integer.parseInt(ageText));
                    currentUser.setSex(sexText);
                    currentUser.setCountry(countryField.getText().trim());
                    currentUser.setCity(cityField.getText().trim());
                    currentUser.setIntro(introField.getText().trim());

                    // Call updateUserInfo() using the main class reference
                    // Need to update the information also in database!
                    mainClassReference.updateUserInfo();

                    dispose();
                }
            });
            pack();
            setLocationRelativeTo(owner);

        }

    }

    private void updateUserInfo() {
        try {
            current_user.updateUser();
            // call for object method
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error updating user information. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendExitMessage() {
        /*
         * This method tells the server this user has exited
         * so that the server can delete it from the online user list
         * */
        if (socket != null && output != null) {
            try {
                Message exitMessage = new Message();
                exitMessage.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
                exitMessage.setSender(current_user.getUsername());
                output.writeObject(exitMessage);
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateFriendsList(List<UserConnection> onlineFriends, String currentUser) {
        /*
         * Input the online friend list and current name
         * This method add all users to the list except the user him/herself
         */
        friendsListModel.clear();
        for (UserConnection userConnection : onlineFriends) {
            User friend = userConnection.getUser();
            if (!friend.getUsername().equals(currentUser) && !isUserInList(friend)) {
                friendsListModel.addElement(friend);
            }
        }
    }

    private boolean isUserInList(User user) {
        for (int i = 0; i < friendsListModel.size(); i++) {
            if (friendsListModel.getElementAt(i).getUsername().equals(user.getUsername())) {
                return true;
            }
        }
        return false;
    }


    private void showWarningMessage(String message) {
        StyledDocument document = messagePane.getStyledDocument();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setForeground(attrs, Color.RED);
        try {
            document.insertString(document.getLength(), message + "\n", attrs);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void listenToIncomingMessages() {
        new Thread(() -> {
            try {
                while (true) {
                    Message incomingMessage = (Message) input.readObject();
                    switch (incomingMessage.getMesType()) {
                        case MessageType.MESSAGE_RET_ONLINE_FRIEND -> {
                            // Fetch the online friend list
                            ArrayList<UserConnection> onlineFriends = incomingMessage.getUserList();
                            friendsListConnection = onlineFriends; // Update the connection list
                            updateFriendsList(onlineFriends, current_user.getUsername());
                        }
                        case MessageType.MESSAGE_COMM_MES -> {
                            String sender = incomingMessage.getSender();
                            String messageContent = incomingMessage.getContent();
                            String sendTime = incomingMessage.getSendTime();
                            System.out.println("Received Messages from:" + sender);
                            // Update the chat pane for the sender
                            SwingUtilities.invokeLater(() -> {
                                JTextPane chatPane = getChatPaneForUser(sender);
                                appendMessageToChatPane(sender, chatPane, messageContent, sendTime, false);
                            });
                        }
                        case MessageType.MESSAGE_FILE_TRANSFER -> {
                            // Two cases: common files and image files
                            System.out.println("Receive a File from " + incomingMessage.getSender());
                            String sender = incomingMessage.getSender();
                            String fileName = incomingMessage.getFileName();
                            System.out.println("Filename: " + fileName);
                            byte[] decodedBytes = Base64.getDecoder().decode(incomingMessage.getContent());

                            if (incomingMessage.isImage) {
                                System.out.println("Receive an Image!");
                                SwingUtilities.invokeLater(() -> {
                                    ImageIcon imageIcon = new ImageIcon(decodedBytes);
                                    JTextPane chatPane = getChatPaneForUser(sender);
                                    appendImageToChatPane(sender, chatPane, imageIcon, true);
                                });
                            }

                            int result = JOptionPane.showConfirmDialog(mainFrame, sender + " wants to send you a file: " + fileName + ". Do you want to accept?", "File Transfer", JOptionPane.YES_NO_OPTION);
                            if (result == JOptionPane.YES_OPTION) {
                                JFileChooser fileChooser = new JFileChooser();
                                fileChooser.setSelectedFile(new File(fileName));
                                fileChooser.setDialogTitle("Save File");
                                int userSelection = fileChooser.showSaveDialog(mainFrame);
                                if (userSelection == JFileChooser.APPROVE_OPTION) {
                                    File fileToSave = fileChooser.getSelectedFile();
                                    try {
                                        Files.write(fileToSave.toPath(), decodedBytes);
                                        JOptionPane.showMessageDialog(mainFrame, "File saved successfully.", "File Transfer", JOptionPane.INFORMATION_MESSAGE);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        JOptionPane.showMessageDialog(mainFrame, "Failed to save the file. Please try again.", "File Transfer", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            }
                        }
                        case MessageType.MESSAGE_EMOJI -> {
                            System.out.println("Receive an Emoji from " + incomingMessage.getSender());
                            String sender = incomingMessage.getSender();
                            String emojiFilename = incomingMessage.getContent();
                            SwingUtilities.invokeLater(() -> {
                                JTextPane chatPane = getChatPaneForUser(sender);
                                appendEmojiToChatPane(sender, chatPane, emojiFilename);
                            });

                            try {
                                PreparedStatement statement = connection.prepareStatement("INSERT INTO message_history(sender, getter, content, send_time) VALUES (?, ?, ?, ?)");
                                statement.setString(1, sender);
                                statement.setString(2, current_user.getUsername());
                                statement.setString(3, emojiFilename);
                                statement.setString(4, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                                statement.executeUpdate();
                            } catch (SQLException e) {
                                e.printStackTrace();
                                showWarningMessage("Failed to save the message. Please try again.");
                            }
                        }
                    }
                }
            } catch (EOFException | SocketException e) {
                System.out.println("Connection closed or reset.");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void sendUpdatedPort() {
        Message updatePortMessage = new Message();
        updatePortMessage.setMesType(MessageType.MESSAGE_UPDATE_PORT);
        updatePortMessage.setSender(current_user.getUsername());
        updatePortMessage.setContent(String.valueOf(socket.getLocalPort()));

        try {
            output.writeObject(updatePortMessage);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JTextPane getChatPaneForUser(String username) {
        JTextPane chatPane = userChatPanes.get(username);
        if (chatPane == null) {
            chatPane = new JTextPane();
            chatPane.setEditable(false);
            userChatPanes.put(username, chatPane);
        }
        return chatPane;
    }

    private void sendFile(File file) {
        User selectedUser = friendsList.getSelectedValue();
        String selectedUsername = selectedUser != null ? selectedUser.getUsername() : null;
        if (selectedUsername == null) {
            showWarningMessage("Please select a friend before sending a file.");
            return;
        }

        String fileName = file.getName();
        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);

        if (fileExtension.equalsIgnoreCase("png") || fileExtension.equalsIgnoreCase("jpg") || fileExtension.equalsIgnoreCase("jpeg")) {
            // Handle image files
            try {
                BufferedImage image = ImageIO.read(file);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(image, fileExtension, outputStream);
                outputStream.flush();
                String base64Image = Base64.getEncoder().encodeToString(outputStream.toByteArray());
                outputStream.close();

                Message message = new Message();
                message.setSender(current_user.getUsername());
                message.setGetter(selectedUsername);
                message.setContent(base64Image);
                message.setFileExtension(fileExtension);
                message.setMesType(MessageType.MESSAGE_FILE_TRANSFER);
                message.setFileName(fileName);
                message.isImage = true;
                output.writeObject(message);
                output.flush();

                SwingUtilities.invokeLater(() -> {
                    ImageIcon imageIcon = new ImageIcon(outputStream.toByteArray());
                    JTextPane chatPane = getChatPaneForUser(selectedUsername);
                    appendImageToChatPane(current_user.getUsername(), chatPane, imageIcon, false);
                });

            } catch (IOException e) {
                e.printStackTrace();
                showWarningMessage("Failed to send the image. Please try again.");
            }
        } else {
            // Handle non-image files
            try {
                byte[] fileContent = Files.readAllBytes(file.toPath());
                String encodedContent = Base64.getEncoder().encodeToString(fileContent);
                Message message = new Message();
                message.setSender(current_user.getUsername());
                message.setGetter(selectedUsername);
                message.setContent(encodedContent);
                message.setMesType(MessageType.MESSAGE_FILE_TRANSFER);
                message.setFileName(fileName);
                output.writeObject(message);
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
                showWarningMessage("Failed to send the file. Please try again.");
            }
        }
    }

    private void appendImageToChatPane(String sender, JTextPane chatPane, ImageIcon imageIcon, boolean flag) {
        // Used StyledDocument to append image to chat box
        StyledDocument doc = chatPane.getStyledDocument();
        Style defaultStyle = doc.getStyle(StyleContext.DEFAULT_STYLE);
        Style senderStyle = doc.addStyle("SenderStyle", defaultStyle);
        StyleConstants.setForeground(senderStyle, Color.BLUE);
        if (flag) StyleConstants.setForeground(senderStyle, Color.RED);
        StyleConstants.setBold(senderStyle, true);

        try {
            doc.insertString(doc.getLength(), sender + ": ", senderStyle);
            Style imageStyle = doc.addStyle("ImageStyle", null);
            StyleConstants.setIcon(imageStyle, imageIcon);
            doc.insertString(doc.getLength(), " ", imageStyle);
            doc.insertString(doc.getLength(), "\n", null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void addEmojis(JPanel emojiPanel) {
        // This method is to SEND the emoji, the manual loading is at about line 200.
        // In charge of anything except appending the emoji on the user's own window, which is done by the next method.
        // Disadvantage: Cannot adjust the size of the picture, so the resolution ratio may not be suitable
        File emojiFolder = new File("resources/emoji");
        // Currently supported forms
        File[] emojiFiles = emojiFolder.listFiles((dir, name) ->
                   name.toLowerCase().endsWith(".png")
                || name.toLowerCase().endsWith(".jpg")
                || name.toLowerCase().endsWith(".jpeg")
                || name.toLowerCase().endsWith(".gif")
                || name.toLowerCase().endsWith(".bmp")
                || name.toLowerCase().endsWith(".tiff")
                || name.toLowerCase().endsWith(".tif"));

        // Sort the files by the last time they're modified.
        if (emojiFiles != null) {
            Arrays.sort(emojiFiles, (file1, file2) -> {
                long lastModified1 = file1.lastModified();
                long lastModified2 = file2.lastModified();
                return Long.compare(lastModified1, lastModified2);
            });
        }

        if (emojiFiles != null) {
            for (File emojiFile : emojiFiles) {
                try {
                    BufferedImage emojiImage = ImageIO.read(emojiFile);
                    ImageIcon emojiIcon = new ImageIcon(emojiImage.getScaledInstance(64, 64, Image.SCALE_SMOOTH));
                    JButton emojiButton = new JButton(emojiIcon);
                    emojiButton.addActionListener(e -> {
                        User selectedUser = friendsList.getSelectedValue();
                        // Update friends first

                        String selectedUsername = selectedUser != null ? selectedUser.getUsername() : null;
                        if (selectedUsername == null) {
                            showWarningMessage("Please select a friend before sending an emoji.");
                            return;
                        }

                        String emojiFilename = emojiFile.getName();

                        // Send the message to server
                        Message message = new Message();
                        message.setSender(current_user.getUsername());
                        message.setGetter(selectedUsername);
                        message.setContent(emojiFilename);
                        message.setMesType(MessageType.MESSAGE_EMOJI);
                        try {
                            output.writeObject(message);
                            output.flush();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                            showWarningMessage("Failed to send the emoji. Please try again.");
                        }
                        JTextPane chatPane = getChatPaneForUser(selectedUsername);
                        appendEmojiToChatPane(current_user.getUsername(), chatPane, emojiFilename);
                    });
                    emojiPanel.add(emojiButton);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void appendEmojiToChatPane(String sender, JTextPane chatPane, String emojiFilename) {
        // Cooperate with addEmojis() method, this is to append it on the chat box.
        try {
            File emojiFile = new File("resources/emoji/" + emojiFilename);
            // Fetch the file by its name
            BufferedImage emojiImage = ImageIO.read(emojiFile);
            ImageIcon emojiIcon = new ImageIcon(emojiImage.getScaledInstance(32, 32, Image.SCALE_SMOOTH));

            StyledDocument doc = chatPane.getStyledDocument();
            Style style = doc.addStyle("emojiStyle", null);
            StyleConstants.setIcon(style, emojiIcon);

            String messageText = sender + ": ";
            doc.insertString(doc.getLength(), messageText, null);
            doc.insertString(doc.getLength(), " ", style);
            doc.insertString(doc.getLength(), "\n", null);

            // Add this line to prevent the disappearing emoji issue
            chatPane.setCaretPosition(doc.getLength());

        } catch (IOException | BadLocationException e) {
            e.printStackTrace();
        }
    }

}
