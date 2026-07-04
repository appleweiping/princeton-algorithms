import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/**
 * Client that reads a sequence of strings from standard input and prints
 * exactly {@code k} of them, chosen uniformly at random. Uses reservoir
 * sampling so that at most {@code k} items are ever stored in the randomized
 * queue (satisfying the extra-credit space requirement of at most k items).
 *
 * Usage: {@code Permutation k < input.txt}
 */
public class Permutation {
    public static void main(String[] args) {
        int k = Integer.parseInt(args[0]);
        RandomizedQueue<String> queue = new RandomizedQueue<>();

        int count = 0;
        // Reservoir sampling: keep the queue at size <= k at all times.
        while (!StdIn.isEmpty()) {
            String item = StdIn.readString();
            count++;
            if (queue.size() < k) {
                queue.enqueue(item);
            } else if (k > 0) {
                // With probability k/count, replace a random existing item.
                if (edu.princeton.cs.algs4.StdRandom.uniformInt(count) < k) {
                    queue.dequeue();
                    queue.enqueue(item);
                }
            }
        }

        for (int i = 0; i < k; i++) {
            StdOut.println(queue.dequeue());
        }
    }
}
