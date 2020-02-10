package com.zsh.teach_oa.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.zsh.teach_oa.dao.ClassOARepository;
import com.zsh.teach_oa.dao.StudentOARepository;
import com.zsh.teach_oa.dao.TaskOARepository;
import com.zsh.teach_oa.ext.ResponseResult;
import com.zsh.teach_oa.ext.StudentInfo;
import com.zsh.teach_oa.model.ClassOa;
import com.zsh.teach_oa.model.StudentOa;
import com.zsh.teach_oa.model.TaskOa;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class TaskOaService {

    @Autowired
    private TaskOARepository taskOARepository;

    @Autowired
    private ClassOARepository classOARepository;

    @Autowired
    private StudentOaService studentOaService;

    @Autowired
    private StudentOARepository studentOARepository;

    /**
     * 发布任务
     * 执行保存任务和保存对应的发布者
     *
     * @param taskOa
     * @param teacherId
     * @return
     */
    @Transactional
    public ResponseResult pushTask(TaskOa taskOa, String teacherId) {

        //TODO 判断参数是否合法  taskOa关键数据是否都有   teacherId是否存在(查数据库)

        if (taskOa.getReference_file_info() != null && taskOa.getReference_file_info().trim().equals("")) {
            taskOa.setReference_file_info(null);
        }
        taskOa.setCreateTime(new Date());//创建时间
        taskOa.setPushAuthor(teacherId);
        //执行保存taskOa
        taskOARepository.save(taskOa);

        //将发布的任务添加学生信息里面
        String[] classList = taskOa.getClass_id().split(",");
        for (String classId : classList) {
            List<StudentInfo> studentList = studentOaService.findStudentListByClassId(classId, teacherId);//这里可以不需要教师的id
            for (StudentInfo studentInfo : studentList) {
                StudentOa studentOa = new StudentOa();
                BeanUtils.copyProperties(studentInfo, studentOa);
                studentOaService.updateStudentTaskList(studentOa, taskOa);
            }
        }

        //将发布的任务id存到班级表里面
        for (String classId : classList) {
            //遍历班级列表 将任务id追加进去
            Optional<ClassOa> optional = classOARepository.findById(classId);
            if (optional.isPresent()) {
                ClassOa classOa = optional.get();
                classOa.setTask_list(classOa.getTask_list() == null || classOa.getTask_list().equals("") ?
                        taskOa.getId() : classOa.getTask_list() + "," + taskOa.getId());
                classOARepository.save(classOa);
            }
        }

        ResponseResult requestResult = new ResponseResult();
        requestResult.setMessage("任务发布成功");
        requestResult.setSuccess(true);

        return requestResult;
    }

    /**
     * 查看任务
     *
     * @param teacherId 根据身份查所有任务
     * @param name      可以根据名字查
     * @return
     */
    public List<TaskOa> getTaskOaByTaskName(String teacherId, String name) {

        TaskOa taskOa = new TaskOa();
        Example<TaskOa> example = Example.of(taskOa);
        if (!StringUtils.isEmpty(teacherId)) {
            taskOa.setPushAuthor(teacherId);
        }
        if (!StringUtils.isEmpty(name)) {
            taskOa.setTask_name(name);
        }
        return taskOARepository.findAll(example, new Sort(Sort.Direction.DESC, "createTime"));
    }

    public List<ClassOa> getClassOaByTaskId(String taskId) {
        Optional<TaskOa> optional = taskOARepository.findById(taskId);
        if (optional.isPresent()) {
            List<ClassOa> list = new ArrayList<>();
            TaskOa taskOa = optional.get();
            String[] classIds = taskOa.getClass_id().split(",");
            for (String classId : classIds) {
                Optional<ClassOa> classOaOptional = classOARepository.findById(classId);
                classOaOptional.ifPresent(list::add);
            }
            list.sort(Comparator.comparing(ClassOa::getClass_name));
            return list;
        }
        return null;
    }

    /**
     * 先根据任务表中的班级去找所有学生
     * 删除数据库中对应的任务表同时也要删除学生里面的任务信息
     * <p>
     * 删除班级表中的对应的任务id
     *
     * @param taskId
     * @return
     */
    @Transactional
    public ResponseResult deleteTaskById(String taskId, String teacherId) {
        ResponseResult responseResult = new ResponseResult();
        String[] classIds = null;
        Optional<TaskOa> optional = taskOARepository.findById(taskId);
        if (optional.isPresent()) {
            TaskOa taskOa = optional.get();
            String class_id = taskOa.getClass_id();
            if (class_id != null && !class_id.trim().equals("")) {
                classIds = class_id.split(",");
                //拿着classIds去找所有对应的班级 找所有学生 删除里面对应taskId的数据
                for (String classId : classIds) {
                    List<StudentOa> studentOaList = studentOARepository.findByClassId(classId);
                    for (StudentOa studentOa : studentOaList) {
                        String task_list = studentOa.getTask_list();
                        List list = JSON.parseObject(task_list, List.class);
                        for (Object o : list) {
                            JSONObject jsonObject = (JSONObject) o;
                            String id = (String) jsonObject.get("id");
                            if (id.equals(taskId)) {
                                list.remove(o);
                                break;
                            }
                        }
                        String jsonString = JSON.toJSONString(list);
                        studentOa.setTask_list(jsonString);
                        studentOARepository.save(studentOa);
                    }
                }
            }
        }

        taskOARepository.deleteById(taskId);

        //对班级表里的任务信息进行修改  这个classIds是在任务表中取出来的
        if (null != classIds) {
            for (String classId : classIds) {
                if (!StringUtils.isEmpty(classId)) {
                    Optional<ClassOa> classOaOptional = classOARepository.findById(classId);
                    if (classOaOptional.isPresent()) {
                        ClassOa classOa = classOaOptional.get();
                        String task_list = classOa.getTask_list();
                        if (!StringUtils.isEmpty(task_list)) {
                            String[] split = task_list.split(",");
                            ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(split));
                            arrayList.remove(taskId);
                            String join = Joiner.on(",").join(arrayList);
                            classOa.setTask_list(join);
                            classOARepository.save(classOa);
                        }
                    }
                }
            }
        }

        responseResult.setSuccess(true);
        responseResult.setMessage("删除成功");
        return responseResult;
    }

    /**
     * 修改任务
     * 将修改的信息去遍历需要修改的学生的任务列 执行学生表的保存操作
     * 修改任务后执行保存
     *
     * @param taskOa
     * @param teacherId
     * @return
     */
    @Transactional
    public ResponseResult updateTask(TaskOa taskOa, String teacherId) {
        ResponseResult responseResult = new ResponseResult();
        //校验数据 名称列和id不能为空
        if (StringUtils.isEmpty(taskOa.getId()) || StringUtils.isEmpty(taskOa.getTask_name())) {
            responseResult.setMessage("更新失败");
            responseResult.setSuccess(false);
            return responseResult;
        }
        String id = taskOa.getId();
        Optional<TaskOa> optional = taskOARepository.findById(id);
        if (!optional.isPresent()) {
            responseResult.setMessage("更新失败");
            responseResult.setSuccess(false);
            return responseResult;
        }

        String newClassIds = taskOa.getClass_id();
        //先修改学生的信息
        TaskOa task = optional.get();


        String oldTaskClassId = task.getClass_id();//未修改的任务表中对应的classId
        String newTaskClassId = taskOa.getClass_id();//新的任务中对应的classId

        String class_id = task.getClass_id();
        if (!StringUtils.isEmpty(class_id)) {
            String[] split = class_id.split(",");
            //对旧的数据进行操作
            for (String classId : split) {
                List<StudentOa> studentOaList = studentOARepository.findByClassId(classId);
                for (StudentOa studentOa : studentOaList) {
                    boolean contains = newClassIds.contains(studentOa.getClassId());
                    List list = JSON.parseObject(studentOa.getTask_list(), List.class);
                    if (contains) {//包含
                        for (Object o : list) {
                            JSONObject jsonObject = (JSONObject) o;
                            if (jsonObject.get("id").equals(task.getId())) {
                                jsonObject.put("task_name", taskOa.getTask_name());
                                jsonObject.put("task_details", taskOa.getTask_details());
                                jsonObject.put("reference_file_info", taskOa.getReference_file_info().trim().equals("") ? null : taskOa.getReference_file_info());//参考文件的信息
                                jsonObject.put("file_type", taskOa.getFile_type());
                                break;
                            }
                        }
                    } else { //要更新的班级里面没有原来的班级 直接删除原来班级里学生对应的任务
                        for (Object o : list) {
                            JSONObject jsonObject = (JSONObject) o;
                            if (jsonObject.get("id").equals(task.getId())) {
                                list.remove(o);
                                break;
                            }
                        }
                    }
                    //保存
                    String jsonString = JSON.toJSONString(list);
                    studentOa.setTask_list(jsonString);
                    studentOARepository.save(studentOa);
                }
            }

            task.setTask_name(taskOa.getTask_name());//作业名称
            task.setReference_file_info(taskOa.getReference_file_info().trim().equals("") ? null : taskOa.getReference_file_info());//参考文件的信息
            task.setUpdate_time(new Date());//更新时间
            task.setFile_type(taskOa.getFile_type());//上传文件的类型
            task.setTask_details(taskOa.getTask_details());//任务的描述
            task.setClass_id(taskOa.getClass_id());//发布的班级


            //添加新的班级信息
            for (String s : split) {
                newClassIds = newClassIds.replace(s, "");//替换字符串 得到全新要添加任务的班级
            }


            if (!StringUtils.isEmpty(newClassIds)) {
                String[] newClassIdss = newClassIds.split(",");
                for (String classIdss : newClassIdss) {
                    if (!classIdss.trim().equals("")) {
                        List<StudentOa> studentOaList = studentOARepository.findByClassId(classIdss);
                        if (studentOaList.size() > 0) {
                            for (StudentOa studentOa : studentOaList) {
                                studentOaService.updateStudentTaskList(studentOa, task);

                            }
                        }
                    }
                }
            }
        }


        //TODO 修改班级表中的数据 ********************************
        String[] oldTaskClassIds = oldTaskClassId.split(",");
        String[] newTaskClassIds = newTaskClassId.split(",");

        for (String oldClassId : oldTaskClassIds) {
            if (oldClassId != null && !oldClassId.trim().equals("")) {
                if (!newTaskClassId.contains(oldClassId)) {//新修改的任务里面不带了旧的班级，需要把旧的班级里面对应的任务id移除
                    Optional<ClassOa> oldClassOaOptional = classOARepository.findById(oldClassId);
                    if (oldClassOaOptional.isPresent()) {
                        ClassOa classOa = oldClassOaOptional.get();
                        String task_list = classOa.getTask_list();
                        if (task_list != null && !task_list.trim().equals("")) {
                            String[] oldTaskIds = task_list.split(",");
                            List<String> taskIdList = new ArrayList<>(Arrays.asList(oldTaskIds));
                            for (String taskId : taskIdList) {
                                if (taskId.equals(taskOa.getId())) {
                                    taskIdList.remove(taskId);
                                    String join = Joiner.on(",").join(taskIdList);
                                    classOa.setTask_list(join);
                                    classOARepository.save(classOa);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        for (String newClassId : newTaskClassIds) {
            if (newClassId != null && !newClassId.trim().equals("")) {
                if (!oldTaskClassId.contains(newClassId)) {//旧的任务里面没有新的班级id，需要把新的班级表里面添加上对应的taskId
                    Optional<ClassOa> oaOptional = classOARepository.findById(newClassId);
                    if (oaOptional.isPresent()) {
                        ClassOa classOa = oaOptional.get();
                        classOa.setTask_list(classOa.getTask_list() == null || classOa.getTask_list().equals("") ?
                                taskOa.getId() : classOa.getTask_list() + "," + taskOa.getId());
                        classOARepository.save(classOa);
                    }
                }
            }
        }

        task.setTask_name(taskOa.getTask_name());//作业名称
        task.setReference_file_info(taskOa.getReference_file_info().trim().equals("") ? null : taskOa.getReference_file_info());//参考文件的信息
        task.setUpdate_time(new Date());//更新时间
        task.setFile_type(taskOa.getFile_type());//上传文件的类型
        task.setTask_details(taskOa.getTask_details());//任务的描述
        task.setClass_id(taskOa.getClass_id());//发布的班级


        //执行任务的保存
        taskOARepository.save(task);

        responseResult.setMessage("更新成功");
        responseResult.setSuccess(true);
        return responseResult;
    }

    public List<TaskOa> searchTask(TaskOa taskOa, String teacherId) {
        //模糊查询
        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("task_name", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("class_id", ExampleMatcher.GenericPropertyMatchers.contains());
        return taskOARepository.findAll(Example.of(taskOa, matcher), new Sort(Sort.Direction.DESC, "createTime"));
    }

    @Transactional
    public ResponseResult evaluationLevel(String teacherId, String studentId, String taskId, String score) {
        //获取对应的学生信息中对应的任务信息，将分数写入后执行保存
        StudentOa one = studentOaService.getOne(studentId);
        String task_list = one.getTask_list();
        List list = JSON.parseObject(task_list, List.class);
        for (Object o : list) {
            JSONObject jsonObject = (JSONObject) o;
            String id = (String) jsonObject.get("id");
            if (id.equals(taskId)) {
                //找到对应的任务  修改分数
                jsonObject.put("score", score);
                String s = JSON.toJSONString(list);
                one.setTask_list(s);
                studentOARepository.save(one);
                break;
            }
        }
        ResponseResult responseResult = new ResponseResult();
        responseResult.setSuccess(true);
        responseResult.setMessage("分数录入成功");
        return responseResult;
    }
}
