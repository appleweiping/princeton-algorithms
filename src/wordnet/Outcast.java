import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

/**
 * Identifies the "outcast" noun in a list: the noun whose total semantic
 * distance to all the others is greatest (i.e. the least related to the rest).
 */
public class Outcast {

    private final WordNet wordnet;

    /** @throws IllegalArgumentException if {@code wordnet} is null */
    public Outcast(WordNet wordnet) {
        if (wordnet == null) throw new IllegalArgumentException("wordnet is null");
        this.wordnet = wordnet;
    }

    /**
     * @return the noun in {@code nouns} that is least related to the others,
     *         i.e. the one maximizing the sum of distances to the rest
     */
    public String outcast(String[] nouns) {
        String outcast = null;
        int maxDistance = -1;
        for (String candidate : nouns) {
            int total = 0;
            for (String other : nouns) {
                total += wordnet.distance(candidate, other);
            }
            if (total > maxDistance) {
                maxDistance = total;
                outcast = candidate;
            }
        }
        return outcast;
    }

    /** Reads a WordNet and one or more noun-list files; prints each outcast. */
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
