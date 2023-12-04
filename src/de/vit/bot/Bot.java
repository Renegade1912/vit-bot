package de.vit.bot;

import de.vit.models.Controller;
import de.vitbund.netmaze.connector.Action;
import de.vitbund.netmaze.connector.IBot;
import de.vitbund.netmaze.connector.NetMazeConnector;
import de.vitbund.netmaze.info.GameEndInfo;
import de.vitbund.netmaze.info.GameInfo;
import de.vitbund.netmaze.info.RoundInfo;

public class Bot implements IBot {
    public static Controller Controller;

    @Override
    public String getName() {
        return "MODX";
    }

    /**
     * Wird aufgerufen, wenn das Spiel gestartet wurde.
     *
     * @param gameInfo GameInfo Informationen über das Spiel
     */
    @Override
    public void onGameStart(GameInfo gameInfo) {
        // Controller initialisieren
        Controller = new Controller(gameInfo);
    }

    /**
     * Wird aufgerufen, wenn ein neuer Zug ansteht.
     *
     * @param roundInfo RoundInfo Informationen über den aktuellen Zug
     * @return Action, die der Bot ausführen möchte
     */
    @Override
    public Action onNewRound(RoundInfo roundInfo) {
        return Controller.getNextAction(roundInfo);
    }

    /**
     * Wird aufgerufen, wenn das Spiel beendet wurde.
     *
     * @param gameEndInfo GameEndInfo Informationen über das beendete Spiel
     */
    @Override
    public void onGameEnd(GameEndInfo gameEndInfo) {
        System.out.println("Spiel zuende!");
        System.out.println(gameEndInfo.getWinner() == Controller.getPlayerId() ? "Wir haben gewonnen!" : "Wir haben verloren!");
        System.out.println("Das Spiel dauerte " + gameEndInfo.getRound() + " Runden.");
        System.exit(0);
    }

    @Override
    public void onError(Exception e) {
        System.err.println("Fehler: " + e.getMessage());
    }

    public static void main(String[] args) {
        Bot bot = new Bot();
        NetMazeConnector connector = new NetMazeConnector(bot);

        // connector.showConnectionSettingsDialog();

        connector.play();
    }
}
