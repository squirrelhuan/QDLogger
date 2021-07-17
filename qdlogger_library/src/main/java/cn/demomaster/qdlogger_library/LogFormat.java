package cn.demomaster.qdlogger_library;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import cn.demomaster.qdlogger_library.format.MapKeyComparator;

import static cn.demomaster.qdlogger_library.QDLogger.logDateFormat;

public class LogFormat {

    SimpleDateFormat simpleDateFormat;
    public LogFormat(SimpleDateFormat simpleDateFormat) {
        this.simpleDateFormat = simpleDateFormat;
    }

    public String formatHeader(String formatStr, QDLogBean qdLogBean){
        Map<Integer,String> valuesMap = new LinkedHashMap<>();

        //先解析对应的标签
        if(formatStr.contains("time")){
            valuesMap.put(formatStr.indexOf("time"),"time");
        }
        if(formatStr.contains("tag")){
            valuesMap.put(formatStr.indexOf("tag"),"tag");
        }
        if(formatStr.contains("class")){
            valuesMap.put(formatStr.indexOf("class"),"class");
        }
        if(formatStr.contains("thread")){
            valuesMap.put(formatStr.indexOf("thread"),"thread");
        }
        //再对已有标签替换内容
        Map<Integer,String> valuesMap2 = sortMapByKey(valuesMap);
        String msg= formatStr;
        for(Map.Entry entry:valuesMap2.entrySet()){
            String val = (String) entry.getValue();
            String newValue="";
            if(val.equals("tag")){
                newValue = qdLogBean.getTag();
            }else if(val.equals("time")){
                newValue = getDateTimeStr();
            }else if(val.equals("class")){
                newValue = "("+qdLogBean.getClazzFileName()+":"+qdLogBean.getLineNumber()+")";
            }else if(val.equals("thread")){
                newValue = "["+qdLogBean.getThreadId()+"]";
            }
            if(TextUtils.isEmpty(newValue)){
                newValue="";
            }
            if(msg.contains(val)){
                String[] strings = msg.split(val,2);
                msg = strings[0]+newValue+strings[1];
            }
        }
        return msg;
    }

    /**
     * 使用 Map按key进行排序
     * @param map
     * @return
     */
    public static Map<Integer, String> sortMapByKey(Map<Integer, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<Integer, String> sortMap = new TreeMap<Integer, String>(
                new MapKeyComparator());
        sortMap.putAll(map);
        return sortMap;
    }

    public String getDateTimeStr() {
        return logDateFormat.format(new Date());
    }

    public String formatEnd(String str) {
        if(!str.trim().endsWith("\n")&&!str.trim().endsWith("\n\r")){
           return str+="\n";
        }
        return str;
    }

    /**
     * 获取simpleName
     * @param str
     * @return
     */
    private static String getClazzSimpleName(String str) {
        if(TextUtils.isEmpty(str)){
            return "";
        }
        String[] arr = str.split("\\u002E");
        if(arr!=null&&arr.length>=1){
            return arr[arr.length-1];
        }
        return str;
    }
}