package org.Projekt.springbootapplication.Service;

import lombok.Getter;
import org.Projekt.springbootapplication.ServerModel.GameState;
import org.springframework.stereotype.Service;

@Getter
@Service
public class GameService {
    private final GameState gameState;

    public GameService(GameState gameState) {
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }


    public void createGame(String gameID, int maxPlayers) {
        gameState.setGameID(gameID);
        gameState.setMaxPlayers(maxPlayers);
        gameState.setCurrentPlayer(0); // Noch keine Spieler registriert
        System.out.println("Spiel erstellt mit GameID: " + gameID + ", MaxPlayers: " + maxPlayers);
    }






    public int registerNewPlayer() {
        int newPlayerId = gameState.getCurrentPlayers() + 1; // Spieler-ID basierend auf der Anzahl aktueller Spieler
        if (gameState.isGameFull()) {
            throw new IllegalStateException("Das Spiel ist bereits voll!");
        }
        gameState.incrementPlayers(); // Spieleranzahl erhöhen
        gameState.registerPlayer(newPlayerId); // Spieler registrieren
        return newPlayerId;
    }


    public int rollDice(int playerId) {
        if (!gameState.isPlayerRegistered(playerId)) {
            throw new IllegalStateException("Spieler nicht registriert!");
        }
        if (gameState.getCurrentPlayers() != playerId) {
            throw new IllegalStateException("Nicht dein Zug!");
        }

        int diceValue = (int) (Math.random() * 6) + 1;
        gameState.setDiceValue(diceValue);
        return diceValue;
    }

    public boolean movePiece(int playerId, int pieceId, int targetPosition) {
        if (!gameState.isPlayerRegistered(playerId)) {
            throw new IllegalStateException("Spieler nicht registriert!");
        }
        if (gameState.getCurrentPlayers() != playerId) {
            throw new IllegalStateException("Nicht dein Zug!");
        }

        if (!gameState.isValidMove(pieceId, targetPosition)) {
            throw new IllegalStateException("Ungültiger Zug!");
        }

        gameState.movePiece(pieceId, targetPosition);
        gameState.nextTurn();
        return true;
    }

}
