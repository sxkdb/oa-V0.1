package com.zsh.teach_oa.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name="teacher_oa")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class TeacherOa {

    @Id
    @GeneratedValue(generator = "jpa-uuid")
    private String id;
    @Column(name = "account_name")
    private String accountName;    //账号
    private String password;    //密码
    private String teacher_name;    //教师名称
    private String class_list;    //所教班级名称集合
    private String push_task_list;    //发布的任务集合
    @Column(name = "is_first")
    private int isFirst;//是否为新手  1-是  0-否

}
