/**
 * MAZE GRAPH CLASS - Manages graph representation of the maze
 * Time Complexity: O(R × C) for construction
 */
public class MazeGraph {
    
    private final Node[][] nodes;
    private final int[][] mazeGrid;
    private final int rows;
    private final int cols;
    
    public MazeGraph(int[][] mazeGrid) {
        this.mazeGrid = mazeGrid;
        this.rows = mazeGrid.length;
        this.cols = mazeGrid[0].length;
        this.nodes = new Node[rows][cols];
        
        buildGraph();
    }
    
    /**
     * Build graph from maze grid
     * Creates nodes and connects adjacent walkable cells
     */
    private void buildGraph() {
        // Step 1: Create all nodes
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                boolean isWall = (mazeGrid[r][c] == 1);
                nodes[r][c] = new Node(r, c, isWall);
            }
        }
        
        // Step 2: Build adjacency lists (edges)
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Node currentNode = nodes[r][c];
                
                if (currentNode.isWall()) continue;
                
                for (int[] dir : directions) {
                    int newRow = r + dir[0];
                    int newCol = c + dir[1];
                    
                    if (isValidPosition(newRow, newCol)) {
                        Node neighbor = nodes[newRow][newCol];
                        
                        if (!neighbor.isWall()) {
                            currentNode.addNeighbor(neighbor);
                        }
                    }
                }
            }
        }
    }
    
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }
    
    public Node getNode(int row, int col) {
        if (!isValidPosition(row, col)) return null;
        return nodes[row][col];
    }
    
    public void resetAllNodes() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (nodes[r][c] != null) {
                    nodes[r][c].reset();
                }
            }
        }
    }
    
    public Node[] getAllWalkableNodes() {
        java.util.List<Node> walkableNodes = new java.util.ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!nodes[r][c].isWall()) {
                    walkableNodes.add(nodes[r][c]);
                }
            }
        }
        return walkableNodes.toArray(new Node[0]);
    }
    
    public int getRows() { return rows; }
    public int getCols() { return cols; }
}