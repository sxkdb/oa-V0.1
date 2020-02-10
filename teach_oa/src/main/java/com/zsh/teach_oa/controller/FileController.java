package com.zsh.teach_oa.controller;

import com.zsh.teach_oa.api.FileControllerApi;
import com.zsh.teach_oa.ext.ResponseResult;
import com.zsh.teach_oa.model.StudentOa;
import com.zsh.teach_oa.service.FileService;
import com.zsh.teach_oa.service.StudentOaService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileController implements FileControllerApi {

    @Autowired
    private FileService fileService;

    @Autowired
    private StudentOaService studentOaService;


    /**
     * 上传文件
     *
     * @param file
     * @param studentId
     * @param taskId
     * @param fileType
     * @return
     */
    @Override
    @PostMapping("/uploadFile")
    public ResponseResult uploadFile(@RequestParam("file") MultipartFile file,
                                     @CookieValue("token_studentId") String studentId,
                                     String taskId,
                                     String fileType) {

        ResponseResult result = fileService.upload(file, studentId, taskId, fileType);

        //得到文件的原始名称
        String originalFilename = file.getOriginalFilename();
        //获取文件名（没有后缀）
        String fileName = originalFilename.substring(0, originalFilename.indexOf("."));
        //获取文件后缀
        String fileSuffix = originalFilename.substring(originalFilename.indexOf(".") + 1);

        switch (fileSuffix) {
            case "doc":
            case "docx":
                try {
                    fileService.uploadWORDFileChild(file.getInputStream(),
                            file.getInputStream(), studentId, fileSuffix, taskId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "png":
            case "jpg":
            case "PNG":
            case "JPG":
                try {
                    fileService.uploadFastDFS(file, studentId, taskId, fileSuffix);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "pdf":
                try {
                    fileService.uploadPDF(file.getInputStream(), file.getInputStream(), studentId, taskId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "zip":
                try {
                    fileService.uploadZip(file.getInputStream(), studentId, taskId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }

//        //异步出来上传的word文件  转换为pdf储存到nginx目录上和将原文件储存到磁盘上，以便未来压缩文件十用
//        if (fileSuffix.equals("doc") || fileSuffix.equals("docx")) {
//            try {
//                fileService.uploadWORDFileChild(file.getInputStream(),
//                        file.getInputStream(), studentId, fileSuffix, taskId);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        //如果文件类型的图片，将图片存到fastDF上，方便查看
//        if (fileSuffix.equals("png") || fileSuffix.equals("jpg") || fileSuffix.equals("PNG") || fileSuffix.equals("JPG")) {
//            fileService.uploadFastDFS(file, studentId, taskId, fileSuffix);
//        }
//        //如果文件后缀是pdf
//        if (fileSuffix.equals("pdf")) {
//            try {
//                fileService.uploadPDF(file.getInputStream(), file.getInputStream(), studentId, taskId);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        //如果文件后缀是zip
//        if (fileSuffix.equals("zip")) {
//            try {
//                fileService.uploadZip(file.getInputStream(), studentId, taskId);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        return result;
    }

    @Override
    @GetMapping("/studentDownloadFile/{fileId}")
    public ResponseResult studentDownloadFile(HttpServletResponse response, HttpServletRequest request, @PathVariable("fileId") String fileId) {
        response.setContentType("application/force-download");// 设置强制下载不打开

        GridFsResource gridFsResource = fileService.downReferenceFileByFileId(fileId);
        String filename = gridFsResource.getFilename();

        //支持火狐和谷歌
        String userAgent = request.getHeader("User-Agent");
        byte[] bytes = userAgent.contains("MSIE") ? filename.getBytes() : filename.getBytes(StandardCharsets.UTF_8);
        filename = new String(bytes, StandardCharsets.ISO_8859_1);
        response.addHeader("Content-Disposition", "attachment;fileName=" + filename);
        try {
            InputStream input = gridFsResource.getInputStream();
            ServletOutputStream outputStream = response.getOutputStream();
            IOUtils.copy(input, outputStream);
            //关流
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    @PostMapping("/uploadFileByExcel")
    public ResponseResult uploadFileByExcel(MultipartFile file, String classId) {
        return fileService.uploadFileByExcel(file, classId);

    }

    /**
     * 导出excel表格
     *
     * @param response
     * @param request
     * @param classId
     */
    @Override
    @GetMapping("/exportStudentInfo/{selectData}/{classId}")
    public void exportStudentInfoInExcel(HttpServletResponse response, HttpServletRequest request, @PathVariable("classId") String classId,
                                         @CookieValue("token_teacherId") String teacherId, @PathVariable("selectData") String selectData) {
        response.setContentType("application/force-download");// 设置强制下载不打开
        Map map = fileService.exportStudentInfoInExcel(classId, teacherId, selectData);
        InputStream inputStream = (InputStream) map.get("inputStream");
        String fileName = (String) map.get("fileName");

        String userAgent = request.getHeader("User-Agent");
        byte[] bytes = userAgent.contains("MSIE") ? fileName.getBytes() : fileName.getBytes(StandardCharsets.UTF_8);
        fileName = new String(bytes, StandardCharsets.ISO_8859_1);
        response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
        inputCopyResponseInput(response, inputStream);

    }

    /**
     * 下载文件
     *
     * @param response
     * @param request
     * @param studentId
     * @param fileId
     * @param fileSuffix
     */
    @Override
    @GetMapping("/downLoad/{studentId}/{fileId}/{fileSuffix}")
    public void downLoadFileByFileId(HttpServletResponse response, HttpServletRequest request,
                                     @PathVariable("studentId") String studentId,
                                     @PathVariable("fileId") String fileId,
                                     @PathVariable("fileSuffix") String fileSuffix) {
        response.setContentType("application/force-download");// 设置强制下载不打开
        StudentOa one = studentOaService.getOne(studentId);
        String filename = one.getStudent_id() + one.getStudent_name() + "." + fileSuffix;

        //支持火狐和谷歌
        String userAgent = request.getHeader("User-Agent");
        byte[] bytes = userAgent.contains("MSIE") ? filename.getBytes() : filename.getBytes(StandardCharsets.UTF_8);
        filename = new String(bytes, StandardCharsets.ISO_8859_1);

        response.addHeader("Content-Disposition", "attachment;fileName=" + filename);
        InputStream input = fileService.downFileByFileId(fileId);
        inputCopyResponseInput(response, input);
    }

    private void inputCopyResponseInput(HttpServletResponse response, InputStream input) {
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            IOUtils.copy(input, outputStream);
            //关流
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    @GetMapping("/downLoadAll/{classId}/{taskId}")
    public void downLoadAllByClassIdAndTaskId(HttpServletResponse response, HttpServletRequest request,
                                              @PathVariable("classId") String classId,
                                              @PathVariable("taskId") String taskId) {
        response.setContentType("application/force-download");// 设置强制下载不打开
        byte[] data = fileService.downLoadAllByClassIdAndTaskId(classId, taskId);
        try {
            OutputStream out = response.getOutputStream();
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.addHeader("Content-Length", "" + data.length);
            response.reset();
            IOUtils.write(data, out);
            out.flush();
            IOUtils.closeQuietly(out);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    @PostMapping("/uploadReferencelFile")
    public ResponseResult teacherUploadReferencelFile(@RequestParam("file") MultipartFile file) {
        return fileService.teacherUploadReferencelFile(file);
    }

}
