package com.zsh.teach_oa_danmu.model;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@ToString
@Document(collection = "daomuList")
public class DanmuList {

    @Id
    private String id;

    private String mediaId;//对应的视频的id

    private List<DanmuExt> danmuList;


}
