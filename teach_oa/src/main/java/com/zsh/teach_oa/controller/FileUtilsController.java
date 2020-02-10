package com.zsh.teach_oa.controller;

import com.zsh.teach_oa.api.FileUtilsControllerApi;
import com.zsh.teach_oa.ext.ResponseResult;
import com.zsh.teach_oa.service.FileUtilService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/file/utils/")
public class FileUtilsController implements FileUtilsControllerApi {

    @Autowired
    private FileUtilService fileUtilService;

    @Override
    @PostMapping("/wordToPdf")
    public ResponseResult wordToPdf(MultipartFile file) {
        return fileUtilService.wordToPdf(file);

    }

    @Override
    @GetMapping("/downLoad/{fileName}")
    public void download(HttpServletResponse response, HttpServletRequest request, @PathVariable("fileName") String fileName) {

        try {
            InputStream inputStream = fileUtilService.download(fileName);
            fileName += ".pdf";
            response.setContentType("application/force-download");// 设置强制下载不打开
            //支持火狐和谷歌
            String userAgent = request.getHeader("User-Agent");
            byte[] bytes = userAgent.contains("MSIE") ? fileName.getBytes() : fileName.getBytes(StandardCharsets.UTF_8);
            fileName = new String(bytes, StandardCharsets.ISO_8859_1);

            response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);

            ServletOutputStream outputStream = response.getOutputStream();

            IOUtils.copy(inputStream, outputStream);
            //关流
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
