package learn.message;

public class NormalMessageHandler implements MessageHandler {

    @Override
    public void process(Message message) {
        System.out.println("processHandle - " + message.getMessage());
    }
}
