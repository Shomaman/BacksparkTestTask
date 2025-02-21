package com.example.Backspark.TestTask.repository;

import com.example.Backspark.TestTask.entity.SockEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SockRepository extends JpaRepository<SockEntity, Integer>, JpaSpecificationExecutor<SockEntity> {
    Optional<SockEntity> findSocksEntityByCottonAndSockColor(Double cotton, String s);

    List<SockEntity> findAll(Specification spec);
}
