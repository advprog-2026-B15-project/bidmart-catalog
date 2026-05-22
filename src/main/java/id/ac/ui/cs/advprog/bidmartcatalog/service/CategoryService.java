package id.ac.ui.cs.advprog.bidmartcatalog.service;

import id.ac.ui.cs.advprog.bidmartcatalog.model.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    Category getCategoryById(UUID id);
    List<UUID> getCategoryAndSubCategoryIds(UUID parentCategoryId);
}
