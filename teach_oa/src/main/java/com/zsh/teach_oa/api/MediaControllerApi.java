package com.zsh.teach_oa.api;

import com.zsh.teach_oa.ext.ResponseResult;
import com.zsh.teach_oa.model.MediaOa;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api
public interface MediaControllerApi {

    @ApiOperation("上传视频，保存到服务器的磁盘路径，发送消息到rabbitmq上(子服务进行监听，监听到之后就将对应的原视频转为m3u8)")
    ResponseResult saveMediaFile(MultipartFile multipartFile, String teacherId);

    @ApiOperation("根据teacherId查看所有的视频信息")
    List<MediaOa> findAllByTeacherId(String teacherId);

    @ApiOperation("根据班级id查看所有的视频信息")
    List<MediaOa> findAllByClassId(String studentId);

    @ApiOperation("逻辑删除媒资信息，不是物理删除")
    ResponseResult deleteMediaById(String mediaId);

}
