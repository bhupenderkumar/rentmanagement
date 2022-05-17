package com.rentmanagement.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.rentmanagement.IntegrationTest;
import com.rentmanagement.domain.Owner;
import com.rentmanagement.repository.OwnerRepository;
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
 * Integration tests for the {@link OwnerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OwnerResourceIT {

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL_ADDRESS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/owners";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOwnerMockMvc;

    private Owner owner;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Owner createEntity(EntityManager em) {
        Owner owner = new Owner().phoneNumber(DEFAULT_PHONE_NUMBER).emailAddress(DEFAULT_EMAIL_ADDRESS);
        return owner;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Owner createUpdatedEntity(EntityManager em) {
        Owner owner = new Owner().phoneNumber(UPDATED_PHONE_NUMBER).emailAddress(UPDATED_EMAIL_ADDRESS);
        return owner;
    }

    @BeforeEach
    public void initTest() {
        owner = createEntity(em);
    }

    @Test
    @Transactional
    void createOwner() throws Exception {
        int databaseSizeBeforeCreate = ownerRepository.findAll().size();
        // Create the Owner
        restOwnerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(owner)))
            .andExpect(status().isCreated());

        // Validate the Owner in the database
        List<Owner> ownerList = ownerRepository.findAll();
        assertThat(ownerList).hasSize(databaseSizeBeforeCreate + 1);
        Owner testOwner = ownerList.get(ownerList.size() - 1);
        assertThat(testOwner.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testOwner.getEmailAddress()).isEqualTo(DEFAULT_EMAIL_ADDRESS);
    }

    @Test
    @Transactional
    void createOwnerWithExistingId() throws Exception {
        // Create the Owner with an existing ID
        owner.setId(1L);

        int databaseSizeBeforeCreate = ownerRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOwnerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(owner)))
            .andExpect(status().isBadRequest());

        // Validate the Owner in the database
        List<Owner> ownerList = ownerRepository.findAll();
        assertThat(ownerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPhoneNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = ownerRepository.findAll().size();
        // set the field null
        owner.setPhoneNumber(null);

        // Create the Owner, which fails.

        restOwnerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(owner)))
            .andExpect(status().isBadRequest());

        List<Owner> ownerList = ownerRepository.findAll();
        assertThat(ownerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOwners() throws Exception {
        // Initialize the database
        ownerRepository.saveAndFlush(owner);

        // Get all the ownerList
        restOwnerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(owner.getId().intValue())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].emailAddress").value(hasItem(DEFAULT_EMAIL_ADDRESS)));
    }

    @Test
    @Transactional
    void getOwner() throws Exception {
        // Initialize the database
        ownerRepository.saveAndFlush(owner);

        // Get the owner
        restOwnerMockMvc
            .perform(get(ENTITY_API_URL_ID, owner.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(owner.getId().intValue()))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.emailAddress").value(DEFAULT_EMAIL_ADDRESS));
    }

    @Test
    @Transactional
    void getNonExistingOwner() throws Exception {
        // Get the owner
        restOwnerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewOwner() throws Exception {
        // Initialize the database
        ownerRepository.saveAndFlush(owner);

        int databaseSizeBeforeUpdate = ownerRepository.findAll().size();

        // Update the owner
        Owner updatedOwner = ownerRepository.findById(owner.getId()).get();
        // Disconnect from session so that the updates on updatedOwner are not directly saved in db
        em.detach(updatedOwner);
        updatedOwner.phoneNumber(UPDATED_PHONE_NUMBER).emailAddress(UPDATED_EMAIL_ADDRESS);

        restOwnerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedOwner.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedOwner))
            )
            .andExpect(status().isOk());

        // Validate the Owner in the database
        List<Owner> ownerList = ownerRepository.findAll();
        assertThat(ownerList).hasSize(databaseSizeBeforeUpdate);
        Owner testOwner = ownerList.get(ownerList.size() - 1);
        assertThat(testOwner.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testOwner.getEmailAddress()).isEqualTo(UPDATED_EMAIL_ADDRESS);
    }

    @Test
    @Transactional
    void putNonExistingOwner() throws Exception {
        int databaseSizeBeforeUpdate = ownerRepository.findAll().size();
        owner.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOwnerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, owner.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(owner))
            )
            .andExpect(status().isBadRequest());

        // Validate the Owner in the database
        List<Owner> ownerList = ownerRepository.findAll();
        assertThat(ownerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOwner() throws Exception {
        int databaseSizeBeforeUpdate = ownerRepository.findAll().size();
        owner.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOwnerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(owner))
            )
            .andExpect(status().isBadRequest());

        // Validate the Owner in the database
        List<Owner> ownerList = ownerRepository.findAll();
        assertThat(ownerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOwner() throws Exception {
        int databaseSizeBeforeUpdate = ownerRepository.findAll().size();
        owner.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOwnerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(owner)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Owner in the database
        List<Owner> ownerList = ownerRepository.findAll();
        assertThat(ownerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOwnerWithPatch() throws Exception {
        // Initialize the database
        ownerRepository.saveAndFlush(owner);

        int databaseSizeBeforeUpdate = ownerRepository.findAll().size();

        // Update the owner using partial update
        Owner partialUpdatedOwner = new Owner();
        partialUpdatedOwner.setId(owner.getId());

        partialUpdatedOwner.emailAddress(UPDATED_EMAIL_ADDRESS);

        restOwnerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOwner.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOwner))
            )
            .andExpect(status().isOk());

        // Validate the Owner in the database
        List<Owner> ownerList = ownerRepository.findAll();
        assertThat(ownerList).hasSize(databaseSizeBeforeUpdate);
        Owner testOwner = ownerList.get(ownerList.size() - 1);
        assertThat(testOwner.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testOwner.getEmailAddress()).isEqualTo(UPDATED_EMAIL_ADDRESS);
    }

    @Test
    @Transactional
    void fullUpdateOwnerWithPatch() throws Exception {
        // Initialize the database
        ownerRepository.saveAndFlush(owner);

        int databaseSizeBeforeUpdate = ownerRepository.findAll().size();

        // Update the owner using partial update
        Owner partialUpdatedOwner = new Owner();
        partialUpdatedOwner.setId(owner.getId());

        partialUpdatedOwner.phoneNumber(UPDATED_PHONE_NUMBER).emailAddress(UPDATED_EMAIL_ADDRESS);

        restOwnerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOwner.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOwner))
            )
            .andExpect(status().isOk());

        // Validate the Owner in the database
        List<Owner> ownerList = ownerRepository.findAll();
        assertThat(ownerList).hasSize(databaseSizeBeforeUpdate);
        Owner testOwner = ownerList.get(ownerList.size() - 1);
        assertThat(testOwner.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testOwner.getEmailAddress()).isEqualTo(UPDATED_EMAIL_ADDRESS);
    }

    @Test
    @Transactional
    void patchNonExistingOwner() throws Exception {
        int databaseSizeBeforeUpdate = ownerRepository.findAll().size();
        owner.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOwnerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, owner.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(owner))
            )
            .andExpect(status().isBadRequest());

        // Validate the Owner in the database
        List<Owner> ownerList = ownerRepository.findAll();
        assertThat(ownerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOwner() throws Exception {
        int databaseSizeBeforeUpdate = ownerRepository.findAll().size();
        owner.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOwnerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(owner))
            )
            .andExpect(status().isBadRequest());

        // Validate the Owner in the database
        List<Owner> ownerList = ownerRepository.findAll();
        assertThat(ownerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOwner() throws Exception {
        int databaseSizeBeforeUpdate = ownerRepository.findAll().size();
        owner.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOwnerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(owner)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Owner in the database
        List<Owner> ownerList = ownerRepository.findAll();
        assertThat(ownerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOwner() throws Exception {
        // Initialize the database
        ownerRepository.saveAndFlush(owner);

        int databaseSizeBeforeDelete = ownerRepository.findAll().size();

        // Delete the owner
        restOwnerMockMvc
            .perform(delete(ENTITY_API_URL_ID, owner.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Owner> ownerList = ownerRepository.findAll();
        assertThat(ownerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
