package cn.demomaster.qdlogger_library.format;

import java.util.Comparator;

public class MapKeyComparator implements Comparator<Integer> {
    // 返回值为int类型。
    // 大于0表示顺序（递增），小于0表示逆序（递减）。
    // 0，相等。
    @Override
    public int compare(Integer o1, Integer o2) {
        return o1 > o2 ? 1 : -1;
    }
}
