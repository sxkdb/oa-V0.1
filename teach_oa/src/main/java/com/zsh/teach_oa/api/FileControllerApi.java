package com.zsh.teach_oa.api;

import com.zsh.teach_oa.ext.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "文件管理")
public interface FileControllerApi {

    @ApiOperation("学生上传文件")
    ResponseResult uploadFile(MultipartFile file, String studentId, String taskId, String fileType);

    @ApiOperation("学生下载老师发布任务中的附件")
    ResponseResult studentDownloadFile(HttpServletResponse response, HttpServletRequest request, String fileId);

    @ApiOperation("老师上传Excel表格导入学生数据")
    ResponseResult uploadFileByExcel(MultipartFile file, String classId);

    @ApiOperation("老师导出学生数据到Excel表格")
    void exportStudentInfoInExcel(HttpServletResponse response, HttpServletRequest request, String classId,String teacherId,String selectData);

    @ApiOperation("教师文件下载(单个，根据文件id去mongodb中下载)")
    void downLoadFileByFileId(HttpServletResponse response, HttpServletRequest request, String studentId, String fileId, String fileSuffix);

    @ApiOperation("教师文件下载(多个，查到到文件的磁盘根目录然后去压缩这个根目录")
    void downLoadAllByClassIdAndTaskId(HttpServletResponse response, HttpServletRequest request, String classId, String taskId);

    @ApiOperation("教师上传参考文件，返回mongodb中任务的id，方便学生下载")
    ResponseResult teacherUploadReferencelFile(MultipartFile file);



}
