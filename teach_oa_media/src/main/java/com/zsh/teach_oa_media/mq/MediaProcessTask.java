package com.zsh.teach_oa_media.mq;

import com.alibaba.fastjson.JSON;
import com.zsh.teach_oa_media.dao.MediaOaRepository;
import com.zsh.teach_oa_media.entity.MediaOa;
import com.zsh.teach_oa_media.utils.HlsVideoUtil;
import com.zsh.teach_oa_media.utils.PinYinUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Component
public class MediaProcessTask {


    @Autowired
    private MediaOaRepository mediaOaRepository;

    @Value("${myconfig.m3u8MediaFilePath}")
    private String m3u8MediaFilePath;


    //视频处理
    @RabbitListener(queues = "${myconfig.mq.queue-media-video-processor}", containerFactory = "customContainerFactory")
    @Transactional
    public void receiveMediaProcessTask(String msg) {
        MediaOa mediaOa = JSON.parseObject(msg, MediaOa.class);


//        String ffmpeg_path = "E:/FFmpeg/ffmpeg-20180227-fa0c9d6-win64-static/bin/ffmpeg.exe";//ffmpeg的安装位置
        String ffmpeg_path = "ffmpeg";//ffmpeg的安装位置
        String video_path = mediaOa.getOriginalFilenamePath();
        String m3u8_name = PinYinUtil.getPingYin(mediaOa.getOriginalFilename()) + ".m3u8";
        UUID uuid = UUID.randomUUID();
        String uuidStr = uuid.toString();
        String relativePath = "/m3u8/" + uuidStr.substring(0, 2) + "/" + uuidStr.substring(2, 4) + "/";//m3u8的相对路径
        String m3u8Path = m3u8MediaFilePath + relativePath;
        HlsVideoUtil videoUtil = new HlsVideoUtil(ffmpeg_path, video_path, m3u8_name, m3u8Path);
        String s = videoUtil.generateM3u8();

        mediaOa.setM3u8FilenamePath(relativePath + m3u8_name);
        mediaOa.setProcessStatus("1");
        mediaOaRepository.save(mediaOa);

        System.out.println(s);


    }


}
