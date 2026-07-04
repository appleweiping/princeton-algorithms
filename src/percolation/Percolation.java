import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/**
 * Models an n-by-n percolation system using the weighted quick-union
 * (union-find) data structure.
 *
 * <p>A full site is an open site that can be connected to an open site in the
 * top row via a chain of neighboring (left, right, up, down) open sites. The
 * system percolates if there is a full site in the bottom row.</p>
 *
 * <p>To avoid <em>backwash</em> (bottom-row sites appearing full because they
 * connect to the bottom virtual site which in turn connects to the top through
 * an already-percolating column) we maintain two union-find structures:</p>
 * <ul>
 *   <li>{@code full}    — connects a virtual TOP site only, used for isFull().</li>
 *   <li>{@code uf}      — connects both virtual TOP and BOTTOM, used for percolates().</li>
 * </ul>
 *
 * Corner cases: constructor throws IllegalArgumentException if n &le; 0; open,
 * isOpen and isFull throw IllegalArgumentException if row or col is outside
 * [1, n].
 */
public class Percolation {
    private final int n;
    private final boolean[] open;      // open[site] == true iff site is open
    private int openCount;
    private final WeightedQuickUnionUF uf;    // includes virtual top & bottom
    private final WeightedQuickUnionUF full;  // includes virtual top only (no backwash)
    private final int top;             // virtual top site index
    private final int bottom;          // virtual bottom site index

    /**
     * Creates an n-by-n grid with all sites initially blocked.
     *
     * @param n grid dimension
     * @throws IllegalArgumentException if {@code n <= 0}
     */
    public Percolation(int n) {
        if (n <= 0) throw new IllegalArgumentException("n must be > 0, got " + n);
        this.n = n;
        this.open = new boolean[n * n + 2];
        this.top = n * n;
        this.bottom = n * n + 1;
        this.uf = new WeightedQuickUnionUF(n * n + 2);
        this.full = new WeightedQuickUnionUF(n * n + 1); // no bottom node needed
        this.openCount = 0;
    }

    // maps 1-based (row, col) to a 0-based flat index; validates bounds.
    private int index(int row, int col) {
        if (row < 1 || row > n || col < 1 || col > n)
            throw new IllegalArgumentException(
                "row/col out of bounds: (" + row + ", " + col + ") for n=" + n);
        return (row - 1) * n + (col - 1);
    }

    /**
     * Opens the site (row, col) if it is not already open, and connects it to
     * any open orthogonal neighbors.
     */
    public void open(int row, int col) {
        int site = index(row, col);
        if (open[site]) return;
        open[site] = true;
        openCount++;

        // Connect to the virtual top / bottom if on the boundary rows.
        if (row == 1) {
            uf.union(site, top);
            full.union(site, top);
        }
        if (row == n) {
            uf.union(site, bottom);
        }

        // Connect to any open neighbors.
        if (row > 1 && open[index(row - 1, col)]) unionBoth(site, index(row - 1, col));
        if (row < n && open[index(row + 1, col)]) unionBoth(site, index(row + 1, col));
        if (col > 1 && open[index(row, col - 1)]) unionBoth(site, index(row, col - 1));
        if (col < n && open[index(row, col + 1)]) unionBoth(site, index(row, col + 1));
    }

    private void unionBoth(int a, int b) {
        uf.union(a, b);
        full.union(a, b);
    }

    /** @return true iff site (row, col) is open. */
    public boolean isOpen(int row, int col) {
        return open[index(row, col)];
    }

    /** @return true iff site (row, col) is full (connected to the top). */
    public boolean isFull(int row, int col) {
        int site = index(row, col);
        return open[site] && full.find(site) == full.find(top);
    }

    /** @return the number of open sites. */
    public int numberOfOpenSites() {
        return openCount;
    }

    /** @return true iff the system percolates. */
    public boolean percolates() {
        // Special-case n == 1: percolates iff the single site is open.
        if (n == 1) return open[0];
        return uf.find(top) == uf.find(bottom);
    }

    // Simple sanity check.
    public static void main(String[] args) {
        Percolation p = new Percolation(3);
        p.open(1, 1);
        p.open(2, 1);
        p.open(3, 1);
        System.out.println("openSites=" + p.numberOfOpenSites());   // 3
        System.out.println("percolates=" + p.percolates());          // true
        System.out.println("isFull(3,1)=" + p.isFull(3, 1));         // true
        System.out.println("isFull(2,1)=" + p.isFull(2, 1));         // true
    }
}
