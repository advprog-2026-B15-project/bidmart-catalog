package id.ac.ui.cs.advprog.bidmartcatalog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class BidmartcatalogApplicationTests {

    @Test
    void contextLoads() {
        // Mengetes apakah Application Context berhasil dimuat
    }

    @Test
    void testMain() {
        // Mengetes apakah method main bisa dijalankan tanpa error
        assertDoesNotThrow(() -> {
            BidmartcatalogApplication.main(new String[] {});
        });
    }
}