package com.zsh.teach_oa.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
@Data
@NoArgsConstructor
@Entity
@Table(name="task_oa")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class TaskOa {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    private String id;
    private String task_name;//任务名称
    private String task_details;//任务详情
    private String class_id;//所选班级
    private String file_type;//文件类型
    private String reference_file_info;//参考资料
    @Column(name = "create_time")
    private Date createTime;
    private Date update_time;
    @Column(name = "push_author")
    private String pushAuthor;//发布者id
}
