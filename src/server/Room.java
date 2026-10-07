package src.server;

import java.util.ArrayList;
import java.util.TimerTask;

import java.util.Timer;

public class Room {

    private String roomName;
    private ArrayList<Player> players = new ArrayList<Player>();

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
        }, 0, 24);
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





}
