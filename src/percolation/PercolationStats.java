import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

/**
 * Performs a Monte Carlo simulation to estimate the percolation threshold p*
 * of an n-by-n grid.
 *
 * <p>For each of {@code trials} independent experiments, sites are opened at
 * random until the system percolates; the fraction of open sites at that moment
 * is one sample of the threshold. The mean, standard deviation and a 95%
 * confidence interval are reported.</p>
 *
 * Corner cases: constructor throws IllegalArgumentException if n &le; 0 or
 * trials &le; 0.
 */
public class PercolationStats {
    private static final double CONFIDENCE_95 = 1.96;
    private final double[] thresholds;
    private final double mean;
    private final double stddev;

    /**
     * Runs {@code trials} independent percolation experiments on an n-by-n grid.
     *
     * @throws IllegalArgumentException if {@code n <= 0} or {@code trials <= 0}
     */
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0)
            throw new IllegalArgumentException(
                "n and trials must be > 0, got n=" + n + ", trials=" + trials);

        this.thresholds = new double[trials];
        for (int t = 0; t < trials; t++) {
            Percolation perc = new Percolation(n);
            while (!perc.percolates()) {
                int row = StdRandom.uniformInt(1, n + 1);
                int col = StdRandom.uniformInt(1, n + 1);
                perc.open(row, col); // open() is idempotent on already-open sites
            }
            thresholds[t] = (double) perc.numberOfOpenSites() / (n * n);
        }
        this.mean = StdStats.mean(thresholds);
        this.stddev = StdStats.stddev(thresholds);
    }

    /** @return sample mean of the percolation threshold. */
    public double mean() {
        return mean;
    }

    /** @return sample standard deviation of the percolation threshold. */
    public double stddev() {
        return stddev;
    }

    /** @return low endpoint of the 95% confidence interval. */
    public double confidenceLo() {
        return mean - CONFIDENCE_95 * stddev / Math.sqrt(thresholds.length);
    }

    /** @return high endpoint of the 95% confidence interval. */
    public double confidenceHi() {
        return mean + CONFIDENCE_95 * stddev / Math.sqrt(thresholds.length);
    }

    /**
     * Command-line client: {@code PercolationStats n trials}.
     */
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);
        PercolationStats stats = new PercolationStats(n, trials);
        System.out.printf("mean                    = %f%n", stats.mean());
        System.out.printf("stddev                  = %f%n", stats.stddev());
        System.out.printf("95%% confidence interval = [%f, %f]%n",
                stats.confidenceLo(), stats.confidenceHi());
    }
}
