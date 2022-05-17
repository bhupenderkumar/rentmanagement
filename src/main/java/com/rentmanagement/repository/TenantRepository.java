package com.rentmanagement.repository;

import com.rentmanagement.domain.Tenant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Tenant entity.
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    default Optional<Tenant> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Tenant> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Tenant> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct tenant from Tenant tenant left join fetch tenant.user",
        countQuery = "select count(distinct tenant) from Tenant tenant"
    )
    Page<Tenant> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct tenant from Tenant tenant left join fetch tenant.user")
    List<Tenant> findAllWithToOneRelationships();

    @Query("select tenant from Tenant tenant left join fetch tenant.user where tenant.id =:id")
    Optional<Tenant> findOneWithToOneRelationships(@Param("id") Long id);
}
