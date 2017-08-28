package learn.message;

import learn.heartbeat.HeartbeatEntity;
import learn.util.GsonUtils;

public class HeartbeatMessageHandler implements MessageHandler {

    @Override
    public void process(Message message) {
        System.out.println("processHeartbeat - " + message.getMessage());
        HeartbeatEntity heartbeatEntity = GsonUtils.parseJsonToObject(message.getMessage(), HeartbeatEntity.class);
        System.out.println(heartbeatEntity.getHeartbeatId() + " - 存活时间: " + System.currentTimeMillis());
    }
}
