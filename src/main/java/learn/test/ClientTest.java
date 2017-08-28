package learn.test;

import learn.Client;
import learn.message.Message;
import learn.util.RequestCode;

import java.io.IOException;

public class ClientTest {

    public static void main(String[] args) throws IOException {
        System.out.println("perpared");
        Client client = new Client("127.0.0.1", 8088);
        System.out.println("bind the server");

        while (true) {
            try {
                Message registerMessage = new Message();
                registerMessage.setMessageCode(RequestCode.REGISTER);
                registerMessage.setMessage("register message");
                client.sendMessage(registerMessage);
                System.out.println("send a register message");

                Message handleMessage = new Message();
                handleMessage.setMessageCode(RequestCode.NORMAL);
                handleMessage.setMessage("handle message");
                client.sendMessage(handleMessage);
                System.out.println("send a handle message");

                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
