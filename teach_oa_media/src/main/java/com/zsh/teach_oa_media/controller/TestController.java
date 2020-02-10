package com.zsh.teach_oa_media.controller;


import com.zsh.teach_oa_media.api.TestControllerApi;
import com.zsh.teach_oa_media.utils.HlsVideoUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController implements TestControllerApi {

    @GetMapping("/testMedia")
    public void testMedia(){
        String ffmpeg_path = "ffmpeg";//ffmpeg的安装位置
        String video_path = "/root/9diting0001.mp4";
        String m3u8_name = "luoxiaoheifanwai.m3u8";
        String m3u8_path = "/root/video/luoxiaoheifanwai/";
        HlsVideoUtil videoUtil = new HlsVideoUtil(ffmpeg_path,video_path,m3u8_name,m3u8_path);
        String s = videoUtil.generateM3u8();
    } 

}
