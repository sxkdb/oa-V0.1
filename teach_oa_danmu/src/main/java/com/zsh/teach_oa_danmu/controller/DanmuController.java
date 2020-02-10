package com.zsh.teach_oa_danmu.controller;

import com.zsh.teach_oa_danmu.api.DanmuControllerApi;
import com.zsh.teach_oa_danmu.ext.ResponseResult;
import com.zsh.teach_oa_danmu.model.Danmu;
import com.zsh.teach_oa_danmu.service.DanmuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/danmu")
public class DanmuController implements DanmuControllerApi {


    @Autowired
    private DanmuService danmuService;


    @Override
    @PostMapping("/send/{sender}/{mediaId}")
    public ResponseResult saveDanmu(@PathVariable("sender") String senderId,
                                    @PathVariable("mediaId") String mediaId,
                                    Danmu danmu) {
        return danmuService.saveDanmu(senderId, mediaId, danmu);
    }

    @Override
    @GetMapping("/find/{mediaId}")
    public List<Danmu> getDanmuListByMediaId(@PathVariable("mediaId") String mediaId) {
        return danmuService.getDanmuListByMediaId(mediaId);
    }
}
