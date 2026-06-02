package id.ac.ui.cs.advprog.bidmartcatalog.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WebConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test Resource Handler for /uploads/**")
    void testUploadsResourceHandler() throws Exception {
        assertNotNull(mockMvc, "MockMvc should be injected");
        mockMvc.perform(get("/uploads/test-image.jpg"))
                .andExpect(status().isNotFound());
    }
}
