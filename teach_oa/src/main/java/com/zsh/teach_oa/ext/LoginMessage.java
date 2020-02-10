package com.zsh.teach_oa.ext;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class LoginMessage {

    public String message;//信息响应
    public String code;//代码
    public String status;//状态


    public String token;//登录成功后保存到浏览器的cookie

}
