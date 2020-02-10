package com.zsh.teach_oa.api;

import com.zsh.teach_oa.ext.ResponseResult;
import com.zsh.teach_oa.model.ClassOa;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

@Api(value = "班级集合")
public interface ClassOaControllerApi {

    @ApiOperation("查找所有班级")
    List<ClassOa> getAll();

    @ApiOperation("添加班级")
    ResponseResult addClassOa(String teacherId, String className);

}
