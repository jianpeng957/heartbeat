package learn.test;

import learn.message.ConvergeMessageHandler;
import learn.util.RequestCode;

import java.io.IOException;

public class ConvergeMessageHandlerTest {
    public static void main(String[] args) throws IOException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        ConvergeMessageHandler convergeMessageHandler = ConvergeMessageHandler.getInstance();
        ConvergeMessageHandler.getMessageHandler(RequestCode.HEARTBEAT);
    }
}
