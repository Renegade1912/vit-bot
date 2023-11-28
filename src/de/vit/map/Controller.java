package de.vit.map;

import de.vitbund.netmaze.connector.Action;
import de.vitbund.netmaze.info.GameEndInfo;
import de.vitbund.netmaze.info.GameInfo;
import de.vitbund.netmaze.info.RoundInfo;

public class Controller {
    private Map map;
    private int sheets;
    private int currentX;
    private int currentY;
    private int currentRound;

    public Controller(GameInfo gameInfo) {
        this.map = new Map(gameInfo.getSizeX(), gameInfo.getSizeY());
        this.sheets = gameInfo.getSheets();
        this.currentX = gameInfo.getStartX();
        this.currentY = gameInfo.getStartY();

        // Rundennummer setzen
        this.currentRound = 0;
    }

    public Action getNextAction(RoundInfo roundInfo) {
        currentRound = roundInfo.getRoundNumber();
        System.out.println("Runde " + currentRound + " beginnt.");

        Action action = new Action();

        System.out.println("Aktuelle Position: " + roundInfo.getCellCurrent().getType());

        return action;
    }
}
