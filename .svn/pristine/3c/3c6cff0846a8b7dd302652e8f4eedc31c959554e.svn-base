package com.tzg.tool.kit.test.date;

import static org.junit.Assert.*;
import static com.tzg.tool.kit.date.DateUtil.compareMonthCeil;
import static com.tzg.tool.kit.date.DateUtil.compareQuarterCeil;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;


public class DateUtilTest {
    @Test
    public void testCompareMonthCeil() throws Exception {
        //计算两个日志之间的月数差，采取进位取整(1个月1天为2个月)
        Assert.assertEquals(1, compareMonthCeil(new Date("2015/04/05"), new Date("2015/05/01")));
        Assert.assertEquals(1, compareMonthCeil(new Date("2015/04/05"), new Date("2015/05/05")));
        Assert.assertEquals(2, compareMonthCeil(new Date("2015/04/05"), new Date("2015/05/06")));
    }

    @Test
    public void testCompareQuarterCeil() throws Exception {
        //计算两个日志之间的月数差，采取进位取整(1个月1天为2个月)
        Assert.assertEquals(1,compareQuarterCeil(new Date("2015/04/01"), new Date("2015/05/01")));
        Assert.assertEquals(1,compareQuarterCeil(new Date("2015/04/01"), new Date("2015/07/01")));
        Assert.assertEquals(2,compareQuarterCeil(new Date("2015/04/01"), new Date("2015/07/02")));
    }

}
