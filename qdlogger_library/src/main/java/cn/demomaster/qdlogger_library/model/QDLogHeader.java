package cn.demomaster.qdlogger_library.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import cn.demomaster.qdlogger_library.util.QDFileUtil;

public class QDLogHeader {

    /**
     * D-C:N-T:
     *     日志头 日期-时间-类名:行号-进程号：
     */
    long time;
    String packageName;
    String versionName;
    int versionCode;
    List<String> keys;
    public QDLogHeader(Context context) {
        this.time = System.currentTimeMillis();
        keys = new ArrayList<>();
        packageName = context.getPackageName();
        versionName = QDFileUtil.getVersionName(context);
        versionCode = QDFileUtil.getVersionCode(context);
    }

    /*@Override
    public String toString() {
        return "QDLogHeader{" +
                "time=" + time +
                ", clazzName='" + clazzName + '\'' +
                ", lineNum=" + lineNum +
                ", threadId=" + threadId +
                ", keys=" + Arrays.toString(keys) +
                '}';
    }*/
}
