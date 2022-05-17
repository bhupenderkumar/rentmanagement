package com.rentmanagement.web.rest;

import com.rentmanagement.domain.Building;
import com.rentmanagement.repository.BuildingRepository;
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
 * REST controller for managing {@link com.rentmanagement.domain.Building}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class BuildingResource {

    private final Logger log = LoggerFactory.getLogger(BuildingResource.class);

    private static final String ENTITY_NAME = "building";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BuildingRepository buildingRepository;

    public BuildingResource(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    /**
     * {@code POST  /buildings} : Create a new building.
     *
     * @param building the building to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new building, or with status {@code 400 (Bad Request)} if the building has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/buildings")
    public ResponseEntity<Building> createBuilding(@Valid @RequestBody Building building) throws URISyntaxException {
        log.debug("REST request to save Building : {}", building);
        if (building.getId() != null) {
            throw new BadRequestAlertException("A new building cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Building result = buildingRepository.save(building);
        return ResponseEntity
            .created(new URI("/api/buildings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /buildings/:id} : Updates an existing building.
     *
     * @param id the id of the building to save.
     * @param building the building to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated building,
     * or with status {@code 400 (Bad Request)} if the building is not valid,
     * or with status {@code 500 (Internal Server Error)} if the building couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/buildings/{id}")
    public ResponseEntity<Building> updateBuilding(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Building building
    ) throws URISyntaxException {
        log.debug("REST request to update Building : {}, {}", id, building);
        if (building.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, building.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!buildingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Building result = buildingRepository.save(building);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, building.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /buildings/:id} : Partial updates given fields of an existing building, field will ignore if it is null
     *
     * @param id the id of the building to save.
     * @param building the building to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated building,
     * or with status {@code 400 (Bad Request)} if the building is not valid,
     * or with status {@code 404 (Not Found)} if the building is not found,
     * or with status {@code 500 (Internal Server Error)} if the building couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/buildings/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Building> partialUpdateBuilding(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Building building
    ) throws URISyntaxException {
        log.debug("REST request to partial update Building partially : {}, {}", id, building);
        if (building.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, building.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!buildingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Building> result = buildingRepository
            .findById(building.getId())
            .map(existingBuilding -> {
                if (building.getBuildingName() != null) {
                    existingBuilding.setBuildingName(building.getBuildingName());
                }

                return existingBuilding;
            })
            .map(buildingRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, building.getId().toString())
        );
    }

    /**
     * {@code GET  /buildings} : get all the buildings.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of buildings in body.
     */
    @GetMapping("/buildings")
    public List<Building> getAllBuildings() {
        log.debug("REST request to get all Buildings");
        return buildingRepository.findAll();
    }

    /**
     * {@code GET  /buildings/:id} : get the "id" building.
     *
     * @param id the id of the building to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the building, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/buildings/{id}")
    public ResponseEntity<Building> getBuilding(@PathVariable Long id) {
        log.debug("REST request to get Building : {}", id);
        Optional<Building> building = buildingRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(building);
    }

    /**
     * {@code DELETE  /buildings/:id} : delete the "id" building.
     *
     * @param id the id of the building to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/buildings/{id}")
    public ResponseEntity<Void> deleteBuilding(@PathVariable Long id) {
        log.debug("REST request to delete Building : {}", id);
        buildingRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
