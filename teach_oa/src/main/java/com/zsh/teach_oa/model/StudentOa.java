package com.zsh.teach_oa.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "student_oa")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class StudentOa {

    @Id
    @GeneratedValue(generator = "jpa-uuid")
    private String id;     //主键
    @Column(name = "account_name")
    private String accountName;     //账号
    private String password;     //密码
    private String student_name;     //学生姓名
    private String student_id;     //学号
    @Column(name = "class_id")
    private String classId;     //班级id
    private String task_list;     //任务信息
    private String qq;     //qq号
    @Column(name = "is_first")
    private int isFirst;//是否为新手  1-是  0-否

}
