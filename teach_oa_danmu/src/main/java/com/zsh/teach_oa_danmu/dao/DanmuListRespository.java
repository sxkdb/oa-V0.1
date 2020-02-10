package com.zsh.teach_oa_danmu.dao;

import com.zsh.teach_oa_danmu.model.DanmuList;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DanmuListRespository extends MongoRepository<DanmuList, String> {

    DanmuList findByMediaId(String mediaId);


}
