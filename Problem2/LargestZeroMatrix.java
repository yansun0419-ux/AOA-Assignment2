package Problem2;

import java.util.Random;

public class LargestZeroMatrix {

  // 定义一个简单的结果类，方便传递数据
  static class Result {
    int maxSize;
    long durationNs;
    double memoryMB; // 存储该次运行分配的数组大小

    public Result(int maxSize, long durationNs, double memoryMB) {
      this.maxSize = maxSize;
      this.durationNs = durationNs;
      this.memoryMB = memoryMB;
    }
  }

  /**
   * 核心 DP 算法
   * 返回 Result 对象而不是直接打印
   */
  public static Result solveDP(int[][] matrix) {
    int m = matrix.length;
    if (m == 0)
      return new Result(0, 0, 0);
    int n = matrix[0].length;

    // 记录开始时间
    long startTime = System.nanoTime();

    // 使用 short 数组 (2 bytes per element)
    // 理论内存占用 = m * n * 2 bytes
    // 这比 Runtime.freeMemory() 准确得多，更能反映算法的空间复杂度
    double theoreticalMemoryMB = (double) (m * n * 2) / (1024 * 1024);

    short[][] dp = new short[m][n];

    int maxSize = 0;

    // DP Process
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        if (matrix[i][j] == 0) {
          if (i == 0 || j == 0) {
            dp[i][j] = 1;
          } else {
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

  // 生成随机矩阵
  public static int[][] generateMatrix(int rows, int cols) {
    int[][] matrix = new int[rows][cols];
    Random rand = new Random();
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        matrix[i][j] = rand.nextInt(2);
      }
    }
    return matrix;
  }

  public static void runExperiments() {
    System.out.println("=== Problem 2 Experiments ===");
    // 使用 String.format 控制对齐，%-15s 表示左对齐占15格
    System.out.printf("%-15s %-15s %-15s\n", "Elements(m*n)", "Time(ms)", "Memory(MB)");

    int[][] dimensions = {
        { 10, 10 },
        { 10, 100 },
        { 10, 1000 },
        { 100, 1000 },
        { 1000, 1000 }
    };

    for (int[] dim : dimensions) {
      int r = dim[0];
      int c = dim[1];
      int[][] matrix = generateMatrix(r, c);

      // 运行算法
      Result res = solveDP(matrix);

      // 打印一行数据
      System.out.printf("%-15d %-15.4f %-15.4f\n",
          (r * c),
          res.durationNs / 1e6, // 纳秒转毫秒
          res.memoryMB);
    }
  }

  public static void main(String[] args) {
    // 1. 简单测试 (用于验证正确性)
    System.out.println("=== Simple Test Case ===");
    int[][] testMatrix = {
        { 1, 1, 1, 0 },
        { 1, 0, 0, 0 },
        { 1, 0, 0, 0 },
        { 1, 0, 0, 1 }
    };
    Result testRes = solveDP(testMatrix);
    System.out.println("Max Square Size: " + testRes.maxSize); // 应该输出 2
    System.out.println("------------------------");

    // 2. 运行实验 (用于画图)
    runExperiments();
  }
}