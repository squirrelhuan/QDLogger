package cn.demomaster.qdlogger_library.format.table;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Table {

    List<TableItem> items;
    //String[] titles;
    private LinkedHashMap<String, Integer> titlesMap;

    public Table() {
        items = new ArrayList<>();
        titlesMap = new LinkedHashMap<>();
    }

    public void setTitles(String[] titles) {
        //this.titles = titles;
        if (titles != null) {
            for (String title : titles) {
                titlesMap.put(title, 0);
            }
        }
    }

    public void addItem(TableItem tableItem) {
        for (Map.Entry entry : tableItem.getFieldMap().entrySet()) {
            if (entry.getKey() != null) {
                int c = Math.max(getValueString(entry.getValue()).length(),((String)entry.getKey()).length());
                String title = (String) entry.getKey();
                if (!titlesMap.containsKey(title)) {
                    titlesMap.put(title, c);
                } else {
                    int len = titlesMap.get(title);
                    if (c > len) {
                        titlesMap.put(title, c);
                    }
                }
            }
        }
        items.add(tableItem);
    }

    private String getValueString(Object value) {
        if (value == null) {
            return "";
        }
        String strValue = null;
        if (value instanceof Date) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");// HH:mm:s
            strValue = simpleDateFormat.format((Date) value);
        } else {
            strValue = String.valueOf(value);
        }
        return strValue;
    }

    public void addAllItem(List<TableItem> tableItems) {
        for (TableItem tableItem : tableItems) {
            for (Map.Entry entry : tableItem.getFieldMap().entrySet()) {
                if (entry.getKey() != null && !titlesMap.containsKey(entry.getKey())) {
                    titlesMap.put((String) entry.getKey(), 0);
                }
                items.add(tableItem);
            }
        }
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        int i = 0;
        for (Map.Entry entry : titlesMap.entrySet()) {
            String title = (String) entry.getKey();
            int maxValue = (int) entry.getValue();
            if (i < titlesMap.size() - 1) {
                stringBuffer.append(String.format("%-"+maxValue+"s\t",title));
            } else {
                stringBuffer.append(String.format("%-"+maxValue+"s\n",title));
            }
            i++;
        }

        for (TableItem item : items) {
            Map<String, Object> fieldMap = item.getFieldMap();
            int j = 0;
            for (Map.Entry entry : fieldMap.entrySet()) {
                String value = getValueString(entry.getValue());
                int maxValue = titlesMap.get(entry.getKey());
                if (j < fieldMap.size() - 1) {
                    stringBuffer.append(String.format("%-"+maxValue+"s\t",value));
                } else {
                    stringBuffer.append(String.format("%-"+maxValue+"s\n",value));
                }
                j++;
            }
        }

        return stringBuffer.toString();
    }
}
