//package com.zsh.teach_oa;
//
//import com.zsh.teach_oa.dao.ClassOARepository;
//import com.zsh.teach_oa.dao.StudentOARepository;
//import com.zsh.teach_oa.dao.TaskOARepository;
//import com.zsh.teach_oa.dao.TeacherOARepository;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class TeachOaApplicationTests {
//
//    @Autowired
//    private ClassOARepository classOARepository;
//
//    @Autowired
//    private TeacherOARepository teacherOARepository;
//
//    @Autowired
//    private StudentOARepository studentOARepository;
//
//    @Autowired
//    private TaskOARepository taskOARepository;
//
//
//    @Test
//    public void testClassOA() {
//        System.out.println(classOARepository.findAll());
//    }
//
//
//    @Test
//    public void testTeacherOa() {
////        System.out.println(teacherOARepository.findAll());
//        System.out.println(teacherOARepository.findByAccountNameAndPassword("cdt", "cat"));
//
//    }
//
//
//
//    @Test
//    public void testStudentOa() {
//        System.out.println(studentOARepository.findAll());
//    }
//
//
//
//    @Test
//    public void testTaskOA() {
//        System.out.println(taskOARepository.findAll());
//    }
//
//
//
//
//}
