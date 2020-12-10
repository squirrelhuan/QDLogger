package cn.demomaster.qdlogger_library.format.table;

import java.util.HashMap;
import java.util.Map;

public class TableItem {
    Map<String,Object> fieldMap;

    public Map<String, Object> getFieldMap() {
        return fieldMap;
    }

    public TableItem() {
        this.fieldMap = new HashMap<>();
    }

    public void addProperty(String fieldName , Object fieldValue){
        fieldMap.put(fieldName,fieldValue);
    }
}
