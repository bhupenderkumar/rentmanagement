package com.rentmanagement.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A GenerateBill.
 */
@Entity
@Table(name = "generate_bill")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class GenerateBill implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "amount_pending")
    private Double amountPending;

    @Column(name = "send_notification")
    private Boolean sendNotification;

    @JsonIgnoreProperties(value = { "tenant", "location", "rooms" }, allowSetters = true)
    @OneToOne(mappedBy = "tenant")
    private Tenant tenant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public GenerateBill id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmountPending() {
        return this.amountPending;
    }

    public GenerateBill amountPending(Double amountPending) {
        this.setAmountPending(amountPending);
        return this;
    }

    public void setAmountPending(Double amountPending) {
        this.amountPending = amountPending;
    }

    public Boolean getSendNotification() {
        return this.sendNotification;
    }

    public GenerateBill sendNotification(Boolean sendNotification) {
        this.setSendNotification(sendNotification);
        return this;
    }

    public void setSendNotification(Boolean sendNotification) {
        this.sendNotification = sendNotification;
    }

    public Tenant getTenant() {
        return this.tenant;
    }

    public void setTenant(Tenant tenant) {
        if (this.tenant != null) {
            this.tenant.setTenant(null);
        }
        if (tenant != null) {
            tenant.setTenant(this);
        }
        this.tenant = tenant;
    }

    public GenerateBill tenant(Tenant tenant) {
        this.setTenant(tenant);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GenerateBill)) {
            return false;
        }
        return id != null && id.equals(((GenerateBill) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GenerateBill{" +
            "id=" + getId() +
            ", amountPending=" + getAmountPending() +
            ", sendNotification='" + getSendNotification() + "'" +
            "}";
    }
}
