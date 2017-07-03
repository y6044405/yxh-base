package com.tzg.tool.kit;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.RandomStringUtils;

import com.tzg.tool.kit.date.DateUtil;

/**
 * 封装各种生成唯一性ID算法的工具类.
 */
public class Identities {
    private static final int[]  prefix    = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    private static SecureRandom random    = new SecureRandom();
    private static final int    MIN_ALPHA = (int) 'A';
    private static final int    MAX_ALPHA = (int) 'Z';

    private Identities() {
    }

    /**
     * 封装JDK自带的UUID, 通过Random数字生成, 中间有-分割.
     */
    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.
     */
    public static String uuid2() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 使用SecureRandom随机生成Long.
     */
    public static long randomLong() {
        return random.nextLong();
    }

    /**
     * 基于Base62编码的SecureRandom随机生成bytes.
     */
    public static String randomBase62(int length) {
        byte[] randomBytes = new byte[length];
        random.nextBytes(randomBytes);
        return Encodes.encodeBase62(randomBytes);
    }

    /**
     * 生成指定位数的随机数字
     * 
     * @param digit
     *            位数
     * @return
     */
    public static String getRandomNum(int digit) {
        digit = checkDigit(digit);
        if (digit == 1)
            return String.valueOf((int) (Math.random() * 10));
        BigDecimal operand = new BigDecimal(10).pow(digit - 1);
        BigDecimal randomNum = new BigDecimal(Math.random());
        MathContext mc = new MathContext(digit, RoundingMode.DOWN);
        return randomNum.multiply(new BigDecimal(9)).multiply(operand).add(operand, mc).toString();

    }

    private static int checkDigit(int digit) {
        if (digit < 0)
            digit = Math.abs(digit);
        else if (digit == 0)
            digit = 1;
        return digit;
    }

    /**
     * 随机产生最大为18位的long型数据(long型数据的最大值是9223372036854775807,共有19位)
     * 
     * @param digit
     *            用户指定随机数据的位数
     */
    public static long randomLong(int digit) {
        if (digit >= 19 || digit <= 0)
            throw new IllegalArgumentException("digit should between 1 and 18(1<=digit<=18)");
        String s = RandomStringUtils.randomNumeric(digit - 1);
        return Long.parseLong(getPrefix() + s);
    }

    /**
     * 随机产生在指定位数之间的long型数据,位数包括两边的值,minDigit<=maxDigit
     * 
     * @param minDigit
     *            用户指定随机数据的最小位数 minDigit>=1
     * @param maxDigit
     *            用户指定随机数据的最大位数 maxDigit<=18
     */
    public static long randomLong(int minDigit, int maxDigit) {
        if (minDigit > maxDigit) {
            throw new IllegalArgumentException("minDigit > maxDigit");
        }
        if (minDigit <= 0 || maxDigit >= 19) {
            throw new IllegalArgumentException("minDigit <=0 || maxDigit>=19");
        }
        return randomLong(minDigit + getDigit(maxDigit - minDigit));
    }

    public static double random(double min, double max) {
        return min + random.nextDouble() * (max - min);
    }

    private static int getDigit(int max) {
        return RandomUtils.nextInt(max + 1);
    }

    /**
     * 保证第一位不是零
     * 
     * @return
     */
    private static String getPrefix() {
        return prefix[RandomUtils.nextInt(9)] + "";
    }

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = format.parse("2015-03-25 10:30:28.618");
        Date date2 = DateUtil.addHour(date, 1);
        date2 = DateUtil.addMinute(date2, 2);
        for (int i = 0; i < 200; i++) {
            System.out.println(format.format(new Date(randomLong(date.getTime(), date2.getTime()))) + " ");
        }
    }

    /**
     * 随机产生指定介于最小值和最大值之间的随机数
     * 
     * @param min
     *            最小值
     * @param max
     *            最大值
     * @return
     */
    public static int randomInt(int min, int max) {
        return (int) (min + Math.random() * (max - min + 1));
    }

    /**
     * 随机产生介于最小值和最大值之间的随机数
     * 
     * @param min
     *            最小值
     * @param max
     *            最大值
     * @return 返回值不包含最小值和最大值
     */
    public static long randomLong(long min, long max) {
        long l = min + (long) (Math.random() * (max - min));
        if (l == min || l == max) {
            return randomLong(min, max);
        }
        return l;
    }

    /**
    * Returns true with a probability of p.
    */
    public static boolean probability(double p) {
        return random.nextDouble() < p;
    }

    public static String randomString(int... segments) {
        StringBuffer tmp = new StringBuffer();
        for (int s : segments) {
            tmp.append(tmp.length() == 0 ? (char) random(MIN_ALPHA, MAX_ALPHA) : '-');
            for (int j = 0; j < s; j++)
                tmp.append(random(10));
        }
        return tmp.toString();
    }

    public static int random(int n) {
        return random.nextInt(n);
    }

    /**
     * Picks a random element from the given list.
     */
    public static <T> T selectRandom(List<T> list) {
        if (list == null || list.isEmpty())
            return null;
        if (list.size() == 1)
            return list.get(0);
        return list.get(random(list.size()));
    }

    /**
     * Selects n elements randomly from the given list.
     */
    public static <T> List<T> selectRandom(List<T> list, int n) {
        if (list == null || list.isEmpty())
            return Collections.emptyList();
        Set<Integer> indices = new HashSet<Integer>();
        List<T> selected = new ArrayList<T>();
        int m = list.size();
        if (n >= m) {
            selected.addAll(list);
        } else {
            while (indices.size() < n) {
                indices.add(random(m));
            }
            for (Integer index : indices) {
                selected.add(list.get(index));
            }
        }
        return selected;
    }
}
