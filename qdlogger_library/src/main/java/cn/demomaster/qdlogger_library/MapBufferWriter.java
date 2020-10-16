package cn.demomaster.qdlogger_library;

import android.text.TextUtils;

import java.io.File;

import static cn.demomaster.qdlogger_library.QDFileUtil.createFile;

public class MapBufferWriter implements LoggerWriter {


    //缓存触发扩容最大值
    private static long fileSize = 10 * 1024;
    private static MappedByteBufferHelper.MyMappedByteBuffer mapByteBuffer;

    public static void setFileSize(long size) {
        fileSize = size;
    }

    //写入日志数据
    @Override
    public void writeLog(String logFilePath, String logMsg) {
        //先判断文件是否存在，不存在则创建
        File file = new File(logFilePath);
        if (!file.exists()) {
            createFile(file);
            if (mapByteBuffer != null) {
                mapByteBuffer.close();
                mapByteBuffer = null;
            }
        }
        if (mapByteBuffer == null) {//虚拟内存映射
            mapByteBuffer = MappedByteBufferHelper.map_log(file, fileSize);
        }

        if (mapByteBuffer != null) {//确保映射成功，可以写入
            mapByteBuffer.put(TextUtils.isEmpty(logMsg) ? "NUL".getBytes() : logMsg.getBytes());
        }
    }

}
