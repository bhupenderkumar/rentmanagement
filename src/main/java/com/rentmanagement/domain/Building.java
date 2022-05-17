package com.rentmanagement.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Building.
 */
@Entity
@Table(name = "building")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Building implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "building_name", nullable = false)
    private String buildingName;

    @OneToOne(optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private Location addressId;

    @OneToMany(mappedBy = "building")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "building", "tenants" }, allowSetters = true)
    private Set<Room> buildings = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Building id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBuildingName() {
        return this.buildingName;
    }

    public Building buildingName(String buildingName) {
        this.setBuildingName(buildingName);
        return this;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public Location getAddressId() {
        return this.addressId;
    }

    public void setAddressId(Location location) {
        this.addressId = location;
    }

    public Building addressId(Location location) {
        this.setAddressId(location);
        return this;
    }

    public Set<Room> getBuildings() {
        return this.buildings;
    }

    public void setBuildings(Set<Room> rooms) {
        if (this.buildings != null) {
            this.buildings.forEach(i -> i.setBuilding(null));
        }
        if (rooms != null) {
            rooms.forEach(i -> i.setBuilding(this));
        }
        this.buildings = rooms;
    }

    public Building buildings(Set<Room> rooms) {
        this.setBuildings(rooms);
        return this;
    }

    public Building addBuildings(Room room) {
        this.buildings.add(room);
        room.setBuilding(this);
        return this;
    }

    public Building removeBuildings(Room room) {
        this.buildings.remove(room);
        room.setBuilding(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Building)) {
            return false;
        }
        return id != null && id.equals(((Building) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Building{" +
            "id=" + getId() +
            ", buildingName='" + getBuildingName() + "'" +
            "}";
    }
}
