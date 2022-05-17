package com.rentmanagement.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.rentmanagement.IntegrationTest;
import com.rentmanagement.domain.Room;
import com.rentmanagement.domain.Tenant;
import com.rentmanagement.domain.User;
import com.rentmanagement.repository.TenantRepository;
import com.rentmanagement.repository.UserRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link TenantResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TenantResourceIT {

    private static final String DEFAULT_TENANT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_TENANT_NAME = "BBBBBBBBBB";

    private static final byte[] DEFAULT_ADDRESS_PROFF = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_ADDRESS_PROFF = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_ADDRESS_PROFF_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_ADDRESS_PROFF_CONTENT_TYPE = "image/png";

    private static final Integer DEFAULT_NUMBEROF_FAMILY_MEMBERS = 1;
    private static final Integer UPDATED_NUMBEROF_FAMILY_MEMBERS = 2;

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_RENT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_RENT_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Double DEFAULT_RENT_AMOUNT = 1D;
    private static final Double UPDATED_RENT_AMOUNT = 2D;

    private static final Integer DEFAULT_ELECTRICITY_UNIT_RATE = 1;
    private static final Integer UPDATED_ELECTRICITY_UNIT_RATE = 2;

    private static final Double DEFAULT_STARTING_ELECTRICITY_UNIT = 1D;
    private static final Double UPDATED_STARTING_ELECTRICITY_UNIT = 2D;

    private static final String DEFAULT_ANY_OTHER_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_ANY_OTHER_DETAILS = "BBBBBBBBBB";

    private static final Boolean DEFAULT_SEND_NOTIFICATION = false;
    private static final Boolean UPDATED_SEND_NOTIFICATION = true;

    private static final String DEFAULT_EMAIL_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_EMERGENCY_CONTACT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_EMERGENCY_CONTACT_NUMBER = "BBBBBBBBBB";

    private static final Double DEFAULT_OUT_STANDING_AMOUNT = 1D;
    private static final Double UPDATED_OUT_STANDING_AMOUNT = 2D;

    private static final Boolean DEFAULT_MONTH_END_CALCULATION = false;
    private static final Boolean UPDATED_MONTH_END_CALCULATION = true;

    private static final Boolean DEFAULT_CALCULATE_ON_DATE = false;
    private static final Boolean UPDATED_CALCULATE_ON_DATE = true;

    private static final String ENTITY_API_URL = "/api/tenants";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private TenantRepository tenantRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTenantMockMvc;

    private Tenant tenant;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tenant createEntity(EntityManager em) {
        Tenant tenant = new Tenant()
            .tenantName(DEFAULT_TENANT_NAME)
            .addressProff(DEFAULT_ADDRESS_PROFF)
            .addressProffContentType(DEFAULT_ADDRESS_PROFF_CONTENT_TYPE)
            .numberofFamilyMembers(DEFAULT_NUMBEROF_FAMILY_MEMBERS)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .rentStartDate(DEFAULT_RENT_START_DATE)
            .rentAmount(DEFAULT_RENT_AMOUNT)
            .electricityUnitRate(DEFAULT_ELECTRICITY_UNIT_RATE)
            .startingElectricityUnit(DEFAULT_STARTING_ELECTRICITY_UNIT)
            .anyOtherDetails(DEFAULT_ANY_OTHER_DETAILS)
            .sendNotification(DEFAULT_SEND_NOTIFICATION)
            .emailAddress(DEFAULT_EMAIL_ADDRESS)
            .emergencyContactNumber(DEFAULT_EMERGENCY_CONTACT_NUMBER)
            .outStandingAmount(DEFAULT_OUT_STANDING_AMOUNT)
            .monthEndCalculation(DEFAULT_MONTH_END_CALCULATION)
            .calculateOnDate(DEFAULT_CALCULATE_ON_DATE);
        // Add required entity
        Room room;
        if (TestUtil.findAll(em, Room.class).isEmpty()) {
            room = RoomResourceIT.createEntity(em);
            em.persist(room);
            em.flush();
        } else {
            room = TestUtil.findAll(em, Room.class).get(0);
        }
        tenant.getRooms().add(room);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        tenant.setUser(user);
        return tenant;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tenant createUpdatedEntity(EntityManager em) {
        Tenant tenant = new Tenant()
            .tenantName(UPDATED_TENANT_NAME)
            .addressProff(UPDATED_ADDRESS_PROFF)
            .addressProffContentType(UPDATED_ADDRESS_PROFF_CONTENT_TYPE)
            .numberofFamilyMembers(UPDATED_NUMBEROF_FAMILY_MEMBERS)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .rentStartDate(UPDATED_RENT_START_DATE)
            .rentAmount(UPDATED_RENT_AMOUNT)
            .electricityUnitRate(UPDATED_ELECTRICITY_UNIT_RATE)
            .startingElectricityUnit(UPDATED_STARTING_ELECTRICITY_UNIT)
            .anyOtherDetails(UPDATED_ANY_OTHER_DETAILS)
            .sendNotification(UPDATED_SEND_NOTIFICATION)
            .emailAddress(UPDATED_EMAIL_ADDRESS)
            .emergencyContactNumber(UPDATED_EMERGENCY_CONTACT_NUMBER)
            .outStandingAmount(UPDATED_OUT_STANDING_AMOUNT)
            .monthEndCalculation(UPDATED_MONTH_END_CALCULATION)
            .calculateOnDate(UPDATED_CALCULATE_ON_DATE);
        // Add required entity
        Room room;
        room = RoomResourceIT.createUpdatedEntity(em);
        em.persist(room);
        em.flush();
        tenant.getRooms().add(room);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        tenant.setUser(user);
        return tenant;
    }

    @BeforeEach
    public void initTest() {
        tenant = createEntity(em);
    }

    @Test
    @Transactional
    void createTenant() throws Exception {
        int databaseSizeBeforeCreate = tenantRepository.findAll().size();
        // Create the Tenant
        restTenantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tenant)))
            .andExpect(status().isCreated());

        // Validate the Tenant in the database
        List<Tenant> tenantList = tenantRepository.findAll();
        assertThat(tenantList).hasSize(databaseSizeBeforeCreate + 1);
        Tenant testTenant = tenantList.get(tenantList.size() - 1);
        assertThat(testTenant.getTenantName()).isEqualTo(DEFAULT_TENANT_NAME);
        assertThat(testTenant.getAddressProff()).isEqualTo(DEFAULT_ADDRESS_PROFF);
        assertThat(testTenant.getAddressProffContentType()).isEqualTo(DEFAULT_ADDRESS_PROFF_CONTENT_TYPE);
        assertThat(testTenant.getNumberofFamilyMembers()).isEqualTo(DEFAULT_NUMBEROF_FAMILY_MEMBERS);
        assertThat(testTenant.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testTenant.getRentStartDate()).isEqualTo(DEFAULT_RENT_START_DATE);
        assertThat(testTenant.getRentAmount()).isEqualTo(DEFAULT_RENT_AMOUNT);
        assertThat(testTenant.getElectricityUnitRate()).isEqualTo(DEFAULT_ELECTRICITY_UNIT_RATE);
        assertThat(testTenant.getStartingElectricityUnit()).isEqualTo(DEFAULT_STARTING_ELECTRICITY_UNIT);
        assertThat(testTenant.getAnyOtherDetails()).isEqualTo(DEFAULT_ANY_OTHER_DETAILS);
        assertThat(testTenant.getSendNotification()).isEqualTo(DEFAULT_SEND_NOTIFICATION);
        assertThat(testTenant.getEmailAddress()).isEqualTo(DEFAULT_EMAIL_ADDRESS);
        assertThat(testTenant.getEmergencyContactNumber()).isEqualTo(DEFAULT_EMERGENCY_CONTACT_NUMBER);
        assertThat(testTenant.getOutStandingAmount()).isEqualTo(DEFAULT_OUT_STANDING_AMOUNT);
        assertThat(testTenant.getMonthEndCalculation()).isEqualTo(DEFAULT_MONTH_END_CALCULATION);
        assertThat(testTenant.getCalculateOnDate()).isEqualTo(DEFAULT_CALCULATE_ON_DATE);

        // Validate the id for MapsId, the ids must be same
        assertThat(testTenant.getId()).isEqualTo(testTenant.getUser().getId());
    }

    @Test
    @Transactional
    void createTenantWithExistingId() throws Exception {
        // Create the Tenant with an existing ID
        tenant.setId(1L);

        int databaseSizeBeforeCreate = tenantRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTenantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tenant)))
            .andExpect(status().isBadRequest());

        // Validate the Tenant in the database
        List<Tenant> tenantList = tenantRepository.findAll();
        assertThat(tenantList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void updateTenantMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        tenantRepository.saveAndFlush(tenant);
        int databaseSizeBeforeCreate = tenantRepository.findAll().size();

        // Load the tenant
        Tenant updatedTenant = tenantRepository.findById(tenant.getId()).get();
        assertThat(updatedTenant).isNotNull();
        // Disconnect from session so that the updates on updatedTenant are not directly saved in db
        em.detach(updatedTenant);

        // Update the User with new association value
        updatedTenant.setUser(tenant.getUser());

        // Update the entity
        restTenantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTenant.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTenant))
            )
            .andExpect(status().isOk());

        // Validate the Tenant in the database
        List<Tenant> tenantList = tenantRepository.findAll();
        assertThat(tenantList).hasSize(databaseSizeBeforeCreate);
        Tenant testTenant = tenantList.get(tenantList.size() - 1);
        // Validate the id for MapsId, the ids must be same
        // Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
        // Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
        // assertThat(testTenant.getId()).isEqualTo(testTenant.getUser().getId());
    }

    @Test
    @Transactional
    void checkTenantNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = tenantRepository.findAll().size();
        // set the field null
        tenant.setTenantName(null);

        // Create the Tenant, which fails.

        restTenantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tenant)))
            .andExpect(status().isBadRequest());

        List<Tenant> tenantList = tenantRepository.findAll();
        assertThat(tenantList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPhoneNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = tenantRepository.findAll().size();
        // set the field null
        tenant.setPhoneNumber(null);

        // Create the Tenant, which fails.

        restTenantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tenant)))
            .andExpect(status().isBadRequest());

        List<Tenant> tenantList = tenantRepository.findAll();
        assertThat(tenantList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRentStartDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = tenantRepository.findAll().size();
        // set the field null
        tenant.setRentStartDate(null);

        // Create the Tenant, which fails.

        restTenantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tenant)))
            .andExpect(status().isBadRequest());

        List<Tenant> tenantList = tenantRepository.findAll();
        assertThat(tenantList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRentAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = tenantRepository.findAll().size();
        // set the field null
        tenant.setRentAmount(null);

        // Create the Tenant, which fails.

        restTenantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tenant)))
            .andExpect(status().isBadRequest());

        List<Tenant> tenantList = tenantRepository.findAll();
        assertThat(tenantList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkElectricityUnitRateIsRequired() throws Exception {
        int databaseSizeBeforeTest = tenantRepository.findAll().size();
        // set the field null
        tenant.setElectricityUnitRate(null);

        // Create the Tenant, which fails.

        restTenantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tenant)))
            .andExpect(status().isBadRequest());

        List<Tenant> tenantList = tenantRepository.findAll();
        assertThat(tenantList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartingElectricityUnitIsRequired() throws Exception {
        int databaseSizeBeforeTest = tenantRepository.findAll().size();
        // set the field null
        tenant.setStartingElectricityUnit(null);

        // Create the Tenant, which fails.

        restTenantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tenant)))
            .andExpect(status().isBadRequest());

        List<Tenant> tenantList = tenantRepository.findAll();
        assertThat(tenantList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTenants() throws Exception {
        // Initialize the database
        tenantRepository.saveAndFlush(tenant);

        // Get all the tenantList
        restTenantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tenant.getId().intValue())))
            .andExpect(jsonPath("$.[*].tenantName").value(hasItem(DEFAULT_TENANT_NAME)))
            .andExpect(jsonPath("$.[*].addressProffContentType").value(hasItem(DEFAULT_ADDRESS_PROFF_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].addressProff").value(hasItem(Base64Utils.encodeToString(DEFAULT_ADDRESS_PROFF))))
            .andExpect(jsonPath("$.[*].numberofFamilyMembers").value(hasItem(DEFAULT_NUMBEROF_FAMILY_MEMBERS)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].rentStartDate").value(hasItem(DEFAULT_RENT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].rentAmount").value(hasItem(DEFAULT_RENT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].electricityUnitRate").value(hasItem(DEFAULT_ELECTRICITY_UNIT_RATE)))
            .andExpect(jsonPath("$.[*].startingElectricityUnit").value(hasItem(DEFAULT_STARTING_ELECTRICITY_UNIT.doubleValue())))
            .andExpect(jsonPath("$.[*].anyOtherDetails").value(hasItem(DEFAULT_ANY_OTHER_DETAILS)))
            .andExpect(jsonPath("$.[*].sendNotification").value(hasItem(DEFAULT_SEND_NOTIFICATION.booleanValue())))
            .andExpect(jsonPath("$.[*].emailAddress").value(hasItem(DEFAULT_EMAIL_ADDRESS)))
            .andExpect(jsonPath("$.[*].emergencyContactNumber").value(hasItem(DEFAULT_EMERGENCY_CONTACT_NUMBER)))
            .andExpect(jsonPath("$.[*].outStandingAmount").value(hasItem(DEFAULT_OUT_STANDING_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].monthEndCalculation").value(hasItem(DEFAULT_MONTH_END_CALCULATION.booleanValue())))
            .andExpect(jsonPath("$.[*].calculateOnDate").value(hasItem(DEFAULT_CALCULATE_ON_DATE.booleanValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTenantsWithEagerRelationshipsIsEnabled() throws Exception {
        when(tenantRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTenantMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(tenantRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTenantsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(tenantRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTenantMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(tenantRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getTenant() throws Exception {
        // Initialize the database
        tenantRepository.saveAndFlush(tenant);

        // Get the tenant
        restTenantMockMvc
            .perform(get(ENTITY_API_URL_ID, tenant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tenant.getId().intValue()))
            .andExpect(jsonPath("$.tenantName").value(DEFAULT_TENANT_NAME))
            .andExpect(jsonPath("$.addressProffContentType").value(DEFAULT_ADDRESS_PROFF_CONTENT_TYPE))
            .andExpect(jsonPath("$.addressProff").value(Base64Utils.encodeToString(DEFAULT_ADDRESS_PROFF)))
            .andExpect(jsonPath("$.numberofFamilyMembers").value(DEFAULT_NUMBEROF_FAMILY_MEMBERS))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.rentStartDate").value(DEFAULT_RENT_START_DATE.toString()))
            .andExpect(jsonPath("$.rentAmount").value(DEFAULT_RENT_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.electricityUnitRate").value(DEFAULT_ELECTRICITY_UNIT_RATE))
            .andExpect(jsonPath("$.startingElectricityUnit").value(DEFAULT_STARTING_ELECTRICITY_UNIT.doubleValue()))
            .andExpect(jsonPath("$.anyOtherDetails").value(DEFAULT_ANY_OTHER_DETAILS))
            .andExpect(jsonPath("$.sendNotification").value(DEFAULT_SEND_NOTIFICATION.booleanValue()))
            .andExpect(jsonPath("$.emailAddress").value(DEFAULT_EMAIL_ADDRESS))
            .andExpect(jsonPath("$.emergencyContactNumber").value(DEFAULT_EMERGENCY_CONTACT_NUMBER))
            .andExpect(jsonPath("$.outStandingAmount").value(DEFAULT_OUT_STANDING_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.monthEndCalculation").value(DEFAULT_MONTH_END_CALCULATION.booleanValue()))
            .andExpect(jsonPath("$.calculateOnDate").value(DEFAULT_CALCULATE_ON_DATE.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingTenant() throws Exception {
        // Get the tenant
        restTenantMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTenant() throws Exception {
        // Initialize the database
        tenantRepository.saveAndFlush(tenant);

        int databaseSizeBeforeUpdate = tenantRepository.findAll().size();

        // Update the tenant
        Tenant updatedTenant = tenantRepository.findById(tenant.getId()).get();
        // Disconnect from session so that the updates on updatedTenant are not directly saved in db
        em.detach(updatedTenant);
        updatedTenant
            .tenantName(UPDATED_TENANT_NAME)
            .addressProff(UPDATED_ADDRESS_PROFF)
            .addressProffContentType(UPDATED_ADDRESS_PROFF_CONTENT_TYPE)
            .numberofFamilyMembers(UPDATED_NUMBEROF_FAMILY_MEMBERS)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .rentStartDate(UPDATED_RENT_START_DATE)
            .rentAmount(UPDATED_RENT_AMOUNT)
            .electricityUnitRate(UPDATED_ELECTRICITY_UNIT_RATE)
            .startingElectricityUnit(UPDATED_STARTING_ELECTRICITY_UNIT)
            .anyOtherDetails(UPDATED_ANY_OTHER_DETAILS)
            .sendNotification(UPDATED_SEND_NOTIFICATION)
            .emailAddress(UPDATED_EMAIL_ADDRESS)
            .emergencyContactNumber(UPDATED_EMERGENCY_CONTACT_NUMBER)
            .outStandingAmount(UPDATED_OUT_STANDING_AMOUNT)
            .monthEndCalculation(UPDATED_MONTH_END_CALCULATION)
            .calculateOnDate(UPDATED_CALCULATE_ON_DATE);

        restTenantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTenant.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTenant))
            )
            .andExpect(status().isOk());

        // Validate the Tenant in the database
        List<Tenant> tenantList = tenantRepository.findAll();
        assertThat(tenantList).hasSize(databaseSizeBeforeUpdate);
        Tenant testTenant = tenantList.get(tenantList.size() - 1);
        assertThat(testTenant.getTenantName()).isEqualTo(UPDATED_TENANT_NAME);
        assertThat(testTenant.getAddressProff()).isEqualTo(UPDATED_ADDRESS_PROFF);
        assertThat(testTenant.getAddressProffContentType()).isEqualTo(UPDATED_ADDRESS_PROFF_CONTENT_TYPE);
        assertThat(testTenant.getNumberofFamilyMembers()).isEqualTo(UPDATED_NUMBEROF_FAMILY_MEMBERS);
        assertThat(testTenant.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testTenant.getRentStartDate()).isEqualTo(UPDATED_RENT_START_DATE);
        assertThat(testTenant.getRentAmount()).isEqualTo(UPDATED_RENT_AMOUNT);
        assertThat(testTenant.getElectricityUnitRate()).isEqualTo(UPDATED_ELECTRICITY_UNIT_RATE);
        assertThat(testTenant.getStartingElectricityUnit()).isEqualTo(UPDATED_STARTING_ELECTRICITY_UNIT);
        assertThat(testTenant.getAnyOtherDetails()).isEqualTo(UPDATED_ANY_OTHER_DETAILS);
        assertThat(testTenant.getSendNotification()).isEqualTo(UPDATED_SEND_NOTIFICATION);
        assertThat(testTenant.getEmailAddress()).isEqualTo(UPDATED_EMAIL_ADDRESS);
        assertThat(testTenant.getEmergencyContactNumber()).isEqualTo(UPDATED_EMERGENCY_CONTACT_NUMBER);
        assertThat(testTenant.getOutStandingAmount()).isEqualTo(UPDATED_OUT_STANDING_AMOUNT);
        assertThat(testTenant.getMonthEndCalculation()).isEqualTo(UPDATED_MONTH_END_CALCULATION);
        assertThat(testTenant.getCalculateOnDate()).isEqualTo(UPDATED_CALCULATE_ON_DATE);
    }

    @Test
    @Transactional
    void putNonExistingTenant() throws Exception {
        int databaseSizeBeforeUpdate = tenantRepository.findAll().size();
        tenant.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTenantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tenant.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tenant))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tenant in the database
        List<Tenant> tenantList = tenantRepository.findAll();
        assertThat(tenantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTenant() throws Exception {
        int databaseSizeBeforeUpdate = tenantRepository.findAll().size();
        tenant.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTenantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tenant))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tenant in the database
        List<Tenant> tenantList = tenantRepository.findAll();
        assertThat(tenantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTenant() throws Exception {
        int databaseSizeBeforeUpdate = tenantRepository.findAll().size();
        tenant.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTenantMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tenant)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tenant in the database
        List<Tenant> tenantList = tenantRepository.findAll();
        assertThat(tenantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTenantWithPatch() throws Exception {
        // Initialize the database
        tenantRepository.saveAndFlush(tenant);

        int databaseSizeBeforeUpdate = tenantRepository.findAll().size();

        // Update the tenant using partial update
        Tenant partialUpdatedTenant = new Tenant();
        partialUpdatedTenant.setId(tenant.getId());

        partialUpdatedTenant
            .tenantName(UPDATED_TENANT_NAME)
            .numberofFamilyMembers(UPDATED_NUMBEROF_FAMILY_MEMBERS)
            .rentStartDate(UPDATED_RENT_START_DATE)
            .startingElectricityUnit(UPDATED_STARTING_ELECTRICITY_UNIT)
            .anyOtherDetails(UPDATED_ANY_OTHER_DETAILS)
            .sendNotification(UPDATED_SEND_NOTIFICATION)
            .outStandingAmount(UPDATED_OUT_STANDING_AMOUNT)
            .monthEndCalculation(UPDATED_MONTH_END_CALCULATION);

        restTenantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTenant.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTenant))
            )
            .andExpect(status().isOk());

        // Validate the Tenant in the database
        List<Tenant> tenantList = tenantRepository.findAll();
        assertThat(tenantList).hasSize(databaseSizeBeforeUpdate);
        Tenant testTenant = tenantList.get(tenantList.size() - 1);
        assertThat(testTenant.getTenantName()).isEqualTo(UPDATED_TENANT_NAME);
        assertThat(testTenant.getAddressProff()).isEqualTo(DEFAULT_ADDRESS_PROFF);
        assertThat(testTenant.getAddressProffContentType()).isEqualTo(DEFAULT_ADDRESS_PROFF_CONTENT_TYPE);
        assertThat(testTenant.getNumberofFamilyMembers()).isEqualTo(UPDATED_NUMBEROF_FAMILY_MEMBERS);
        assertThat(testTenant.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testTenant.getRentStartDate()).isEqualTo(UPDATED_RENT_START_DATE);
        assertThat(testTenant.getRentAmount()).isEqualTo(DEFAULT_RENT_AMOUNT);
        assertThat(testTenant.getElectricityUnitRate()).isEqualTo(DEFAULT_ELECTRICITY_UNIT_RATE);
        assertThat(testTenant.getStartingElectricityUnit()).isEqualTo(UPDATED_STARTING_ELECTRICITY_UNIT);
        assertThat(testTenant.getAnyOtherDetails()).isEqualTo(UPDATED_ANY_OTHER_DETAILS);
        assertThat(testTenant.getSendNotification()).isEqualTo(UPDATED_SEND_NOTIFICATION);
        assertThat(testTenant.getEmailAddress()).isEqualTo(DEFAULT_EMAIL_ADDRESS);
        assertThat(testTenant.getEmergencyContactNumber()).isEqualTo(DEFAULT_EMERGENCY_CONTACT_NUMBER);
        assertThat(testTenant.getOutStandingAmount()).isEqualTo(UPDATED_OUT_STANDING_AMOUNT);
        assertThat(testTenant.getMonthEndCalculation()).isEqualTo(UPDATED_MONTH_END_CALCULATION);
        assertThat(testTenant.getCalculateOnDate()).isEqualTo(DEFAULT_CALCULATE_ON_DATE);
    }

    @Test
    @Transactional
    void fullUpdateTenantWithPatch() throws Exception {
        // Initialize the database
        tenantRepository.saveAndFlush(tenant);

        int databaseSizeBeforeUpdate = tenantRepository.findAll().size();

        // Update the tenant using partial update
        Tenant partialUpdatedTenant = new Tenant();
        partialUpdatedTenant.setId(tenant.getId());

        partialUpdatedTenant
            .tenantName(UPDATED_TENANT_NAME)
            .addressProff(UPDATED_ADDRESS_PROFF)
            .addressProffContentType(UPDATED_ADDRESS_PROFF_CONTENT_TYPE)
            .numberofFamilyMembers(UPDATED_NUMBEROF_FAMILY_MEMBERS)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .rentStartDate(UPDATED_RENT_START_DATE)
            .rentAmount(UPDATED_RENT_AMOUNT)
            .electricityUnitRate(UPDATED_ELECTRICITY_UNIT_RATE)
            .startingElectricityUnit(UPDATED_STARTING_ELECTRICITY_UNIT)
            .anyOtherDetails(UPDATED_ANY_OTHER_DETAILS)
            .sendNotification(UPDATED_SEND_NOTIFICATION)
            .emailAddress(UPDATED_EMAIL_ADDRESS)
            .emergencyContactNumber(UPDATED_EMERGENCY_CONTACT_NUMBER)
            .outStandingAmount(UPDATED_OUT_STANDING_AMOUNT)
            .monthEndCalculation(UPDATED_MONTH_END_CALCULATION)
            .calculateOnDate(UPDATED_CALCULATE_ON_DATE);

        restTenantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTenant.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTenant))
            )
            .andExpect(status().isOk());

        // Validate the Tenant in the database
        List<Tenant> tenantList = tenantRepository.findAll();
        assertThat(tenantList).hasSize(databaseSizeBeforeUpdate);
        Tenant testTenant = tenantList.get(tenantList.size() - 1);
        assertThat(testTenant.getTenantName()).isEqualTo(UPDATED_TENANT_NAME);
        assertThat(testTenant.getAddressProff()).isEqualTo(UPDATED_ADDRESS_PROFF);
        assertThat(testTenant.getAddressProffContentType()).isEqualTo(UPDATED_ADDRESS_PROFF_CONTENT_TYPE);
        assertThat(testTenant.getNumberofFamilyMembers()).isEqualTo(UPDATED_NUMBEROF_FAMILY_MEMBERS);
        assertThat(testTenant.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testTenant.getRentStartDate()).isEqualTo(UPDATED_RENT_START_DATE);
        assertThat(testTenant.getRentAmount()).isEqualTo(UPDATED_RENT_AMOUNT);
        assertThat(testTenant.getElectricityUnitRate()).isEqualTo(UPDATED_ELECTRICITY_UNIT_RATE);
        assertThat(testTenant.getStartingElectricityUnit()).isEqualTo(UPDATED_STARTING_ELECTRICITY_UNIT);
        assertThat(testTenant.getAnyOtherDetails()).isEqualTo(UPDATED_ANY_OTHER_DETAILS);
        assertThat(testTenant.getSendNotification()).isEqualTo(UPDATED_SEND_NOTIFICATION);
        assertThat(testTenant.getEmailAddress()).isEqualTo(UPDATED_EMAIL_ADDRESS);
        assertThat(testTenant.getEmergencyContactNumber()).isEqualTo(UPDATED_EMERGENCY_CONTACT_NUMBER);
        assertThat(testTenant.getOutStandingAmount()).isEqualTo(UPDATED_OUT_STANDING_AMOUNT);
        assertThat(testTenant.getMonthEndCalculation()).isEqualTo(UPDATED_MONTH_END_CALCULATION);
        assertThat(testTenant.getCalculateOnDate()).isEqualTo(UPDATED_CALCULATE_ON_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingTenant() throws Exception {
        int databaseSizeBeforeUpdate = tenantRepository.findAll().size();
        tenant.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTenantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tenant.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tenant))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tenant in the database
        List<Tenant> tenantList = tenantRepository.findAll();
        assertThat(tenantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTenant() throws Exception {
        int databaseSizeBeforeUpdate = tenantRepository.findAll().size();
        tenant.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTenantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tenant))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tenant in the database
        List<Tenant> tenantList = tenantRepository.findAll();
        assertThat(tenantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTenant() throws Exception {
        int databaseSizeBeforeUpdate = tenantRepository.findAll().size();
        tenant.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTenantMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(tenant)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tenant in the database
        List<Tenant> tenantList = tenantRepository.findAll();
        assertThat(tenantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTenant() throws Exception {
        // Initialize the database
        tenantRepository.saveAndFlush(tenant);

        int databaseSizeBeforeDelete = tenantRepository.findAll().size();

        // Delete the tenant
        restTenantMockMvc
            .perform(delete(ENTITY_API_URL_ID, tenant.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Tenant> tenantList = tenantRepository.findAll();
        assertThat(tenantList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
