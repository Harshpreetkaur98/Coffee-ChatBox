import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.Assert.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerAdminMultiThreadTest {
    @Test
    @BeforeAll
    public void testServerRunning() throws IOException, InterruptedException {
        // Create a new thread to run the server
        Thread serverThread = new Thread(() -> {
            try {
                MultiThreadedServer server = new MultiThreadedServer();
                server.run();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Start the server
        serverThread.start();

        // Give the server some time to start
        Thread.sleep(1000);

        // Create a client socket to connect to the server
        Socket clientSocket = new Socket("127.0.0.1", 2000);

        // Check that the client socket is connected
        assertTrue(clientSocket.isConnected());

        // Close the client socket
        clientSocket.close();

        // Stop the server
        serverThread.interrupt();
    }

    @Test
    @AfterAll
    public void testStopServer() {
        // Create a new thread to run the server
        Thread serverThread = new Thread(() -> {
            try {
                MultiThreadedServer server = new MultiThreadedServer();
                server.run();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Start the server
        serverThread.start();

        // Give the server some time to start
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Stop the server
        serverThread.interrupt();

        // Check that the server is stopped
        assertFalse(serverThread.isAlive());
    }
}