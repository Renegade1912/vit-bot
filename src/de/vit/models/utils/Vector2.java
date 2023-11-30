package de.vit.models.utils;

public class Vector2 {
    public int x;
    public int y;

    public Vector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2() {
        this.x = 0;
        this.y = 0;
    }

    public Vector2 add(Vector2 vector) {
        return new Vector2(this.x + vector.x, this.y + vector.y);
    }

    public Vector2 sub(Vector2 vector) {
        return new Vector2(this.x - vector.x, this.y - vector.y);
    }

    public Vector2 add(int x, int y) {
        return new Vector2(this.x + x, this.y + y);
    }

    public Vector2 sub(int x, int y) {
        return new Vector2(this.x - x, this.y - y);
    }
}
