package com.rentmanagement.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.rentmanagement.IntegrationTest;
import com.rentmanagement.domain.GenerateBill;
import com.rentmanagement.repository.GenerateBillRepository;
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
 * Integration tests for the {@link GenerateBillResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GenerateBillResourceIT {

    private static final Double DEFAULT_AMOUNT_PENDING = 1D;
    private static final Double UPDATED_AMOUNT_PENDING = 2D;

    private static final Boolean DEFAULT_SEND_NOTIFICATION = false;
    private static final Boolean UPDATED_SEND_NOTIFICATION = true;

    private static final String ENTITY_API_URL = "/api/generate-bills";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private GenerateBillRepository generateBillRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGenerateBillMockMvc;

    private GenerateBill generateBill;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GenerateBill createEntity(EntityManager em) {
        GenerateBill generateBill = new GenerateBill().amountPending(DEFAULT_AMOUNT_PENDING).sendNotification(DEFAULT_SEND_NOTIFICATION);
        return generateBill;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GenerateBill createUpdatedEntity(EntityManager em) {
        GenerateBill generateBill = new GenerateBill().amountPending(UPDATED_AMOUNT_PENDING).sendNotification(UPDATED_SEND_NOTIFICATION);
        return generateBill;
    }

    @BeforeEach
    public void initTest() {
        generateBill = createEntity(em);
    }

    @Test
    @Transactional
    void createGenerateBill() throws Exception {
        int databaseSizeBeforeCreate = generateBillRepository.findAll().size();
        // Create the GenerateBill
        restGenerateBillMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(generateBill)))
            .andExpect(status().isCreated());

        // Validate the GenerateBill in the database
        List<GenerateBill> generateBillList = generateBillRepository.findAll();
        assertThat(generateBillList).hasSize(databaseSizeBeforeCreate + 1);
        GenerateBill testGenerateBill = generateBillList.get(generateBillList.size() - 1);
        assertThat(testGenerateBill.getAmountPending()).isEqualTo(DEFAULT_AMOUNT_PENDING);
        assertThat(testGenerateBill.getSendNotification()).isEqualTo(DEFAULT_SEND_NOTIFICATION);
    }

    @Test
    @Transactional
    void createGenerateBillWithExistingId() throws Exception {
        // Create the GenerateBill with an existing ID
        generateBill.setId(1L);

        int databaseSizeBeforeCreate = generateBillRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGenerateBillMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(generateBill)))
            .andExpect(status().isBadRequest());

        // Validate the GenerateBill in the database
        List<GenerateBill> generateBillList = generateBillRepository.findAll();
        assertThat(generateBillList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllGenerateBills() throws Exception {
        // Initialize the database
        generateBillRepository.saveAndFlush(generateBill);

        // Get all the generateBillList
        restGenerateBillMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(generateBill.getId().intValue())))
            .andExpect(jsonPath("$.[*].amountPending").value(hasItem(DEFAULT_AMOUNT_PENDING.doubleValue())))
            .andExpect(jsonPath("$.[*].sendNotification").value(hasItem(DEFAULT_SEND_NOTIFICATION.booleanValue())));
    }

    @Test
    @Transactional
    void getGenerateBill() throws Exception {
        // Initialize the database
        generateBillRepository.saveAndFlush(generateBill);

        // Get the generateBill
        restGenerateBillMockMvc
            .perform(get(ENTITY_API_URL_ID, generateBill.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(generateBill.getId().intValue()))
            .andExpect(jsonPath("$.amountPending").value(DEFAULT_AMOUNT_PENDING.doubleValue()))
            .andExpect(jsonPath("$.sendNotification").value(DEFAULT_SEND_NOTIFICATION.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingGenerateBill() throws Exception {
        // Get the generateBill
        restGenerateBillMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewGenerateBill() throws Exception {
        // Initialize the database
        generateBillRepository.saveAndFlush(generateBill);

        int databaseSizeBeforeUpdate = generateBillRepository.findAll().size();

        // Update the generateBill
        GenerateBill updatedGenerateBill = generateBillRepository.findById(generateBill.getId()).get();
        // Disconnect from session so that the updates on updatedGenerateBill are not directly saved in db
        em.detach(updatedGenerateBill);
        updatedGenerateBill.amountPending(UPDATED_AMOUNT_PENDING).sendNotification(UPDATED_SEND_NOTIFICATION);

        restGenerateBillMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedGenerateBill.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedGenerateBill))
            )
            .andExpect(status().isOk());

        // Validate the GenerateBill in the database
        List<GenerateBill> generateBillList = generateBillRepository.findAll();
        assertThat(generateBillList).hasSize(databaseSizeBeforeUpdate);
        GenerateBill testGenerateBill = generateBillList.get(generateBillList.size() - 1);
        assertThat(testGenerateBill.getAmountPending()).isEqualTo(UPDATED_AMOUNT_PENDING);
        assertThat(testGenerateBill.getSendNotification()).isEqualTo(UPDATED_SEND_NOTIFICATION);
    }

    @Test
    @Transactional
    void putNonExistingGenerateBill() throws Exception {
        int databaseSizeBeforeUpdate = generateBillRepository.findAll().size();
        generateBill.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGenerateBillMockMvc
            .perform(
                put(ENTITY_API_URL_ID, generateBill.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(generateBill))
            )
            .andExpect(status().isBadRequest());

        // Validate the GenerateBill in the database
        List<GenerateBill> generateBillList = generateBillRepository.findAll();
        assertThat(generateBillList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGenerateBill() throws Exception {
        int databaseSizeBeforeUpdate = generateBillRepository.findAll().size();
        generateBill.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGenerateBillMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(generateBill))
            )
            .andExpect(status().isBadRequest());

        // Validate the GenerateBill in the database
        List<GenerateBill> generateBillList = generateBillRepository.findAll();
        assertThat(generateBillList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGenerateBill() throws Exception {
        int databaseSizeBeforeUpdate = generateBillRepository.findAll().size();
        generateBill.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGenerateBillMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(generateBill)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the GenerateBill in the database
        List<GenerateBill> generateBillList = generateBillRepository.findAll();
        assertThat(generateBillList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGenerateBillWithPatch() throws Exception {
        // Initialize the database
        generateBillRepository.saveAndFlush(generateBill);

        int databaseSizeBeforeUpdate = generateBillRepository.findAll().size();

        // Update the generateBill using partial update
        GenerateBill partialUpdatedGenerateBill = new GenerateBill();
        partialUpdatedGenerateBill.setId(generateBill.getId());

        partialUpdatedGenerateBill.amountPending(UPDATED_AMOUNT_PENDING);

        restGenerateBillMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGenerateBill.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGenerateBill))
            )
            .andExpect(status().isOk());

        // Validate the GenerateBill in the database
        List<GenerateBill> generateBillList = generateBillRepository.findAll();
        assertThat(generateBillList).hasSize(databaseSizeBeforeUpdate);
        GenerateBill testGenerateBill = generateBillList.get(generateBillList.size() - 1);
        assertThat(testGenerateBill.getAmountPending()).isEqualTo(UPDATED_AMOUNT_PENDING);
        assertThat(testGenerateBill.getSendNotification()).isEqualTo(DEFAULT_SEND_NOTIFICATION);
    }

    @Test
    @Transactional
    void fullUpdateGenerateBillWithPatch() throws Exception {
        // Initialize the database
        generateBillRepository.saveAndFlush(generateBill);

        int databaseSizeBeforeUpdate = generateBillRepository.findAll().size();

        // Update the generateBill using partial update
        GenerateBill partialUpdatedGenerateBill = new GenerateBill();
        partialUpdatedGenerateBill.setId(generateBill.getId());

        partialUpdatedGenerateBill.amountPending(UPDATED_AMOUNT_PENDING).sendNotification(UPDATED_SEND_NOTIFICATION);

        restGenerateBillMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGenerateBill.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGenerateBill))
            )
            .andExpect(status().isOk());

        // Validate the GenerateBill in the database
        List<GenerateBill> generateBillList = generateBillRepository.findAll();
        assertThat(generateBillList).hasSize(databaseSizeBeforeUpdate);
        GenerateBill testGenerateBill = generateBillList.get(generateBillList.size() - 1);
        assertThat(testGenerateBill.getAmountPending()).isEqualTo(UPDATED_AMOUNT_PENDING);
        assertThat(testGenerateBill.getSendNotification()).isEqualTo(UPDATED_SEND_NOTIFICATION);
    }

    @Test
    @Transactional
    void patchNonExistingGenerateBill() throws Exception {
        int databaseSizeBeforeUpdate = generateBillRepository.findAll().size();
        generateBill.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGenerateBillMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, generateBill.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(generateBill))
            )
            .andExpect(status().isBadRequest());

        // Validate the GenerateBill in the database
        List<GenerateBill> generateBillList = generateBillRepository.findAll();
        assertThat(generateBillList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGenerateBill() throws Exception {
        int databaseSizeBeforeUpdate = generateBillRepository.findAll().size();
        generateBill.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGenerateBillMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(generateBill))
            )
            .andExpect(status().isBadRequest());

        // Validate the GenerateBill in the database
        List<GenerateBill> generateBillList = generateBillRepository.findAll();
        assertThat(generateBillList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGenerateBill() throws Exception {
        int databaseSizeBeforeUpdate = generateBillRepository.findAll().size();
        generateBill.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGenerateBillMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(generateBill))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the GenerateBill in the database
        List<GenerateBill> generateBillList = generateBillRepository.findAll();
        assertThat(generateBillList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGenerateBill() throws Exception {
        // Initialize the database
        generateBillRepository.saveAndFlush(generateBill);

        int databaseSizeBeforeDelete = generateBillRepository.findAll().size();

        // Delete the generateBill
        restGenerateBillMockMvc
            .perform(delete(ENTITY_API_URL_ID, generateBill.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<GenerateBill> generateBillList = generateBillRepository.findAll();
        assertThat(generateBillList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
