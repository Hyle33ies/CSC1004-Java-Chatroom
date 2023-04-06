package org.client;

import org.client.tools.Message;
import org.client.tools.MessageType;
import org.client.tools.User;
import org.server.UserConnection;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class Chatroom extends JFrame{

    private JFrame mainFrame;
    private JList<User> friendsList;
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
    private List<UserConnection> userConnections;


    public Chatroom(User current_user) {
        this.current_user = current_user;
        this.userConnections = new ArrayList<>();
        System.out.println("Current user: " + current_user.getUsername());
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            sendExitMessage();
        }
        super.processWindowEvent(e);
    }


    private void connectToServer(String serverAddress, int serverPort) {
        try {
            socket = new Socket(serverAddress, serverPort);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error connecting to the server. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void start() {
        mainFrame = new JFrame("Chat Application");
        mainFrame.setSize(800, 600);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setAlwaysOnTop(true);

        //connect to server
        connectToServer("localhost", 8889);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainFrame.add(mainPanel);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        mainFrame.setJMenuBar(menuBar);

        JMenu actionsMenu = new JMenu("Actions");
/*
        // Add a Friend menu item
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
        JMenuItem newUserLoginItem = new JMenuItem("New User Login");
        actionsMenu.add(newUserLoginItem);
        newUserLoginItem.addActionListener(e -> {
            try {
                Login newLogin = new Login();
                newLogin.start();
            } catch (SQLException ex) {
                ex.printStackTrace();
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

        JMenuItem switchUserMenuItem = new JMenuItem("Login with another user");
        switchUserMenuItem.addActionListener(e -> {
            sendExitMessage();
            mainFrame.dispose();
            Login login;
            try {
                login = new Login();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            login.start();
        });
        actionsMenu.add(switchUserMenuItem);

        menuBar.add(actionsMenu);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.addActionListener(e -> {
            try {
                File readmeFile = Paths.get("resources", "readme.md").toFile();
                Desktop.getDesktop().open(readmeFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        helpMenu.add(aboutMenuItem);
        menuBar.add(helpMenu);

        // In the Chatroom class, inside the start() method

        JMenuItem updateProfileMenuItem = new JMenuItem("Update Profile");
        updateProfileMenuItem.addActionListener(e -> {
            UpdateProfileDialog updateProfileDialog = new UpdateProfileDialog(mainFrame, current_user, this);
            updateProfileDialog.setVisible(true);
        });
        actionsMenu.add(updateProfileMenuItem);

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
            AddUserDialog addUserDialog = new AddUserDialog(mainFrame);
            addUserDialog.setVisible(true);

            String newUser = addUserDialog.getNewUserName();
            if (newUser != null) {
                friendsListModel.addElement(newUser);
            }
        });
*/
        JButton getOnlineFriendsButton = new JButton("Get Online Friends");
        friendsPanel.add(getOnlineFriendsButton, BorderLayout.SOUTH);
        getOnlineFriendsButton.addActionListener(e -> {
            try {
                Message getOnlineFriendsMessage = new Message();
                getOnlineFriendsMessage.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
                getOnlineFriendsMessage.setSender(current_user.getUsername());
                output.writeObject(getOnlineFriendsMessage);

                Message responseMessage = (Message) input.readObject();
                if (responseMessage.getMesType().equals(MessageType.MESSAGE_RET_ONLINE_FRIEND)) {
                    List<UserConnection> onlineFriends = responseMessage.getUserList();
                    updateFriendsList(onlineFriends, current_user.getUsername());
                }
            } catch (IOException | ClassNotFoundException ex) {
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
                String displayText = user.getUsername(); // You can customize the display text to include more information about the user
                return super.getListCellRendererComponent(list, displayText, index, isSelected, cellHasFocus);
            }
        });


        JScrollPane friendsScrollPane = new JScrollPane(friendsList);
        friendsScrollPane.setPreferredSize(new Dimension(200, 0));

        JPopupMenu userInfoMenu = new JPopupMenu();
        JMenuItem showUserInfoItem = new JMenuItem("Show User Information");
        userInfoMenu.add(showUserInfoItem);

        //look up user's information
        showUserInfoItem.addActionListener(e -> {
            // Show a dialog with user information
            JDialog userInfoDialog = new JDialog(mainFrame, "User Information", true);
            userInfoDialog.setLayout(new GridLayout(6, 1));
            userInfoDialog.add(new JLabel("Username: "));
            userInfoDialog.add(new JLabel("Age: "));
            userInfoDialog.add(new JLabel("Sex: "));
            userInfoDialog.add(new JLabel("Country: "));
            userInfoDialog.add(new JLabel("City: "));
            userInfoDialog.add(new JLabel("Introduction: "));
            userInfoDialog.pack();
            userInfoDialog.setLocationRelativeTo(mainFrame);
            userInfoDialog.setVisible(true);
        });
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

        // 3. Add About the developer menu item
        JMenuItem aboutDeveloperMenuItem = new JMenuItem("About the developer");
        aboutDeveloperMenuItem.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://www.example.com"));
            } catch (IOException | URISyntaxException ex) {
                ex.printStackTrace();
            }
        });
        helpMenu.add(aboutDeveloperMenuItem);

        menuBar.add(helpMenu);

        // 4. Add User menu
        JMenu userMenu = new JMenu("User");
        JMenuItem currentUserMenuItem = new JMenuItem("Current User");
        currentUserMenuItem.addActionListener(e -> JOptionPane.showMessageDialog(mainFrame, "Username: " + current_user.getUsername() + "\nAge: " + current_user.getAge() + "\nSex: " + current_user.getSex() + "\nCountry: " + current_user.getCountry() + "\nCity: " + current_user.getCity() + "\nIntroduction: " + current_user.getIntro(), "User Information", JOptionPane.INFORMATION_MESSAGE));
        userMenu.add(currentUserMenuItem);
        menuBar.add(userMenu);

        // Chat panel
        chatWithPanel = new JPanel(new BorderLayout());
        JPanel userInfoPanel = new JPanel(new BorderLayout());
        JLabel currentUserLabel = new JLabel("Current User @" + current_user.getUsername());
        userInfoPanel.add(currentUserLabel, BorderLayout.WEST);

        chatWithLabel = new JLabel("Chat with: ");
        chatWithLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        userInfoPanel.add(chatWithLabel, BorderLayout.EAST);

        chatWithPanel.add(userInfoPanel, BorderLayout.NORTH);

        messagePane = new JTextPane();
        messagePane.setEditable(false);
        messageScrollPane = new JScrollPane(messagePane);
        chatWithPanel.add(messageScrollPane, BorderLayout.CENTER);

        JPanel typingPanel = new JPanel(new BorderLayout());
        messageField = new JTextArea(3, 50);
        JScrollPane typingScrollPane = new JScrollPane(messageField);
        typingPanel.add(typingScrollPane, BorderLayout.CENTER);

        JButton submitButton = new JButton("Submit");
        typingPanel.add(submitButton, BorderLayout.EAST);
        submitButton.addActionListener(e -> submitMessage());
        submitButton.setPreferredSize(new Dimension(80, 40));
        //Add a keyboard shortcut "ctrl + Enter" for submitButton
        submitButton.registerKeyboardAction(e -> submitMessage(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
//        submitButton.registerKeyboardAction(e -> submitMessage(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        JButton sendFilesButton = new JButton("Files");
        sendFilesButton.setPreferredSize(new Dimension(80, 20));
        sendFilesButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(mainFrame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                // Perform file handling operations here
            }
        });

        JButton emojiButton = new JButton("Emoji");
        emojiButton.setPreferredSize(new Dimension(80, 20));
        // Add actionListener for emojiButton here

        JButton historyButton = new JButton("History");
        historyButton.setPreferredSize(new Dimension(80, 20));
        historyButton.addActionListener(e -> {
            JFrame historyFrame = new JFrame("History");
            historyFrame.setAlwaysOnTop(true);
            historyFrame.setSize(300, 600);
            historyFrame.setLocationRelativeTo(mainFrame);
            historyFrame.setVisible(true);
        });

        Dimension buttonSize = new Dimension(80, 20);
        sendFilesButton.setMaximumSize(buttonSize);
        emojiButton.setMaximumSize(buttonSize);
        historyButton.setMaximumSize(buttonSize);

        JPanel leftButtonPanel = new JPanel();
        leftButtonPanel.setLayout(new BoxLayout(leftButtonPanel, BoxLayout.Y_AXIS));
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
        String formattedMessage = "<font color=\"blue\"><b>" + current_user.getUsername() + " [" + now.format(formatter) + "]:</b></font> " + message.replace("\n", "<br>") + "<br>";
        chatPane.setContentType("text/html");
        String currentText = chatPane.getText().replaceAll("</body>", "").replaceAll("</html>", "");
        chatPane.setText(currentText + formattedMessage + "</body></html>");

        messageField.setText("");
        messageField.setLineWrap(true);
        messageField.setWrapStyleWord(true);
    }


@Deprecated
    static class AddUserDialog extends JDialog {
        private final JTextField userNameField;
        private final JTextField ipAddressField;
        private final JTextField portField;
        private final JButton addButton;
        private String newUserName;

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
                newUserName = userNameField.getText().trim();
                dispose();
            });

            pack();
            setLocationRelativeTo(owner);
        }

        public String getNewUserName() {
            return newUserName;
        }
    }

    static class UpdateProfileDialog extends JDialog {
        private final JTextField ageField;
        private final JTextField sexField;
        private final JTextField countryField;
        private final JTextField cityField;
        private final JTextField introField;
        private final JButton updateButton;
        private final User currentUser;
        private final Chatroom mainClassReference;

        public UpdateProfileDialog(JFrame owner, User currentUser, Chatroom mainClassReference) {
            super(owner, "Update Profile", false);
            setLayout(new GridLayout(6, 2));

            this.currentUser = currentUser;

            ageField = new JTextField(10);
            sexField = new JTextField(10);
            countryField = new JTextField(10);
            cityField = new JTextField(10);
            introField = new JTextField(10);
            updateButton = new JButton("Update");

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
            this.mainClassReference = mainClassReference;

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
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error updating user information. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendExitMessage() {
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
        friendsListModel.clear();
        for (UserConnection userConnection : onlineFriends) {
            User friend = userConnection.getUser();
            if (!friend.getUsername().equals(currentUser)) {
                friendsListModel.addElement(friend);
            }
        }
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

    private void updateUserConnections(String[] onlineFriends) {
        userConnections.clear();
        for (String friend : onlineFriends) {
            String[] friendInfo = friend.split(":");
            String username = friendInfo[0];
            int age = Integer.parseInt(friendInfo[1]);
            String sex = friendInfo[2];
            String email = friendInfo[3];
            String country = friendInfo[4];
            String city = friendInfo[5];
            String intro = friendInfo[6];
            String ipAddress = friendInfo[7];
            int port = Integer.parseInt(friendInfo[8]);

            User user = new User(username, "", age, sex, email, country, city, intro);
            userConnections.add(new UserConnection(user, ipAddress, port));
        }
    }
}


