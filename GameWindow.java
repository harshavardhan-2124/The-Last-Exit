import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * ============================================================================
 * GAME WINDOW - Main UI Controller with Turn-Based System
 * ============================================================================
 * 
 * Features:
 * - Turn-based movement (Player → AI → Player)
 * - Real-time graph visualization
 * - Complex maze navigation
 * - Professional UI/UX
 */
public class GameWindow extends JFrame {
    
    private enum GameState { MENU, PLAYING, WON, LOST }
    
    // ========== GAME STATE ==========
    GameState gameState;
    String difficulty;
    MazeGraph graph;
    GreedyAI ai;
    Point playerPos;
    Point aiPos;
    Point exitPos;
    int[][] currentMaze;
    int moves;
    int seconds;
    GreedyAI.Decision lastAiDecision;
    
    // ========== TURN-BASED CONTROL ==========
    boolean waitingForPlayer = true;
    boolean isProcessingMove = false;
    
    // ========== UI COMPONENTS ==========
    private JPanel mainPanel;
    private MazePanel mazePanel;
    private GraphPanel graphPanel;
    private JLabel timeLabel, movesLabel, difficultyLabel, turnLabel;
    private Timer gameTimer, aiMoveTimer;
    
    /**
     * Constructor: Initialize main game window
     */
    public GameWindow() {
        setTitle("The Last Exit: Greedy AI Pursuit System");
        setSize(1400, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        gameState = GameState.MENU;
        difficulty = "medium";
        
        setupUI();
        showMenu();
    }
    
    /**
     * Setup main UI layout
     */
    private void setupUI() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(15, 23, 42));
        setContentPane(mainPanel);
        setupKeyboardControls();
    }
    
    /**
     * Display difficulty selection menu
     */
    private void showMenu() {
        mainPanel.removeAll();
        
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(15, 23, 42));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        // Title
        JLabel titleLabel = new JLabel("🚪 THE LAST EXIT");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(new Color(34, 211, 238));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Turn-Based Greedy AI Pursuit");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        subtitleLabel.setForeground(Color.LIGHT_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        menuPanel.add(titleLabel);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(subtitleLabel);
        menuPanel.add(Box.createVerticalStrut(50));
        
        // Difficulty buttons
        String[] difficulties = {"Easy", "Medium", "Hard"};
        String[] descriptions = {
            "Pure Greedy - Winding paths",
            "Greedy + Dead-End Penalty - Labyrinth",
            "Greedy + Lookahead - Complex maze"
        };
        Color[] colors = {
            new Color(34, 197, 94), 
            new Color(234, 179, 8), 
            new Color(239, 68, 68)
        };
        
        for (int i = 0; i < difficulties.length; i++) {
            final String diff = difficulties[i].toLowerCase();
            JButton btn = createMenuButton(difficulties[i], descriptions[i], colors[i]);
            btn.addActionListener(e -> { 
                difficulty = diff; 
                initGame(); 
            });
            menuPanel.add(btn);
            menuPanel.add(Box.createVerticalStrut(15));
        }
        
        // Turn-based explanation panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(30, 41, 59));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(6, 182, 212), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        infoPanel.setMaximumSize(new Dimension(600, 120));
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel infoTitle = new JLabel("🎯 Turn-Based System");
        infoTitle.setFont(new Font("Arial", Font.BOLD, 14));
        infoTitle.setForeground(new Color(34, 211, 238));
        infoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel info1 = new JLabel("• You move → AI moves (one move each turn)");
        JLabel info2 = new JLabel("• No straight-line escapes - strategic navigation required");
        JLabel info3 = new JLabel("• Graph shows AI's greedy decision-making in real-time");
        
        info1.setForeground(Color.LIGHT_GRAY);
        info2.setForeground(Color.LIGHT_GRAY);
        info3.setForeground(Color.LIGHT_GRAY);
        info1.setFont(new Font("Arial", Font.PLAIN, 12));
        info2.setFont(new Font("Arial", Font.PLAIN, 12));
        info3.setFont(new Font("Arial", Font.PLAIN, 12));
        info1.setAlignmentX(Component.LEFT_ALIGNMENT);
        info2.setAlignmentX(Component.LEFT_ALIGNMENT);
        info3.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        infoPanel.add(infoTitle);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(info1);
        infoPanel.add(info2);
        infoPanel.add(info3);
        
        menuPanel.add(Box.createVerticalStrut(30));
        menuPanel.add(infoPanel);
        
        mainPanel.add(menuPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    
    /**
     * Create styled difficulty button
     */
    private JButton createMenuButton(String title, String desc, Color color) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                   RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                if (getModel().isRollover()) {
                    g2.setColor(new Color(51, 65, 85));
                } else {
                    g2.setColor(new Color(30, 41, 59));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Border
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                // Title text
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 24));
                FontMetrics fm = g2.getFontMetrics();
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent() - 10;
                g2.drawString(title, 20, textY);
                
                // Description text
                g2.setFont(new Font("Arial", Font.PLAIN, 14));
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawString(desc, 20, textY + 25);
            }
        };
        
        btn.setPreferredSize(new Dimension(600, 80));
        btn.setMaximumSize(new Dimension(600, 80));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return btn;
    }
    
    /**
     * Initialize new game with selected difficulty
     */
    private void initGame() {
        // Get maze configuration
        MazeConfigurations.MazeConfig config = MazeConfigurations.getMaze(difficulty);
        
        currentMaze = config.grid;
        playerPos = new Point(config.playerStart);
        aiPos = new Point(config.aiStart);
        exitPos = new Point(config.exit);
        
        // Reset stats
        moves = 0;
        seconds = 0;
        lastAiDecision = null;
        
        // Reset turn system
        waitingForPlayer = true;
        isProcessingMove = false;
        
        // Build graph and AI
        graph = new MazeGraph(currentMaze);
        ai = new GreedyAI(graph, difficulty);
        
        // Setup game UI
        setupGameUI();
        startGameTimer();
        
        gameState = GameState.PLAYING;
    }
    
    /**
     * Setup game playing UI
     */
    private void setupGameUI() {
        mainPanel.removeAll();
        
        // Top panel with stats and controls
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBackground(new Color(15, 23, 42));
        
        timeLabel = createStatLabel("⏱️ Time: 0s");
        movesLabel = createStatLabel("🚶 Moves: 0");
        difficultyLabel = createStatLabel("🎯 " + 
            difficulty.substring(0, 1).toUpperCase() + difficulty.substring(1));
        
        // Turn indicator
        turnLabel = createStatLabel("🎮 YOUR TURN");
        turnLabel.setBackground(new Color(59, 130, 246));
        
        JButton restartBtn = new JButton("🔄 Restart");
        styleButton(restartBtn, new Color(6, 182, 212));
        restartBtn.addActionListener(e -> initGame());
        
        JButton menuBtn = new JButton("📋 Menu");
        styleButton(menuBtn, new Color(100, 116, 139));
        menuBtn.addActionListener(e -> { 
            stopTimers(); 
            showMenu(); 
        });
        
        topPanel.add(timeLabel);
        topPanel.add(movesLabel);
        topPanel.add(difficultyLabel);
        topPanel.add(turnLabel);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(restartBtn);
        topPanel.add(menuBtn);
        
        // Center panel with maze and graph
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setBackground(new Color(15, 23, 42));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        // Maze panel
        mazePanel = new MazePanel();
        mazePanel.setParent(this);
        centerPanel.add(mazePanel);
        
        // Graph panel
        graphPanel = new GraphPanel();
        graphPanel.setParent(this);
        centerPanel.add(graphPanel);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        mainPanel.revalidate();
        mainPanel.repaint();
        requestFocusInWindow();
    }
    
    /**
     * Create styled stat label
     */
    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        label.setOpaque(true);
        label.setBackground(new Color(30, 41, 59));
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        return label;
    }
    
    /**
     * Style button with color
     */
    private void styleButton(JButton btn, Color color) {
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    /**
     * Start game timer
     */
    private void startGameTimer() {
        stopTimers();
        gameTimer = new Timer(1000, e -> {
            seconds++;
            timeLabel.setText("⏱️ Time: " + seconds + "s");
        });
        gameTimer.start();
    }
    
    /**
     * Stop all timers
     */
    private void stopTimers() {
        if (gameTimer != null) gameTimer.stop();
        if (aiMoveTimer != null) aiMoveTimer.stop();
    }
    
    /**
     * Setup keyboard controls with turn-based restriction
     */
    private void setupKeyboardControls() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
            .addKeyEventDispatcher(e -> {
                if (e.getID() == KeyEvent.KEY_PRESSED && 
                    gameState == GameState.PLAYING &&
                    waitingForPlayer && 
                    !isProcessingMove) {
                    handleKeyPress(e.getKeyCode());
                    return true;
                }
                return false;
            });
    }
    
    /**
     * Handle player movement (TURN-BASED)
     * Player can only move when waitingForPlayer = true
     */
    private void handleKeyPress(int keyCode) {
        // Prevent multiple moves in same turn
        if (isProcessingMove || !waitingForPlayer) return;
        
        int newRow = playerPos.x;
        int newCol = playerPos.y;
        
        // Map keys to directions
        switch (keyCode) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                newRow--;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                newRow++;
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                newCol--;
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                newCol++;
                break;
            default:
                return;
        }
        
        // Validate and execute move
        if (isValidMove(newRow, newCol)) {
            // Lock player turn
            isProcessingMove = true;
            waitingForPlayer = false;
            
            // Move player
            playerPos.setLocation(newRow, newCol);
            moves++;
            movesLabel.setText("🚶 Moves: " + moves);
            
            // Update turn indicator
            turnLabel.setText("🤖 AI TURN");
            turnLabel.setBackground(new Color(239, 68, 68));
            
            // Check win/lose conditions
            if (playerPos.equals(exitPos)) { 
                gameWon(); 
                return; 
            }
            if (playerPos.equals(aiPos)) { 
                gameLost(); 
                return; 
            }
            
            // Update display
            mazePanel.repaint();
            graphPanel.repaint();
            
            // Schedule AI move
            scheduleAIMove();
        }
    }
    
    /**
     * Check if move is valid
     */
    private boolean isValidMove(int row, int col) {
        if (row < 0 || row >= currentMaze.length || 
            col < 0 || col >= currentMaze[0].length) {
            return false;
        }
        return currentMaze[row][col] == 0;
    }
    
    /**
     * Schedule AI move with difficulty-based delay
     */
    private void scheduleAIMove() {
        if (aiMoveTimer != null) aiMoveTimer.stop();
        
        // Delay based on difficulty (gives player time to see AI thinking)
        int delay = difficulty.equals("easy") ? 600 : 
                   difficulty.equals("medium") ? 450 : 300;
        
        aiMoveTimer = new Timer(delay, e -> { 
            moveAI(); 
            aiMoveTimer.stop(); 
        });
        aiMoveTimer.setRepeats(false);
        aiMoveTimer.start();
    }
    
    /**
     * Execute AI move using greedy algorithm
     * After AI moves, turn returns to player
     */
    private void moveAI() {
        if (gameState != GameState.PLAYING) return;
        
        // Get greedy decision
        lastAiDecision = ai.getGreedyMove(
            aiPos.x, aiPos.y, 
            playerPos.x, playerPos.y
        );
        
        if (lastAiDecision != null && lastAiDecision.chosenMove != null) {
            Node chosen = lastAiDecision.chosenMove;
            aiPos.setLocation(chosen.getRow(), chosen.getCol());
            
            // Check if AI caught player
            if (aiPos.equals(playerPos)) { 
                gameLost(); 
                return; 
            }
            
            // Return turn to player
            waitingForPlayer = true;
            isProcessingMove = false;
            turnLabel.setText("🎮 YOUR TURN");
            turnLabel.setBackground(new Color(59, 130, 246));
            
            // Update display
            mazePanel.repaint();
            graphPanel.repaint();
        }
    }
    
    /**
     * Handle win condition
     */
    private void gameWon() {
        gameState = GameState.WON;
        stopTimers();
        showEndDialog("🎉 VICTORY!", "You escaped the maze!", 
                     new Color(34, 197, 94));
    }
    
    /**
     * Handle lose condition
     */
    private void gameLost() {
        gameState = GameState.LOST;
        stopTimers();
        showEndDialog("💀 CAUGHT!", "The AI caught you!", 
                     new Color(239, 68, 68));
    }
    
    /**
     * Show end game dialog with stats
     */
    private void showEndDialog(String title, String msg, Color color) {
        String fullMsg = msg + "\n\n" +
                        "Time: " + seconds + "s\n" +
                        "Moves: " + moves + "\n" +
                        "Difficulty: " + difficulty.toUpperCase();
        
        JOptionPane.showMessageDialog(this, fullMsg, title, 
                                     JOptionPane.INFORMATION_MESSAGE);
        showMenu();
    }
}
