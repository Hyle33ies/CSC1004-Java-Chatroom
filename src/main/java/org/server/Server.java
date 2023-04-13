package org.server;

import org.client.tools.MessageType;
import org.client.tools.User;
import org.client.tools.Message;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int PORT = 8889;
    private Connection connection;
    private List<UserConnection> connectedUsers;
    private ExecutorService threadPool;

    public static void main(String[] args) {
        new Server().start();
    }

    public void start() {
        connectedUsers = Collections.synchronizedList(new ArrayList<>());
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatroom_users", "root", "@Frankett2004");
            threadPool = Executors.newCachedThreadPool();
            // Preparations for socket programming and multi-thread programming.
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("Server started on port " + PORT); // All System output in server side is Debug info!
                while (true) {
                    Socket socket = serverSocket.accept();
                    // New user connected
                    System.out.println("IP: " + socket.getInetAddress().getHostAddress() + " port: " + socket.getPort() + " trying to connect");
                    threadPool.execute(new ClientHandler(socket));
                    System.out.println("Client connected.");
                }
            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    class ClientHandler implements Runnable {
        private final Socket socket;
        private ObjectOutputStream outputStream;
        private ObjectInputStream inputStream;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());

                Message message = (Message) inputStream.readObject();
                try {
                    while (true) {
                        //Continue receiving messages
                        System.out.println("Message received: " + message.getMesType());
                        switch (message.getMesType()) {
                            case MessageType.MESSAGE_LOGIN_ATTEMPT -> {
                                String username = message.getSender();
                                String password = message.getContent();
                                //TODO To check whether this user has already login
                                User user;
                                Message responseMessage = new Message();
                                if ((user = checkUser(username, password)) != null) {
                                    responseMessage.setMesType(MessageType.MESSAGE_LOGIN_SUCCESSFUL);
                                    responseMessage.setUser(user);
                                    connectedUsers.add(new UserConnection(user, socket.getInetAddress().getHostAddress(), socket.getPort(), outputStream));
                                    System.out.println(connectedUsers);
                                } else {
                                    responseMessage.setMesType(MessageType.MESSAGE_LOGIN_FAILURE);
                                }
                                outputStream.writeObject(responseMessage);
                            }
                            case MessageType.MESSAGE_GET_ONLINE_FRIEND ->
                                    sendOnlineFriendsList(message.getSender(), outputStream);
                            case MessageType.MESSAGE_CLIENT_EXIT -> {
                                // User exit
                                Message finalMessage = message;
                                connectedUsers.removeIf(userConnection -> userConnection.getUser().getUsername().equals(finalMessage.getSender()));
                                System.out.println(connectedUsers);
                            }
                            case MessageType.MESSAGE_COMM_MES -> {
                                System.out.println("This Message is for: " + message.getGetter());
                                UserConnection targetUserConnection = null;
                                for (UserConnection userConnection : connectedUsers) {
                                    if (userConnection.getUser().getUsername().equals(message.getGetter())) {
                                        targetUserConnection = userConnection;
                                        break;
                                    }
                                }
                                System.out.println(targetUserConnection.toString());
                                if (targetUserConnection != null) {
                                    try {
                                        ObjectOutputStream targetOutputStream = targetUserConnection.getOutputStream();
                                        targetOutputStream.writeObject(message);
                                        System.out.println("Send Successfully");
                                        targetOutputStream.flush();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    System.out.println("Target user not found or not online.");
                                }
                            }
                            case MessageType.MESSAGE_UPDATE_PORT -> {
                                String username = message.getSender();
                                int updatedPort = Integer.parseInt(message.getContent());
                                updatePort(username, updatedPort);
                            }
                            case MessageType.MESSAGE_UPDATE_OUTPUT_STREAM -> {
                                updateOutputStream(message.getSender(), outputStream);
                            }
                        }
                        message = (Message) inputStream.readObject();
                    }
                } catch (EOFException | SocketException e) {
                    // Connection was closed or reset; handle cleanup here
                    System.out.println("Client disconnected.");
                } catch (IOException | ClassNotFoundException e) {
                    // Other errors occurred while reading data
                    e.printStackTrace();
                }

            } catch (IOException | ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            } finally {
//                try {
//                    if (outputStream != null) outputStream.close();
//                    if (inputStream != null) if (outputStream != null) {
//                        outputStream.close();
//                    }
//                    if (inputStream != null) inputStream.close();
//                    if (socket != null) socket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }

    private void sendOnlineFriendsList(String requesterUsername, ObjectOutputStream outputStream) throws IOException {
        Message onlineFriendsMessage = new Message();
        onlineFriendsMessage.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);

        // Send back the list except the user him/herself
        ArrayList<UserConnection> onlineFriends = new ArrayList<>(connectedUsers);
        onlineFriends.removeIf(userConnection -> userConnection.getUser().getUsername().equals(requesterUsername));

        System.out.println("Online friends: " + onlineFriends);
        onlineFriendsMessage.setUserList(onlineFriends);
        outputStream.writeObject(onlineFriendsMessage);
    }

    private User checkUser(String username, String password) throws SQLException {
        // To check whether the username & password combine rightly
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, username);
        statement.setString(2, password);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            User user = new User();
            user.setUsername(resultSet.getString("username"));
            user.setPasswd(resultSet.getString("password"));
            user.setAge(resultSet.getInt("age"));
            user.setSex(resultSet.getString("sex"));
            user.setEmail(resultSet.getString("email"));
            user.setCountry(resultSet.getString("country"));
            user.setCity(resultSet.getString("city"));
            user.setIntro(resultSet.getString("introduction"));
            System.out.println("User Verified: " + user);
            // If so, return the user
            return user;
        }
        return null;
    }
    private void updatePort(String username, int updatedPort) {
        for (UserConnection userConnection : connectedUsers) {
            if (userConnection.getUser().getUsername().equals(username)) {
                userConnection.setPort(updatedPort);
                break;
            }
        }
    }
    private void updateOutputStream(String username, ObjectOutputStream updatedOutputStream) {
        for (UserConnection userConnection : connectedUsers) {
            if (userConnection.getUser().getUsername().equals(username)) {
                userConnection.setOutputStream(updatedOutputStream);
                break;
            }
        }
    }

}
