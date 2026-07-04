import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Determines which teams in a sports division have been mathematically
 * eliminated from finishing in first place, using a maxflow / mincut reduction.
 *
 * <p>A team x is <em>trivially</em> eliminated if some other team already has
 * more wins than x can possibly reach. Otherwise, x is <em>non-trivially</em>
 * eliminated iff, in the flow network that models all remaining games among the
 * other teams, the maxflow does not saturate every game edge — the source side
 * of the min cut then gives a certificate (a subset R of teams that collectively
 * must win more games than x could ever match).</p>
 */
public class BaseballElimination {

    private final int n;
    private final String[] teams;
    private final Map<String, Integer> teamIndex;
    private final int[] w;         // wins
    private final int[] l;         // losses
    private final int[] r;         // remaining
    private final int[][] g;       // g[i][j] = games left between i and j

    /** Reads the division standings from the given file. */
    public BaseballElimination(String filename) {
        In in = new In(filename);
        n = in.readInt();
        teams = new String[n];
        teamIndex = new HashMap<>();
        w = new int[n];
        l = new int[n];
        r = new int[n];
        g = new int[n][n];

        for (int i = 0; i < n; i++) {
            teams[i] = in.readString();
            teamIndex.put(teams[i], i);
            w[i] = in.readInt();
            l[i] = in.readInt();
            r[i] = in.readInt();
            for (int j = 0; j < n; j++) g[i][j] = in.readInt();
        }
    }

    /** @return number of teams. */
    public int numberOfTeams() {
        return n;
    }

    /** @return all team names. */
    public Iterable<String> teams() {
        List<String> list = new ArrayList<>();
        for (String t : teams) list.add(t);
        return list;
    }

    /** @return wins for the given team. */
    public int wins(String team) {
        return w[index(team)];
    }

    /** @return losses for the given team. */
    public int losses(String team) {
        return l[index(team)];
    }

    /** @return remaining games for the given team. */
    public int remaining(String team) {
        return r[index(team)];
    }

    /** @return number of remaining games between the two teams. */
    public int against(String team1, String team2) {
        return g[index(team1)][index(team2)];
    }

    private int index(String team) {
        Integer i = (team == null) ? null : teamIndex.get(team);
        if (i == null) throw new IllegalArgumentException("invalid team: " + team);
        return i;
    }

    /** @return true iff the given team is mathematically eliminated. */
    public boolean isEliminated(String team) {
        int x = index(team);
        return trivialCertificate(x) != null || nontrivialCertificate(x) != null;
    }

    /**
     * @return the subset of teams that certifies elimination of {@code team},
     *         or null if the team is not eliminated
     */
    public Iterable<String> certificateOfElimination(String team) {
        int x = index(team);
        List<String> trivial = trivialCertificate(x);
        if (trivial != null) return trivial;
        return nontrivialCertificate(x);
    }

    // Trivial elimination: some team i already has more wins than x's ceiling.
    private List<String> trivialCertificate(int x) {
        int ceiling = w[x] + r[x];
        for (int i = 0; i < n; i++) {
            if (i != x && w[i] > ceiling) {
                List<String> cert = new ArrayList<>();
                cert.add(teams[i]);
                return cert;
            }
        }
        return null;
    }

    // Non-trivial elimination via maxflow. Returns the certificate subset R, or
    // null if x is not eliminated by this test.
    private List<String> nontrivialCertificate(int x) {
        // Vertices: source(0), one per game pair (i<j, i,j != x), one per team, sink.
        // We index game vertices and team vertices explicitly.
        int source = 0;
        int sink = 1;
        // team vertices: 2 .. 2+n-1
        int teamVertex = 2;

        // Collect the game pairs among teams other than x.
        List<int[]> pairs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (i == x) continue;
            for (int j = i + 1; j < n; j++) {
                if (j == x) continue;
                pairs.add(new int[]{i, j});
            }
        }

        int gameVertexStart = teamVertex + n;   // game vertices start after team vertices
        int totalVertices = gameVertexStart + pairs.size();

        FlowNetwork network = new FlowNetwork(totalVertices);
        double totalGameCapacity = 0;

        for (int p = 0; p < pairs.size(); p++) {
            int i = pairs.get(p)[0], j = pairs.get(p)[1];
            int gv = gameVertexStart + p;
            int games = g[i][j];
            // source -> game vertex, capacity = games between i and j
            network.addEdge(new FlowEdge(source, gv, games));
            totalGameCapacity += games;
            // game vertex -> each of the two team vertices, capacity infinite
            network.addEdge(new FlowEdge(gv, teamVertex + i, Double.POSITIVE_INFINITY));
            network.addEdge(new FlowEdge(gv, teamVertex + j, Double.POSITIVE_INFINITY));
        }

        // team vertex -> sink, capacity = w[x] + r[x] - w[i] (how many more games
        // team i may win before matching x's ceiling).
        int ceiling = w[x] + r[x];
        for (int i = 0; i < n; i++) {
            if (i == x) continue;
            int cap = ceiling - w[i];
            if (cap < 0) cap = 0;   // should not happen after the trivial test
            network.addEdge(new FlowEdge(teamVertex + i, sink, cap));
        }

        FordFulkerson maxflow = new FordFulkerson(network, source, sink);

        // If every game edge is saturated (maxflow == total capacity), x is NOT
        // eliminated by the non-trivial test. Otherwise the source-side team
        // vertices form the certificate.
        if (maxflow.value() >= totalGameCapacity - 1e-10) return null;

        List<String> cert = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (i != x && maxflow.inCut(teamVertex + i)) cert.add(teams[i]);
        }
        return cert.isEmpty() ? null : cert;
    }

    /** Prints the elimination status (and certificate) of every team. */
    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) StdOut.print(t + " ");
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
