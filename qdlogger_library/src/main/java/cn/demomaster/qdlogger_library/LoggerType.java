package cn.demomaster.qdlogger_library;

import android.util.Log;

public enum LoggerType {
    ALL(-2),
    INFO(Log.INFO),
    DEBUG(Log.DEBUG),
    ERROR(Log.ERROR),
    VERBOSE(Log.VERBOSE),
    WARN(Log.WARN),
    PRINTLN(-3);

    private int value = 0;

    LoggerType(int value) {//必须是private的，否则编译错误
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
