package com.zsh.teach_oa.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zsh.teach_oa.dao.ClassOARepository;
import com.zsh.teach_oa.dao.StudentOARepository;
import com.zsh.teach_oa.dao.TeacherOARepository;
import com.zsh.teach_oa.ext.ResponseResult;
import com.zsh.teach_oa.ext.StudentInfo;
import com.zsh.teach_oa.ext.StudentTask;
import com.zsh.teach_oa.model.ClassOa;
import com.zsh.teach_oa.model.StudentOa;
import com.zsh.teach_oa.model.TeacherOa;
import com.zsh.teach_oa.utils.ListUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TeacherOaService {

    @Autowired
    private TeacherOARepository teacherOARepository;

    @Autowired
    private StudentOARepository studentOARepository;

    @Autowired
    private ClassOARepository classOARepository;

    public List<StudentInfo> getStudentInfoByClassIdAndTaskId(String classId, String taskId) {

        List<StudentInfo> studentInfoList = new ArrayList<>();

        List<StudentOa> studentOas = studentOARepository.findByClassId(classId);
        for (StudentOa studentOa : studentOas) {
            StudentInfo studentInfo = new StudentInfo();

            BeanUtils.copyProperties(studentOa, studentInfo);
            studentInfo.setTask_list(null);

            StudentTask studentTask = null;
            String task_list = studentOa.getTask_list();
            List list = JSONObject.parseObject(task_list, List.class);
            for (Object o : list) {
                JSONObject jsonObject = (JSONObject) o;
                Object taskid = ((JSONObject) o).get("id");
                if (((String) taskid).equals(taskId)) {
                    String s = JSON.toJSONString(jsonObject);
                    studentTask = JSON.parseObject(s, StudentTask.class);
                    List<StudentTask> studentTasks = new ArrayList<>();
                    studentTasks.add(studentTask);
                    studentInfo.setStudentTaskList(studentTasks);
                    break;
                }
            }
            studentInfoList.add(studentInfo);
        }

        return studentInfoList;
    }


    public TeacherOa getOne(String account_name, String password) {
        //TODO 判断参数是否合法
        return teacherOARepository.findByAccountNameAndPassword(account_name, password);
    }

    public TeacherOa getOne(String id) {
        Optional<TeacherOa> optional = teacherOARepository.findById(id);
        return optional.orElse(null);
    }

    public List<ClassOa> findClassOasByTeacherId(String teacherId) {
        List<ClassOa> result = new ArrayList<>();
        TeacherOa one = this.getOne(teacherId);
        String class_list = one.getClass_list();
        if (class_list == null || class_list.trim().equals("")) {
            return new ArrayList<>();
        }
        String[] classIds = class_list.split(",");
        for (String classId : classIds) {
            Optional<ClassOa> optional = classOARepository.findById(classId);
            optional.ifPresent(result::add);
        }
        return result;

    }

    @Transactional
    public ResponseResult associationClass(String classIds, String teacherId) {
        ResponseResult result = new ResponseResult();
        TeacherOa one = teacherOARepository.getOne(teacherId);
        String class_list = one.getClass_list();
        //判断关联是否重复
        String[] newClassIds = classIds.split(".");
        for (String newClassId : newClassIds) {
            boolean contains = class_list.contains(newClassId);
            if (contains) {
                result.setMessage("关联班级失败，其中有已经关联的班级");
                result.setSuccess(false);
                return result;
            }
        }
        if (class_list == null) {
            one.setClass_list(classIds);
        } else {
            one.setClass_list(class_list + "," + classIds);
        }

        //执行保存
        teacherOARepository.save(one);

        result.setMessage("关联班级成功");
        result.setSuccess(true);


        return result;
    }

    public List<ClassOa> findClassOaByNoAssociationToTeacher(String teacherId) {

        List<ClassOa> all = classOARepository.findAll();

        List<ClassOa> removeList = new ArrayList<>();

        TeacherOa one = teacherOARepository.getOne(teacherId);

        String class_list = one.getClass_list();
        if (class_list != null && !class_list.trim().equals("")) {
            String[] classIds = class_list.split(",");
            for (String classId : classIds) {
                for (ClassOa classOa : all) {
                    if (classOa.getId().equals(classId)) {
                        removeList.add(classOa);
                    }
                }
            }
        }

        if (removeList.size() > 0) {
            all.removeAll(removeList);
        }
        all.sort(Comparator.comparing(ClassOa::getClass_name));
        return all;
    }

    /**
     * 根据teacherId查出关联了的班级
     *
     * @param teacherId
     * @return
     */
    public List<ClassOa> findClassByAssociation(String teacherId) {
        List<ClassOa> all = classOARepository.findAll();

        List<ClassOa> cancelAssociation = new ArrayList<>();

        TeacherOa one = teacherOARepository.getOne(teacherId);

        String class_list = one.getClass_list();
        if (class_list != null && !class_list.trim().equals("")) {
            String[] classIds = class_list.split(",");
            for (String classId : classIds) {
                for (ClassOa classOa : all) {
                    if (classOa.getId().equals(classId)) {
                        cancelAssociation.add(classOa);
                    }
                }
            }
        }
        cancelAssociation.sort(Comparator.comparing(ClassOa::getClass_name));
        return cancelAssociation;
    }

    @Transactional
    public ResponseResult cancelAssociationClass(String classIds, String teacherId) {
        ResponseResult result = new ResponseResult();
        TeacherOa one = teacherOARepository.getOne(teacherId);
        String class_list = one.getClass_list();//现在是要修改这个暑假

        String[] classListStr = class_list.split(",");
        String[] removeClassListStr = classIds.split(",");

        List<String> classList = new ArrayList<>(Arrays.asList(classListStr));
        List<String> removeClassList = new ArrayList<>(Arrays.asList(removeClassListStr));

        classList.removeAll(removeClassList);

        String s = ListUtils.listToString(classList);
        one.setClass_list(s);

        //执行保存
        teacherOARepository.save(one);

        result.setMessage("关联班级成功");
        result.setSuccess(true);
        return result;
    }

    @Transactional
    public ResponseResult register(TeacherOa teacherOa) {
        ResponseResult responseResult = new ResponseResult();
        if (teacherOa.getAccountName() == null || teacherOa.getAccountName().trim().equals("")
                || teacherOa.getTeacher_name() == null || teacherOa.getTeacher_name().trim().equals("")
                || teacherOa.getPassword() == null || teacherOa.getPassword().trim().equals("")) {
            responseResult.setSuccess(false);
            responseResult.setMessage("注册信息有误，请重新输入");
            return responseResult;
        }

        List<TeacherOa> list = teacherOARepository.findAllByAccountName(teacherOa.getAccountName());
        if (list.size() > 0) {
            responseResult.setSuccess(false);
            responseResult.setMessage("账号已被注册,请重新注册");
            return responseResult;
        }
        teacherOa.setIsFirst(1);
        teacherOARepository.save(teacherOa);
        responseResult.setSuccess(true);
        return responseResult;
    }

    public Boolean determineTeacherIsFirst(String teacherId) {
        TeacherOa one = getOne(teacherId);
        int isFirst = one.getIsFirst();
        if (isFirst == 1) {
            return true;
        }
        return false;
    }

    @Transactional
    public void changeTeacherIsFirst(String teacherId) {
        TeacherOa one = getOne(teacherId);
        int isFirst = one.getIsFirst();
        if (isFirst == 1) {
            one.setIsFirst(0);
            teacherOARepository.save(one);
        }
    }
}
