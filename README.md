Here’s a **ready-to-use `README.md`** for your GitHub repository. You can copy-paste this directly into a file named `README.md`.

---

# The Last Exit 🚪🧩

**The Last Exit** is a Java-based maze navigation game that models mazes as graphs and visualizes AI-driven pathfinding in real time. An autonomous agent traverses the maze using heuristic-based decision making, demonstrating key concepts in graph theory, search algorithms, and artificial intelligence.

The project is modular and extensible, allowing new maze layouts and pathfinding strategies to be added easily.

---

## Features

* Graph-based maze representation using nodes and edges
* Greedy AI for heuristic-driven pathfinding
* Real-time visualization using Java Swing
* Configurable maze layouts
* Extensible design for adding BFS, DFS, A*, or other algorithms

---

## Project Structure

```
├── MazeRunner.java         # Main entry point
├── GameWindow.java         # Application window
├── MazePanel.java          # Maze visualization
├── GraphPanel.java         # Graph visualization
├── MazeGraph.java          # Graph data structure
├── Node.java               # Node representation
├── GreedyAI.java           # Greedy pathfinding AI
├── MazeConfigurations.java # Predefined maze layouts
└── MazeRunner.iml          # IntelliJ project file
```

---

## Requirements

* Java JDK 8 or higher
* IntelliJ IDEA (recommended) or any Java-compatible IDE

---

## Setup Instructions

### Using IntelliJ IDEA

1. Clone the repository:

   ```bash
   git clone https://github.com/your-username/the-last-exit.git
   ```
2. Open **IntelliJ IDEA** and select **Open**.
3. Choose the project folder.
4. Set the Project SDK:

   * **File → Project Structure → Project**
   * Select Java 8 or higher.
5. Open `MazeRunner.java`.

---

## Running the Program

### From IntelliJ IDEA

1. Open `MazeRunner.java`.
2. Right-click the file.
3. Select **Run 'MazeRunner.main()'**.
4. The maze window will open and the AI agent will begin navigating.

### From the Command Line

```bash
javac *.java
java MazeRunner
```

---

## Customization

* Edit `MazeConfigurations.java` to add or modify maze layouts.
* Modify `GreedyAI.java` to adjust the AI’s behavior.
* Add new AI classes (e.g., BFS, DFS, A*) and integrate them into the game logic.

---

## Educational Purpose

This project is intended for learning and demonstrating:

* Graph data structures
* AI search strategies
* Heuristic decision making
* GUI-based algorithm visualization

---

## License

This project is intended for educational use. Add a license if you plan to distribute or reuse the code publicly.

---

