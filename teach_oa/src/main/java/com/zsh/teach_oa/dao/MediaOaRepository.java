package com.zsh.teach_oa.dao;

import com.zsh.teach_oa.model.MediaOa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaOaRepository extends JpaRepository<MediaOa,String> {
    List<MediaOa> findAllByTeacherIdAndIsDelete(String teacherId, Boolean isDelete);
}
