package de.vit.models.game;

public class GameMapField {
    private final int x;
    private final int y;
    private int distance = Integer.MAX_VALUE;

    public GameMapField(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
