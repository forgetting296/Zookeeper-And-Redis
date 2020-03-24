package com.shusaku.study.bitwise;

import com.shusaku.study.zk.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @program: ZoopeeperAndRedis
 * @description:
 *
 * 按位运算
 *  & : 与运算，两位都为1　结果才是1
 *  | : 或运算, 有一位为1　结果就为1
 *  ~ : 非运算, ~0 = 1, ~1 = 0
 *  ^ : 异或运算, 两位不相同　结果才为1
 *
 * 移位运算
 *  << : 左移　各二进制位全部左移N位　高位丢弃　低位补0
 *  >> : 右移　各二进制位全部右移N位　若值为正　则在高位插入0　若值为负　则在高位插入1  应该是为了保证符号吧　　最高位是符号位
 *  >>> : 无符号右移　各二进制位全部右移N位　无论正负　都在高位插入0
 *
 *  原码：　原码表示法在数字前面增加了一位符号位　即最高位为符号位　正数位该位为0　负数位该位为1　
 *  反码：　正数的反码是其本身　负数的反码在其原码的基础上　符号位不变　其余各位取反
 *  补码：　正数的补码是其本身　负数的补码在其原码的基础上　符号位不变　其余各位取反　最后　+1　
 * 注意：　在计算机系统中　数字一律是用补码表示、运算、存储
 *
 * @author: Shusaku
 * @create: 2020-03-23 16:37
 */
@Slf4j
public class BitwiseTest {

    @Test
    public void testAnd() {

        int a = 5;
        int b = 4;

        print("5&4", a, b, NumberUtil.decimal2Binary(a & b), (a & b));
    }

    @Test
    public void testOr() {

        int a = 5;
        int b = 4;

        print("4|5", a, b, NumberUtil.decimal2Binary(a | b), (a | b));
    }

    @Test
    public void testNot() {

        int a = 5;
        int b = 4;
        log.info("~5: {}", Integer.toBinaryString(~5));
        log.info("~5: {}", ~5);
        print("~5", a, b, NumberUtil.decimal2Binary(~ a), (~ a));
    }

    @Test
    public void testNotOr() {

        int a = 5;
        int b = 4;

        print("5 ^ 4", a, b, NumberUtil.decimal2Binary(5 ^ 4), (5 ^ 4));
    }

    @Test
    public void testLeftMove() {

        int a = 5;
        int b = 4;
        int c = -5;
        //就是　a * 2的幂　　不区分正数和负数　
        print("-5 << 4", c, b, Integer.toBinaryString(c << b), (c << b));
    }

    @Test
    public void testRightMove() {

        int a = 50;
        int b = 4;
        int c = -5;
        //　正数的话　　相当于 a / 2的幂　　负数的话由于在高位补得是1 结果不确定
        print("5 >> 4", a, b, Integer.toBinaryString(a >> b), (a >> b));
    }

    @Test
    public void testRightMoveNoChar() {

        int a = -50;
        int b = 4;
        int c = -5;
        //　无符号右移　　直接右移　　前边补零
        print("50 >>> 4", a, b, Integer.toBinaryString(a >>> b), (a >>> b));
    }

    /**
     * 使用位运算　不使用第三个变量　实现两个数字的互换
     */
    @Test
    public void numChangeNoOther() {
        if(isOdd(3)){
            log.info("奇数");
        } else {
            log.info("偶数");
        }
    }

    private int getAbsoluteValue(int a) {

        return (a^(a>>31))-(a>>31);
    }

    private boolean isOdd(int a) {
        return (a & 1) == 1;
    }

    private void print(String operation,int a, int b, Object binaryResult, int decimalResult) {

        log.info("num: {}, binary: {}", a, Integer.toBinaryString(a));
        log.info("num: {}, binary: {}", b, Integer.toBinaryString(b));
        log.info("operation: {}", operation);
        log.info("处理后binaryResult: {}", binaryResult);
        log.info("处理后decimalResult: {}", decimalResult);
    }

}
