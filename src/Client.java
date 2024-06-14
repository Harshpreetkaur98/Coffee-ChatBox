import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

// View: GUI components for user interactions
public class Client extends JFrame {

    // View Elements 
    private JTextArea server_response = new JTextArea(20, 50);
    private JTextArea input = new JTextArea(1, 50);
    private JScrollPane inputPane = new JScrollPane(input, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JTextArea clientNameArea = new JTextArea(9, 30);
    private JScrollPane clientNamePane = new JScrollPane(clientNameArea);
    private JTextArea commands = new JTextArea(10, 36);
    private JScrollPane commandPane = new JScrollPane(commands);    
    public JTextArea status = new JTextArea(1, 20);
    public JTextArea connectedIP = new JTextArea(1,20);
    public String current_leader = "";
    private PrintWriter pw;
    public String isLeader = "";
    ArrayList<String> clientList = new ArrayList<String>();
    private Socket ss;
    private static Client c;
    final static boolean RIGHT_TO_LEFT = false;
    JButton contactserver = new JButton("Contact Server");
    JButton sendtext = new JButton("Send text");
    

    // Factory Method Pattern : Method for styling the buttons
    public void styleButton(JButton button) {
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 10));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(Color.decode("#a68164"));
    }

    // Method to add components to the GUI container
    public void addComponentsToPane(Container content) {
        // Layout and Constrains
        if (RIGHT_TO_LEFT) {
            content.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        // Using Factory Pattern to create an ImageIcon
        final ImageIcon bg = new ImageIcon("images/chatbg.png");
        JPanel mainbg = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg.getImage(), 0, 0, null);
            }
        };
        setContentPane(mainbg);
        setTitle("Coffee Chat");


        mainbg.setLayout(new GridBagLayout());        
        GridBagConstraints constrain = new GridBagConstraints();

        // Constants and GUI elements setup 
        Font metadata = new Font("Comic Sans MS", sendtext.getFont().getStyle(), 10); 
        Font textFont=new Font("Comic Sans MS",sendtext.getFont().getStyle(),12); 

        
        // SERVER RESPONSE
        // Builder Pattern: for step by step construction of objects with varying config.
        // Using Builder Pattern to construct Gridbagconstraints fro server response textarea.
                constrain.fill = GridBagConstraints.BOTH;
                constrain.anchor = GridBagConstraints.NORTHWEST;
                constrain.weightx = 1;
                constrain.weighty = 1;
                constrain.gridx = 0;
                constrain.gridy = 0;
                constrain.insets = new Insets(10, 10, 0, 0);
                server_response.setFont(textFont);
                server_response.setLineWrap(true);
                JScrollPane server_responsePane = new JScrollPane(server_response);
                server_responsePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
                DefaultCaret caret = (DefaultCaret) server_response.getCaret(); 
                caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
                mainbg.add(server_responsePane, constrain);




                // STATUS
                // Using Builder Pattern to construct Gridbagconstraints fro server response textarea.
                constrain.fill = GridBagConstraints.FIRST_LINE_START;
                constrain.anchor = GridBagConstraints.NORTHWEST;
                constrain.weightx = 1;
                constrain.gridx = 0;
                constrain.gridy = 1;
                constrain.insets = new Insets(21, 10, 0, 0);
                status.setFont(metadata);
                status.append("Status: ");
                status.setEnabled(false);
                mainbg.add(status, constrain);

                // INPUT TEXT AREA
                // Builder Pattern: for step by step construction of objects with varying config.
                constrain.fill = GridBagConstraints.FIRST_LINE_START;
                constrain.anchor = GridBagConstraints.WEST;
                constrain.weightx = 1;
                constrain.gridx = 0;
                constrain.gridy = 2;
                constrain.insets = new Insets(10, 10, 10, 0);
                inputPane.setFont(textFont);
                inputPane.setPreferredSize(new Dimension(340, 100));
                input.setLineWrap(true);
                mainbg.add(inputPane, constrain);
                
            

                // SEND TEXT BUTTON
                // Builder Pattern: for step by step construction of objects with varying config.
                constrain.fill = GridBagConstraints.FIRST_LINE_START;
                constrain.anchor = GridBagConstraints.WEST;
                constrain.weightx = 1;
                constrain.gridx = 0;
                constrain.gridy = 2;
                constrain.insets = new Insets(5, 400, 0, 0);
                sendtext.setPreferredSize(new Dimension(120, 20));
                styleButton(sendtext);
                mainbg.add(sendtext, constrain);

                // CONNECTED IP
                // Decorator Pattern: For designing without affecting other objects.
                constrain.fill = GridBagConstraints.FIRST_LINE_START;
                constrain.anchor = GridBagConstraints.WEST;
                constrain.weightx = 1;
                constrain.gridx = 0;
                constrain.gridy = 1;
                constrain.insets = new Insets(5, 200, 0, 0);
                connectedIP.setFont(metadata);
                connectedIP.append("Socket: ");  
                connectedIP.setEnabled(false);      
                mainbg.add(connectedIP, constrain);

                // CONTACT SERVER BUTTON
                // Decorator Pattern (Button Styling)
                constrain.fill = GridBagConstraints.FIRST_LINE_START;
                constrain.anchor = GridBagConstraints.WEST;
                constrain.weightx = 1;
                constrain.gridx = 0;
                constrain.gridy = 1;
                constrain.insets = new Insets(5, 400, 0, 0);
                contactserver.setPreferredSize(new Dimension(120, 20));
                styleButton(contactserver);
                mainbg.add(contactserver, constrain);

                // CLIENT NAME AREA - USER LIST
                // Facade Pattern: Simplifies interface to a complex system of classes (Simplifies clientNameArea stepup)
                constrain.fill = GridBagConstraints.FIRST_LINE_START;
                constrain.anchor = GridBagConstraints.NORTHEAST;
                constrain.weightx = 1;
                constrain.gridx = 1;
                constrain.gridy = 0;
                constrain.insets = new Insets(10, 0, 0, 30);
                clientNameArea.setFont(textFont);
                clientNameArea.setEnabled(false);
                mainbg.add(clientNamePane, constrain);
                

                // COMMANDS
                // Composite pattern: allowing clients to work with individual objects and composition of objects uniformly.(Appending commands to commands JTextArea)
                constrain.fill = GridBagConstraints.FIRST_LINE_START;
                constrain.anchor = GridBagConstraints.NORTHEAST;
                constrain.weightx = 1;
                constrain.gridx = 1;
                constrain.gridy = 0;
                constrain.insets = new Insets(200, 0, 0, 35);
                commands.setFont(metadata);
                commands.setEditable(false);
                commands.append("Coffee Chat Commands:\n"
                + "/whisper [name] [message] - Send a private message\n"
                + "/kick [name] [reason] - Kick a user from the server\n"
                + "/list - List all users in the server inc. IP & Ports\n"
                + "/leader - Show the current leader\n"
                + "/exit - Close the server");
                commandPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                commandPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
                mainbg.add(commandPane, constrain);
            }

            public Client() {

                // Factory Pattern: Instantiating a container object by invoking the getcontentpane method.
                Container content = this.getContentPane();
                getContentPane().setBackground(Color.decode("#dbc2af"));

                // Command Pattern: Adding a key listner to the input text area to handle key events.
                input.addKeyListener(new KeyAdapter() {
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        	if (e.isControlDown()){
                            	input.setText(input.getText() + "\n");
                        	}
                        	else {
                        		sendText(input.getText());
                        		e.consume();
                        	}
                        }
                    }
                });

                // Observer pattern: Adding action listener to buttons to handle user interactions.
                contactserver.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        contactServer();
                    }
                });
                sendtext.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        String message = input.getText();
                        if (message.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "The Message Box is blank.");
                        } else {
                            sendText(input.getText());
                        }
                    }
                });

                // Decorate pattern: Wrapping the server_response text area with a JScrollPane.
                content.add(new JScrollPane(server_response));
                server_response.setEnabled(false);
            }
            String clientName = "";

            public void contactServer() {
                // Ask the user for the server's IP address, if left, will remain on existing connection - CG 01/02/24
                String serverIP = JOptionPane.showInputDialog(this, "Enter Server IP Address:"); // Builder Pattern 
                if (serverIP == null) {
                    // User canceled the input, do nothing
                    return;
                }

                String port = JOptionPane.showInputDialog(this, "Enter Server Port:"); // Builder Pattern 
                if (port == null) {
                    // User canceled the input, do nothing
                    return;
                }

                // Ask the user for the client name, is persistent, cannot be cancelled - CG 02/02/24
                while (clientName.trim().isEmpty()) {
                    clientName = JOptionPane.showInputDialog(this, "Enter Your Name:"); // Builder Pattern 
                }

                try {            
                    ss = new Socket(serverIP, Integer.parseInt(port)); // Factory Pattern
                    OutputStream os = ss.getOutputStream(); // Factory Pattern
                    pw = new PrintWriter(os, true); // Factory Pattern
                    pw.println("Hello, I'm " + clientName); // Builder Pattern
                    //connectedPort.setText(":" + port);
                    connectedIP.setText("Socket: " + serverIP + ":" + port); // Builder Pattern
                    status.setText("Status: Connected!!"); // Builder Pattern

                } catch (IOException ioe) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Error connecting to server: " + ioe.getMessage());
                    ioe.printStackTrace(); // Null object Pattern
                }

                // The Thread creation here follows the Command pattern, where the Runnable interface
                // acts as a command to execute the code in a separate thread.
                
                
                new Thread(new Runnable() { // Command Pattern
                    @Override
                    public void run() {
                        try {
                            InputStream is = ss.getInputStream();
                            BufferedReader br = new BufferedReader(new InputStreamReader(is));
                            String line;
                            while ((line = br.readLine()) != null) {

                                if (line.contains("DELETEALL")) {
                                    clientNameArea.setText("");
                                }

                                else if(line.equals("ReturnDupe")) {
                                    JOptionPane.showMessageDialog(null, "Username already exists, please choose another.");
                                }

                                else if (line.contains("CURRENT_LEADER:")) {
                                    current_leader = line.split(":")[1];
                                }
                                else if (line.contains("CAU")) {
                                    String removeCAU = line.replace("CAU", "");
                                    String username = removeCAU.split(" - ")[0];
                                    if (!removeCAU.contains(line)) {
                                            if (username.equals(current_leader)) {
                                                int spaceIndex = removeCAU.indexOf(" ");
                                                String leaderdeets = new StringBuilder(removeCAU).insert(spaceIndex + 1, "(Leader)").toString();
                                                clientNameArea.append(leaderdeets + "\n");
                                            }
                                            else {
                                                clientNameArea.append(removeCAU + "\n");
                                                
                                            }
                                        }
                                    }
                                
                                else if (line.equals("LEADERYOU"))  {
                                    JOptionPane.showMessageDialog(null, "You are the leader!");
                                    isLeader = "[Leader]";
                                }

                                else if (line.equals("DISCONNECT")) {
                                    JOptionPane.showMessageDialog(null, "You have been disconnected from the server.");
                                    closeConnection();
                                    connectedIP.setText("Socket: N/A");
                                    status.setText("Status: Disconnected");
                                }
                                else {
                                server_response.append(line + "\n");
                                String text = server_response.getText();
                                String[] lines = text.split("\n");
                                StringBuilder sb = new StringBuilder();
                            
                                for (String lin : lines) {
                                    if (!lin.trim().isEmpty()) {
                                        sb.append(lin);
                                        sb.append("\n");
                                    }
                                }
                                server_response.setText(sb.toString());
                                }
                            }
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    }
                }).start();
            }              

            public Socket getSocket() {
                return ss;
            }

            public void sendText(String message) {
                if (input.getText().equals("/exit")) {
                    closeConnection();
                    System.exit(0);
                    return;
                }
                pw.println(message);
                input.setText("");
            }

            public void closeConnection() {
                try {
                    if (ss != null)
                        pw.println("Goodbye server, this is " + clientName + " closing connection.");
                        if (ss != null)
                        ss.close();
                        connectedIP.setText("Socket: N/A");
                        status.setText("Status: Disconnected");
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }

    public static void main(String[] args) throws Exception {
        // Singleton Pattern : ensures that a class has only one instance and provides a global point of access to that instance.
        JFrame splashFrame = new JFrame();
        splashFrame.setUndecorated(true); 
        splashFrame.setSize(800, 500); 
        splashFrame.setLocationRelativeTo(null);

        // Creating the splash screen via the Singleton Pattern.
        JPanel splashPanel = new JPanel();
        splashPanel.setBackground(Color.getHSBColor(251,243,203));
        splashFrame.add(splashPanel); 
        
        // Factory Pattern: provides an interface for creating objects in a superclass, 
        // but allows subclasses to alter the type of objects that will be created.
        // Using Factory Pattern to create an ImageIcon
        ImageIcon loadingIcon = new ImageIcon("images/loading.gif"); 
        
        // Using Factory Pattern to create an ImageIcon
        JLabel loadingLabel = new JLabel(loadingIcon);
        splashPanel.add(loadingLabel);
        
        splashFrame.setVisible(true);
        
        try {
            Thread.sleep(1000); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        splashFrame.dispose();      

        // Creating an instance of client class.
        c = new Client();
        c.setSize(900, 600);
        c.setResizable(false);
        c.addComponentsToPane(c.getContentPane());
        // calculating the location of the client window.
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((d.getWidth() - c.getWidth()) / 2);
        int y = (int) ((d.getHeight() - c.getHeight()) / 2);
        c.setLocation(x, y);
        c.setVisible(true);
        c.setTitle("Coffee Chat: Client");
        c.status.append("Disconnected");
        c.connectedIP.append("N/A");
        // Setting the default close operation of the client window.
        c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Adding a window listener to handle window closing events
        c.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent wa) {
                // closing the connection when teh window is closed.
                c.closeConnection();
                // Exiting the application. 
                System.exit(0);
            }
        });

    }
}

