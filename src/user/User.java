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
    private HashMap<User, List<Message>> messages;

    public User(String name) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            this.publicKey = keyPair.getPublic();
            this.privateKey = keyPair.getPrivate();
            this.name = name;
            this.messages = new HashMap<User, List<Message>>();
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

            byte[] test = Base64.getDecoder().decode(encryptedMessage);

            System.out.println(test.length);
            System.out.println(encryptedMessageBytes.length);
            for (int i = 0; i < encryptedMessageBytes.length; i++) {
                byte a = test[i];
                byte b = encryptedMessageBytes[i];

                if (a != b) {
                    System.out.println("DNE: " + i);
                }
            }

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
            System.out.println(string);
            byte[] stringBytes = Base64.getDecoder().decode(string);
            byte[] decryptedMessageBytes = decryptCipher.doFinal(stringBytes);
            String decryptedMessage = new String(decryptedMessageBytes, StandardCharsets.UTF_8);

            return decryptedMessage;
        } catch (Exception e) {
            System.out.println("hello");
            e.printStackTrace();
            return null;
        }
    }

    private void saveMessage(Message message, User sender) {
        if (!messages.containsKey(sender)) {
            messages.put(sender, new ArrayList<Message>());
        }

        messages.get(sender).add(message);
    }

    public void recieveMessage(EncryptedMessage message) {
        User sender = message.getSender();
        String signature = message.getSignature();
        String expectedSignature = message.getExpectedSignature();
        System.out.println("decrypting actual signature");
        System.out.println(sender.name);
        String actualSignature = decryptString(signature, sender.getPublicKey());
        System.out.println(actualSignature);
        System.out.println("decrypting messagea contents");
        String decryptedMessageContents = decryptString(message.message, privateKey);
        System.out.println(decryptedMessageContents);
        Message decryptedMessage;

        if (actualSignature.equals(expectedSignature)) {
            // Sender is who they say they are
            decryptedMessage = new DecryptedSignedMessage(decryptedMessageContents, sender);
            saveMessage(decryptedMessage, sender);
        } else {
            // Sender cannot be confirmed
            decryptedMessage = new DecryptedUnsignedMessage(decryptedMessageContents);
            saveMessage(decryptedMessage, null);
        }
    }

    private void sendMessage(User recipient, String message, String signature, String expectedSignature) {
        String encryptedContents = encryptString(message, recipient.getPublicKey());
        EncryptedMessage encryptedMessage = new EncryptedMessage(encryptedContents, this, signature, expectedSignature);
        recipient.recieveMessage(encryptedMessage);
    }

    public void sendSignedMessage(User recipient, String message) {
        String expectedSignature = recipient.name;
        String signature = encryptString(expectedSignature, privateKey);
        sendMessage(recipient, message, signature, expectedSignature);
    }

    public void sendUnsignedMessage(User recipient, String message) {
        sendMessage(recipient, message, "", "Unsigned");
    }


    public void printMessages() {
        for (User sender : messages.keySet()) {
            System.out.println(sender.name + "\n-------------------\n");
            List<Message> messageChain = messages.get(sender);

            for (int i = 0; i < messageChain.size(); i++) {
                System.out.println(messageChain.get(i).getMessage());
            }

            System.out.println("\n");
        }
    }
}
