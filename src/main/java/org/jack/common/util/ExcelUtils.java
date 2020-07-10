package org.jack.common.util;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jack.common.core.ColumnInfo;
import org.jack.common.core.Pair;
import org.jack.common.core.Position;
import org.jack.common.core.Result;
import org.jack.common.core.ColumnInfo.Formatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
public class ExcelUtils {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);
    public static <T> Workbook writeToExcel(Workbook workbook,List<T> beanList,List<Pair<String,ColumnInfo>> pairList)
            throws IOException {
        Sheet sheet;
        if(workbook==null){
            workbook = WorkbookFactory.create(true);
            sheet=workbook.createSheet();
        }else{
            sheet=workbook.getSheetAt(0);
        }
        Map<String,Integer> propertyIndexMap=new HashMap<String,Integer>();
        int i=-1;
        for(Pair<String,ColumnInfo> pair:pairList){
            propertyIndexMap.put(pair.getV1(), ++i);
        }
        int firstRowNum=sheet.getFirstRowNum();
        List<Pair<Position, List<Object>>>  list=exportData(sheet, firstRowNum, firstRowNum);
        if(list.isEmpty()){
            Row  row=sheet.createRow(firstRowNum+1);
            writeHeadToExcel(row, pairList);
            i=-1;
            for(Pair<String,ColumnInfo> pair:pairList){
                ++i;
                ColumnInfo columnInfo=pair.getV2();
                int width=columnInfo.getWidth();
                if(width>0){
                    sheet.setColumnWidth(i,width);
                }
            }
        }
        writeDataToExcel(sheet, beanList, propertyIndexMap,pairList);
        return workbook;
    }
    public static void writeHeadToExcel(Row row,List<Pair<String,ColumnInfo>> pairList){
        int i=-1;
        for(Pair<String,ColumnInfo> pair:pairList){
            Cell  cell=row.getCell(++i);
            if(cell==null){
                cell=row.createCell(i);
            }
            cell.setCellValue(pair.getV2().getName());
        }
    }
    public static <T> void  writeDataToExcel(Sheet sheet,List<T> beanList,Map<String,Integer> propertyIndexMap,List<Pair<String,ColumnInfo>> pairList){
        int lastRowNum=sheet.getLastRowNum();
        for(Object bean:beanList){
            Row row=sheet.getRow(++lastRowNum);
            if(row==null){
                row=sheet.createRow(lastRowNum);
            }
            Object[] cellValues=new Object[propertyIndexMap.size()];
            BeanWrapperImpl beanWrapper=new BeanWrapperImpl(bean);
            PropertyDescriptor[] pds=beanWrapper.getPropertyDescriptors();
            for(PropertyDescriptor pd:pds){
                String property=pd.getName();
                Method  readMethod=pd.getReadMethod();
                if(readMethod==null
                ||Object.class.equals(readMethod.getDeclaringClass())
                ||!beanWrapper.isReadableProperty(property)
                ||!propertyIndexMap.containsKey(property)){
                    continue;
                }
                int i=propertyIndexMap.get(property).intValue();
                cellValues[i]=beanWrapper.getPropertyValue(property);
            }
            int i=-1;
        for(Object value:cellValues){
                Cell  cell=row.getCell(++i);
                if(cell==null){
                    cell=row.createCell(i);
                }
                ColumnInfo columnInfo=pairList.get(i).getV2();
                Formatter<Object,Object> formatter=(Formatter<Object,Object>)columnInfo.getFormatter();
                if(formatter!=null){
                    value=formatter.format(value, bean);
                }
                if(value==null){
                    cell.setCellValue("");
                }else if (value instanceof String){
                    cell.setCellValue((String)value);
                }else if (value instanceof Date){
                    String dateFormat=columnInfo.getDateFormat();
                    if(!StringUtils.hasText(dateFormat)){
                        dateFormat=DateUtils.DATE_FORMAT_DATETIME;
                    }
                    cell.setCellValue(DateUtils.formatDate((Date)value,dateFormat));
                }else{
                    cell.setCellValue(value.toString());
                }
            }
        }
    }
    public static boolean isExcelFile(String fileName){
        if (fileName.matches("^.+\\.(?i)(xls|xlsx)$")){
            return true;
        }
        return false;
    }
    public static <T> Result<List<Pair<Position,T>>> tableToModelList(List<Pair<Position,List<Object>>> list,Class<T> clazz,Map<String,String> headMap){
        List<Pair<Position,T>>  modelList=new ArrayList<Pair<Position,T>>();
        if(CollectionUtils.isEmpty(list)||list.size()<2){
            return Result.success(modelList);
        }
        Map<Integer,String> fieldMap=new HashMap<Integer,String>();
        List<Object> head=list.get(0).getV2();
        Position headPosition=list.get(0).getV1();
        int ln=list.size();
        int hi=-1;
        for(Object field:head){
            hi++;
            String property=headMap.get(field.toString().trim());
            if(StringUtils.hasText(property)){
                fieldMap.put(hi+headPosition.getCellIndex(), property);
            }
        }
        fieldMap=Collections.unmodifiableMap(fieldMap);
        Position nextRowPosition=headPosition.bottom();
        int fieldCount=head.size();
        for(int i=1;i<ln;i++){
            List<Object> row=list.get(i).getV2();
            Position rowPosition=list.get(i).getV1();
            if(!nextRowPosition.cellRange(fieldCount).inRange(rowPosition.cellRange(row.size()))){
                logger.info("nextRowPosition:"+nextRowPosition+" fieldCount:"+fieldCount+",rowPosition:"+rowPosition+" size:"+row.size());
                return Result.fail(nextRowPosition+"表格数据读取错误");
            }else{
                nextRowPosition=nextRowPosition.bottom();
            }
            int j=-1;
            BeanWrapperImpl beanWrapper=new BeanWrapperImpl(clazz);
            for(Object value:row){
                int ci=++j+rowPosition.getCellIndex();
                if(fieldMap.containsKey(ci)){
                    beanWrapper.setPropertyValue(fieldMap.get(ci), value);
                }
            }
            Pair<Position,T> rowPair=new Pair<Position,T>();
            rowPair.setV1(rowPosition);
            rowPair.setV2((T)beanWrapper.getRootInstance());
            modelList.add(rowPair);
        }
        return Result.success(modelList);
    }
    public static List<Pair<Position,List<Object>>> excelToList(InputStream inputStream, int sheetIndex)
            throws EncryptedDocumentException, IOException {
        Workbook workbook = WorkbookFactory.create(inputStream);
        List<Pair<Position,List<Object>>> data=exportData(workbook.getSheetAt(sheetIndex));
        workbook.close();
        inputStream.close();
        return data;
    }
    public static List<Pair<Position,List<Object>>> excelToList(InputStream inputStream, String sheetName)
            throws EncryptedDocumentException, IOException {
        Workbook workbook = WorkbookFactory.create(inputStream);
        List<Pair<Position,List<Object>>> data=exportData(workbook.getSheet(sheetName));
        workbook.close();
        inputStream.close();
        return data;
    }
    public static List<Pair<Position,List<Object>>> exportData(Sheet sheet){
        int firstRowNum=sheet.getFirstRowNum();
        int lastRowNum=sheet.getLastRowNum();
        return exportData(sheet, firstRowNum, lastRowNum);
    }
    public static List<Pair<Position,List<Object>>> exportData(Sheet sheet,int firstRowNum,int lastRowNum){
        List<Pair<Position,List<Object>>> data = new ArrayList<Pair<Position,List<Object>>>();
        for (int i = firstRowNum; i <=lastRowNum; i++) {//循环获取工作表的每一行
            Row sheetRow = sheet.getRow(i);
            if(sheetRow==null){
                continue;
            }
            int firstCellNum=sheetRow.getFirstCellNum();
            int lastCellNum=sheetRow.getLastCellNum();
            Position position=new Position(i,firstCellNum);
            List<Object> rowData = new ArrayList<Object>();
            for (int j = firstCellNum; j < lastCellNum; j++) {//循环获取每一列
                Cell  cell=sheetRow.getCell(j);
                if(cell==null){
                    rowData.add(null);
                    continue;
                }
                CellType  cellType =cell.getCellType();
                if(CellType.STRING.equals(cellType)){
                    rowData.add(cell.getStringCellValue());
                }else if(CellType.NUMERIC.equals(cellType)){
                    rowData.add(cell.getNumericCellValue());
                }else if(CellType.BOOLEAN.equals(cellType)){
                    rowData.add(cell.getBooleanCellValue());
                }else{
                    rowData.add(null);
                }
            }
            Pair<Position,List<Object>>  row=new Pair<Position,List<Object>>(); 
            row.setV1(position);
            row.setV2(rowData);
            data.add(row);
        }
        return data;
    }
}