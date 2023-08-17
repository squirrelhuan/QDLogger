package cn.demomaster.qdlogger_library.format;

import android.os.Build;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StringFormat {

    private static final char TOP_LEFT_CORNER = '┌';
    private static final char BOTTOM_LEFT_CORNER = '└';
    private static final char MIDDLE_CORNER = '├';
    private static final char HORIZONTAL_LINE = '│';
    private static final char HORIZONTAL_LINE2 = '║';
    private static final String DOUBLE_DIVIDER = "─";
    private static final String END = "\n";
    private static final String HORIZONTAL_LINE_END = "│\n";

    public static String format(List<String> stringList) {
        StringBuilder stringBuilder = new StringBuilder();
        int maxLen = 0;
        for (String str : stringList) {
            maxLen = Math.max(maxLen, strLength(str,"UTF-8"));//Math.max(str.getBytes("GBK").length, maxLength);
        }
        maxLen+=1;
        System.out.println("maxLen=" + maxLen);
        /*for (int i=0;i<stringList.size();i++) {
            String str = "│"+stringList.get(i);
            int len = strLength(str,"UTF-8");
            int len1 = maxLen - len;
            int count_tr = (int) (Math.ceil(len1 / 4d)+((len1 / 4d<=Math.ceil(len1 / 4d))?1:0)); ;//(int) Math.ceil(len1 / 4d)+(maxLen%4d==0?0:1);
            System.out.println(len + ",缺失位数：" + len1 + ",补偿位数：" + count_tr);
            //System.out.println("len=" + len + ",count_tr=" + count_tr);
            for (int j = 0; j < count_tr; j++) {
                str += "\t";
            }
            stringList.set(i,str);
        }*/
        int count_tr = (int) ((Math.floor(maxLen/4d)+(maxLen % 4d== 0 ? 0 : 1))*4);
        String line1 ="";
        for (int j = 0; j < count_tr; j++) {
            line1 += "─";
        }
        String topLine = "┌"+line1+"┐\n";
        stringBuilder.append(topLine);
        for (String str : stringList) {
            String ftr = "│"+str + "\n";// String.format("│%-" + maxLength + "s│\t\t\t\t\n", str);
            stringBuilder.append(ftr);
        }
        String line2 = "└" +line1+ "┘\n";//String.format("└%-10" + "s┘\t\t\t\t\n", DOUBLE_DIVIDER);
        stringBuilder.append(line2);

        return stringBuilder.toString();
    }

    //左对齐，半角转全角（原串、希望域宽、填充字符）
    public static int getStringLen(String str) {
        /*汉字：[0x4e00,0x9fa5](或十进制[19968,40869])
        数字：[0x30,0x39](或十进制[48, 57])
        小写字母：[0x61,0x7a](或十进制[97, 122])
        大写字母：[0x41,0x5a](或十进制[65, 90])*/
        char[] array = str.toCharArray();
        int n = 0;
        for (char c : array) {//半角转全角
            boolean isChinese = isChinese(c);
            // System.out.println(c + " --> " + (isChinese ? "是" : "否"));
            if (isChinese) {
                n = n + 2;
            } else {
                n = n + 1;
            }
        }
        return n;
    }

    //左对齐，半角转全角（原串、希望域宽、填充字符）
    public static String leftAlign(String str, int len, char c) {
        char[] array = str.toCharArray();
        for (int i = 0; i < array.length; i++) {//半角转全角
            if (array[i] == ' ') {
                array[i] = '\u3000';
            } else if (array[i] < '\177') {
                array[i] = (char) (array[i] + 65248);
            }
        }
        int sub = len - str.length();
        if (sub <= 0) {
            return new String(array);//大于等于len返回
        }
        char[] temp = new char[len];
        System.arraycopy(array, 0, temp, 0, str.length());
        for (int j = str.length(); j < len; j++) {//左对齐右填充
            if (c == ' ') {
                temp[j] = '\u3000';
            } else if (c < '\177') {
                temp[j] = (char) (c + 65248);
            }
        }
        return new String(temp);
    }

    //右对齐,半角转全角
    public static String rightAlign(String str, int len, char c) {
        char[] array = str.toCharArray();
        for (int i = 0; i < array.length; i++) {
            if (array[i] == ' ') {
                array[i] = '\u3000';
            } else if (array[i] < '\177') {
                array[i] = (char) (array[i] + 65248);
            }
        }
        int sub = len - str.length();
        if (sub <= 0) {
            return new String(array);
        }
        char[] temp = new char[len];
        System.arraycopy(array, 0, temp, sub, str.length());
        for (int j = 0; j < sub; j++) {//右对齐左填充
            if (c == ' ') {
                temp[j] = '\u3000';
            } else if (c < '\177') {
                temp[j] = (char) (c + 65248);
            }
        }
        return new String(temp);
    }

    //全角转半角，输出默认是半角
    public static String quanToban(String str) {
        char[] array = str.toCharArray();
        for (int i = 0; i < array.length; i++) {
            if (array[i] == '\u3000') {
                array[i] = ' ';
            } else if (array[i] > '\uFF00' && array[i] < '\uFF5F') {
                array[i] = (char) (array[i] - 65248);
            }
        }
        return new String(array);
    }


    // 根据Unicode编码完美的判断中文汉字和符号
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    // 完整的判断中文汉字和符号
    public static boolean isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (char c : ch) {
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    // 只能判断部分CJK字符（CJK统一汉字）
    public static boolean isChineseByREG(String str) {
        if (str == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("[\\u4E00-\\u9FBF]+");
        return pattern.matcher(str.trim()).find();
    }

    // 只能判断部分CJK字符（CJK统一汉字）
    public static boolean isChineseByName(String str) {
        if (str == null) {
            return false;
        }
        // 大小写不同：\\p 表示包含，\\P 表示不包含
        // \\p{Cn} 的意思为 Unicode 中未被定义字符的编码，\\P{Cn} 就表示 Unicode中已经被定义字符的编码
        String reg = "\\p{InCJK Unified Ideographs}&&\\P{Cn}";
        Pattern pattern = Pattern.compile(reg);
        return pattern.matcher(str.trim()).find();
    }

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    /**
     * 将content按照正则匹配，返回可以匹配的字符串列表
     *
     * @param reg
     * @param content
     * @return
     */
    public static List<String> extractMessage(String reg, String content) {
        Pattern compile = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        Matcher matcher = compile.matcher(content);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list;
    }

    /**
     * 将str重复count次，返回结果
     *
     * @param str
     * @param count
     * @return
     */
    public static String getRepeatChar(String str, int count) {
        StringBuilder res = new StringBuilder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            IntStream.range(0, count).forEach(i -> res.append(str));
        }
        return res.toString();
    }

    /**
     * 将字符串填充到指定长度并居中对齐
     *
     * @param str
     * @param len
     * @return
     */
    public static String getPadString(String str, Integer len) {
        StringBuilder res = new StringBuilder();
        str = str.trim();
        if (str.length() < len) {
            int diff = len - str.length();
            int fixLen = diff / 2;
            String fix = getRepeatChar(" ", fixLen);
            res.append(fix).append(str).append(fix);
            if (res.length() > len) {
                return res.substring(0, len);
            } else {
                res.append(getRepeatChar(" ", len - res.length()));
                return res.toString();
            }
        }
        return str.substring(0, len);
    }

    /**
     * 此方法主要为表格的单元格数据按照指定长度填充并居中对齐并带上分割符号
     *
     * @param str    原始字符串
     * @param len    输出字符串的总长度
     * @param symbol 分割符号
     * @param index  传入的cell在list的索引，如果为第一个则需要在前面增加分割符号
     * @return
     */
    public static String getPadString(String str, Integer len, String symbol, int index) {
        String origin = str + "  ";
        if (index == 0) {
            String tmp = getPadString(origin, len - 2);
            return symbol + tmp + symbol;
        } else {

            String tmp = getPadString(origin, len - 1);
            return tmp + symbol;
        }
    }

    /**
     * 得到一个字符串中单字节出现的次数
     *
     * @param cell
     * @return
     */
    public static Integer getENCharCount(String cell) {
        if (cell == null) {
            return 0;
        }
        String reg = "[^\t\\x00-\\xff]";
//        String reg = "|[^\t\\x00-\\xff]";
        return cell.replaceAll(reg, "").length();
    }

    /**
     * 得到制表符长度，每个\t显示四个长度
     *
     * @param cell
     * @return
     */
    public static Integer getTableCount(String cell) {
        if (cell == null) {
            return 0;
        }
        String reg = "\t";
//        String reg = "|[^\t\\x00-\\xff]";
        return cell.length() - cell.replaceAll(reg, "").length();
    }

    /**
     * 得到一个字符串中双字节出现的次数
     *
     * @param cell
     * @return
     */
    public static Integer getZHCharCount(String cell) {
        if (cell == null) {
            return 0;
        }
        return cell.length() - getENCharCount(cell);
    }

    public static void main(String[] args) {
        String test = "ab\t哈哈嘻嘻";
        String reg = "[^\t\\x00-\\xff]";
        System.out.println(test.replaceAll(reg, "").length());
        test.replaceAll("\t|[^\\x00-\\xff]", "");
        System.out.println(test.length());
        System.out.println(getZHCharCount(test));
        System.out.println(getENCharCount(test));
    }

    public static int getStringLen2(String str) {
        int startLen = str.length();
        try {
            String ss1 = bSubstring(str,startLen);
            while (!ss1.equals(str)){
                startLen++;
                ss1 = bSubstring(str,startLen);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return startLen;
    }


    public static String bSubstring(String s, int length) throws Exception {
        byte[] bytes = s.getBytes("Unicode");
        int n = 0; // 表示当前的字节数
        int i = 2; // 要截取的字节数，从第3个字节开始
        for (; i < bytes.length && n < length; i++) {
// 奇数位置，如3、5、7等，为UCS2编码中两个字节的第二个字节
            if (i % 2 == 1) {
                n++; // 在UCS2第二个字节时n加1
            } else {
// 当UCS2编码的第一个字节不等于0时，该UCS2字符为汉字，一个汉字算两个字节
                if (bytes[i] != 0) {
                    n++;
                }
            }
        }
// 如果i为奇数时，处理成偶数
        if (i % 2 == 1) {
// 该UCS2字符是汉字时，去掉这个截一半的汉字
            if (bytes[i - 1] != 0) {
                i = i - 1;
// 该UCS2字符是字母或数字，则保留该字符
            } else {
                i = i + 1;

            }
        }
        return new String(bytes, 0, i, "Unicode");
    }
    public static int strLength(String str, String charset) {
        int len = 0;
        int j = 0;
        byte[] bytes = str.getBytes(Charset.forName(charset));
        while (bytes.length > 0) {
            short tmpst = (short) (bytes[j] & 0xF0);
            if (tmpst >= 0xB0) {
                if (tmpst < 0xC0) {
                    j += 2;
                    len += 2;
                } else if ((tmpst == 0xC0) || (tmpst == 0xD0)) {
                    j += 2;
                    len += 2;
                } else if (tmpst == 0xE0) {
                    j += 3;
                    len += 2;
                } else if (tmpst == 0xF0) {
                    short tmpst0 = (short) (((short) bytes[j]) & 0x0F);
                    if (tmpst0 == 0) {
                        j += 4;
                        len += 2;
                    } else if ((tmpst0 > 0) && (tmpst0 < 12)) {
                        j += 5;
                        len += 2;
                    } else if (tmpst0 > 11) {
                        j += 6;
                        len += 2;
                    }
                }
            } else {
                j += 1;
                len += 1;
            }
            if (j > bytes.length - 1) {
                break;
            }
        }
        return len;
    }

}
