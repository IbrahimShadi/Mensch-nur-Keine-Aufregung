package org.Projekt.springbootapplication.ServerModel;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GameState {
    private int currentPlayer;
    private int diceValue;
    private String gameID;
    private int maxPlayers; // Maximale Spieleranzahl
    private int currentPlayers; // Aktuelle Anzahl der Spieler
    private final List<Integer> registeredPlayers = new ArrayList<>();

    public GameState() {
        this.currentPlayer = -1; // Kein Spieler zu Beginn
        this.currentPlayers = 0; // Keine Spieler registriert
    }
    private int nextPlayerId = 1; // Zählt die Spieler-IDs hoch

    public int getNextPlayerId() {
        return nextPlayerId++;
    }


    public void incrementPlayers() {
        this.currentPlayers++;
    }

    public boolean isGameFull() {
        return currentPlayers >= maxPlayers;
    }

    public void registerPlayer(int playerId) {
        if (registeredPlayers.contains(playerId)) {
            throw new IllegalStateException("Spieler bereits registriert!");
        }
        registeredPlayers.add(playerId);
    }

    public boolean isPlayerRegistered(int playerId) {
    return registeredPlayers.contains(playerId);
}

    private final Map<Integer, Integer> piecePositions = new HashMap<>();

    public Map<Integer, Integer> getPiecePositions() {
        return piecePositions;
    }


    public void movePiece(int pieceId, int targetPosition) {
        piecePositions.put(pieceId, targetPosition);

    }

    public boolean isValidMove(int pieceId, int targetPosition) {
        return targetPosition >= 0 && targetPosition <= 100 && !piecePositions.containsValue(targetPosition);
    }

    public void nextTurn() {
        int index = registeredPlayers.indexOf(currentPlayer);
        if (index != -1 && index < registeredPlayers.size() - 1) {
            currentPlayer = registeredPlayers.get(index + 1);
        } else {
            currentPlayer = registeredPlayers.get(0); // Zurück zum ersten Spieler
        }
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getCurrentPlayers() {
        return currentPlayers;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }


    public int getDiceValue() {
        return diceValue;
    }

    public void setDiceValue(int diceValue) {
        this.diceValue = diceValue;
    }

}
