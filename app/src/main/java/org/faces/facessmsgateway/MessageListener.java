package org.faces.facessmsgateway;

public interface MessageListener {
    /**
     * To call this method when new message received and send back
     * @param message Message
     */
    void messageReceived(String sender,String from,String body,String display_message,int time,String message);
}
