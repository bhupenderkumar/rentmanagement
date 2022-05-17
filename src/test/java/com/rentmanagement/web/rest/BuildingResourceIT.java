package com.rentmanagement.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.rentmanagement.IntegrationTest;
import com.rentmanagement.domain.Building;
import com.rentmanagement.domain.Location;
import com.rentmanagement.domain.Room;
import com.rentmanagement.repository.BuildingRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BuildingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BuildingResourceIT {

    private static final String DEFAULT_BUILDING_NAME = "AAAAAAAAAA";
    private static final String UPDATED_BUILDING_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/buildings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBuildingMockMvc;

    private Building building;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Building createEntity(EntityManager em) {
        Building building = new Building().buildingName(DEFAULT_BUILDING_NAME);
        // Add required entity
        Location location;
        if (TestUtil.findAll(em, Location.class).isEmpty()) {
            location = LocationResourceIT.createEntity(em);
            em.persist(location);
            em.flush();
        } else {
            location = TestUtil.findAll(em, Location.class).get(0);
        }
        building.setAddressId(location);
        // Add required entity
        Room room;
        if (TestUtil.findAll(em, Room.class).isEmpty()) {
            room = RoomResourceIT.createEntity(em);
            em.persist(room);
            em.flush();
        } else {
            room = TestUtil.findAll(em, Room.class).get(0);
        }
        building.getBuildings().add(room);
        return building;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Building createUpdatedEntity(EntityManager em) {
        Building building = new Building().buildingName(UPDATED_BUILDING_NAME);
        // Add required entity
        Location location;
        if (TestUtil.findAll(em, Location.class).isEmpty()) {
            location = LocationResourceIT.createUpdatedEntity(em);
            em.persist(location);
            em.flush();
        } else {
            location = TestUtil.findAll(em, Location.class).get(0);
        }
        building.setAddressId(location);
        // Add required entity
        Room room;
        if (TestUtil.findAll(em, Room.class).isEmpty()) {
            room = RoomResourceIT.createUpdatedEntity(em);
            em.persist(room);
            em.flush();
        } else {
            room = TestUtil.findAll(em, Room.class).get(0);
        }
        building.getBuildings().add(room);
        return building;
    }

    @BeforeEach
    public void initTest() {
        building = createEntity(em);
    }

    @Test
    @Transactional
    void createBuilding() throws Exception {
        int databaseSizeBeforeCreate = buildingRepository.findAll().size();
        // Create the Building
        restBuildingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(building)))
            .andExpect(status().isCreated());

        // Validate the Building in the database
        List<Building> buildingList = buildingRepository.findAll();
        assertThat(buildingList).hasSize(databaseSizeBeforeCreate + 1);
        Building testBuilding = buildingList.get(buildingList.size() - 1);
        assertThat(testBuilding.getBuildingName()).isEqualTo(DEFAULT_BUILDING_NAME);
    }

    @Test
    @Transactional
    void createBuildingWithExistingId() throws Exception {
        // Create the Building with an existing ID
        building.setId(1L);

        int databaseSizeBeforeCreate = buildingRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBuildingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(building)))
            .andExpect(status().isBadRequest());

        // Validate the Building in the database
        List<Building> buildingList = buildingRepository.findAll();
        assertThat(buildingList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkBuildingNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = buildingRepository.findAll().size();
        // set the field null
        building.setBuildingName(null);

        // Create the Building, which fails.

        restBuildingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(building)))
            .andExpect(status().isBadRequest());

        List<Building> buildingList = buildingRepository.findAll();
        assertThat(buildingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBuildings() throws Exception {
        // Initialize the database
        buildingRepository.saveAndFlush(building);

        // Get all the buildingList
        restBuildingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(building.getId().intValue())))
            .andExpect(jsonPath("$.[*].buildingName").value(hasItem(DEFAULT_BUILDING_NAME)));
    }

    @Test
    @Transactional
    void getBuilding() throws Exception {
        // Initialize the database
        buildingRepository.saveAndFlush(building);

        // Get the building
        restBuildingMockMvc
            .perform(get(ENTITY_API_URL_ID, building.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(building.getId().intValue()))
            .andExpect(jsonPath("$.buildingName").value(DEFAULT_BUILDING_NAME));
    }

    @Test
    @Transactional
    void getNonExistingBuilding() throws Exception {
        // Get the building
        restBuildingMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewBuilding() throws Exception {
        // Initialize the database
        buildingRepository.saveAndFlush(building);

        int databaseSizeBeforeUpdate = buildingRepository.findAll().size();

        // Update the building
        Building updatedBuilding = buildingRepository.findById(building.getId()).get();
        // Disconnect from session so that the updates on updatedBuilding are not directly saved in db
        em.detach(updatedBuilding);
        updatedBuilding.buildingName(UPDATED_BUILDING_NAME);

        restBuildingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBuilding.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBuilding))
            )
            .andExpect(status().isOk());

        // Validate the Building in the database
        List<Building> buildingList = buildingRepository.findAll();
        assertThat(buildingList).hasSize(databaseSizeBeforeUpdate);
        Building testBuilding = buildingList.get(buildingList.size() - 1);
        assertThat(testBuilding.getBuildingName()).isEqualTo(UPDATED_BUILDING_NAME);
    }

    @Test
    @Transactional
    void putNonExistingBuilding() throws Exception {
        int databaseSizeBeforeUpdate = buildingRepository.findAll().size();
        building.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBuildingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, building.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(building))
            )
            .andExpect(status().isBadRequest());

        // Validate the Building in the database
        List<Building> buildingList = buildingRepository.findAll();
        assertThat(buildingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBuilding() throws Exception {
        int databaseSizeBeforeUpdate = buildingRepository.findAll().size();
        building.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBuildingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(building))
            )
            .andExpect(status().isBadRequest());

        // Validate the Building in the database
        List<Building> buildingList = buildingRepository.findAll();
        assertThat(buildingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBuilding() throws Exception {
        int databaseSizeBeforeUpdate = buildingRepository.findAll().size();
        building.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBuildingMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(building)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Building in the database
        List<Building> buildingList = buildingRepository.findAll();
        assertThat(buildingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBuildingWithPatch() throws Exception {
        // Initialize the database
        buildingRepository.saveAndFlush(building);

        int databaseSizeBeforeUpdate = buildingRepository.findAll().size();

        // Update the building using partial update
        Building partialUpdatedBuilding = new Building();
        partialUpdatedBuilding.setId(building.getId());

        partialUpdatedBuilding.buildingName(UPDATED_BUILDING_NAME);

        restBuildingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBuilding.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBuilding))
            )
            .andExpect(status().isOk());

        // Validate the Building in the database
        List<Building> buildingList = buildingRepository.findAll();
        assertThat(buildingList).hasSize(databaseSizeBeforeUpdate);
        Building testBuilding = buildingList.get(buildingList.size() - 1);
        assertThat(testBuilding.getBuildingName()).isEqualTo(UPDATED_BUILDING_NAME);
    }

    @Test
    @Transactional
    void fullUpdateBuildingWithPatch() throws Exception {
        // Initialize the database
        buildingRepository.saveAndFlush(building);

        int databaseSizeBeforeUpdate = buildingRepository.findAll().size();

        // Update the building using partial update
        Building partialUpdatedBuilding = new Building();
        partialUpdatedBuilding.setId(building.getId());

        partialUpdatedBuilding.buildingName(UPDATED_BUILDING_NAME);

        restBuildingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBuilding.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBuilding))
            )
            .andExpect(status().isOk());

        // Validate the Building in the database
        List<Building> buildingList = buildingRepository.findAll();
        assertThat(buildingList).hasSize(databaseSizeBeforeUpdate);
        Building testBuilding = buildingList.get(buildingList.size() - 1);
        assertThat(testBuilding.getBuildingName()).isEqualTo(UPDATED_BUILDING_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingBuilding() throws Exception {
        int databaseSizeBeforeUpdate = buildingRepository.findAll().size();
        building.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBuildingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, building.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(building))
            )
            .andExpect(status().isBadRequest());

        // Validate the Building in the database
        List<Building> buildingList = buildingRepository.findAll();
        assertThat(buildingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBuilding() throws Exception {
        int databaseSizeBeforeUpdate = buildingRepository.findAll().size();
        building.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBuildingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(building))
            )
            .andExpect(status().isBadRequest());

        // Validate the Building in the database
        List<Building> buildingList = buildingRepository.findAll();
        assertThat(buildingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBuilding() throws Exception {
        int databaseSizeBeforeUpdate = buildingRepository.findAll().size();
        building.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBuildingMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(building)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Building in the database
        List<Building> buildingList = buildingRepository.findAll();
        assertThat(buildingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBuilding() throws Exception {
        // Initialize the database
        buildingRepository.saveAndFlush(building);

        int databaseSizeBeforeDelete = buildingRepository.findAll().size();

        // Delete the building
        restBuildingMockMvc
            .perform(delete(ENTITY_API_URL_ID, building.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Building> buildingList = buildingRepository.findAll();
        assertThat(buildingList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
