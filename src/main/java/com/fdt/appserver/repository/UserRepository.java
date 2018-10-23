package com.fdt.appserver.repository;

import com.fdt.appserver.bean.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author guo_d
 * @date 2018/10/23
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Override
    List<User> findAll();

    @Override
    <S extends User> S save(S s);

    @Override
    Optional<User> findById(String s);
}
