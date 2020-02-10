package com.zsh.teach_oa_danmu.model;

import lombok.Data;

import java.util.Date;

@Data
public class DanmuExt extends Danmu{

    private Date createTime;//创建时间

    private String senderId;//发送者
}
