package com.zsh.teach_oa.controller;

import com.zsh.teach_oa.api.MediaControllerApi;
import com.zsh.teach_oa.ext.ResponseResult;
import com.zsh.teach_oa.model.MediaOa;
import com.zsh.teach_oa.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/media")
public class MediaController implements MediaControllerApi {

    @Autowired
    private MediaService mediaService;


    @Override
    @PostMapping("/saveMediaFile")
    public ResponseResult saveMediaFile(@RequestParam("file") MultipartFile multipartFile,
                                        @CookieValue("token_teacherId") String teacherId) {
        return mediaService.saveMediaFile(multipartFile,teacherId);
    }

    @Override
    @GetMapping("/findAllByTeacherId")
    public List<MediaOa> findAllByTeacherId(@CookieValue("token_teacherId") String teacherId) {
        return mediaService.findAllByTeacherId(teacherId);
    }

    @Override
    @GetMapping("/findAllByClassId")
    public List<MediaOa> findAllByClassId(@CookieValue("token_studentId") String studentId) {
        return mediaService.findAllByClassId(studentId);
    }

    @Override
    @PostMapping("/delMedia/{mediaId}")
    public ResponseResult deleteMediaById(@PathVariable("mediaId") String mediaId) {
        return mediaService.deleteMediaById(mediaId);
    }
}
