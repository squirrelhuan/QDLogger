package cn.demomaster.qdlogger_library;

public interface LoggerWriter {
    void writeLog(String logFilePath, byte[] bytes);
}
