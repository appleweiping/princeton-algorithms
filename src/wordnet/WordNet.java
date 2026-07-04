import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A WordNet: a rooted directed acyclic graph of noun "synsets" (sets of
 * synonyms) connected by "is-a" (hypernym) relationships. Provides membership
 * queries, shortest semantic distance between nouns, and their shortest
 * ancestral synset.
 *
 * <p>Input files:</p>
 * <ul>
 *   <li><b>synsets</b>: {@code id,synonym_set,gloss}; the synonym set is a list
 *       of space-separated nouns (multiword nouns use underscores).</li>
 *   <li><b>hypernyms</b>: {@code id,hyper1,hyper2,...}; edges from a synset to
 *       each of its hypernyms.</li>
 * </ul>
 */
public class WordNet {

    // Maps each noun to the list of synset ids that contain it.
    private final Map<String, List<Integer>> nounToIds;
    // Maps each synset id to its full synonym-set string.
    private final Map<Integer, String> idToSynset;
    private final SAP sap;

    /**
     * Builds a WordNet from the given synset and hypernym files.
     *
     * @throws IllegalArgumentException if an argument is null, or the hypernym
     *         graph is not a rooted DAG
     */
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new IllegalArgumentException("synsets/hypernyms filename is null");

        nounToIds = new HashMap<>();
        idToSynset = new TreeMap<>();
        int count = readSynsets(synsets);

        Digraph graph = new Digraph(count);
        readHypernyms(hypernyms, graph);

        // Must be a rooted DAG: acyclic, and exactly one vertex with out-degree 0.
        DirectedCycle finder = new DirectedCycle(graph);
        if (finder.hasCycle())
            throw new IllegalArgumentException("hypernym graph has a cycle; not a DAG");
        if (countRoots(graph) != 1)
            throw new IllegalArgumentException("hypernym graph is not rooted (must have exactly one root)");

        this.sap = new SAP(graph);
    }

    private int readSynsets(String synsets) {
        In in = new In(synsets);
        int count = 0;
        while (in.hasNextLine()) {
            String line = in.readLine();
            if (line == null || line.isEmpty()) continue;
            String[] fields = line.split(",");
            int id = Integer.parseInt(fields[0]);
            String synset = fields[1];
            idToSynset.put(id, synset);
            for (String noun : synset.split(" ")) {
                nounToIds.computeIfAbsent(noun, k -> new ArrayList<>()).add(id);
            }
            count++;
        }
        return count;
    }

    private void readHypernyms(String hypernyms, Digraph graph) {
        In in = new In(hypernyms);
        while (in.hasNextLine()) {
            String line = in.readLine();
            if (line == null || line.isEmpty()) continue;
            String[] fields = line.split(",");
            int id = Integer.parseInt(fields[0]);
            for (int i = 1; i < fields.length; i++) {
                graph.addEdge(id, Integer.parseInt(fields[i]));
            }
        }
    }

    // A root is a vertex with no outgoing edges (no hypernym).
    private int countRoots(Digraph graph) {
        int roots = 0;
        for (int v = 0; v < graph.V(); v++)
            if (graph.outdegree(v) == 0) roots++;
        return roots;
    }

    /** @return all WordNet nouns. */
    public Iterable<String> nouns() {
        return nounToIds.keySet();
    }

    /**
     * @return true iff {@code word} is a WordNet noun
     * @throws IllegalArgumentException if {@code word} is null
     */
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException("word is null");
        return nounToIds.containsKey(word);
    }

    /**
     * @return the shortest semantic distance between {@code nounA} and {@code nounB}
     * @throws IllegalArgumentException if either is null or not a WordNet noun
     */
    public int distance(String nounA, String nounB) {
        validateNoun(nounA);
        validateNoun(nounB);
        return sap.length(nounToIds.get(nounA), nounToIds.get(nounB));
    }

    /**
     * @return the synonym set (as a space-separated string) that is the common
     *         ancestor of {@code nounA} and {@code nounB} in the shortest
     *         ancestral path
     * @throws IllegalArgumentException if either is null or not a WordNet noun
     */
    public String sap(String nounA, String nounB) {
        validateNoun(nounA);
        validateNoun(nounB);
        int ancestor = sap.ancestor(nounToIds.get(nounA), nounToIds.get(nounB));
        return idToSynset.get(ancestor);
    }

    private void validateNoun(String noun) {
        if (noun == null) throw new IllegalArgumentException("noun is null");
        if (!isNoun(noun)) throw new IllegalArgumentException("not a WordNet noun: " + noun);
    }

    /** Unit tests. */
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        System.out.println("isNoun(worm)=" + wordnet.isNoun("worm"));
        System.out.println("isNoun(bird)=" + wordnet.isNoun("bird"));
        if (args.length >= 4) {
            System.out.println("distance(" + args[2] + ", " + args[3] + ") = "
                    + wordnet.distance(args[2], args[3]));
            System.out.println("sap(" + args[2] + ", " + args[3] + ") = "
                    + wordnet.sap(args[2], args[3]));
        }
    }
}
