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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ImageControllerTest {

    private MockMvc mockMvc;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        // ImageController uses Paths.get("uploads"), which is hard to mock directly without refactoring.
        // However, for unit testing we can try to test the logic.
        // Since it's a simple controller, I'll use MockMvc.
        mockMvc = MockMvcBuilders.standaloneSetup(new ImageController()).build();
        
        // Ensure "uploads" directory exists for the test if the controller depends on it.
        Files.createDirectories(Path.of("uploads"));
    }

    @Test
    void testServeFileJpeg() throws Exception {
        Path file = Path.of("uploads", "test.jpg");
        Files.writeString(file, "dummy content");
        
        mockMvc.perform(get("/uploads/test.jpg"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, "public, max-age=86400"));
        
        Files.deleteIfExists(file);
    }

    @Test
    void testServeFilePng() throws Exception {
        Path file = Path.of("uploads", "test.png");
        Files.writeString(file, "dummy content");
        
        mockMvc.perform(get("/uploads/test.png"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG));
        
        Files.deleteIfExists(file);
    }

    @Test
    void testServeFileWebp() throws Exception {
        Path file = Path.of("uploads", "test.webp");
        Files.writeString(file, "dummy content");
        
        mockMvc.perform(get("/uploads/test.webp"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/webp"));
        
        Files.deleteIfExists(file);
    }

    @Test
    void testServeFileGif() throws Exception {
        Path file = Path.of("uploads", "test.gif");
        Files.writeString(file, "dummy content");
        
        mockMvc.perform(get("/uploads/test.gif"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_GIF));
        
        Files.deleteIfExists(file);
    }

    @Test
    void testServeFileNotFound() throws Exception {
        mockMvc.perform(get("/uploads/nonexistent.jpg"))
                .andExpect(status().isNotFound());
    }
}
