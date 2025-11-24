package Problem2;

import java.util.Random;

public class LargestZeroMatrix {

  // 结果封装
  static class Result {
    int maxSize;
    long durationNs;
    double memoryMB; // 理论分配内存 (Input + DP Table)

    public Result(int maxSize, long durationNs, double memoryMB) {
      this.maxSize = maxSize;
      this.durationNs = durationNs;
      this.memoryMB = memoryMB;
    }
  }

  /**
   * 核心 DP 算法
   * 使用 byte[][] 作为输入以节省空间 (题目提示优化内存)
   */
  public static Result solveDP(byte[][] matrix) {
    int m = matrix.length;
    if (m == 0)
      return new Result(0, 0, 0);
    int n = matrix[0].length;

    long startTime = System.nanoTime();

    // === 内存计算 (Theoretical Allocation) ===
    // 1. 输入矩阵: m * n * 1 byte (byte类型)
    // 2. DP 表: m * n * 2 bytes (short类型)
    // 总计: 3 * m * n bytes
    // 注意：我们使用 short 而不是 byte，因为 1000x1000 的矩阵
    // 最大边长可能达到 1000，超过了 byte 的最大值 (127)。
    double theoreticalMemoryMB = (double) (m * n * (1 + 2)) / (1024 * 1024);

    short[][] dp = new short[m][n];
    int maxSize = 0;

    // DP Process
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        if (matrix[i][j] == 0) { // 0 代表目标区域
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

  // 生成随机矩阵 (使用固定种子保证可复现)
  public static byte[][] generateMatrix(int rows, int cols, long seed) {
    byte[][] matrix = new byte[rows][cols];
    Random rand = new Random(seed); // 固定种子
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        // 随机生成 0 或 1
        matrix[i][j] = (byte) rand.nextInt(2);
      }
    }
    return matrix;
  }

  public static void runExperiments() {
    System.out.println("=== Problem 2 Experiments (Fixed Seed) ===");
    // 打印表头，包含 MaxSqSize
    System.out.printf("%-15s %-15s %-15s %-15s\n", "Elements(m*n)", "Time(ms)", "Memory(MB)", "MaxSqSize");

    int[][] dimensions = {
        { 10, 10 },
        { 10, 100 },
        { 10, 1000 },
        { 100, 1000 },
        { 1000, 1000 }
    };

    long seed = 999; // 实验用的固定种子

    for (int[] dim : dimensions) {
      int r = dim[0];
      int c = dim[1];

      // 生成测试数据 (Input)
      byte[][] matrix = generateMatrix(r, c, seed);

      // 运行算法
      Result res = solveDP(matrix);

      // 打印一行数据
      System.out.printf("%-15d %-15.4f %-15.4f %-15d\n",
          (r * c),
          res.durationNs / 1e6, // ns -> ms
          res.memoryMB,
          res.maxSize);
    }
  }

  public static void main(String[] args) {
    // 1. 简单测试 (验证逻辑)
    System.out.println("=== Simple Test Case ===");
    byte[][] testMatrix = {
        { 1, 1, 1, 0 },
        { 1, 0, 0, 0 },
        { 1, 0, 0, 0 },
        { 1, 0, 0, 1 }
    };
    Result testRes = solveDP(testMatrix);
    System.out.println("Max Square Size: " + testRes.maxSize); // 预期输出 2
    System.out.println("------------------------");

    // 2. 运行实验
    runExperiments();
  }
}