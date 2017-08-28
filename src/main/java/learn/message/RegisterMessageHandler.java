package learn.message;

public class RegisterMessageHandler implements MessageHandler {

    @Override
    public void process(Message message) {
        System.out.println("processRegister - " + message.getMessage());
    }
}
