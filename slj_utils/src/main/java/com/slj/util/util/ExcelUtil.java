/**
 * @(#)ExcelUtil.java Created by albert.shen on 2019/9/19   19:14
 * <p>
 * Copyrights (C) 2019保留所有权利
 */

package com.slj.util.util;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

/**
 * (类型功能说明描述)
 *
 * <p>
 * 修改历史:                                 <br>  
 * 修改日期           修改人员       版本       修改内容<br>  
 * -------------------------------------------------<br>  
 * 2019/9/19 19:14   albert.shen     1.0       初始化创建<br>
 * </p> 
 *
 * @author albert.shen
 * @version 1.0
 * @since JDK1.8
 */


/**
 * 小提示：用maven引入依赖jar包的可能会遇到包引用不到的bug，
 * 但是maven依赖确实已经引入了，而且没有任何报错，但是只要一引用  org.apache.poi.hssf.usermodel下面的类
 * 就会报错，报错内容为：Caused by: java.lang.NoClassDefFoundError: org/apache/poi/hssf/usermodel/HSSFWorkbook。
 *
 * HSSF（用于操作Excel的组件）提供给用户使用的对象在rg.apache.poi.hssf.usermodel包中,主要部分包括Excel对象，样式和格式，有以下几种常用的对象：
 * 常用组件：
 * HSSFWorkbook     excel的文档对象
 * HSSFSheet            excel的表单
 * HSSFRow               excel的行
 * HSSFCell                excel的格子单元
 * HSSFFont               excel字体
 * 样式：
 * HSSFCellStyle         cell样式
 *
 * 4.基本操作步骤：
 * 首先，我们应该要知道的是，一个Excel文件对应一个workbook，一个workbook中有多个sheet组成，一个sheet是由多个行(row)和列(cell)组成。
 * 那么我们用poi要导出一个Excel表格的正确顺序应该是：
 * 1、用HSSFWorkbook打开或者创建“Excel文件对象”
 * 2、用HSSFWorkbook对象返回或者创建Sheet对象
 * 3、用Sheet对象返回行对象，用行对象得到Cell对象
 * 4、对Cell对象读写。
 * 5、将生成的HSSFWorkbook放入HttpServletResponse中响应到前端页面
 */
public class ExcelUtil {
    /**
     * 导出Excel
     * @param sheetName sheet名称
     * @param title 标题
     * @param values 内容
     * @param wb HSSFWorkbook对象
     * @return
     */
    public static HSSFWorkbook getHSSFWorkbook(String sheetName, String []title, String [][]values, HSSFWorkbook wb){

        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
        if(wb == null){
            wb = new HSSFWorkbook();
        }

        // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet(sheetName);

        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
        HSSFRow row = sheet.createRow(0);

        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        //创建一个居中样式
        style.setAlignment(HorizontalAlignment.CENTER);

        //声明列对象
        HSSFCell cell = null;

        //创建标题
        for(int i=0;i<title.length;i++){
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }

        //创建内容
        for(int i=0;i<values.length;i++){
            row = sheet.createRow(i + 1);
            for(int j=0;j<values[i].length;j++){
                //将内容按顺序赋给对应的列对象
                row.createCell(j).setCellValue(values[i][j]);
            }
        }
        return wb;
    }
}
/*
@Controller
@RequestMapping(value = "/report")
public class ReportFormController extends BaseController {

    @Resource(name = "reportService")
    private ReportManager reportService;

    //导出报表
    @RequestMapping(value = "/export")
    @ResponseBody
    public void export(HttpServletRequest request,HttpServletResponse response) throws Exception {
        //获取数据
        List<PageData> list = reportService.bookList(page);

        //excel标题
        　　String[] title = {"名称","性别","年龄","学校","班级"};

         　//excel文件名
        　 String fileName = "学生信息表"+System.currentTimeMillis()+".xls";

  　　　　　//sheet名
        　 String sheetName = "学生信息表";

　　　　　　for (int i = 0; i < list.size(); i++) {
            content[i] = new String[title.length];
            PageData obj = list.get(i);
            content[i][0] = obj.get("stuName").tostring();
            content[i][1] = obj.get("stuSex").tostring();
            content[i][2] = obj.get("stuAge").tostring();
            content[i][3] = obj.get("stuSchoolName").tostring();
            content[i][4] = obj.get("stuClassName").tostring();
　　　　　　}

　　　　　　//创建HSSFWorkbook
　　　　　　HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook(sheetName, title, content, null);

　　　　　　//响应到客户端
　　　　　　try {
　　　　　　　　this.setResponseHeader(response, fileName);
       　　　　OutputStream os = response.getOutputStream();
       　　　　wb.write(os);
       　　　　os.flush();
       　　　　os.close();
 　　　　　　} catch (Exception e) {
       　　　　e.printStackTrace();
 　　　　　　}
　　}

    //发送响应流方法
    public void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes(),"ISO8859-1");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename="+ fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}*/



