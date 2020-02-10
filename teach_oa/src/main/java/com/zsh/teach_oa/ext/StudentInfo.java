package com.zsh.teach_oa.ext;

import com.zsh.teach_oa.model.StudentOa;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 学生的信息，主要是对任务数据的处理
 */
@Data
@NoArgsConstructor
@ToString
public class StudentInfo extends StudentOa {

    private List<StudentTask> studentTaskList;//任务列表

    private int completeTotal;//任务完成的数量

    private int unfinishedTotal;//任务未完成的数量

    private int total;//任务总数



}
