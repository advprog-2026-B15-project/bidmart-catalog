package id.ac.ui.cs.advprog.bidmartcatalog.service;

import id.ac.ui.cs.advprog.bidmartcatalog.exception.CategoryNotFoundException;
import id.ac.ui.cs.advprog.bidmartcatalog.model.Category;
import id.ac.ui.cs.advprog.bidmartcatalog.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    @Override
    public List<UUID> getCategoryAndSubCategoryIds(UUID parentCategoryId) {
        List<UUID> ids = new ArrayList<>();
        Category category = categoryRepository.findById(parentCategoryId)
                .orElseThrow(() -> new CategoryNotFoundException(parentCategoryId));

        collectCategoryIds(category, ids);
        return ids;
    }

    private void collectCategoryIds(Category category, List<UUID> ids) {
        ids.add(category.getId());

        if (category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
            for (Category sub : category.getSubCategories()) {
                collectCategoryIds(sub, ids);
            }
        }
    }
}
