import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

/**
 * Command-line client for the collinear-points assignment. Reads points from an
 * input file and prints the line segments found by both the brute-force and the
 * fast algorithms, so that their results can be cross-checked. (The official
 * assignment ships two separate clients that also draw with StdDraw; this
 * headless variant is used for automated verification.)
 *
 * Usage: {@code CollinearClient input.txt}
 */
public class CollinearClient {
    public static void main(String[] args) {
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        BruteCollinearPoints brute = new BruteCollinearPoints(points);
        StdOut.println("Brute-force: " + brute.numberOfSegments() + " segment(s)");
        for (LineSegment s : brute.segments()) StdOut.println("  " + s);

        FastCollinearPoints fast = new FastCollinearPoints(points);
        StdOut.println("Fast: " + fast.numberOfSegments() + " segment(s)");
        for (LineSegment s : fast.segments()) StdOut.println("  " + s);
    }
}
