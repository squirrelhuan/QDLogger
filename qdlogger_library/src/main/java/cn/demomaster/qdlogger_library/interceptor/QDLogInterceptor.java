package cn.demomaster.qdlogger_library.interceptor;

import cn.demomaster.qdlogger_library.model.LogBean;

public interface QDLogInterceptor {
    void onLog(LogBean msg);
}
