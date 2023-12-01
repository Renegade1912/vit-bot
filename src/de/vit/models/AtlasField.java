package de.vit.models;

import de.vit.bot.Bot;
import de.vitbund.netmaze.info.Cell;

public class AtlasField {
    private final int x;
    private final int y;
    private int distance = Integer.MAX_VALUE;
    private Cell cell;

    public AtlasField(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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
}
