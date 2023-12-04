package de.vit.models;

import de.vit.bot.Bot;
import de.vit.models.utils.Vector2;
import de.vitbund.netmaze.info.Cell;

public class AtlasField {
    private final Vector2 coords;
    private int distance = Integer.MAX_VALUE;
    private int type = 1; // 1 = wall
    private int playerId;

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public boolean isOwnFinishField() {
        return type == Cell.FINISH && playerId == Bot.Controller.getPlayerId();
    }

    // @ToDo: is form and get form number
    // Cell.FORM + Cell.getNumber() [starts at 0] + Cell.getPlayer() [playerId]

    // @ToDo: test if saving cell is needed / a good idea

    // @ToDo: implement own equals method
    // @ToDo: implement own toString method for maps
    // https://stackoverflow.com/questions/65314335/2048-game-java-how-do-i-print-game-board-based-on-users-input
}
