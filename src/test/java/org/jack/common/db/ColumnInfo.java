package org.jack.common.db;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jack.common.util.Utils;

public class ColumnInfo implements Comparable<ColumnInfo>{
	private String name;
	private String type;
	private Integer length;
	private Integer precision;
	private Integer scale;
	private String comments;
	private boolean nullable=true;
	private int index;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getLength() {
		return length;
	}
	public void setLength(Integer length) {
		this.length = length;
	}
	public Integer getPrecision() {
		return precision;
	}
	public void setPrecision(Integer precision) {
		this.precision = precision;
	}
	public Integer getScale() {
		return scale;
	}
	public void setScale(Integer scale) {
		this.scale = scale;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public boolean isNullable() {
		return nullable;
	}
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	@Override
	public int compareTo(ColumnInfo o) {
		return Integer.compare(index, o.index);
	}
	public Map<String,Object> convertToPropertyModel() {
		Map<String,Object> propertyModel=new HashMap<>();
		propertyModel.put("name", Utils.columnToProperty(name, new StringBuilder()));
		propertyModel.put("type", this.converToPropertyType());
		propertyModel.put("comment", comments);
		return propertyModel;
	}
	private Object converToPropertyType() {
		if("TIMESTAMP".equalsIgnoreCase(type)||"DATE".equalsIgnoreCase(type)){
			Date.class.getSimpleName();
		}
		if("NUMBER".equalsIgnoreCase(type)) {
			if(scale==2&&precision>10) {
				return BigDecimal.class.getSimpleName();
			}
			if(scale>0) {
				return Double.class.getSimpleName();
			}
			if(precision>10) {
				return Long.class.getSimpleName();
			}
			return Integer.class.getSimpleName();
		}
		if("int".equalsIgnoreCase(type)) {
			return Integer.class.getSimpleName();
		}
		if("bigint".equalsIgnoreCase(type)) {
			return Long.class.getSimpleName();
		}
		return String.class.getSimpleName();
	}
}
