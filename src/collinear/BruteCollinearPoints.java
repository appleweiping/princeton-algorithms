import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Finds every maximal line segment containing exactly 4 collinear points by
 * examining all 4-point combinations (brute force). Worst-case running time is
 * O(n^4); extra space (beyond the returned segments) is O(n).
 */
public class BruteCollinearPoints {

    private final LineSegment[] segments;

    /**
     * Examines the given points for 4-point collinear segments.
     *
     * @throws IllegalArgumentException if the array is null, contains a null
     *         point, or contains a repeated point
     */
    public BruteCollinearPoints(Point[] points) {
        validate(points);
        Point[] pts = points.clone();
        Arrays.sort(pts);                 // sort by natural (y, x) order
        checkDuplicates(pts);

        List<LineSegment> found = new ArrayList<>();
        int n = pts.length;
        for (int a = 0; a < n; a++) {
            for (int b = a + 1; b < n; b++) {
                double slopeAB = pts[a].slopeTo(pts[b]);
                for (int c = b + 1; c < n; c++) {
                    double slopeAC = pts[a].slopeTo(pts[c]);
                    if (slopeAB != slopeAC) continue;      // early prune
                    for (int d = c + 1; d < n; d++) {
                        double slopeAD = pts[a].slopeTo(pts[d]);
                        if (slopeAB == slopeAD) {
                            // pts sorted, so a is smallest and d is largest.
                            found.add(new LineSegment(pts[a], pts[d]));
                        }
                    }
                }
            }
        }
        segments = found.toArray(new LineSegment[0]);
    }

    /** @return the number of maximal line segments. */
    public int numberOfSegments() {
        return segments.length;
    }

    /** @return the maximal line segments. */
    public LineSegment[] segments() {
        return segments.clone();
    }

    private static void validate(Point[] points) {
        if (points == null) throw new IllegalArgumentException("points array is null");
        for (Point p : points)
            if (p == null) throw new IllegalArgumentException("points contains a null point");
    }

    // Assumes pts already sorted; adjacent equal points indicate a duplicate.
    private static void checkDuplicates(Point[] pts) {
        for (int i = 1; i < pts.length; i++)
            if (pts[i - 1].compareTo(pts[i]) == 0)
                throw new IllegalArgumentException("points contains a repeated point");
    }
}
