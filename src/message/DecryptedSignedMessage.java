package message;

import user.User;

public class DecryptedSignedMessage extends Message {
    private User sender;

    public DecryptedSignedMessage(String message, User sender) {
        super(message);

        this.sender = sender;
    }

    public User getSender() {
        return this.sender;
    }
}
