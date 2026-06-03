package id.ac.ui.cs.advprog.bidmartcatalog.config;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

class JacksonConfigTest {

    @Test
    @DisplayName("Test hibernate6Module bean creation")
    void testHibernate6ModuleBean() {
        JacksonConfig config = new JacksonConfig();
        Hibernate6Module module = config.hibernate6Module();
        
        assertAll("Verify JacksonConfig",
            () -> assertNotNull(module, "Hibernate6Module should not be null"),
            () -> assertFalse(module.isEnabled(Hibernate6Module.Feature.FORCE_LAZY_LOADING), 
                "FORCE_LAZY_LOADING should be disabled by default in our config")
        );
    }
}
