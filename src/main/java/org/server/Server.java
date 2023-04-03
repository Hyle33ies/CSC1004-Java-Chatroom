package org.server;

import org.client.tools.MessageType;
import org.client.tools.Message;
import org.client.tools.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int PORT = 8889;
    private Connection connection;
    private Map<String, UserConnection> connectedUsers;
    private ExecutorService threadPool;

    public static void main(String[] args) {
        new Server().start();
    }

    public void start() {
        connectedUsers = new ConcurrentHashMap<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatroom_users", "root", "@Frankett2004");
            threadPool = Executors.newCachedThreadPool();
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("IP: " + socket.getInetAddress().getHostAddress() + " port: " + socket.getPort() + " trying to connect");
                threadPool.execute(new ClientHandler(socket));
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

                if (message.getMesType().equals(MessageType.MESSAGE_LOGIN_ATTEMPT)) {
                    String username = message.getSender();
                    String password = message.getContent();

                    boolean loginResult = checkUser(username, password);
                    Message responseMessage = new Message();

                    if (loginResult) {
                        responseMessage.setMesType(MessageType.MESSAGE_LOGIN_SUCCESSFUL);
                        User user = new User();
                        user.setUsername(username);
                        connectedUsers.put(username, new UserConnection(user, socket.getInetAddress().getHostAddress(), socket.getPort()));
                    } else {
                        responseMessage.setMesType(MessageType.MESSAGE_LOGIN_FAILURE);
                    }

                    outputStream.writeObject(responseMessage);
                } else if (message.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)) {
                    String username1 = message.getSender();
                    connectedUsers.remove(username1);
                    System.out.println("User '" + username1 + "' exited.");
                }
                // Here you can add more message types for multi-user chat and file transmission.

            } catch (IOException | ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (outputStream != null) outputStream.close();
                    if (inputStream != null) inputStream.close();
                    if (socket != null) socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private boolean checkUser(String username, String password) throws SQLException {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();
        }
    }
}

class UserConnection {
    private User user;
    private String ipAddress;
    private int port;

    public UserConnection(User user, String ipAddress, int port) {
        this.user = user;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
