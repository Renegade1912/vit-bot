package de.vit.models;

import de.vit.models.game.GameMap;
import de.vitbund.netmaze.info.Cell;

public class CellAction {
    private final Cell cell;
    private final GameMap.Direction direction;

    public CellAction(Cell cell, GameMap.Direction direction) {
        this.cell = cell;
        this.direction = direction;
    }

    public Cell getCell() {
        return cell;
    }

    public GameMap.Direction getDirection() {
        return direction;
    }
}
