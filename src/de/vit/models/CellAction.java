package de.vit.models;

import de.vit.enums.Direction;
import de.vitbund.netmaze.info.Cell;

public class CellAction {
    private final Cell cell;
    private final Direction direction;

    public CellAction(Cell cell, Direction direction) {
        this.cell = cell;
        this.direction = direction;
    }

    public Cell getCell() {
        return cell;
    }

    public Direction getDirection() {
        return direction;
    }
}
