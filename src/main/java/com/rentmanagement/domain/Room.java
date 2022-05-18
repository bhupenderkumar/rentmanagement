package com.rentmanagement.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Room.
 */
@Entity
@Table(name = "room")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "room_name", nullable = false)
    private String roomName;

    @Column(name = "floor")
    private String floor;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "address", "buildings" }, allowSetters = true)
    private Building building;

    @ManyToOne
    @JsonIgnoreProperties(value = { "location", "rooms", "generateBills" }, allowSetters = true)
    private Tenant tenants;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Room id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomName() {
        return this.roomName;
    }

    public Room roomName(String roomName) {
        this.setRoomName(roomName);
        return this;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getFloor() {
        return this.floor;
    }

    public Room floor(String floor) {
        this.setFloor(floor);
        return this;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public Building getBuilding() {
        return this.building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public Room building(Building building) {
        this.setBuilding(building);
        return this;
    }

    public Tenant getTenants() {
        return this.tenants;
    }

    public void setTenants(Tenant tenant) {
        this.tenants = tenant;
    }

    public Room tenants(Tenant tenant) {
        this.setTenants(tenant);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Room)) {
            return false;
        }
        return id != null && id.equals(((Room) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Room{" +
            "id=" + getId() +
            ", roomName='" + getRoomName() + "'" +
            ", floor='" + getFloor() + "'" +
            "}";
    }
}
