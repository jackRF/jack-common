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
        if(firstRowNum==-1){
            writeHeadToExcel(sheet.createRow(firstRowNum+1), pairList);
        }
        writeDataToExcel(sheet, beanList, propertyIndexMap,pairList);
        return workbook;
    }
    public static void writeHeadToExcel(Row row,List<Pair<String,ColumnInfo>> pairList){
        Sheet sheet=row.getSheet();
        int i=-1;
        for(Pair<String,ColumnInfo> pair:pairList){
            Cell  cell=row.getCell(++i);
            if(cell==null){
                cell=row.createCell(i);
            }
            cell.setCellValue(pair.getV2().getName());
            ColumnInfo columnInfo=pair.getV2();
            int width=columnInfo.getWidth();
            if(width>0){
                sheet.setColumnWidth(i,width);
            }
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
            data.add(exportRowData(sheetRow, i));
        }
        return data;
    }
    public static <B extends ImportBean> Result<List<Pair<Position,B>>> exportData(Sheet sheet,Class<B> clazz,Map<String,String> headMap,boolean stopIfFail){
        int firstRowNum=sheet.getFirstRowNum();
        int lastRowNum=sheet.getLastRowNum();
        return exportData(sheet, firstRowNum, lastRowNum, clazz, headMap, stopIfFail);
    }
    public static <B extends ImportBean> Result<List<Pair<Position,B>>> exportData(Sheet sheet,int firstRowNum,int lastRowNum,Class<B> clazz,Map<String,String> headMap,boolean stopIfFail){
        List<Pair<Position,B>> data=new ArrayList<Pair<Position,B>>();
        Pair<Position,List<Object>> head=null;
        Map<Integer,String> propertyMap=new HashMap<Integer,String>();
        Position nextRowPosition=null;
        int fieldCount=0;
        List<String> messages=new ArrayList<String>();
        for (int i = firstRowNum; i <=lastRowNum; i++) {//循环获取工作表的每一行
            Row sheetRow = sheet.getRow(i);
            if(sheetRow==null){
                if(head!=null){
                    break;
                }
                continue;
            }
            Pair<Position,List<Object>> rowData=exportRowData(sheetRow,i);
            if(head==null){
                head=rowData;
                int cellIndex=head.getV1().getCellIndex();
                List<Object> values=head.getV2();
                for(Object value:values){
                    String columnName=(String)value;
                    if(!StringUtils.hasText(columnName)){
                        return Result.fail("列名读取错误！");
                    }
                    if(headMap.containsKey(columnName)){
                        propertyMap.put(cellIndex,headMap.get(columnName));
                    }
                    cellIndex++;
                }
                nextRowPosition=rowData.getV1().bottom();
                fieldCount=values.size();
            }else{
                if(!nextRowPosition.cellRange(fieldCount).inRange(rowData.getV1().cellRange(rowData.getV2().size()))){
                    return Result.fail("读取数据错误:"+rowData.getV1().toString());
                }
                nextRowPosition=nextRowPosition.bottom();
                Pair<Position,B> rowBean=convertToBean(rowData, clazz, propertyMap);
                B bean=rowBean.getV2();
                Result<Void> beanResult=bean.validateImport();
                if(!beanResult.isSuccess()){
                    String message="第"+(rowBean.getV1().getRowIndex()+1)+"行"+beanResult.getMessage();
                    if(stopIfFail){
                        return Result.fail(message); 
                    }else{
                        messages.add(message);
                    }
                }else if(messages.isEmpty()){
                    data.add(rowBean);
                }
            }
        }
        if(!messages.isEmpty()){
            return Result.fail(messages.toString());
        }
        return Result.success(data);
    }
    public static interface ImportBean{
        Result<Void> validateImport();
    }
    public static <B> Pair<Position,B> convertToBean(Pair<Position,List<Object>> rowData,Class<B> clazz, Map<Integer,String> propertyMap){
        Pair<Position,B>  rowBean=new Pair<>();
        rowBean.setV1(rowData.getV1());
        BeanWrapperImpl beanWrapper=new BeanWrapperImpl(clazz);
        int currentCellIndex=rowBean.getV1().getCellIndex();
        for(Object value:rowData.getV2()){
            if(propertyMap.containsKey(currentCellIndex)){
                beanWrapper.setPropertyValue(propertyMap.get(currentCellIndex), value);
            }
            currentCellIndex++;
        }
        rowBean.setV2((B)beanWrapper.getWrappedInstance());
        return rowBean;
    }
    public static Pair<Position,List<Object>> exportRowData(Row sheetRow,int i){
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
        return row;
    }
}