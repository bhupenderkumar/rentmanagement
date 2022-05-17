package com.rentmanagement.web.rest;

import com.rentmanagement.domain.Tenant;
import com.rentmanagement.repository.TenantRepository;
import com.rentmanagement.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.rentmanagement.domain.Tenant}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TenantResource {

    private final Logger log = LoggerFactory.getLogger(TenantResource.class);

    private static final String ENTITY_NAME = "tenant";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TenantRepository tenantRepository;

    public TenantResource(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    /**
     * {@code POST  /tenants} : Create a new tenant.
     *
     * @param tenant the tenant to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tenant, or with status {@code 400 (Bad Request)} if the tenant has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tenants")
    public ResponseEntity<Tenant> createTenant(@Valid @RequestBody Tenant tenant) throws URISyntaxException {
        log.debug("REST request to save Tenant : {}", tenant);
        if (tenant.getId() != null) {
            throw new BadRequestAlertException("A new tenant cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Tenant result = tenantRepository.save(tenant);
        return ResponseEntity
            .created(new URI("/api/tenants/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /tenants/:id} : Updates an existing tenant.
     *
     * @param id the id of the tenant to save.
     * @param tenant the tenant to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tenant,
     * or with status {@code 400 (Bad Request)} if the tenant is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tenant couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tenants/{id}")
    public ResponseEntity<Tenant> updateTenant(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Tenant tenant
    ) throws URISyntaxException {
        log.debug("REST request to update Tenant : {}, {}", id, tenant);
        if (tenant.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tenant.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tenantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Tenant result = tenantRepository.save(tenant);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tenant.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /tenants/:id} : Partial updates given fields of an existing tenant, field will ignore if it is null
     *
     * @param id the id of the tenant to save.
     * @param tenant the tenant to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tenant,
     * or with status {@code 400 (Bad Request)} if the tenant is not valid,
     * or with status {@code 404 (Not Found)} if the tenant is not found,
     * or with status {@code 500 (Internal Server Error)} if the tenant couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tenants/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Tenant> partialUpdateTenant(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Tenant tenant
    ) throws URISyntaxException {
        log.debug("REST request to partial update Tenant partially : {}, {}", id, tenant);
        if (tenant.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tenant.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tenantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Tenant> result = tenantRepository
            .findById(tenant.getId())
            .map(existingTenant -> {
                if (tenant.getTenantName() != null) {
                    existingTenant.setTenantName(tenant.getTenantName());
                }
                if (tenant.getAddressProff() != null) {
                    existingTenant.setAddressProff(tenant.getAddressProff());
                }
                if (tenant.getAddressProffContentType() != null) {
                    existingTenant.setAddressProffContentType(tenant.getAddressProffContentType());
                }
                if (tenant.getNumberofFamilyMembers() != null) {
                    existingTenant.setNumberofFamilyMembers(tenant.getNumberofFamilyMembers());
                }
                if (tenant.getPhoneNumber() != null) {
                    existingTenant.setPhoneNumber(tenant.getPhoneNumber());
                }
                if (tenant.getRentStartDate() != null) {
                    existingTenant.setRentStartDate(tenant.getRentStartDate());
                }
                if (tenant.getRentAmount() != null) {
                    existingTenant.setRentAmount(tenant.getRentAmount());
                }
                if (tenant.getElectricityUnitRate() != null) {
                    existingTenant.setElectricityUnitRate(tenant.getElectricityUnitRate());
                }
                if (tenant.getStartingElectricityUnit() != null) {
                    existingTenant.setStartingElectricityUnit(tenant.getStartingElectricityUnit());
                }
                if (tenant.getAnyOtherDetails() != null) {
                    existingTenant.setAnyOtherDetails(tenant.getAnyOtherDetails());
                }
                if (tenant.getSendNotification() != null) {
                    existingTenant.setSendNotification(tenant.getSendNotification());
                }
                if (tenant.getEmailAddress() != null) {
                    existingTenant.setEmailAddress(tenant.getEmailAddress());
                }
                if (tenant.getEmergencyContactNumber() != null) {
                    existingTenant.setEmergencyContactNumber(tenant.getEmergencyContactNumber());
                }
                if (tenant.getOutStandingAmount() != null) {
                    existingTenant.setOutStandingAmount(tenant.getOutStandingAmount());
                }
                if (tenant.getMonthEndCalculation() != null) {
                    existingTenant.setMonthEndCalculation(tenant.getMonthEndCalculation());
                }
                if (tenant.getCalculateOnDate() != null) {
                    existingTenant.setCalculateOnDate(tenant.getCalculateOnDate());
                }

                return existingTenant;
            })
            .map(tenantRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tenant.getId().toString())
        );
    }

    /**
     * {@code GET  /tenants} : get all the tenants.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tenants in body.
     */
    @GetMapping("/tenants")
    public List<Tenant> getAllTenants() {
        log.debug("REST request to get all Tenants");
        return tenantRepository.findAll();
    }

    /**
     * {@code GET  /tenants/:id} : get the "id" tenant.
     *
     * @param id the id of the tenant to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tenant, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tenants/{id}")
    public ResponseEntity<Tenant> getTenant(@PathVariable Long id) {
        log.debug("REST request to get Tenant : {}", id);
        Optional<Tenant> tenant = tenantRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(tenant);
    }

    /**
     * {@code DELETE  /tenants/:id} : delete the "id" tenant.
     *
     * @param id the id of the tenant to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tenants/{id}")
    public ResponseEntity<Void> deleteTenant(@PathVariable Long id) {
        log.debug("REST request to delete Tenant : {}", id);
        tenantRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
