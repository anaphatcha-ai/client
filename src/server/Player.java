package src.server;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.Buffer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
public class Player {

    private Socket socket;
    private int id;
    private String Name = "Unknown";
    private boolean isReady = false;
    private boolean leader = false;
    private Server server;
    boolean allReady;
    private BufferedReader in;
    private PrintStream out;
    private Room room;
    private Player PLAYER = this;

    public Player(Socket socket, int id, Server server) {
        this.socket = socket;
        this.id = id;
        this.server = server;

        try {
            in = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void StartTask() {
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            public void run() {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        String[] parts = message.split(":");
                        String command = parts[0];

                        if (command.equals("JOIN")) {
                            server.getRooms().forEach((room) -> {
                                if (room.getRoomName().equals(parts[1])) {
                                    room.addPlayer(PLAYER);
                                    PLAYER.setRoom(room);
                                    Name = parts[2];
                                    server.SendMessage("Player " + Name + " has joined room " + parts[1] + ".");
                                }
                            });

                        } else if (command.equals("READY")) {
                            isReady = true;
                            server.SendMessage("Player " + Name + " is ready.");
                        } else if (command.equals("START")) {
                            if (getAllReady()) {
                                server.SendMessage("Game is starting.");
                            }
                            
                        } else if (command.equals("CREATEROOM")) {
                            leader = true;
                            room = new Room(parts[1], Player.this);
                            Name = parts[2];
                            server.addRoom(room);
                            server.SendMessage("Room " + parts[1] + " has been created by " + Name + ".");
                            SendMessage("Room Create:");

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0);
    }

    public boolean getAllReady() {
        allReady = true;

        server.getRooms().forEach((room) -> {
            if (room.getRoomName().equals(PLAYER.getRoom().getRoomName())) {
                for (Player player : room.getPlayers()) {
                    if (!player.isReady() && !player.isLeader()) {
                        allReady = false;
                    }
                }
            }
        });

        return allReady;
    }

    public void SendMessage(String message) {
        out.println(message);
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean isReady) {
        this.isReady = isReady;
    }

    public boolean isLeader() {
        return leader;
    }

    public void setLeader(boolean leader) {
        this.leader = leader;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public BufferedReader getIn() {
        return in;
    }

    public void setIn(BufferedReader in) {
        this.in = in;
    }

    public PrintStream getOut() {
        return out;
    }

    public void setOut(PrintStream out) {
        this.out = out;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

}
