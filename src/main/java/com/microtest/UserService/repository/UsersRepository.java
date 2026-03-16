package com.microtest.UserService.repository;



import com.microtest.UserService.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {


    //<editor-fold defaultState="collapsed" desc="DForm Data">
    @Query(
            value = "select st.id from Users st where st.email = :email "
    )
    Optional<Long> findIdByUserEmail(@Param("email") String email);

    @Query(
            value = "select st.id from Users st where st.login = :email "
    )
    Optional<Long> findIdByUserLogin(@Param("email") String email);

    Optional<Users> findByEmailOrLogin(String email, String login);
//</editor-fold>



}
