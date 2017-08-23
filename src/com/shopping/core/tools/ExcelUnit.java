package com.shopping.core.tools;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.hssf.record.formula.functions.T;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelUnit
{
  public static void exportExcel(String title, String[] headers, Collection<T> dataset, OutputStream out, String pattern)
  {
    HSSFWorkbook workbook = new HSSFWorkbook();

    HSSFSheet sheet = workbook.createSheet(title);

    sheet.setDefaultColumnWidth(15);

    HSSFCellStyle style = workbook.createCellStyle();
    style.setFillForegroundColor((short) 40);
    style.setFillPattern((short) 1);

    style.setBorderBottom((short) 1);
    style.setBorderLeft((short) 1);
    style.setBorderRight((short) 1);
    style.setBorderTop((short) 1);

    style.setAlignment((short) 2);

    HSSFFont font = workbook.createFont();
    font.setColor((short) 20);
    font.setFontHeightInPoints((short) 12);
    font.setBoldweight((short) 700);

    style.setFont(font);

   /* HSSFCellStyle style_ = workbook.createCellStyle();
    style_.setFillForegroundColor(43);
    style_.setFillPattern(1);
    style_.setBorderBottom(1);
    style_.setBorderLeft(1);
    style_.setBorderRight(1);
    style_.setBorderTop(1);

    style_.setAlignment(2);
    style_.setVerticalAlignment(1);

    HSSFFont font_ = workbook.createFont();

    font.setBoldweight(400);
    style_.setFont(font_);*/

    HSSFPatriarch patriarch = sheet.createDrawingPatriarch();

    HSSFComment comment = patriarch.createComment(
      new HSSFClientAnchor(0,0, 0, 0, (short)4, 2, (short)6, 5));
    comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));

    comment.setAuthor("erikchang");

    HSSFRow row = sheet.createRow(0);
    for (int i = 0; i < headers.length; i++)
    {
      HSSFCell cell = row.createCell(i);
      cell.setCellStyle(style);
      cell.setCellValue(new HSSFRichTextString(headers[i]));
    }

    Iterator it = dataset.iterator();
    int index = 0;
    while (it.hasNext()) {
      index++;
      row = sheet.createRow(index);
      T t = (T)it.next();

      Field[] fields = t.getClass().getDeclaredFields();
      for (int i = 0; i < fields.length; i++) {
        HSSFCell cell = row.createCell(i);
        cell.setCellStyle(style);
        Field field = fields[i];
        String fieldName = field.getName();
        String getMethodName = "get" + 
          fieldName.substring(0, 1).toUpperCase() + 
          fieldName.substring(1);
        try {
          Class tCls = t.getClass();
          Method getMethod = tCls.getMethod(getMethodName, 
            new Class[0]);
          Object value = getMethod.invoke(t, new Object[0]);

          String textValue = null;
          if ((value instanceof Boolean)) {
            boolean bValue = ((Boolean)value).booleanValue();
            textValue = "男";
            if (!bValue)
              textValue = "女";
          }
          else if ((value instanceof Date)) {
            Date date = (Date)value;
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            textValue = sdf.format(date);
          }
          else if ((value instanceof byte[]))
          {
            row.setHeightInPoints(60.0F);

            sheet.setColumnWidth(i, 2856);
            byte[] bsValue = (byte[])value;
            HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0,1023, 255, (short)6, index, (short)6, index);
            anchor.setAnchorType(2);
            patriarch.createPicture(anchor, workbook.addPicture(
              bsValue, 5));
          }
          else
          {
            textValue = value.toString();
          }

          if (textValue != null) {
            Pattern p = Pattern.compile("^\\d+(\\.\\d+)?$");
            Matcher matcher = p.matcher(textValue);
            if (matcher.matches())
            {
              cell.setCellValue(Double.parseDouble(textValue));
            }
            else {
              HSSFRichTextString richString = new HSSFRichTextString(
                textValue);
              HSSFFont _font = workbook.createFont();
              _font.setColor((short) 12);
              richString.applyFont(_font);
              cell.setCellValue(richString);
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    try {
      workbook.write(out);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void exportTable2Excel(String title, String[] headers, Collection dataset, OutputStream out, String pattern)
  {
    HSSFWorkbook workbook = new HSSFWorkbook();

    HSSFSheet sheet = workbook.createSheet(title);

    sheet.setDefaultColumnWidth(20);

    HSSFCellStyle style = workbook.createCellStyle();

    style.setFillForegroundColor((short)40);
    style.setFillPattern((short)1);

    style.setBorderBottom((short)1);
    style.setBorderLeft((short)1);
    style.setBorderRight((short)1);
    style.setBorderTop((short)1);

    style.setAlignment((short)2);

    HSSFFont font = workbook.createFont();
    font.setColor((short)20);
    font.setFontHeightInPoints((short)14);
    font.setBoldweight((short)700);

    style.setFont(font);

    HSSFCellStyle style_ = workbook.createCellStyle();
    style_.setFillForegroundColor((short)43);
    style_.setFillPattern((short)1);
    style_.setBorderBottom((short)1);
    style_.setBorderLeft((short)1);
    style_.setBorderRight((short)1);
    style_.setBorderTop((short)1);

    style_.setAlignment((short)2);
    style_.setVerticalAlignment((short)1);

    HSSFFont font_ = workbook.createFont();

    font_.setFontHeightInPoints((short)14);

    style_.setFont(font_);

    HSSFRow row = sheet.createRow(0);
    for (int i = 0; i < headers.length; i++)
    {
      HSSFCell cell = row.createCell(i);
      cell.setCellStyle(style);
      cell.setCellValue(new HSSFRichTextString(headers[i]));
    }

    int index = 0;
    for (Iterator localIterator = dataset.iterator(); localIterator.hasNext(); ) { Object obj = localIterator.next();
      index++;
      row = sheet.createRow(index);
      Object[] list = (Object[])obj;
      int rowIndex = 0;
      for (Object o : list)
        if (o != null) {
          HSSFCell cell = row.createCell(rowIndex);
          cell.setCellStyle(style_);
          cell.setCellValue(o.toString());
          rowIndex++;
        }
    }
    try
    {
      workbook.write(out);
      out.flush();
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}