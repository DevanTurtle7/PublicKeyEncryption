package message;

import user.User;

public class EncryptedMessage extends Message {
    private User sender;
    private String signature;
    private String expectedSignature;

    public EncryptedMessage(String message, User sender, String signature, String expectedSignature) {
        super(message);

        this.sender = sender;
        this.signature = signature;
        this.expectedSignature = expectedSignature;
    }

    public User getSender() {
        return this.sender;
    }

    public String getSignature() {
        return this.signature;
    }

    public String getExpectedSignature() {
        return this.expectedSignature;
    }
}
