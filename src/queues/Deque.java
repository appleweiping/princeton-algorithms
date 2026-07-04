import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A double-ended queue (deque) supporting adding and removing items from either
 * end. Implemented as a doubly linked list so that every operation runs in
 * constant worst-case time. The iterator runs in constant worst-case time per
 * call and uses constant extra space.
 *
 * @param <Item> the type of elements held in this deque
 */
public class Deque<Item> implements Iterable<Item> {

    private Node first;
    private Node last;
    private int size;

    private class Node {
        Item item;
        Node prev;
        Node next;
    }

    /** Constructs an empty deque. */
    public Deque() {
        first = null;
        last = null;
        size = 0;
    }

    /** @return true iff the deque is empty. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** @return the number of items on the deque. */
    public int size() {
        return size;
    }

    /**
     * Adds an item to the front.
     *
     * @throws IllegalArgumentException if {@code item} is null
     */
    public void addFirst(Item item) {
        if (item == null) throw new IllegalArgumentException("cannot add null item");
        Node node = new Node();
        node.item = item;
        node.next = first;
        node.prev = null;
        if (first == null) {
            last = node;
        } else {
            first.prev = node;
        }
        first = node;
        size++;
    }

    /**
     * Adds an item to the back.
     *
     * @throws IllegalArgumentException if {@code item} is null
     */
    public void addLast(Item item) {
        if (item == null) throw new IllegalArgumentException("cannot add null item");
        Node node = new Node();
        node.item = item;
        node.prev = last;
        node.next = null;
        if (last == null) {
            first = node;
        } else {
            last.next = node;
        }
        last = node;
        size++;
    }

    /**
     * Removes and returns the item from the front.
     *
     * @throws NoSuchElementException if the deque is empty
     */
    public Item removeFirst() {
        if (isEmpty()) throw new NoSuchElementException("deque is empty");
        Item item = first.item;
        first = first.next;
        if (first == null) {
            last = null;           // deque became empty
        } else {
            first.prev = null;
        }
        size--;
        return item;
    }

    /**
     * Removes and returns the item from the back.
     *
     * @throws NoSuchElementException if the deque is empty
     */
    public Item removeLast() {
        if (isEmpty()) throw new NoSuchElementException("deque is empty");
        Item item = last.item;
        last = last.prev;
        if (last == null) {
            first = null;          // deque became empty
        } else {
            last.next = null;
        }
        size--;
        return item;
    }

    /** @return an iterator over items in order from front to back. */
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<Item> {
        private Node current = first;

        public boolean hasNext() {
            return current != null;
        }

        public Item next() {
            if (current == null) throw new NoSuchElementException("no more items");
            Item item = current.item;
            current = current.next;
            return item;
        }

        public void remove() {
            throw new UnsupportedOperationException("remove is not supported");
        }
    }

    /** Unit tests. */
    public static void main(String[] args) {
        Deque<Integer> d = new Deque<>();
        d.addFirst(1);      // [1]
        d.addLast(2);       // [1, 2]
        d.addFirst(0);      // [0, 1, 2]
        StringBuilder sb = new StringBuilder();
        for (int x : d) sb.append(x).append(' ');
        System.out.println("order front->back: " + sb.toString().trim()); // 0 1 2
        System.out.println("size=" + d.size());               // 3
        System.out.println("removeFirst=" + d.removeFirst()); // 0
        System.out.println("removeLast=" + d.removeLast());   // 2
        System.out.println("size=" + d.size());               // 1
        System.out.println("isEmpty=" + d.isEmpty());         // false
    }
}
