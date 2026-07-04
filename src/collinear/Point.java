import edu.princeton.cs.algs4.StdDraw;

import java.util.Comparator;

/**
 * An immutable point with integer coordinates in the plane, ordered by
 * y-coordinate then x-coordinate. Provides slope computation and a comparator
 * that orders points by the slope they make with this point.
 */
public class Point implements Comparable<Point> {

    private final int x;
    private final int y;

    /** Constructs the point (x, y). */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /** Draws this point to standard draw. */
    public void draw() {
        StdDraw.point(x, y);
    }

    /** Draws the line segment from this point to {@code that}. */
    public void drawTo(Point that) {
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    /**
     * Returns the slope between this point and {@code that}.
     * <ul>
     *   <li>Horizontal line segment: positive zero.</li>
     *   <li>Vertical line segment: positive infinity.</li>
     *   <li>Degenerate line segment (both points equal): negative infinity.</li>
     * </ul>
     */
    public double slopeTo(Point that) {
        if (this.x == that.x && this.y == that.y) return Double.NEGATIVE_INFINITY;
        if (this.x == that.x) return Double.POSITIVE_INFINITY; // vertical
        if (this.y == that.y) return +0.0;                     // horizontal (force +0)
        return (double) (that.y - this.y) / (that.x - this.x);
    }

    /**
     * Compares two points by y-coordinate, breaking ties by x-coordinate.
     * Formally, (x0, y0) < (x1, y1) iff y0 < y1, or y0 == y1 and x0 < x1.
     */
    public int compareTo(Point that) {
        if (this.y < that.y) return -1;
        if (this.y > that.y) return +1;
        return Integer.compare(this.x, that.x);
    }

    /**
     * Returns a comparator that orders two points by the slopes they make with
     * this (invoking) point.
     */
    public Comparator<Point> slopeOrder() {
        return new SlopeOrder();
    }

    private class SlopeOrder implements Comparator<Point> {
        public int compare(Point p, Point q) {
            return Double.compare(slopeTo(p), slopeTo(q));
        }
    }

    /** @return a string representation of this point, e.g. "(3, 4)". */
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    /** Unit tests. */
    public static void main(String[] args) {
        Point p = new Point(0, 0);
        System.out.println("slope to (1,1) = " + p.slopeTo(new Point(1, 1)));  // 1.0
        System.out.println("slope to (2,0) = " + p.slopeTo(new Point(2, 0)));  // 0.0
        System.out.println("slope to (0,5) = " + p.slopeTo(new Point(0, 5)));  // Infinity
        System.out.println("slope to (0,0) = " + p.slopeTo(new Point(0, 0)));  // -Infinity
        System.out.println("compareTo (0,1): " + p.compareTo(new Point(0, 1))); // -1
    }
}
