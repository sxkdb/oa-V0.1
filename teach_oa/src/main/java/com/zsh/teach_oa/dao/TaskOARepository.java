package com.zsh.teach_oa.dao;

import com.zsh.teach_oa.model.TaskOa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskOARepository extends JpaRepository<TaskOa,String> {

    List<TaskOa> findByPushAuthor(String teacherId);

}
