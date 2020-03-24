package com.shusaku.study.bitwise;

import org.junit.Test;

import java.util.EnumSet;
import java.util.Set;

/**
 * @program: ZoopeeperAndRedis
 * @description: 在 《Effective Java》　中　更推荐用EnumSet来代替位域: 位域表示法也允许利用位操作　有效地执行像union和intersection这样的集合操作
 *              但是位域有着int枚举常量所有的缺点　甚至更多　当位域以数字形式打印时　翻译位域比翻译简单的枚举常量要困难得多　甚至要遍历位域表示的所有元素也没有很容易的办法
 * @author: Shusaku
 * @create: 2020-03-24 10:22
 */
public class Text {

    private int flag;
    public static final int STYLE_BOLD          = 1 << 0;
    public static final int STYLE_ITALIC        = 1 << 1;
    public static final int STYLE_UNDERLINE     = 1 << 2;
    public static final int STYLE_STRIKETHROUGH = 1 << 3;

    public void applyStyles(int style) {
        flag |= style;
    }

    //======================================================================================================

    public enum Style {
        BLOD, ITALIC, UNDERLINE, STRIKETHROUGH
    }

    public void applyStyles(Set<Style> styles) {
        System.out.println(styles);
    }

    @Test
    public void test() {
        Text text = new Text();
        text.applyStyles(EnumSet.of(Style.BLOD, Style.ITALIC));
    }

}
