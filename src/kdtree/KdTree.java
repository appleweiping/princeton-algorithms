import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;
import java.util.List;

/**
 * A 2d-tree: a BST of 2-D points where levels alternate between splitting on the
 * x- and y-coordinate. Supports efficient range search and nearest-neighbor
 * queries via geometric pruning: a subtree is only explored if its axis-aligned
 * bounding rectangle could contain a relevant point.
 *
 * <p>Points are assumed to lie in the unit square [0,1] x [0,1].</p>
 */
public class KdTree {

    private Node root;
    private int size;

    private static final class Node {
        private final Point2D p;      // the point stored here
        private final RectHV rect;    // the axis-aligned rectangle for this subtree
        private Node left;            // left/bottom subtree
        private Node right;           // right/top subtree

        Node(Point2D p, RectHV rect) {
            this.p = p;
            this.rect = rect;
        }
    }

    /** Constructs an empty 2d-tree. */
    public KdTree() {
        root = null;
        size = 0;
    }

    /** @return true iff the tree is empty. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** @return the number of points in the tree. */
    public int size() {
        return size;
    }

    /**
     * Adds the point to the tree (if it is not already present).
     *
     * @throws IllegalArgumentException if {@code p} is null
     */
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("point is null");
        root = insert(root, p, true, 0.0, 0.0, 1.0, 1.0);
    }

    // vertical == true means this level splits on x (a vertical dividing line).
    private Node insert(Node node, Point2D p, boolean vertical,
                        double xmin, double ymin, double xmax, double ymax) {
        if (node == null) {
            size++;
            return new Node(p, new RectHV(xmin, ymin, xmax, ymax));
        }
        if (node.p.equals(p)) return node;   // already present; ignore

        if (vertical) {
            // Compare x; left child gets x < node.x, right child gets x >= node.x.
            if (p.x() < node.p.x())
                node.left = insert(node.left, p, false, xmin, ymin, node.p.x(), ymax);
            else
                node.right = insert(node.right, p, false, node.p.x(), ymin, xmax, ymax);
        } else {
            // Compare y.
            if (p.y() < node.p.y())
                node.left = insert(node.left, p, true, xmin, ymin, xmax, node.p.y());
            else
                node.right = insert(node.right, p, true, xmin, node.p.y(), xmax, ymax);
        }
        return node;
    }

    /**
     * @return true iff the tree contains point {@code p}
     * @throws IllegalArgumentException if {@code p} is null
     */
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("point is null");
        Node node = root;
        boolean vertical = true;
        while (node != null) {
            if (node.p.equals(p)) return true;
            boolean goLeft;
            if (vertical) goLeft = p.x() < node.p.x();
            else          goLeft = p.y() < node.p.y();
            node = goLeft ? node.left : node.right;
            vertical = !vertical;
        }
        return false;
    }

    /** Draws all points (black) and their dividing lines (red = vertical, blue = horizontal). */
    public void draw() {
        draw(root, true);
    }

    private void draw(Node node, boolean vertical) {
        if (node == null) return;
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        node.p.draw();
        StdDraw.setPenRadius();
        if (vertical) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(node.p.x(), node.rect.ymin(), node.p.x(), node.rect.ymax());
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(node.rect.xmin(), node.p.y(), node.rect.xmax(), node.p.y());
        }
        draw(node.left, !vertical);
        draw(node.right, !vertical);
    }

    /**
     * @return all points inside (or on the boundary of) {@code rect}
     * @throws IllegalArgumentException if {@code rect} is null
     */
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("rectangle is null");
        List<Point2D> result = new ArrayList<>();
        range(root, rect, result);
        return result;
    }

    private void range(Node node, RectHV rect, List<Point2D> result) {
        if (node == null) return;
        // Prune: if the query rectangle does not intersect this subtree's
        // bounding rectangle, nothing here can be inside it.
        if (!rect.intersects(node.rect)) return;
        if (rect.contains(node.p)) result.add(node.p);
        range(node.left, rect, result);
        range(node.right, rect, result);
    }

    /**
     * @return the closest point in the tree to {@code p}, or null if empty
     * @throws IllegalArgumentException if {@code p} is null
     */
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("point is null");
        if (root == null) return null;
        return nearest(root, p, root.p, true);
    }

    private Point2D nearest(Node node, Point2D query, Point2D best, boolean vertical) {
        if (node == null) return best;
        // Prune: if the closest possible point in this subtree's rectangle is
        // farther than the current best, skip the entire subtree.
        if (node.rect.distanceSquaredTo(query) >= query.distanceSquaredTo(best))
            return best;

        if (query.distanceSquaredTo(node.p) < query.distanceSquaredTo(best))
            best = node.p;

        // Explore the side of the splitting line that the query lies on first,
        // so the "best" tightens quickly and the other side is more likely pruned.
        boolean queryOnLeft;
        if (vertical) queryOnLeft = query.x() < node.p.x();
        else          queryOnLeft = query.y() < node.p.y();

        Node near = queryOnLeft ? node.left : node.right;
        Node far  = queryOnLeft ? node.right : node.left;

        best = nearest(near, query, best, !vertical);
        best = nearest(far, query, best, !vertical);
        return best;
    }

    /** Unit tests. */
    public static void main(String[] args) {
        KdTree tree = new KdTree();
        tree.insert(new Point2D(0.7, 0.2));
        tree.insert(new Point2D(0.5, 0.4));
        tree.insert(new Point2D(0.2, 0.3));
        tree.insert(new Point2D(0.4, 0.7));
        tree.insert(new Point2D(0.9, 0.6));
        System.out.println("size=" + tree.size());                                  // 5
        System.out.println("contains(0.5,0.4)=" + tree.contains(new Point2D(0.5, 0.4))); // true
        System.out.println("contains(0.5,0.5)=" + tree.contains(new Point2D(0.5, 0.5))); // false
        System.out.println("nearest to (0.6,0.5)=" + tree.nearest(new Point2D(0.6, 0.5)));
        System.out.println("range [0.3,0.9]x[0.3,0.7]:");
        for (Point2D p : tree.range(new RectHV(0.3, 0.3, 0.9, 0.7))) System.out.println("  " + p);
    }
}
