package com.rentmanagement.web.rest;

import com.rentmanagement.domain.GenerateBill;
import com.rentmanagement.repository.GenerateBillRepository;
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
 * REST controller for managing {@link com.rentmanagement.domain.GenerateBill}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class GenerateBillResource {

    private final Logger log = LoggerFactory.getLogger(GenerateBillResource.class);

    private static final String ENTITY_NAME = "generateBill";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GenerateBillRepository generateBillRepository;

    public GenerateBillResource(GenerateBillRepository generateBillRepository) {
        this.generateBillRepository = generateBillRepository;
    }

    /**
     * {@code POST  /generate-bills} : Create a new generateBill.
     *
     * @param generateBill the generateBill to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new generateBill, or with status {@code 400 (Bad Request)} if the generateBill has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/generate-bills")
    public ResponseEntity<GenerateBill> createGenerateBill(@Valid @RequestBody GenerateBill generateBill) throws URISyntaxException {
        log.debug("REST request to save GenerateBill : {}", generateBill);
        if (generateBill.getId() != null) {
            throw new BadRequestAlertException("A new generateBill cannot already have an ID", ENTITY_NAME, "idexists");
        }
        GenerateBill result = generateBillRepository.save(generateBill);
        return ResponseEntity
            .created(new URI("/api/generate-bills/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /generate-bills/:id} : Updates an existing generateBill.
     *
     * @param id the id of the generateBill to save.
     * @param generateBill the generateBill to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated generateBill,
     * or with status {@code 400 (Bad Request)} if the generateBill is not valid,
     * or with status {@code 500 (Internal Server Error)} if the generateBill couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/generate-bills/{id}")
    public ResponseEntity<GenerateBill> updateGenerateBill(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody GenerateBill generateBill
    ) throws URISyntaxException {
        log.debug("REST request to update GenerateBill : {}, {}", id, generateBill);
        if (generateBill.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, generateBill.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!generateBillRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        GenerateBill result = generateBillRepository.save(generateBill);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, generateBill.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /generate-bills/:id} : Partial updates given fields of an existing generateBill, field will ignore if it is null
     *
     * @param id the id of the generateBill to save.
     * @param generateBill the generateBill to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated generateBill,
     * or with status {@code 400 (Bad Request)} if the generateBill is not valid,
     * or with status {@code 404 (Not Found)} if the generateBill is not found,
     * or with status {@code 500 (Internal Server Error)} if the generateBill couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/generate-bills/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<GenerateBill> partialUpdateGenerateBill(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody GenerateBill generateBill
    ) throws URISyntaxException {
        log.debug("REST request to partial update GenerateBill partially : {}, {}", id, generateBill);
        if (generateBill.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, generateBill.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!generateBillRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<GenerateBill> result = generateBillRepository
            .findById(generateBill.getId())
            .map(existingGenerateBill -> {
                if (generateBill.getAmountPending() != null) {
                    existingGenerateBill.setAmountPending(generateBill.getAmountPending());
                }
                if (generateBill.getSendNotification() != null) {
                    existingGenerateBill.setSendNotification(generateBill.getSendNotification());
                }
                if (generateBill.getElectricityUnit() != null) {
                    existingGenerateBill.setElectricityUnit(generateBill.getElectricityUnit());
                }

                return existingGenerateBill;
            })
            .map(generateBillRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, generateBill.getId().toString())
        );
    }

    /**
     * {@code GET  /generate-bills} : get all the generateBills.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of generateBills in body.
     */
    @GetMapping("/generate-bills")
    public List<GenerateBill> getAllGenerateBills() {
        log.debug("REST request to get all GenerateBills");
        return generateBillRepository.findAll();
    }

    /**
     * {@code GET  /generate-bills/:id} : get the "id" generateBill.
     *
     * @param id the id of the generateBill to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the generateBill, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/generate-bills/{id}")
    public ResponseEntity<GenerateBill> getGenerateBill(@PathVariable Long id) {
        log.debug("REST request to get GenerateBill : {}", id);
        Optional<GenerateBill> generateBill = generateBillRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(generateBill);
    }

    /**
     * {@code DELETE  /generate-bills/:id} : delete the "id" generateBill.
     *
     * @param id the id of the generateBill to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/generate-bills/{id}")
    public ResponseEntity<Void> deleteGenerateBill(@PathVariable Long id) {
        log.debug("REST request to delete GenerateBill : {}", id);
        generateBillRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
