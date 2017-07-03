package com.tzg.tool.kit.numbers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

import com.tzg.tool.kit.date.DateUtil;

/**
 * 格式化数值
 */
public class Numbers {

    /**
     * 天数字符串的转换 大于90天用月，小于用天：55天，3个月+
     * 
     * @param dayDiff
     * @return
     */
    public static String getPeriodBetweenDates(Integer dayDiff) {
        if ((dayDiff / 30) >= 3) {
            StringBuilder contentBuilder = new StringBuilder();
            contentBuilder.append(String.format("%d个月", dayDiff / 30));
            contentBuilder.append(dayDiff % 30 == 0 ? "" : "+");
            return String.valueOf(contentBuilder);
        }
        return String.format("%d天", dayDiff);
    }

    /**
     * 获取标的利率：计划年利率+奖励利率+额外利率
     * 
     * @param numInterestRate 计划年利率
     * @param numRewardRate 奖励利率
     * @param numOtherRate 额外利率
     * @return
     */
    public static String getTotalInterest(BigDecimal numInterestRate, BigDecimal numRewardRate, BigDecimal numOtherRate) {
        BigDecimal totalInterest = BigDecimal.ZERO;
        if (null != numInterestRate) {
            totalInterest = totalInterest.add(numInterestRate);
        }
        if (null != numRewardRate) {
            totalInterest = totalInterest.add(numRewardRate);
        }
        if (null != numOtherRate) {
            totalInterest = totalInterest.add(numOtherRate);
        }
        return totalInterest.toString();
    }

    /**
     * 万元 格式化
     * 
     * @param val
     * @return
     */
    public static String formatWan(BigDecimal val) {
        String unit = "元";
        if (val == null) {
            return "0.00" + unit;
        }
        if (val.compareTo(new BigDecimal(10000)) >= 0) {
            unit = "万";
            val = val.divide(new BigDecimal(10000));
        }
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(6); // 设置数值的小数部分允许的最大位数。
        format.setMinimumFractionDigits(2); // 设置数值的小数部分允许的最小位数。
        format.setMaximumIntegerDigits(10); // 设置数值的整数部分允许的最大位数。
        format.setMinimumIntegerDigits(1); // 设置数值的整数部分允许的最小位数.
        return format.format(val.doubleValue()) + unit;
    }

    public static String formatWanForApp(BigDecimal val) {
        String unit = "元";
        if (val == null) {
            return "0.00" + unit;
        }
        // if(val.compareTo(new BigDecimal(1000))>=0){
        //
        // }
        unit = "万";
        val = val.divide(new BigDecimal(10000));
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(6); // 设置数值的小数部分允许的最大位数。
        format.setMinimumFractionDigits(2); // 设置数值的小数部分允许的最小位数。
        format.setMaximumIntegerDigits(10); // 设置数值的整数部分允许的最大位数。
        format.setMinimumIntegerDigits(1); // 设置数值的整数部分允许的最小位数.
        if (unit.equals("万")) {
            return format.format(val.setScale(2, RoundingMode.HALF_UP).doubleValue()) + unit;
        } else {
            return format.format(val.doubleValue()) + unit;
        }

    }

    /**
     * 百分比(0-100 内的整数)
     * 
     * @param dividend
     * @param divisor
     * @return
     */
    public static int getPercent(BigDecimal dividend, BigDecimal divisor) {
        int percent = 0;
        if (dividend == null) {
            dividend = BigDecimal.ZERO;
        }
        if (divisor == null) {
            divisor = BigDecimal.ONE;
        }
        BigDecimal result = dividend.multiply(new BigDecimal(100)).divide(divisor, RoundingMode.DOWN);
        percent = result.intValue();
        if (percent > 100) {
            percent = 100;
        }
        if (percent <= 0) {
            percent = 0;
        }
        return percent;
    }

    /**
     * 百分比(0-100 内的整数)
     * 
     * @param dividend
     * @param divisor
     * @return
     */
    public static String getPercent(int divisor,int dividend) { 
        NumberFormat nt = NumberFormat.getPercentInstance();
        nt.setMinimumFractionDigits(2);
        return nt.format((float)divisor/(float)dividend);
    }
    public static void main(String[] args) {
        System.out.println(100/300);
        System.out.println(getPercent(100,300));
    }
    /**
     * 货币，每三位加逗号
     *
     * @param val
     * @return
     */
    public static String toCurrency(BigDecimal val) {
        if (val == null) {
            return "0.00";
        }

        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2); // 设置数值的小数部分允许的最大位数。
        format.setMinimumFractionDigits(2); // 设置数值的小数部分允许的最小位数。
        format.setMaximumIntegerDigits(16); // 设置数值的整数部分允许的最大位数。
        format.setMinimumIntegerDigits(1); // 设置数值的整数部分允许的最小位数.
        return format.format(val.doubleValue());
    }

    public static int intValue(BigDecimal b) {
        if (b == null) {
            return 0;
        }
        return b.intValue();
    }

    /**
     * 相加
     *
     * @param d1
     * @param d2
     * @return
     */
    public static BigDecimal add(BigDecimal b1, BigDecimal b2) {
        if (b1 == null) {
            b1 = BigDecimal.ZERO;
        }
        if (b2 == null) {
            b2 = BigDecimal.ZERO;
        }
        return b1.add(b2);
    }

    /**
     * 相减
     * 
     * @param b1
     * @param b2
     * @return
     */
    public static BigDecimal subtract(BigDecimal b1, BigDecimal b2) {
        if (b1 == null) {
            b1 = BigDecimal.ZERO;
        }
        if (b2 == null) {
            b2 = BigDecimal.ZERO;
        }
        return b1.subtract(b2);
    }

    /**
     * 生产流水号
     * 
     * @param type
     * @param id
     * @return
     */
    public static String getSerialNumber(Integer itradeType, Integer id) {
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("%02d", itradeType));
        sb.append(DateUtil.getDate("yyyyMMdd"));
        sb.append(String.format("%0" + 10 + "d", id));
        return sb.toString();
    }

    /**
     * 格式化 固定长度字符
     * 
     * @param str
     * @param len
     * @return
     */
    public static String formatStrToNo(String str, Integer len) {
        if (str.length() > len) {
            str = str.substring(str.length() - len);
        } else {
            str = String.format("%11s", str);
            str = str.replaceAll("\\s", "0");
        }
        return str;
    }

    /**
     * 万元 格式化(整数)
     * 
     * @param val
     * @return
     */
    public static String formatWanToInt(BigDecimal val) {
        String unit = "元";
        if (val == null) {
            return "0" + unit;
        }
        if (val.compareTo(new BigDecimal(10000)) >= 0) {
            unit = "万";
            val = val.divide(new BigDecimal(10000));
        }
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(6); // 设置数值的小数部分允许的最大位数。
        format.setMinimumFractionDigits(2); // 设置数值的小数部分允许的最小位数。
        format.setMaximumIntegerDigits(10); // 设置数值的整数部分允许的最大位数。
        format.setMinimumIntegerDigits(1); // 设置数值的整数部分允许的最小位数.
        return format.format(val.doubleValue()).substring(0, format.format(val.doubleValue()).length() - 3) + unit;
    }
}
