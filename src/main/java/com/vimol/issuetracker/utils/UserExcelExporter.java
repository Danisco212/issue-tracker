package com.vimol.issuetracker.utils;

import com.vimol.issuetracker.entities.Issue;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;


public class UserExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Issue> issues;

    public UserExcelExporter(List<Issue> issues) {
        this.issues = issues;
        workbook = new XSSFWorkbook();
    }


    private void writeHeaderLine() {
        sheet = workbook.createSheet("Users");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "ID", style);
        createCell(row, 1, "Title", style);
        createCell(row, 2, "Reason", style);
        createCell(row, 3, "Status", style);
        createCell(row, 4, "Solution", style);
        createCell(row, 5, "Category", style);
        createCell(row, 6, "Created By", style);
        createCell(row, 7, "Solver", style);

    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (Issue issue : issues) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, issue.getId().toString(), style);
            createCell(row, columnCount++, issue.getTitle(), style);
            createCell(row, columnCount++, issue.getReason(), style);
            createCell(row, columnCount++, issue.getStatus().toString(), style);
            createCell(row, columnCount++, issue.getSolution(), style);
            createCell(row, columnCount++, issue.getCategory().getCatName(), style);
            createCell(row, columnCount++, issue.getUser().getEmail(), style);
            createCell(row, columnCount++, issue.getSolver().getEmail(), style);

        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}