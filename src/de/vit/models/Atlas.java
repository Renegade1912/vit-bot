package de.vit.models;

import de.vit.bot.Bot;
import de.vit.enums.Direction;
import de.vit.models.utils.Vector2;
import de.vitbund.netmaze.info.Cell;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

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
                currentField.setExplored(true);
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
                    int tmpDirectionInt = (direction.ordinal() + 2) % 4;
                    Direction tmpDirection = Direction.values()[tmpDirectionInt];
                    neighbor.setDistance(distance);
                    neighbor.setDirection(tmpDirection);
                    queue.add(neighbor);
                }
            }
        }
    }

    public boolean isMapFullyKnown() {
        for (AtlasField[] field : fields) {
            for (AtlasField atlasField : field) {
                // get neighbor fields
                LinkedList<AtlasField> neighbors = getNeighborsFrom(atlasField.getX(), atlasField.getY());

                // check if all neighbors are explored
                for (AtlasField neighbor : neighbors) {
                    if (neighbor.getType() == AtlasField.UNKNWON_FIELD) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public AtlasField getNextExplorableField() throws NullPointerException {
        int closestDistance = Integer.MAX_VALUE;
        AtlasField closestField = null;

        for (AtlasField[] field : fields) {
            for (AtlasField atlasField : field) {
                if (atlasField.getType() == Cell.WALL || atlasField.getType() == AtlasField.UNKNWON_FIELD) {
                    continue;
                }

                // get neighbor fields
                LinkedList<AtlasField> neighbors = getNeighborsFrom(atlasField.getX(), atlasField.getY());

                for (AtlasField neighbor : neighbors) {
                    // check if neighbor is explored
                    if (neighbor.getType() == AtlasField.UNKNWON_FIELD) {
                        // check if neighbor is closer than current closest
                        int distance = atlasField.getDistance();
                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closestField = atlasField;
                        }
                    }
                }
            }
        }

        if (closestField == null) {
            throw new NullPointerException("No unexplored field found");
        }

        return closestField;
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

    public LinkedList<AtlasField> getNeighbors() {
        return getNeighborsFrom(currentX, currentY);
    }

    public LinkedList<AtlasField> getNeighborsFrom(int x, int y) {
        LinkedList<AtlasField> neighbors = new LinkedList<>();
        neighbors.add(getFieldByDirectionFrom(x, y, Direction.NORTH));
        neighbors.add(getFieldByDirectionFrom(x, y, Direction.EAST));
        neighbors.add(getFieldByDirectionFrom(x, y, Direction.SOUTH));
        neighbors.add(getFieldByDirectionFrom(x, y, Direction.WEST));

        return neighbors;
    }

    public void updateCurrentField(Direction direction) {
        lastField = currentField;
        currentField = getFieldByDirection(direction);
        currentField.setExplored(true);
        currentX = currentField.getX();
        currentY = currentField.getY();
    }

    public void setFieldTypeByDirection(Direction direction, Cell cell) {
        AtlasField field = getFieldByDirection(direction);

        int type = cell.getType();
        switch (type) {
            // case Cell.FLOOR, Cell.WALL -> field.setExplored(true);
            case Cell.FORM -> {
                // cell returns its form number and the owning player
                field.setFormNumber(cell.getNumber());
                field.setPlayerId(cell.getPlayer());
            }
            case Cell.SHEET -> System.out.println("Sheet found"); // toDo
            case Cell.FINISH -> {
                field.setPlayerId(cell.getPlayer());

                // finish cell returns the number of forms needed to finish
                if (field.getPlayerId() == Bot.Controller.getPlayerId()) {
                    // form numbers start at 0, so we need to subtract 1
                    Bot.Controller.setNeededFormCount(cell.getNumber() - 1);
                }
            }
        }

        field.setType(type);
    }

    public AtlasField getNextFormField(int nextForm) {
        for (AtlasField[] field : fields) {
            for (AtlasField atlasField : field) {
                if (atlasField.isOwnFormField() && atlasField.getFormNumber() == nextForm) {
                    return atlasField;
                }
            }
        }

        return null;
    }

    public AtlasField getFinishField() {
        for (AtlasField[] field : fields) {
            for (AtlasField atlasField : field) {
                if (atlasField.isOwnFinishField()) {
                    return atlasField;
                }
            }
        }

        return null;
    }
}
