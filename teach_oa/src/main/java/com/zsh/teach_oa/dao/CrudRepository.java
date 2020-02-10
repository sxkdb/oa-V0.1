package com.zsh.teach_oa.dao;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.io.Serializable;

@NoRepositoryBean
public interface CrudRepository<T, ID extends Serializable> extends Repository<T, ID> {

    <S extends T> S save(S entity);//保存

    <S extends T> Iterable<S> save(Iterable<S> entities);//批量保存

    T findOne(ID id);//根据id查询一个对象

    boolean exists(ID id);//判断对象是否存在

    Iterable<T> findAll();//查询所有的对象

    Iterable<T> findAll(Iterable<ID> ids);//根据id列表查询所有的对象

    long count();//计算对象的总个数

    void delete(ID id);//根据id删除

    void delete(T entity);//删除对象

    void delete(Iterable<? extends T> entities);//批量删除

    void deleteAll();//删除所有
}