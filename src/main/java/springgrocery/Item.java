package springgrocery;

import javax.persistence.*;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String unit;
    
    @Column(nullable = false)
    private double availableStock;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "item_allowed_quantities", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "quantity")
    private Set<Double> allowedQuantities = new HashSet<>();
    
    @Column(nullable = false)
    private double pricePerUnit;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public Item() {}

    public Item(String name, String unit, double availableStock, 
               Set<Double> allowedQuantities, double pricePerUnit) {
        setName(name);
        setUnit(unit);
        setAvailableStock(availableStock);
        setAllowedQuantities(allowedQuantities);
        setPricePerUnit(pricePerUnit);
    }

    // Getters and setters with validation
    public Long getId() { return id; }
    
    public String getName() { return name; }
    public void setName(String name) { 
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be empty");
        }
        this.name = name.trim(); 
    }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { 
        if (unit == null || unit.trim().isEmpty()) {
            throw new IllegalArgumentException("Unit cannot be empty");
        }
        this.unit = unit.trim(); 
    }
    
    public double getAvailableStock() { return availableStock; }
    public void setAvailableStock(double availableStock) { 
        if (availableStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        this.availableStock = availableStock; 
    }
    
    public Set<Double> getAllowedQuantities() { 
        return new HashSet<>(allowedQuantities);
    }
    
    public void setAllowedQuantities(Set<Double> allowedQuantities) {
        if (allowedQuantities == null || allowedQuantities.isEmpty()) {
            throw new IllegalArgumentException("At least one allowed quantity must be specified");
        }
        this.allowedQuantities = new HashSet<>(allowedQuantities);
    }
    
    public double getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(double pricePerUnit) { 
        if (pricePerUnit <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        this.pricePerUnit = pricePerUnit; 
    }
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { 
        this.category = category; 
    }

    @Override
    public String toString() {
        return String.format("%s (%.2f %s available @ Rs.%.2f/%s)", 
            name, availableStock, unit, pricePerUnit, unit);
    }

    public void addAllowedQuantity(double quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.allowedQuantities.add(quantity);
    }

    public boolean isQuantityAllowed(double quantity) {
        return this.allowedQuantities.contains(quantity);
    }
}