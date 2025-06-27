package org.Projekt.frontend;

import org.Projekt.frontend.ClientModel.ClientModelImpl;
import org.Projekt.frontend.ClientModel.ClientModelInterface;
import org.Projekt.frontend.ClientController.ClientControllerImpl;
import org.Projekt.frontend.ClientController.ClientControllerInterface;
import org.Projekt.frontend.ClientView.ClientView;

public class GameApplication {
    public static void main(String[] args) {
        // Beispiel GameID (kann durch Benutzer-Eingabe ersetzt werden)
        String gameID = "1234"; // Diese GameID sollte entweder dynamisch generiert oder eingegeben werden

        // Erstelle das Model
        ClientModelInterface model = new ClientModelImpl(gameID);

        // Erstelle den Controller und verbinde ihn mit dem Model
        ClientControllerInterface controller = new ClientControllerImpl(model);

        // Erstelle die View und verbinde sie mit Controller und Model
        ClientView view = new ClientView(controller, model);

        // Starte die Anwendung
        System.out.println("Spiel gestartet!");
    }
}

