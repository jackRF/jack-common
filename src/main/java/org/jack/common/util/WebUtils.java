package org.jack.common.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Workbook;

public class WebUtils {
    // public static void download(HttpServletResponse response, Workbook workbook, String fileName) throws IOException {
    //     response.setContentType("application/vnd.ms-excel;charset=UTF-8");
    //     response.setCharacterEncoding("UTF-8");
    //     response.setHeader("Content-Disposition", "attachment;fileName=" +   java.net.URLEncoder.encode(fileName,"UTF-8"));
    //     workbook.write(response.getOutputStream());
    // }
    // public static void download(HttpServletResponse response, InputStream in, String fileName) throws IOException {
    //     int li=fileName.lastIndexOf(".");
    //     if(li>=0){
    //         String ext=fileName.substring(li);
    //         if(".xls".equalsIgnoreCase(ext)||".xlsx".equalsIgnoreCase(ext)){
    //             response.setContentType("application/vnd.ms-excel;charset=UTF-8");
    //         }
    //     }
    //     response.setCharacterEncoding("UTF-8");
    //     response.setHeader("Content-Disposition", "attachment;fileName=" +   java.net.URLEncoder.encode(fileName,"UTF-8"));
    //     IOUtils.copy(in, response.getOutputStream());
    // }
}