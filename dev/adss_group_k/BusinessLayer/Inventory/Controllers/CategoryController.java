package adss_group_k.BusinessLayer.Inventory.Controllers;


import adss_group_k.BusinessLayer.Inventory.Categories.Category;
import adss_group_k.BusinessLayer.Inventory.Categories.SubCategory;
import adss_group_k.dataLayer.dao.PersistenceController;
import adss_group_k.dataLayer.records.CategoryRecord;
import adss_group_k.dataLayer.records.readonly.CategoryData;
import adss_group_k.shared.response.ResponseT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryController {

    private final Map<String, Category> categories;
    private static CategoryController category_controller;
    private final PersistenceController pc;

    //singleton instance
    public static CategoryController getInstance(PersistenceController pc) {
        if (category_controller == null)
            category_controller = new CategoryController(pc);
        return category_controller;
    }

    //CONSTRUCTORS
    private CategoryController(PersistenceController pc) {
        categories = new HashMap<>();
        this.pc = pc;
        pc.getCategories().all().forEach(this::addFromExisting);
    }

    //METHODS
    public void addCategory(String name) throws Exception {
        if (categories.containsKey(name))
            throw new IllegalArgumentException("Category name already exists in the system");
        ResponseT<CategoryData> r = pc.getCategories().create(name);
        if (!r.success)
            throw new Exception(r.error);
        Category category = new Category(name, pc);
        categories.put(name, category);
    }

    public void addSubCategory(String categoryName, String SubCategoryName) throws Exception {
        if (!categories.containsKey(categoryName))
            throw new IllegalArgumentException("Category doesn't exists");
        categories.get(categoryName).addSubCategory(SubCategoryName);
    }

    public void addSubSubCategory(String categoryName, String sub_category, String name) throws Exception {
        if (!categories.containsKey(categoryName))
            throw new IllegalArgumentException("Category doesn't exists");
        categories.get(categoryName).getSubCategory(sub_category).addSubSubCategory(categoryName, name);
    }

    public void removeCategory(String name, boolean safe_remove) throws Exception {
        if (!safe_remove)
            throw new IllegalArgumentException("Category has products in it");
        if (!categories.containsKey(name))
            throw new IllegalArgumentException("Category doesn't exists");
        for (String sc : categories.get(name).getSubC().keySet())
            categories.get(name).removeSubCategory(sc);
        int r = pc.getCategories().runDeleteQuery(name);
        if (r == -1)
            throw new Exception("Error deleting category from DB");
        categories.remove(name);
    }

    public void removeSubCategory(String categoryName, String SubCategoryName, boolean safe_remove) throws Exception {
        if (!safe_remove)
            throw new IllegalArgumentException("Subcategory has products in it");
        if (!categories.containsKey(categoryName))
            throw new IllegalArgumentException("Category doesn't exists");
        categories.get(categoryName).removeSubCategory(SubCategoryName);
    }

    public void removeSubSubCategory(String categoryName, String sub_category, String name, boolean safe_remove) throws Exception {
        if (!safe_remove)
            throw new IllegalArgumentException("Sub-sub-category has products in it");
        if (!categories.containsKey(categoryName))
            throw new IllegalArgumentException("Category doesn't exists");
        Category category = categories.get(categoryName);
        SubCategory subCategory = category.getSubCategory(sub_category);
        subCategory.removeSubSubCategory(categoryName, name);
    }

    public Category getCategory(String name) {
        if (!categories.containsKey(name))
            throw new IllegalArgumentException("Category doesn't exists");
        return categories.get(name);
    }

    public SubCategory getSubCategory(String categoryName, String SubCategoryName) {
        if (!categories.containsKey(categoryName))
            throw new IllegalArgumentException("Category doesn't exists");
        return categories.get(categoryName).getSubCategory(SubCategoryName);
    }

    public List<String> getCategoriesNames() {
        return new ArrayList<>(categories.keySet());
    }

    public void restart() {
        categories.clear();
    }

    //GETTERS AND SETTERS
    public Map<String, Category> getCategories() {
        return categories;
    }

    //PRIVATE METHODS
    private void addFromExisting(CategoryRecord category) {
        categories.put(category.getName(), new Category(category, pc));
    }
}

