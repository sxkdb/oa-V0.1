package com.zsh.teach_oa_danmu.service;

import com.zsh.teach_oa_danmu.dao.DanmuListRespository;
import com.zsh.teach_oa_danmu.ext.ResponseResult;
import com.zsh.teach_oa_danmu.model.Danmu;
import com.zsh.teach_oa_danmu.model.DanmuExt;
import com.zsh.teach_oa_danmu.model.DanmuList;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DanmuService {

    @Autowired
    private DanmuListRespository danmuListRespository;


    public ResponseResult saveDanmu(String senderId, String mediaId, Danmu danmu) {

        List<DanmuExt> list = new ArrayList<>();
        DanmuExt danmuExt = new DanmuExt();
        BeanUtils.copyProperties(danmu, danmuExt);
        danmuExt.setCreateTime(new Date());
        danmuExt.setSenderId(senderId);

        DanmuList danmuList = danmuListRespository.findByMediaId(mediaId);
        if (danmuList == null) {

            DanmuList newList = new DanmuList();
            newList.setMediaId(mediaId);

            list.add(danmuExt);
            newList.setDanmuList(list);

            danmuListRespository.save(newList);
        } else {
            list = danmuList.getDanmuList();

            if (list == null) {
                list = new ArrayList<>();
                list.add(danmuExt);
            } else {
                list.add(danmuExt);
            }
            danmuList.setDanmuList(list);
            danmuListRespository.save(danmuList);
        }

        ResponseResult responseResult = new ResponseResult();
        responseResult.setSuccess(true);
        return responseResult;
    }

    public List<Danmu> getDanmuListByMediaId(String mediaId) {
        DanmuList danmuList = danmuListRespository.findByMediaId(mediaId);
        List<DanmuExt> danmus = danmuList.getDanmuList();

        List<Danmu> result = new ArrayList<>();
        for (DanmuExt danmuExt : danmus) {
            Danmu danmu = new Danmu();
            BeanUtils.copyProperties(danmuExt, danmu);
            result.add(danmu);
        }
        return result;
    }

}
