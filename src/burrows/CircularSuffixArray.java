import java.util.Arrays;

/**
 * Describes the abstraction of a sorted array of the n circular suffixes of a
 * string of length n, without materializing the suffixes themselves. Sorting is
 * done on indices with a comparator that compares circular suffixes character by
 * character, giving O(n log n) time (with O(n) comparison worst case, this is
 * O(n^2 log n) in the pathological case, but n log n comparisons on average and
 * well within the assignment's linearithmic target for typical inputs).
 *
 * <p>Space is O(n): only the string and the index permutation are stored.</p>
 */
public class CircularSuffixArray {

    private final int n;
    private final Integer[] index;   // index[i] = original position of i-th sorted suffix

    /**
     * @throws IllegalArgumentException if {@code s} is null
     */
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException("string is null");
        this.n = s.length();
        this.index = new Integer[n];
        for (int i = 0; i < n; i++) index[i] = i;

        // Sort suffix start-offsets by comparing circular suffixes.
        Arrays.sort(index, (a, b) -> {
            for (int k = 0; k < n; k++) {
                char ca = s.charAt((a + k) % n);
                char cb = s.charAt((b + k) % n);
                if (ca != cb) return Character.compare(ca, cb);
            }
            return 0;   // identical circular suffixes
        });
    }

    /** @return the length n of the string. */
    public int length() {
        return n;
    }

    /**
     * @return the original starting index of the i-th sorted circular suffix
     * @throws IllegalArgumentException if {@code i} is not in [0, n-1]
     */
    public int index(int i) {
        if (i < 0 || i >= n) throw new IllegalArgumentException("i out of range: " + i);
        return index[i];
    }

    /** Unit tests. */
    public static void main(String[] args) {
        CircularSuffixArray csa = new CircularSuffixArray("ABRACADABRA!");
        System.out.println("length = " + csa.length());   // 12
        System.out.print("index = [");
        for (int i = 0; i < csa.length(); i++) {
            System.out.print(csa.index(i));
            if (i < csa.length() - 1) System.out.print(", ");
        }
        System.out.println("]");
        // Expected: [11, 10, 7, 0, 3, 5, 8, 1, 4, 6, 9, 2]
    }
}
