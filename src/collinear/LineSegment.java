/**
 * An immutable line segment connecting two points in the plane. This is a
 * faithful reimplementation of the helper class provided with the Princeton
 * "Collinear Points" assignment (which is not distributed in algs4.jar). Its
 * behavior — draw() and toString() — matches the assignment specification.
 */
public class LineSegment {

    private final Point p;   // one endpoint of the segment
    private final Point q;   // the other endpoint of the segment

    /**
     * Constructs the line segment between points p and q.
     *
     * @throws IllegalArgumentException if either endpoint is null, or if the
     *         two endpoints are equal
     */
    public LineSegment(Point p, Point q) {
        if (p == null || q == null)
            throw new IllegalArgumentException("argument to LineSegment constructor is null");
        if (p.compareTo(q) == 0)
            throw new IllegalArgumentException("both arguments to LineSegment constructor are the same point: " + p);
        this.p = p;
        this.q = q;
    }

    /** Draws this line segment to standard draw. */
    public void draw() {
        p.drawTo(q);
    }

    /** @return a string representation, e.g. "(1, 0) -> (3, 4)". */
    public String toString() {
        return p + " -> " + q;
    }
}
