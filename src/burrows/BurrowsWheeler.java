import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/**
 * The Burrows-Wheeler transform and its inverse. The forward transform rearranges
 * the input's characters (via the sorted circular suffix array) so that
 * sequences of repeated characters cluster together, which makes the data far
 * more compressible after move-to-front + Huffman. The inverse transform
 * reconstructs the original using the "next" array derived from the last column.
 */
public class BurrowsWheeler {

    private static final int R = 256;

    /**
     * Forward transform: reads input, writes a 4-byte integer {@code first} (the
     * row of the sorted circular suffixes that equals the original string)
     * followed by the last column {@code t[]}.
     */
    public static void transform() {
        String s = BinaryStdIn.readString();
        int n = s.length();
        CircularSuffixArray csa = new CircularSuffixArray(s);

        // Find 'first': the sorted row whose suffix starts at original index 0.
        int first = -1;
        for (int i = 0; i < n; i++) {
            if (csa.index(i) == 0) { first = i; break; }
        }
        BinaryStdOut.write(first);

        // Last column: for sorted suffix starting at index[i], the last char is
        // the one just before it, i.e. s[(index[i] + n - 1) % n].
        for (int i = 0; i < n; i++) {
            int start = csa.index(i);
            BinaryStdOut.write(s.charAt((start + n - 1) % n));
        }
        BinaryStdOut.close();
    }

    /**
     * Inverse transform: reads {@code first} and the last column {@code t[]},
     * reconstructs the original string using the {@code next[]} array, and writes
     * it out.
     */
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        StringBuilder sb = new StringBuilder();
        while (!BinaryStdIn.isEmpty()) sb.append(BinaryStdIn.readChar());
        char[] t = new char[sb.length()];
        for (int i = 0; i < t.length; i++) t[i] = sb.charAt(i);
        int n = t.length;

        // Key-indexed counting to build the first column (sorted t) and the
        // next[] array. next[i] gives the row in which the (i+1)-th original
        // character's row appears.
        int[] count = new int[R + 1];
        for (int i = 0; i < n; i++) count[t[i] + 1]++;
        for (int c = 0; c < R; c++) count[c + 1] += count[c];

        int[] next = new int[n];
        // Process t in order; for each character, its position in the sorted
        // first column determines next[].
        for (int i = 0; i < n; i++) {
            next[count[t[i]]++] = i;
        }

        // Reconstruct the original string by following next[] from 'first'.
        int p = next[first];
        for (int i = 0; i < n; i++) {
            BinaryStdOut.write(t[p]);
            p = next[p];
        }
        BinaryStdOut.close();
    }

    /** {@code BurrowsWheeler -} transforms; {@code BurrowsWheeler +} inverts. */
    public static void main(String[] args) {
        if (args.length < 1) throw new IllegalArgumentException("usage: BurrowsWheeler (- | +)");
        if (args[0].equals("-")) transform();
        else if (args[0].equals("+")) inverseTransform();
        else throw new IllegalArgumentException("unknown flag: " + args[0]);
    }
}
