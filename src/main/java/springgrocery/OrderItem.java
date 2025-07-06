package springgrocery;

public class OrderItem {
    private Item item;
    private double quantity;

    public OrderItem() {}

    public OrderItem(Item item, double quantity) {
        setItem(item);
        setQuantity(quantity);
    }

    // Getters and Setters with validation
    public Item getItem() { return item; }
    public void setItem(Item item) { 
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        this.item = item; 
    }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (item != null && quantity > item.getAvailableStock()) {
            throw new IllegalArgumentException("Quantity exceeds available stock");
        }
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return quantity * item.getPricePerUnit();
    }

    public boolean isAvailable() {
        return item != null && quantity <= item.getAvailableStock();
    }

    @Override
    public String toString() {
        return String.format(
            "OrderItem[item=%s, quantity=%.2f %s, unitPrice=Rs.%.2f, total=Rs.%.2f]",
            item.getName(), quantity, item.getUnit(), item.getPricePerUnit(), getTotalPrice()
        );
    }
}