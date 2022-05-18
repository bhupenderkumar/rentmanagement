package com.rentmanagement.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Tenant.
 */
@Entity
@Table(name = "tenant")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Tenant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "tenant_name", nullable = false)
    private String tenantName;

    @Lob
    @Column(name = "address_proff", nullable = false)
    private byte[] addressProff;

    @NotNull
    @Column(name = "address_proff_content_type", nullable = false)
    private String addressProffContentType;

    @Column(name = "numberof_family_members")
    private Integer numberofFamilyMembers;

    @NotNull
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @NotNull
    @Column(name = "rent_start_date", nullable = false)
    private LocalDate rentStartDate;

    @NotNull
    @Column(name = "rent_amount", nullable = false)
    private Double rentAmount;

    @NotNull
    @Column(name = "electricity_unit_rate", nullable = false)
    private Integer electricityUnitRate;

    @NotNull
    @Column(name = "starting_electricity_unit", nullable = false)
    private Double startingElectricityUnit;

    @Size(max = 500)
    @Column(name = "any_other_details", length = 500)
    private String anyOtherDetails;

    @Column(name = "send_notification")
    private Boolean sendNotification;

    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "emergency_contact_number")
    private String emergencyContactNumber;

    @Column(name = "out_standing_amount")
    private Double outStandingAmount;

    @Column(name = "month_end_calculation")
    private Boolean monthEndCalculation;

    @Column(name = "calculate_on_date")
    private Boolean calculateOnDate;

    @Column(name = "calculated_for_current_month")
    private Boolean calculatedForCurrentMonth;

    @OneToOne
    @JoinColumn(unique = true)
    private Location location;

    @OneToMany(mappedBy = "tenants")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "building", "tenants" }, allowSetters = true)
    private Set<Room> rooms = new HashSet<>();

    @OneToMany(mappedBy = "tenant")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "tenant" }, allowSetters = true)
    private Set<GenerateBill> generateBills = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Tenant id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenantName() {
        return this.tenantName;
    }

    public Tenant tenantName(String tenantName) {
        this.setTenantName(tenantName);
        return this;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public byte[] getAddressProff() {
        return this.addressProff;
    }

    public Tenant addressProff(byte[] addressProff) {
        this.setAddressProff(addressProff);
        return this;
    }

    public void setAddressProff(byte[] addressProff) {
        this.addressProff = addressProff;
    }

    public String getAddressProffContentType() {
        return this.addressProffContentType;
    }

    public Tenant addressProffContentType(String addressProffContentType) {
        this.addressProffContentType = addressProffContentType;
        return this;
    }

    public void setAddressProffContentType(String addressProffContentType) {
        this.addressProffContentType = addressProffContentType;
    }

    public Integer getNumberofFamilyMembers() {
        return this.numberofFamilyMembers;
    }

    public Tenant numberofFamilyMembers(Integer numberofFamilyMembers) {
        this.setNumberofFamilyMembers(numberofFamilyMembers);
        return this;
    }

    public void setNumberofFamilyMembers(Integer numberofFamilyMembers) {
        this.numberofFamilyMembers = numberofFamilyMembers;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public Tenant phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getRentStartDate() {
        return this.rentStartDate;
    }

    public Tenant rentStartDate(LocalDate rentStartDate) {
        this.setRentStartDate(rentStartDate);
        return this;
    }

    public void setRentStartDate(LocalDate rentStartDate) {
        this.rentStartDate = rentStartDate;
    }

    public Double getRentAmount() {
        return this.rentAmount;
    }

    public Tenant rentAmount(Double rentAmount) {
        this.setRentAmount(rentAmount);
        return this;
    }

    public void setRentAmount(Double rentAmount) {
        this.rentAmount = rentAmount;
    }

    public Integer getElectricityUnitRate() {
        return this.electricityUnitRate;
    }

    public Tenant electricityUnitRate(Integer electricityUnitRate) {
        this.setElectricityUnitRate(electricityUnitRate);
        return this;
    }

    public void setElectricityUnitRate(Integer electricityUnitRate) {
        this.electricityUnitRate = electricityUnitRate;
    }

    public Double getStartingElectricityUnit() {
        return this.startingElectricityUnit;
    }

    public Tenant startingElectricityUnit(Double startingElectricityUnit) {
        this.setStartingElectricityUnit(startingElectricityUnit);
        return this;
    }

    public void setStartingElectricityUnit(Double startingElectricityUnit) {
        this.startingElectricityUnit = startingElectricityUnit;
    }

    public String getAnyOtherDetails() {
        return this.anyOtherDetails;
    }

    public Tenant anyOtherDetails(String anyOtherDetails) {
        this.setAnyOtherDetails(anyOtherDetails);
        return this;
    }

    public void setAnyOtherDetails(String anyOtherDetails) {
        this.anyOtherDetails = anyOtherDetails;
    }

    public Boolean getSendNotification() {
        return this.sendNotification;
    }

    public Tenant sendNotification(Boolean sendNotification) {
        this.setSendNotification(sendNotification);
        return this;
    }

    public void setSendNotification(Boolean sendNotification) {
        this.sendNotification = sendNotification;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public Tenant emailAddress(String emailAddress) {
        this.setEmailAddress(emailAddress);
        return this;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmergencyContactNumber() {
        return this.emergencyContactNumber;
    }

    public Tenant emergencyContactNumber(String emergencyContactNumber) {
        this.setEmergencyContactNumber(emergencyContactNumber);
        return this;
    }

    public void setEmergencyContactNumber(String emergencyContactNumber) {
        this.emergencyContactNumber = emergencyContactNumber;
    }

    public Double getOutStandingAmount() {
        return this.outStandingAmount;
    }

    public Tenant outStandingAmount(Double outStandingAmount) {
        this.setOutStandingAmount(outStandingAmount);
        return this;
    }

    public void setOutStandingAmount(Double outStandingAmount) {
        this.outStandingAmount = outStandingAmount;
    }

    public Boolean getMonthEndCalculation() {
        return this.monthEndCalculation;
    }

    public Tenant monthEndCalculation(Boolean monthEndCalculation) {
        this.setMonthEndCalculation(monthEndCalculation);
        return this;
    }

    public void setMonthEndCalculation(Boolean monthEndCalculation) {
        this.monthEndCalculation = monthEndCalculation;
    }

    public Boolean getCalculateOnDate() {
        return this.calculateOnDate;
    }

    public Tenant calculateOnDate(Boolean calculateOnDate) {
        this.setCalculateOnDate(calculateOnDate);
        return this;
    }

    public void setCalculateOnDate(Boolean calculateOnDate) {
        this.calculateOnDate = calculateOnDate;
    }

    public Boolean getCalculatedForCurrentMonth() {
        return this.calculatedForCurrentMonth;
    }

    public Tenant calculatedForCurrentMonth(Boolean calculatedForCurrentMonth) {
        this.setCalculatedForCurrentMonth(calculatedForCurrentMonth);
        return this;
    }

    public void setCalculatedForCurrentMonth(Boolean calculatedForCurrentMonth) {
        this.calculatedForCurrentMonth = calculatedForCurrentMonth;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Tenant location(Location location) {
        this.setLocation(location);
        return this;
    }

    public Set<Room> getRooms() {
        return this.rooms;
    }

    public void setRooms(Set<Room> rooms) {
        if (this.rooms != null) {
            this.rooms.forEach(i -> i.setTenants(null));
        }
        if (rooms != null) {
            rooms.forEach(i -> i.setTenants(this));
        }
        this.rooms = rooms;
    }

    public Tenant rooms(Set<Room> rooms) {
        this.setRooms(rooms);
        return this;
    }

    public Tenant addRooms(Room room) {
        this.rooms.add(room);
        room.setTenants(this);
        return this;
    }

    public Tenant removeRooms(Room room) {
        this.rooms.remove(room);
        room.setTenants(null);
        return this;
    }

    public Set<GenerateBill> getGenerateBills() {
        return this.generateBills;
    }

    public void setGenerateBills(Set<GenerateBill> generateBills) {
        if (this.generateBills != null) {
            this.generateBills.forEach(i -> i.setTenant(null));
        }
        if (generateBills != null) {
            generateBills.forEach(i -> i.setTenant(this));
        }
        this.generateBills = generateBills;
    }

    public Tenant generateBills(Set<GenerateBill> generateBills) {
        this.setGenerateBills(generateBills);
        return this;
    }

    public Tenant addGenerateBill(GenerateBill generateBill) {
        this.generateBills.add(generateBill);
        generateBill.setTenant(this);
        return this;
    }

    public Tenant removeGenerateBill(GenerateBill generateBill) {
        this.generateBills.remove(generateBill);
        generateBill.setTenant(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tenant)) {
            return false;
        }
        return id != null && id.equals(((Tenant) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Tenant{" +
            "id=" + getId() +
            ", tenantName='" + getTenantName() + "'" +
            ", addressProff='" + getAddressProff() + "'" +
            ", addressProffContentType='" + getAddressProffContentType() + "'" +
            ", numberofFamilyMembers=" + getNumberofFamilyMembers() +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", rentStartDate='" + getRentStartDate() + "'" +
            ", rentAmount=" + getRentAmount() +
            ", electricityUnitRate=" + getElectricityUnitRate() +
            ", startingElectricityUnit=" + getStartingElectricityUnit() +
            ", anyOtherDetails='" + getAnyOtherDetails() + "'" +
            ", sendNotification='" + getSendNotification() + "'" +
            ", emailAddress='" + getEmailAddress() + "'" +
            ", emergencyContactNumber='" + getEmergencyContactNumber() + "'" +
            ", outStandingAmount=" + getOutStandingAmount() +
            ", monthEndCalculation='" + getMonthEndCalculation() + "'" +
            ", calculateOnDate='" + getCalculateOnDate() + "'" +
            ", calculatedForCurrentMonth='" + getCalculatedForCurrentMonth() + "'" +
            "}";
    }
}
