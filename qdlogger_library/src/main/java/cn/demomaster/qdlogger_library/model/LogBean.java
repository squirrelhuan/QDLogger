package cn.demomaster.qdlogger_library.model;

import android.text.TextUtils;
import android.util.Log;

import java.io.Serializable;

import cn.demomaster.qdlogger_library.constant.QDLogLevel;

public class LogBean implements Serializable {
    private QDLogLevel level;//日志级别
    private Object message;//内容
    private String tag;//tag标签
    private StackTraceElement[] stackTraceElements;//进程堆栈
    private long threadId = -1;//进程id
    private int lineNumber;//打印所在行
    private Throwable throwable;//异常
    //private boolean showThreadInfo = true;//是否显示进程信息
    //private boolean showTag = true;//是否显示tag标签
    private String clazzName;
    private String clazzFileName;

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getClazzName() {
        return clazzName;
    }

    public void setClazzName1(String clazzName) {
        this.clazzName = clazzName;
    }

    public String getClazzFileName() {
        return clazzFileName;
    }

    public void setClazzFileName(String clazzFileName) {
        this.clazzFileName = clazzFileName;
    }
    
    public LogBean(QDLogLevel level, String tag, Object message) {
        this.level = level;
        this.message = message;
        this.tag = tag;
        if (message instanceof Throwable) {
            this.throwable = (Throwable) message;
        }
        initStackTrace();
    }

    private void initStackTrace() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        if (stackTraceElements.length >= 7) {
            setLineNumber(stackTraceElements[6].getLineNumber());
            setClazzFileName(stackTraceElements[6].getFileName());
            //qdLogBean.setClazzFile(getClazzSimpleName(stackTraceElements[5].getClassName()));
        }
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
        if (throwable == message) {
            message = "";
        }
    }

    public QDLogLevel getLevel() {
        return level;
    }

    public void setLevel(QDLogLevel level) {
        this.level = level;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public StackTraceElement[] getStackTraceElements() {
        return stackTraceElements;
    }

    public void setStackTraceElements(StackTraceElement[] stackTraceElements) {
        this.stackTraceElements = stackTraceElements;
    }

    public String getThrowableInfo() {
        return String.format("\n%s", Log.getStackTraceString(throwable));
    }

    public String getStackInfo() {
        //String fileName = Thread.currentThread().getStackTrace()[index].getFileName(); //文件名
            /*className = Thread.currentThread().getStackTrace()[index].getClassName();
            methodName = Thread.currentThread().getStackTrace()[index].getMethodName();//函数名
            lineNumber = Thread.currentThread().getStackTrace()[index].getLineNumber(); //行号*/
        StringBuilder stringBuilder = new StringBuilder();
        if (stackTraceElements != null) {
            stringBuilder.append("\nStackInfo:");
            for (StackTraceElement stackTraceElement : stackTraceElements) {
                stringBuilder.append("\n")
                        .append(String.format("\tat %s:%s", stackTraceElement.getClassName(), stackTraceElement.getLineNumber()));
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 获取行号
     * @param stackTraceElements
     * @param className
     * @return
     */
    public int getLogLineNumber(StackTraceElement[] stackTraceElements, String className) {
        if (!TextUtils.isEmpty(className) && stackTraceElements != null && stackTraceElements.length > 1) {
            for (StackTraceElement stackTraceElement : stackTraceElements) {
                String elementName = stackTraceElement.getClassName();
                if (elementName.equals(className)) {
                    return stackTraceElement.getLineNumber();
                }
            }
        }
        return 0;
    }

    String mHeaderStr;

    public String generateHeader123() {
        /*if (TextUtils.isEmpty(mHeaderStr)) {
            Thread thread = Thread.currentThread();
            if (thread != null) {
                setThreadId(thread.getId());
                setStackTraceElements(thread.getStackTrace());
            }

            String str = "";// String.format("\n%s", logDateFormat.format(new Date()));
            if (QDLogger.isDebug() && !TextUtils.isEmpty(clazzName)) {
                str += String.format("(%s:%s)", clazzName, getLineNumber());
            }
            if (showTag || getThrowable() != null) {
                str += String.format("-%s", tag);
            }
            if (showThreadInfo || getThrowable() != null) {
                str += String.format("-[Thread:%s]", threadId);
            }
            mHeaderStr = str;
        }*/
        return mHeaderStr;
    }

    public String generateBody() {
        if (throwable != null) {//错误日志
            return getMessage() + getThrowableInfo();
        }
        return getMessage() + "";
    }

   /* public boolean isShowThreadInfo() {
        return showThreadInfo;
    }

    public void setShowThreadInfo(boolean showThreadInfo) {
        this.showThreadInfo = showThreadInfo;
    }*/
}

