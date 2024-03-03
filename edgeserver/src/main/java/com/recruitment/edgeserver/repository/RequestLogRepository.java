package com.recruitment.edgeserver.repository;

import com.recruitment.edgeserver.entity.RequestLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestLogRepository extends JpaRepository<RequestLogEntity, Long> {
}
