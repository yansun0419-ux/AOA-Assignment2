package Problem2;

import java.util.Random;

public class LargestZeroMatrix {

  // Result class to encapsulate algorithm output
  static class Result {
    int maxSize;
    long durationNs;
    double memoryMB; // Theoretical memory allocation (Input + DP Table)

    public Result(int maxSize, long durationNs, double memoryMB) {
      this.maxSize = maxSize;
      this.durationNs = durationNs;
      this.memoryMB = memoryMB;
    }
  }

  /**
   * Core dynamic programming algorithm to find largest zero square submatrix
   * Uses byte[][] as input to save memory
   */
  public static Result solveDP(byte[][] matrix) {
    int m = matrix.length;
    if (m == 0)
      return new Result(0, 0, 0);
    int n = matrix[0].length;

    long startTime = System.nanoTime();

    // Memory calculation (Theoretical Allocation)
    // 1. Input matrix: m * n * 1 byte
    // 2. DP table: m * n * 2 bytes (short type)
    // Total: 3 * m * n bytes
    // Note: short is used instead of byte because for 1000x1000 matrices,
    // the maximum square side length could reach 1000, exceeding byte's max value
    // (127)
    double theoreticalMemoryMB = (double) (m * n * (1 + 2)) / (1024 * 1024);

    short[][] dp = new short[m][n];
    int maxSize = 0;

    // Dynamic programming process
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        if (matrix[i][j] == 0) {
          if (i == 0 || j == 0) {
            dp[i][j] = 1;
          } else {
            // Bellman Equation
            int minNeighbor = Math.min(dp[i - 1][j], Math.min(dp[i][j - 1], dp[i - 1][j - 1]));
            dp[i][j] = (short) (minNeighbor + 1);
          }

          if (dp[i][j] > maxSize) {
            maxSize = dp[i][j];
          }
        } else {
          dp[i][j] = 0;
        }
      }
    }

    long endTime = System.nanoTime();
    return new Result(maxSize, (endTime - startTime), theoreticalMemoryMB);
  }

  // Generate random matrix with fixed seed for reproducibility
  public static byte[][] generateMatrix(int rows, int cols, long seed) {
    byte[][] matrix = new byte[rows][cols];
    Random rand = new Random(seed);
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        // Randomly generate 0 or 1
        matrix[i][j] = (byte) rand.nextInt(2);
      }
    }
    return matrix;
  }

  public static void runExperiments() {
    System.out.println("=== Problem 2 Experiments (Fixed Seed) ===");
    // Print table header
    System.out.printf("%-15s %-15s %-15s %-15s\n", "Elements(m*n)", "Time(ms)", "Memory(MB)", "MaxSqSize");

    int[][] dimensions = {
        { 10, 10 },
        { 10, 100 },
        { 10, 1000 },
        { 100, 1000 },
        { 1000, 1000 }
    };

    long seed = 999;

    for (int[] dim : dimensions) {
      int r = dim[0];
      int c = dim[1];

      // Generate test data
      byte[][] matrix = generateMatrix(r, c, seed);

      // Run algorithm
      Result res = solveDP(matrix);

      // Print results
      System.out.printf("%-15d %-15.4f %-15.4f %-15d\n",
          (r * c),
          res.durationNs / 1e6,
          res.memoryMB,
          res.maxSize);
    }
  }

  public static void main(String[] args) {
    // Simple test case
    System.out.println("=== Simple Test Case ===");
    byte[][] testMatrix = {
        { 1, 1, 1, 0 },
        { 1, 0, 0, 0 },
        { 1, 0, 0, 0 },
        { 1, 0, 0, 1 }
    };
    Result testRes = solveDP(testMatrix);
    System.out.println("Max Square Size: " + testRes.maxSize);
    System.out.println("------------------------");

    // Run experiments
    runExperiments();
  }
}