package id.ac.ui.cs.advprog.bidmartcatalog.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class WebConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test Resource Handler for /uploads/**")
    void testUploadsResourceHandler() throws Exception {
        mockMvc.perform(get("/uploads/test-image.jpg"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test SPA routing - Access /login should serve index.html (fallback)")
    void testSPARouting() throws Exception {
        // Since we have a real index.html now, it should return 200 OK
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test static resource with extension should NOT trigger SPA fallback")
    void testStaticResourceWithExtension() throws Exception {
        mockMvc.perform(get("/css/style.css"))
                .andExpect(status().isNotFound());
    }
    }