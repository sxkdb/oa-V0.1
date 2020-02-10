package com.zsh.teach_oa.service;

import com.zsh.teach_oa.dao.ClassOARepository;
import com.zsh.teach_oa.dao.TeacherOARepository;
import com.zsh.teach_oa.ext.ResponseResult;
import com.zsh.teach_oa.model.ClassOa;
import com.zsh.teach_oa.model.TeacherOa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ClassOaService {

    @Autowired
    private ClassOARepository classOARepository;

    @Autowired
    private TeacherOaService teacherOaService;

    @Autowired
    private TeacherOARepository teacherOARepository;

    public List<ClassOa> getAll() {
        return classOARepository.findAll();
    }


    @Transactional
    public ResponseResult addClassOa(String teacherId, String className) {

        ResponseResult requestResult = new ResponseResult();

        //判断className是否已经存在
        ClassOa oa = new ClassOa();
        Example<ClassOa> example = Example.of(oa);
        oa.setClass_name(className);

        List<ClassOa> list = classOARepository.findAll(example);

        if (list.size() != 0) {
            requestResult.setMessage("该班级名称已经存在");
            requestResult.setSuccess(false);
            requestResult.setCode("500");
            requestResult.setStatus("200");
            return requestResult;
        }

        oa = classOARepository.save(oa);

        String id = oa.getId();

        TeacherOa one = teacherOaService.getOne(teacherId);
        String class_list = one.getClass_list();
        if (StringUtils.isEmpty(class_list)) {
            class_list = "";
        }
        class_list += "," + id;
        one.setClass_list(class_list);

        teacherOARepository.save(one);


        requestResult.setMessage("添加班级成功");
        requestResult.setSuccess(true);
        requestResult.setCode("200");
        requestResult.setStatus("200");

        return requestResult;
    }


}
