package com.zsh.teach_oa.service;

import com.alibaba.fastjson.JSON;
import com.zsh.teach_oa.config.RabbitMQConfig;
import com.zsh.teach_oa.dao.MediaOaRepository;
import com.zsh.teach_oa.dao.TeacherOARepository;
import com.zsh.teach_oa.ext.ResponseResult;
import com.zsh.teach_oa.model.MediaOa;
import com.zsh.teach_oa.model.StudentOa;
import com.zsh.teach_oa.model.TeacherOa;
import com.zsh.teach_oa.utils.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
public class MediaService {

    @Autowired
    private MediaOaRepository mediaOaRepository;

    @Autowired
    private TeacherOaService teacherOaService;

    @Autowired
    private StudentOaService studentOaService;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Value("${myconfig.mediaFilePath}")
    private String mediaFilePath;//源文件的根目录

    //视频处理路由
    @Value("${myconfig.mq.routingkey-media-video}")
    public String routingkey_media_video;

    @Transactional
    public ResponseResult saveMediaFile(MultipartFile multipartFile, String teacherId) {

        TeacherOa oa = teacherOaService.getOne(teacherId);
        ResponseResult responseResult = new ResponseResult();
        //得到文件的原始名称
        String originalFilename = multipartFile.getOriginalFilename();
        //获取文件名（没有后缀）
        String fileName = originalFilename.substring(0, originalFilename.indexOf("."));
        //获取文件后缀
        assert originalFilename != null;
        String fileSuffix = originalFilename.substring(originalFilename.indexOf(".") + 1);

        UUID uuid = UUID.randomUUID();
        String uuidStr = uuid.toString();

        String path1 = uuidStr.substring(0, 2);//一级目录
        String path2 = uuidStr.substring(2, 4);//二级目录

        //如果目录不存在，就创建
        FileUtils.makeDirectory(new File(mediaFilePath + path1 + "/" + path2 + "/" + originalFilename));


        //保存文件到磁盘路径后再执行保存数据到数据库
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(mediaFilePath + path1 + "/" + path2 + "/" + originalFilename));
            InputStream inputStream = multipartFile.getInputStream();
            IOUtils.copy(inputStream, fileOutputStream);
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(fileOutputStream);
            //执行保存数据
            MediaOa mediaOa = new MediaOa();

            mediaOa.setFileType(fileSuffix);
            mediaOa.setOriginalFilenamePath(mediaFilePath + path1 + "/" + path2 + "/" + originalFilename);
            mediaOa.setOriginalFilename(fileName);
            mediaOa.setTeacherId(teacherId);
            mediaOa.setUploadTime(new Date());
            mediaOa.setProcessStatus("0");//0-未处理 1-已处理
            mediaOa.setTeacherName(oa.getTeacher_name());
            mediaOa.setClassIds(oa.getClass_list());
            mediaOa.setIsDelete(false);
            mediaOaRepository.save(mediaOa);

            responseResult.setSuccess(true);
            responseResult.setMessage("上传成功");

            //发送消息到rabbitmq
            rabbitTemplate.convertAndSend(RabbitMQConfig.EX_MEDIA_PROCESSTASK, routingkey_media_video, JSON.toJSONString(mediaOa));
        } catch (IOException e) {
            e.printStackTrace();
            responseResult.setSuccess(false);
            responseResult.setMessage("上传失败");

        }

        return responseResult;
    }


    public List<MediaOa> findAllByTeacherId(String teacherId) {
        return mediaOaRepository.findAllByTeacherIdAndIsDelete(teacherId,false);
    }

    public List<MediaOa> findAllByClassId(String studentId) {
        StudentOa one = studentOaService.getOne(studentId);
        String classId = one.getClassId();
        if (StringUtils.isEmpty(classId)) {
            return new ArrayList<>();
        }
        //使用模糊查询

        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("classIds", ExampleMatcher.GenericPropertyMatchers.contains());
        MediaOa media = new MediaOa();
        media.setClassIds(classId);
        media.setProcessStatus("1");
        media.setIsDelete(false);
        List<MediaOa> all = mediaOaRepository.findAll(Example.of(media, matcher));
        return all;

    }

    public ResponseResult deleteMediaById(String mediaId) {
        ResponseResult  responseResult  = new ResponseResult();

        Optional<MediaOa> optional = mediaOaRepository.findById(mediaId);

        if (optional.isPresent()) {
            MediaOa mediaOa = optional.get();
            mediaOa.setIsDelete(true);
            mediaOaRepository.save(mediaOa);

            responseResult.setSuccess(true);
            responseResult.setMessage("删除成功");
            return responseResult;
        }
        responseResult.setSuccess(false);
        responseResult.setMessage("删除失败");
        return responseResult;
    }
}
