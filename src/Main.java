import user.User;

public class Main {
    public static void main(String[] args) {
        User bob = new User("Bob");
        User alice = new User("Alice");

        bob.sendSignedMessage(alice, "Hello this is a test");
        alice.printMessages();
    }
}
