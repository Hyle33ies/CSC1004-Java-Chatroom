package org.example;

import org.example.tools.Message;
import org.example.tools.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Chatroom {

    private JFrame mainFrame;
    private JList<String> friendsList;
    private DefaultListModel<String> friendsListModel;
    private JScrollPane messageScrollPane;
    private JTextPane messagePane;
    private JTextArea messageField;
    private JLabel chatWithLabel;
    private JPanel chatWithPanel;
    private Map<String, JTextPane> userChatPanes;
    private final User current_user;


    public Chatroom(User current_user) {
        this.current_user = current_user;
    }

    protected void start() {
        mainFrame = new JFrame("Chat Application");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setAlwaysOnTop(true);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainFrame.add(mainPanel);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        mainFrame.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("Actions");

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
        fileMenu.add(addFriendMenuItem);

        // Exit menu item
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(mainFrame, "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);

        JMenuItem switchUserMenuItem = new JMenuItem("Login with another user");
        switchUserMenuItem.addActionListener(e -> {
            mainFrame.dispose();
            Login login = new Login();
            login.start();
        });
        fileMenu.add(switchUserMenuItem);

        menuBar.add(fileMenu);

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

        // Friends list panel
        JPanel friendsListPanel = new JPanel(new BorderLayout());
        mainPanel.add(friendsListPanel, BorderLayout.WEST);

        // Friends list title
        JPanel friendsPanel = new JPanel(new BorderLayout());
        JLabel friendsListTitle = new JLabel("FRIENDS");
        friendsListTitle.setHorizontalAlignment(SwingConstants.CENTER);
        friendsPanel.add(friendsListTitle, BorderLayout.NORTH);

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


        // Friends list
        friendsListModel = new DefaultListModel<>();
        friendsListModel.addElement("User1");
        friendsListModel.addElement("User2");
        friendsListModel.addElement("User3");

        friendsList = new JList<>(friendsListModel);
        friendsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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
                    String selectedUser = friendsList.getSelectedValue();
                    chatWithLabel.setText("Chat with: " + selectedUser);
                    showChatPaneForUser(selectedUser);
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
        currentUserMenuItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(mainFrame, "Username: " + current_user.getUsername() + "\nAge: " + current_user.getAge() + "\nSex: " + current_user.getSex() + "\nCountry: " + current_user.getCountry() + "\nCity: " + current_user.getCity() + "\nIntroduction: " + current_user.getIntro(), "User Information", JOptionPane.INFORMATION_MESSAGE);
        });
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
                    System.exit(0);
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

        String selectedUser = friendsList.getSelectedValue();
        JTextPane chatPane = userChatPanes.get(selectedUser);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String sendTime = now.format(formatter);

        // Create a Message object
        Message msg = new Message();
        msg.setSender(current_user.getUsername());
        msg.setGetter(selectedUser);
        msg.setContent(message);
        msg.setSendTime(sendTime);
        msg.setMesType("3");
        String formattedMessage = "<font color=\"blue\"><b>admin [" + now.format(formatter) + "]:</b></font> " + message.replace("\n", "<br>") + "<br>";
        chatPane.setContentType("text/html");
        String currentText = chatPane.getText().replaceAll("</body>", "").replaceAll("</html>", "");
        chatPane.setText(currentText + formattedMessage + "</body></html>");

        messageField.setText("");
        messageField.setLineWrap(true);
        messageField.setWrapStyleWord(true);
    }

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

}


