package org.Projekt.frontend.ClientModel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ClientModelImpl implements ClientModelInterface {
    private int diceValue;
    private int currentPlayer;
    private int playerId;
    private final Map<Integer, Integer> piecePositions;

    // Angepasster Konstruktor mit gameID als Parameter
    public ClientModelImpl(String gameID) {
        this.diceValue = 0;
        this.currentPlayer = 1;
        this.piecePositions = new HashMap<>();
        initializePieces();

        // Spieler-ID vom Backend mit der gameID abrufen
        this.playerId = fetchPlayerIdFromBackend(gameID);
    }

    private void initializePieces() {
        for (int i = 1; i <= 16; i++) {
            piecePositions.put(i, 0);
        }
    }

    @Override
    public int getDiceValue() {
        return diceValue;
    }

    @Override
    public void setDiceValue(int diceValue) {
        this.diceValue = diceValue;
    }

    @Override
    public int getPiecePosition(int pieceId) {
        return piecePositions.getOrDefault(pieceId, -1);
    }

    @Override
    public void movePiece(int pieceId, int newPosition) {
        piecePositions.put(pieceId, newPosition);
    }

    @Override
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    @Override
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    @Override
    public int getPlayerId() {
        return playerId;
    }

    @Override
    public void nextPlayer() {
        currentPlayer = (currentPlayer % 4) + 1;
    }

    private int fetchPlayerIdFromBackend(String gameID) {
        try {
            URL url = new URL("http://localhost:8080/api/game/joinGame?gameID=" + gameID);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            if (connection.getResponseCode() == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    return Integer.parseInt(reader.readLine());
                }
            } else {
                throw new RuntimeException("Fehler beim Beitritt zum Spiel: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Fehlerfall
        }
    }
}
