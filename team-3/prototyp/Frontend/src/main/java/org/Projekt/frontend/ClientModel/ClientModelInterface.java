package org.Projekt.frontend.ClientModel;

public interface ClientModelInterface {
    int getDiceValue();
    void setDiceValue(int diceValue);

    int getPiecePosition(int pieceId);
    void movePiece(int pieceId, int newPosition);

    int getCurrentPlayer();
    void setPlayerId(int playerId); // Hinzugefügt
    int getPlayerId(); // Hinzugefügt

    void nextPlayer();
}
