import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

/**
 * This class performs a series of computational experiments to test the percolation threshold calculation
 * @author Castila
 * @date 04.06.2018
 *
 */
public class PercolationStats {   
    /** The equation factor needed for confidence interval */
    private static final double EQUATIONFACTOR = 1.96;
    
    /** perform trials independent experiments on an n-by-n grid
    /** The number of trials */
    private final int trials;
    
    /** The mean of the thresholds */
    private final double mean;
    
    /** The standard deviation of the thresholds */
    private final double stdDev;

    /**
    * 
    * @param n
    * @param trials
    * @throws IllegalArgumentException
    */
   public PercolationStats(int n, int trials) {
     if (n <= 0 || trials <= 0) {
         throw new IllegalArgumentException();
     }
     this.trials = trials;
     boolean error = false;
     double[] thresholds = new double[trials];
     for (int i = 0; i < trials; i++) {
          try {
            Percolation p = new Percolation(n);
            thresholds[i] = findThreshold(p, n);
          } catch (NullPointerException e) {
            error = true;
          }
     }
     mean = StdStats.mean(thresholds);
     if (trials == 1 || error) {
        stdDev = Double.NaN;
     } else {
        stdDev = StdStats.stddev(thresholds);
     }
   }
   
   /**
    * Method to find the percolate threshold
    * @return the threshold
    */
   private double findThreshold(final Percolation p, int dimension) {
     boolean percolates = p.percolates();
     while (!percolates) {
       int row = StdRandom.uniform(dimension + 1);
       if (row == 0) row++;
       int col = StdRandom.uniform(dimension + 1);
       if (col == 0) col++;
       try {
          if (!p.isOpen(row, col)) {
             p.open(row, col);
             percolates = p.percolates();
          }
       } catch (IllegalArgumentException e) {
          return -1;
       }
     }
     return (double) p.numberOfOpenSites()/(dimension*dimension);
   }
   
   /** sample mean of percolation threshold
    * 
    * @return the mean
    */
   public double mean() {
      return mean;
   }
   
   /** sample standard deviation of percolation threshold
    * 
    * @return the standard deviation
    */
   public double stddev() {
     return stdDev;
   }
   
   /** low  endpoint of 95% confidence interval
    * 
    * @return the low value of confidence interval
    */
   public double confidenceLo() {
     return mean - ((EQUATIONFACTOR*stdDev) / Math.sqrt(trials));
   }
   
   /** high endpoint of 95% confidence interval
    * 
    * @return the high value of confidence interval
    */
   public double confidenceHi() {
     return mean + ((EQUATIONFACTOR*stdDev) / Math.sqrt(trials));
   }

   /**
    * Main method to execute the class
    * @param args
    */
   public static void main(String[] args) {
     int n = Integer.parseInt(args[0]);
     int t = Integer.parseInt(args[1]);
     /**
     // takes two command-line arguments n and T
     StdOut.print("Enter matrix dimension:");
     int n = StdIn.readInt();
     StdOut.print("Enter number of trials:");
     int t = StdIn.readInt(); */
     // performs T independent computational experiments (discussed above) on an n-by-n grid,
     PercolationStats ps = new PercolationStats(n, t);
     StdOut.print("Percolated mean: " + ps.mean() + "\n");
     StdOut.print("Percolated standard deviation: " + ps.stddev() + "\n");
      
     if (t > 30) {
       // provides also confidence intervals
       StdOut.print("95% confidence interval = [ " + ps.confidenceLo() + "," + ps.confidenceHi() + "]");
     }
   }
}