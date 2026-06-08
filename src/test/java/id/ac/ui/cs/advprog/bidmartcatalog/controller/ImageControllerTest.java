package id.ac.ui.cs.advprog.bidmartcatalog.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.test.web.servlet.ResultActions;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
class ImageControllerTest {

    private MockMvc mockMvc;
    private static final String UPLOADS = "uploads";
    private static final String DUMMY_CONTENT = "dummy content";

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        // ImageController uses Paths.get("uploads"), which is hard to mock directly without refactoring.
        // However, for unit testing we can try to test the logic.
        // Since it's a simple controller, I'll use MockMvc.
        mockMvc = MockMvcBuilders.standaloneSetup(new ImageController()).build();
        
        // Ensure "uploads" directory exists for the test if the controller depends on it.
        Files.createDirectories(Path.of(UPLOADS));
    }

    @Test
    void testServeFileJpeg() throws Exception {
        Path file = Path.of(UPLOADS, "test.jpg");
        Files.writeString(file, DUMMY_CONTENT);
        
        ResultActions result = mockMvc.perform(get("/uploads/test.jpg"));
        
        assertAll("Verify serve JPEG file",
            () -> result.andExpect(status().isOk()),
            () -> result.andExpect(content().contentType(MediaType.IMAGE_JPEG)),
            () -> result.andExpect(header().string(HttpHeaders.CACHE_CONTROL, "public, max-age=86400"))
        );
        
        Files.deleteIfExists(file);
    }

    @Test
    void testServeFilePng() throws Exception {
        Path file = Path.of(UPLOADS, "test.png");
        Files.writeString(file, DUMMY_CONTENT);
        
        ResultActions result = mockMvc.perform(get("/uploads/test.png"));
        
        assertAll("Verify serve PNG file",
            () -> result.andExpect(status().isOk()),
            () -> result.andExpect(content().contentType(MediaType.IMAGE_PNG))
        );
        
        Files.deleteIfExists(file);
    }

    @Test
    void testServeFileWebp() throws Exception {
        Path file = Path.of(UPLOADS, "test.webp");
        Files.writeString(file, DUMMY_CONTENT);
        
        ResultActions result = mockMvc.perform(get("/uploads/test.webp"));
        
        assertAll("Verify serve WEBP file",
            () -> result.andExpect(status().isOk()),
            () -> result.andExpect(content().contentType("image/webp"))
        );
        
        Files.deleteIfExists(file);
    }

    @Test
    void testServeFileGif() throws Exception {
        Path file = Path.of(UPLOADS, "test.gif");
        Files.writeString(file, DUMMY_CONTENT);
        
        ResultActions result = mockMvc.perform(get("/uploads/test.gif"));
        
        assertAll("Verify serve GIF file",
            () -> result.andExpect(status().isOk()),
            () -> result.andExpect(content().contentType(MediaType.IMAGE_GIF))
        );
        
        Files.deleteIfExists(file);
    }

    @Test
    void testServeFileNotFound() throws Exception {
        assertNotNull(mockMvc, "MockMvc should be initialized");
        ResultActions result = mockMvc.perform(get("/uploads/nonexistent.jpg"));
        
        assertAll("Verify not found status",
            () -> result.andExpect(status().isNotFound())
        );
    }
}
