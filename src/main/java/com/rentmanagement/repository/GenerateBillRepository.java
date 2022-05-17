package com.rentmanagement.repository;

import com.rentmanagement.domain.GenerateBill;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the GenerateBill entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GenerateBillRepository extends JpaRepository<GenerateBill, Long> {}
