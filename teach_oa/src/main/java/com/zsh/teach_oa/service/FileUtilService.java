package com.zsh.teach_oa.service;

import com.zsh.teach_oa.ext.ResponseResult;
import com.zsh.teach_oa.utils.Word2PdfUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
public class FileUtilService {

    @Value("${myconfig.word2PdfFilePath}")
    private String word2PdfFilePath;


    public ResponseResult wordToPdf(MultipartFile file) {
        ResponseResult responseResult = new ResponseResult();
        //得到文件的原始名称
        String originalFilename = file.getOriginalFilename();
        //获取文件名（没有后缀）
        String fileName = originalFilename.substring(0, originalFilename.indexOf("."));
        //转为pdf后返回url
        try {
            //写个公共的方法以供下载
            Word2PdfUtil.word2Pdf(file.getInputStream(), word2PdfFilePath + fileName + ".pdf");

            responseResult.setSuccess(true);
            responseResult.setMessage(fileName);
            return responseResult;
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseResult.setSuccess(false);
        responseResult.setMessage("word转换pdf失败");
        return responseResult;
    }


    public InputStream download(String fileName) {
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(word2PdfFilePath+fileName+".pdf"));
            return fileInputStream;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
