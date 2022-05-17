package com.rentmanagement.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.rentmanagement.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BuildingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Building.class);
        Building building1 = new Building();
        building1.setId(1L);
        Building building2 = new Building();
        building2.setId(building1.getId());
        assertThat(building1).isEqualTo(building2);
        building2.setId(2L);
        assertThat(building1).isNotEqualTo(building2);
        building1.setId(null);
        assertThat(building1).isNotEqualTo(building2);
    }
}
