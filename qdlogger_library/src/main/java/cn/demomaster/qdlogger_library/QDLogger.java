package cn.demomaster.qdlogger_library;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import java.io.File;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import cn.demomaster.qdlogger_library.config.Config;
import cn.demomaster.qdlogger_library.config.ConfigBuilder;
import cn.demomaster.qdlogger_library.constant.QDLogLevel;
import cn.demomaster.qdlogger_library.format.LogFormat;
import cn.demomaster.qdlogger_library.format.StringFormat;
import cn.demomaster.qdlogger_library.interceptor.QDLogInterceptor;
import cn.demomaster.qdlogger_library.model.LogBean;
import cn.demomaster.qdlogger_library.model.QDLogHeader;
import cn.demomaster.qdlogger_library.util.QDFileUtil;
import cn.demomaster.qdlogger_library.writer.FileWriter;
import cn.demomaster.qdlogger_library.writer.LoggerWriter;
import cn.demomaster.qdlogger_library.writer.MapBufferWriter;

/**
 * 日志打印帮助类
 */
public class QDLogger {
    public static String TAG = QDLogger.class.getSimpleName();//默认标签
    private static Context mContext;
    private static StringBuffer logStringBuffer;
    static LoggerWriter loggerWriter;
    static Config mConfig;
    static LogFormat logFormat;
    static QDLogHeader qdLogHeader;

    public static void init(Context context, Config config) {
        mContext = context.getApplicationContext();
        logFormat = new LogFormat(logDateFormat);
        mConfig = config;
        if (mConfig == null) {
            ConfigBuilder builder = new ConfigBuilder(context);
            mConfig = builder.build();
        }
        mConfig.getLogSavePath(); //setLogPath(mConfig.logFileRelativePath);
        String log_header_ = "# QDLogger Start " +
                "\n# 版本名称："+ QDFileUtil.getVersionName(mContext)
                +"\n# 版本号："+QDFileUtil.getVersionCode(mContext)+"\n";
        List<String> stringList = new ArrayList<>();
        stringList.add("QDLogger V1.0.1");
        stringList.add("日期："+simpleDateFormat.format(new Date()));
        stringList.add("包名："+ mContext.getPackageName());
        stringList.add("版本名称："+ QDFileUtil.getVersionName(mContext));
        stringList.add("版本号："+QDFileUtil.getVersionCode(mContext));
        log_header_ = StringFormat.format(stringList);
        //log_header_ = qdLogHeader.toString()+"\n";
        if (logStringBuffer == null) {//設置打印頭
            logStringBuffer = new StringBuffer();
        }
        logStringBuffer.append(log_header_);
        if (mConfig.writerMode == 0) {//mapbuffer写入
            loggerWriter = new MapBufferWriter();
        } else {//普通文件写入
            loggerWriter = new FileWriter();
        }
    }

    //#通过根日志记录器指定日志级别及输出源
//#日志输出的优先级：  debug < info < warn < error
    public static void i(Object obj) {
        doLog(QDLogLevel.INFO, mConfig.TAG, obj, null);
    }

    public static void i(String tag, Object obj) {
        doLog(QDLogLevel.INFO, tag, obj, null);
    }

    public static void d(String tag, Object obj) {
        doLog(QDLogLevel.DEBUG, tag, obj, null);
    }

    public static void d(Object obj) {
        doLog(QDLogLevel.DEBUG, mConfig.TAG, obj, null);
    }

    public static void e(String tag, Object obj) {
        doLog(QDLogLevel.ERROR, tag, obj, null);
    }

    public static void e(String tag, Object obj, Throwable tr) {
        doLog(QDLogLevel.ERROR, tag, obj, tr);
    }

    public static void e(Object obj) {
        doLog(QDLogLevel.ERROR, mConfig.TAG, obj, null);
    }

    public static void v(String tag, Object obj) {
        doLog(QDLogLevel.VERBOSE, tag, obj, null);
    }

    public static void v(Object obj) {
        doLog(QDLogLevel.VERBOSE, mConfig.TAG, obj, null);
    }

    public static void w(String tag, Object obj) {
        doLog(QDLogLevel.WARN, tag, obj, null);
    }

    public static void w(Object obj) {
        doLog(QDLogLevel.WARN, mConfig.TAG, obj, null);
    }

    public static void println(Object message) {
        doLog(QDLogLevel.PRINTLN, mConfig.TAG, message, null);
    }

    public static void println(String tag, Object message) {
        doLog(QDLogLevel.PRINTLN, tag, message, null);
    }

    public static void setEnable(boolean enable) {
        if (mConfig != null) {
            mConfig.enable = enable;
        }
    }

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

    static LogBean lastLogBean;//上一次输出的文本
    static boolean sampleModel;
    private static final char TOP_LEFT_CORNER = '┌';
    private static final char BOTTOM_LEFT_CORNER = '└';
    private static final char MIDDLE_CORNER = '├';
    private static final char HORIZONTAL_LINE = '│';
    private static final char HORIZONTAL_LINE2 = '║';
    private static final String DOUBLE_DIVIDER = "────────────────────────────────────────────────────────";
    private static final String SINGLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";
    private static final String SINGLE_DIVIDER2 = "╚═══════════════════════════════════════════════════════════════════════════════════════\n";

    private static void doLog(QDLogLevel qdLogLevel, String tag, Object obj, Throwable tr) {
        LogBean logBean = new LogBean(qdLogLevel, tag, obj);
        if (obj instanceof Throwable) {
            logBean.setThrowable((Throwable) obj);
        }
        if (tr != null) {
            logBean.setThrowable(tr);
        }
        doLog(logBean);
    }

    private static void doLog(LogBean logBean) {
        if (mContext == null) {
            Log.e(mConfig.TAG, "[QDLogger]未初始化");
            return;
        }

        String logStr1 = logFormat.formatHeader(headerFormat3, logBean) + logBean.generateBody();
        if (mLogInterceptor != null) {
            mLogInterceptor.onLog(logBean);
        }
        //正常打印日志
        switch (logBean.getLevel()) {
            case VERBOSE:
                Log.v(logBean.getTag(), logStr1);
                break;
            case INFO:
                Log.i(logBean.getTag(), logStr1);
                break;
            case WARN:
                Log.w(logBean.getTag(), logStr1);
                break;
            case DEBUG:
                Log.d(logBean.getTag(), logStr1);
                break;
            case ERROR:
                if (logBean.getThrowable() != null && BuildConfig.DEBUG) {
                    (logBean.getThrowable()).printStackTrace();
                } else {
                    Log.e(logBean.getTag(), logStr1);
                }
                break;
            case PRINTLN:
                System.out.println(logStr1);
                return;
        }
        write(logBean);
        //destory
        logBean.setStackTraceElements(null);
        logBean.setThrowable(null);
        logBean.setMessage(null);
        logBean.setClazzFileName(null);
        logBean.setClazzName1(null);
        logBean.setTag(null);
        logBean.setLevel(null);
    }

    private static void write(LogBean logBean) {
        boolean useful = checkWritePermission(mContext);
        if (!useful) {//检查该存储权限
            logNoPermissionError();
            return;
        }
        if (mConfig.enable) {//是否启用
            //如果配置了日志目录，则打印log到指定目录
            if (!TextUtils.isEmpty(mConfig.getLogSavePath())) {//指定了存储目录
                String logFilePath = getLogFileSavePath();
                //checkFilePath(logFilePath);
                //处理单行日志头
                String headStr = "";
                if (lastLogBean == null
                        || TextUtils.isEmpty(logBean.getClazzFileName())
                        || TextUtils.isEmpty(lastLogBean.getClazzFileName())
                        || !logBean.getClazzFileName().equals(lastLogBean.getClazzFileName())
                        || logBean.getLineNumber() != lastLogBean.getLineNumber()) {
                    headStr = logFormat.formatHeader(mConfig.lineHeaderFormat, logBean);
                    if (sampleModel && mConfig.usingGraphics) {
                        headStr = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + "\n" + headStr;
                        sampleModel = false;
                    }
                } else {
                    if (mConfig.usingGraphics) {//图形化输出处理
                        headStr = MIDDLE_CORNER + "";
                        sampleModel = true;
                    }
                    headStr = headStr + logFormat.formatHeader(headerFormat2, logBean);
                }
                lastLogBean = logBean;
                String outputString = headStr + logBean.generateBody();
                outputString = logFormat.formatEnd(outputString);

                logStringBuffer.append(outputString);
                if (mConfig.canWriteAble) {//是否允许应急写入
                    byte[] bytes = logStringBuffer.toString().getBytes();
                    logStringBuffer.delete(0, logStringBuffer.length());//清除StringBuffer中的内容
                    loggerWriter.writeLog(logFilePath, bytes);
                } else {//添加到缓存，但是缓存有大小限制，超过缓存大小，清除之前的缓存
                    int ds = logStringBuffer.length() - mConfig.bufferMaxSize;
                    if (ds > 0) {//删除字符串从0~ds处的内容
                        logStringBuffer.delete(0, ds);
                    }
                }
            } else {
                Log.e(mConfig.TAG, "未配置日志目录");
            }
        }
    }

    /*private static void checkFilePath(String logFilePath) {
        File file = new File(logFilePath);
        if (!file.exists()) {
            QDFileUtil.createFile(file);
            qdLogHeader = new QDLogHeader(mContext);
            Gson gson = new Gson();
            String str = gson.toJson(qdLogHeader);
            QDFileUtil.writeFileSdcardFile(file, str + "\n", true);
        } else {
            if (qdLogHeader == null) {
                qdLogHeader = new QDLogHeader(mContext);
                String h = QDFileUtil.readFileLine(file.getAbsolutePath(), 0);
                System.out.println("hea00=" + h);
                if (!TextUtils.isEmpty(h)) {
                    //h=h.substring(0,h.length()-1);
                    //System.out.println("hea0=" + h);
                    Gson gson = new Gson();
                    qdLogHeader = gson.fromJson(h, QDLogHeader.class);
                    System.out.println("hea1=" + qdLogHeader);
                }
            }
        }
    }*/

    private static String getLogFileSavePath() {
        String logFileName = simpleDateFormat2.format(new Date()) + mConfig.LOGFILE_SUFFIX;
        return mConfig.getLogSavePath() + logFileName;
    }

    static boolean hasDealPermissionError = true;

    private static void logNoPermissionError() {
        if (hasDealPermissionError) {
            hasDealPermissionError = false;
            String err = "[log打印失败，请打开存储权限]";
            Log.e(mConfig.TAG, err);
            //throw new IllegalArgumentException(err);
        }
    }

    static String permission1 = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    static String permission2 = Manifest.permission.MANAGE_EXTERNAL_STORAGE;

    private static boolean checkWritePermission(Context context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            if (mConfig.saveExternalStorageAfterAndroidQ) {//强制使用外置存储 针对Android Q以上的版本，但是需要 MANAGE_EXTERNAL_STORAGE
                return (checkPermissionStatus(mContext, permission1) && checkPermissionStatus(mContext, permission2));
            } else {
                return true;
            }
        } else {
            if (mConfig.saveExternalStorageBeforeAndroidQ) {//使用外置存储
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                    if (Environment.isExternalStorageLegacy()) {
                        return checkPermissionStatus(mContext, permission1);
                    } else {
                        Log.e(mConfig.TAG, "请在Manifest添加： android:requestLegacyExternalStorage=\"true\"");
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

    /**
     * 设置是否可以读写，一般在压缩文件时暂停写入。
     *
     * @param mcanWriteAble
     */
    public static void setCanWriteAble(boolean mcanWriteAble) {
        mConfig.canWriteAble = mcanWriteAble;
        System.err.println((!mcanWriteAble ? "暂停" : "恢复") + " 日志文件写入");
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
                    String v = (entry.getValue() + "").trim();
                    stringBuilder.append(entry.getKey())
                            .append("=")
                            .append(v);
                    if (!TextUtils.isEmpty(v) && (!(v.endsWith("\n\r") && !v.endsWith("\n")))) {
                        stringBuilder.append("\n\r");
                    }
                }
                stringBuilder.append(tag2);
                QDLogger.i(stringBuilder.toString());
            } else if (data instanceof Collection) {
                Collection map = (Collection) data;
                for (Object entry : map) {
                    stringBuilder.append(entry);
                    if (!TextUtils.isEmpty(stringBuilder.toString()) && (!(stringBuilder.toString().endsWith("\n\r") && !stringBuilder.toString().endsWith("\n")))) {
                        stringBuilder.append("\n\r");
                    }
                }
                stringBuilder.append(tag2);
                QDLogger.i(stringBuilder.toString());
            }
        }else {
            QDLogger.i(tag,"nul");
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

    public static Config getConfig() {
        return mConfig;
    }
}
