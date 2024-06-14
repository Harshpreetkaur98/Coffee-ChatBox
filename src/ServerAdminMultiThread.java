import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.DefaultCaret;

import java.util.Timer;
import java.util.TimerTask;

import java.io.*;
import java.net.Socket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.lang.String;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.List;
import java.util.Map;


class LeaderQueue {
    public static Queue<String> leaderQueue = new LinkedList<>();
    public static List<Socket> clientSockets = new ArrayList<>();
    public static Map<String, Socket> clientSocketDictionary = new HashMap<>();
}

class HandleConnection extends Thread {
  Socket returnsocket = null;
  Socket s = null;
  JTextArea display;
  private static PrintWriter pw;
  public HandleConnection(Socket s, JTextArea display) {
  this.s = s;
  this.display = display;
  }

  static void updateCAU() {
    String current_Leader = LeaderQueue.leaderQueue.peek().split(":")[0];

    for (Socket clientSocket : LeaderQueue.clientSockets) {
        try {
            OutputStream os = clientSocket.getOutputStream();
            pw = new PrintWriter(os, true);
            pw.println("DELETEALL\n");
            pw.println("CURRENT_LEADER:" + current_Leader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    LeaderQueue.clientSocketDictionary.forEach((key, value) ->  {
        for  (Socket clientSocket : LeaderQueue.clientSockets) {
            try {
                OutputStream os = clientSocket.getOutputStream();
                pw = new PrintWriter(os, true);
                pw.println("CAU" + key + " - " + value + "\n");
                if (LeaderQueue.leaderQueue.size() == 1) {
                    pw.println("LEADERYOU");
                }
                pw.flush();                
        } catch (IOException e) {
            e.printStackTrace();
        }
    }}); 
  }

  public void run() {
    String clientName = "";
    String output;

    try {
        InputStream is = s.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String str = br.readLine();
        String message = "";
        clientName = str.substring(str.lastIndexOf(" ") + 1);
        LeaderQueue.leaderQueue.add(clientName + ":" + InetAddress.getLocalHost());
        LeaderQueue.clientSocketDictionary.put(clientName, LeaderQueue.clientSocketDictionary.get("uname"));
        LeaderQueue.clientSocketDictionary.remove("uname");
        updateCAU();
        display.append(clientName +" Joined." + "\n");                
        
        

        if (LeaderQueue.leaderQueue.size() == 1) {
            display.append(clientName + " is the leader." + "\n");
        }

        while (str != null) {
            if (str.equals("/list")) {
                for (String clients : LeaderQueue.leaderQueue) {
                    output = clients + "\n";
                    s.getOutputStream().write(output.getBytes());
                }
                display.append("Handled /list - " + clientName + "\n");
            }

            else if (str.equals("/leader")) {
            	output = ("The current leader is: " + LeaderQueue.leaderQueue.peek() + "\n");
                s.getOutputStream().write(output.getBytes());
                display.append("Handled /leader - " + clientName + "\n");
            }

            else if (str.contains("/whisper")) {
                int firstspace = str.indexOf(" ") + 1;
                int secondspace = str.indexOf(" ", firstspace);
                String target_name = str.substring(firstspace, secondspace);
                Socket target_socket = LeaderQueue.clientSocketDictionary.get(target_name);
                OutputStream ts = target_socket.getOutputStream();
                pw = new PrintWriter(ts, true);
                pw.println(clientName + " whispered: " + str.substring(secondspace + 1));
                pw.flush();
                display.append(clientName + " whispered to " + target_name + ": " + str.substring(secondspace + 1) + "\n");  }
                
            else {
	            for (Socket clientSocket : LeaderQueue.clientSockets) {
	                OutputStream os = clientSocket.getOutputStream();
	                pw = new PrintWriter(os, true);
                    if (clientName.contains(LeaderQueue.leaderQueue.peek().split(":")[0])) {
                        message = clientName + " (Leader): " + str;
                        pw.println(message);
                        pw.flush();
                    }
                    else {
                        message = clientName + ": " + str;
                        pw.println(message);
                        pw.flush();
                        System.out.println(LeaderQueue.clientSockets);
                    }
	            } display.append(message + "\n");
            }
            str = br.readLine();  
        }
    } catch (IOException ioe) {
        ioe.printStackTrace();
    }

    final String disconnectedClient = clientName;
    LeaderQueue.clientSocketDictionary.remove(disconnectedClient);
    LeaderQueue.leaderQueue.removeIf(item -> item.contains(disconnectedClient));
    display.append("The new leader is " + LeaderQueue.leaderQueue.peek() + "\n");
    display.append(disconnectedClient + " disconnected.\n");


    // display.setResizable(false);
    LeaderQueue.clientSockets.remove(s);
    updateCAU();

    if (LeaderQueue.leaderQueue.peek() != null){        
        display.append((LeaderQueue.leaderQueue.peek()).split(":")[0] + " is the leader" + "\n");
    } else {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                if(LeaderQueue.leaderQueue.isEmpty()) {
                    display.append("There is no leader, server closing in 5 seconds" + "\n");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.exit(0);
                } else {
                    String newLeader = LeaderQueue.leaderQueue.poll();
                    display.append("The new leader is "+ newLeader + "\n");
                    sendNotificationToLeader(newLeader);
    
                    for(String clientName : LeaderQueue.leaderQueue) {
                        if(!clientName.equals(newLeader)) {
                            sendNotificationToClient(clientName, newLeader);
                        }
                    }
                }                
            }        
        }, 5000);}
    }

        static void sendNotificationToLeader(String leaderName) {
            Socket leaderSocket = LeaderQueue.clientSocketDictionary.get(leaderName);
            try{
                OutputStream os = leaderSocket.getOutputStream();
                PrintWriter pw = new PrintWriter(os, true);
                pw.println("You are the new leader.");
                pw.flush();
            } catch (IOException e){
                e.printStackTrace();
            }
            }

        static void sendNotificationToClient(String clientName, String message) {
            Socket clienSocket = LeaderQueue.clientSocketDictionary.get(clientName);
            try{
                OutputStream os = clienSocket.getOutputStream();
                PrintWriter pw = new PrintWriter(os, true);
                pw.println(message);
                pw.flush();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

class MultiThreadedServer extends Thread {
private JTextArea display;
private volatile boolean isRunning = true; 
public Socket mySocket;
private int port;

  public MultiThreadedServer(JTextArea display) {
    this.display = display;
    display.setEnabled(false);
}

    // Default constructor
    public MultiThreadedServer() {
        this.port = 8080; // Default port number
    }

    // Constructor with port parameter
    public MultiThreadedServer(int port) {
        this.port = port;
    }

  public void stopServer() {
    for (Socket clienSocket : LeaderQueue.clientSockets) {
        try {
            OutputStream os = clienSocket.getOutputStream();
            PrintWriter pw = new PrintWriter(os, true);
            pw.println("DISCONNECT");
            clienSocket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    LeaderQueue.clientSockets.clear();
    LeaderQueue.clientSocketDictionary.clear();
  }

  public void run() {
      InetAddress ip;
      LeaderQueue.clientSockets = new ArrayList<>();

      try{
        @SuppressWarnings("resource")
        ServerSocket ss = new ServerSocket(2000);
        ip = InetAddress.getLocalHost();
        display.append("Server starting on IP: " + ip.getHostAddress() + " & Port: " + ss.getLocalPort() + "\n");
          while (isRunning) {
             Socket mySocket = ss.accept();
             HandleConnection hc = new HandleConnection(mySocket, display);
             LeaderQueue.clientSockets.add(mySocket);
             LeaderQueue.clientSocketDictionary.put("uname", mySocket);
             hc.start();
          
      }} catch (UnknownHostException e) {
            e.printStackTrace();
      }
      catch (IOException ioe) {
          ioe.printStackTrace();
      }
      display.append("Server closing\n");
  }
}


public class ServerAdminMultiThread extends JFrame implements ActionListener {
    private JButton one = new JButton("Start Server");
    private JButton two = new JButton("Stop Server");
    private JTextArea display = new JTextArea(10,40);

    //private JScrollPane inputPane = new JScrollPane(input,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private Thread theServer;
    

    public static void main(String[] args) {
        ServerAdminMultiThread sa = new ServerAdminMultiThread();
        sa.setSize(500, 300);
        sa.setVisible(true);
        sa.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    class makeConnection extends Thread {
        Socket s = new Socket();
        JTextArea display;

        public makeConnection(Socket s, JTextArea display) {
            this.s = s;
            this.display = display;

        }
    }

    public ServerAdminMultiThread() {
        Container appletContent = this.getContentPane();
        appletContent.setLayout(new FlowLayout());
        getContentPane().setBackground(Color.decode("#dbc2af"));
        setTitle("Coffee Chat: Server");
        appletContent.add(one);
        one.addActionListener(this);
        appletContent.add(new JScrollPane(display));
        theServer = new MultiThreadedServer(display);

        one.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        one.setBorderPainted(false);
        one.setFocusPainted(false);
        one.setForeground(Color.WHITE);
        one.setBackground(Color.decode("#a68164"));
        display.setFont(new Font("Comic Sans MS", Font.BOLD, 13));
       DefaultCaret caret = (DefaultCaret)display.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE); 

        appletContent.add(two);
        two.addActionListener(this);
        appletContent.add(new JScrollPane(display));
        two.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        two.setBorderPainted(false);
        two.setFocusPainted(false);
        two.setForeground(Color.WHITE);
        two.setBackground(Color.decode("#a68164"));     
        display.setFont(new Font("Comic Sans MS", Font.BOLD, 13));

        this.setResizable(false);
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == one) {
            if (!theServer.isAlive()) {
                theServer = new MultiThreadedServer(display);
                theServer.start();
            } else {
                display.append("Server is now running.\n");
            }            
        } else if (event.getSource() == two) { 
            ((MultiThreadedServer) theServer).stopServer();
            display.append("Server stopped\n");
        }
    }
    
}
