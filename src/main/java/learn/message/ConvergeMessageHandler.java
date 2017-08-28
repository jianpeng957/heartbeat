package learn.message;

import learn.util.RequestCode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConvergeMessageHandler {

    private static Map<Integer, String> convergeMessageHandler;

    private static String SUFFIX = "MessageHandler";
    private static String PREFIX = "learn.message.";
    private static ConvergeMessageHandler instance;

    private ConvergeMessageHandler() throws IOException {
        convergeMessageHandler = new HashMap<Integer, String>();
        initial();
    }

    public static ConvergeMessageHandler getInstance() throws IOException {
        if (instance == null) {
            synchronized (ConvergeMessageHandler.class) {
                instance = new ConvergeMessageHandler();
            }
        }

        return instance;
    }

    public void initial() {
        convergeMessageHandler.put(RequestCode.NORMAL, "Normal");
        convergeMessageHandler.put(RequestCode.REGISTER, "Register");
        convergeMessageHandler.put(RequestCode.HEARTBEAT, "Heartbeat");
    }

    public static MessageHandler getMessageHandler(int requestCode) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String messageHandlerClass = convergeMessageHandler.get(requestCode);
        if (messageHandlerClass == null)
            return null;

        String messageHandlerClassName = PREFIX + messageHandlerClass  + SUFFIX;
        return (MessageHandler) Class.forName(messageHandlerClassName).newInstance();
    }
}
