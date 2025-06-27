package org.Projekt.frontend.ClientView;


import org.Projekt.frontend.ClientController.ClientControllerInterface;
import org.Projekt.frontend.ClientModel.ClientModelInterface;


import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class ClientView {
    private JFrame frame;
    private JPanel boardPanel;
    private JButton rollDiceButton;
    private JLabel diceValueLabel, currentPlayerLabel;
    private ClientControllerInterface gameController;
    private ClientModelInterface gameModel;
    private static final double window_width = 900;
    private static final double window_height = 900;
    private JFrame searchFrame; // Instanzvariable für das Fenster



    public ClientView(ClientControllerInterface gameController, ClientModelInterface gameModel) {
        this.gameController = gameController;
        this.gameModel = gameModel;
        createMenu();
    }

    private void createMenu() {
        frame = new JFrame("Nur keine Aufregung - Menü");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize((int) window_width, (int) window_height);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Replace with the path to your background image.
                ImageIcon background = new ImageIcon("Frontend/src/main/resources/images/Menu_BG.jpeg");
                g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(null);
        panel.setOpaque(false);

        JLabel title = new JLabel("Nur Keine Aufregung!");
        title.setFont(new Font("Arial", Font.BOLD, 48));
        title.setForeground(Color.WHITE);
        title.setBounds((200), (100), 500, 60); // Set position and size
        panel.add(title);

        JButton searchGameButton = createStyledButton("Spiel Suchen");
        searchGameButton.setBounds((300), (400), 300, 60); // Set position and size
        panel.add(searchGameButton);

        JButton createGameButton = createStyledButton("Spiel Erstellen");
        createGameButton.setBounds((300), (500), 300, 60); // Set position and size
        panel.add(createGameButton);

        JButton quitButton = createStyledButton("Quit");
        quitButton.setBounds((300), (650), 300, 60); // Set position and size
        panel.add(quitButton);

        searchGameButton.addActionListener(e -> {
            System.out.println("Spiel suchen...");
            frame.dispose();
            createSearchGameMenu(); // Öffne das Fenster zur Eingabe der GameID
        });


        createGameButton.addActionListener(e -> {
            System.out.println("Spiel erstellen...");
            try {
                int maxPlayers = getMaxPlayersFromUser(); // Benutzer wählt Anzahl der Spieler
                String gameID = createGameOnBackend(maxPlayers);
                JOptionPane.showMessageDialog(frame, "Spiel erstellt! GameID: " + gameID, "Spiel Erstellen", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                createGameLobby(gameID, maxPlayers); // Öffne die Lobby
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Fehler beim Erstellen des Spiels!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });



        quitButton.addActionListener(e -> {
            System.out.println("Spiel beendet.");
            System.exit(0);
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    private void createGameLobby(String gameID, int maxPlayers) {
        JFrame lobbyFrame = new JFrame("Spiel Lobby");
        lobbyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        lobbyFrame.setSize((int) window_width, (int) window_height);
        lobbyFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("Spiel erstellt!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setBounds(200, 50, 500, 60);
        panel.add(titleLabel);

        JLabel gameIdLabel = new JLabel("GameID: " + gameID);
        gameIdLabel.setFont(new Font("Arial", Font.BOLD, 36));
        gameIdLabel.setBounds(250, 150, 400, 40);
        panel.add(gameIdLabel);

        JLabel playersLabel = new JLabel("Spieler: 1 von " + maxPlayers);
        playersLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        playersLabel.setBounds(250, 250, 400, 40);
        panel.add(playersLabel);

        JButton startButton = createStyledButton("Spiel starten");
        startButton.setBounds(300, 400, 200, 50);
        startButton.setEnabled(false); // Spiel kann erst gestartet werden, wenn alle Spieler beigetreten sind
        panel.add(startButton);

        // Aktualisiere die Spieleranzahl bei jedem neuen Spieler
        new Timer(1000, e -> {
            try {
                int currentPlayers = getCurrentPlayersFromBackend(gameID);
                playersLabel.setText("Spieler: " + currentPlayers + " von " + maxPlayers);
                startButton.setEnabled(currentPlayers == maxPlayers);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();


        lobbyFrame.add(panel);
        lobbyFrame.setVisible(true);
    }

    private void startGameOnBackend(String gameID) throws Exception {
        URL url = new URL("http://localhost:8080/api/game/startGame?gameID=" + gameID);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        if (connection.getResponseCode() != 200) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                String errorMessage = reader.readLine();
                throw new RuntimeException(errorMessage);
            }
        }
    }



    private int getCurrentPlayersFromBackend(String gameID) throws Exception {
        URL url = new URL("http://localhost:8080/api/game/currentPlayers?gameID=" + gameID);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() == 200) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                return Integer.parseInt(reader.readLine());
            }
        } else {
            throw new RuntimeException("Fehler beim Abrufen der Spieleranzahl!");
        }
    }



    private int getMaxPlayersFromUser() {
        String[] options = {"2 Spieler", "3 Spieler", "4 Spieler"};
        int choice = JOptionPane.showOptionDialog(frame, "Wählen Sie die maximale Spieleranzahl:",
                "Spiel erstellen", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        return choice + 2; // 0 -> 2 Spieler, 1 -> 3 Spieler, 2 -> 4 Spieler
    }

    private void createGameLobby(String gameID) {
        JFrame lobbyFrame = new JFrame("Spiel Lobby");
        lobbyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        lobbyFrame.setSize((int) window_width, (int) window_height);
        lobbyFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Optional: Hintergrundbild oder Farbe setzen
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("Spiel erstellt!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setBounds(200, 50, 500, 60);
        panel.add(titleLabel);

        JLabel gameIdLabel = new JLabel("GameID: " + gameID);
        gameIdLabel.setFont(new Font("Arial", Font.BOLD, 36));
        gameIdLabel.setBounds(250, 200, 400, 40);
        panel.add(gameIdLabel);

        JLabel infoLabel = new JLabel("Teilen Sie diese GameID mit anderen Spielern.");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        infoLabel.setBounds(150, 300, 600, 40);
        panel.add(infoLabel);

        JButton startButton = createStyledButton("Spiel starten");
        startButton.setBounds(300, 400, 200, 50);
        panel.add(startButton);

        startButton.addActionListener(e -> {
        try {
            startGameOnBackend(gameID);
            JOptionPane.showMessageDialog(null, "Spiel wurde gestartet!", "Erfolg", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    });



        lobbyFrame.add(panel);
        lobbyFrame.setVisible(true);
    }

    private void createGameView() {
        JFrame gameFrame = new JFrame("Nur keine Aufregung - Spiel");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setSize(800, 800);
        gameFrame.setLayout(new BorderLayout());

        // Obere Anzeigeleiste
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 2));

        JLabel currentPlayerLabel = new JLabel("Aktueller Spieler: Spieler " + gameModel.getCurrentPlayer(), SwingConstants.CENTER);
        JLabel diceValueLabel = new JLabel("Würfel: ", SwingConstants.CENTER);
        topPanel.add(currentPlayerLabel);
        topPanel.add(diceValueLabel);

        JButton rollDiceButton = new JButton("Würfeln");
        rollDiceButton.addActionListener(e -> {
            try {
                int diceValue = rollDiceFromBackend(); // Würfelwert vom Backend abrufen
                diceValueLabel.setText("Würfel: " + diceValue);

                // Spielstatus aktualisieren
                updateGameStateFromBackend();

                // Automatische Spieleraktion nach dem Würfeln
                performPlayerTurn();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(gameFrame, "Fehler beim Würfeln!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        topPanel.add(rollDiceButton);

        gameFrame.add(topPanel, BorderLayout.NORTH);

        // Spielfeld
        JPanel boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGameBoard(g);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(gameFrame.getWidth(), gameFrame.getHeight() - 150);
            }
        };
        gameFrame.add(boardPanel, BorderLayout.CENTER);

        gameFrame.setVisible(true);
    }


    private String createGameOnBackend(int maxPlayers) throws Exception {
        URL url = new URL("http://localhost:8080/api/game/createGame?maxPlayers=" + maxPlayers);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        if (connection.getResponseCode() == 200) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                return reader.readLine(); // GameID vom Server zurückgeben
            }
        } else {
            throw new RuntimeException("Fehler beim Erstellen des Spiels: " + connection.getResponseCode());
        }
    }



    private void createSearchGameMenu() {
        searchFrame = new JFrame("Spiel Suchen");
        searchFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        searchFrame.setSize((int) window_width, (int) window_height);
        searchFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("GameID Eingeben:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setBounds(300, 200, 400, 40);
        panel.add(titleLabel);

        JTextField gameIdField = new JTextField();
        gameIdField.setBounds(300, 250, 265, 40);
        panel.add(gameIdField);

        JButton continueButton = new JButton("Weiter");
        continueButton.setBounds(300, 350, 150, 50);
        panel.add(continueButton);

        // ActionListener für "Weiter"-Button
        continueButton.addActionListener(e -> {
            String gameID = gameIdField.getText(); // GameID aus dem Textfeld abrufen
            if (!gameID.isEmpty()) {
                int playerId = fetchPlayerIdFromBackend(gameID); // Backend-Abfrage für Beitritt
                if (playerId != -1) {
                    System.out.println("Spieler erfolgreich beigetreten mit ID: " + playerId);
                    searchFrame.dispose(); // Fenster schließen
                    createPlayerNameInputWindow1(); // Weiter zum Spielernamen-Fenster
                } else {
                    JOptionPane.showMessageDialog(searchFrame, "Fehler beim Beitritt zum Spiel. Überprüfen Sie die GameID.", "Fehler", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(searchFrame, "Bitte geben Sie eine gültige GameID ein.", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });

        searchFrame.add(panel);
        searchFrame.setVisible(true);
    }




    private void joinGameOnBackend(String gameID) throws Exception {
        URL url = new URL("http://localhost:8080/api/game/joinGame?gameID=" + gameID);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        if (connection.getResponseCode() != 200) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                String errorMessage = reader.readLine();
                throw new RuntimeException(errorMessage);
            }
        }
    }



    private void createPlayerNameInputWindow1() {
        JFrame nameFrame = new JFrame("Spieler Name Eingeben");
        nameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        nameFrame.setSize((int) window_width, (int) window_height);
        nameFrame.setLocationRelativeTo(null);

        ImageIcon backgroundIcon = new ImageIcon("Frontend/src/main/resources/images/Menu_BG.jpeg");

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setDoubleBuffered(true);
        panel.setLayout(null);
        panel.setOpaque(false);

        JLabel title3 = new JLabel("Nur Keine Aufregung!");
        title3.setFont(new Font("Arial", Font.BOLD, 48));
        title3.setForeground(Color.WHITE);
        title3.setBounds((200), (100), 500, 60); // Set position and size
        panel.add(title3);

        JLabel nameLabel = new JLabel("Spieler Name Eingeben:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 30));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBounds(250, 400, 400, 40);
        panel.add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setBounds(300, 450, 265, 40);
        panel.add(nameField);

        JButton backButton = createStyledButton("Zurück");
        backButton.setBounds(50, 600, 200, 40);
        panel.add(backButton);

        JButton continueButton = createStyledButton("Weiter");
        continueButton.setBounds(700, 600, 150, 40);
        panel.add(continueButton);

        //continueButton.addActionListener(e -> nameFrame.dispose());
        continueButton.addActionListener(e -> {
            String playerName = nameField.getText();
            System.out.println("Spielername: " + playerName);
            nameFrame.dispose();
            createLoadingWindow();
            // Add logic to proceed with the game using playerName
        });

        backButton.addActionListener(e -> {
            nameFrame.dispose();
            createSearchGameMenu();
        });

        nameFrame.add(panel);
        nameFrame.setVisible(true);
    }

    private void createLoadingWindow() {
        JFrame loadingFrame = new JFrame("Laden");
        loadingFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        loadingFrame.setSize((int) window_width, (int) window_height);
        loadingFrame.setLocationRelativeTo(null);

        ImageIcon backgroundIcon = new ImageIcon("Frontend/src/main/resources/images/Menu_BG.jpeg");

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setDoubleBuffered(true);
        panel.setLayout(null);
        panel.setOpaque(false);

        JLabel title4 = new JLabel("Nur Keine Aufregung!");
        title4.setFont(new Font("Arial", Font.BOLD, 48));
        title4.setForeground(Color.WHITE);
        title4.setBounds((200), (100), 500, 60); // Set position and size
        panel.add(title4);

        JLabel loadingLabel = new JLabel("Spiel wird geladen ...");
        loadingLabel.setFont(new Font("Arial", Font.BOLD, 30));
        loadingLabel.setForeground(Color.WHITE);
        loadingLabel.setBounds(550, 600, 300, 40);
        panel.add(loadingLabel);

        loadingFrame.add(panel);
        loadingFrame.setVisible(true);

        // Simulate loading delay
        // Use a one-time timer
        Timer timer = new Timer(2000, e -> {
            loadingFrame.dispose();
            createView(); // Proceed to the main game view
        });
        timer.setRepeats(false); // Ensure it only runs once
        timer.start();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 30));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(300, 80));

        button.setBackground(new Color(0, 0, 0, 80)); // Gray with transparency
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);

        button.setOpaque(true);

        return button;
    }

    private void createGameModeSelection() {
        JFrame modeFrame = new JFrame("Spielmodus Auswählen");
        modeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        modeFrame.setSize((int) window_width, (int) window_height);
        modeFrame.setLocationRelativeTo(null);

        ImageIcon backgroundIcon = new ImageIcon("Frontend/src/main/resources/images/Menu_BG.jpeg");

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setDoubleBuffered(true);
        panel.setLayout(null);
        panel.setOpaque(false);

        JLabel title2 = new JLabel("Nur Keine Aufregung!");
        title2.setFont(new Font("Arial", Font.BOLD, 48));
        title2.setForeground(Color.WHITE);
        title2.setBounds((200), (100), 500, 60); // Set position and size
        panel.add(title2);

        JLabel selectLabel = new JLabel("Spielmodus Auswählen:");
        selectLabel.setFont(new Font("Arial", Font.BOLD, 30));
        selectLabel.setForeground(Color.WHITE);
        selectLabel.setBounds(300, 200, 400, 40);
        panel.add(selectLabel);

        JButton twoPlayersButton = createStyledButton("2 Spieler");
        twoPlayersButton.setBounds(300, 300, 300, 60);
        panel.add(twoPlayersButton);

        JButton threePlayersButton = createStyledButton("3 Spieler");
        threePlayersButton.setBounds(300, 400, 300, 60);
        panel.add(threePlayersButton);

        JButton fourPlayersButton = createStyledButton("4 Spieler");
        fourPlayersButton.setBounds(300, 500, 300, 60);
        panel.add(fourPlayersButton);

        // Add listeners for each button
        twoPlayersButton.addActionListener(e -> {
            System.out.println("2 Spieler ausgewählt.");
            modeFrame.dispose();
            // Proceed to game setup with 2 players
            createPlayerNameInputWindow2();
        });

        threePlayersButton.addActionListener(e -> {
            System.out.println("3 Spieler ausgewählt.");
            modeFrame.dispose();
            // Proceed to game setup with 3 players
            createPlayerNameInputWindow2();
        });

        fourPlayersButton.addActionListener(e -> {
            System.out.println("4 Spieler ausgewählt.");
            modeFrame.dispose();
            // Proceed to game setup with 4 players
            createPlayerNameInputWindow2();
        });

        modeFrame.add(panel);
        modeFrame.setVisible(true);
    }

    private void createPlayerNameInputWindow2() {
        JFrame nameFrame1 = new JFrame("Spieler Name Eingeben");
        nameFrame1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        nameFrame1.setSize((int) window_width, (int) window_height);
        nameFrame1.setLocationRelativeTo(null);

        ImageIcon backgroundIcon = new ImageIcon("Frontend/src/main/resources/images/Menu_BG.jpeg");

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setDoubleBuffered(true);
        panel.setLayout(null);
        panel.setOpaque(false);

        JLabel title3 = new JLabel("Nur Keine Aufregung!");
        title3.setFont(new Font("Arial", Font.BOLD, 48));
        title3.setForeground(Color.WHITE);
        title3.setBounds((200), (100), 500, 60); // Set position and size
        panel.add(title3);

        JLabel nameLabel = new JLabel("Spieler Name Eingeben:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 30));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBounds(250, 400, 400, 40);
        panel.add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setBounds(300, 450, 265, 40);
        panel.add(nameField);

        JButton backButton = createStyledButton("Zurück");
        backButton.setBounds(50, 600, 200, 40);
        panel.add(backButton);

        JButton continueButton = createStyledButton("Weiter");
        continueButton.setBounds(700, 600, 150, 40);
        panel.add(continueButton);

        //continueButton.addActionListener(e -> nameFrame1.dispose());
        continueButton.addActionListener(e -> {
            String playerName = nameField.getText();
            System.out.println("Spielername: " + playerName);
            nameFrame1.dispose();
            createGameIdWindow();
            // Add logic to proceed with the game using playerName
        });

        backButton.addActionListener(e -> {
            nameFrame1.dispose();
            createGameModeSelection();
        });

        nameFrame1.add(panel);
        nameFrame1.setVisible(true);
    }

    private void createGameIdWindow() {
        JFrame gameIdFrame = new JFrame("Game ID");
        gameIdFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gameIdFrame.setSize((int) window_width, (int) window_height);
        gameIdFrame.setLocationRelativeTo(null);

        ImageIcon backgroundIcon = new ImageIcon("Frontend/src/main/resources/images/Menu_BG.jpeg");

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setDoubleBuffered(true);
        panel.setLayout(null);
        panel.setOpaque(false);

        JLabel title3 = new JLabel("Nur Keine Aufregung!");
        title3.setFont(new Font("Arial", Font.BOLD, 48));
        title3.setForeground(Color.WHITE);
        title3.setBounds((200), (100), 500, 60); // Set position and size
        panel.add(title3);

        JLabel idLabel = new JLabel("Game ID lautet:");
        idLabel.setFont(new Font("Arial", Font.BOLD, 30));
        idLabel.setForeground(Color.WHITE);
        idLabel.setBounds(350, 400, 300, 40);
        panel.add(idLabel);

        JTextField gameIdField = new JTextField();
        gameIdField.setBounds(300, 450, 300, 40);
        panel.add(gameIdField);

        JButton backButton = createStyledButton("Zurück");
        backButton.setBounds(50, 600, 150, 40);
        panel.add(backButton);

        JButton continueButton = createStyledButton("Weiter");
        continueButton.setBounds(700, 600, 150, 40);
        panel.add(continueButton);

        //continueButton.addActionListener(e -> gameIdFrame.dispose());
        continueButton.addActionListener(e -> {
        String gameID = gameIdField.getText(); // GameID aus dem Textfeld abrufen
        if (!gameID.isEmpty()) {
            int playerId = fetchPlayerIdFromBackend(gameID); // Backend-Abfrage für Beitritt
            if (playerId != -1) {
                System.out.println("Spieler erfolgreich beigetreten mit ID: " + playerId);
                searchFrame.dispose(); // Fenster schließen
                createPlayerNameInputWindow1(); // Weiter zum Spielernamen-Fenster
            } else {
                JOptionPane.showMessageDialog(searchFrame, "Fehler beim Beitritt zum Spiel. Überprüfen Sie die GameID.", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(searchFrame, "Bitte geben Sie eine gültige GameID ein.", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    });



        backButton.addActionListener(e -> {
            gameIdFrame.dispose();
            createPlayerNameInputWindow2();
        });

        gameIdFrame.add(panel);
        gameIdFrame.setVisible(true);
    }

    private void createView() {
        frame = new JFrame("Nur keine Aufregung - Spiel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLayout(new BorderLayout());

        // Obere Anzeigeleiste
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 2));

        currentPlayerLabel = new JLabel("Spieler-ID: " + gameModel.getPlayerId(), SwingConstants.CENTER);

        diceValueLabel = new JLabel("Würfel: ", SwingConstants.CENTER);
        topPanel.add(currentPlayerLabel);
        topPanel.add(diceValueLabel);

        rollDiceButton = new JButton("Würfeln");
        rollDiceButton.addActionListener(e -> {
            try {
                int diceValue = rollDiceFromBackend();
                diceValueLabel.setText("Würfel: " + diceValue);

                // Aktualisiere den Server mit neuen Daten
                updateGameStateFromBackend();

                // Automatische Spieleraktion nach dem Würfeln
                performPlayerTurn();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Fehler beim Abrufen des Würfelergebnisses!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        topPanel.add(rollDiceButton);

        frame.add(topPanel, BorderLayout.NORTH);

        // Spielfeld
        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGameBoard(g);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(frame.getWidth(), frame.getHeight() - 150);
            }
        };
        frame.add(boardPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private int rollDiceFromBackend() throws Exception {
        URL url = new URL("http://localhost:8080/api/game/rollDice?playerId=" + gameModel.getCurrentPlayer());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        if (connection.getResponseCode() == 200) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                return Integer.parseInt(reader.readLine());
            }
        } else {
            throw new RuntimeException("Fehler beim Würfeln: " + connection.getResponseCode());
        }
    }

    private void updateGameStateFromBackend() throws Exception {
        URL url = new URL("http://localhost:8080/api/game/state");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() == 200) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String jsonState = reader.readLine();
                // Aktualisiere die Anzeige basierend auf den Backend-Daten
                System.out.println("Spielstatus: " + jsonState);
            }
        } else {
            throw new RuntimeException("Fehler beim Abrufen des Spielstatus: " + connection.getResponseCode());
        }
    }


    private void drawGameBoard(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, boardPanel.getWidth(), boardPanel.getHeight());

        int cellSize = Math.min(boardPanel.getWidth(), boardPanel.getHeight()) / 15;
        int offsetX = (boardPanel.getWidth() - (cellSize * 15)) / 2;
        int offsetY = (boardPanel.getHeight() - (cellSize * 15)) / 2;

        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                int x = offsetX + col * cellSize;
                int y = offsetY + row * cellSize;

                g.setColor(Color.WHITE);
                g.fillRect(x, y, cellSize, cellSize);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, cellSize, cellSize);
            }
        }
    }


    private void performPlayerTurn() {
        gameController.endTurn();
        try {
            updateGameStateFromBackend();
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentPlayerLabel.setText("Aktueller Spieler: Spieler " + gameModel.getCurrentPlayer());
        boardPanel.repaint();
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
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    String errorMessage = reader.readLine();
                    throw new RuntimeException(errorMessage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Fehlerfall
        }
    }

}
