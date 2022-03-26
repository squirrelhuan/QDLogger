package cn.demomaster.qdlogger_library.writer;

import static cn.demomaster.qdlogger_library.util.QDFileUtil.writeFileSdcardFile;

public class FileWriter implements LoggerWriter {
    @Override
    public void writeLog(String logFilePath, byte[] bytes) {
        writeFileSdcardFile(logFilePath, bytes, true);
    }

}
