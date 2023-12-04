package de.vit.models;

import de.vit.enums.Direction;
import de.vit.models.utils.Vector2;
import de.vitbund.netmaze.info.Cell;

import java.util.LinkedList;
import java.util.Queue;

public class Atlas {
    // get form char by number (0-25)
    public static final String[] FORMS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");

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

    public AtlasField[][] getFields() {
        return fields;
    }

    public AtlasField getCurrentField() {
        return currentField;
    }

    public AtlasField getLastField() {
        return lastField;
    }

    public void printAtlasMap() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < ydim; y++) {
            for (int x = 0; x < xdim; x++) {
                if (x == currentX && y == currentY) {
                    sb.append("Bot");
                    continue;
                }
                sb.append(fields[x][y].toString());
            }
            sb.append("\n");
        }

        System.out.println(sb);
    }

    public void printPathCostsMap() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < ydim; y++) {
            for (int x = 0; x < xdim; x++) {
                if (fields[x][y].getDistance() == Integer.MAX_VALUE) {
                    sb.append("XXX ");
                    continue;
                }
                sb.append(String.format("%03d", fields[x][y].getDistance())).append(" ");
            }
            sb.append("\n");
        }

        System.out.println(sb);
    }

    public void calculatePathCosts() {
        Queue<AtlasField> queue = new LinkedList<>();
        // reset all distances
        for (AtlasField[] field : fields) {
            for (AtlasField atlasField : field) {
                atlasField.setDistance(Integer.MAX_VALUE);
            }
        }

        queue.add(currentField);
        currentField.setDistance(0);

        while (!queue.isEmpty()) {
            AtlasField field = queue.poll();
            int distance = field.getDistance() + 1;

            // check neighbors is not wall
            for (Direction direction : Direction.values()) {
                AtlasField neighbor = getFieldByDirectionFrom(field.getX(), field.getY(), direction);
                if (neighbor.getType() != Cell.WALL && neighbor.getType() != AtlasField.UNKNWON_FIELD && neighbor.getDistance() > distance) {
                    neighbor.setDistance(distance);
                    neighbor.setDirection(direction);
                    queue.add(neighbor);
                }
            }
        }
    }

    public AtlasField getFieldByDirection(Direction direction) {
        return getFieldByDirectionFrom(currentX, currentY, direction);
    }

    public AtlasField getFieldByDirectionFrom(int x, int y, Direction direction) {
        Vector2 coords = new Vector2(x, y);

        // calculate position of direction
        switch (direction) {
            case NORTH -> coords.y -= 1;
            case EAST -> coords.x += 1;
            case SOUTH -> coords.y += 1;
            case WEST -> coords.x -= 1;
            case SELF -> coords = new Vector2(x, y);
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

    public void setFieldTypeByDirection(Direction direction, Cell cell) {
        AtlasField field = getFieldByDirection(direction);

        int type = cell.getType();
        if (type == Cell.FORM) {
            field.setFormNumber(cell.getNumber());
            field.setPlayerId(cell.getPlayer());
        }

        field.setType(type);
    }
}
