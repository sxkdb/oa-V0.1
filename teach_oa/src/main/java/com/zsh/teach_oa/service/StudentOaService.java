package com.zsh.teach_oa.service;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zsh.teach_oa.dao.StudentOARepository;
import com.zsh.teach_oa.dao.TeacherOARepository;
import com.zsh.teach_oa.ext.ResponseResult;
import com.zsh.teach_oa.ext.StudentInfo;
import com.zsh.teach_oa.ext.StudentTask;
import com.zsh.teach_oa.model.StudentOa;
import com.zsh.teach_oa.model.TaskOa;
import com.zsh.teach_oa.model.TeacherOa;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StudentOaService {

    @Autowired
    private StudentOARepository studentOARepository;

    @Autowired
    private TeacherOARepository teacherOARepository;

    public List<StudentInfo> findStudentListByClassId(String classId, String teacherId) {

        List<StudentInfo> studentInfos = new ArrayList<>();

        if (StringUtils.isEmpty(classId)) return null;

        List<StudentOa> list = studentOARepository.findByClassId(classId);
        //处理得到的学生数据
        for (StudentOa studentOa : list) {
            StudentInfo studentInfo = new StudentInfo();

            String task_list = studentOa.getTask_list();
            List taskList = JSON.parseObject(task_list, List.class);

            BeanUtils.copyProperties(studentOa, studentInfo);//拷贝所有属性
            studentInfo.setStudentTaskList(taskList);//设置作业列表
            studentInfo.setTotal(taskList == null ? 0 : taskList.size());//总作业数

            int completeTotal = 0;
            int UnfinishedTotal = 0;

            //统计任务完成次数
            if (taskList != null && taskList.size() != 0) {
                for (Object o : taskList) {
                    JSONObject jsonObject = (JSONObject) o;
                    if (jsonObject.get("pushAuthor").equals(teacherId)) {//在
                        Boolean submit = (Boolean) jsonObject.get("submit");
                        if (submit) {
                            completeTotal++;
                        } else {
                            UnfinishedTotal++;
                        }
                    }
                }
            }

            studentInfo.setCompleteTotal(completeTotal);//完成数量
            studentInfo.setUnfinishedTotal(UnfinishedTotal);//未完成数量

            studentInfos.add(studentInfo);
        }
        //TODO 校验classId在classOa表中是否存在
        return studentInfos;
    }

    public boolean updateStudentById(String studentId, TaskOa taskOa) {
        List<TaskOa> taskOaList = new ArrayList<>();

        Optional<StudentOa> optional = studentOARepository.findById(studentId);
        if (optional.isPresent()) {
            StudentOa studentOa = optional.get();
            taskOaList.add(taskOa);
            String jsonString = JSONUtils.toJSONString(taskOaList);
            studentOa.setTask_list(jsonString);

            studentOARepository.save(studentOa);
        }

        return true;
    }

    /**
     * 修改studentOa表中的任务栏
     *
     * @param studentOa
     * @param taskOa
     * @return
     */
    @Transactional
    public boolean updateStudentTaskList(StudentOa studentOa, TaskOa taskOa) {

        List studentTasks = null;

        String task_list = studentOa.getTask_list();
        if (!StringUtils.isEmpty(task_list)) {
            studentTasks = JSON.parseObject(task_list, List.class);
        } else {
            studentTasks = new ArrayList();
        }

        StudentTask studentTask = new StudentTask();

        studentTask.setSubmit(false);
        studentTask.setFileInfo("");
        studentTask.setFileUrl("");


        BeanUtils.copyProperties(taskOa, studentTask);
        String teacherId = taskOa.getPushAuthor();
        Optional<TeacherOa> optional = teacherOARepository.findById(teacherId);

        //发布者的名字
        if (optional.isPresent()) {
            TeacherOa teacherOa = optional.get();
            studentTask.setPushAuthorName(teacherOa.getTeacher_name());
        }

        studentTasks.add(studentTask);

        String string = JSON.toJSONString(studentTasks);

        studentOa.setTask_list(string);

        studentOARepository.save(studentOa);

        return true;
    }

    public StudentOa getOne(String account_name, String password) {
        //TODO 判断参数是否合法
        return studentOARepository.findByAccountNameAndPassword(account_name, password);

    }

    public StudentInfo findStudentTasksByStudentId(String id) {

        StudentInfo studentInfo = new StudentInfo();

        Optional<StudentOa> optional = studentOARepository.findById(id);
        if (!optional.isPresent()) return null;
        StudentOa studentOa = optional.get();

        String task_list = studentOa.getTask_list();
        List<StudentTask> list = JSON.parseObject(task_list, List.class);

        studentInfo.setStudentTaskList(list);//设置作业列表
        BeanUtils.copyProperties(studentOa, studentInfo);//拷贝所有属性

        int completeTotal = 0;
        int UnfinishedTotal = 0;
        //统计作业完成情况 次数
        for (Object o : list) {
            JSONObject jsonObject = (JSONObject) o;
            Boolean submit = (Boolean) jsonObject.get("submit");
            if (submit) {
                completeTotal++;
            } else {
                UnfinishedTotal++;
            }
        }

        studentInfo.setCompleteTotal(completeTotal);//完成数量
        studentInfo.setUnfinishedTotal(UnfinishedTotal);//未完成数量

        return studentInfo;
    }

    public StudentOa getOne(String id) {
        Optional<StudentOa> optional = studentOARepository.findById(id);
        return optional.orElse(null);
    }

    @Transactional
    public void save(StudentOa studentOa) {
        studentOARepository.save(studentOa);
    }

    @Transactional
    public void saveList(List<StudentOa> list) {
        for (StudentOa studentOa : list) {
            //TODO 这里需要判断保存的学生数据是否在数据库中已经存在了
            studentOARepository.save(studentOa);
        }
    }

    @Transactional
    public ResponseResult cleanStudentListByClassId(String classId) {

        int i = studentOARepository.deleteAllByClassId(classId);
        System.out.println(i);

        ResponseResult responseResult = new ResponseResult();

        responseResult.setMessage("删除成功");
        responseResult.setSuccess(true);

        return responseResult;
    }

    public StudentInfo getStudentTaskInfoByClassIdAndTeacherId(String studentId, String teacherId) {
        StudentInfo studentInfo = new StudentInfo();
        List<StudentTask> studentTaskList = new ArrayList<>();
        Optional<StudentOa> optional = studentOARepository.findById(studentId);
        if (optional.isPresent()) {
            StudentOa studentOa = optional.get();
            BeanUtils.copyProperties(studentOa, studentInfo);
            String task_list = studentOa.getTask_list();
            if (task_list != null && !task_list.equals("")) {
                List list = JSON.parseObject(task_list, List.class);
                if (list != null && list.size() != 0) {
                    for (Object o : list) {
                        JSONObject jsonObject = (JSONObject) o;
                        if (jsonObject.get("pushAuthor").equals(teacherId)) {
                            StudentTask task = new StudentTask();
                            boolean submit = (boolean) jsonObject.get("submit");//是否已经提交
                            String task_name = (String) jsonObject.get("task_name");//任务名称
                            String fileUrl = (String) jsonObject.get("fileUrl");//文件访问的url
                            String fileName = (String) jsonObject.get("fileName");//文件原来的名字
                            String fileId = (String) jsonObject.get("fileId");//文件id(如果上传过的话)
                            String fileSuffix = (String) jsonObject.get("fileSuffix");//文件id(如果上传过的话)

                            task.setSubmit(submit);
                            task.setTask_name(task_name);
                            task.setFileUrl(fileUrl);
                            task.setFileName(fileName);
                            task.setFileId(fileId);
                            task.setFileSuffix(fileSuffix);

                            studentTaskList.add(task);
                        }
                    }

                }
            }
        }
        studentInfo.setStudentTaskList(studentTaskList);
        return studentInfo;
    }

    public Boolean determineStudentIsFirst(String studentId) {
        StudentOa one = getOne(studentId);
        int isFirst = one.getIsFirst();
        if(isFirst == 1){//是新手
            return true;
        }
        return false;
    }

    public void changeStudentIsFirst(String studentId) {
        StudentOa one = getOne(studentId);
        one.setIsFirst(0);
        save(one);
    }
}