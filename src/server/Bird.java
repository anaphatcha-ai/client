package src;

/**
 * นกฝั่ง server: ตำแหน่งคำนวณจากเวลา (บินเส้นตรงแนวนอน)
 * client ใช้สูตรเดียวกันนี้วาดนก ส่วนการตัดสินว่ายิงโดนหรือไม่ server เป็นคนตัดสิน
 */
public class Bird {

    public enum Type {
        //     คะแนน, รัศมี(px), ความเร็ว(px/วินาที)
        SILVER(10, 30, 150),
        GOLD(30, 24, 230),
        RAINBOW(100, 18, 340);

        public final int points;
        public final int radius;
        public final double speed;

        Type(int points, int radius, double speed) {
            this.points = points;
            this.radius = radius;
            this.speed = speed;
        }
    }

    public final int id;
    public final Type type;
    public final double startX;
    public final double y;
    public final int dir;          // +1 บินไปขวา, -1 บินไปซ้าย
    public final long spawnTimeMs;

    public Bird(int id, Type type, double startX, double y, int dir, long spawnTimeMs) {
        this.id = id;
        this.type = type;
        this.startX = startX;
        this.y = y;
        this.dir = dir;
        this.spawnTimeMs = spawnTimeMs;
    }

    /** ความเร็วแบบมีทิศทาง (px/วินาที) */
    public double velocityX() {
        return dir * type.speed;
    }

    /** ตำแหน่ง x ณ เวลา nowMs */
    public double xAt(long nowMs) {
        return startX + velocityX() * (nowMs - spawnTimeMs) / 1000.0;
    }

    public boolean isOffscreen(long nowMs, int fieldWidth) {
        double x = xAt(nowMs);
        return x < -type.radius * 2 || x > fieldWidth + type.radius * 2;
    }
}
