package cn.demomaster.qdlogger_library.constant;

import android.util.Log;

public enum QDLogLevel {
    ALL(-2),
    INFO(Log.INFO),
    DEBUG(Log.DEBUG),
    ERROR(Log.ERROR),
    VERBOSE(Log.VERBOSE),
    WARN(Log.WARN),
    PRINTLN(-3);

    private int value = 0;
    QDLogLevel(int value) {//必须是private的，否则编译错误
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
