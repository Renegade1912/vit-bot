package de.vit.models;

import de.vit.enums.Direction;
import de.vitbund.netmaze.connector.Action;
import de.vitbund.netmaze.info.Cell;
import de.vitbund.netmaze.info.GameInfo;
import de.vitbund.netmaze.info.Result;
import de.vitbund.netmaze.info.RoundInfo;

import java.util.ArrayList;
import java.util.List;

public class Controller {
    // Our player number [1-4]
    private final int playerId;
    // Current level to determined game rules
    private final int currentLevel;
    // Our map
    private final Atlas atlas;
    // Max number of sheets allowed to place
    private final int maxSheets;
    // Number of forms needed to collect
    private int neededFormCount = Atlas.FORMS.length;
    // Current number of form to collect (if needed)
    private int nextForm = 0;
    // Current round number
    private int currentRound = 0;
    // Current round done (to skip following action checks)
    private boolean roundDone = false;

    /**
     * Erstelle einen neuen Steuerungs-Controller. für unseren Bot.
     * Steuert unseren Bot und seine Aktionen / Informationen.
     *
     * @param gameInfo GameInfo Informationen über das Spiel
     */
    public Controller(GameInfo gameInfo) {
        this.playerId = gameInfo.getPlayerId();
        this.currentLevel = gameInfo.getLevel();
        this.atlas = new Atlas(gameInfo.getSizeX(), gameInfo.getSizeY(), gameInfo.getStartX(), gameInfo.getStartY());
        this.maxSheets = gameInfo.getSheets();
    }

    public int getPlayerId() {
        return playerId;
    }

    public boolean isGameDone() {
        // No form collecting needed
        if (currentLevel == 1) return true;

        return neededFormCount > 0 && nextForm > neededFormCount;
    }

    private void updateGameState(RoundInfo roundInfo) {
        // Success/Fail of last action
        int result = roundInfo.getResult().getResult();

        // Handle result, e.g. update map position
        switch (result) {
            case Result.NOK -> System.out.println("NOK"); // toDo
            case Result.NOK_NOTSUPPORTED -> System.out.println("Not supported"); // toDo
            case Result.NOK_BLOCKED -> System.out.println("Blocked"); // toDo
            case Result.NOK_NOTYOURS -> System.out.println("Not yours"); // toDo
            case Result.NOK_EMPTY -> System.out.println("Empty"); // toDo
            case Result.NOK_WRONGORDER -> System.out.println("Wrong order"); // toDo
            case Result.NOK_TALKING -> System.out.println("Talking"); // toDo
            case Result.OK_NORTH -> atlas.updateCurrentField(Direction.NORTH);
            case Result.OK_EAST -> atlas.updateCurrentField(Direction.EAST);
            case Result.OK_SOUTH -> atlas.updateCurrentField(Direction.SOUTH);
            case Result.OK_WEST -> atlas.updateCurrentField(Direction.WEST);
            case Result.OK_FORM -> nextForm++;
            case Result.OK_SHEET -> System.out.println("Sheet"); // toDo
            case Result.OK_FINISH -> System.exit(0);
        }

        // Bind neighbour cells to AtlasFields
        atlas.setFieldTypeByDirection(Direction.NORTH, roundInfo.getCellNorth());
        atlas.setFieldTypeByDirection(Direction.EAST, roundInfo.getCellEast());
        atlas.setFieldTypeByDirection(Direction.SOUTH, roundInfo.getCellSouth());
        atlas.setFieldTypeByDirection(Direction.WEST, roundInfo.getCellWest());
    }

    public Action getNextAction(RoundInfo roundInfo) {
        // Inform about round count
        currentRound = roundInfo.getRoundNumber();
        System.out.println("##################### Runde " + currentRound + " beginnt #####################");

        // Update game state
        updateGameState(roundInfo);

        // Prepare action
        Action action = new Action();

        // Info about last action
        Result result = roundInfo.getResult();

        // Info about current cell
        Cell cell = roundInfo.getCellCurrent();

        if (cell.getType() == Cell.FORM && result.getResult() != Result.NOK_WRONGORDER) {
            // Take form
            // @ToDo: Implement form checking
            action.take();
        } else if (cell.getType() == Cell.FINISH) {
            // At finish cell = end game
            // @ToDo: Implement better can finish checking
            System.out.println("Ziel erreicht!");
            action.finish();
        } else {
            // Get next move
            // @ToDo: Refactor to remove CellActions
            List<CellAction> cells = new ArrayList<>();
            cells.add(new CellAction(roundInfo.getCellNorth(), Direction.NORTH));
            cells.add(new CellAction(roundInfo.getCellEast(), Direction.EAST));
            cells.add(new CellAction(roundInfo.getCellSouth(), Direction.SOUTH));
            cells.add(new CellAction(roundInfo.getCellWest(), Direction.WEST));

            for (CellAction c : cells) {
                {
                    Cell handledCell = c.getCell();
                    AtlasField affectedMapField = atlas.getFieldByDirection(c.getDirection());

                    if (handledCell.getType() == Cell.FORM) {
                        System.out.println(handledCell.getNumber() + " / " + handledCell.getPlayer());
                    }

                    if ((handledCell.getType() == Cell.FLOOR || handledCell.getType() == Cell.FINISH || handledCell.getType() == Cell.FORM) && !affectedMapField.equals(atlas.getLastField())) {
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

        return action;
    }

    public void setNeededFormCount(int neededFormCount) {
        this.neededFormCount = neededFormCount;
    }
}
