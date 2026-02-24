package com.microtest.UserService.repo;



import com.microtest.UserService.bean.Users;
import com.microtest.UserService.enums.ROLE_ENUM;
import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {


    //<editor-fold defaultState="collapsed" desc="DForm Data">
//    @Query(
//            value = "select st from Users st where (st.email = :userName or st.login = :userName)"
//    )
//    Optional<Users> findFirstByEmailOrLogin(@Param("userName") String userName);

    //old_clean_code: findEmailUser
    //new_clean_code: findIdByUserEmail
    @Query(
            value = "select st.id from Users st where st.email = :email "
    )
    Optional<Long> findIdByUserEmail(@Param("email") String email);

    //old_clean_code: findLoginUser
    //new_clean_code: findIdByUserLogin
    @Query(
            value = "select st.id from Users st where st.login = :email "
    )
    Optional<Long> findIdByUserLogin(@Param("email") String email);


    Optional<Users> findByEmailOrLogin(String email, String login);
//</editor-fold>



}
