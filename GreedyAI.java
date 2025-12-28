import java.util.*;

/**
 * GREEDY AI CLASS - Implements intelligent pursuit algorithms
 * Core greedy algorithm: selects move with minimum distance to target
 * Time Complexity: O(1) per decision
 */
public class GreedyAI {
    
    private final MazeGraph graph;
    private final String difficulty;
    private Node lastPosition;
    
    /**
     * Decision data class
     */
    public static class Decision {
        public final Node chosenMove;
        public final List<Candidate> candidates;
        public final double chosenScore;
        
        public Decision(Node chosenMove, List<Candidate> candidates, double chosenScore) {
            this.chosenMove = chosenMove;
            this.candidates = candidates;
            this.chosenScore = chosenScore;
        }
    }
    
    /**
     * Candidate move evaluation
     */
    public static class Candidate {
        public final Node node;
        public final double score;
        public final int distance;
        
        public Candidate(Node node, double score, int distance) {
            this.node = node;
            this.score = score;
            this.distance = distance;
        }
    }
    
    public GreedyAI(MazeGraph graph, String difficulty) {
        this.graph = graph;
        this.difficulty = difficulty;
        this.lastPosition = null;
    }
    
    /**
     * CORE GREEDY ALGORITHM
     * Evaluates all neighbors and selects move with minimum cost
     */
    public Decision getGreedyMove(int currentRow, int currentCol, 
                                   int targetRow, int targetCol) {
        
        Node currentNode = graph.getNode(currentRow, currentCol);
        Node targetNode = graph.getNode(targetRow, targetCol);
        
        if (currentNode == null || targetNode == null) {
            return null;
        }
        
        Node bestMove = null;
        double bestScore = Double.MAX_VALUE;
        List<Candidate> candidates = new ArrayList<>();
        
        // Evaluate all valid neighbors
        for (Node neighbor : currentNode.getNeighbors()) {
            if (neighbor.isWall()) continue;
            
            // Calculate Manhattan Distance
            int distance = neighbor.calculateManhattanDistance(targetNode);
            double score = distance;
            
            // Apply difficulty modifiers
            score = applyDifficultyModifiers(neighbor, targetNode, distance, score);
            
            candidates.add(new Candidate(neighbor, score, distance));
            
            // Greedy choice: select minimum score
            if (score < bestScore) {
                bestScore = score;
                bestMove = neighbor;
            }
        }
        
        lastPosition = currentNode;
        
        return new Decision(bestMove, candidates, bestScore);
    }
    
    /**
     * Apply difficulty-specific scoring
     */
    private double applyDifficultyModifiers(Node neighbor, Node target, 
                                            int distance, double baseScore) {
        double score = baseScore;
        
        switch (difficulty.toLowerCase()) {
            case "medium":
                int neighborCount = neighbor.getNeighbors().size();
                if (neighborCount <= 1) {
                    score += 3;
                } else if (neighborCount == 2) {
                    score += 1;
                }
                
                if (lastPosition != null && neighbor.equals(lastPosition)) {
                    score += 2;
                }
                break;
                
            case "hard":
                double lookaheadScore = evaluateLookahead(neighbor, target);
                score = distance * 0.7 + lookaheadScore * 0.3;
                
                if (lastPosition != null && neighbor.equals(lastPosition)) {
                    score += 4;
                }
                
                if (neighbor.getNeighbors().size() <= 1) {
                    score += 5;
                }
                break;
                
            case "easy":
            default:
                break;
        }
        
        return score;
    }
    
    /**
     * Lookahead evaluation for hard mode
     */
    private double evaluateLookahead(Node node, Node target) {
        double minDistance = Double.MAX_VALUE;
        
        for (Node futureNeighbor : node.getNeighbors()) {
            if (!futureNeighbor.isWall()) {
                int dist = futureNeighbor.calculateManhattanDistance(target);
                minDistance = Math.min(minDistance, dist);
            }
        }
        
        return minDistance;
    }
    
    public String getDifficulty() {
        return difficulty;
    }
}