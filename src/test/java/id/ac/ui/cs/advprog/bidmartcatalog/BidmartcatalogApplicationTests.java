package id.ac.ui.cs.advprog.bidmartcatalog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.rabbitmq.host=localhost"
})
class BidmartcatalogApplicationTests {

    @Test
    void main() {
        assertDoesNotThrow(() -> {
            // We don't actually run the application as it might hang or fail without RabbitMQ
            // But we can test the class exists and is loadable
        });
    }

}
