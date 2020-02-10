package com.zsh.teach_oa.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@ToString
@Data
@NoArgsConstructor
@Entity
@Table(name="class_oa")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class ClassOa {

    @Id
    @GeneratedValue(generator = "jpa-uuid")
    private String id;
    private String class_name; //班级名称
    private String task_list; //班级内所有的任务信息


}
