package cn.demomaster.qdlogger_library;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 日志打印帮助类
 */
public class QDLogger {
    public static boolean enable = true;//是否启用
    public static String TAG = "QDLogger";
    private static Context mContext;

    private static int bufferMaxSize = 10 * 1024;
    private static StringBuffer logBuffer;
    static LoggerWriter loggerWriter;

    public static void init(Context context,String logFilePath) {
        mContext = context.getApplicationContext();
        setLogPath(logFilePath);
        if (logBuffer == null) {
            logBuffer = new StringBuffer();
            logBuffer.append("\n[QDLogger Start]");
        }
        if(loggerWriter==null) {
            loggerWriter = new MapBufferWriter();
            //loggerWriter = new FileWriter();
        }
    }

    public static void setEnable(boolean enable) {
        QDLogger.enable = enable;
    }

    public static void setTAG(String tag) {
        TAG = tag;
    }

    /**********  i ***********/
    public static void i(Object obj) {
        QDLogBean QDLogBean = new QDLogBean(QDLoggerType.INFO, TAG, obj);
        doLog(null, QDLogBean);
    }

    public static void i(String tag, Object obj) {
        QDLogBean QDLogBean = new QDLogBean(QDLoggerType.INFO, tag, obj);
        doLog(null, QDLogBean);
    }

    public static void i(Context context, Object obj) {
        QDLogBean QDLogBean = new QDLogBean(QDLoggerType.INFO, TAG, obj);
        doLog(context, QDLogBean);
    }

    public static void i(Context context, String tag, Object obj) {
        QDLogBean QDLogBean = new QDLogBean(QDLoggerType.INFO, tag, obj);
        doLog(context, QDLogBean);
    }

    /**********  d ***********/
    public static void d(String tag, Object obj) {
        QDLogBean QDLogBean = new QDLogBean(QDLoggerType.DEBUG, tag, obj);
        doLog(null, QDLogBean);
    }

    public static void d(Object obj) {
        QDLogBean QDLogBean = new QDLogBean(QDLoggerType.DEBUG, TAG, obj);
        doLog(null, QDLogBean);
    }

    public static void d(Context context, Object obj) {
        QDLogBean QDLogBean = new QDLogBean(QDLoggerType.DEBUG, TAG, obj);
        doLog(context, QDLogBean);
    }

    public static void d(Context context, String tag, Object obj) {
        QDLogBean QDLogBean = new QDLogBean(QDLoggerType.DEBUG, tag, obj);
        doLog(context, QDLogBean);
    }

    /**********  e ***********/
    public static void e(String tag, Object obj) {
        QDLogBean QDLogBean = new QDLogBean(QDLoggerType.ERROR, tag, obj);
        doLog(null, QDLogBean);
    }

    public static void e(String tag, Object obj, Throwable tr) {
        QDLogBean QDLogBean = new QDLogBean(QDLoggerType.ERROR, tag, obj);
        QDLogBean.setThrowable(tr);
        doLog(null, QDLogBean);
    }

    public static void e(Object obj) {
        QDLogBean QDLogBean = new QDLogBean(QDLoggerType.ERROR, TAG, obj);
        doLog(null, QDLogBean);
    }

    public static void e(Context context, Object obj) {
        QDLogBean QDLogBean = new QDLogBean(QDLoggerType.ERROR, TAG, obj);
        doLog(context, QDLogBean);
    }

    public static void e(Context context, String tag, Object obj) {
        QDLogBean QDLogBean = new QDLogBean(QDLoggerType.ERROR, tag, obj);
        doLog(context, QDLogBean);
    }

    public static void e(Context context, String tag, Object obj, Throwable tr) {
        QDLogBean QDLogBean = new QDLogBean(QDLoggerType.ERROR, tag, obj);
        doLog(context, QDLogBean);
    }

    /**********  v ***********/
    public static void v(String tag, Object obj) {
        QDLogBean QDLogBean = new QDLogBean(QDLoggerType.VERBOSE, tag, obj);
        doLog(null, QDLogBean);
    }

    public static void v(Object obj) {
        QDLogBean QDLogBean = new QDLogBean(QDLoggerType.VERBOSE, TAG, obj);
        doLog(null, QDLogBean);
    }

    public static void v(Context context, Object obj) {
        QDLogBean QDLogBean = new QDLogBean(QDLoggerType.VERBOSE, TAG, obj);
        doLog(context, QDLogBean);
    }

    public static void v(Context context, String tag, Object obj) {
        QDLogBean QDLogBean = new QDLogBean(QDLoggerType.VERBOSE, tag, obj);
        doLog(context, QDLogBean);
    }

    /**********  w ***********/
    public static void w(String tag, Object obj) {
        QDLogBean QDLogBean = new QDLogBean(QDLoggerType.WARN, tag, obj);
        doLog(null, QDLogBean);
    }

    public static void w(Object obj) {
        QDLogBean QDLogBean = new QDLogBean(QDLoggerType.WARN, TAG, obj);
        doLog(null, QDLogBean);
    }

    public static void w(Context context, Object obj) {
        QDLogBean QDLogBean = new QDLogBean(QDLoggerType.WARN, TAG, obj);
        doLog(context, QDLogBean);
    }

  /*  private static void Log(Logger logType, String tag, Object obj) {
        Log(null, logType, tag, obj);
    }*/

    public static void println(Object message) {
        printlndo("", message);
    }

    public static void println(String tag, Object message) {
        printlndo("", message);
    }

    private static void printlndo(String tag, Object obj) {
        QDLogBean QDLogBean = new QDLogBean(QDLoggerType.PRINTLN, tag, obj);
        String logStr = generateMessage(QDLogBean);
        System.out.println(logStr);
        if (mLogInterceptor != null) {
            mLogInterceptor.onLog(QDLogBean);
        }
    }

    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.CHINA);// HH:mm:ss
    public static SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);// HH:mm:ss
    public static SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss:SSS", Locale.CHINA);// HH:mm:ss
    public static SimpleDateFormat logDateFormat = new SimpleDateFormat("HH:mm:ss:SSS", Locale.CHINA);// HH:mm:ss

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

    static String permissions = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static void doLog(Context context, QDLogBean qdLogBean) {
        if (mContext == null) {
            try {
                throw new Exception("QDLogger 未初始化");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String logStr = null;
        if (qdLogBean.getThrowable() != null) {
            logStr = generateErrorMessage(qdLogBean);
        } else {
            logStr = generateMessage(qdLogBean);
        }
        if (mContext == null) {
            Log.e(TAG, TAG + "未初始化，" + logStr);
            return;
        }

        if (mLogInterceptor != null) {
            mLogInterceptor.onLog(qdLogBean);
        }

        //正常打印日志
        switch (qdLogBean.getType()) {
            case VERBOSE:
                Log.v(qdLogBean.getTag(), logStr);
                break;
            case INFO:
                Log.i(qdLogBean.getTag(), logStr);
                break;
            case WARN:
                Log.w(qdLogBean.getTag(), logStr);
                break;
            case DEBUG:
                Log.d(qdLogBean.getTag(), logStr);
                break;
            case ERROR:
                Log.e(qdLogBean.getTag(), logStr);
                break;
            case PRINTLN:
                printlndo("", logStr);
                return;
        }

        if (enable) {
            //如果配置了日志目录，则打印log到指定目录
            if (!TextUtils.isEmpty(LogFileDir)) {
                // 检查该权限是否已经获取
                boolean useful = checkPermissionStatus(mContext, permissions);
                if (useful) {
                    String logFileName = simpleDateFormat2.format(new Date()) + ".txt";
                    String logFilePath = LogFileDir + logFileName;
                    if (canWriteAble) {
                        if (logBuffer.length() > 0) {//先写入缓存数据
                            String cacheStr = new String(logBuffer);
                            int sb_length = logBuffer.length();// 取得字符串的长度
                            logBuffer.delete(0, sb_length);    //删除字符串从0~sb_length-1处的内容 (这个方法就是用来清除StringBuffer中的内容的)
                            loggerWriter.writeLog(logFilePath, cacheStr);
                        }
                        loggerWriter.writeLog(logFilePath, logStr);
                    } else {
                        //添加到缓存，但是缓存有大小限制，超过缓存大小，清除之前的缓存
                        int ds = logBuffer.length() - bufferMaxSize;
                        if (ds > 0) {
                            logBuffer.delete(0, ds);    //删除字符串从0~ds-1处的内容
                        }
                        logBuffer.append(logStr);
                    }
                } else {
                    String err = "log 打印失败，请打开存储权限:" + logStr;
                    Log.e(TAG, err);
                    //throw new IllegalArgumentException(err);
                }
            } else {
                Log.e(TAG, "未配置日志目录");
            }
        }
    }

    private static boolean checkPermissionStatus(Context context, String permissions) {
        int r = ContextCompat.checkSelfPermission(context.getApplicationContext(), permissions);
        // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
        return (r == PackageManager.PERMISSION_GRANTED);
    }

    //是否显示类的详细信息（类名行号）
    private static boolean showClassInfo = true;

    public static void setShowClassInfo(boolean show) {
        showClassInfo = show;
    }

    //是否显示Thread的详细信息（threadId）
    private static boolean showThreadInfo = true;

    public static void setShowThreadInfo(boolean show) {
        showThreadInfo = show;
    }

    //是否显示Tag的详细信息（Tag）
    private static boolean showTag = true;

    public static void setShowTag(boolean show) {
        showTag = show;
    }

    //是否显打印空字符串
    private static boolean checkNull = false;

    public static void setCheckNull(boolean show) {
        checkNull = show;
    }

    /**
     * 拼接日志内容
     *
     * @param
     * @return
     */
    private static String generateMessage(QDLogBean QDLogBean) {
        String str = String.format("\n%s", logDateFormat.format(new Date()));
        if (showTag) {
            str += String.format("-%s", QDLogBean.getThreadId());
        }
        if (showThreadInfo) {
            str += String.format("-[Thread:%s]", QDLogBean.getThreadId());
        }
        str += " " + QDLogBean.getMessage();
        if (showClassInfo) {
            if (QDLogBean.getStackTraceElements() != null)
                for (StackTraceElement stackTraceElement : QDLogBean.getStackTraceElements()) {
                    str += "\n|" + String.format("\tat %s:%s", stackTraceElement.getClassName(), stackTraceElement.getLineNumber());
                }
        }
        return str;
    }

    /**
     * 错误日志
     * @param qdLogBean
     * @return
     */
    private static String generateErrorMessage(QDLogBean qdLogBean) {
        if (BuildConfig.DEBUG) {
            ((Throwable) qdLogBean.getThrowable()).printStackTrace();
        }
        qdLogBean = formatLoger(qdLogBean);
        String str = String.format("\n%s", logDateFormat.format(new Date()));
        str += String.format("-%s", qdLogBean.getType());
        str += String.format("-[Thread:%s]", qdLogBean.getThreadId());
        str += String.format("\n%s", qdLogBean.getThrowable());
        if (qdLogBean.getStackTraceElements() != null)
            for (StackTraceElement stackTraceElement : qdLogBean.getStackTraceElements()) {
                str += "\n" + String.format("\tat %s:%s", stackTraceElement.getClassName(), stackTraceElement.getLineNumber());
            }
        return str;
    }
    /**
     *格式化日志
     */
    private static QDLogBean formatLoger(QDLogBean qdlogBean) {
        if (showClassInfo || (qdlogBean.getMessage() != null && qdlogBean.getMessage() instanceof Throwable)) {
            Thread thread = Thread.currentThread();
            if(thread!=null) {
                try {
                    StackTraceElement[] stackTraceElements = thread.getStackTrace();
                    qdlogBean.setStackTraceElements(stackTraceElements);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        if (showThreadInfo || (qdlogBean.getMessage() != null && qdlogBean.getMessage() instanceof Throwable)) {
            Thread thread = Thread.currentThread();
            if(thread!=null) {
                long threadId = thread.getId();
                qdlogBean.setThreadId(threadId);
            }
        }
        return qdlogBean;
    }

    //日志文件存放目录
    public static String LogFileDir;
    public static void setLogPath(String dirPath) {
        File file;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File file_documents = mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            file = new File(file_documents.getAbsoluteFile(),dirPath);
        }else {
            file = new File(Environment.getExternalStorageDirectory(),dirPath);
        }
        LogFileDir = file.getAbsolutePath();
        if(!TextUtils.isEmpty(LogFileDir)){
            if(!LogFileDir.trim().endsWith(File.separator)){
                LogFileDir+=File.separator;
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
                color = tagColors[2];
                break;
            case Log.WARN:// 5;
                color = tagColors[3];
                break;
            case Log.ERROR:// 6;
                color = tagColors[4];
                break;
            case -2:// 6;
                color = tagColors[2];
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


}
