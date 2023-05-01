import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("SHA-256");
        System.out.println(keyPairGen);
    }
}
