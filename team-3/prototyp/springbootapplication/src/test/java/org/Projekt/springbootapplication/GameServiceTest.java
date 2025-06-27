package org.Projekt.springbootapplication;

import org.junit.jupiter.api.*;
import org.Projekt.springbootapplication.ServerModel.GameState;
import org.Projekt.springbootapplication.Service.GameService;

public class GameServiceTest {

    private GameService gameService;
    private GameState gameState;

    @BeforeEach
    public void setUp() {
        gameState = new GameState();
        gameService = new GameService(gameState);
    }

    @Test
    public void testInitialGameState() {
        System.out.println("Testing initial game state...");

        int currentPlayer = gameState.getCurrentPlayer();
        Assertions.assertEquals(1, currentPlayer);
        System.out.println("Current Player: " + currentPlayer);

        int diceValue = gameState.getDiceValue();
        Assertions.assertEquals(0, diceValue);
        System.out.println("Initial Dice Value: " + diceValue);

        boolean allPiecesAtStart = true;
        for (int i = 1; i <= 16; i++) {
            if (gameState.getPiecePositions().get(i) != 0) {
                allPiecesAtStart = false;
                break;
            }
        }
        Assertions.assertTrue(allPiecesAtStart);
        System.out.println("All pieces start at position 0: " + allPiecesAtStart);

        System.out.println("Initial game state tested successfully.");
    }

    @Test
    public void testRollDice() {
        gameState.setCurrentPlayer(1);
        int diceValue = gameService.rollDice(1);
        System.out.println("Rolled dice value: " + diceValue);
        Assertions.assertTrue(diceValue >= 1 && diceValue <= 6);
    }

    @Test
    public void testMovePiece() {
        System.out.println("Testing move piece...");
        gameState.setCurrentPlayer(1);
        int targetPosition = 5;
        boolean moved = gameService.movePiece(1, 1, targetPosition);
        Assertions.assertTrue(moved);
        Assertions.assertEquals(targetPosition, gameState.getPiecePositions().get(1));
        System.out.println("Piece moved to target position: " + targetPosition);
    }

    @Test
    public void testTurnChange() {
        System.out.println("Testing turn change...");
        gameState.setCurrentPlayer(1);
        gameService.movePiece(1, 1, 5);
        int currentPlayer = gameState.getCurrentPlayer();
        Assertions.assertEquals(2, currentPlayer);
        System.out.println("Turn change test passed. Current player is now: " + currentPlayer);
    }
}