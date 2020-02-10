package com.zsh.teach_oa.dao;

import com.zsh.teach_oa.model.StudentOa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentOARepository extends JpaRepository<StudentOa,String> {

    List<StudentOa> findByClassId(String classId);

    StudentOa findByAccountNameAndPassword(String account_name, String password);

    int deleteAllByClassId(String classId);
}
