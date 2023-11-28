package de.vit.unlimitedbot;

import de.vit.map.Map;
import de.vitbund.netmaze.connector.Action;
import de.vitbund.netmaze.connector.IBot;
import de.vitbund.netmaze.info.GameEndInfo;
import de.vitbund.netmaze.info.GameInfo;
import de.vitbund.netmaze.info.RoundInfo;

public class UnlimitedBot implements IBot {
    private int playerId;

    private Map map;
    private int currentX;
    private int currentY;
    private int currentLevel;
    private int sheets;
    private int currentRound;

    @Override
    public String getName() {
        return "OfficeBot";
    }

    /**
     * Wird aufgerufen, wenn das Spiel gestartet wurde.
     *
     * @param gameInfo GameInfo Informationen über das Spiel
     */
    @Override
    public void onGameStart(GameInfo gameInfo) {
        // Spieler-ID auslesen
        playerId = gameInfo.getPlayerId();

        // Karte initialisieren
        map = new Map(gameInfo.getSizeX(), gameInfo.getSizeY());

        // Startposition auslesen
        currentX = gameInfo.getStartX();
        currentY = gameInfo.getStartY();

        // Level für Spielregeln auslesen (irgendwie immer 1?)
        currentLevel = gameInfo.getLevel();

        // Anzahl der Formulare ermitteln
        sheets = gameInfo.getSheets();

        // Rundennummer setzen
        currentRound = 0;
    }

    /**
     * Wird aufgerufen, wenn ein neuer Zug ansteht.
     *
     * @param roundInfo RoundInfo Informationen über den aktuellen Zug
     * @return Action, die der Bot ausführen möchte
     */
    @Override
    public Action onNewRound(RoundInfo roundInfo) {
        currentRound = roundInfo.getRoundNumber();
        System.out.println("Runde " + currentRound + " beginnt.");

        Action action = new Action();
        action.moveEast();

        return action;
    }

    /**
     * Wird aufgerufen, wenn das Spiel beendet wurde.
     *
     * @param gameEndInfo GameEndInfo Informationen über das beendete Spiel
     */
    @Override
    public void onGameEnd(GameEndInfo gameEndInfo) {
        System.out.println("Spiel zuende!");
        System.out.println("Gewinner: " + gameEndInfo.getWinner());
        System.out.println("Das Spiel dauerte " + gameEndInfo.getRound() + " Runden.");
    }

    @Override
    public void onError(Exception e) {
        System.out.println("Fehler: " + e.getMessage());
    }


}
