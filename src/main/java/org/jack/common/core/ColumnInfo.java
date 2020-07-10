package org.jack.common.core;

public class ColumnInfo{
    private String name;
    private int width;
    private String dateFormat;
    private Formatter<?,?> formatter;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
    public Formatter<?, ?> getFormatter() {
        return formatter;
    }

    public void setFormatter(Formatter<?, ?> formatter) {
        this.formatter = formatter;
    }
    public static interface Formatter<V,T>{
        Object format(V value,T bean);
    }

    
}