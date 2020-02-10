package com.zsh.teach_oa.controller;

import com.zsh.teach_oa.api.StudentOaControllerApi;
import com.zsh.teach_oa.ext.LoginMessage;
import com.zsh.teach_oa.ext.ResponseResult;
import com.zsh.teach_oa.ext.StudentInfo;
import com.zsh.teach_oa.model.StudentOa;
import com.zsh.teach_oa.service.StudentOaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentOaController implements StudentOaControllerApi {


    @Autowired
    private StudentOaService studentOaService;


    /**
     * 根据学生id查相关的所有作业信息
     * @param id
     * @return
     */
    @Override
    @GetMapping("/findTaskByStudentId/{studentId}")
    public StudentInfo findStudentTasksByStudentId(@PathVariable("studentId") String id) {
        return studentOaService.findStudentTasksByStudentId(id);
    }

    @Override
    @GetMapping("/getStudent/{tokenId}")
    public StudentOa getStudentOaById(@PathVariable("tokenId") String id) {
        return studentOaService.getOne(id);
    }

    @Override
    @PostMapping("/cleanStudentList/{classId}")
    public ResponseResult cleanStudentListByClassId(@PathVariable("classId") String classId) {
        return studentOaService.cleanStudentListByClassId(classId);
    }

    @Override
    @GetMapping("/getStudentTaskInfo/{studentId}")
    public StudentInfo getStudentTaskInfoByClassIdAndTeacherId(
            @PathVariable("studentId") String studentId, @CookieValue("token_teacherId") String teacherId) {
        return studentOaService.getStudentTaskInfoByClassIdAndTeacherId(studentId, teacherId);
    }

    @Override
    @GetMapping("/determineIsFirst")
    public Boolean determineStudentIsFirst(@CookieValue("token_studentId") String studentId) {
        return studentOaService.determineStudentIsFirst(studentId);
    }

    @Override
    @PostMapping("/changeIsFirst")
    public void changeStudentIsFirst(@CookieValue("token_studentId") String studentId) {
        studentOaService.changeStudentIsFirst(studentId);
    }


    /**
     * 登录
     *
     * @param account_name
     * @param password
     * @return
     */
    @Override
    @PostMapping("/login")
    public LoginMessage login(@RequestParam(name = "username") String account_name,
                              @RequestParam(name = "password") String password) {

        LoginMessage loginMessage = new LoginMessage();
        StudentOa one = studentOaService.getOne(account_name, password);
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


    /**
     * 根据班级id查所有学生
     *
     * @param classId
     * @return
     */
    @Override
    @GetMapping("/findList/{classId}")
    public List<StudentInfo> findStudentListByClassId(@PathVariable("classId") String classId,
                                                      @CookieValue("token_teacherId") String teacherId) {
        return studentOaService.findStudentListByClassId(classId, teacherId);
    }

}
