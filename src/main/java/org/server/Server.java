package org.server;

import org.client.tools.MessageType;
import org.client.tools.User;
import org.client.tools.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
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
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("Server started on port " + PORT);

                while (true) {
                    Socket socket = serverSocket.accept();
                    System.out.println("IP: " + socket.getInetAddress().getHostAddress() + " port: " + socket.getPort() + " trying to connect");
                    threadPool.execute(new ClientHandler(socket));
                }
            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    class ClientHandler implements Runnable {
        private Socket socket;
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

                while (message != null) {
                    if (message.getMesType().equals(MessageType.MESSAGE_LOGIN_ATTEMPT)) {
                        String username = message.getSender();
                        String password = message.getContent();
                        User user;
                        Message responseMessage = new Message();
                        if ((user = checkUser(username, password)) != null) {
                            responseMessage.setMesType(MessageType.MESSAGE_LOGIN_SUCCESSFUL);
                            responseMessage.setUser(user);
                            connectedUsers.add(new UserConnection(user, socket.getInetAddress().getHostAddress(), socket.getPort()));
                        } else {
                            responseMessage.setMesType(MessageType.MESSAGE_LOGIN_FAILURE);
                        }
                        outputStream.writeObject(responseMessage);
                    } else if (message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)) {
                        sendOnlineFriendsList(message.getSender(), outputStream);
                    }
                    message = (Message) inputStream.readObject();
                }

                String username = message.getSender();
                connectedUsers.removeIf(userConnection -> userConnection.getUser().getUsername().equals(username));
                System.out.println("User '" + username + "' exited.");

            } catch (IOException | ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (outputStream != null) outputStream.close();
                    if (inputStream != null) if (outputStream != null) {
                        outputStream.close();
                    }
                    if (inputStream != null) inputStream.close();
                    if (socket != null) socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendOnlineFriendsList(String requesterUsername, ObjectOutputStream outputStream) throws IOException {
        Message onlineFriendsMessage = new Message();
        onlineFriendsMessage.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);

        ArrayList<UserConnection> onlineFriends = new ArrayList<>(connectedUsers);
        onlineFriends.removeIf(userConnection -> userConnection.getUser().getUsername().equals(requesterUsername));

        onlineFriendsMessage.setUserList(onlineFriends);
        outputStream.writeObject(onlineFriendsMessage);
    }

    private User checkUser(String username, String password) throws SQLException {
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
            return user;
        }

        return null;
    }
}
