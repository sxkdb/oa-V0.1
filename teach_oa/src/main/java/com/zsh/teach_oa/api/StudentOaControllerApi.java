package com.zsh.teach_oa.api;

import com.zsh.teach_oa.ext.LoginMessage;
import com.zsh.teach_oa.ext.ResponseResult;
import com.zsh.teach_oa.ext.StudentInfo;
import com.zsh.teach_oa.model.StudentOa;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

@Api(value = "学生信息")
public interface StudentOaControllerApi {

    @ApiOperation("查找对应班级的学生,已经根据教师id把对应发布的任务量也统计出来")
    List<StudentInfo> findStudentListByClassId(String classId,String teacherId);

    @ApiOperation("登录功能(账号密码登录)")
    LoginMessage login(String account_name, String password);

    @ApiOperation("根据学生信息查相关的所有作业")
    StudentInfo findStudentTasksByStudentId(String id);

    @ApiOperation("根据id返回学生信息")
    StudentOa getStudentOaById(String id);

    @ApiOperation("根据classId清空学生数据")
    ResponseResult cleanStudentListByClassId(String classId);

    @ApiOperation("返回学生关于该教师id的所有任务情况")
    StudentInfo getStudentTaskInfoByClassIdAndTeacherId(String studentId,String teacherId);

    @ApiOperation("判断是否为新手,设置这个功能是为了提供新手教程")
    Boolean determineStudentIsFirst(String studentId);

    @ApiOperation("更改用户的状态，新用户-->普通用户")
    void changeStudentIsFirst(String studentId);
}
