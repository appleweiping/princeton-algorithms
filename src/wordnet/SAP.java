import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/**
 * Computes the shortest ancestral path (SAP) between vertices (or sets of
 * vertices) in a directed graph. An ancestral path between v and w is a shortest
 * path from v to a common ancestor x together with a shortest path from w to x;
 * a shortest ancestral path is one of minimum total length.
 *
 * <p>Each query runs two BFS traversals (from v and from w) and takes the
 * ancestor minimizing the summed distance, so every query is O(E + V).</p>
 */
public class SAP {

    private final Digraph graph;   // an immutable defensive copy

    /**
     * @throws IllegalArgumentException if {@code G} is null
     */
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException("digraph is null");
        this.graph = new Digraph(G);   // defensive copy for immutability
    }

    /** @return length of the shortest ancestral path between v and w; -1 if none. */
    public int length(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        return search(new BreadthFirstDirectedPaths(graph, v),
                      new BreadthFirstDirectedPaths(graph, w))[1];
    }

    /** @return a common ancestor of v and w on the shortest ancestral path; -1 if none. */
    public int ancestor(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        return search(new BreadthFirstDirectedPaths(graph, v),
                      new BreadthFirstDirectedPaths(graph, w))[0];
    }

    /** @return length of the shortest ancestral path between any v in {@code v} and any w in {@code w}; -1 if none. */
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertices(v);
        validateVertices(w);
        return search(new BreadthFirstDirectedPaths(graph, v),
                      new BreadthFirstDirectedPaths(graph, w))[1];
    }

    /** @return a common ancestor that participates in the shortest ancestral path; -1 if none. */
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertices(v);
        validateVertices(w);
        return search(new BreadthFirstDirectedPaths(graph, v),
                      new BreadthFirstDirectedPaths(graph, w))[0];
    }

    // Returns {ancestor, length}. Scans all vertices reachable from both sources,
    // keeping the one with minimum summed distance.
    private int[] search(BreadthFirstDirectedPaths bfsV, BreadthFirstDirectedPaths bfsW) {
        int bestAncestor = -1;
        int bestLength = Integer.MAX_VALUE;
        for (int x = 0; x < graph.V(); x++) {
            if (bfsV.hasPathTo(x) && bfsW.hasPathTo(x)) {
                int len = bfsV.distTo(x) + bfsW.distTo(x);
                if (len < bestLength) {
                    bestLength = len;
                    bestAncestor = x;
                }
            }
        }
        if (bestAncestor == -1) return new int[]{-1, -1};
        return new int[]{bestAncestor, bestLength};
    }

    private void validateVertex(int v) {
        if (v < 0 || v >= graph.V())
            throw new IllegalArgumentException("vertex " + v + " out of range [0, " + (graph.V() - 1) + "]");
    }

    private void validateVertices(Iterable<Integer> vertices) {
        if (vertices == null) throw new IllegalArgumentException("vertex iterable is null");
        for (Integer v : vertices) {
            if (v == null) throw new IllegalArgumentException("iterable contains a null vertex");
            validateVertex(v);
        }
    }

    /** Reads a digraph and answers SAP length/ancestor queries from stdin. */
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d%n", length, ancestor);
        }
    }
}
