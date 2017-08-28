package learn.heartbeat;

import learn.Server;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class HeartbeatHandler {

    public Server server;

    private ExecutorService heartbeatExecutorService;
    private Map<String, Long> heartbeat;
    private Map<String, Integer> heartbeatRetry;

    private final static long TIME_OUT = 3 * 1000;
    private final static int MAX_RETRY = 3;
    private final static long TIME_MONITOR_HERTBEAT = 3 * 1000;

    private static HeartbeatHandler instance;

    private AtomicBoolean isRunning = new AtomicBoolean(false); // 设置 HeartbeatHandler 是否在运行

    private HeartbeatHandler() throws IOException {
        initial();
    }

    public static HeartbeatHandler getInstance() throws IOException {
        if (instance == null) {
            synchronized (HeartbeatHandler.class) {
                instance = new HeartbeatHandler();
            }
        }

        return instance;
    }

    private void initial() throws IOException {
        heartbeatExecutorService = Executors.newSingleThreadExecutor(); // 应对多客户端心跳
        server = Server.getInstance();

        heartbeat = new ConcurrentHashMap<String, Long>();
        heartbeatRetry = new ConcurrentHashMap<String, Integer>();
    }

    public void start() {
        if (!isRunning.get()) {
            isRunning.set(true);

            heartbeatExecutorService.execute(() -> {
                while (true) {
                    System.out.println("hearbeat size: " + heartbeat.size());
                    System.out.println("register size: " + server.getRegisterSize());
                    for (Map.Entry<String, Long> node : heartbeat.entrySet()) {
                        String heartbeatId = node.getKey();
                        Long lastHeartbeatTime = node.getValue();

                        if (System.currentTimeMillis() - lastHeartbeatTime > TIME_OUT) {
                            int retry = heartbeatRetry.get(heartbeatId);

                            if (retry > MAX_RETRY) {
                                System.out.println(heartbeatId + " 断开连接");
                                heartbeat.remove(heartbeatId);
                                // 应当以某种形式通知注册中心，将某一客户端从注册列表中删除 - 观察者模式？
                                server.removeRegister(heartbeatId);
                            } else {
                                System.out.println(heartbeatId + " 第" + (retry + 1) + "次尝试重连");
                                heartbeatRetry.put(heartbeatId, ++retry);
                            }
                        }
                    }

                    try {
                        Thread.sleep(TIME_MONITOR_HERTBEAT);   // 若不限制检测心跳线程的运行速度，可能会在网络延迟上对各个客户端进行误操作，将客户端移除register
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public Set<Map.Entry<String, Long>> getHeartbeat() {
        return heartbeat.entrySet();
    }

    public void putHeartbeat(String heartbeatId, Long aliveTime) {
        heartbeat.put(heartbeatId, aliveTime);
        heartbeatRetry.put(heartbeatId, 0);
    }
}
