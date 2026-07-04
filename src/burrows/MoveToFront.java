import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/**
 * Move-to-front encoding and decoding over the extended-ASCII alphabet
 * (R = 256). Encoding maintains an ordered list of characters; for each input
 * byte it outputs the byte's current position in the list, then moves that
 * character to the front. Decoding inverts the process. Frequently-used
 * characters map to small output values, which improves subsequent compression.
 */
public class MoveToFront {

    private static final int R = 256;

    /** Reads bytes from standard input, MTF-encodes, and writes to standard output. */
    public static void encode() {
        char[] seq = initSequence();
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            // Find c's current position and shift everything before it right by one.
            char pos = 0;
            while (seq[pos] != c) pos++;
            BinaryStdOut.write(pos);
            for (int i = pos; i > 0; i--) seq[i] = seq[i - 1];
            seq[0] = c;
        }
        BinaryStdOut.close();
    }

    /** Reads MTF-encoded bytes from standard input, decodes, and writes output. */
    public static void decode() {
        char[] seq = initSequence();
        while (!BinaryStdIn.isEmpty()) {
            int pos = BinaryStdIn.readChar();
            char c = seq[pos];
            BinaryStdOut.write(c);
            for (int i = pos; i > 0; i--) seq[i] = seq[i - 1];
            seq[0] = c;
        }
        BinaryStdOut.close();
    }

    private static char[] initSequence() {
        char[] seq = new char[R];
        for (int i = 0; i < R; i++) seq[i] = (char) i;
        return seq;
    }

    /** {@code MoveToFront -} encodes; {@code MoveToFront +} decodes. */
    public static void main(String[] args) {
        if (args.length < 1) throw new IllegalArgumentException("usage: MoveToFront (- | +)");
        if (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
        else throw new IllegalArgumentException("unknown flag: " + args[0]);
    }
}
