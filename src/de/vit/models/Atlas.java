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

    public Atlas(int xdim, int ydim, int startX, int startY) {
        this.xdim = xdim;
        this.ydim = ydim;
        this.currentX = startX;
        this.currentY = startY;
        this.fields = new AtlasField[xdim][ydim];

        // build map
        for (int i = 0; i < (xdim * ydim); i++) {
            int x = i % xdim;
            int y = i / xdim;
            AtlasField field = new AtlasField(x, y);

            if (x == currentX && y == currentY) {
                currentField = field;
                field.setDistance(0);
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
        Vector2 vector = new Vector2(currentX, currentY);

        // calculate position of direction
        switch (direction) {
            case NORTH -> vector.y -= 1;
            case EAST ->  vector.x += 1;
            case SOUTH -> vector.y += 1;
            case WEST -> vector.x -= 1;
            case SELF -> vector = new Vector2(currentX, currentY);
            default -> throw new IllegalStateException("Unexpected direction: " + direction);
        }

        // check for out of bounds
        if (vector.x < 0) vector.x = xdim - 1;
        if (vector.x >= xdim) vector.x = 0;
        if (vector.y < 0) vector.y = ydim - 1;
        if (vector.y >= ydim) vector.y = 0;

        return fields[vector.x][vector.y];
    }

    public void updateCurrentField(Direction direction) {
        lastField = currentField;
        currentField = getFieldByDirection(direction);
        currentX = currentField.getX();
        currentY = currentField.getY();
    }

    public void setFieldCellByPosition(int x, int y, Cell cell) {
        fields[x][y].setCell(cell);
    }

    public void setFieldCellByDirection(Direction direction, Cell cell) {
        AtlasField field = getFieldByDirection(direction);
        field.setCell(cell);
    }
}