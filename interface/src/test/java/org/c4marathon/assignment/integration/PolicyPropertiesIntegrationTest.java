package org.c4marathon.assignment.integration;

import org.c4marathon.assignment.AssignmentApplication;
import org.c4marathon.assignment.infra.properties.MainAccountPolicy;
import org.c4marathon.assignment.infra.properties.PendingTransferPolicyProperties;
import org.c4marathon.assignment.infra.properties.SavingAccountPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AssignmentApplication.class)
@EnableConfigurationProperties({
    MainAccountPolicy.class,
    SavingAccountPolicy.class,
    PendingTransferPolicyProperties.class
})
public class PolicyPropertiesIntegrationTest {

    @Autowired
    private MainAccountPolicy mainAccountPolicy;
    
    @Autowired
    private SavingAccountPolicy savingAccountPolicy;
    
    @Autowired
    private PendingTransferPolicyProperties pendingTransferPolicyProperties;

    @Test
    @DisplayName("프로퍼티가 정상적으로 로드되는지 확인")
    void testPropertiesLoading() {
        // given
        // when
        // then
        assertThat(mainAccountPolicy).isNotNull();
        assertThat(savingAccountPolicy).isNotNull();
        assertThat(pendingTransferPolicyProperties).isNotNull();
        
        System.out.println("=== Policy Values ===");
        System.out.println("Main Account Daily Limit: " + mainAccountPolicy.getMainDailyLimit());
        System.out.println("Saving Account Fixed Interest Rate: " + savingAccountPolicy.getFixedInterestRate());
        System.out.println("Pending Transfer Expire After (hours): " + 
            pendingTransferPolicyProperties.getPendingTransferExpireAfterDurationHours());
    }
}
