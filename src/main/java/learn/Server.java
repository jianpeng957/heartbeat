package learn;

import learn.heartbeat.HeartbeatHandler;
import learn.message.ConvergeMessageHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
    private static ExecutorService clientExecutorService;
    private static ServerSocket serverSocket;
    private static Map<String, Object> register;

    private static int port = 8088;

    private static Server instance;
    private HeartbeatHandler heartbeatHandler;
    private ConvergeMessageHandler convergeMessageHandler;

    private AtomicBoolean isRunning = new AtomicBoolean(false); // 设置 Server 是否在运行

    private Server() throws IOException {
        initial();
    }

    public static Server getInstance() throws IOException {
        if (instance == null) {
            synchronized (Server.class) {
                instance = new Server();
            }
        }

        return instance;
    }

    private void initial() throws IOException {
        serverSocket = new ServerSocket();
        InetSocketAddress socketAddress = new InetSocketAddress(port);
        serverSocket.bind(socketAddress);

        clientExecutorService = Executors.newFixedThreadPool(20); // 应对多客户端线程池
        register = new ConcurrentHashMap<String, Object>();

        System.out.println("server start");
    }

    public void startComponent() throws IOException {
        heartbeatHandler = HeartbeatHandler.getInstance();
        heartbeatHandler.start();

        convergeMessageHandler = ConvergeMessageHandler.getInstance();

        System.out.println("component start");
    }

    public void start() throws IOException {
        if (!isRunning.get()) {
            isRunning.set(true);

            while (true) {
                System.out.println("waiting..");
                Socket socket = serverSocket.accept();
                String key = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
                System.out.println("accept socket: " + key);
                System.out.println("------------------------");
                heartbeatHandler.putHeartbeat(key, System.currentTimeMillis()); // 保存第一次注册时的时间
                Future future = clientExecutorService.submit(new ClientThread(socket));
                register.put(key, future);
            }
        }
    }

    public boolean removeRegister(String key) {
        Future future = (Future) register.remove(key);
        future.cancel(true);
        return future.isCancelled();
    }

    public int getRegisterSize() {
        return register.size();
    }
}
