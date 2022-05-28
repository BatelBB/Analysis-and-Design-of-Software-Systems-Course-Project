package adss_group_k.BusinessLayer.Suppliers.Service;

import adss_group_k.BusinessLayer.Suppliers.BusinessLogicException;
import adss_group_k.BusinessLayer.Suppliers.BussinessObject.Item;
import adss_group_k.BusinessLayer.Suppliers.BussinessObject.Order;
import adss_group_k.BusinessLayer.Suppliers.BussinessObject.QuantityDiscount;

import adss_group_k.BusinessLayer.Suppliers.BussinessObject.Supplier;
import adss_group_k.dataLayer.records.OrderType;
import adss_group_k.dataLayer.records.PaymentCondition;
import adss_group_k.shared.response.Response;
import adss_group_k.shared.response.ResponseT;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collection;

public interface ISupplierService {
    ResponseT<Order> getOrder(int id);

    ResponseT<Supplier> createSupplier(
            int ppn, int bankAccount, String name,
            boolean isDelivering, PaymentCondition paymentCondition,
            DayOfWeek regularSupplyingDays,
            String contactName, String contactPhone, String contactEmail);

    Collection<Supplier> getSuppliers();

    ResponseT<Supplier> getSupplier(int ppn) throws BusinessLogicException;

    Response deleteSupplier(int ppn);

    ResponseT<Item> createItem(int supplierPPN, int catalogNumber,
                               int productID, float price);

    Collection<Item> getItems();

    ResponseT<Item> getItem(int ppn, int catalog);

    Response deleteItem(int ppn, int catalogN);

    ResponseT<QuantityDiscount> createDiscount(int supplierPPN, int catalogN, int amount, float discount);

    Response deleteDiscount(QuantityDiscount discount);

    ResponseT<Order> createOrder(int supplierPPN, LocalDate ordered, LocalDate delivered,
                                 OrderType type);

    Collection<Order> getOrders();

    Response deleteOrder(int orderId);

    QuantityDiscount getDiscount(int amount, int ppn, int catalog) throws BusinessLogicException;

    Response orderItem(int orderId, int supplier, int catalogNumber, int amount);

    Response setPrice(int supplier, int catalogNumber, float price);

    Collection<QuantityDiscount> getDiscounts();

    Response setOrderOrdered(int orderID, LocalDate ordered) throws BusinessLogicException;

    Response setOrderProvided(int orderID, LocalDate provided) throws BusinessLogicException;

    Response setSupplierBankAccount(int supplierPPN, int bankAct);

    Response setSupplierCompanyName(int supplierPPN, String newName);

    Response setSupplierIsDelivering(int supplierPPN, boolean newValue);

    Response setSupplierPaymentCondition(int supplierPPN, PaymentCondition payment);

    Response setSupplierRegularSupplyingDays(int supplierPPN, DayOfWeek dayOfWeek);

    Response setSupplierContact(int supplierPPN, String name, String phoneNumber, String email);

    Response setItemName(int supplier, int catalogNumber, String name);

    Response setItemCategory(int supplier, int catalogNumber, String category);


    Response updateOrderAmount(int orderID, int supplier, int catalogNumber, int amount);

    Supplier findCheapestSupplierFor(int productID, int amount);
}