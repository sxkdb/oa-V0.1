package com.zsh.teach_oa_media.api;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
public interface TestControllerApi {

    @ApiOperation("测试")
    void testMedia();


}
