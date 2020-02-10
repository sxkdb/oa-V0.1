package com.zsh.teach_oa.api;

import com.zsh.teach_oa.model.ClassOa;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

@Api("作业信息")
public interface TaskOaControllerApi {

    @ApiOperation("根据作业id查对应的作业绑定了的所有班级的id")
    List<ClassOa> getClassOaByTaskId(String taskId);



}
