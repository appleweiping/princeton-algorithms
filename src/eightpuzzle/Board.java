import java.util.ArrayList;
import java.util.List;

/**
 * An immutable n-by-n board for the 8-puzzle (sliding-tile) problem. Tiles are
 * labeled 1..n^2-1 and the blank is 0. Provides Hamming and Manhattan distance
 * heuristics, neighbor generation, and a twin (used to detect unsolvable
 * boards).
 */
public class Board {

    private final int[][] tiles;
    private final int n;
    private final int blankRow;
    private final int blankCol;

    /**
     * Constructs a board from an n-by-n array of tiles, where tiles[row][col]
     * is the tile at (row, col) and 0 denotes the blank.
     *
     * @throws IllegalArgumentException if {@code tiles} is null
     */
    public Board(int[][] tiles) {
        if (tiles == null) throw new IllegalArgumentException("tiles is null");
        this.n = tiles.length;
        this.tiles = new int[n][n];
        int br = -1, bc = -1;
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                this.tiles[r][c] = tiles[r][c];
                if (tiles[r][c] == 0) {
                    br = r;
                    bc = c;
                }
            }
        }
        this.blankRow = br;
        this.blankCol = bc;
    }

    /** @return a string representation: first line n, then the grid rows. */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(n).append('\n');
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                sb.append(String.format("%2d ", tiles[r][c]));
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** @return the board dimension n. */
    public int dimension() {
        return n;
    }

    /** @return the number of tiles out of place (blank excluded). */
    public int hamming() {
        int count = 0;
        for (int r = 0; r < n; r++)
            for (int c = 0; c < n; c++) {
                int tile = tiles[r][c];
                if (tile != 0 && tile != goalValue(r, c)) count++;
            }
        return count;
    }

    /** @return the sum of Manhattan distances of tiles to their goal positions. */
    public int manhattan() {
        int sum = 0;
        for (int r = 0; r < n; r++)
            for (int c = 0; c < n; c++) {
                int tile = tiles[r][c];
                if (tile != 0) {
                    int goalRow = (tile - 1) / n;
                    int goalCol = (tile - 1) % n;
                    sum += Math.abs(r - goalRow) + Math.abs(c - goalCol);
                }
            }
        return sum;
    }

    // the value that belongs at (r, c) in the goal board (blank at bottom-right).
    private int goalValue(int r, int c) {
        if (r == n - 1 && c == n - 1) return 0;
        return r * n + c + 1;
    }

    /** @return true iff this board is the goal board. */
    public boolean isGoal() {
        return hamming() == 0;
    }

    /** @return true iff {@code y} is a Board with the same tile arrangement. */
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null || y.getClass() != this.getClass()) return false;
        Board other = (Board) y;
        if (other.n != this.n) return false;
        for (int r = 0; r < n; r++)
            for (int c = 0; c < n; c++)
                if (this.tiles[r][c] != other.tiles[r][c]) return false;
        return true;
    }

    /** @return all boards reachable by sliding one tile into the blank. */
    public Iterable<Board> neighbors() {
        List<Board> result = new ArrayList<>();
        int[][] deltas = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] d : deltas) {
            int nr = blankRow + d[0];
            int nc = blankCol + d[1];
            if (nr >= 0 && nr < n && nc >= 0 && nc < n) {
                int[][] copy = copyTiles();
                copy[blankRow][blankCol] = copy[nr][nc];
                copy[nr][nc] = 0;
                result.add(new Board(copy));
            }
        }
        return result;
    }

    /**
     * @return a board obtained by swapping any pair of (non-blank) tiles. Used
     *         to determine solvability: exactly one of a board and its twin is
     *         solvable.
     */
    public Board twin() {
        int[][] copy = copyTiles();
        // Find the first two non-blank tiles (in row-major order) and swap them.
        int[] first = null;
        for (int r = 0; r < n && first == null; r++)
            for (int c = 0; c < n; c++)
                if (copy[r][c] != 0) { first = new int[]{r, c}; break; }
        int[] second = null;
        for (int r = 0; r < n && second == null; r++)
            for (int c = 0; c < n; c++)
                if (copy[r][c] != 0 && !(r == first[0] && c == first[1])) {
                    second = new int[]{r, c};
                    break;
                }
        int tmp = copy[first[0]][first[1]];
        copy[first[0]][first[1]] = copy[second[0]][second[1]];
        copy[second[0]][second[1]] = tmp;
        return new Board(copy);
    }

    private int[][] copyTiles() {
        int[][] copy = new int[n][n];
        for (int r = 0; r < n; r++)
            copy[r] = tiles[r].clone();
        return copy;
    }

    /** Unit tests. */
    public static void main(String[] args) {
        int[][] t = {{8, 1, 3}, {4, 0, 2}, {7, 6, 5}};
        Board b = new Board(t);
        System.out.print(b);
        System.out.println("dimension=" + b.dimension());  // 3
        System.out.println("hamming=" + b.hamming());        // 5
        System.out.println("manhattan=" + b.manhattan());    // 10
        System.out.println("isGoal=" + b.isGoal());          // false
        System.out.println("neighbors:");
        for (Board nb : b.neighbors()) System.out.println(nb);
        System.out.println("twin:\n" + b.twin());
    }
}
