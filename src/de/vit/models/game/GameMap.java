package de.vit.models.game;

import de.vit.models.game.GameMapField;

import java.util.ArrayList;
import java.util.List;

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
    private List<GameMapField> fields = new ArrayList<>();

    public GameMap(int xdim, int ydim, int startX, int startY) {
        this.xdim = xdim;
        this.ydim = ydim;
        this.currentX = startX;
        this.currentY = startY;

        // print position
        System.out.println("Startposition: " + currentX + ", " + currentY);

        // build map
        for (int i = 0; i <= (xdim * ydim); i++) {
            int x = i % xdim;
            int y = i / xdim;
            GameMapField field = new GameMapField(x, y);

            if (x == currentX && y == currentY) {
                currentField = field;
            }

            fields.add(field);
        }
    }

    public int getCurrentX() {
        return currentX;
    }

    public int getCurrentY() {
        return currentY;
    }

    public GameMapField getFieldByDirection(Direction direction) {
        GameMapField desiredField = switch (direction) {
            case NORTH -> new GameMapField(currentX, currentY - 1);
            case EAST -> new GameMapField(currentX + 1, currentY);
            case SOUTH -> new GameMapField(currentX, currentY + 1);
            case WEST -> new GameMapField(currentX - 1, currentY);
            case SELF -> new GameMapField(currentX, currentY);
        };

        if (desiredField.getX() < 0) desiredField = new GameMapField(xdim - 1, desiredField.getY());
        if (desiredField.getX() >= xdim) desiredField = new GameMapField(0, desiredField.getY());
        if (desiredField.getY() < 0) desiredField = new GameMapField(desiredField.getX(), ydim - 1);
        if (desiredField.getY() >= ydim) desiredField = new GameMapField(desiredField.getX(), 0);

        for (GameMapField field : fields) {
            if (field.getX() == desiredField.getX() && field.getY() == desiredField.getY()) {
                return field;
            }
        }

        return currentField;
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
