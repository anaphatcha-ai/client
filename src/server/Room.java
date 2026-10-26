package src.server;

import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;
import src.server.Bird;
import java.util.Timer;

public class Room {

    private String roomName;
    private ArrayList<Player> players = new ArrayList<Player>();
    static final int w = 1280;
    static final int h = 720;
    static final long gametime = 180000;   // 3 นาที
    private Timer lobbyTimer;
    private Timer gameTimer;
    private ArrayList<Bird> birds = new ArrayList<Bird>();
    private Random random = new Random();
    private int birdid = 1;
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
            endTime = System.currentTimeMillis() + gametime;
            Boradcast("gamestart" + w + ':' + h + ":" + (gametime / 1000));
            gameTimer = new Timer();
            gameTimer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    tric();
                }
            }, 0, 1000);
        }
    }

    public synchronized void tric() {
        long now = System.currentTimeMillis();
        if (now >= endTime) {
            gameend();
            return;
        }
        spawbird(now);
        for (int i = birds.size() - 1; i >= 0; i = i - 1) {
            if (birds.get(i).isOffscreen(now, w)) {
                Boradcast("birdgone:" + birds.get(i).id);
                birds.remove(i);
            }
        }
    }

    public synchronized void spawbird(long now) {
        int randomtype = random.nextInt(100) + 1;
        Bird.Type type;
        int fx;
        int fy;
        int dir;
        if (randomtype <= 70) {
            type = Bird.Type.SILVER;
        } else if (randomtype <= 95) {
            type = Bird.Type.GOLD;
        } else {
            type = Bird.Type.RAINBOW;
        }
        int randomdir = random.nextInt(1);
        if (randomdir == 0) {
            fx = 1;
            fy = random.nextInt(100);
            dir = 1;
        }else {
            fx = 1280;
            fy = random.nextInt(100);
            dir = -1;
        }
        Bird bird = new Bird(birdid,type,fx,fy,dir,now);
        birds.add(bird);
        Boradcast("spawn:"+bird.id+":"+type.name()+":"+":"+fx+":"+fy+":"+bird.velocityX()+":"+type.radius);

    }
    public synchronized void shot(Player shoter,int x,int y)
    {
        long now = System.currentTimeMillis();
        if(now >= endTime)
            {
                return;
            }
        if(now <= shoter.getReload()){
            return;
        }
        if(shoter.getBullet()==0){
            shoter.setBullet(3);
        }
        shoter.setBullet(shoter.getBullet()-1);
        if(shoter.getBullet()==0){
            shoter.setReload(now+2000);
        }
        shoter.SendMessage("bullet"+shoter.getBullet());
        Bird hit = null;
        double best = 99999;
        for (Bird b : birds ){
            double dx = x - b.xAt(now); 
            double dy = y - b.y;
            double d = Math.sqrt((dx*dx)+(dy*dy));
            if(d <= b.type.radius && d < best){
                best = d;
                hit = b;
            }
        }
        if(hit != null){
            birds.remove(hit);
            shoter.setScore(shoter.getScore()+hit.type.points);
            Boradcast("hit"+":"+shoter.getId()+":"+hit.type.points);
            Boradcast(scoreMessage("score"));
        }
    }
    private  synchronized String scoreMessage(String massage){
        String massg = massage;
        for (Player p : players){
            massg = massg +":"+p.getName()+":"+p.getScore(); 
        }
        return massg;
    }
    public synchronized void gameend() {
        gameTimer.cancel();
        birds.clear();
        Player winner = null;
        for (Player p : players){
            if( winner == null ||p.getScore()>winner.getScore()){
                winner = p;
            }
        }
        Boradcast("gameover"+":"+winner.getName());
    }

}
