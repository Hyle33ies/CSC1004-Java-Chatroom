package org.client.tools;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * User: HP
 * Date: 2023/3/23
 * WELCOME!
 */
@SuppressWarnings("All")
public class Message implements Serializable {
    public static final long serialVersionUID = 1L;
    private String sender;
    private String getter;
    private String content;
    private String mesType = MessageType.MESSAGE_LOGIN_FAILURE;
    private User user;
    private ArrayList<UserConnection> userList;
    private String sendTime;
    private String fileName;
    public boolean isImage = false;
    private String FileExtension;

    public String getFileExtension() {
        return FileExtension;
    }

    public void setFileExtension(String fileExtension) {
        FileExtension = fileExtension;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getGetter() {
        return getter;
    }

    public void setGetter(String getter) {
        this.getter = getter;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMesType() {
        return mesType;
    }

    public void setMesType(String mesType) {
        this.mesType = mesType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<UserConnection> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<UserConnection> userList) {
        this.userList = userList;
    }

}
