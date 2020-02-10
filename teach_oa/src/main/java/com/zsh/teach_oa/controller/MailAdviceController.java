package com.zsh.teach_oa.controller;

import com.zsh.teach_oa.ext.ResponseResult;
import com.zsh.teach_oa.utils.MailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mail")
public class MailAdviceController {

    @Autowired
    private JavaMailSenderImpl mailSender;

    @PostMapping("/send/{addresseeQQ}/{studentName}/{taskName}")
    public ResponseResult sendMailAdviceStudentByStudentQQ(@PathVariable("addresseeQQ") String addresseeQQ,
                                                           @PathVariable("studentName") String studentName,
                                                           @PathVariable("taskName") String taskName) {
        ResponseResult requestResult = null;
        try {
            MailUtils.sendMail(mailSender, addresseeQQ, studentName + "你好，你还有作业(" + taskName + ")未上传");
            requestResult = new ResponseResult();
            requestResult.setSuccess(true);
            requestResult.setMessage("QQ邮件发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            requestResult.setSuccess(false);
            requestResult.setMessage("QQ邮件发送失败");
            return requestResult;
        }
        return requestResult;
    }

}
