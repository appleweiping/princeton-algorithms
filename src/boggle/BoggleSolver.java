import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;
import java.util.Set;

/**
 * Finds all valid words on a Boggle board given a dictionary. The dictionary is
 * preprocessed into a 26-way trie so that the depth-first search over the board
 * can prune the moment a path's prefix is not a dictionary prefix. This makes it
 * possible to solve thousands of random boards per second.
 *
 * <p>Rules: a word must be at least 3 letters, formed by a path of
 * horizontally/vertically/diagonally adjacent dice, using each die at most once;
 * the 'Q' die counts as "Qu".</p>
 */
public class BoggleSolver {

    // A 26-way trie node. isWord marks the end of a dictionary word.
    private static final class TrieNode {
        private final TrieNode[] next = new TrieNode[26];
        private boolean isWord;
    }

    private final TrieNode root;

    /**
     * @param dictionary an array of dictionary words (uppercase A-Z only)
     */
    public BoggleSolver(String[] dictionary) {
        if (dictionary == null) throw new IllegalArgumentException("dictionary is null");
        root = new TrieNode();
        for (String word : dictionary) {
            if (word.length() >= 3) put(word);   // words < 3 letters can never score
        }
    }

    private void put(String word) {
        TrieNode node = root;
        for (int i = 0; i < word.length(); i++) {
            int c = word.charAt(i) - 'A';
            if (node.next[c] == null) node.next[c] = new TrieNode();
            node = node.next[c];
        }
        node.isWord = true;
    }

    /** @return all valid words on the board. */
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if (board == null) throw new IllegalArgumentException("board is null");
        Set<String> found = new HashSet<>();
        int rows = board.rows(), cols = board.cols();

        // Cache the board letters for speed.
        char[][] letters = new char[rows][cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                letters[i][j] = board.getLetter(i, j);

        boolean[][] visited = new boolean[rows][cols];
        StringBuilder prefix = new StringBuilder();
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                dfs(letters, i, j, root, visited, prefix, found);
        return found;
    }

    // Depth-first search from cell (i, j). 'node' is the trie node reached by the
    // current prefix (before adding this cell's letter).
    private void dfs(char[][] letters, int i, int j, TrieNode node,
                     boolean[][] visited, StringBuilder prefix, Set<String> found) {
        char letter = letters[i][j];
        // Advance in the trie by this cell's letter(s). Q always means "QU".
        TrieNode child = node.next[letter - 'A'];
        if (child == null) return;                    // dead end: prune
        int added = 1;
        prefix.append(letter);
        if (letter == 'Q') {
            child = child.next['U' - 'A'];
            if (child == null) { prefix.deleteCharAt(prefix.length() - 1); return; }
            prefix.append('U');
            added = 2;
        }

        if (child.isWord && prefix.length() >= 3) {
            found.add(prefix.toString());
        }

        visited[i][j] = true;
        int rows = letters.length, cols = letters[0].length;
        for (int di = -1; di <= 1; di++) {
            for (int dj = -1; dj <= 1; dj++) {
                if (di == 0 && dj == 0) continue;
                int ni = i + di, nj = j + dj;
                if (ni >= 0 && ni < rows && nj >= 0 && nj < cols && !visited[ni][nj]) {
                    dfs(letters, ni, nj, child, visited, prefix, found);
                }
            }
        }
        visited[i][j] = false;
        prefix.delete(prefix.length() - added, prefix.length());
    }

    /**
     * @return the Boggle score of {@code word} if it is in the dictionary, else 0
     */
    public int scoreOf(String word) {
        if (word == null || word.length() < 3 || !contains(word)) return 0;
        int len = word.length();
        if (len <= 4) return 1;
        if (len == 5) return 2;
        if (len == 6) return 3;
        if (len == 7) return 5;
        return 11;                 // 8+ letters
    }

    private boolean contains(String word) {
        TrieNode node = root;
        for (int i = 0; i < word.length(); i++) {
            int c = word.charAt(i) - 'A';
            if (c < 0 || c >= 26) return false;
            node = node.next[c];
            if (node == null) return false;
        }
        return node.isWord;
    }

    /** Reads a dictionary and a board, prints all words and the total score. */
    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        int count = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
            count++;
        }
        StdOut.println("Words = " + count);
        StdOut.println("Score = " + score);
    }
}
