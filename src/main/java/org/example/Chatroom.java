package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Chatroom{

    private JFrame mainFrame;
    private JList<String> friendsList;
    private DefaultListModel<String> friendsListModel;
    private JScrollPane messageScrollPane;
    private JTextPane messagePane;
    private JTextArea messageField;
    private JLabel chatWithLabel;
    private JPanel chatWithPanel;
    private Map<String, JTextPane> userChatPanes;


    protected void start() {
        mainFrame = new JFrame("Chat Application");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);
        mainFrame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainFrame.add(mainPanel);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        mainFrame.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutMenuItem = new JMenuItem("About");
        helpMenu.add(aboutMenuItem);
        menuBar.add(helpMenu);

        // Friends list
        friendsListModel = new DefaultListModel<>();
        friendsListModel.addElement("User1");
        friendsListModel.addElement("User2");
        friendsListModel.addElement("User3");

        friendsList = new JList<>(friendsListModel);
        friendsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        friendsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    // Show chat panel with selected user
                    String selectedUser = friendsList.getSelectedValue();
                    chatWithLabel.setText("Chat with: " + selectedUser);
                    showChatPaneForUser(selectedUser);
                }
            }
        });

        JScrollPane friendsScrollPane = new JScrollPane(friendsList);
        friendsScrollPane.setPreferredSize(new Dimension(200, 0));
        mainPanel.add(friendsScrollPane, BorderLayout.WEST);

        // Chat panel
        chatWithPanel = new JPanel(new BorderLayout());
        chatWithLabel = new JLabel("Chat with: ");
        chatWithLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        chatWithPanel.add(chatWithLabel, BorderLayout.NORTH);

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

        chatWithPanel.add(typingPanel, BorderLayout.SOUTH);

        mainPanel.add(chatWithPanel, BorderLayout.CENTER);

        // Initialize userChatPanes
        userChatPanes = new HashMap<>();

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

        String formattedMessage = "<font color=\"blue\"><b>admin [" + now.format(formatter) + "]:</b></font> " + message + "<br>";

        chatPane.setContentType("text/html");
        String currentText = chatPane.getText().replaceAll("</body>", "").replaceAll("</html>", "");
        chatPane.setText(currentText + formattedMessage + "</body></html>");

        messageField.setText("");
    }
}


