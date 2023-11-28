package de.vit.unlimitedbot;

import de.vit.map.Controller;
import de.vitbund.netmaze.connector.Action;
import de.vitbund.netmaze.connector.IBot;
import de.vitbund.netmaze.info.GameEndInfo;
import de.vitbund.netmaze.info.GameInfo;
import de.vitbund.netmaze.info.RoundInfo;

public class UnlimitedBot implements IBot {
    private int playerId;
    private Controller controller;
    private int currentLevel;

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

        // Controller initialisieren
        controller = new Controller(gameInfo);


        // Level für Spielregeln auslesen (irgendwie immer 1?)
        currentLevel = gameInfo.getLevel();


    }

    /**
     * Wird aufgerufen, wenn ein neuer Zug ansteht.
     *
     * @param roundInfo RoundInfo Informationen über den aktuellen Zug
     * @return Action, die der Bot ausführen möchte
     */
    @Override
    public Action onNewRound(RoundInfo roundInfo) {
        return controller.getNextAction(roundInfo);
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
        System.exit(0);
    }

    @Override
    public void onError(Exception e) {
        System.out.println("Fehler: " + e.getMessage());
    }


}
