package com.zsh.teach_oa.controller;

import com.zsh.teach_oa.api.TeacherOaControllerApi;
import com.zsh.teach_oa.ext.LoginMessage;
import com.zsh.teach_oa.ext.ResponseResult;
import com.zsh.teach_oa.ext.StudentInfo;
import com.zsh.teach_oa.model.ClassOa;
import com.zsh.teach_oa.model.TaskOa;
import com.zsh.teach_oa.model.TeacherOa;
import com.zsh.teach_oa.service.TaskOaService;
import com.zsh.teach_oa.service.TeacherOaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teacher")
public class TeacherOaController implements TeacherOaControllerApi {

    @Autowired
    private TeacherOaService teacherOaService;

    @Autowired
    private TaskOaService taskOaService;


    /**
     * 根据班级id和任务id查看学生信息，studentTaskList中只根据任务id放一个数据
     *
     * @param classId
     * @param taskId
     * @return
     */
    @Override
    @GetMapping("/getStudentInfos")
    public List<StudentInfo> getStudentInfoByClassIdAndTaskId(@RequestParam("classId") String classId, @RequestParam("taskId") String taskId) {
        return teacherOaService.getStudentInfoByClassIdAndTaskId(classId, taskId);
    }

    @Override
    @GetMapping("/getTeacher/{tokenId}")
    public TeacherOa getTeacherOa(@PathVariable("tokenId") String id) {
        return teacherOaService.getOne(id);
    }

    //废弃
    @Override
    @GetMapping("/findClass")
    public List<ClassOa> findClassOasByTeacherId(@CookieValue("token_teacherId") String teacherId) {
        return teacherOaService.findClassOasByTeacherId(teacherId);
    }

    @Override
    @PostMapping("/associationClass")
    public ResponseResult associationClass(String classIds,
                                           @CookieValue("token_teacherId") String teacherId) {
        return teacherOaService.associationClass(classIds, teacherId);
    }

    @Override
    @PostMapping("/cancelAssociationClass")
    public ResponseResult cancelAssociationClass(String classIds,
                                                 @CookieValue("token_teacherId") String teacherId) {
        return teacherOaService.cancelAssociationClass(classIds, teacherId);
    }

    @Override
    @GetMapping("/findClassByNoAssociation")
    public List<ClassOa> findClassOaByNoAssociationToTeacher(@CookieValue("token_teacherId") String teacherId) {
        return teacherOaService.findClassOaByNoAssociationToTeacher(teacherId);
    }

    @Override
    @GetMapping("/findClassByAssociation")
    public List<ClassOa> findClassByAssociation(@CookieValue("token_teacherId") String teacherId) {
        return teacherOaService.findClassByAssociation(teacherId);
    }

    @Override
    @PostMapping("/register")
    public ResponseResult register(TeacherOa teacherOa) {
        return teacherOaService.register(teacherOa);
    }

    /**
     * 根据任务id删除任务
     *
     * @param taskId
     * @param teacherId
     * @return
     */
    @Override
    @PostMapping("/deleteTaskById/{taskId}")
    public ResponseResult deleteTaskById(@PathVariable("taskId") String taskId, String teacherId) {
        return taskOaService.deleteTaskById(taskId, teacherId);
    }

    @Override
    @PostMapping("/updateTask")
    public ResponseResult updateTask(TaskOa taskOa, @CookieValue("token_teacherId") String teacherId) {
        return taskOaService.updateTask(taskOa, teacherId);
    }

    /**
     * 条件模糊查询
     *
     * @param taskOa
     * @param teacherId
     * @return
     */
    @Override
    @GetMapping("/searchTask")
    public List<TaskOa> searchTask(TaskOa taskOa, @CookieValue("token_teacherId") String teacherId) {
        return taskOaService.searchTask(taskOa, teacherId);
    }

    @Override
    @PostMapping("/evaluationLevel/{studentId}/{taskId}/{score}")
    public ResponseResult evaluationLevel(@CookieValue("token_teacherId") String teacherId, @PathVariable("studentId") String studentId,
                                          @PathVariable("taskId") String taskId, @PathVariable("score") String score) {
        return taskOaService.evaluationLevel(teacherId,studentId,taskId,score);
    }

    @Override
    @GetMapping("/determineIsFirst")
    public Boolean determineTeacherIsFirst(@CookieValue("token_teacherId") String teacherId) {
        return teacherOaService.determineTeacherIsFirst(teacherId);
    }

    @Override
    @PostMapping("/changeIsFirst")
    public void changeTeacherIsFirst(@CookieValue("token_teacherId") String teacherId) {
        teacherOaService.changeTeacherIsFirst(teacherId);
    }

    /**
     * 发布任务
     *
     * @param taskOa          任务
     * @param token_teacherId 教师id
     * @return
     */
    @Override
    @PostMapping("/pushTask")
    public ResponseResult pushTask(TaskOa taskOa,
                                   @CookieValue("token_teacherId") String token_teacherId) {
        return taskOaService.pushTask(taskOa, token_teacherId);
    }

    /**
     * 查看任务
     *
     * @param teacherId 根据身份查所有任务
     * @param name      可以根据名字查
     * @return
     */
    @Override
    @GetMapping("/findTask")
    public List<TaskOa> getTaskOaByTaskName(@CookieValue("token_teacherId") String teacherId,
                                            @RequestParam(value = "tackName", required = false) String name) {
        return taskOaService.getTaskOaByTaskName(teacherId, name);
    }


    @Override
    @PostMapping("/login")
    public LoginMessage login(@RequestParam(name = "username") String account_name,
                              @RequestParam(name = "password") String password) {
        LoginMessage loginMessage = new LoginMessage();
        TeacherOa one = teacherOaService.getOne(account_name, password);
        if (one != null && one.getAccountName() != null && one.getPassword() != null) {
            //登录成功
            loginMessage.setStatus("200");
            loginMessage.setCode("200");
            loginMessage.setMessage("登录成功");
            loginMessage.setToken(one.getId());
        } else {
            //登录失败
            loginMessage.setStatus("200");
            loginMessage.setCode("500");
            loginMessage.setMessage("登录失败");
        }
        return loginMessage;
    }

}
