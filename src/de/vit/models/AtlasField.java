package de.vit.models;

import de.vit.bot.Bot;
import de.vit.models.utils.Vector2;
import de.vitbund.netmaze.info.Cell;

public class AtlasField {
    private final Vector2 coords;
    private int distance = Integer.MAX_VALUE;
    private Cell cell;

    public AtlasField(int x, int y) {
        this.coords = new Vector2(x, y);
    }

    public int getX() {
        return coords.x;
    }

    public int getY() {
        return coords.y;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public boolean isOwnFinishField() {
        return cell.getType() == Cell.FINISH && cell.getPlayer() == Bot.Controller.getPlayerId();
    }

    // @ToDo: is form and get form number
    // Cell.FORM + Cell.getNumber() [starts at 0] + Cell.getPlayer() [playerId]

    // @ToDo: test if saving cell is needed / a good idea

    // @ToDo: implement own equals method
}
