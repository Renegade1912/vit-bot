package de.vit.models;

import de.vit.enums.Direction;
import de.vitbund.netmaze.connector.Action;
import de.vitbund.netmaze.info.Cell;
import de.vitbund.netmaze.info.GameInfo;
import de.vitbund.netmaze.info.Result;
import de.vitbund.netmaze.info.RoundInfo;

import java.rmi.UnexpectedException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

/*
    toDo:
    - implement player radar
    - check if field is updated on talking?
*/

public class Controller {
    // Our map
    public final Atlas atlas;
    // Our player number [1-4]
    private final int playerId;
    // Current level to determined game rules
    private final int currentLevel;
    // current routes
    private final Queue<AtlasField> routes = new LinkedList<>();
    // Max number of sheets allowed to place
    private int availableSheets;
    // Number of forms needed to collect
    private int neededFormCount = Atlas.FORMS.length;
    // Current number of form to collect (if needed)
    private int nextForm = 0;
    private boolean lastActionWasKick = false;
    private boolean lastActionWasSheet = false;
    private int searchRadius = 1;

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
        this.availableSheets = currentLevel == 5 ? 3 : 0; // gameInfo.getSheets();
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

        if (result == Result.OK) {
            if (lastActionWasSheet) {
                availableSheets--;
                lastActionWasSheet = false;
            }
        }

        // Handle result, e.g. update map position
        switch (result) {
            case Result.NOK:
                System.out.println("NOK");
                break;
            case Result.NOK_NOTSUPPORTED:
                System.out.println("Not supported");
                break;
            case Result.NOK_BLOCKED:
                System.out.println("Went into a wall (unexpected)");
                // toDo: Check if we kicked into startfield
                // System.exit(1);
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
                if (!lastActionWasKick)
                    atlas.updateCurrentField(Direction.NORTH);

                lastActionWasKick = false;
                break;
            case Result.OK_EAST:
                if (!lastActionWasKick)
                    atlas.updateCurrentField(Direction.EAST);

                lastActionWasKick = false;
                break;
            case Result.OK_SOUTH:
                if (!lastActionWasKick)
                    atlas.updateCurrentField(Direction.SOUTH);

                lastActionWasKick = false;
                break;
            case Result.OK_WEST:
                if (!lastActionWasKick)
                    atlas.updateCurrentField(Direction.WEST);

                lastActionWasKick = false;
                break;
            case Result.OK_FORM:
                nextForm++;
                break;
            case Result.OK_SHEET:
                availableSheets++;
                break;
            case Result.OK_FINISH:
                System.exit(0);
        }

        // Update current cell type
        atlas.setFieldTypeByDirection(Direction.SELF, roundInfo.getCellCurrent());

        // Bind neighbour cells to AtlasFields
        atlas.setFieldTypeByDirection(Direction.NORTH, roundInfo.getCellNorth());
        atlas.setFieldTypeByDirection(Direction.EAST, roundInfo.getCellEast());
        atlas.setFieldTypeByDirection(Direction.SOUTH, roundInfo.getCellSouth());
        atlas.setFieldTypeByDirection(Direction.WEST, roundInfo.getCellWest());
    }

    public Action getNextAction(RoundInfo roundInfo) throws IllegalStateException {
        try {
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
                if ((currentField.getPlayerId() == -1 || currentField.getFormNumber() == -1) || currentField.isOwnFormField()) {
                    // we dont know if our form is under it
                    action.take();
                    System.out.println("Versuche Papier zu nehmen.");
                    return action;
                }
            }

            // On Form
            if (cellType == Cell.FORM) {
                if (currentField.isOwnFormField() && nextForm == cell.getNumber()) {
                    // Own next form = take it
                    action.take();
                    System.out.println("Versuche Formular " + Atlas.FORMS[nextForm] + " zu nehmen.");
                    return action;
                } else if (!currentField.isOwnFormField()) {
                    //  Other players form
                    if (availableSheets > 0) {
                        // can place sheet
                        // 40 percentage chance
                        if (Math.random() > 0.6) {
                        action.put();
                        lastActionWasSheet = true;
                        System.out.println("Versuche Papier zu platzieren.");
                        return action;
                        }
                    }

                    if (currentLevel >= 4) {
                        Direction direction = getFreeDirection();
                        if (direction != null) {
                            switch (direction) {
                                case NORTH:
                                    System.out.println("Versuche nach Norden zu kicken.");
                                    action.kickNorth();
                                    break;
                                case EAST:
                                    System.out.println("Versuche nach Osten zu kicken.");
                                    action.kickEast();
                                    break;
                                case SOUTH:
                                    System.out.println("Versuche nach Süden zu kicken.");
                                    action.kickSouth();
                                    break;
                                case WEST:
                                    System.out.println("Versuche nach Westen zu kicken.");
                                    action.kickWest();
                                    break;
                            }

                            lastActionWasKick = true;

                            return action;
                        }
                    }
                }
            }


            AtlasField nextFormField = atlas.getNextFormField(nextForm);
            if (nextFormField != null) {
                // next form known?
                addRoute(nextFormField);
            }

            // Explore map?
            if (routes.isEmpty()) {
                try {
                    AtlasField tmpField = atlas.getNextExplorableField();
                    routes.add(tmpField);
                } catch (NullPointerException e) {
                    System.out.println("Kein Ziel gefunden!");
                    System.out.println("Setze Felder für Radius " + searchRadius + " zurück.");
                    atlas.resetExploredForRadius(++searchRadius);
                    atlas.calculatePathCosts();
                    routes.add(atlas.getNextExplorableField());
                }
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
                        System.out.println("Versuche nach Norden zu gehen.");
                        action.moveNorth();
                        break;
                    case EAST:
                        System.out.println("Versuche nach Osten zu gehen.");
                        action.moveEast();
                        break;
                    case SOUTH:
                        System.out.println("Versuche nach Süden zu gehen.");
                        action.moveSouth();
                        break;
                    case WEST:
                        System.out.println("Versuche nach Westen zu gehen.");
                        action.moveWest();
                        break;
                }

                return action;
            }

            // No action found
            throw new IllegalStateException("No action found!");
        } catch (Exception e) {
            atlas.printAtlasMap();
            atlas.printPathCostsMap();
            throw new IllegalStateException(e);
        }
    }

    public Direction getFreeDirection() {
        // Get the current field neighbors from atlas
        LinkedList<AtlasField> neighbors = atlas.getNeighbors();
        Collections.shuffle(neighbors);

        Direction tmpDirection = null;
        for (AtlasField neighbor : neighbors) {
            if (neighbor.getType() == Cell.FLOOR) {
                try {
                    tmpDirection = atlas.getDirectionToFieldFromCurrent(neighbor);
                    break;
                } catch (UnexpectedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return tmpDirection;
    }
}
