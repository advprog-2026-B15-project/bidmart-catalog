package id.ac.ui.cs.advprog.bidmartcatalog.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class CategoryDataInitializer {

    @Bean
    CommandLineRunner initCategories(JdbcTemplate jdbcTemplate) {
        return args -> {
            // Gunakan Native SQL 'ON CONFLICT DO NOTHING' agar tidak error jika ID sudah ada
            // ID Parent dari V2__insert_categories.sql
            String elektronikId = "11111111-1111-1111-1111-111111111111";
            String fashionId = "22222222-2222-2222-2222-222222222222";

            // ID Statis untuk Subkategori
            String laptopId = "11111111-1111-1111-1111-222222222222";
            String smartphoneId = "11111111-1111-1111-1111-333333333333";
            String sepatuId = "22222222-2222-2222-2222-444444444444";

            // Insert Laptop
            jdbcTemplate.execute(String.format(
                "INSERT INTO categories (id, name, parent_id) VALUES ('%s', 'Laptop', '%s') ON CONFLICT (id) DO NOTHING",
                laptopId, elektronikId));

            // Insert Smartphone
            jdbcTemplate.execute(String.format(
                "INSERT INTO categories (id, name, parent_id) VALUES ('%s', 'Smartphone', '%s') ON CONFLICT (id) DO NOTHING",
                smartphoneId, elektronikId));

            // Insert Sepatu
            jdbcTemplate.execute(String.format(
                "INSERT INTO categories (id, name, parent_id) VALUES ('%s', 'Sepatu', '%s') ON CONFLICT (id) DO NOTHING",
                sepatuId, fashionId));
        };
    }
}
