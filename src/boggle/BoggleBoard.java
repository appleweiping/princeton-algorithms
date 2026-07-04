import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;

/**
 * An m-by-n Boggle board of uppercase letters (with 'Q' representing the "Qu"
 * die). This is a faithful reimplementation of the helper class distributed with
 * the Princeton "Boggle" assignment (not part of algs4.jar); its public API —
 * construction from a file, random construction, {@code rows()}, {@code cols()},
 * {@code getLetter(i, j)} and {@code toString()} — matches the assignment.
 */
public class BoggleBoard {

    private final int rows;
    private final int cols;
    private final char[][] board;

    /** Constructs a random 4-by-4 Boggle board using the standard Boggle dice. */
    public BoggleBoard() {
        this(4, 4);
    }

    /** Constructs a random m-by-n board with independent uniform letters. */
    public BoggleBoard(int m, int n) {
        this.rows = m;
        this.cols = n;
        this.board = new char[m][n];
        // Letter frequencies of the English alphabet (per the assignment helper).
        String freq = "AAAAAAAAABBCCDDDDEEEEEEEEEEEEFFGGGHHHHHHIIIIIIIIIJKLLLL"
                + "MMNNNNNNOOOOOOOOPPQRRRRRRSSSSSSTTTTTTTTTUUUUVVWWXYYYZ";
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                board[i][j] = freq.charAt(StdRandom.uniformInt(freq.length()));
    }

    /**
     * Constructs a board from a file whose first line is "m n" and whose next m
     * lines each contain n letters (Q denotes the Qu die).
     */
    public BoggleBoard(String filename) {
        In in = new In(filename);
        this.rows = in.readInt();
        this.cols = in.readInt();
        if (rows <= 0 || cols <= 0)
            throw new IllegalArgumentException("board dimensions must be positive");
        this.board = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                String letter = in.readString().toUpperCase();
                if (letter.equals("QU")) board[i][j] = 'Q';
                else if (letter.length() != 1)
                    throw new IllegalArgumentException("invalid letter: " + letter);
                else if (letter.charAt(0) == 'Q')
                    throw new IllegalArgumentException("the Q die must be entered as Qu");
                else board[i][j] = letter.charAt(0);
            }
        }
    }

    /** Constructs a board from a 2-D array of letters (Q denotes the Qu die). */
    public BoggleBoard(char[][] a) {
        this.rows = a.length;
        this.cols = a[0].length;
        this.board = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            if (a[i].length != cols)
                throw new IllegalArgumentException("ragged board");
            for (int j = 0; j < cols; j++) {
                if (a[i][j] < 'A' || a[i][j] > 'Z')
                    throw new IllegalArgumentException("invalid letter: " + a[i][j]);
                board[i][j] = a[i][j];
            }
        }
    }

    /** @return the number of rows. */
    public int rows() {
        return rows;
    }

    /** @return the number of columns. */
    public int cols() {
        return cols;
    }

    /** @return the letter in row i, column j (uppercase; 'Q' means "Qu"). */
    public char getLetter(int i, int j) {
        return board[i][j];
    }

    /** @return a string representation of the board. */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(rows).append(" ").append(cols).append("\n");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sb.append(board[i][j]);
                if (board[i][j] == 'Q') sb.append("u ");
                else sb.append("  ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
