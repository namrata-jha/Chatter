package com.example.android.firebasemessaging;

public class ChatListUser extends MessageUser {

    private long lastMessageTime;
    private String lastMessage;

    public ChatListUser(String userName, String userID, String emailID, long lastMessageTime, long lastMessageTime1, String lastMessage) {
        super(userName, userID, emailID);
        this.lastMessageTime = lastMessageTime1;
        this.lastMessage = lastMessage;
    }

    public ChatListUser(long lastMessageTime, String lastMessage) {
        this.lastMessageTime = lastMessageTime;
        this.lastMessage = lastMessage;
    }

    public ChatListUser(String userName, String userID, String emailID, long lastMessageTime, String lastMessage) {
        super(userName, userID, emailID);
        this.lastMessageTime = lastMessageTime;
        this.lastMessage = lastMessage;
    }

    public ChatListUser(){

    }

    public ChatListUser(MessageUser messageUser, long lastMessageTime, String lastMessage){
        super(messageUser.getUserName(), messageUser.getUserID(), messageUser.getEmailID());
        this.lastMessageTime = lastMessageTime;
        this.lastMessage = lastMessage;
    }


    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void reverseTime(){
        lastMessageTime = -1* lastMessageTime;
    }
}
