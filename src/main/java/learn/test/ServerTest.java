package learn.test;

import learn.Server;

import java.io.IOException;

public class ServerTest {
    public static void main(String[] args) throws IOException {
        Server server = Server.getInstance();
        server.startComponent();
        server.start();
    }
}
