import user.User;

public class Main {
    public static void main(String[] args) {
        User bob = new User("Bob");
        User alice = new User("Alice");

        bob.sendSignedMessage(alice, "Hello this is a test");
        bob.sendSignedMessage(alice, "How are you doing?");
        bob.sendUnsignedMessage(alice, "Who is this?");

        alice.sendSignedMessage(bob, "Hey Bob, its alice!");

        alice.printMessages();
        System.out.println("\n");
        bob.printMessages();
    }
}
