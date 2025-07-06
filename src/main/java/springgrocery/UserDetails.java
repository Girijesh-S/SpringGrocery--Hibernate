package springgrocery;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, length = 10)
    private String phone;
    
    @Column(name = "alt_phone", nullable = false, length = 10)
    private String altPhone;
    
    @Column(nullable = false, length = 6)
    private String pincode;
    
    @Column(nullable = false)
    private String city;
    
    @Column(nullable = false)
    private String state;
    
    @Column(name = "address_line1", nullable = false)
    private String addressLine1;
    
    @Column(name = "address_line2")
    private String addressLine2;
    
    @Column(name = "delivery_date", nullable = false)
    private String deliveryDate;

    // Constructors
    public UserDetails() {}

    // Getters and Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAltPhone() { return altPhone; }
    public void setAltPhone(String altPhone) { this.altPhone = altPhone; }
    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }
    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }
    public String getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(String deliveryDate) { this.deliveryDate = deliveryDate; }

    @Override
    public String toString() {
        return String.format(
            "Name: %s\nPhone: %s\nAlt Phone: %s\nAddress: %s, %s, %s, %s\nDelivery Date: %s",
            name, phone, altPhone, addressLine1, addressLine2, city, state, deliveryDate
        );
    }
}
