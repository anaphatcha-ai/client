package src.server;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.*;

import org.w3c.dom.Text;

class Server extends JFrame {

    final int PORT = 8080;
    private String in;
    private BufferedReader In;
    private PrintStream Out;
    private Server server = this;

    private ArrayList<Player> players = new ArrayList<Player>();
    private ArrayList<Room> rooms = new ArrayList<Room>();
    
    JTextArea textArea;
    JTextField textField;

    public Server() {
        setTitle("Server");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        textArea = new JTextArea();
        textField = new JTextField();

        JScrollPane scrollPane = new JScrollPane(textArea);

        add(scrollPane, BorderLayout.CENTER);
        add(textField, BorderLayout.SOUTH);
        

        StartServer();
        setVisible(true);
    }

    public void SendMessage(String message) {
        textArea.append(message + "\n");
    }

    public void StartServer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(PORT);
                    
                    int i =1;
                    while(true) {
                        Socket socket = serverSocket.accept();
                        Player player = new Player(socket, i, server);
                        players.add(player);
                        player.StartTask();
                        i++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0);
    }

    public void Boradcast(String message) {
        for (Player player : players) {
            player.SendMessage(message);
        }
    }

    public void addRoom(Room room) {
        rooms.add(room);
    }
    public static void main(String[] args) {
        new Server();
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }
    
}