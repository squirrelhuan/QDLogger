package cn.demomaster.qdlogger_library;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * 日志打印帮助类
 */
public class QDLogger {
    private static boolean enable = true;//是否启用
    public static String TAG = "QDLogger";
    private static Context mContext;
    //缓存阀值
    private static int bufferMaxSize = 10 * 1024;
    private static StringBuffer logBuffer;
    static LoggerWriter loggerWriter;
    static boolean fouceUseExternalStorage = false;//强制使用外置存储 针对Android Q以上的版本，但是需要 MANAGE_EXTERNAL_STORAGE
    static boolean fouceUseExternalStorage2 = true;//使用外置存储 Android Q以下

    /**
     * 强制使用外置存储 （针对Android Q以上的版本）
     *
     * @param fouceUseExternalStorage
     */
    public static void setFouceUseExternalStorage(boolean fouceUseExternalStorage) {
        QDLogger.fouceUseExternalStorage = fouceUseExternalStorage;
        setLogPath(logFilePath);
    }

    /**
     * 强制使用外置存储 （针对Android Q及其以下的版本）
     *
     * @param fouceUseExternalStorage2
     */
    public static void setFouceUseExternalStorage2(boolean fouceUseExternalStorage2) {
        QDLogger.fouceUseExternalStorage2 = fouceUseExternalStorage2;
        setLogPath(logFilePath);
    }

    static int writerMode = 1;//写入模式
    public static void setWriterMode(int writerMode) {
        QDLogger.writerMode = writerMode;
        if (writerMode == 0) {//mapbuffer写入
            loggerWriter = new MapBufferWriter();
        } else {//普通文件写入
            loggerWriter = new FileWriter();
        }
    }

    static LogFormat logFormat;
    static final String log_header_ = "\n[QDLogger Start]";

    public static void init(Context context, String logFilePath) {
        mContext = context.getApplicationContext();
        logFormat = new LogFormat(logDateFormat);
        setLogPath(logFilePath);
        if (logBuffer == null) {//設置打印頭
            logBuffer = new StringBuffer();
            logBuffer.append(log_header_);
        }
        setWriterMode(writerMode);
    }

    public static void setEnable(boolean enable) {
        QDLogger.enable = enable;
    }

    public static void setTAG(String tag) {
        TAG = tag;
    }

    /**********  i ***********/
    public static void i(Object obj) {
        log_i(TAG, obj);
    }

    public static void i(String tag, Object obj) {
        log_i(tag, obj);
    }

    private static void log_i(String tag, Object obj) {
        QDLogBean qdLogBean = new QDLogBean(QDLoggerType.INFO, tag, obj);
        doLog(qdLogBean);
    }

    /**********  d ***********/
    public static void d(String tag, Object obj) {
        log_d(null, tag, obj);
    }

    public static void d(Object obj) {
        log_d(null, TAG, obj);
    }

    public static void d(Class clazz, Object obj) {
        log_d(clazz, TAG, obj);
    }

    private static void log_d(Class clazz, String tag, Object obj) {
        QDLogBean qdLogBean = new QDLogBean(QDLoggerType.DEBUG, tag, obj);
        doLog(qdLogBean);
    }

    /**********  e ***********/
    public static void e(String tag, Object obj) {
        if (obj instanceof Throwable) {
            log_e(tag, obj, (Throwable) obj);
        } else {
            log_e(tag, obj, null);
        }
    }

    public static void e(String tag, Object obj, Throwable tr) {
        log_e(tag, obj, tr);
    }

    public static void e(Object obj) {
        if (obj instanceof Throwable) {
            log_e(TAG, obj, (Throwable) obj);
        } else {
            log_e(TAG, obj, null);
        }
    }

    private static void log_e(String tag, Object obj, Throwable tr) {
        QDLogBean qdLogBean = new QDLogBean(QDLoggerType.ERROR, tag, obj);
        qdLogBean.setThrowable(tr);
        doLog(qdLogBean);
    }

    /**********  v ***********/
    public static void v(String tag, Object obj) {
        log_v(tag, obj);
    }

    public static void v(Object obj) {
        log_v(TAG, obj);
    }

    private static void log_v(String tag, Object obj) {
        QDLogBean qdLogBean = new QDLogBean(QDLoggerType.VERBOSE, tag, obj);
        doLog(qdLogBean);
    }

    /**********  w ***********/
    public static void w(String tag, Object obj) {
        log_w(tag, obj);
    }

    public static void w(Object obj) {
        log_w(TAG, obj);
    }

    private static void log_w(String tag, Object obj) {
        QDLogBean qdLogBean = new QDLogBean(QDLoggerType.WARN, TAG, obj);
        doLog(qdLogBean);
    }

    public static void println(Object message) {
        printlndo(TAG, message);
    }

    public static void println(String tag, Object message) {
        printlndo(tag, message);
    }

    private static void printlndo(String tag, Object obj) {
        QDLogBean qdLogBean = new QDLogBean(QDLoggerType.PRINTLN, tag, obj);
        doLog(qdLogBean);
    }

    public void setHeaderFormat(String formatStr) {
        headerFormat = formatStr;
    }

    public static String headerFormat = "time-class-thread:";//正则表达式,处理头文件 tag日志标签 class所在类信息 time打印时间 thread打印进程信息 "tag-class-time-thread:";
    public static String headerFormat2 = "time:";//正则表达式,处理头文件 tag日志标签 class所在类信息 time打印时间 thread打印进程信息
    public static String headerFormat3 = "class:";//正则表达式,处理头文件 tag日志标签 class所在类信息 time打印时间 thread打印进程信息
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.CHINA);
    public static SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);// HH:mm:ss "yyyy年MM月dd日 HH:mm:ss:SSS"
    public static SimpleDateFormat logDateFormat = new SimpleDateFormat("HH:mm:ss:SSS", Locale.CHINA);

   /* static Locale mLocale = Locale.CHINA;
    public static void setLocale(Locale locale) {
        mLocale = locale;
        simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", mLocale);// HH:mm:ss
        simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd", mLocale);// HH:mm:ss
    }*/

    /**
     * 设置时区
     *
     * @param timeZone
     */
    public static void setTimeZone(TimeZone timeZone) {
        simpleDateFormat.setTimeZone(timeZone);
        simpleDateFormat2.setTimeZone(timeZone);
    }

    static QDLogBean lastQDLogBean;//上一次输出的文本
    static boolean sampleModel;
    static boolean usingGraphics = true;//启用图形化

    private static final char TOP_LEFT_CORNER = '┌';
    private static final char BOTTOM_LEFT_CORNER = '└';
    private static final char MIDDLE_CORNER = '├';
    private static final char HORIZONTAL_LINE = '│';
    private static final char HORIZONTAL_LINE2 = '║';
    private static final String DOUBLE_DIVIDER = "────────────────────────────────────────────────────────";
    private static final String SINGLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";
    private static final String SINGLE_DIVIDER2 = "╚═══════════════════════════════════════════════════════════════════════════════════════\n";

    private static void doLog(QDLogBean qdLogBean) {
        if (mContext == null) {
            Log.e(TAG, "[QDLogger]未初始化");
            return;
        }

        String logStr1 = logFormat.formatHeader(headerFormat3, qdLogBean) + qdLogBean.generateBody();
        if (mLogInterceptor != null) {
            mLogInterceptor.onLog(qdLogBean);
        }
        //正常打印日志
        switch (qdLogBean.getType()) {
            case VERBOSE:
                Log.v(qdLogBean.getTag(), logStr1);
                break;
            case INFO:
                Log.i(qdLogBean.getTag(), logStr1);
                break;
            case WARN:
                Log.w(qdLogBean.getTag(), logStr1);
                break;
            case DEBUG:
                Log.d(qdLogBean.getTag(), logStr1);
                break;
            case ERROR:
                if (qdLogBean.getThrowable() != null && BuildConfig.DEBUG) {
                    (qdLogBean.getThrowable()).printStackTrace();
                } else {
                    Log.e(qdLogBean.getTag(), logStr1);
                }
                break;
            case PRINTLN:
                System.out.println(logStr1);
                return;
        }

        String logStr2 = "";
        if (enable) {
            //如果配置了日志目录，则打印log到指定目录
            if (!TextUtils.isEmpty(LOG_DIR_ABSOLUTEPATH)) {
                // 检查该权限是否已经获取
                boolean useful = checkWritePermission(mContext);// (checkPermissionStatus(mContext, permissions)&&checkPermissionStatus(mContext, permissions2));
                if (useful) {
                    String logFileName = simpleDateFormat2.format(new Date()) + ".txt";
                    String logFilePath = LOG_DIR_ABSOLUTEPATH + logFileName;
                    String headStr = "";
                    if (lastQDLogBean == null
                            || TextUtils.isEmpty(qdLogBean.getClazzFileName())
                            || TextUtils.isEmpty(lastQDLogBean.getClazzFileName())
                            || !qdLogBean.getClazzFileName().equals(lastQDLogBean.getClazzFileName())
                            || qdLogBean.getLineNumber() != lastQDLogBean.getLineNumber()) {
                        headStr = logFormat.formatHeader(headerFormat, qdLogBean);
                        if (sampleModel && usingGraphics) {
                            headStr = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + "\n" + headStr;
                            sampleModel = false;
                        }
                    } else {
                        if (usingGraphics) {
                            headStr = MIDDLE_CORNER + "";
                            sampleModel = true;
                        }
                        headStr = headStr + logFormat.formatHeader(headerFormat2, qdLogBean);
                    }
                    lastQDLogBean = qdLogBean;
                    logStr2 = headStr + qdLogBean.generateBody();
                    logStr2 = logFormat.formatEnd(logStr2);

                    if (canWriteAble) {
                        String text = logStr2;
                        if (logBuffer.length() > 0) {//先写入缓存数据
                            text = logStr2 + "\n" + logBuffer.toString();
                            int sb_length = logBuffer.length();// 取得字符串的长度
                            logBuffer.delete(0, sb_length);    //删除字符串从0~sb_length-1处的内容 (这个方法就是用来清除StringBuffer中的内容的)
                            //loggerWriter.writeLog(logFilePath, logBuffer);
                        }
                        loggerWriter.writeLog(logFilePath, text);
                    } else {
                        //添加到缓存，但是缓存有大小限制，超过缓存大小，清除之前的缓存
                        int ds = logBuffer.length() - bufferMaxSize;
                        if (ds > 0) {
                            logBuffer.delete(0, ds);//删除字符串从0~ds-1处的内容
                        }
                        logStr2 = logFormat.formatEnd(logStr2);
                        logBuffer.append(logStr2);
                    }
                } else {
                    logNoPermissionError();
                }
            } else {
                Log.e(TAG, "未配置日志目录");
            }
        }
    }

    static boolean hasDealPermissionError = true;

    private static void logNoPermissionError() {
        if (hasDealPermissionError) {
            hasDealPermissionError = false;
            String err = "[log打印失败，请打开存储权限]";
            Log.e(TAG, err);
            //throw new IllegalArgumentException(err);
        }
    }

    static String permission1 = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    static String permission2 = Manifest.permission.MANAGE_EXTERNAL_STORAGE;

    private static boolean checkWritePermission(Context context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            if (fouceUseExternalStorage) {//强制使用外置存储 针对Android Q以上的版本，但是需要 MANAGE_EXTERNAL_STORAGE
                return (checkPermissionStatus(mContext, permission1) && checkPermissionStatus(mContext, permission2));
            } else {
                return true;
            }
        } else {
            if (fouceUseExternalStorage2) {//使用外置存储
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                    if (Environment.isExternalStorageLegacy()) {
                        return checkPermissionStatus(mContext, permission1);
                    } else {
                        Log.e(TAG, "请在Manifest添加： android:requestLegacyExternalStorage=\"true\"");
                        return false;
                    }
                } else {
                    return checkPermissionStatus(mContext, permission1);
                }
            } else {
                return true;
            }
        }
    }

    private static boolean checkPermissionStatus(Context context, String permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && permissions.equals(Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
            boolean p = Environment.isExternalStorageManager();
            return p;// 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
        } else {
            if (permissions.equals(Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
                return true;
            }
            int r = ContextCompat.checkSelfPermission(context.getApplicationContext(), permissions);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            return (r == PackageManager.PERMISSION_GRANTED);
        }
    }

    //日志文件存放目录
    public static String LOG_DIR_ABSOLUTEPATH;
    public static String logFilePath;

    public static void setLogPath(String dirPath) {
        logFilePath = dirPath;

        File file;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            if (fouceUseExternalStorage) {//强制使用外置存储 针对Android Q以上的版本，但是需要 MANAGE_EXTERNAL_STORAGE
                file = new File(Environment.getExternalStorageDirectory(), dirPath);
            } else {
                File file_documents = mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                file = new File(file_documents.getAbsoluteFile(), dirPath);
            }
        } else {
            if (fouceUseExternalStorage2) {//使用外置存储
                file = new File(Environment.getExternalStorageDirectory(), dirPath);
            } else {
                File file_documents = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    file_documents = mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                }
                file = new File(file_documents.getAbsoluteFile(), dirPath);
            }
        }
        System.out.println("日志保存路径:" + file.getAbsolutePath());
        LOG_DIR_ABSOLUTEPATH = file.getAbsolutePath();
        if (!TextUtils.isEmpty(LOG_DIR_ABSOLUTEPATH)) {
            if (!LOG_DIR_ABSOLUTEPATH.trim().endsWith(File.separator)) {
                LOG_DIR_ABSOLUTEPATH += File.separator;
            }
        }
    }

    public static boolean canWriteAble = true;//是否可以读写日志

    /**
     * 设置是否可以读写，一般在压缩文件时暂停写入。
     *
     * @param mcanWriteAble
     */
    public static void setCanWriteAble(boolean mcanWriteAble) {
        canWriteAble = mcanWriteAble;
        if (!mcanWriteAble) {
            System.err.println("暂停日志文件写入权限");
        } else {
            System.err.println("恢复日志写入文件权限");
        }
    }

    static int[] tagColors = new int[]{Color.WHITE, Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED, Color.RED, Color.DKGRAY};

    public static int getColor(int logType) {
        int color = tagColors[1];
        switch (logType) {
            case Log.VERBOSE:// 2;
                color = tagColors[0];
                break;
            case Log.DEBUG:// 3;
                color = tagColors[6];
                break;
            case Log.INFO:// 4;
            case -2:// 6;
                color = tagColors[2];
                break;
            case Log.WARN:// 5;
                color = tagColors[3];
                break;
            case Log.ERROR:// 6;
                color = tagColors[4];
                break;
        }
        return color;
    }

    static QDLogInterceptor mLogInterceptor;

    /**
     * 日志拦截器
     *
     * @param logInterceptor
     */
    public static void setInterceptor(QDLogInterceptor logInterceptor) {
        mLogInterceptor = logInterceptor;
    }

    public static void formatArray(String tag, Object data) {
        if (data != null) {
            String tag1 = String.format("<%s>\n\r", tag);
            String tag2 = String.format("</%s>\n\r", tag);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(tag1);
            if (data instanceof Map) {
                Map<Object, Object> map = (Map) data;
                for (Map.Entry entry : map.entrySet()) {
                    stringBuilder.append(entry.getKey())
                            .append("=")
                            .append(entry.getValue())
                            .append("\n\r");
                }
                stringBuilder.append(tag2);
                QDLogger.i(stringBuilder.toString());
            } else if (data instanceof Array) {
              /*  Array map = (Array) data;
                for (Object entry : map) {
                    logStr = logStr + entry+"\n\r" ;
                }
                logStr = logStr + tag2;
                QDLogger.i(logStr);*/
            }
        }
    }

    public static boolean isDebug() {
        if (mContext == null) {
            return true;
        }
        return isDebug(mContext);
    }

    /**
     * 判断当前应用是否是debug状态
     */
    public static boolean isDebug(Context context) {
        boolean isDebug = context.getApplicationInfo() != null &&
                (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        return isDebug;
    }
}
