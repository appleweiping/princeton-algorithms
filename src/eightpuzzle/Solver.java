import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Solves the 8-puzzle problem using the A* search algorithm with the Manhattan
 * priority function. To detect unsolvable boards, A* is run in lockstep on the
 * initial board and its twin; exactly one of them is solvable, so whichever
 * reaches the goal first tells us the answer.
 */
public class Solver {

    private final boolean solvable;
    private final SearchNode goalNode;

    // A node in the game tree: a board, the number of moves to reach it, and a
    // back-pointer to the previous node. Manhattan distance is cached.
    private final class SearchNode implements Comparable<SearchNode> {
        private final Board board;
        private final int moves;
        private final SearchNode prev;
        private final int manhattan;   // cached heuristic

        SearchNode(Board board, int moves, SearchNode prev) {
            this.board = board;
            this.moves = moves;
            this.prev = prev;
            this.manhattan = board.manhattan();
        }

        int priority() {
            return manhattan + moves;
        }

        public int compareTo(SearchNode that) {
            int cmp = Integer.compare(this.priority(), that.priority());
            if (cmp != 0) return cmp;
            // Tie-break on the heuristic itself (empirically speeds up search).
            return Integer.compare(this.manhattan, that.manhattan);
        }
    }

    /**
     * Finds a shortest solution to the initial board (if one exists).
     *
     * @throws IllegalArgumentException if {@code initial} is null
     */
    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException("initial board is null");

        MinPQ<SearchNode> pq = new MinPQ<>();
        MinPQ<SearchNode> twinPq = new MinPQ<>();
        pq.insert(new SearchNode(initial, 0, null));
        twinPq.insert(new SearchNode(initial.twin(), 0, null));

        while (true) {
            SearchNode node = step(pq);
            if (node != null) {          // initial board solved
                solvable = true;
                goalNode = node;
                return;
            }
            SearchNode twinNode = step(twinPq);
            if (twinNode != null) {      // twin solved => initial unsolvable
                solvable = false;
                goalNode = null;
                return;
            }
        }
    }

    // Perform one A* expansion step on the given priority queue. Returns the
    // goal node if the dequeued board is the goal, else null.
    private SearchNode step(MinPQ<SearchNode> pq) {
        SearchNode node = pq.delMin();
        if (node.board.isGoal()) return node;
        for (Board neighbor : node.board.neighbors()) {
            // Critical optimization: skip the neighbor equal to our parent board.
            if (node.prev == null || !neighbor.equals(node.prev.board)) {
                pq.insert(new SearchNode(neighbor, node.moves + 1, node));
            }
        }
        return null;
    }

    /** @return true iff the initial board is solvable. */
    public boolean isSolvable() {
        return solvable;
    }

    /** @return the minimum number of moves to solve, or -1 if unsolvable. */
    public int moves() {
        return solvable ? goalNode.moves : -1;
    }

    /** @return the sequence of boards in a shortest solution, or null if unsolvable. */
    public Iterable<Board> solution() {
        if (!solvable) return null;
        Deque<Board> path = new ArrayDeque<>();
        for (SearchNode node = goalNode; node != null; node = node.prev) {
            path.addFirst(node.board);
        }
        return path;
    }

    /** Reads a board from a file and prints the solution. */
    public static void main(String[] args) {
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        Solver solver = new Solver(initial);
        if (!solver.isSolvable()) {
            StdOut.println("No solution possible");
        } else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution()) StdOut.println(board);
        }
    }
}
