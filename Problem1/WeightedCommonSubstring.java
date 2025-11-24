package Problem1;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WeightedCommonSubstring {

  // 定义一个简单的类来保存结果：分数、子串、以及具体的位置信息
  static class Result {
    double maxScore;
    String substring;
    // 记录在 s1 和 s2 中的起始和结束索引 (0-based)
    int s1Start, s1End;
    int s2Start, s2End;

    public Result(double maxScore, String substring, int endI, int endJ) {
      this.maxScore = maxScore;
      this.substring = substring;
      // DP表的下标是 1-based (1..m)，对应字符串下标是 0..m-1
      // endI 是 DP 表中的索引，对应字符串索引是 endI - 1
      // 子串长度
      int len = substring.length();

      if (len > 0) {
        this.s1End = endI - 1;
        this.s1Start = this.s1End - len + 1;
        this.s2End = endJ - 1;
        this.s2Start = this.s2End - len + 1;
      } else {
        this.s1Start = this.s1End = -1;
        this.s2Start = this.s2End = -1;
      }
    }
  }

  /**
   * 核心 DP 算法
   */
  public static Result solve(String s1, String s2, Map<Character, Double> weights, double delta) {
    int m = s1.length();
    int n = s2.length();

    // dp[i][j] 代表以 s1[i-1] 和 s2[j-1] 结尾的公共子串的最大分数
    double[][] dp = new double[m + 1][n + 1];

    double globalMaxScore = 0;
    int maxI = 0;
    int maxJ = 0;

    // 填充 DP 表
    for (int i = 1; i <= m; i++) {
      for (int j = 1; j <= n; j++) {
        char c1 = s1.charAt(i - 1);
        char c2 = s2.charAt(j - 1);

        double score;
        if (c1 == c2) {
          // 匹配
          score = dp[i - 1][j - 1] + weights.getOrDefault(c1, 1.0);
        } else {
          // 不匹配
          score = dp[i - 1][j - 1] - delta;
        }

        // 局部对齐逻辑：如果得分 < 0，则重置为 0
        dp[i][j] = Math.max(0, score);

        // 更新全局最大值
        if (dp[i][j] > globalMaxScore) {
          globalMaxScore = dp[i][j];
          maxI = i;
          maxJ = j;
        }
      }
    }

    // 回溯提取最优子串
    StringBuilder sb = new StringBuilder();
    int currI = maxI;
    int currJ = maxJ;

    while (currI > 0 && currJ > 0 && dp[currI][currJ] > 0) {
      sb.append(s1.charAt(currI - 1));
      currI--;
      currJ--;
    }

    return new Result(globalMaxScore, sb.reverse().toString(), maxI, maxJ);
  }

  // --- 实验部分 ---

  // Scenario 1: 固定权重测试
  public static void runScenario1() {
    System.out.println("=== Running Scenario 1 (Verification) ===");
    String s1 = "ABCAABCAA";
    String s2 = "ABBCAACCBBBBBB";

    Map<Character, Double> weights = new HashMap<>();
    for (char c = 'A'; c <= 'Z'; c++) {
      weights.put(c, 1.0);
    }

    double delta = 10.0;
    Result res = solve(s1, s2, weights, delta);

    System.out.printf("Input S1: %s\nInput S2: %s\n", s1, s2);
    System.out.printf("Parameters: Weight=1.0, Delta=%.1f\n", delta);
    System.out.println("------------------------------------------------");
    System.out.printf("Max Score: %.2f\n", res.maxScore);
    System.out.printf("Substring: \"%s\"\n", res.substring);
    // 打印详细位置，验证 Copilot 提到的准确性问题
    if (res.maxScore > 0) {
      System.out.printf("Location in S1: index %d to %d\n", res.s1Start, res.s1End);
      System.out.printf("Location in S2: index %d to %d\n", res.s2Start, res.s2End);
    }
    System.out.println("================================================\n");
  }

  // Scenario 2: 真实频率权重 + 固定种子随机数据
  public static void runScenario2() {
    System.out.println("=== Running Scenario 2 (Experiments) ===");

    // 使用固定种子 (Seed) 保证结果可复现 (Reproducible)
    long seed = 12345;
    String s1 = generateRandomString(1000, seed);
    // 使用不同的种子或者在这个基础上继续生成，这里简单起见再次调用带种子的生成会重置，
    // 所以我们传递 Random 对象或者用不同的种子更好。
    // 为了简单且确定，我们用 seed+1 生成 s2
    String s2 = generateRandomString(1000, seed + 1);

    Map<Character, Double> freqWeights = new HashMap<>();
    // Cornell University Math Dept. Frequency Data
    freqWeights.put('A', 8.12);
    freqWeights.put('B', 1.49);
    freqWeights.put('C', 2.71);
    freqWeights.put('D', 4.32);
    freqWeights.put('E', 12.02);
    freqWeights.put('F', 2.30);
    freqWeights.put('G', 2.03);
    freqWeights.put('H', 5.92);
    freqWeights.put('I', 7.31);
    freqWeights.put('J', 0.10);
    freqWeights.put('K', 0.69);
    freqWeights.put('L', 3.98);
    freqWeights.put('M', 2.61);
    freqWeights.put('N', 6.95);
    freqWeights.put('O', 7.68);
    freqWeights.put('P', 1.82);
    freqWeights.put('Q', 0.11);
    freqWeights.put('R', 6.02);
    freqWeights.put('S', 6.28);
    freqWeights.put('T', 9.10);
    freqWeights.put('U', 2.88);
    freqWeights.put('V', 1.11);
    freqWeights.put('W', 2.09);
    freqWeights.put('X', 0.17);
    freqWeights.put('Y', 2.11);
    freqWeights.put('Z', 0.07);

    double minW = 0.07;
    double maxW = 12.02;
    double step = (maxW - minW) / 9.0;

    System.out.printf("%-10s %-10s %-10s %-30s\n", "Delta", "MaxScore", "Length", "Substring (First 15 chars)");
    System.out.println("-------------------------------------------------------------------");

    for (int i = 0; i < 10; i++) {
      double delta = minW + i * step;
      Result res = solve(s1, s2, freqWeights, delta);

      String displayStr = res.substring.length() > 15 ? res.substring.substring(0, 15) + "..." : res.substring;
      System.out.printf("%-10.2f %-10.2f %-10d %-30s\n", delta, res.maxScore, res.substring.length(), displayStr);
    }
  }

  // 生成随机大写字母字符串 (带种子)
  private static String generateRandomString(int length, long seed) {
    StringBuilder sb = new StringBuilder();
    Random rand = new Random(seed);
    for (int i = 0; i < length; i++) {
      sb.append((char) ('A' + rand.nextInt(26)));
    }
    return sb.toString();
  }

  public static void main(String[] args) {
    runScenario1();
    runScenario2();
  }
}