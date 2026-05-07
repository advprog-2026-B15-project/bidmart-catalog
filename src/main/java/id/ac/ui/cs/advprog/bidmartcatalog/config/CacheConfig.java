package id.ac.ui.cs.advprog.bidmartcatalog.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Konfigurasi cache in-process menggunakan Caffeine.
 *
 * Cache yang dikelola:
 *
 *   "listings"       → cache hasil getListingById(UUID).
 *                       TTL: 5 menit, max 500 entry.
 *                       Di-evict setiap kali listing diubah/dihapus/di-close.
 *
 *   "activeListings" → cache hasil getActiveListings(Pageable).
 *                       TTL: 2 menit, max 100 entry (page-aware key).
 *                       Frekuensi evict lebih tinggi karena harga berubah
 *                       setiap ada BidPlaced.
 *
 * Catatan produksi: Untuk deployment multi-instance, ganti dengan
 * Redis (spring-boot-starter-data-redis + @EnableCaching) agar cache
 * bersifat shared dan tidak stale antar pod.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager();

        // Tidak set default spec agar tiap cache bisa dikonfigurasi sendiri
        manager.setCacheNames(java.util.List.of("listings", "activeListings"));

        // Spec berbeda per cache nama
        manager.registerCustomCache("listings",
                Caffeine.newBuilder()
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .maximumSize(500)
                        .recordStats()
                        .build());

        manager.registerCustomCache("activeListings",
                Caffeine.newBuilder()
                        .expireAfterWrite(2, TimeUnit.MINUTES)
                        .maximumSize(100)
                        .recordStats()
                        .build());

        return manager;
    }
}
