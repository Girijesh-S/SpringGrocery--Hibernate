package springgrocery;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserDetails user;
    
    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;
    
    @Column(name = "item_unit", nullable = false, length = 20)
    private String itemUnit;
    
    @Column(name = "category_name", nullable = false, length = 50)
    private String categoryName;
    
    @Column(nullable = false)
    private double quantity;
    
    @Column(name = "unit_price", nullable = false)
    private double unitPrice;
    
    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;
    
    @Column(name = "total_price", nullable = false)
    private double totalPrice;

    public Order() {
        this.orderDate = LocalDateTime.now();
    }

    public Order(UserDetails user, String itemName, String itemUnit, 
                String categoryName, double quantity, double unitPrice) {
        this();
        setUser(user);
        setItemName(itemName);
        setItemUnit(itemUnit);
        setCategoryName(categoryName);
        setQuantity(quantity);
        setUnitPrice(unitPrice);
        updateTotalPrice();
    }

    // Getters and Setters with validation
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserDetails getUser() { return user; }
    public void setUser(UserDetails user) { 
        if (user == null) throw new IllegalArgumentException("User cannot be null");
        this.user = user; 
    }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { 
        if (itemName == null || itemName.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be empty");
        }
        this.itemName = itemName.trim(); 
    }

    public String getItemUnit() { return itemUnit; }
    public void setItemUnit(String itemUnit) { 
        if (itemUnit == null || itemUnit.trim().isEmpty()) {
            throw new IllegalArgumentException("Item unit cannot be empty");
        }
        this.itemUnit = itemUnit.trim(); 
    }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { 
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        this.categoryName = categoryName.trim(); 
    }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.quantity = quantity;
        updateTotalPrice();
    }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) {
        if (unitPrice <= 0) {
            throw new IllegalArgumentException("Unit price must be positive");
        }
        this.unitPrice = unitPrice;
        updateTotalPrice();
    }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { 
        if (orderDate == null) {
            throw new IllegalArgumentException("Order date cannot be null");
        }
        this.orderDate = orderDate; 
    }

    public double getTotalPrice() { return totalPrice; }

    public void updateTotalPrice() {
        this.totalPrice = this.quantity * this.unitPrice;
    }

    @Override
    public String toString() {
        return String.format(
            "Order[id=%d, item='%s', quantity=%.2f %s, price=Rs.%.2f, total=Rs.%.2f, date=%s]",
            id, itemName, quantity, itemUnit, unitPrice, totalPrice, orderDate
        );
    }
}