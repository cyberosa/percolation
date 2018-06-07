/**
 * Java class to estimate the percolation threshold of a matrix
 * @author Castila
 * @date 04.06.2018
 */

import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class Percolation {
    /** Dimension of the grid */
    private final int dimension;
    
    /** Number of open sites in the grid */
    private int openSites = 0;
    
    /** Array containing the elements with the ids for the quick union find */
    private final WeightedQuickUnionUF elements;
   
    /** Percolate matrix with three states: 0 (blocked), 1 (open), 2 (full) */
    private final int[][] matrix;
    
    /** Flag to indicate when the system percolates */
    private boolean percolated = false;

    /**
     * Constructor: create n-by-n grid, with all sites blocked
     * @param n the dimension of the grid
     */
    public Percolation(int n) {
       if (n <= 0) throw new IllegalArgumentException("Non-valid parameter: " + n);
       // use the WeightedQuickUnionUF algorithm
       dimension = n;
       percolated = false;
       openSites = 0;
       matrix = new int[n][n];
       for (int i = 0; i < n; i++) {
          for (int j = 0; j < n; j++) {
            matrix[i][j] = 0; // all elements blocked
          }
       }
       elements = new WeightedQuickUnionUF(n * n);
   }
   
   /** open site (row, col) if it is not open already
    * 
    * @param row row index
    * @param col column index
    */
   public void open(int row, int col) {
      if (checkDimensions(row, col)) {
         // if the element is already open or full then do nothing
         if (isOpen(row, col)) return;
      // all elements of row 1 go directly to status Full
         if (row == 1) {
            matrix[row - 1][col - 1] = 2;
         } else {
            matrix[row - 1][col - 1] = 1;
         }
         openSites++;
         addUnionWithNeighbours(row, col);
         
      }
   }
   
   /**
    * Method to add all possible union with neighbours if open. Update also full status.
    * @param row row position
    * @param col column position
    */
   private void addUnionWithNeighbours(int row, int col) {
     // check new unions with adjacent elements or update full status
     int id = (row - 1)*dimension + (col - 1);
     boolean open =  matrix[row - 1][col - 1] == 1;
      // the order of checking the neighbours is important, we start from above
     boolean stateChanged = false;
     // neighbour 1
     if (row > 1) {
         if (isOpen(row - 1, col)) {
            elements.union(id, id - dimension);
            stateChanged = updateAdjacentStates(row, col, row - 1, col);
         }
     }
     open = !stateChanged;
     stateChanged = false;
     // neighbour 2
     if (col > 1) {
         if (isOpen(row, col - 1)) {
            elements.union(id, id - 1);
            stateChanged = updateAdjacentStates(row, col, row, col - 1);
         }
     }
     if (open && stateChanged && row > 1 && isOpen(row - 1, col)) {
         // repeat visit first neighbour;
         updateAdjacentStates(row, col, row - 1, col);
     }
     open = !stateChanged; 
     stateChanged = false;
     // neighbour  3
     if (col < dimension) {
         if (isOpen(row, col + 1)) {
            elements.union(id, id + 1);
            stateChanged = updateAdjacentStates(row, col, row, col + 1);
         }
     }
     if (open && stateChanged) {
         // repeat visit first and second neighbour; 
          if (row > 1 && isOpen(row - 1, col)) updateAdjacentStates(row, col, row - 1, col);
          if (col > 1 && isOpen(row, col - 1)) updateAdjacentStates(row, col, row, col - 1);
     }
     open = !stateChanged;
     stateChanged = false;
     // neighbour 4 
     if (row < dimension) {
         if (isOpen(row + 1, col)) {
            elements.union(id, id + dimension);
            stateChanged = updateAdjacentStates(row, col, row + 1, col);
         }
     }
     if (open && stateChanged) {
         // repeat visit first, second and third neighbour;
         if (row > 1 && isOpen(row - 1, col)) updateAdjacentStates(row, col, row - 1, col);
         if (col > 1 && isOpen(row, col - 1)) updateAdjacentStates(row, col, row, col - 1);
         if (col < dimension && isOpen(row, col + 1)) updateAdjacentStates(row, col, row, col + 1);
     }
   }
   
   /**
    * Method to unify the state of adjacent cells that were connected
    * @param row1 row of cell 1 (the just opened cell)
    * @param col1 column of cell 1 (the just opened cell)
    * @param row2 row of cell 2 (neighbour)
    * @param col2 column of cell 2 (neighbour)
    * @returns true if the state of the first cell was changed
    */
   private boolean updateAdjacentStates(int row1, int col1, int row2, int col2) {
     if (matrix[row1 - 1][col1 - 1] == 1 &&
           matrix[row2 - 1][col2 - 1] == 2) {
          // the neighbour was already in state Full so we just need to
          // update the current cell to Full
          // StdOut.print("Full site: ("+ row1 + "," + col1+")\n");
          matrix[row1 - 1][col1 - 1] = 2;
          if ((row1 == dimension)) percolated = true;
          return true;
     } else if (matrix[row1 - 1][col1 - 1] == 2 && 
           matrix[row2 - 1][col2 - 1] == 1) {
           // we need to update the neighbour and all adjacent open neighbours
           // StdOut.print("Full site: ("+ row2+ "," + col2+")\n");
           matrix[row2 - 1][col2 - 1] = 2;
           if ((row2 == dimension)) percolated = true;
           if (col2 < dimension && matrix[row2 - 1][col2] == 1) {  
              // equivalent to  isOpen(row2, col2 + 1)) {
              updateAdjacentStates(row2, col2, row2, col2 + 1);
           }
           if (col2 > 1 && matrix[row2 - 1][col2 - 2] == 1) { 
             // equivalent to isOpen(row2, col2 - 1)) {
             updateAdjacentStates(row2, col2, row2, col2 - 1);
           }
           if (row2 < dimension && matrix[row2][col2 - 1] == 1) {
             // equivalent to  isOpen(row2 + 1, col2)) {
              updateAdjacentStates(row2, col2, row2 + 1, col2);
           }
           if (row2 > 1 && matrix[row2 - 2][col2 - 1] == 1) {
              // equivalent to  isOpen(row2 - 1, col2)) {
              updateAdjacentStates(row2, col2, row2 - 1, col2);
           }
      }
     return false;
   }
   
   /**
    * Method to check if a site (row, col) is open
    * @param row the row index
    * @param col the column index
    * @return true if it is open
    */
   public boolean isOpen(int row, int col) {
     if (checkDimensions(row, col)) {
       return (matrix[row - 1][col - 1] == 1 || matrix[row - 1][col - 1] == 2);
     }
      throw new IllegalArgumentException("Wrong index arguments");
   }
   
   /** Method to check if a site (row, col) is full
    * A full site is an open site that can be connected to an open site in the top row via a chain of neighboring
    *  (left, right, up, down) open sites
    * @param row the row index
    * @param col the column index
    * @return true if it is blocked
    */
   public boolean isFull(int row, int col) {
     if (checkDimensions(row, col)) {
      return matrix[row - 1][col - 1] == 2;
     }
     throw new IllegalArgumentException("Wrong index arguments");
   }
   
   /**
    * Method to count the number of open sites
    * @return the number of open sites
    */
   public int numberOfOpenSites() {
      return openSites;
   }
   
   /** Method to check if the system percolates
    * 
    * @return true if percolates
    */
   public boolean percolates() {
     return percolated;
   }

   /**
    * Main method for testing
    * @param args
    */
   public static void main(String[] args) {
     StdOut.print("Enter matrix dimension:");
     int n = StdIn.readInt();
     Percolation p = new Percolation(n);
     boolean percolates = p.percolates();
     while (!percolates) {
         int row = StdRandom.uniform(n + 1);
         if (row == 0) row++;
         int col = StdRandom.uniform(n + 1);
         if (col == 0) col++;
         try {
           if (!p.isOpen(row, col)) {
              p.open(row, col);
              percolates = p.percolates();
           }
         } catch (IllegalArgumentException e) {
           StdOut.print("Exception while opening the grid cells");
         }
     }
     StdOut.print("The system percolates when: " + p.openSites + " sites are opened.\n");
     double estimation = (double) p.openSites/(n * n);
     StdOut.print("Estimation of the percolation threshold: " + estimation);
   }
   
   /**
    * Method to check the dimensions according to the grid
    * @param row the row index
    * @param col the column index
    * @return true if dimensions are ok
    */
   private boolean checkDimensions(int row, int col) {
       if (row <= 0) {
          throw new IllegalArgumentException("Row is out of bounds: " + row);
       } else if (col <= 0) {
          throw new IllegalArgumentException("Col is out of bounds: " + col);
       }
       if (row > dimension) {
          throw new IllegalArgumentException("Row is out of bounds: " + row);
       } else if (col > dimension) {
          throw new IllegalArgumentException("Col is out of bounds: " + col);
       }
       return true;
   }   
}
