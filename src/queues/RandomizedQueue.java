import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A randomized queue: {@code dequeue()} and {@code sample()} return an item
 * chosen uniformly at random from the items currently present. Backed by a
 * resizing array, so enqueue/dequeue run in constant amortized time. Each
 * iterator returns the items in an independent uniformly random order and is
 * constructed in linear time; its {@code next()} / {@code hasNext()} run in
 * constant worst-case time.
 *
 * @param <Item> the type of elements held in this queue
 */
public class RandomizedQueue<Item> implements Iterable<Item> {

    private Item[] items;
    private int size;

    /** Constructs an empty randomized queue. */
    @SuppressWarnings("unchecked")
    public RandomizedQueue() {
        items = (Item[]) new Object[2];
        size = 0;
    }

    /** @return true iff the queue is empty. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** @return the number of items on the queue. */
    public int size() {
        return size;
    }

    @SuppressWarnings("unchecked")
    private void resize(int capacity) {
        Item[] copy = (Item[]) new Object[capacity];
        for (int i = 0; i < size; i++) copy[i] = items[i];
        items = copy;
    }

    /**
     * Adds an item.
     *
     * @throws IllegalArgumentException if {@code item} is null
     */
    public void enqueue(Item item) {
        if (item == null) throw new IllegalArgumentException("cannot enqueue null item");
        if (size == items.length) resize(2 * items.length);
        items[size++] = item;
    }

    /**
     * Removes and returns a random item.
     *
     * @throws NoSuchElementException if the queue is empty
     */
    public Item dequeue() {
        if (isEmpty()) throw new NoSuchElementException("queue is empty");
        int idx = StdRandom.uniformInt(size);
        Item item = items[idx];
        // Move the last item into the vacated slot to keep the array compact.
        items[idx] = items[size - 1];
        items[size - 1] = null;        // avoid loitering
        size--;
        if (size > 0 && size == items.length / 4) resize(items.length / 2);
        return item;
    }

    /**
     * Returns (but does not remove) a random item.
     *
     * @throws NoSuchElementException if the queue is empty
     */
    public Item sample() {
        if (isEmpty()) throw new NoSuchElementException("queue is empty");
        return items[StdRandom.uniformInt(size)];
    }

    /** @return an independent iterator over the items in random order. */
    public Iterator<Item> iterator() {
        return new RandomizedIterator();
    }

    private class RandomizedIterator implements Iterator<Item> {
        private final Item[] shuffled;
        private int current;

        @SuppressWarnings("unchecked")
        RandomizedIterator() {
            shuffled = (Item[]) new Object[size];
            for (int i = 0; i < size; i++) shuffled[i] = items[i];
            StdRandom.shuffle(shuffled);   // independent random order
            current = 0;
        }

        public boolean hasNext() {
            return current < shuffled.length;
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException("no more items");
            return shuffled[current++];
        }

        public void remove() {
            throw new UnsupportedOperationException("remove is not supported");
        }
    }

    /** Unit tests. */
    public static void main(String[] args) {
        RandomizedQueue<Integer> q = new RandomizedQueue<>();
        for (int i = 1; i <= 5; i++) q.enqueue(i);
        System.out.println("size=" + q.size());        // 5
        System.out.println("sample in [1,5]: " + q.sample());
        StringBuilder sb = new StringBuilder();
        for (int x : q) sb.append(x).append(' ');
        System.out.println("one random order: " + sb.toString().trim());
        int a = q.dequeue(), b = q.dequeue();
        System.out.println("dequeued two: " + a + ", " + b);
        System.out.println("size=" + q.size());        // 3
    }
}
