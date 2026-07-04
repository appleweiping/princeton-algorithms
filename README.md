# Princeton Algorithms I & II — Programming Assignments

> Solutions to all 10 programming assignments of Princeton's **Algorithms, Part I & II**
> (Sedgewick & Wayne) — from Percolation to Burrows-Wheeler — an independent, from-scratch
> implementation built on `algs4`, part of a [csdiy.wiki](https://csdiy.wiki/) full-catalog build.

![status](https://img.shields.io/badge/status-complete-brightgreen)
![language](https://img.shields.io/badge/Java-21-informational)
![license](https://img.shields.io/badge/license-MIT-blue)

## Overview

Princeton's *Algorithms* is the canonical undergraduate data-structures-and-algorithms course.
Its 10 programming assignments each take a real algorithmic technique — union-find, elementary
data structures, sorting, priority queues / A\* search, geometric search trees, graph BFS,
shortest paths via dynamic programming, max-flow / min-cut, tries, and data compression — and
require a from-scratch implementation that must pass the course's correctness **and**
time/space complexity requirements.

This repo implements every assignment in Java against the official `algs4.jar` library, and
verifies each one with real runs: known-answer checks from the assignment specs, cross-validation
against independent brute-force reference implementations, statistical tests, and end-to-end
pipelines that produce measured results.

## Results (measured on this machine — Windows, JDK 21, CPU-only)

| # | Assignment | What it does | Result (measured) |
|---|---|---|---|
| 1 | **Percolation** | Monte-Carlo percolation threshold via union-find | p\* = **0.592** (known ≈ 0.5927); backwash prevented |
| 2 | **Deques & Randomized Queues** | Constant-time deque + randomized queue | dequeue uniformity **~25.0%** over 400k trials; independent iterators |
| 3 | **Collinear Points** | Brute O(n⁴) + Fast O(n² log n) segment finders | **2000/2000** random trials brute==fast; timing ratio **1.99** (~n² log n) |
| 4 | **8 Puzzle** | A\* search with Manhattan heuristic | puzzle04=**4**, puzzle07=**7**, hardest-3×3=**31** moves (1.9 s); unsolvable detected |
| 5 | **KdTrees** | 2d-tree with range/nearest pruning | vs brute on **100k** points: **10000/10000** queries agree; **229k** nearest/sec |
| 6 | **WordNet** | Rooted-DAG semantic distance (SAP) | matches assignment `digraph1.txt` answers; **20/20** checks pass |
| 7 | **Seam Carving** | Content-aware resize via dual-gradient energy + DP | energy(1,1)=**189.0106** (hand-verified); 150×100→100×70 carve in **75 ms** |
| 8 | **Baseball Elimination** | Max-flow / min-cut elimination certificates | teams4 & teams5 match canonical answers; **17/17** assertions pass |
| 9 | **Boggle** | Trie-backed board word search | solver==brute-force cross-check; Qu rule works; **~4800 boards/sec** |
| 10 | **Burrows-Wheeler** | BWT + move-to-front + circular suffix array | full BWT+MTF+Huffman pipeline **lossless**, **3.36×** compression (vs 1.7× Huffman-only) |

Sample generated artifact — a seam-carved image (`results/seam_carved.png`) produced by removing
50 vertical + 30 horizontal minimum-energy seams from a 150×100 synthetic image:

![seam carved](results/seam_carved.png)

Full run logs for every assignment are in [`results/logs/`](results/logs/).

## Implemented assignments

**Part I**
- [x] **Percolation** — `Percolation` (dual union-find, backwash-free), `PercolationStats` (Monte Carlo, 95% CI)
- [x] **Deques and Randomized Queues** — `Deque` (doubly linked list), `RandomizedQueue` (resizing array), `Permutation` (reservoir sampling)
- [x] **Collinear Points** — `Point` (slopeTo/slopeOrder), `BruteCollinearPoints`, `FastCollinearPoints`
- [x] **8 Puzzle** — `Board` (Hamming/Manhattan/neighbors/twin), `Solver` (A\* with `MinPQ`)
- [x] **KdTrees** — `PointSET` (brute reference), `KdTree` (2d-tree, pruned range & nearest)

**Part II**
- [x] **WordNet** — `SAP` (dual-BFS shortest ancestral path), `WordNet` (rooted-DAG validation), `Outcast`
- [x] **Seam Carving** — `SeamCarver` (dual-gradient energy, DP min-seam, seam removal)
- [x] **Baseball Elimination** — `BaseballElimination` (trivial + max-flow elimination, min-cut certificate)
- [x] **Boggle** — `BoggleSolver` (26-way trie + pruned DFS), `BoggleBoard`
- [x] **Burrows-Wheeler** — `CircularSuffixArray`, `MoveToFront`, `BurrowsWheeler`

## Project structure

```
princeton-algorithms/
├── lib/algs4.jar            # Sedgewick & Wayne algs4 library (from algs4.cs.princeton.edu)
├── src/
│   ├── percolation/         # Percolation, PercolationStats
│   ├── queues/              # Deque, RandomizedQueue, Permutation
│   ├── collinear/           # Point, Brute/FastCollinearPoints, LineSegment
│   ├── eightpuzzle/         # Board, Solver
│   ├── kdtree/              # PointSET, KdTree
│   ├── wordnet/             # SAP, WordNet, Outcast
│   ├── seam/                # SeamCarver
│   ├── baseball/            # BaseballElimination
│   ├── boggle/              # BoggleSolver, BoggleBoard
│   └── burrows/             # CircularSuffixArray, MoveToFront, BurrowsWheeler
├── data/                    # small test inputs (large data downloaded at runtime, gitignored)
├── results/                 # measured run logs + generated seam image
└── build.ps1                # compile all assignments against algs4.jar
```

## How to run

Requires **JDK 21** (`javac`/`java` on PATH). `lib/algs4.jar` is included; if missing, download it
from `https://algs4.cs.princeton.edu/code/algs4.jar`.

```powershell
# Compile everything into out/<assignment>/
powershell -ExecutionPolicy Bypass -File build.ps1
```

```bash
CP="lib/algs4.jar;out/percolation"     # ';' is the Windows classpath separator

# 1. Percolation — Monte Carlo threshold on a 200x200 grid, 100 trials
java -cp "lib/algs4.jar;out/percolation" PercolationStats 200 100

# 4. 8 Puzzle — solve a board
java -cp "lib/algs4.jar;out/eightpuzzle" Solver data/eightpuzzle/puzzle31.txt

# 8. Baseball Elimination
java -cp "lib/algs4.jar;out/baseball" BaseballElimination data/baseball/teams5.txt

# 9. Boggle — first build the dictionary (369k English words, not committed):
bash data/boggle/fetch-dictionary.sh
java -cp "lib/algs4.jar;out/boggle" BoggleSolver data/boggle/dictionary-english.txt data/boggle/board4x4.txt

# 10. Burrows-Wheeler — full lossless compression pipeline
java -cp "lib/algs4.jar;out/burrows" BurrowsWheeler - < input.txt \
  | java -cp "lib/algs4.jar;out/burrows" MoveToFront - \
  | java -cp "lib/algs4.jar" edu.princeton.cs.algs4.Huffman - > input.compressed
```

## Verification

Every assignment was verified with real runs (all commands and outputs captured in
[`results/logs/`](results/logs/)):

- **Percolation** — estimated threshold 0.592 vs the known constant ≈ 0.5927; an explicit backwash
  test confirms bottom-connected-but-not-top sites are not reported full.
- **Randomized Queue** — 400k-trial test shows each element is dequeued first ~25.0% of the time;
  two iterators over the same queue give independent permutations.
- **Collinear Points** — 2000 random inputs: the fast and brute solvers return identical segment
  sets; timing at n=800 vs n=1600 gives ratio 1.99, consistent with O(n² log n).
- **8 Puzzle** — solved puzzles of known optimal length (4, 7) and the hardest solvable 3×3 (31
  moves); unsolvable boards correctly detected via the twin technique.
- **KdTree** — cross-validated against the brute-force `PointSET` on 100k random points:
  `contains`, `nearest`, and `range` all agree on 10 000 queries; 229k nearest queries/sec.
- **WordNet / SAP** — reproduces the assignment's `digraph1.txt` reference answers; 20/20
  distance/ancestor/outcast checks on a hand-built hierarchy.
- **Seam Carving** — energy of an interior pixel matches a hand computation (√35725 = 189.0106);
  the minimum seam provably follows a designed low-energy channel; a real 150×100 → 100×70 carve
  runs in 75 ms; all exception cases fire.
- **Baseball Elimination** — matches the canonical answers for `teams4.txt` (Philadelphia and
  Montreal eliminated, with the expected certificates) and `teams5.txt` (Detroit eliminated).
- **Boggle** — output cross-checked against an independent brute-force reference; the "Qu" die
  rule finds `QUEEN`; solves ~4800 random 4×4 boards/sec on a 369k-word dictionary.
- **Burrows-Wheeler** — `CircularSuffixArray` matches the `ABRACADABRA!` reference index; the
  full **BWT → MTF → Huffman** pipeline losslessly compresses a real 27 974-byte input to 8 326
  bytes (3.36×), decompressing to a byte-exact match, versus 1.7× for Huffman alone.

## Tech stack

- **Java 21**, compiled with `javac`, run with `java`.
- **algs4** (`algs4.jar`) — Sedgewick & Wayne's library for `WeightedQuickUnionUF`, `MinPQ`,
  `Digraph`, `BreadthFirstDirectedPaths`, `FlowNetwork`/`FordFulkerson`, `Point2D`/`RectHV`,
  `Picture`, `BinaryStdIn`/`BinaryStdOut`, `Huffman`, and the standard I/O libraries.

## Key ideas / what I learned

- **Union-find** with a second UF to eliminate backwash — a clean example of choosing the data
  structure to encode exactly the invariant you need.
- **A\*** with an admissible (Manhattan) heuristic, priority-function caching, and the twin trick
  to decide solvability without a separate parity argument.
- **kd-trees**: geometric pruning turns worst-case linear range/nearest search into something that
  is fast in practice, validated head-to-head against a brute-force oracle.
- **Reductions**: baseball elimination becomes a max-flow / min-cut problem, and the min cut
  *is* the human-readable certificate.
- **Compression as a pipeline**: Burrows-Wheeler + move-to-front don't compress on their own —
  they reorganize the data so that an entropy coder (Huffman) does far better.

## Credits & license

Based on the programming assignments of **Algorithms, Part I & II** by Robert Sedgewick and Kevin
Wayne (Princeton University). This repository is an independent educational reimplementation; all
course materials, assignment specifications, and the `algs4` library belong to their original
authors. `algs4.jar` is redistributed here under its own (GPLv3) license for convenience. Original
implementation code in this repo is released under the [MIT License](LICENSE).
