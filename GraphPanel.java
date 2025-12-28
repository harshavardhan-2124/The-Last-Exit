import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * ENHANCED GRAPH PANEL - Professional real-time visualization
 */
class GraphPanel extends JPanel {
    
    private GameWindow parent;
    
    private static final Color NODE_COLOR = new Color(100, 116, 139);
    private static final Color EDGE_COLOR = new Color(71, 85, 105);
    private static final Color PLAYER_NODE = new Color(59, 130, 246);
    private static final Color AI_NODE = new Color(239, 68, 68);
    private static final Color EXIT_NODE = new Color(34, 197, 94);
    private static final Color CHOSEN_PATH = new Color(34, 197, 94);
    private static final Color REJECTED_PATH = new Color(239, 68, 68);
    private static final Color PANEL_BG = new Color(15, 23, 42);
    
    public GraphPanel() {
        setBackground(PANEL_BG);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(6, 182, 212), 3),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
    }
    
    public void setParent(GameWindow parent) {
        this.parent = parent;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (parent == null || parent.graph == null) return;
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Enhanced title with gradient
        drawTitle(g2d);
        
        int rows = parent.currentMaze.length;
        int cols = parent.currentMaze[0].length;
        
        int graphWidth = getWidth() - 40;
        int graphHeight = getHeight() - 240;
        int cellSize = Math.min(graphWidth / cols, graphHeight / rows);
        
        int offsetX = (graphWidth - cellSize * cols) / 2 + 20;
        int offsetY = (graphHeight - cellSize * rows) / 2 + 60;
        
        // Draw background grid
        drawBackgroundGrid(g2d, rows, cols, cellSize, offsetX, offsetY);
        
        // Layer 1: All edges
        drawEdges(g2d, cellSize, offsetX, offsetY);
        
        // Layer 2: AI decision paths (with glow effect)
        if (parent.lastAiDecision != null) {
            drawAIDecisionPaths(g2d, cellSize, offsetX, offsetY);
        }
        
        // Layer 3: All nodes with shadows
        drawNodes(g2d, rows, cols, cellSize, offsetX, offsetY);
        
        // Layer 4: AI analysis panel
        drawEnhancedAIAnalysis(g2d);
    }
    
    private void drawTitle(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Gradient text effect
        GradientPaint gradient = new GradientPaint(
            15, 20, new Color(6, 182, 212),
            200, 20, new Color(139, 92, 246)
        );
        g2d.setPaint(gradient);
        g2d.drawString("🗺️ Real-Time Graph Visualization", 15, 30);
        
        // Subtitle
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        g2d.setColor(new Color(148, 163, 184));
        g2d.drawString("Adjacency List | Greedy Algorithm Decision Tree", 15, 45);
    }
    
    private void drawBackgroundGrid(Graphics2D g2d, int rows, int cols, 
                                    int cellSize, int offsetX, int offsetY) {
        g2d.setColor(new Color(30, 41, 59, 50));
        g2d.setStroke(new BasicStroke(0.5f));
        
        for (int r = 0; r <= rows; r++) {
            int y = offsetY + r * cellSize;
            g2d.drawLine(offsetX, y, offsetX + cols * cellSize, y);
        }
        
        for (int c = 0; c <= cols; c++) {
            int x = offsetX + c * cellSize;
            g2d.drawLine(x, offsetY, x, offsetY + rows * cellSize);
        }
    }
    
    private void drawEdges(Graphics2D g2d, int cellSize, int offsetX, int offsetY) {
        Node[] allNodes = parent.graph.getAllWalkableNodes();
        
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.setColor(EDGE_COLOR);
        
        for (Node node : allNodes) {
            int x1 = offsetX + node.getCol() * cellSize + cellSize / 2;
            int y1 = offsetY + node.getRow() * cellSize + cellSize / 2;
            
            for (Node neighbor : node.getNeighbors()) {
                if (neighbor.getRow() > node.getRow() || 
                    (neighbor.getRow() == node.getRow() && neighbor.getCol() > node.getCol())) {
                    
                    int x2 = offsetX + neighbor.getCol() * cellSize + cellSize / 2;
                    int y2 = offsetY + neighbor.getRow() * cellSize + cellSize / 2;
                    
                    g2d.drawLine(x1, y1, x2, y2);
                }
            }
        }
    }
    
    private void drawAIDecisionPaths(Graphics2D g2d, int cellSize, 
                                     int offsetX, int offsetY) {
        if (parent.lastAiDecision == null || parent.lastAiDecision.candidates == null) return;
        
        Node aiNode = parent.graph.getNode(parent.aiPos.x, parent.aiPos.y);
        if (aiNode == null) return;
        
        int aiX = offsetX + aiNode.getCol() * cellSize + cellSize / 2;
        int aiY = offsetY + aiNode.getRow() * cellSize + cellSize / 2;
        
        // Draw glow effect for rejected paths first
        for (GreedyAI.Candidate candidate : parent.lastAiDecision.candidates) {
            Node candidateNode = candidate.node;
            boolean isChosen = (candidateNode == parent.lastAiDecision.chosenMove);
            
            if (!isChosen) {
                int candX = offsetX + candidateNode.getCol() * cellSize + cellSize / 2;
                int candY = offsetY + candidateNode.getRow() * cellSize + cellSize / 2;
                
                // Glow effect
                g2d.setColor(new Color(239, 68, 68, 30));
                g2d.setStroke(new BasicStroke(8.0f));
                g2d.drawLine(aiX, aiY, candX, candY);
                
                // Main line
                g2d.setColor(REJECTED_PATH);
                g2d.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_BUTT, 
                             BasicStroke.JOIN_MITER, 10.0f, new float[]{6.0f, 6.0f}, 0.0f));
                g2d.drawLine(aiX, aiY, candX, candY);
            }
        }
        
        // Draw chosen path with glow
        for (GreedyAI.Candidate candidate : parent.lastAiDecision.candidates) {
            Node candidateNode = candidate.node;
            boolean isChosen = (candidateNode == parent.lastAiDecision.chosenMove);
            
            if (isChosen) {
                int candX = offsetX + candidateNode.getCol() * cellSize + cellSize / 2;
                int candY = offsetY + candidateNode.getRow() * cellSize + cellSize / 2;
                
                // Glow effect
                g2d.setColor(new Color(34, 197, 94, 50));
                g2d.setStroke(new BasicStroke(12.0f));
                g2d.drawLine(aiX, aiY, candX, candY);
                
                // Main line
                g2d.setColor(CHOSEN_PATH);
                g2d.setStroke(new BasicStroke(5.0f));
                g2d.drawLine(aiX, aiY, candX, candY);
                
                // Arrow head
                drawEnhancedArrow(g2d, aiX, aiY, candX, candY);
                
                // Score label
                drawScoreLabel(g2d, (aiX + candX) / 2, (aiY + candY) / 2, 
                              candidate.score);
            }
        }
    }
    
    private void drawEnhancedArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int arrowSize = 12;
        
        int[] xPoints = new int[3];
        int[] yPoints = new int[3];
        
        xPoints[0] = x2;
        yPoints[0] = y2;
        xPoints[1] = (int) (x2 - arrowSize * Math.cos(angle - Math.PI / 6));
        yPoints[1] = (int) (y2 - arrowSize * Math.sin(angle - Math.PI / 6));
        xPoints[2] = (int) (x2 - arrowSize * Math.cos(angle + Math.PI / 6));
        yPoints[2] = (int) (y2 - arrowSize * Math.sin(angle + Math.PI / 6));
        
        g2d.fillPolygon(xPoints, yPoints, 3);
    }
    
    private void drawScoreLabel(Graphics2D g2d, int x, int y, double score) {
        String scoreText = String.format("%.1f", score);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        FontMetrics fm = g2d.getFontMetrics();
        
        int width = fm.stringWidth(scoreText) + 8;
        int height = fm.getHeight() + 4;
        
        // Background
        g2d.setColor(new Color(34, 197, 94));
        g2d.fillRoundRect(x - width/2, y - height/2, width, height, 5, 5);
        
        // Text
        g2d.setColor(Color.WHITE);
        g2d.drawString(scoreText, x - fm.stringWidth(scoreText)/2, 
                      y + fm.getAscent()/2 - 2);
    }
    
    private void drawNodes(Graphics2D g2d, int rows, int cols, int cellSize, 
                          int offsetX, int offsetY) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Node node = parent.graph.getNode(r, c);
                if (node == null || node.isWall()) continue;
                
                int x = offsetX + c * cellSize + cellSize / 2;
                int y = offsetY + r * cellSize + cellSize / 2;
                
                Color nodeColor = NODE_COLOR;
                int nodeSize = 10;
                boolean isSpecial = false;
                
                if (r == parent.exitPos.x && c == parent.exitPos.y) {
                    nodeColor = EXIT_NODE;
                    nodeSize = 14;
                    isSpecial = true;
                }
                if (r == parent.playerPos.x && c == parent.playerPos.y) {
                    nodeColor = PLAYER_NODE;
                    nodeSize = 16;
                    isSpecial = true;
                }
                if (r == parent.aiPos.x && c == parent.aiPos.y) {
                    nodeColor = AI_NODE;
                    nodeSize = 16;
                    isSpecial = true;
                }
                
                // Shadow effect for special nodes
                if (isSpecial) {
                    g2d.setColor(new Color(0, 0, 0, 50));
                    g2d.fillOval(x - nodeSize/2 + 2, y - nodeSize/2 + 2, 
                                nodeSize, nodeSize);
                }
                
                // White border
                g2d.setColor(Color.WHITE);
                g2d.fillOval(x - nodeSize/2 - 2, y - nodeSize/2 - 2, 
                            nodeSize + 4, nodeSize + 4);
                
                // Node
                g2d.setColor(nodeColor);
                g2d.fillOval(x - nodeSize/2, y - nodeSize/2, nodeSize, nodeSize);
                
                // Pulse effect for AI and Player
                if (isSpecial && (r == parent.aiPos.x || r == parent.playerPos.x)) {
                    g2d.setColor(new Color(nodeColor.getRed(), nodeColor.getGreen(), 
                                          nodeColor.getBlue(), 80));
                    g2d.setStroke(new BasicStroke(2.0f));
                    g2d.drawOval(x - nodeSize/2 - 4, y - nodeSize/2 - 4, 
                                nodeSize + 8, nodeSize + 8);
                }
            }
        }
    }
    
    private void drawEnhancedAIAnalysis(Graphics2D g2d) {
        int panelY = getHeight() - 160;
        int panelHeight = 140;
        
        // Gradient background
        GradientPaint bgGradient = new GradientPaint(
            0, panelY, new Color(15, 23, 42),
            0, panelY + panelHeight, new Color(30, 41, 59)
        );
        g2d.setPaint(bgGradient);
        g2d.fillRoundRect(15, panelY, getWidth() - 30, panelHeight, 15, 15);
        
        // Border with gradient
        GradientPaint borderGradient = new GradientPaint(
            15, panelY, new Color(6, 182, 212),
            getWidth() - 15, panelY, new Color(139, 92, 246)
        );
        g2d.setPaint(borderGradient);
        g2d.setStroke(new BasicStroke(3.0f));
        g2d.drawRoundRect(15, panelY, getWidth() - 30, panelHeight, 15, 15);
        
        // Title
        g2d.setFont(new Font("Arial", Font.BOLD, 15));
        g2d.setColor(new Color(6, 182, 212));
        g2d.drawString("🧠 Greedy Algorithm Analysis", 30, panelY + 30);
        
        if (parent.lastAiDecision != null) {
            int textY = panelY + 55;
            int lineHeight = 22;
            
            // Turn indicator
            g2d.setFont(new Font("Arial", Font.BOLD, 13));
            g2d.setColor(parent.waitingForPlayer ? new Color(59, 130, 246) : 
                        new Color(239, 68, 68));
            String turnText = parent.waitingForPlayer ? "🎮 YOUR TURN" : "🤖 AI THINKING...";
            g2d.drawString(turnText, getWidth() - 180, panelY + 30);
            
            // Stats
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            
            drawStatRow(g2d, 30, textY, "Moves Evaluated:", 
                       String.valueOf(parent.lastAiDecision.candidates.size()),
                       new Color(250, 204, 21));
            
            drawStatRow(g2d, 30, textY + lineHeight, "Best Score:", 
                       String.format("%.2f", parent.lastAiDecision.chosenScore),
                       new Color(34, 197, 94));
            
            String algoType = parent.difficulty.equals("easy") ? "Pure Greedy" :
                            parent.difficulty.equals("medium") ? "Greedy + Penalties" :
                            "Greedy + Lookahead";
            drawStatRow(g2d, 30, textY + lineHeight * 2, "Strategy:", 
                       algoType, new Color(147, 197, 253));
            
        } else {
            g2d.setFont(new Font("Arial", Font.ITALIC, 13));
            g2d.setColor(Color.GRAY);
            g2d.drawString("Waiting for first move...", 30, panelY + 70);
        }
        
        // Legend
        drawEnhancedLegend(g2d, panelY + panelHeight - 25);
    }
    
    private void drawStatRow(Graphics2D g2d, int x, int y, String label, 
                            String value, Color valueColor) {
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawString(label, x, y);
        
        FontMetrics fm = g2d.getFontMetrics();
        int labelWidth = fm.stringWidth(label);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.setColor(valueColor);
        g2d.drawString(value, x + labelWidth + 10, y);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
    }
    
    private void drawEnhancedLegend(Graphics2D g2d, int y) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 9));
        
        String[] labels = {"─ Edge", "━ Chosen", "┈ Rejected", 
                          "● Node", "● You", "● AI", "● Exit"};
        Color[] colors = {EDGE_COLOR, CHOSEN_PATH, REJECTED_PATH, 
                         NODE_COLOR, PLAYER_NODE, AI_NODE, EXIT_NODE};
        
        int startX = 30;
        int itemWidth = 70;
        
        for (int i = 0; i < labels.length; i++) {
            int x = startX + (i * itemWidth);
            
            g2d.setColor(colors[i]);
            if (i < 3) {
                g2d.setStroke(i == 2 ? 
                    new BasicStroke(1.5f, BasicStroke.CAP_BUTT, 
                                   BasicStroke.JOIN_MITER, 10.0f, 
                                   new float[]{3.0f, 3.0f}, 0.0f) :
                    new BasicStroke(i == 1 ? 4.0f : 2.0f));
                g2d.drawLine(x, y + 5, x + 15, y + 5);
            } else {
                g2d.fillOval(x + 2, y, 10, 10);
            }
            
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawString(labels[i], x + 18, y + 9);
        }
    }
}