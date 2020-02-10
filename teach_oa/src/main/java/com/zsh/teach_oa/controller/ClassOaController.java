package com.zsh.teach_oa.controller;

import com.zsh.teach_oa.api.ClassOaControllerApi;
import com.zsh.teach_oa.ext.ResponseResult;
import com.zsh.teach_oa.model.ClassOa;
import com.zsh.teach_oa.service.ClassOaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/classOa")
public class ClassOaController implements ClassOaControllerApi {

    @Autowired
    private ClassOaService classOaService;

    //TODO 这里之后要加上教师id，返回的是对应教师的班级
    @Override
    @GetMapping("/getAll")
    public List<ClassOa> getAll() {
        return classOaService.getAll();
    }

    @Override
    @PostMapping("/addClassOa/{teacherId}/{className}")
    public ResponseResult addClassOa(@PathVariable("teacherId") String teacherId,
                                     @PathVariable("className") String className) {
        return classOaService.addClassOa(teacherId,className);
    }


}
