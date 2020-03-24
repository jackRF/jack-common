package org.jack.common.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jack.common.util.Utils;

public class Table {
	private String name;
	private List<KeyInfo> keyInfos;
	private List<ColumnInfo> columnInfos;
	private List<IndexInfo> IndexInfos;
	private List<CheckInfo> checkInfos;
	public Table() {
	}
	public Table(String name) {
		this.name=name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<KeyInfo> getKeyInfos() {
		return keyInfos;
	}
	public void setKeyInfos(List<KeyInfo> keyInfos) {
		this.keyInfos = keyInfos;
	}
	public List<ColumnInfo> getColumnInfos() {
		return columnInfos;
	}
	public void setColumnInfos(List<ColumnInfo> columnInfos) {
		this.columnInfos = columnInfos;
	}
	public List<IndexInfo> getIndexInfos() {
		return IndexInfos;
	}
	public void setIndexInfos(List<IndexInfo> indexInfos) {
		IndexInfos = indexInfos;
	}
	public List<CheckInfo> getCheckInfos() {
		return checkInfos;
	}
	public void setCheckInfos(List<CheckInfo> checkInfos) {
		this.checkInfos = checkInfos;
	}
	public Map<String,Object> convertToClassModel(){
		Map<String,Object> classModel=new HashMap<String, Object>();
		classModel.put("name", Utils.tableToClass(name, new StringBuilder()));
		List<Map<String,Object>> properties=new ArrayList<Map<String,Object>>();
		for(ColumnInfo columnInfo:columnInfos) {
			properties.add(columnInfo.convertToPropertyModel());
		}
		classModel.put("properties", properties);
		return classModel;
	}
}
