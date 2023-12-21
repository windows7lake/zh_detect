package com.lio.zh_detect;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class ExcelTool {

    public static <T> void export(String filePath, String fileName, String[] title, List<T> rowData) {
        try {
            HSSFWorkbook wb = generate(fileName, title, rowData);
            FileOutputStream fos = new FileOutputStream(filePath + File.separator + fileName);
            wb.write(fos);
        } catch (IllegalAccessException | IOException e) {
            e.printStackTrace();
        }
    }

    private static <T> HSSFWorkbook generate(String sheetName, String[] title, List<T> rowData)
            throws IllegalAccessException {
        // 创建工作薄
        HSSFWorkbook wb = new HSSFWorkbook();

        // 创建工作表
        HSSFSheet sheet = wb.createSheet();
        sheet.createFreezePane(0, 1, 0, 1);
        wb.setSheetName(0, sheetName);

        // 创建样式和字体
        HSSFCellStyle curStyle = wb.createCellStyle();
        HSSFFont curFont = wb.createFont();
        Font font = wb.getFontAt((short) 0);
        font.setCharSet(HSSFFont.DEFAULT_CHARSET);

        // 更改默认字体大小
        font.setFontHeightInPoints((short) 12);
        font.setFontName("宋体");
        curStyle.setFont(font);

        // 创建行列（默认第一行第一列）
        HSSFRow nRow = sheet.createRow(0);
        HSSFCell nCell = nRow.createCell(0);

        // 行号
        int rowNo = 0;

        // 设置标题到第一行
        nRow = sheet.createRow(rowNo++);
        for (int i = 0; i < title.length; i++) {
            nCell = nRow.createCell(i);
            nCell.setCellValue(title[i]);
            nCell.setCellStyle(titleStyle(curStyle, curFont));
        }

        // 设置内容
        for (T row : rowData) {
            Class<?> aClass = row.getClass();
            nRow = sheet.createRow(rowNo++);
            int colNo = 0; // 列号
            Field[] fields = aClass.getDeclaredFields();
            for (int i = 0; i < title.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                nCell = nRow.createCell(colNo++);
                nCell.setCellValue(field.get(row).toString());
                nCell.setCellStyle(textStyle(curStyle, curFont));
            }
        }

        return wb;
    }

    /**
     * 表格内容样式
     *
     * @param curStyle 表格样式
     * @param curFont  字体样式
     * @return 表格样式
     */
    private static HSSFCellStyle textStyle(HSSFCellStyle curStyle, HSSFFont curFont) {
        curStyle.setAlignment(HorizontalAlignment.CENTER);  // 水平居中
        curStyle.setWrapText(false);                        // 自动换行
        curFont.setFontName("宋体");                         // 字体
        curFont.setFontHeightInPoints((short) 10);          // 字体大小
        curStyle.setFont(curFont);                          // 绑定关系
        return curStyle;
    }

    /**
     * 表格标题样式
     *
     * @param curStyle 表格样式
     * @param curFont  字体样式
     * @return 表格样式
     */
    private static HSSFCellStyle titleStyle(HSSFCellStyle curStyle, HSSFFont curFont) {
        curStyle.setAlignment(HorizontalAlignment.CENTER);  // 水平居中
        curFont.setFontHeightInPoints((short) 16);          // 字体大小
        curStyle.setFont(curFont);                          // 绑定关系
        return curStyle;
    }
}
