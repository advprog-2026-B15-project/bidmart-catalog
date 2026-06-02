package id.ac.ui.cs.advprog.bidmartcatalog;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
class CatalogFunctionalTest {

    @LocalServerPort
    private int port;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    @DisplayName("Test Catalog Page Title and Header")
    void testCatalogPage(ChromeDriver driver) {
        driver.get(baseUrl + "/listings");
        
        String title = driver.getTitle();
        // Adjust based on your Thymeleaf page title
        
        WebElement header = driver.findElement(By.tagName("h1"));
        assertAll("Check Catalog Page Title and Header",
                () -> assertNotNull(title, "Page title should not be null"),
                () -> assertTrue(header.getText().contains("Katalog Lelang") || header.getText().contains("Listings"),
                        "Header should contain 'Katalog Lelang' or 'Listings'")
        );
    }

    @Test
    @DisplayName("Test Create Listing Navigation")
    void testNavigateToCreateListing(ChromeDriver driver) {
        driver.get(baseUrl + "/listings");
        
        WebElement createLink = driver.findElement(By.linkText("Buat Listing Baru"));
        createLink.click();
        
        assertEquals(baseUrl + "/listings/create", driver.getCurrentUrl(), "Should navigate to the create listing page");
    }
}
