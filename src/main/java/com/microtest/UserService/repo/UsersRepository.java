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


    //<editor-fold defaultState="collapsed" desc="Change, Forgot Password">


//</editor-fold>

    //<editor-fold defaultState="collapsed" desc="Disable Old Salary">
    @Query(
            value = "select st.state " +
                    "from Users st   " +
                    "where st.id = :user_id "
    )
    Optional<Boolean> getState(@Param("user_id") Long user_id);

    @Modifying
    @Transactional
    @Query("update Users st set st.state = :state where st.id = :user_id")
    int update_stateUser(@Param("user_id") Long user_id, @Param("state") boolean state);
//</editor-fold>

    //<editor-fold defaultState="collapsed" desc="LoginData">
    //Request For LoginData


//    @Query(
//            value = "select new com.admistock.Api_gestion.dto.SearchByIdDTO(sh.id, sh.w) " +
//                    "from Shop sh join sh.admin st where st.id = :user_id"
//    )
//    List<SearchByIdDTO> findShopIDByAdmin(@Param("user_id") Long user_id);

//    @Query(
////            nativeQuery = true,
//            value = "select new com.admistock.Api_gestion.dto.SearchByIdDTO(sh.id, sh.w) " +
//                    "from Stock sh join sh.admin st where st.id = :user_id"
//    )
//    List<SearchByIdDTO> findStockIDByAdmin(@Param("user_id") Long user_id);


//    @Query(
//            value = "select new com.admistock.Api_gestion.dto.SearchByIdDTO(sh.id, sh.w) " +
//                    "from Role_manager rl join rl.manager st join rl.stock sh where st.id = :user_id"
//    )
//    List<SearchByIdDTO> findStockIDByManager(@Param("user_id") Long user_id);

//    @Query(
//            value = "select new com.admistock.Api_gestion.dto.SearchByIdDTO(sh1.id, sh1.w)" +
//                    " from Role_manager rl join rl.manager st join rl.stock sh " +
//                    "join sh.role_stocks rl1 join rl1.shop sh1 where st.id = :user_id"
//    )
//    List<SearchByIdDTO> findShopIDByManager(@Param("user_id") Long user_id);
//</editor-fold>


    //<editor-fold defaultState="collapsed" desc="DForm Data">


    @Query(
            value = "select st from Users st where (st.email = :userName or st.login = :userName)"
    )
    Optional<Users> findFirstByEmailOrLogin(@Param("userName") String userName);

    @Query(
            value = "select st.id from Users st where st.email = :email "
    )
    Optional<Long> findEmailUser(@Param("email") String email);

    @Query(
            value = "select st.id from Users st where st.login = :email "
    )
    Optional<Long> findLoginUser(@Param("email") String email);

    Users findByRole(ROLE_ENUM role);


    Optional<Users> findByEmailOrLogin(String email, String login);
//</editor-fold>



}
