import java.security.*;
import java.util.HashMap;
import java.util.List;

public class User {
    private String name;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private HashMap<User, List<Message>> messages;

    public User(String name) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        this.name = name;
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
        this.messages = new HashMap<User, List<Message>>();
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public String getName() {
        return this.name;
    }
}
