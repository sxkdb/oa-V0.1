package com.zsh.teach_oa.service;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.zsh.teach_oa.dao.ClassOARepository;
import com.zsh.teach_oa.ext.ResponseResult;
import com.zsh.teach_oa.ext.StudentInfo;
import com.zsh.teach_oa.model.ClassOa;
import com.zsh.teach_oa.model.StudentOa;
import com.zsh.teach_oa.utils.CompressedFileUtil;
import com.zsh.teach_oa.utils.FileUtils;
import com.zsh.teach_oa.utils.Word2PdfUtil;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.bson.types.ObjectId;
import org.csource.fastdfs.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
public class FileService {


    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private StudentOaService studentOaService;

    @Autowired
    private ClassOARepository classOARepository;


    @Value("${myconfig.pdfurl}")
    private String PDF_URL;

    @Value("${myconfig.pdfPath}")
    private String PDF_PATH;

    @Value("${myconfig.allFilePath}")
    private String ALL_FILE_PATH;

    @Value("${myconfig.fastdfsUrl}")
    private String IMG_URL;

    @Value("${myconfig.classInfoFileExcel}")
    private String CLASSINFO_FILE_EXCELPATH;


    public ResponseResult upload(MultipartFile multipartFile, String studentId, String taskId, String fileTypeStr) {

        String fileType = fileTypeStrTofileType(fileTypeStr);

        //得到文件的原始名称
        String originalFilename = multipartFile.getOriginalFilename();
        //获取文件名（没有后缀）
        String fileName = originalFilename.substring(0, originalFilename.indexOf("."));
        //获取文件后缀
        String fileSuffix = originalFilename.substring(originalFilename.indexOf(".") + 1);

        //判断文件格式是否符合上传要求的格式
        if (!fileType.contains(fileSuffix)) {
            //不符合的情况
            ResponseResult requestResult = new ResponseResult();
            requestResult.setMessage("文件格式不符合上传要求");
            requestResult.setSuccess(false);
            requestResult.setStatus("200");
            requestResult.setCode("500");
            return requestResult;
        }

        //TODO 判断是什么文件后缀,然后用不同的处理逻辑
        //所有文件都要去mongodb，也要写入信息到数据库，不同的是只有word、pdf、图片有查看功能
        ObjectId store = null;
        try {
            InputStream inputStream = multipartFile.getInputStream();
            //将文件上传到mongodb
            store = gridFsTemplate.store(inputStream, fileName);
            IOUtils.closeQuietly(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //将文件信息写到数据库中
        StudentInfo studentInfo = studentOaService.findStudentTasksByStudentId(studentId);
        List studentTaskList = studentInfo.getStudentTaskList();
        for (Object o : studentTaskList) {
            JSONObject jsonObject = (JSONObject) o;
            if (jsonObject.get("id").equals(taskId)) {
                //更新数据库中的作业信息
                jsonObject.put("fileId", store.toString());
                jsonObject.put("fileName", fileName);
                jsonObject.put("fileSuffix", fileSuffix);
                jsonObject.put("submit", true);
                //如果文件是word或者pdf的话
                if (fileSuffix.equals("doc") || fileSuffix.equals("docx")) {
                    //服务器的 ip地址+班级id+任务id+学生学号+学生姓名+.pdf
                    jsonObject.put("fileUrl", PDF_URL + studentInfo.getClassId() + "-" + taskId + "-" + studentInfo.getStudent_id() + studentInfo.getStudent_name() + ".pdf");
                } else if (fileSuffix.equals("pdf")) {
                    jsonObject.put("fileUrl", PDF_URL + studentInfo.getClassId() + "/" + taskId + "/" + studentInfo.getStudent_id() + studentInfo.getStudent_name() + ".pdf");
                } else {
                    jsonObject.put("fileUrl", "");
                }
                break;
            }

        }
        String json = JSONUtils.toJSONString(studentTaskList);
        StudentOa one = new StudentOa();
        BeanUtils.copyProperties(studentInfo, one);
        one.setTask_list(json);
        studentOaService.save(one);

        ResponseResult requestResult = new ResponseResult();
        requestResult.setMessage("上传成功");
        requestResult.setSuccess(true);
        return requestResult;

    }


    private String fileTypeStrTofileType(String fileTypeStr) {//图片 pdf word zip
        String result = "";
        String[] strings = fileTypeStr.split(",");
        int index = 0;
        for (String string : strings) {
            if (string.equals("图片")) {
                result += "png";
                result += "PNG";
                result += "jpg";
                result += "JPG";
            }
            if (string.equals("pdf")) {
                result += "pdf";
            }
            if (string.equals("word")) {
                result += "doc";
                result += "docx";
            }
            if (string.equals("zip")) {
                result += "zip";
            }
        }
        return result;
    }

    /**
     * 根据文件id去mongodb下载文件
     *
     * @param fileId
     * @return
     */
    public InputStream downFileByFileId(String fileId) {
        try {
            //查询文件
            GridFSFile fsFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
            //打开下载流对象
            assert fsFile != null;
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(fsFile.getObjectId());
            //创建GridFsResource 用于获取流对象
            GridFsResource gridFsResource = new GridFsResource(fsFile, gridFSDownloadStream);
//            System.out.println(gridFsResource.getFilename());
            InputStream inputStream = gridFsResource.getInputStream();
            return inputStream;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void uploadPDF(InputStream in1, InputStream in2, String studentId, String taskId) {
        FileOutputStream outputStream1 = null;
        FileOutputStream outputStream2 = null;
        try {
            //将pdf放在磁盘目录  方便后期批量下载
            StudentOa one = studentOaService.getOne(studentId);
            //创建文件夹
            FileUtils.makeDirectory(new File(ALL_FILE_PATH + "/" + one.getClassId() + "/" + taskId + "/" + one.getStudent_id() + one.getStudent_name() + "." + "pdf"));
            outputStream1 = new FileOutputStream(new File(ALL_FILE_PATH + "/" + one.getClassId() + "/" + taskId + "/" + one.getStudent_id() + one.getStudent_name() + "." + "pdf"));

            //创建文件夹
            FileUtils.makeDirectory(new File(PDF_PATH + "/" + one.getClassId() + "/" + taskId + "/" + one.getStudent_id() + one.getStudent_name() + "." + "pdf"));
            //将pdf放在nginx的映射文件html
            outputStream2 = new FileOutputStream(new File(PDF_PATH + "/" + one.getClassId() + "/" + taskId + "/" + one.getStudent_id() + one.getStudent_name() + "." + "pdf"));
            IOUtils.copy(in1, outputStream1);
            IOUtils.copy(in2, outputStream2);
        } catch (IOException e) {
            e.printStackTrace();
            //关流
            IOUtils.closeQuietly(in1);
            IOUtils.closeQuietly(outputStream1);
            IOUtils.closeQuietly(in2);
            IOUtils.closeQuietly(outputStream2);
        }
    }

    /**
     * 处理png、jpg文件
     *
     * @param multipartFile
     */
//    @Async
    public void uploadFastDFS(MultipartFile multipartFile, String studentId, String taskId, String fileSuffix) {
        try {
            InputStream in1 = multipartFile.getInputStream();
            //将图片上传到fastDFS
            initFastDFSConfig();
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            StorageClient1 storageClient1 = new StorageClient1(trackerServer, storeStorage);
            String originalFilename = multipartFile.getOriginalFilename();
            assert originalFilename != null;
            String prefix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String fileUrl = storageClient1.upload_file1(multipartFile.getBytes(), prefix, null);

            //修改数据库中对应的fileUrl信息
            StudentInfo studentInfo = studentOaService.findStudentTasksByStudentId(studentId);
            List studentTaskList = studentInfo.getStudentTaskList();
            for (Object o : studentTaskList) {
                JSONObject jsonObject = (JSONObject) o;
                if (jsonObject.get("id").equals(taskId)) {
                    //将图片的地址写到数据库中
                    jsonObject.put("fileUrl", IMG_URL + fileUrl);
                    break;
                }
            }
            String json = JSONUtils.toJSONString(studentTaskList);
            StudentOa one = new StudentOa();
            BeanUtils.copyProperties(studentInfo, one);
            one.setTask_list(json);
            studentOaService.save(one);//更新数据库中的信息

            //将图片存到服务器的物理路径上
            FileUtils.makeDirectory(new File(ALL_FILE_PATH + "/" + one.getClassId() + "/" + taskId + "/" + one.getStudent_id() + one.getStudent_name() + "." + fileSuffix));
            FileOutputStream outputStream = new FileOutputStream(new File(ALL_FILE_PATH + "/" + one.getClassId() + "/" + taskId + "/" + one.getStudent_id() + one.getStudent_name() + "." + fileSuffix));
            IOUtils.copy(in1, outputStream);
            //关流
            IOUtils.closeQuietly(in1);
            IOUtils.closeQuietly(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化fastDFS的连接
     */
    private void initFastDFSConfig() {
        try {
            ClientGlobal.initByProperties("fastdfs-client.properties");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理word文件
     * 将文件保存到服务器的地址上，且将pdf也保存到服务器的nginx上
     *
     * @param in1
     * @param in2
     * @param studentId  student的id
     * @param fileSuffix 文件名称后缀
     */
//    @Async
    public void uploadWORDFileChild(InputStream in1, InputStream in2, String studentId, String fileSuffix, String taskId) {
        StudentOa one = studentOaService.getOne(studentId);
        String fileName = one.getStudent_id() + one.getStudent_name();
        String classId = one.getClassId();

        try {
            //将文件存储在服务器的物理路径上
            FileUtils.makeDirectory(new File(ALL_FILE_PATH + "/" + classId + "/" + taskId + "/" + fileName + "." + fileSuffix));
            FileOutputStream outputStream = new FileOutputStream(new File(ALL_FILE_PATH + "/" + classId + "/" + taskId + "/" + fileName + "." + fileSuffix));
            IOUtils.copy(in1, outputStream);
            //关流
            IOUtils.closeQuietly(in1);
            IOUtils.closeQuietly(outputStream);
            //将文件转成pdf并保存到服务器的物理地址上
            //服务器的 ip地址+班级id+任务id+学生学号+学生姓名+.pdf
            Word2PdfUtil.word2Pdf(in2, PDF_PATH + one.getClassId() + "-" + taskId + "-" + fileName + ".pdf");
            //关流
            IOUtils.closeQuietly(in2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理zip文件
     *
     * @param inputStream
     * @param studentId
     * @param taskId
     */
    public void uploadZip(InputStream inputStream, String studentId, String taskId) {
        try {
            StudentOa one = studentOaService.getOne(studentId);
            FileUtils.makeDirectory(new File(ALL_FILE_PATH + "/" + one.getClassId() + "/" + taskId + "/" + one.getStudent_id() + one.getStudent_name() + "." + "zip"));
            FileOutputStream outputStream = new FileOutputStream(new File(ALL_FILE_PATH + "/" + one.getClassId() + "/" + taskId + "/" + one.getStudent_id() + one.getStudent_name() + "." + "zip"));
            IOUtils.copy(inputStream, outputStream);
            //关流
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public ResponseResult uploadFileByExcel(MultipartFile file, String classId) {

        int studentIdExcelIndex = -1;//学号的index
        int studentNameExcelIndex = -1;//姓名的index
        int accountNameExcelIndex = -1;//账号的index
        int passwordExcelIndex = -1;//密码的index
        int qqExcelIndex = -1;//qq的index

        int checkIndex = 0;//判断数据是否齐全

        //处理上传过来的excel表格

        try {
            //1:创建workbook
            Workbook workbook = Workbook.getWorkbook(file.getInputStream());
            //2:获取第一个工作表sheet
            Sheet sheet = workbook.getSheet(0);

            for (int i = 0; i < sheet.getColumns(); i++) {
                Cell cell = sheet.getCell(i, 0);
                if (cell.getContents().equals("学号")) {
                    studentIdExcelIndex = i;
                    checkIndex++;
                } else if (cell.getContents().equals("姓名")) {
                    studentNameExcelIndex = i;
                    checkIndex++;
                } else if (cell.getContents().equals("账号")) {
                    accountNameExcelIndex = i;
                    checkIndex++;
                } else if (cell.getContents().equals("密码")) {
                    passwordExcelIndex = i;
                    checkIndex++;
                } else if (cell.getContents().contains("qq") || cell.getContents().contains("QQ")) {
                    qqExcelIndex = i;
                }
            }

            if (checkIndex < 4) {
                ResponseResult responseResult = new ResponseResult();
                responseResult.setSuccess(false);
                responseResult.setMessage("Excel中数据格式错误");
            }

            List<StudentOa> list = new ArrayList<>();

            for (int i = 1; i < sheet.getRows(); i++) {//行  从第二行开始遍历

                StudentOa studentOa = new StudentOa();

                for (int j = 0; j < sheet.getColumns(); j++) {//列
                    Cell cell = sheet.getCell(j, i);//列 行

                    if (this.numEqNum(studentIdExcelIndex, j)) {
                        studentOa.setStudent_id(cell.getContents().trim());
                    }

                    if (this.numEqNum(studentNameExcelIndex, j)) {
                        studentOa.setStudent_name(cell.getContents().trim());
                    }
                    if (this.numEqNum(accountNameExcelIndex, j)) {
                        studentOa.setAccountName(cell.getContents().trim());
                    }
                    if (this.numEqNum(passwordExcelIndex, j)) {
                        studentOa.setPassword(cell.getContents().trim());
                    }
                    if (qqExcelIndex != -1 && this.numEqNum(qqExcelIndex, j)) {
                        studentOa.setQq(cell.getContents().trim());
                    }
                    studentOa.setClassId(classId);

                }
                studentOa.setIsFirst(1);
                list.add(studentOa);
            }

            studentOaService.saveList(list);

            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ResponseResult responseResult = new ResponseResult();
        responseResult.setSuccess(true);
        responseResult.setMessage("数据导入成功");

        return responseResult;
    }


    private boolean numEqNum(int num1, int num2) {
        return num1 == num2;
    }

    /**
     * 里面要放文件名称和inputSteam
     *
     * @param classId
     * @return
     */
    public Map exportStudentInfoInExcel(String classId, String teacherId,String selectData) {
        Map map = new HashMap();

        List<StudentInfo> list = studentOaService.findStudentListByClassId(classId, teacherId);
        Optional<ClassOa> optional = classOARepository.findById(classId);
        ClassOa classOa = optional.get();
        String filePath = CLASSINFO_FILE_EXCELPATH + "/" + classOa.getClass_name() + ".xls";
        this.CreateExcelFileInStudentInfo(filePath, classOa.getClass_name(), selectData, list, teacherId);

        try {
            FileInputStream inputStream = new FileInputStream(new File(filePath));
            map.put("inputStream", inputStream);
            map.put("fileName", classOa.getClass_name() + ".xls");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return map;
    }


    public ResponseResult teacherUploadReferencelFile(MultipartFile multipartFile) {

        ResponseResult requestResult = new ResponseResult();

        //得到文件的原始名称
        String originalFilename = multipartFile.getOriginalFilename();

        ObjectId store = null;
        try {
            InputStream inputStream = multipartFile.getInputStream();
            store = gridFsTemplate.store(inputStream, originalFilename);
            IOUtils.closeQuietly(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            requestResult.setMessage("上传失败");
            requestResult.setSuccess(false);
        }

        requestResult.setMessage("上传成功");
        requestResult.setSuccess(true);
        assert store != null;
        requestResult.setFileId(store.toString());
        requestResult.setFileName(originalFilename);
        return requestResult;
    }

    public GridFsResource downReferenceFileByFileId(String fileId) {
        //查询文件
        GridFSFile fsFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        //打开下载流对象
        assert fsFile != null;
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(fsFile.getObjectId());
        //创建GridFsResource 用于获取流对象
        return new GridFsResource(fsFile, gridFSDownloadStream);

    }

    public byte[] downLoadAllByClassIdAndTaskId(String classId, String taskId) {
        try {
            //创建文件夹
            FileUtils.makeDirectory(new File(ALL_FILE_PATH + classId + "/" + taskId+ "/test.txt"));
            //压缩
            byte[] zip = CompressedFileUtil.createZip(ALL_FILE_PATH + classId + "/" + taskId);
            if(zip!=null) return zip;
            else return new byte[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean CreateExcelFileInStudentInfo(String path, String className, String selectData, List<StudentInfo> list, String teacherId) {

        try {
            //1:创建excel文件
            File file = new File(path);
            file.createNewFile();

            //2:创建工作簿
            WritableWorkbook workbook = Workbook.createWorkbook(file);
            //3:创建sheet,设置第二三四..个sheet，依次类推即可
            WritableSheet sheet = workbook.createSheet(className, 0);

            //5:单元格
            Label label = null;

            List<String> titles = new ArrayList<>();
            char isWriteStudent_id = selectData.charAt(0);//是否写入学号
            char isWriteStudent_name = selectData.charAt(1);//是否写入姓名
            char isWriteTaskInfo = selectData.charAt(2);//是否写入作业信息
            char isWriteAccount_name = selectData.charAt(3);//是否写入账号
            char isWritePassword = selectData.charAt(4);//是否写入密码
            char isWriteQQ = selectData.charAt(5);//是否写入qq账号

            if (isWriteStudent_id == '1') {
                titles.add("学号");
            }
            if (isWriteStudent_name == '1') {
                titles.add("姓名");
            }
            if (isWriteAccount_name == '1') {
                titles.add("账号");
            }
            if (isWritePassword == '1') {
                titles.add("密码");
            }
            if (isWriteQQ == '1') {
                titles.add("qq");
            }

            //给list添加任务的名字
            if (isWriteTaskInfo == '1')
                for (StudentInfo studentInfo : list) {
                    String task_list = studentInfo.getTask_list();
                    List taskList = JSON.parseObject(task_list, List.class);
                    if (taskList != null && taskList.size() > 0) {
                        for (Object o : taskList) {
                            JSONObject jsonObject = (JSONObject) o;
                            String teacherID = (String) jsonObject.get("pushAuthor");
                            if (teacherID.equals(teacherId)) {
                                String taskName = (String) jsonObject.get("task_name");
                                //追加到titles
                                titles.add(taskName);
                            }
                        }
                        break;
                    }
                }

            //给第一行设置列名
            for (int i = 0; i < titles.size(); i++) {
                //x,y,第一行的列名
                label = new Label(i, 0, titles.get(i));
                //7：添加单元格
                sheet.addCell(label);
            }

            int row = 1;
            int column = 0;
            //从第二行开始填充数据
            for (StudentInfo studentInfo : list) {
                column = 0;//列
                if (isWriteStudent_id == '1') {
                    label = new Label(column++, row, studentInfo.getStudent_id());
                    sheet.addCell(label);
                }

                if (isWriteStudent_name == '1') {
                    label = new Label(column++, row, studentInfo.getStudent_name());
                    sheet.addCell(label);
                }

                if (isWriteAccount_name == '1') {
                    label = new Label(column++, row, studentInfo.getAccountName());
                    sheet.addCell(label);
                }

                if (isWritePassword == '1') {
                    label = new Label(column++, row, studentInfo.getPassword());
                    sheet.addCell(label);
                }

                if (isWriteQQ == '1') {
                    label = new Label(column++, row, studentInfo.getQq() == null ? "" : studentInfo.getQq());
                    sheet.addCell(label);
                }
                if (isWriteTaskInfo == '1') {
                    String task_list = studentInfo.getTask_list();
                    List taskList = JSON.parseObject(task_list, List.class);
                    if (taskList != null && taskList.size() > 0) {
                        for (Object o : taskList) {
                            JSONObject jsonObject = (JSONObject) o;
                            String teacherID = (String) jsonObject.get("pushAuthor");//判断和老师名字是否一致   一致就给第一行的列追加任务名
                            if (teacherID.equals(teacherId)) {
                                Boolean submit = (Boolean) jsonObject.get("submit");
                                String score = (String) jsonObject.get("score");
                                //拿出任务名字 和 isSubmit  如果提交了的就在对应的行列写上已提交，否者未提交
                                label = new Label(column++, row, (submit ? (score == null || score.equals("") ? "已提交" : score) : (score == null || score.equals("") ? "未提交" : score)));
                                sheet.addCell(label);
                            }
                        }
                    }
                }
                row++;
            }

            //写入数据，一定记得写入数据，不然你都开始怀疑世界了，excel里面啥都没有
            workbook.write();
            //最后一步，关闭工作簿
            workbook.close();
        } catch (IOException | WriteException e) {
            e.printStackTrace();
        }
        return true;
    }
}
