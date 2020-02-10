package com.zsh.teach_oa_danmu.api;

import com.zsh.teach_oa_danmu.ext.ResponseResult;
import com.zsh.teach_oa_danmu.model.Danmu;

import java.util.List;

//@Api("弹幕ControllerApi")
public interface DanmuControllerApi {


//    @ApiOperation("保存弹幕到mongodb,sender是发送者")
    ResponseResult saveDanmu(String senderId, String mediaId, Danmu danmu);

//    @ApiOperation("根据媒资id获取弹幕列表")
    List<Danmu> getDanmuListByMediaId(String mediaId);





}
