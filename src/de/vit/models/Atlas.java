package de.vit.models;

import de.vit.enums.Direction;
import de.vit.models.utils.Vector2;
import de.vitbund.netmaze.info.Cell;

public class Atlas {
    // Map max x dimension
    private final int xdim;
    // Map max y dimension
    private final int ydim;
    // Map fields
    private final AtlasField[][] fields;
    // Current x position
    private int currentX;
    // Current y position
    private int currentY;
    // Last map field
    private AtlasField lastField;
    // Current map field
    private AtlasField currentField;


    /**
     * Builds a new Map for our Bot.
     *
     * @param xdim   x dimension (maximum x coordinate)
     * @param ydim   y dimension (maximum y coordinate)
     * @param startX start x coordinate
     * @param startY start y coordinate
     */
    public Atlas(int xdim, int ydim, int startX, int startY) {
        this.xdim = xdim;
        this.ydim = ydim;
        this.currentX = startX;
        this.currentY = startY;
        this.fields = new AtlasField[xdim][ydim];

        // build map fields
        for (int i = 0; i < (xdim * ydim); i++) {
            int x = i % xdim;
            int y = i / xdim;
            AtlasField field = new AtlasField(x, y);

            if (x == currentX && y == currentY) {
                currentField = field;
                currentField.setType(Cell.FLOOR);
            }

            fields[x][y] = field;
        }
    }

    public AtlasField getCurrentField() {
        return currentField;
    }

    public AtlasField getLastField() {
        return lastField;
    }

    public AtlasField getFieldByDirection(Direction direction) {
        Vector2 coords = new Vector2(currentX, currentY);

        // calculate position of direction
        switch (direction) {
            case NORTH -> coords.y -= 1;
            case EAST -> coords.x += 1;
            case SOUTH -> coords.y += 1;
            case WEST -> coords.x -= 1;
            case SELF -> coords = new Vector2(currentX, currentY);
            default -> throw new IllegalStateException("Unexpected direction: " + direction);
        }

        // check for out of bounds
        if (coords.x < 0) coords.x = xdim - 1;
        if (coords.x >= xdim) coords.x = 0;
        if (coords.y < 0) coords.y = ydim - 1;
        if (coords.y >= ydim) coords.y = 0;

        return fields[coords.x][coords.y];
    }

    public void updateCurrentField(Direction direction) {
        lastField = currentField;
        currentField = getFieldByDirection(direction);
        currentX = currentField.getX();
        currentY = currentField.getY();
    }

    public void setFieldTypeByPosition(int x, int y, int type) {
        fields[x][y].setType(type);
    }

    public void setFieldTypeByDirection(Direction direction, int type) {
        // toDo: check add to wartemenge
        AtlasField field = getFieldByDirection(direction);
        field.setType(type);
    }
}
