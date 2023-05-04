package user;

import message.DecryptedSignedMessage;
import message.DecryptedUnsignedMessage;
import message.EncryptedMessage;
import message.Message;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

public class User {
    private String name;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private List<Message> unsignedMessages;
    private HashMap<User, List<Message>> signedMessages;

    public User(String name) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            this.publicKey = keyPair.getPublic();
            this.privateKey = keyPair.getPrivate();
            this.name = name;
            this.signedMessages = new HashMap<User, List<Message>>();
            this.unsignedMessages = new ArrayList<Message>();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public String getName() {
        return this.name;
    }

    private String encryptString(String string, Key key) {
        try {
            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] stringBytes = string.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedMessageBytes = encryptCipher.doFinal(stringBytes);
            String encryptedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes);

            return encryptedMessage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String decryptString(String string, Key key) {
        try {
            Cipher decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, key);
            byte[] stringBytes = Base64.getDecoder().decode(string);
            byte[] decryptedMessageBytes = decryptCipher.doFinal(stringBytes);
            String decryptedMessage = new String(decryptedMessageBytes, StandardCharsets.UTF_8);

            return decryptedMessage;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }

    private void saveMessage(DecryptedSignedMessage message) {
        User sender = message.getSender();

        if (!signedMessages.containsKey(sender)) {
            signedMessages.put(sender, new ArrayList<Message>());
        }

        signedMessages.get(sender).add(message);
    }

    public void receiveMessage(EncryptedMessage message) {
        User sender = message.getSender();
        String decryptedMessageContents = decryptString(message.message, privateKey);
        String signature = message.getSignature();
        String expectedSignature = message.getExpectedSignature();

        try {
            String actualSignature = decryptString(signature, sender.getPublicKey());

            if (actualSignature != null && actualSignature.equals(expectedSignature)) {
                // Sender is who they say they are
                DecryptedSignedMessage decryptedMessage = new DecryptedSignedMessage(decryptedMessageContents, sender);
                saveMessage(decryptedMessage);
                return;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }

        // Sender cannot be confirmed
        Message decryptedMessage = new DecryptedUnsignedMessage(decryptedMessageContents);
        unsignedMessages.add(decryptedMessage);
    }

    private void sendMessage(User recipient, String message, String signature, String expectedSignature) {
        String encryptedContents = encryptString(message, recipient.getPublicKey());
        EncryptedMessage encryptedMessage = new EncryptedMessage(encryptedContents, this, signature, expectedSignature);
        recipient.receiveMessage(encryptedMessage);
    }

    public void sendSignedMessage(User recipient, String message) {
        String expectedSignature = recipient.name;
        String signature = encryptString(expectedSignature, privateKey);
        sendMessage(recipient, message, signature, expectedSignature);
    }

    public void sendUnsignedMessage(User recipient, String message) {
        sendMessage(recipient, message, "empty", "Unsigned");
    }


    public void printMessages() {
        for (User sender : signedMessages.keySet()) {
            System.out.println(sender.name + "\n-------------------\n");
            List<Message> messageChain = signedMessages.get(sender);

            for (int i = 0; i < messageChain.size(); i++) {
                System.out.println(messageChain.get(i).getMessage());
            }

            System.out.println("\n");
        }

        System.out.println("Unsigned messages\n----------------\n");
        for (Message message : unsignedMessages) {
            System.out.println(message.getMessage());
        }
    }
}
