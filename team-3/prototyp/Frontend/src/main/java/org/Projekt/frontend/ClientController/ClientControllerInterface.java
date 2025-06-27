package org.Projekt.frontend.ClientController;

public interface ClientControllerInterface {
    void rollDice(); // Würfeln
    void movePiece(int pieceId, int targetPosition); // Figur bewegen
    void endTurn(); // Zug beenden
}
