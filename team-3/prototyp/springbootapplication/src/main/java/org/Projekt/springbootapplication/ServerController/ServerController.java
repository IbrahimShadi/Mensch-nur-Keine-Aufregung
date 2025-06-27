package org.Projekt.springbootapplication.ServerController;

import org.Projekt.springbootapplication.Service.GameService;
import org.Projekt.springbootapplication.ServerModel.GameState;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
public class ServerController {
    private final GameService gameService;

    public ServerController(GameService gameService) {
        this.gameService = gameService;
    }


    @PostMapping("/createGame")
    public ResponseEntity<String> createGame(@RequestParam int maxPlayers) {
        if (maxPlayers < 2 || maxPlayers > 4) {
            return ResponseEntity.badRequest().body("Ungültige Anzahl an Spielern! Wählen Sie zwischen 2 und 4 Spielern.");
        }

        String gameID = String.valueOf((int) (Math.random() * 9000) + 1000); // Zufällige 4-stellige Zahl
        gameService.createGame(gameID, maxPlayers);
        return ResponseEntity.ok(gameID);
    }



    @PostMapping("/joinGame")
        public ResponseEntity<String> joinGame(@RequestParam String gameID) {
            GameState currentState = gameService.getGameState();

            // Überprüfung der GameID
            if (!currentState.getGameID().equals(gameID)) {
                return ResponseEntity.badRequest().body("Ungültige GameID!");
            }

            // Prüfen, ob das Spiel voll ist
            if (currentState.isGameFull()) {
                return ResponseEntity.badRequest().body("Das Spiel ist bereits voll!");
            }

            // Spieler registrieren
            int playerId = currentState.getNextPlayerId();
            currentState.registerPlayer(playerId);
            currentState.incrementPlayers();

            System.out.println("Spieler " + playerId + " ist beigetreten.");

            return ResponseEntity.ok(String.valueOf(playerId)); // Spieler-ID zurückgeben
        }




    @PostMapping("/startGame")
        public ResponseEntity<String> startGame(@RequestParam String gameID) {
            GameState currentState = gameService.getGameState();

            // Prüfen, ob die GameID stimmt
            if (!currentState.getGameID().equals(gameID)) {
                return ResponseEntity.badRequest().body("Ungültige GameID!");
            }

            // Prüfen, ob genug Spieler beigetreten sind
            if (currentState.getCurrentPlayers() < currentState.getMaxPlayers()) {
                return ResponseEntity.badRequest().body("Nicht genug Spieler, um das Spiel zu starten!");
            }

            return ResponseEntity.ok("Spiel gestartet!");
        }

    @PostMapping("/registerPlayer")
    public ResponseEntity<Integer> registerPlayer() {
        int newPlayerId = gameService.registerNewPlayer();
        return ResponseEntity.ok(newPlayerId);
    }


    @GetMapping("/state")
    public ResponseEntity<GameState> getGameState() {
        return ResponseEntity.ok(gameService.getGameState());
    }

    @GetMapping("/currentPlayers")
        public ResponseEntity<Integer> getCurrentPlayers(@RequestParam String gameID) {
            if (!gameID.equals(gameService.getGameState().getGameID())) {
                return ResponseEntity.badRequest().body(-1);
            }
            return ResponseEntity.ok(gameService.getGameState().getCurrentPlayers());
        }



    @PostMapping("/rollDice")
    public ResponseEntity<Integer> rollDice(@RequestParam int playerId) {
        try {
            int diceValue = gameService.rollDice(playerId);
            return ResponseEntity.ok(diceValue);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(-1);
        }
    }

    @PostMapping("/movePiece")
    public ResponseEntity<Boolean> movePiece(
        @RequestParam int playerId,
        @RequestParam int pieceId,
        @RequestParam int targetPosition) {
        try {
            boolean result = gameService.movePiece(playerId, pieceId, targetPosition);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(false);
        }
    }
}
