package com.zsh.teach_oa.api;

import com.zsh.teach_oa.ext.LoginMessage;
import com.zsh.teach_oa.ext.ResponseResult;
import com.zsh.teach_oa.ext.StudentInfo;
import com.zsh.teach_oa.model.ClassOa;
import com.zsh.teach_oa.model.TaskOa;
import com.zsh.teach_oa.model.TeacherOa;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

@Api(value = "教师管理")
public interface TeacherOaControllerApi {

    @ApiOperation("登录功能(账号密码登录)")
    LoginMessage login(String account_name, String password);

    @ApiOperation("发布任务，浏览器需要带上cookie token")
    ResponseResult pushTask(TaskOa taskOa, String token_teacherId);

    @ApiOperation("查看所有任务或者根据任务名字查找")
    List<TaskOa> getTaskOaByTaskName(String teacherId, String name);

    @ApiOperation("返回当前班级学生的信息以及对应的任务id对应的任务信息")
    List<StudentInfo> getStudentInfoByClassIdAndTaskId(String classId, String taskId);

    @ApiOperation("根据id查teacherOa")
    TeacherOa getTeacherOa(String id);

    @ApiOperation("根据教师id查对应关联的班级")
    List<ClassOa> findClassOasByTeacherId(String teacherId);

    @ApiOperation("关联班级")
    ResponseResult associationClass(String classIds, String teacherId);

    @ApiOperation("取消关联班级")
    ResponseResult cancelAssociationClass(String classIds, String teacherId);

    @ApiOperation("查询未关联的所有班级")
    List<ClassOa> findClassOaByNoAssociationToTeacher(String teacherId);

    @ApiOperation("查看关联的所有班级")
    List<ClassOa> findClassByAssociation(String teacherId);

    @ApiOperation("注册功能")
    ResponseResult register(TeacherOa teacherOa);

    @ApiOperation("根据任务id删除任务")
    ResponseResult deleteTaskById(String taskId, String teacherId);

    @ApiOperation("修改任务")
    ResponseResult updateTask(TaskOa taskOa, String teacherId);

    @ApiOperation("条件查询作业")
    List<TaskOa> searchTask(TaskOa taskOa, String teacherId);

    @ApiOperation("给学生打分数")
    ResponseResult evaluationLevel(String teacherId, String studentId, String taskId, String score);

    @ApiOperation("判断是否为新手,设置这个功能是为了提供新手教程")
    Boolean determineTeacherIsFirst(String teacherId);

    @ApiOperation("更改用户的状态，新用户-->普通用户")
    void changeTeacherIsFirst(String teacherId);
}
