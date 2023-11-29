package de.vit.models.game;

public class GameMap {
    public static final int NORTH = 0;
    public static final int EAST = 1;
    public static final int SOUTH = 2;
    public static final int WEST = 3;
    public static final int SELF = 4;
    private final int xdim;
    private final int ydim;
    private GameMapField currentField;
    private int currentX;
    private int currentY;
    private GameMapField[][] fields;

    public GameMap(int xdim, int ydim, int startX, int startY) {
        this.xdim = xdim;
        this.ydim = ydim;
        this.currentX = startX;
        this.currentY = startY;
        this.fields = new GameMapField[xdim][ydim];

        // print position
        System.out.println("Startposition: " + currentX + ", " + currentY);

        // build map
        for (int i = 0; i < (xdim * ydim); i++) {
            int x = i % xdim;
            int y = i / xdim;
            GameMapField field = new GameMapField(x, y);

            if (x == currentX && y == currentY) {
                currentField = field;
            }

            fields[x][y] = field;
        }
    }

    public int getCurrentX() {
        return currentX;
    }

    public int getCurrentY() {
        return currentY;
    }

    public GameMapField getFieldByDirection(Direction direction) {
        int x, y;

        // calculate position of direction
        switch (direction) {
            case NORTH -> {
                x = currentX;
                y = currentY - 1;
            }
            case EAST -> {
                x = currentX + 1;
                y = currentY;
            }
            case SOUTH -> {
                x = currentX;
                y = currentY + 1;
            }
            case WEST -> {
                x = currentX - 1;
                y = currentY;
            }
            case SELF -> {
                x = currentX;
                y = currentY;
            }
            default -> throw new IllegalStateException("Unexpected direction: " + direction);
        }

        // check for out of bounds
        if (x < 0) x = xdim - 1;
        if (x >= xdim) x = 0;
        if (y < 0) y = ydim - 1;
        if (y >= ydim) y = 0;

        GameMapField field = fields[x][y];
        if (field == null) {
            System.out.println("Field is null!");
            return currentField;
        }

        return field;
    }

    public GameMapField getCurrentField() {
        return currentField;
    }

    public void updateCurrentField(Direction direction) {
        currentField = getFieldByDirection(direction);
        currentX = currentField.getX();
        currentY = currentField.getY();
    }

    public void setCurrentField(GameMapField currentField) {
        this.currentField = currentField;
    }

    public enum Direction {
        NORTH, EAST, SOUTH, WEST, SELF
    }

}
