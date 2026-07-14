package com.example.hostelmanagement.repository;

import com.example.hostelmanagement.entity.Tenant;
import com.example.hostelmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {


    boolean existsByEmail(String email);


    boolean existsByAadhaarNumber(String aadhaarNumber);


    Optional<Tenant> findByEmail(String email);


    List<Tenant> findByRoomId(Long roomId);


    long countByRoomIdAndActiveTrue(Long roomId);


    long countByActiveTrue();

    List<Tenant> findByActiveTrue();

    List<Tenant> findByRoomAdmin(User admin);

    List<Tenant> findByActiveTrueAndRoomAdmin(User admin);

    long countByActiveTrueAndRoomAdmin(User admin);
}
