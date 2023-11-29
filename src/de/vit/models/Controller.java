package de.vit.models;

import de.vit.models.game.GameMap;
import de.vit.models.game.GameMapField;
import de.vitbund.netmaze.connector.Action;
import de.vitbund.netmaze.info.Cell;
import de.vitbund.netmaze.info.GameInfo;
import de.vitbund.netmaze.info.Result;
import de.vitbund.netmaze.info.RoundInfo;

import java.util.ArrayList;
import java.util.List;

public class Controller {
    private final int playerId;
    private final GameMap gameMap;
    private final boolean isSheetRound;
    private final int maxSheets;
    private int seenSheets = 0;
    private int collectdSheets = 0;
    private int currentRound;
    private GameMapField lastMapField;

    public Controller(GameInfo gameInfo) {
        this.playerId = gameInfo.getPlayerId();
        this.gameMap = new GameMap(gameInfo.getSizeX(), gameInfo.getSizeY(), gameInfo.getStartX(), gameInfo.getStartY());
        this.isSheetRound = gameInfo.getSheets() > 0;
        this.maxSheets = gameInfo.getSheets();

        // Rundennummer setzen
        this.currentRound = 0;
    }

    public Action getNextAction(RoundInfo roundInfo) {
        // Inform about round count
        currentRound = roundInfo.getRoundNumber();
        System.out.println("Runde " + currentRound + " beginnt.");

        Action action = new Action();

        // Info about last action
        Result result = roundInfo.getResult();

        // Info about current cell
        Cell cell = roundInfo.getCellCurrent();

        // Update position on map
        if (currentRound == 1) {
            lastMapField = gameMap.getCurrentField();
        } else {
            switch (result.getResult()) {
                case Result.OK_NORTH -> {
                    lastMapField = gameMap.getCurrentField();
                    gameMap.updateCurrentField(GameMap.Direction.NORTH);
                }
                case Result.OK_EAST -> {
                    lastMapField = gameMap.getCurrentField();
                    gameMap.updateCurrentField(GameMap.Direction.EAST);
                }
                case Result.OK_SOUTH -> {
                    lastMapField = gameMap.getCurrentField();
                    gameMap.updateCurrentField(GameMap.Direction.SOUTH);
                }
                case Result.OK_WEST -> {
                    lastMapField = gameMap.getCurrentField();
                    gameMap.updateCurrentField(GameMap.Direction.WEST);
                }
            }
        }

        if (result.getResult() == Result.OK_FINISH) {
            // Game ended
            System.out.println("Spiel erledigt!");
            System.exit(0);
        } else if (cell.getType() == Cell.FORM && result.getResult() != Result.NOK_WRONGORDER) {
            // Take form
            action.take();
        } else if (cell.getType() == Cell.FINISH) {
            // At finish cell = end game
            System.out.println("Ziel erreicht!");
            action.finish();
        } else {
            // Get next move
            List<CellAction> cells = new ArrayList<>();
            cells.add(new CellAction(roundInfo.getCellEast(), GameMap.Direction.EAST));
            cells.add(new CellAction(roundInfo.getCellNorth(), GameMap.Direction.NORTH));
            cells.add(new CellAction(roundInfo.getCellSouth(), GameMap.Direction.SOUTH));
            cells.add(new CellAction(roundInfo.getCellWest(), GameMap.Direction.WEST));

            for (CellAction c : cells) {
                {
                    Cell handledCell = c.getCell();
                    GameMapField effectedMapField = gameMap.getFieldByDirection(c.getDirection());

                    if ((handledCell.getType() == Cell.FLOOR || handledCell.getType() == Cell.FINISH) && !effectedMapField.equals(lastMapField)) {
                        switch (c.getDirection()) {
                            case EAST -> action.moveEast();
                            case NORTH -> action.moveNorth();
                            case SOUTH -> action.moveSouth();
                            case WEST -> action.moveWest();
                        }
                        break;
                    }
                }
            }
        }
        // action.moveEast();

        return action;
    }
}
