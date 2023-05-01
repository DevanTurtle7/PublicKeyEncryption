public class Message {
    public String messagePlaintext;
    public String signaturePlaintext;

    public Message(String messagePlaintext, String signaturePlaintext)  {
        this.messagePlaintext = messagePlaintext;
        this.signaturePlaintext = signaturePlaintext;
    }
}
