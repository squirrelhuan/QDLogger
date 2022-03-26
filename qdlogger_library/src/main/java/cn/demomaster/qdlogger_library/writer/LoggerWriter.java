package cn.demomaster.qdlogger_library.writer;

public interface LoggerWriter {
    void writeLog(String logFilePath, byte[] bytes);
}
