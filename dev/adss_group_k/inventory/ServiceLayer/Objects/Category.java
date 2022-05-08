package adss_group_k.inventory.ServiceLayer.Objects;

import adss_group_k.inventory.BusinessLayer.SubCategory;

import java.util.HashMap;
import java.util.Map;

public class Category {
    private Map<String, adss_group_k.inventory.ServiceLayer.Objects.SubCategory> subC;
    private String name;

    public String getName() {
        return name;
    }

    public Category(adss_group_k.inventory.BusinessLayer.Category category) {
        name = category.getName();
        subC = new HashMap<>();
        Map<String, SubCategory> BusinessSubC = category.getSubC();
        for (Map.Entry<String, SubCategory> entry : BusinessSubC.entrySet()) {
            subC.put(entry.getKey(), new adss_group_k.inventory.ServiceLayer.Objects.SubCategory(entry.getValue()));
        }
    }
}
