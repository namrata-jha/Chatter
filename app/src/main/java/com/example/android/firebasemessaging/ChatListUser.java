package com.example.android.firebasemessaging;

public class ChatListUser extends MessageUser {

    private long lastMessageTime;
    private String lastMessage;
    private boolean unreadMessages;
    private boolean online;

    public ChatListUser(MessageUser messageUser, long lastMessageTime, String lastMessage, boolean unreadMessages, boolean online) {
        super(messageUser.getUserName(), messageUser.getUserID(), messageUser.getEmailID());
        this.lastMessageTime = lastMessageTime;
        this.lastMessage = lastMessage;
        this.unreadMessages = unreadMessages;
        this.online = online;
    }

    public ChatListUser(MessageUser messageUser, long lastMessageTime, String lastMessage, boolean unreadMessages) {
        super(messageUser.getUserName(), messageUser.getUserID(), messageUser.getEmailID());
        this.lastMessageTime = lastMessageTime;
        this.lastMessage = lastMessage;
        this.unreadMessages = unreadMessages;
        online = false;
    }


    public ChatListUser(){

    }

    public ChatListUser(MessageUser messageUser, long lastMessageTime, String lastMessage){
        super(messageUser.getUserName(), messageUser.getUserID(), messageUser.getEmailID());
        this.lastMessageTime = lastMessageTime;
        this.lastMessage = lastMessage;
        unreadMessages = false;
        online = false;
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

    public boolean getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(boolean unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
