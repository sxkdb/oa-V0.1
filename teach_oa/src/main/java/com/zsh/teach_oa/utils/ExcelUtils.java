package com.zsh.teach_oa.utils;


public class ExcelUtils {


//    public static boolean CreateExcelFileInStudentInfo(String path, String className, String selectData, List<StudentInfo> list, String teacherId) {
//
//        try {
//            //1:创建excel文件
//            File file = new File(path);
//            file.createNewFile();
//
//            //2:创建工作簿
//            WritableWorkbook workbook = Workbook.createWorkbook(file);
//            //3:创建sheet,设置第二三四..个sheet，依次类推即可
//            WritableSheet sheet = workbook.createSheet(className, 0);
//
//            //5:单元格
//            Label label = null;
//
//            List<String> titles = new ArrayList<>();
//            char isWriteStudent_id = selectData.charAt(0);//是否写入学号
//            char isWriteStudent_name = selectData.charAt(1);//是否写入姓名
//            char isWriteTaskInfo = selectData.charAt(2);//是否写入作业信息
//            char isWriteAccount_name = selectData.charAt(3);//是否写入账号
//            char isWritePassword = selectData.charAt(4);//是否写入密码
//            char isWriteQQ = selectData.charAt(5);//是否写入qq账号
//
//            if (isWriteStudent_id == '1') {
//                titles.add("学号");
//            }
//            if (isWriteStudent_name == '1') {
//                titles.add("姓名");
//            }
//            if (isWriteAccount_name == '1') {
//                titles.add("账号");
//            }
//            if (isWritePassword == '1') {
//                titles.add("密码");
//            }
//            if (isWriteQQ == '1') {
//                titles.add("qq");
//            }
//
//            //给list添加任务的名字
//            if (isWriteTaskInfo == '1')
//                for (StudentInfo studentInfo : list) {
//                    String task_list = studentInfo.getTask_list();
//                    List taskList = JSON.parseObject(task_list, List.class);
//                    if (taskList != null && taskList.size() > 0) {
//                        for (Object o : taskList) {
//                            JSONObject jsonObject = (JSONObject) o;
//                            String teacherID = (String) jsonObject.get("pushAuthor");
//                            if (teacherID.equals(teacherId)) {
//                                String taskName = (String) jsonObject.get("task_name");
//                                //追加到titles
//                                titles.add(taskName);
//                            }
//                        }
//                        break;
//                    }
//                }
//
//            //给第一行设置列名
//            for (int i = 0; i < titles.size(); i++) {
//                //x,y,第一行的列名
//                label = new Label(i, 0, titles.get(i));
//                //7：添加单元格
//                sheet.addCell(label);
//            }
//
//            int row = 1;
//            int column = 0;
//            //从第二行开始填充数据
//            for (StudentInfo studentInfo : list) {
//                column = 0;//列
//                if (isWriteStudent_id == '1') {
//                    label = new Label(column++, row, studentInfo.getStudent_id());
//                    sheet.addCell(label);
//                }
//
//                if (isWriteStudent_name == '1') {
//                    label = new Label(column++, row, studentInfo.getStudent_name());
//                    sheet.addCell(label);
//                }
//
//                if (isWriteAccount_name == '1') {
//                    label = new Label(column++, row, studentInfo.getAccountName());
//                    sheet.addCell(label);
//                }
//
//                if (isWritePassword == '1') {
//                    label = new Label(column++, row, studentInfo.getPassword());
//                    sheet.addCell(label);
//                }
//
//                if (isWriteQQ == '1') {
//                    label = new Label(column++, row, studentInfo.getQq() == null ? "" : studentInfo.getQq());
//                    sheet.addCell(label);
//                }
//                if (isWriteTaskInfo == '1') {
//                    String task_list = studentInfo.getTask_list();
//                    List taskList = JSON.parseObject(task_list, List.class);
//                    if (taskList != null && taskList.size() > 0) {
//                        for (Object o : taskList) {
//                            JSONObject jsonObject = (JSONObject) o;
//                            String teacherID = (String) jsonObject.get("pushAuthor");//判断和老师名字是否一致   一致就给第一行的列追加任务名
//                            if (teacherID.equals(teacherId)) {
//                                Boolean submit = (Boolean) jsonObject.get("submit");
//                                String score = (String) jsonObject.get("score");
//                                //拿出任务名字 和 isSubmit  如果提交了的就在对应的行列写上已提交，否者未提交
//                                label = new Label(column++, row, (submit ? (score == null || score.equals("") ? "已提交" : score) : "未提交"));
//                                sheet.addCell(label);
//                            }
//                        }
//                    }
//                }
//                row++;
//            }
//
//            //写入数据，一定记得写入数据，不然你都开始怀疑世界了，excel里面啥都没有
//            workbook.write();
//            //最后一步，关闭工作簿
//            workbook.close();
//        } catch (IOException | WriteException e) {
//            e.printStackTrace();
//        }
//        return true;
//    }


}