package com.zsh.teach_oa.ext;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 系统返回数据给前端的数据类型
 */
@Data
@ToString
@NoArgsConstructor
public class ResponseResult {

    private String message;//信息

    private String status;//当前状态

    private String code;//错误代码

    private boolean success; //是否成功

    private String fileId;

    private String fileName;



}


