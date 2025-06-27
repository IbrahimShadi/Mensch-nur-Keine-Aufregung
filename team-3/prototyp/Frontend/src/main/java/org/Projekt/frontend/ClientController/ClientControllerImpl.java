package org.Projekt.frontend.ClientController;

import org.Projekt.frontend.ClientModel.ClientModelInterface;

public class ClientControllerImpl implements ClientControllerInterface {

    private ClientModelInterface gameModel;

    public ClientControllerImpl(ClientModelInterface model) {
        this.gameModel = model;
    }

    @Override
    public void rollDice() {
        // Simuliere einen Serveraufruf für das Würfeln
        int diceRoll = (int) (Math.random() * 6) + 1; // Temporärer Würfelwert
        gameModel.setDiceValue(diceRoll);
    }

    @Override
    public void movePiece(int pieceId, int targetPosition) {
        // Aktualisiere das Model mit der neuen Position
        gameModel.movePiece(pieceId, targetPosition);
    }

    @Override
    public void endTurn() {
        // Logik für das Beenden eines Zuges
        gameModel.nextPlayer();
    }
}
