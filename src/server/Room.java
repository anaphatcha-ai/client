package src.client;

import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;

import src.Bird;

import java.util.Timer;

public class Room {

    private String roomName;
    private ArrayList<Player> players = new ArrayList<Player>();
    static final int FIELD_W = 800;
    static final int FIELD_H = 600;
    static final long GAME_TIME = 60000;   // 60 วินาที
    private Timer lobbyTimer;
    private Timer gameTimer;
    private ArrayList<Bird> birds = new ArrayList<Bird>();
    private Random random = new Random();
    private int nextBirdId = 1;
    private boolean started = false;
    private long endTime;

    public Room(String roomName, Player player) {
        this.roomName = roomName;
        this.players.add(player);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                String UpdateMessage = "ROOM_UPDATE:" + roomName;
                for (Player player : players) {

                    UpdateMessage += ":" + player.getName() + ":" + player.isReady();
                }
                for (Player player : players) {
                    player.SendMessage(UpdateMessage);
                }
            }
        }, 0, 500);
        
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public String getRoomName() {
        return roomName;
    }

    public void Boradcast(String massage) {
        for (Player p : players) {
            p.SendMessage(massage);
        }
    }

    public synchronized void gamestart() {
        for (Player p : players) {
            p.setBullet(3);
            p.setScore(3);
            p.setReload(0);
        }
    }

}
