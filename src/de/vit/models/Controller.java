package de.vit.models;

import de.vit.enums.Direction;
import de.vitbund.netmaze.connector.Action;
import de.vitbund.netmaze.info.Cell;
import de.vitbund.netmaze.info.GameInfo;
import de.vitbund.netmaze.info.Result;
import de.vitbund.netmaze.info.RoundInfo;

import java.rmi.UnexpectedException;
import java.util.LinkedList;
import java.util.Queue;

public class Controller {
    // Our player number [1-4]
    private final int playerId;
    // Current level to determined game rules
    private final int currentLevel;
    // Our map
    private final Atlas atlas;
    // current routes
    private final Queue<AtlasField> routes = new LinkedList<>();
    // Max number of sheets allowed to place
    private int availableSheets;
    // Number of forms needed to collect
    private int neededFormCount = Atlas.FORMS.length;
    // Current number of form to collect (if needed)
    private int nextForm = 0;
    private int searchRadius = 0;
    private boolean isFormRoute = false;

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
        this.availableSheets = gameInfo.getSheets();

        System.out.println(gameInfo.getSheets());
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setNeededFormCount(int neededFormCount) {
        this.neededFormCount = neededFormCount;
    }

    public void addRoute(AtlasField field) {
        if (!routes.contains(field)) {
            routes.add(field);
        }
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
        // toDo
        // toDo
        // toDo
        // toDo
        // toDo
        // toDo
        switch (result) {
            case Result.NOK:
                System.out.println("NOK");
                break;
            case Result.NOK_NOTSUPPORTED:
                System.out.println("Not supported");
                break;
            case Result.NOK_BLOCKED:
                System.out.println("Went into a wall (unexpected)");
                break;
            case Result.NOK_NOTYOURS:
                System.out.println("Not yours");
                break;
            case Result.NOK_EMPTY:
                System.out.println("Empty");
                break;
            case Result.NOK_WRONGORDER:
                System.out.println("Wrong order");
                break;
            case Result.NOK_TALKING:
                System.out.println("Talking");
                break;
            case Result.OK_NORTH:
                atlas.updateCurrentField(Direction.NORTH);
                break;
            case Result.OK_EAST:
                atlas.updateCurrentField(Direction.EAST);
                break;
            case Result.OK_SOUTH:
                atlas.updateCurrentField(Direction.SOUTH);
                break;
            case Result.OK_WEST:
                atlas.updateCurrentField(Direction.WEST);
                break;
            case Result.OK_FORM:
                nextForm++;
                isFormRoute = false;
                break;
            case Result.OK_SHEET:
                System.out.println("Placed sheet on " + Atlas.FORMS[atlas.getCurrentField().getFormNumber()] + atlas.getCurrentField().getPlayerId());
                availableSheets--;
                break;
            case Result.OK_FINISH:
                System.exit(0);
        }

        // Bind neighbour cells to AtlasFields
        atlas.setFieldTypeByDirection(Direction.NORTH, roundInfo.getCellNorth());
        atlas.setFieldTypeByDirection(Direction.EAST, roundInfo.getCellEast());
        atlas.setFieldTypeByDirection(Direction.SOUTH, roundInfo.getCellSouth());
        atlas.setFieldTypeByDirection(Direction.WEST, roundInfo.getCellWest());
    }

    public Action getNextAction(RoundInfo roundInfo) throws IllegalStateException {
        // Inform about round count
        System.out.println("##################### Runde " + roundInfo.getRoundNumber() + " beginnt #####################");

        // Update game state
        updateGameState(roundInfo);

        // Calculate paths
        atlas.calculatePathCosts();

        // Prepare action
        Action action = new Action();

        // Info about current cell
        Cell cell = roundInfo.getCellCurrent();
        int cellType = cell.getType();
        AtlasField currentField = atlas.getCurrentField();

        // At own finish cell and game is done = end game
        if (cellType == Cell.FINISH && currentField.isOwnFinishField() && isGameDone()) {
            System.out.println("Ziel erreicht!");
            action.finish();

            return action;
        }

        // Can finish game and not on finish pos but known = move to finish
        AtlasField finishField = atlas.getFinishField();
        if (isGameDone() && finishField != null) {
            routes.clear();
            addRoute(finishField);
        }

        // On Sheet
        if (cellType == Cell.SHEET) {
            // toDo: we know that we are on a sheet and dont know if our form is under it, we need to check if it is ours
        }

        // On Form
        if (cellType == Cell.FORM) {
            if (currentField.isOwnFormField() && nextForm == cell.getNumber()) {
                // Own next form = take it
                action.take();
                return action;
            } else {
                //  Other players form
                if (availableSheets > 0 && currentLevel == 5) {
                    // can place sheet
                    action.put();
                    return action;
                } else if (currentLevel >= 4) {
                    // toDo: unstable
                    // toDo: check where is form now??? not that we care >:D

                    // Get the current field neighbors from atlas
                    LinkedList<AtlasField> neighbors = atlas.getNeighbors();
                    for (AtlasField neighbor : neighbors) {
                        if (neighbor.getType() == Cell.FLOOR) {
                            // neighbor is the form we are on
                            Direction tmpDirection = null;
                            try {
                                tmpDirection = atlas.getDirectionToFieldFromCurrent(neighbor);
                            } catch (UnexpectedException e) {
                                throw new RuntimeException(e);
                            }

                            switch (tmpDirection) {
                                case NORTH:
                                    action.kickNorth();
                                    break;
                                case EAST:
                                    action.kickEast();
                                    break;
                                case SOUTH:
                                    action.kickSouth();
                                    break;
                                case WEST:
                                    action.kickWest();
                                    break;
                            }
                            return action;
                        }
                    }
                }
            }
        }


        AtlasField nextFormField = atlas.getNextFormField(nextForm);
        if (nextFormField != null) {
            // next form known?
            addRoute(nextFormField);
            isFormRoute = true;
        }

        // Explore map?
        if (!atlas.isMapFullyKnown() && routes.isEmpty()) {
            addRoute(atlas.getNextExplorableField());
        }

        // Check for routes
        if (!routes.isEmpty()) {
            AtlasField nextField = routes.poll();
            // get list of currentfield neighbors
            LinkedList<AtlasField> neighbors = atlas.getNeighbors();

            // init with nextField as origin
            AtlasField nextFieldOrigin = nextField;

            // check if next field origin is in neighbors
            while (!neighbors.contains(nextFieldOrigin)) {
                // not in neighbors, move to nextFieldOrigin´s origin
                nextFieldOrigin = atlas.getFieldByDirectionFrom(nextFieldOrigin.getX(), nextFieldOrigin.getY(), nextFieldOrigin.getDirection());
            }

            int tmpDirectionInt = (nextFieldOrigin.getDirection().ordinal() + 2) % 4;
            Direction tmpDirection = Direction.values()[tmpDirectionInt];

            switch (tmpDirection) {
                case NORTH:
                    action.moveNorth();
                    break;
                case EAST:
                    action.moveEast();
                    break;
                case SOUTH:
                    action.moveSouth();
                    break;
                case WEST:
                    action.moveWest();
                    break;
            }

            return action;
        }


        // No action found
        throw new IllegalStateException("No action found!");
    }
}
