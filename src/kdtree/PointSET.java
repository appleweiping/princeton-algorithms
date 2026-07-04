import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * A brute-force set of 2-D points backed by a red-black BST ({@link TreeSet}).
 * insert/contains are logarithmic; range and nearest are linear (they scan all
 * points). Used as the reference implementation to validate {@link KdTree}.
 */
public class PointSET {

    private final TreeSet<Point2D> points;

    /** Constructs an empty set of points. */
    public PointSET() {
        points = new TreeSet<>();
    }

    /** @return true iff the set is empty. */
    public boolean isEmpty() {
        return points.isEmpty();
    }

    /** @return the number of points in the set. */
    public int size() {
        return points.size();
    }

    /**
     * Adds the point to the set (if it is not already present).
     *
     * @throws IllegalArgumentException if {@code p} is null
     */
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("point is null");
        points.add(p);
    }

    /**
     * @return true iff the set contains point {@code p}
     * @throws IllegalArgumentException if {@code p} is null
     */
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("point is null");
        return points.contains(p);
    }

    /** Draws all points to standard draw. */
    public void draw() {
        for (Point2D p : points) p.draw();
    }

    /**
     * @return all points that are inside the rectangle (or on its boundary)
     * @throws IllegalArgumentException if {@code rect} is null
     */
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("rectangle is null");
        List<Point2D> inside = new ArrayList<>();
        for (Point2D p : points)
            if (rect.contains(p)) inside.add(p);
        return inside;
    }

    /**
     * @return the closest point in the set to {@code p}, or null if empty
     * @throws IllegalArgumentException if {@code p} is null
     */
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("point is null");
        Point2D best = null;
        double bestDist = Double.POSITIVE_INFINITY;
        for (Point2D q : points) {
            double d = p.distanceSquaredTo(q);
            if (d < bestDist) {
                bestDist = d;
                best = q;
            }
        }
        return best;
    }

    /** Unit tests. */
    public static void main(String[] args) {
        PointSET set = new PointSET();
        set.insert(new Point2D(0.1, 0.1));
        set.insert(new Point2D(0.5, 0.5));
        set.insert(new Point2D(0.9, 0.9));
        System.out.println("size=" + set.size());                                  // 3
        System.out.println("contains(0.5,0.5)=" + set.contains(new Point2D(0.5, 0.5))); // true
        System.out.println("nearest to (0.6,0.6)=" + set.nearest(new Point2D(0.6, 0.6))); // (0.5,0.5)
        System.out.println("range [0,0.6]x[0,0.6]:");
        for (Point2D p : set.range(new RectHV(0, 0, 0.6, 0.6))) System.out.println("  " + p);
    }
}
