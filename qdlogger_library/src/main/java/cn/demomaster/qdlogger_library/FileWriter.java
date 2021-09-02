package cn.demomaster.qdlogger_library;

import static cn.demomaster.qdlogger_library.QDFileUtil.writeFileSdcardFile;

public class FileWriter implements LoggerWriter {
    @Override
    public void writeLog(String logFilePath, String logMsg) {
        writeFileSdcardFile(logFilePath, logMsg, true);
    }

}
