import org.junit.Test;
import static org.junit.Assert.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.*;
import java.awt.Container;

public class ClientTest {
    @Test
    public void testAddComponentsToPane() {
        Client client = new Client();

        // Call the addComponentsToPane method
        client.addComponentsToPane(client.getContentPane());

        // Verify the expected output
        assertEquals(1, client.getComponentCount());
    }

    @Test
    @AfterAll
    public void testCloseConnection() {
        // Create an instance of Client
        Client client = new Client();

        // Call the closeConnection method
        client.closeConnection();

        // Verify the expected output
        assertNull(client.getSocket());
        client.connectedIP.getText();
        assertEquals("Socket: N/A", client.connectedIP.getText());
        assertEquals("Status: Disconnected", client.status.getText()); 
    }

    @Test
    @BeforeAll
    public void testContactServer() { //Tests the data collection from the ContactServer method
        // Prepare output for JOptionPane
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);

        // Create an instance of Client
        Client client = new Client();

        // Call the contactServer method
        client.contactServer();

        // Verify the expected output
        assertNotNull(client.getSocket());
    }

    @Test
    public void testSendMessage() {
        // Prepare output for JOptionPane
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);

        // Create an instance of Client
        Client client = new Client();

        // Call the contactServer method
        client.contactServer();

        // Call the sendMessage method
        client.sendText("Test message");

        // Verify the expected output
        assertNotNull(client.getSocket());}
    }