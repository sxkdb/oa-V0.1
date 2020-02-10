package com.zsh.teach_oa;

import com.zsh.teach_oa.dao.StudentOARepository;
import com.zsh.teach_oa.ext.StudentInfo;
import com.zsh.teach_oa.service.StudentOaService;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import java.io.File;
import java.util.List;

/** 
* @author BieHongLi 
* @version 创建时间：2017年3月3日 下午4:03:18 
* 创建excel表格
*/
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class CreateExcelTest {

//    @Autowired
    private StudentOARepository studentOARepository;

//    @Autowired
    private StudentOaService studentInfoService;
//    @Test
    public void testCreateExcel() throws Exception {

        String classId = "1";

        String teacherId = "1";

        List<StudentInfo> list = studentInfoService.findStudentListByClassId(classId,teacherId);

        //1:创建excel文件
        File file=new File("test.xls");
        file.createNewFile();

        //2:创建工作簿
        WritableWorkbook workbook= Workbook.createWorkbook(file);
        //3:创建sheet,设置第二三四..个sheet，依次类推即可
        WritableSheet sheet=workbook.createSheet("用户管理", 0);
        //4：设置titles
        String[] titles={"学号","姓名","账号","密码","qq"};


        //5:单元格
        Label label=null;
        //6:给第一行设置列名
        for(int i=0;i<titles.length;i++){
            //x,y,第一行的列名
            label=new Label(i,0,titles[i]);
            //7：添加单元格
            sheet.addCell(label);
        }


        int count = 1;
        for (StudentInfo studentInfo : list) {
            label=new Label(0,count,studentInfo.getStudent_id());
            sheet.addCell(label);

            label=new Label(1,count,studentInfo.getStudent_name());
            sheet.addCell(label);

            label=new Label(2,count,studentInfo.getAccountName());
            sheet.addCell(label);

            label=new Label(3,count,studentInfo.getPassword());
            sheet.addCell(label);

            label=new Label(4,count,studentInfo.getQq()==null?"":studentInfo.getQq());
            sheet.addCell(label);

            count++;
        }
        count = 1;


        //写入数据，一定记得写入数据，不然你都开始怀疑世界了，excel里面啥都没有
        workbook.write();
        //最后一步，关闭工作簿
        workbook.close();
    }


//    @Test
//    public void testExcelUtils() throws Exception {
//        String classId = "1";
//
//        String teacherId = "1";
//        String[] titles={"学号","姓名","账号","密码","qq"};
//        List<StudentInfo> list = studentInfoService.findStudentListByClassId(classId,teacherId);
//        ExcelUtils.CreateExcelFileInStudentInfo("D:/test/referenceFile/studentInfo.xls", "2班", titles, list,"");
//    }
}