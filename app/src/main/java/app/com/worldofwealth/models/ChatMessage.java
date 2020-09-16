package app.com.worldofwealth.models;

public class ChatMessage {
    public boolean fromme;
    public String message,date;


    public ChatMessage(boolean fromme, String message, String date) {
        super();
        this.fromme = fromme;
        this.message = message;
        this.date = date;
    }
}
