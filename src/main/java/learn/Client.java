package learn;

import learn.heartbeat.HeartbeatEntity;
import learn.message.Message;
import learn.util.GsonUtils;
import learn.util.RequestCode;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    private ExecutorService heartbeatExecutorService = Executors.newSingleThreadExecutor();
    private Socket socket;

    private int port;
    private String hostname;
    private InetSocketAddress inetSocketAddress;

    private final String SPLIT_SYMBOL = "\n";

    private PrintWriter printWriter; // 输出流

    public Client(String hostname, int port) throws IOException {
        this.socket = new Socket();
        this.inetSocketAddress = new InetSocketAddress(hostname, port);
        this.socket.connect(inetSocketAddress); // 根据服务端地址绑定
        this.printWriter = new PrintWriter(socket.getOutputStream());

        this.initHeartbeat();
    }

    public void sendMessage(Message message) {
        String json = GsonUtils.parseObjectToJson(message);
        printWriter.write(json + SPLIT_SYMBOL);
        printWriter.flush();
    }

    public String getHostAddress() {
        return socket.getInetAddress().getHostAddress();
    }

    public String getPort() {
        return String.valueOf(socket.getLocalPort());
    }

    private void initHeartbeat() {
        heartbeatExecutorService.execute(() -> {
            while (true) {
                Message heartbeatMessage = new Message();
                HeartbeatEntity heartbeatEntity = new HeartbeatEntity();
                heartbeatEntity.setSendTime(System.currentTimeMillis());
                heartbeatEntity.setHeartbeatId(getPort());
                heartbeatMessage.setMessageCode(RequestCode.HEARTBEAT);
                heartbeatMessage.setMessage(GsonUtils.parseObjectToJson(heartbeatEntity));
                sendMessage(heartbeatMessage);
                System.out.println("send a heartbeat message");

                try {
                    Thread.sleep(3 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
