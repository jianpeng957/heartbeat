package learn;

import learn.heartbeat.HeartbeatHandler;
import learn.message.ConvergeMessageHandler;
import learn.message.Message;
import learn.message.MessageHandler;
import learn.util.GsonUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientThread implements Runnable {

    private Socket socket;
    private HeartbeatHandler heartbeatHandler;

    public ClientThread(Socket socket) throws IOException {
        this.socket = socket;
        this.heartbeatHandler = HeartbeatHandler.getInstance();
    }

    @Override
    public void run() {
        String key = String.valueOf(socket.getInetAddress().getHostAddress() + ":" +socket.getPort());
        BufferedReader inputStream = null;
        try {
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
            String line = null;
            Message message = null;
            while ((line = inputStream.readLine()) != null) {
                System.out.println("receive message: " + line);
                message = GsonUtils.parseJsonToObject(line, Message.class);
                process(message);

                heartbeatHandler.putHeartbeat(key, System.currentTimeMillis()); // 每个发送过来的包都视为心跳包？// 或者将 heartbeatHandler 传递给每个 HeartbeatHandler？
                System.out.println(key + " - " + message.getMessageCode() + " : " + message.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println(key + " - terminated");
    }

    private void process(Message message) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        MessageHandler messageHandler = ConvergeMessageHandler.getMessageHandler(message.getMessageCode());
        if (messageHandler != null)
            messageHandler.process(message);
    }
}
