# Mensch, nur keine Aufregung! 🎲

Ein digitales Mehrspieler-Spiel auf Basis von Java und Spring Boot, umgesetzt mit der MVC-Architektur.  
Ziel war es, ein funktionierendes Frontend- und Backend-System zu realisieren – inspiriert vom klassischen Brettspiel **"Mensch ärgere dich nicht"**.

## 📦 Projektstruktur
prototyp/
│
├── Frontend/ # Java-Frontend (Client-Logik in MVC)
│ ├── src/main/java/org/Projekt/frontend/
│ │ ├── ClientController/
│ │ ├── ClientModel/
│ │ └── ClientView/
│
├── springbootapplication/ # Spring Boot Backend (Server)
│ ├── src/main/java/org/Projekt/springbootapplication/
│ │ ├── ServerController/
│ │ ├── ServerModel/
│ │ └── Service/


---

## 🚀 Features

- Mehrspieler-Funktionalität
- Client-Server-Kommunikation mit Spring Boot
- Strikte Trennung durch **MVC-Architektur**
- Eigenständig laufendes Java-Frontend
- Lokale oder vernetzte Spielumgebung möglich (geplant)

---

## 🧪 Status

✅ **Spiel ist implementiert und funktionsfähig**  
⚠️ Es gibt noch **einen Fehler im Code**, der die fehlerfreie Ausführung verhindert – wird derzeit analysiert.

---

## 🛠️ Technologien

| Sprache     | Frameworks/Tools         |
|------------|---------------------------|
| Java        | Spring Boot               |
| Build Tool  | Gradle                    |
| IDEs        | IntelliJ IDEA, Eclipse    |
| UI Design   | Figma                     |
| Versionskontrolle | Git & GitHub       |

---

## 👨‍💻 Autoren

- [Mohamed Tarek Rais](#)
- [Ibrahim Shadi](#)
- [Moustafa Dyarbakarlı](#)
- [Hanie Sarabi](#)

---

## 📂 Projekt aufsetzen

### Voraussetzungen

- Java 17 oder höher
- Gradle installiert (oder Wrapper verwenden)
- Git

### Backend starten

```bash
cd springbootapplication
./gradlew bootRun
```

