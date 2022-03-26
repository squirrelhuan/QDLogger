package cn.demomaster.qdlogger_library.config;

import android.content.Context;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import java.io.File;

import cn.demomaster.qdlogger_library.QDLogger;

/**
 * 配置文件构建器
 */
public class ConfigBuilder {
    //String logSavePath = "";
    String saveExternalStoragePath = "";//外部路径
    String saveInternalSoragePath = "/log/";//保存在内部存储缓存目录中的位置
    String TAG = QDLogger.class.getSimpleName();
    String LOGFILE_SUFFIX = ".log";
    String lineHeaderFormat = "time class:";//"time class level tag:"正则表达式,处理头文件 level日志级别、 tag日志标签、 class所在类信息、 time打印时间 thread打印进程信息 "tag-class-time-thread:";

    String topLevelTag;
    Character logCatLogLevel;
    Character fileLogLevel;
    boolean showStackTraceInfo = true;
    boolean showFileTimeInfo = true;
    boolean showFilePidInfo = true;
    boolean showFileLogLevel = true;
    boolean showFileLogTag = true;
    boolean showFileStackTraceInfo = true;
    boolean saveExternalStorageAfterAndroidQ  = false;//强制使用外置存储 针对Android Q以上的版本，但是需要 MANAGE_EXTERNAL_STORAGE。 true 保存在指定文件夹，,false则保存在缓存目录
    boolean saveExternalStorageBeforeAndroidQ = true;//强制使用外置存储 Android Q以下的版本。true 保存在指定文件夹，false则保存在缓存目录
    /**
     * 删除过了几天无用日志条目
     */
    int deleteUnusedLogEntriesAfterDays = 7;
    Context context;

    public ConfigBuilder(Context context) {
        this.context = context.getApplicationContext();
    }

    public Config build() {
        return new Config(this);
    }

    /**
     * 设置日志保存路径 (内部存储)
     * @param saveInternalSoragePath 保存在缓存目录时传递文件夹相对路径
     * @return
     */
    public ConfigBuilder setSaveInternalSoragePath(String saveInternalSoragePath) {
        this.saveInternalSoragePath = saveInternalSoragePath;
        return this;
    }

    /**
     * 设置日志保存路径 (外部存储)
     * @param saveExternalStorageDirFile 需要传递绝对路径
     * @return
     */
    public ConfigBuilder setSaveExternalStoragePath(@NonNull File saveExternalStorageDirFile) {
        this.saveExternalStoragePath = saveExternalStorageDirFile.getAbsolutePath();
        return this;
    }

    /**
     * 强制使用外置存储 针对Android Q以上的版本，但是需要 MANAGE_EXTERNAL_STORAGE。
     * @param saveExternalStorageAfterAndroidQ true 保存在指定文件夹，,false则保存在缓存目录
     * @return
     */
    public ConfigBuilder setSaveExternalStorageAfterAndroidQ(boolean saveExternalStorageAfterAndroidQ) {
        this.saveExternalStorageAfterAndroidQ = saveExternalStorageAfterAndroidQ;
        return this;
    }

    /**
     * 强制使用外置存储 Android Q以下的版本。
     * @param saveExternalStorageBeforeAndroidQ true 保存在指定文件夹，false则保存在缓存目录
     * @return
     */
    public ConfigBuilder setSaveExternalStorageBeforeAndroidQ(boolean saveExternalStorageBeforeAndroidQ) {
        this.saveExternalStorageBeforeAndroidQ = saveExternalStorageBeforeAndroidQ;
        return this;
    }

    public ConfigBuilder logCatLogLevel(Character logCatLogLevel) {
        this.logCatLogLevel = logCatLogLevel;
        return this;
    }

    public ConfigBuilder fileLogLevel(Character fileLogLevel) {
        this.fileLogLevel = fileLogLevel;
        return this;
    }

    public ConfigBuilder logCatLogLevel(int logCatLogLevel) {
        this.logCatLogLevel = (char) logCatLogLevel;
        return this;
    }

    public ConfigBuilder fileLogLevel(int fileLogLevel) {
        this.fileLogLevel = (char) fileLogLevel;
        return this;
    }

    public ConfigBuilder topLevelTag(String tag) {
        this.topLevelTag = tag;
        return this;
    }

    public ConfigBuilder showStackTraceInfo(boolean show) {
        this.showStackTraceInfo = show;
        return this;
    }

    public ConfigBuilder showFileTimeInfo(boolean show) {
        this.showFileTimeInfo = show;
        return this;
    }

    public ConfigBuilder showFilePidInfo(boolean show) {
        this.showFilePidInfo = show;
        return this;
    }

    public ConfigBuilder showFileLogLevel(boolean show) {
        this.showFileLogLevel = show;
        return this;
    }

    public ConfigBuilder showFileLogTag(boolean show) {
        this.showFileLogTag = show;
        return this;
    }

    public ConfigBuilder showFileStackTraceInfo(boolean show) {
        this.showFileStackTraceInfo = show;
        return this;
    }

    /**
     * 删除过了几天无用日志条目
     */
    public ConfigBuilder deleteUnusedLogEntriesAfterDays(@IntRange(from = 1, to = Integer.MAX_VALUE) int days) {
        this.deleteUnusedLogEntriesAfterDays = days;
        return this;
    }

    public ConfigBuilder setLineHeaderFormat(String lineHeaderFormat) {
        this.lineHeaderFormat = lineHeaderFormat;
        return this;
    }
}
