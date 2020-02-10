package com.zsh.teach_oa_media.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@ToString
@Data
@NoArgsConstructor
@Entity
@Table(name="media_oa")
public class MediaOa {

    @Id
    @GeneratedValue(generator = "jpa-uuid")
    private String id;
    private String originalFilename;
    private String originalFilenamePath;
    private String m3u8FilenamePath;
    private String teacherId;
    private String teacherName;
    private Date uploadTime;
    private String processStatus;
    private String fileType;
    private String classIds;

}
