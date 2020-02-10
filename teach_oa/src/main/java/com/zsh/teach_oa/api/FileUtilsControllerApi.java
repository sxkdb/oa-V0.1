package com.zsh.teach_oa.api;

import com.zsh.teach_oa.ext.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api("文件工具类")
public interface FileUtilsControllerApi {

    @ApiOperation("word to pdf")
    ResponseResult wordToPdf(MultipartFile file);

    @ApiOperation("根据传过来的参数去对应的地址去磁盘上找文件并下载")
    void download(HttpServletResponse response, HttpServletRequest request, String fileName);

}
