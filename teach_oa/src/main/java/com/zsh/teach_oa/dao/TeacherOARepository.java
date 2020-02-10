package com.zsh.teach_oa.dao;

import com.zsh.teach_oa.model.TeacherOa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeacherOARepository extends JpaRepository<TeacherOa,String> {

    TeacherOa findByAccountNameAndPassword(String account_name,String password);

    List<TeacherOa> findAllByAccountName(String accountName);

}
