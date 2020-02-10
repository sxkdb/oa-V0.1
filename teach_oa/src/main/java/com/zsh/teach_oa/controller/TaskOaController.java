package com.zsh.teach_oa.controller;

import com.zsh.teach_oa.api.TaskOaControllerApi;
import com.zsh.teach_oa.model.ClassOa;
import com.zsh.teach_oa.service.TaskOaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/taskOa")
public class TaskOaController implements TaskOaControllerApi {

    @Autowired
    private TaskOaService taskOaService;

    /**
     * 根据作业id查找该作业绑定几个班级，将相关的班级都放回出去
     * @param taskId 作业id
     * @return
     */
    @Override
    @GetMapping("/getClassOaByTaskId")
    public List<ClassOa> getClassOaByTaskId(String taskId) {
        return taskOaService.getClassOaByTaskId(taskId);
    }
}
