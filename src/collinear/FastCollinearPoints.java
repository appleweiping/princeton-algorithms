import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Finds every maximal line segment containing 4 or more collinear points, using
 * the sorting-based algorithm: for each origin point p, sort the remaining
 * points by the slope they make with p; runs of 3+ equal slopes (i.e. 4+
 * collinear points including p) form a segment. Worst-case running time is
 * O(n^2 log n); extra space (beyond the returned segments) is O(n).
 *
 * <p>To report each maximal segment exactly once, a collinear run is recorded
 * only when the origin p is the smallest point (in natural order) among the
 * points on that segment.</p>
 */
public class FastCollinearPoints {

    private final LineSegment[] segments;

    /**
     * Examines the given points for maximal 4+-point collinear segments.
     *
     * @throws IllegalArgumentException if the array is null, contains a null
     *         point, or contains a repeated point
     */
    public FastCollinearPoints(Point[] points) {
        validate(points);
        Point[] sorted = points.clone();
        Arrays.sort(sorted);                  // natural order; also groups dups
        checkDuplicates(sorted);

        List<LineSegment> found = new ArrayList<>();
        int n = sorted.length;

        // 'byNatural' is a stable reference copy in natural order.
        Point[] byNatural = sorted.clone();

        for (int i = 0; i < n; i++) {
            Point origin = byNatural[i];
            // Sort the others by slope to origin. A stable sort keeps the
            // natural order among points of equal slope, so the first element
            // of each equal-slope run is the smallest such point.
            Point[] bySlope = byNatural.clone();
            Arrays.sort(bySlope, origin.slopeOrder());

            // bySlope[0] is origin itself (slope -inf); scan the rest for runs.
            int j = 1;
            while (j < n) {
                double runSlope = origin.slopeTo(bySlope[j]);
                int k = j;
                while (k < n && origin.slopeTo(bySlope[k]) == runSlope) k++;
                int runLen = k - j;   // number of points collinear with origin
                if (runLen >= 3) {
                    // origin + run of >=3 => >=4 collinear points.
                    // Record only if origin is the smallest point on the segment,
                    // which (since bySlope is stably sorted from natural order)
                    // means origin < bySlope[j] (the smallest of the run).
                    if (origin.compareTo(bySlope[j]) < 0) {
                        found.add(new LineSegment(origin, bySlope[k - 1]));
                    }
                }
                j = k;
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

    private static void checkDuplicates(Point[] pts) {
        for (int i = 1; i < pts.length; i++)
            if (pts[i - 1].compareTo(pts[i]) == 0)
                throw new IllegalArgumentException("points contains a repeated point");
    }
}
