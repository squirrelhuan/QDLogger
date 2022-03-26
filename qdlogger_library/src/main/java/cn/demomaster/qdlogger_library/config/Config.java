package cn.demomaster.qdlogger_library.config;

import android.os.Build;
import android.text.TextUtils;

import java.io.File;

import cn.demomaster.qdlogger_library.util.QDFileUtil;

public class Config {
    String diskCacheDir;
    public Config(ConfigBuilder builder) {
       //this.logSavePath = builder.getLogSavePath();
       this.saveExternalStorageAfterAndroidQ = builder.saveExternalStorageAfterAndroidQ;
        this.saveExternalStorageBeforeAndroidQ = builder.saveExternalStorageBeforeAndroidQ;
        this.saveExternalStoragePath = builder.saveExternalStoragePath;
        this.saveInternalSoragePath = builder.saveInternalSoragePath;
        this.lineHeaderFormat = builder.lineHeaderFormat;
        this.TAG = builder.TAG;
        this.diskCacheDir = QDFileUtil.getDiskCacheDir(builder.context);
    }
    public String TAG;//默认标签
    public boolean enable = true;//是否启用
    public int bufferMaxSize = 10 * 1024;//缓存阀值 当缓存大于此值清楚缓存

    String log_header_ = "\n[QDLogger Start]";
    public String LOGFILE_SUFFIX = ".log";
    public String lineHeaderFormat;//正则表达式,处理头文件 tag日志标签 class所在类信息 time打印时间 thread打印进程信息 "tag-class-time-thread:";

    /**
     * 是否可以写日志
     * 用在 日志文件正在其他操作时，暂停写入。操作完成恢复写入
     */
    public boolean canWriteAble = true;

    public boolean usingGraphics = true;//是否启用图形化 格式化输出
    public int writerMode = 1;//写入模式 1 普通文件写入，0 mapbuffer写入
    String logSavePath;//存放日志文件的目录全路径
    String saveExternalStoragePath;//存储在外部存储时的路径
    String saveInternalSoragePath;//保存在内部存储缓存目录中的位置
    public boolean saveExternalStorageAfterAndroidQ;//强制使用外置存储 针对Android Q以上的版本，但是需要 MANAGE_EXTERNAL_STORAGE。 true 保存在指定文件夹，,false则保存在缓存目录
    public boolean saveExternalStorageBeforeAndroidQ;//强制使用外置存储 Android Q以下的版本。true 保存在指定文件夹，false则保存在缓存目录

    public String getLogSavePath() {
        if(TextUtils.isEmpty(logSavePath)) {
            File file;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                if (saveExternalStorageAfterAndroidQ) {//强制使用外置存储 针对Android Q以上的版本，但是需要 MANAGE_EXTERNAL_STORAGE
                    file = new File(saveExternalStoragePath);
                } else {
                    file = new File(diskCacheDir, saveInternalSoragePath);
                }
            } else {
                if (saveExternalStorageBeforeAndroidQ) {//使用外置存储
                    file = new File(saveExternalStoragePath);
                } else {
                    file = new File(diskCacheDir, saveInternalSoragePath);
                }
            }
            System.out.println("sdk_int:" + Build.VERSION.SDK_INT + ",日志存储路径:" + file.getAbsolutePath());
            String DIR_PATH = file.getAbsolutePath();
            if (!TextUtils.isEmpty(DIR_PATH)) {
                if (!DIR_PATH.trim().endsWith(File.separator)) {
                    DIR_PATH += File.separator;
                }
            }
            logSavePath = DIR_PATH;
        }
        return logSavePath;
    }
}
