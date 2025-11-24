package Problem1;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WeightedCommonSubstring {

  // 定义一个简单的类来保存结果：分数和对应的子串
  static class Result {
    double maxScore;
    String substring;
    int endI; // 记录结束位置，方便调试
    int endJ;

    public Result(double maxScore, String substring, int endI, int endJ) {
      this.maxScore = maxScore;
      this.substring = substring;
      this.endI = endI;
      this.endJ = endJ;
    }
  }

  /**
   * 核心 DP 算法
   * * @param s1 字符串1
   * 
   * @param s2      字符串2
   * @param weights 字符权重表 (比如 'A' -> 1.0)
   * @param delta   不匹配时的惩罚 (正数，计算时会用减法)
   * @return Result 对象包含最高分和子串
   */
  public static Result solve(String s1, String s2, Map<Character, Double> weights, double delta) {
    int m = s1.length();
    int n = s2.length();

    // 1. 初始化 DP 表
    // dp[i][j] 代表以 s1[i-1] 和 s2[j-1] 结尾的公共子串的最大分数
    // 默认初始化为 0.0
    double[][] dp = new double[m + 1][n + 1];

    double globalMaxScore = 0;
    int maxI = 0;
    int maxJ = 0;

    // 2. 填充 DP 表 (Iterative approach, NO recursion)
    for (int i = 1; i <= m; i++) {
      for (int j = 1; j <= n; j++) {
        char c1 = s1.charAt(i - 1);
        char c2 = s2.charAt(j - 1);

        double score;
        if (c1 == c2) {
          // 匹配：增加权重
          // getOrDefault 防止遇到未定义的字符报错，默认给 1.0
          score = dp[i - 1][j - 1] + weights.getOrDefault(c1, 1.0);
        } else {
          // 不匹配：扣分 (惩罚 delta)
          score = dp[i - 1][j - 1] - delta;
        }

        // 关键点：如果是负数，重置为 0 (Local Alignment 逻辑)
        // 这意味着我们放弃之前的子串，从当前位置重新开始尝试
        dp[i][j] = Math.max(0, score);

        // 更新全局最大值
        if (dp[i][j] > globalMaxScore) {
          globalMaxScore = dp[i][j];
          maxI = i;
          maxJ = j;
        }
      }
    }

    // 3. 回溯 (Traceback) 提取最优子串
    // 从 (maxI, maxJ) 往回找，直到分数变为 0 或到达边界
    StringBuilder sb = new StringBuilder();
    int currI = maxI;
    int currJ = maxJ;

    // 只要当前分数大于 0，说明还在同一个子串路径上
    while (currI > 0 && currJ > 0 && dp[currI][currJ] > 0) {
      // 注意：因为只允许连续子串，所以肯定是沿对角线回溯
      sb.append(s1.charAt(currI - 1));
      currI--;
      currJ--;
    }

    // 因为是从后往前加的，所以要反转回来
    return new Result(globalMaxScore, sb.reverse().toString(), maxI, maxJ);
  }

  // --- 下面是实验用的辅助函数 ---

  // Scenario 1: 所有权重为 1, delta = 10
  public static void runScenario1() {
    System.out.println("=== Running Scenario 1 ===");
    String s1 = "ABCAABCAA";
    String s2 = "ABBCAACCBBBBBB";

    Map<Character, Double> weights = new HashMap<>();
    // 简单权重：所有字母权重都是 1.0
    for (char c = 'A'; c <= 'Z'; c++) {
      weights.put(c, 1.0);
    }

    double delta = 10.0;
    Result res = solve(s1, s2, weights, delta);

    System.out.printf("S1: %s\nS2: %s\nDelta: %.1f\n", s1, s2, delta);
    System.out.printf("Max Score: %.2f\nSubstring: %s\n", res.maxScore, res.substring);
    System.out.println("--------------------------");
  }

  // Scenario 2: 真实频率权重, Delta 变化
  public static void runScenario2() {
    System.out.println("=== Running Scenario 2 (Real English Frequencies) ===");

    // 生成随机字符串用于测试 (保持长一点，以便观察效果)
    // 注意：虽然权重不能随机，但输入的字符串 s1 和 s2 题目没说不能随机生成
    // 题目只说了 "synthetic data" (合成数据)，所以随机字符串是可以的
    String s1 = generateRandomString(1000);
    String s2 = generateRandomString(1000);

    Map<Character, Double> freqWeights = new HashMap<>();

    // 数据来源：Cornell University Math Department
    // 频率越高，通常认为匹配价值越低(信息量小)？或者价值越高？
    // 题目说 "proportional to the frequency" (与频率成正比)
    // 所以我们就直接用百分比作为权重 (例如 E=12.02, Z=0.07)
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

    // 找出最小和最大权重，确定 Delta 的范围
    // 最小是 Z (0.07), 最大是 E (12.02)
    double minW = 0.07;
    double maxW = 12.02;

    // 实验：Delta 取 10 个中间值
    // 我们让 Delta 从 最小权重 略大一点开始，一直到 最大权重
    double step = (maxW - minW) / 9.0;

    System.out.println("Delta \t MaxScore \t SubstringLen \t Substring (First 10 chars)");
    for (int i = 0; i < 10; i++) {
      double delta = minW + i * step;
      Result res = solve(s1, s2, freqWeights, delta);

      // 打印结果，如果子串太长只显示前10个字符
      String displayStr = res.substring.length() > 10 ? res.substring.substring(0, 10) + "..." : res.substring;
      System.out.printf("%.2f \t %.2f \t\t %d \t\t %s\n", delta, res.maxScore, res.substring.length(), displayStr);
    }
  }

  // 生成随机大写字母字符串
  private static String generateRandomString(int length) {
    StringBuilder sb = new StringBuilder();
    Random rand = new Random();
    for (int i = 0; i < length; i++) {
      sb.append((char) ('A' + rand.nextInt(26)));
    }
    return sb.toString();
  }

  public static void main(String[] args) {
    // 运行作业要求的两个场景
    runScenario1();
    runScenario2();
  }
}