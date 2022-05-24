package adss_group_k.BusinessLayer.Suppliers.Entity;

import adss_group_k.shared.utils.Utils;
import adss_group_k.BusinessLayer.Suppliers.Entity.readonly.Item;

public class QuantityDiscount {
    public final Item item;
    public final int quantity;
    public final float discount;

    public QuantityDiscount(Item item, int quantity, float discount){
        this.item = item;
        this.quantity = quantity;
        this.discount = discount;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public String toString() {
        return  Utils.table(
                2, 30, true,
                "  **** QUANTITY DISCOUNT **** ", "",
                "Item name", item.getName(),
                "Item catalog number", item.getCatalogNumber(),
                "For amounts over", quantity,
                "Discount", String.format("%.2f%%", discount * 100),
                "Supplier (name)", item.getSupplier().getName(),
                "Supplier (ppn)", item.getSupplier().getPpn()
        );
    }
}
