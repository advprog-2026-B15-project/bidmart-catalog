package id.ac.ui.cs.advprog.bidmartcatalog.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Listing {
    private String id;
    private String title;
    private String description;
    private double startPrice;
}