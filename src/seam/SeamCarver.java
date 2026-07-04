import edu.princeton.cs.algs4.Picture;

/**
 * Content-aware image resizing (seam carving). A seam is a connected path of
 * pixels — one per row (vertical) or one per column (horizontal) — with the
 * lowest total "energy". Removing minimum-energy seams shrinks an image while
 * preserving salient content.
 *
 * <p>Energy uses the dual-gradient function; the minimum seam is found with
 * dynamic programming over the pixel grid (topological order is simply
 * row-by-row / column-by-column), so each seam operation is O(width x height).</p>
 *
 * <p>Internally the picture is stored as a color matrix. To avoid transposing
 * on every horizontal operation, energy and seam computations are parameterized
 * by orientation.</p>
 */
public class SeamCarver {

    private static final double BORDER_ENERGY = 1000.0;

    private int width;
    private int height;
    private int[][] rgb;     // rgb[col][row] packed color; column-major for cache-friendly vertical seams

    /**
     * @throws IllegalArgumentException if {@code picture} is null
     */
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException("picture is null");
        this.width = picture.width();
        this.height = picture.height();
        this.rgb = new int[width][height];
        for (int col = 0; col < width; col++)
            for (int row = 0; row < height; row++)
                rgb[col][row] = picture.getARGB(col, row);
    }

    /** @return the current picture. */
    public Picture picture() {
        Picture pic = new Picture(width, height);
        for (int col = 0; col < width; col++)
            for (int row = 0; row < height; row++)
                pic.setARGB(col, row, rgb[col][row]);
        return pic;
    }

    /** @return the width of the current picture. */
    public int width() {
        return width;
    }

    /** @return the height of the current picture. */
    public int height() {
        return height;
    }

    /**
     * @return the dual-gradient energy of pixel (x, y)
     * @throws IllegalArgumentException if (x, y) is out of range
     */
    public double energy(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height)
            throw new IllegalArgumentException("pixel (" + x + ", " + y + ") out of range");
        if (x == 0 || x == width - 1 || y == 0 || y == height - 1) return BORDER_ENERGY;

        int left = rgb[x - 1][y], right = rgb[x + 1][y];
        int up = rgb[x][y - 1], down = rgb[x][y + 1];
        double dx2 = gradientSquared(left, right);
        double dy2 = gradientSquared(up, down);
        return Math.sqrt(dx2 + dy2);
    }

    private double gradientSquared(int a, int b) {
        int ra = (a >> 16) & 0xFF, ga = (a >> 8) & 0xFF, ba = a & 0xFF;
        int rb = (b >> 16) & 0xFF, gb = (b >> 8) & 0xFF, bb = b & 0xFF;
        int dr = ra - rb, dg = ga - gb, db = ba - bb;
        return (double) dr * dr + (double) dg * dg + (double) db * db;
    }

    /** @return an array of column indices, one per row, for the min-energy vertical seam. */
    public int[] findVerticalSeam() {
        return findSeam(true);
    }

    /** @return an array of row indices, one per column, for the min-energy horizontal seam. */
    public int[] findHorizontalSeam() {
        return findSeam(false);
    }

    /*
     * Unified DP seam finder. For vertical == true we move top-to-bottom, one
     * pixel per row (result indexed by row, values are columns). For
     * vertical == false we move left-to-right, one pixel per column (result
     * indexed by column, values are rows). We map generic (major, minor)
     * coordinates onto the fixed (x, y) grid accordingly.
     *
     * major = the axis we walk along (rows for vertical, columns for horizontal)
     * minor = the axis the seam can shift within (columns for vertical, rows for horizontal)
     */
    private int[] findSeam(boolean vertical) {
        int majorLen = vertical ? height : width;   // number of steps in the seam
        int minorLen = vertical ? width : height;   // width the seam can occupy

        // energy grid indexed [major][minor]
        double[][] e = new double[majorLen][minorLen];
        for (int i = 0; i < majorLen; i++)
            for (int j = 0; j < minorLen; j++)
                e[i][j] = vertical ? energy(j, i) : energy(i, j);

        double[][] distTo = new double[majorLen][minorLen];
        int[][] edgeTo = new int[majorLen][minorLen];

        // first row/column: distance is just the pixel's own energy.
        for (int j = 0; j < minorLen; j++) distTo[0][j] = e[0][j];

        for (int i = 1; i < majorLen; i++) {
            for (int j = 0; j < minorLen; j++) {
                // best predecessor among j-1, j, j+1 in the previous major line.
                int bestPrev = j;
                double best = distTo[i - 1][j];
                if (j > 0 && distTo[i - 1][j - 1] < best) {
                    best = distTo[i - 1][j - 1];
                    bestPrev = j - 1;
                }
                if (j < minorLen - 1 && distTo[i - 1][j + 1] < best) {
                    best = distTo[i - 1][j + 1];
                    bestPrev = j + 1;
                }
                distTo[i][j] = e[i][j] + best;
                edgeTo[i][j] = bestPrev;
            }
        }

        // find the minor index with min total distance on the last major line.
        int last = majorLen - 1;
        int minIdx = 0;
        for (int j = 1; j < minorLen; j++)
            if (distTo[last][j] < distTo[last][minIdx]) minIdx = j;

        // backtrack.
        int[] seam = new int[majorLen];
        for (int i = last; i >= 0; i--) {
            seam[i] = minIdx;
            minIdx = edgeTo[i][minIdx];
        }
        return seam;
    }

    /**
     * Removes the given horizontal seam.
     *
     * @throws IllegalArgumentException if the seam is null, of wrong length, has
     *         out-of-range entries, or two adjacent entries differ by more than
     *         one; or if the height is at most 1
     */
    public void removeHorizontalSeam(int[] seam) {
        validateSeam(seam, width, height);
        if (height <= 1) throw new IllegalArgumentException("height too small to remove a horizontal seam");
        int[][] next = new int[width][height - 1];
        for (int col = 0; col < width; col++) {
            int r = 0;
            for (int row = 0; row < height; row++) {
                if (row == seam[col]) continue;   // skip the seam pixel
                next[col][r++] = rgb[col][row];
            }
        }
        rgb = next;
        height--;
    }

    /**
     * Removes the given vertical seam.
     *
     * @throws IllegalArgumentException if the seam is invalid, or if the width
     *         is at most 1
     */
    public void removeVerticalSeam(int[] seam) {
        validateSeam(seam, height, width);
        if (width <= 1) throw new IllegalArgumentException("width too small to remove a vertical seam");
        int[][] next = new int[width - 1][height];
        for (int row = 0; row < height; row++) {
            int c = 0;
            for (int col = 0; col < width; col++) {
                if (col == seam[row]) continue;   // skip the seam pixel
                next[c++][row] = rgb[col][row];
            }
        }
        rgb = next;
        width--;
    }

    // expectedLen = number of seam entries; bound = exclusive upper bound on each entry.
    private void validateSeam(int[] seam, int expectedLen, int bound) {
        if (seam == null) throw new IllegalArgumentException("seam is null");
        if (seam.length != expectedLen)
            throw new IllegalArgumentException("seam has wrong length: " + seam.length
                    + " (expected " + expectedLen + ")");
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= bound)
                throw new IllegalArgumentException("seam entry " + seam[i] + " out of range [0, " + (bound - 1) + "]");
            if (i > 0 && Math.abs(seam[i] - seam[i - 1]) > 1)
                throw new IllegalArgumentException("seam entries " + seam[i - 1] + " and " + seam[i]
                        + " differ by more than 1");
        }
    }

    /** Unit tests. */
    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        SeamCarver sc = new SeamCarver(picture);
        System.out.println("size: " + sc.width() + " x " + sc.height());
        System.out.printf("energy(1,1) = %.2f%n", sc.energy(1, 1));
        int[] v = sc.findVerticalSeam();
        System.out.print("vertical seam:");
        for (int x : v) System.out.print(" " + x);
        System.out.println();
    }
}
