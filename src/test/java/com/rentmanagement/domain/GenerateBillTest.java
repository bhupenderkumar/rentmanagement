package com.rentmanagement.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.rentmanagement.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GenerateBillTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GenerateBill.class);
        GenerateBill generateBill1 = new GenerateBill();
        generateBill1.setId(1L);
        GenerateBill generateBill2 = new GenerateBill();
        generateBill2.setId(generateBill1.getId());
        assertThat(generateBill1).isEqualTo(generateBill2);
        generateBill2.setId(2L);
        assertThat(generateBill1).isNotEqualTo(generateBill2);
        generateBill1.setId(null);
        assertThat(generateBill1).isNotEqualTo(generateBill2);
    }
}
