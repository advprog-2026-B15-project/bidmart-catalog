package id.ac.ui.cs.advprog.bidmartcatalog.model;

import id.ac.ui.cs.advprog.bidmartcatalog.model.ListingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "listings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    @Column(length = 2000)
    private String description;

    private String category;

    private String sellerId;

    private Double startingPrice;

    private Double currentPrice;

    private Double reservePrice;

    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ListingStatus status = ListingStatus.DRAFT;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}