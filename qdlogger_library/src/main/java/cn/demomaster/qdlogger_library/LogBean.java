package cn.demomaster.qdlogger_library;

public class LogBean {
    private LoggerType type;
    private Object message;
    private String tag;
    private StackTraceElement[] stackTraceElements;
    private long threadId = -1;

    public LogBean(LoggerType type, String tag, Object message) {
        this.type = type;
        this.message = message;
        this.tag = tag;
    }

    public LoggerType getType() {
        return type;
    }

    public void setType(LoggerType type) {
        this.type = type;
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

    public String getStackInfo() {
        //String fileName = Thread.currentThread().getStackTrace()[index].getFileName(); //文件名
            /*className = Thread.currentThread().getStackTrace()[index].getClassName();
            methodName = Thread.currentThread().getStackTrace()[index].getMethodName();//函数名
            lineNumber = Thread.currentThread().getStackTrace()[index].getLineNumber(); //行号*/

        String str = "";
        for(StackTraceElement stackTraceElement : stackTraceElements){
           str += "\n"+String.format("\tat %s:%s", stackTraceElement.getClassName(), stackTraceElement.getLineNumber());
        }
       return str;
    }
}

