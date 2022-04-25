package ServiceLayer.Objects;

import java.util.HashMap;
import java.util.Map;

public class Product {

    private int item_ids;
    private final int product_id;
    private String name;
    private int shelf_qty;
    private int storage_qty;
    private String manufacturer;
    private double man_price;
    private double cus_price;
    private int min_qty;
    private int supply_time;
    private Map<String, ProductItem> items;

    private String cat;
    private String sub_cat;
    private String sub_sub_cat;

    public Product(BusinessLayer.Product p) {
        item_ids=p.getItem_ids();
        product_id = p.getProduct_id();
        name = p.getName();
        shelf_qty = p.getShelf_qty();
        storage_qty = p.getStorage_qty();
        manufacturer = p.getManufacturer();
        man_price = p.getMan_price();
        cus_price = p.getCus_price();
        min_qty = p.getMin_qty();
        supply_time = p.getSupply_time();
        cat=p.getCat();
        sub_cat= p.getCat();
        sub_sub_cat= p.getSub_sub_cat();
        Map<String,BusinessLayer.ProductItem> BusinessItemsMap=p.getItems();
        for (Map.Entry<String,BusinessLayer.ProductItem> entry : BusinessItemsMap.entrySet()) {
            items.put(entry.getKey(), new ProductItem(entry.getValue()));
        }

    }
}