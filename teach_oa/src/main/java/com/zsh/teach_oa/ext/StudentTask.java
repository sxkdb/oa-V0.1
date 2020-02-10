package com.zsh.teach_oa.ext;

import com.zsh.teach_oa.model.TaskOa;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 学生的任务列表
 * 再拓展 上传的文件信息
 */
@Data
@NoArgsConstructor
public class StudentTask extends TaskOa {


    private boolean isSubmit = false;//是否提交

    private String fileUrl = "";//文件地址

    private String fileInfo = "";//文件信息（文件名字）

    private String pushAuthorName = "";//发布者名字

    private String fileName = "";//文件名字

    private String fileSuffix = "";//文件后缀

    private String fileId = "";//文件id

    private String remark = "";//备注

    private Date createTime = new Date();//创建时间

    private Date updateTime = new Date();//更新时间

    private Boolean isDel = false;//是否已经删除

    private String score = "";//分数


}
