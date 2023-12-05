package de.vit.models;

import de.vit.bot.Bot;
import de.vit.enums.Direction;
import de.vit.models.utils.Vector2;
import de.vitbund.netmaze.info.Cell;

public class AtlasField {
    public static final int UNKNWON_FIELD = -1;

    private final Vector2 coords;
    private int distance = Integer.MAX_VALUE;
    private int type = -1; // 1 = wall, -1 not defined
    private int playerId;
    private int formNumber;

    private Direction direction;

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

    public int getPlayerId() {
        return playerId;
    }

    public boolean isOwnFinishField() {
        return type == Cell.FINISH && playerId == Bot.Controller.getPlayerId();
    }

    public boolean isOwnFormField() {
        return type == Cell.FORM && playerId == Bot.Controller.getPlayerId();
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getFormNumber() {
        return formNumber;
    }

    public void setFormNumber(int formNumber) {
        this.formNumber = formNumber;
    }

    @Override
    public String toString() {
        String result = " W ";
        switch (type) {
            case Cell.FLOOR:
                result = "   ";
                break;
            case Cell.WALL:
                result = " W ";
                break;
            case Cell.FORM:
                result = " " + Atlas.FORMS[formNumber] + playerId;
                break;
            case Cell.SHEET:
                result = " P ";
                break;
            case Cell.FINISH:
                result = " Z ";
                break;
        }

        return result;
    }
}
