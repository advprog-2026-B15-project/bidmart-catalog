package id.ac.ui.cs.advprog.bidmartcatalog.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "listing_images")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private boolean isPrimary; // Untuk menandai gambar utama

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;
}