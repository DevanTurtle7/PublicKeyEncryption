package message;

public abstract class Message {
    public String message;

    public Message(String message)  {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
