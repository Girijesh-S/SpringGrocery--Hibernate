package springgrocery;

import org.hibernate.Session;
import org.hibernate.Transaction;
import java.time.LocalDateTime;
import java.util.*;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static List<Category> catalog = new ArrayList<>();
    static List<OrderItem> cart = new ArrayList<>();
    static UserDetails user = null;
    static final List<String> allowedCities = Arrays.asList("Chennai", "Chengalpattu", "Kancheepuram");

    public static void main(String[] args) {
        try {
            initCatalog();
            System.out.println("Welcome to SpringGrocery-Organic Store");
            System.out.println("Good Day, Please place your Order and Enter your Details for Home Delivery");
            System.out.println("Free home delivery on above Rs.500");
            System.out.println("Currently functional only in TamilNadu - Chennai, Chengalpattu, Kancheepuram");
            mainMenu();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }

    static void mainMenu() {
        while (true) {
            System.out.println("\nMain Menu:");
            System.out.println("1. Enter User Details");
            System.out.println("2. View Products");
            System.out.println("3. View Bill and Delivery Details");
            System.out.println("4. View Order History");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    enterUserDetails();
                    break;
                case "2":
                    if (user == null) {
                        System.out.println("Please enter user details first (option 1)");
                    } else {
                        viewCategories();
                    }
                    break;
                case "3":
                    if (cart.isEmpty()) {
                        System.out.println("No items ordered yet.");
                    } else if (user == null) {
                        System.out.println("Please enter user details first (option 1)");
                    } else {
                        displayBillAndDetails();
                    }
                    break;
                case "4":
                    if (user == null) {
                        System.out.println("Please enter user details first (option 1)");
                    } else {
                        viewOrderHistory();
                    }
                    break;
                case "5":
                    System.out.println("Thank you! Exiting the SpringGrocery. Come back soon.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }

    static void enterUserDetails() {
        UserDetails tempUser = new UserDetails();

        System.out.print("Enter your Name: ");
        tempUser.setName(scanner.nextLine());

        while (true) {
            System.out.print("Enter your Phone Number (10 digits): ");
            String phone = scanner.nextLine();
            if (phone.matches("\\d{10}")) {
                tempUser.setPhone(phone);
                break;
            }
            System.out.println("Invalid phone number. Please enter 10 digits.");
        }

        while (true) {
            System.out.print("Enter Alternative Phone Number (10 digits): ");
            String altPhone = scanner.nextLine();
            if (altPhone.matches("\\d{10}")) {
                tempUser.setAltPhone(altPhone);
                break;
            }
            System.out.println("Invalid phone number. Please enter 10 digits.");
        }

        while (true) {
            System.out.print("Enter Pincode (6 digits): ");
            String pincode = scanner.nextLine();
            if (pincode.matches("\\d{6}")) {
                tempUser.setPincode(pincode);
                break;
            }
            System.out.println("Invalid pincode. Please enter 6 digits.");
        }

        tempUser.setState("TamilNadu");

        System.out.println("Choose your City:");
        for (int i = 0; i < allowedCities.size(); i++) {
            System.out.println((i + 1) + ". " + allowedCities.get(i));
        }
        System.out.print("Enter choice (1-" + allowedCities.size() + "): ");
        int cityChoice;
        while (true) {
            try {
                cityChoice = Integer.parseInt(scanner.nextLine());
                if (cityChoice >= 1 && cityChoice <= allowedCities.size()) {
                    tempUser.setCity(allowedCities.get(cityChoice - 1));
                    break;
                }
                System.out.println("Invalid choice. Please enter between 1 and " + allowedCities.size());
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        System.out.print("Enter Address Line 1: ");
        tempUser.setAddressLine1(scanner.nextLine());

        System.out.print("Enter Address Line 2: ");
        tempUser.setAddressLine2(scanner.nextLine());

        System.out.println("Choose Delivery Date:");
        System.out.println("1. Today");
        System.out.println("2. Tomorrow");
        String dateChoice = scanner.nextLine();
        switch (dateChoice) {
            case "1":
                tempUser.setDeliveryDate("Today");
                break;
            case "2":
                tempUser.setDeliveryDate("Tomorrow");
                break;
            default:
                System.out.println("Invalid choice, Setting delivery date to Today.");
                tempUser.setDeliveryDate("Today");
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(tempUser);
            transaction.commit();
            user = tempUser;
            System.out.println("User details saved successfully.");
        } catch (Exception e) {
            System.out.println("Error saving user details: " + e.getMessage());
        }
    }

    static void viewCategories() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            while (true) {
                System.out.println("\nProduct Categories:");
                for (int i = 0; i < catalog.size(); i++) {
                    System.out.println((i + 1) + ". " + catalog.get(i).getName());
                }
                System.out.println("-1. Back to Main Menu");
                System.out.print("Choose category: ");
                String input = scanner.nextLine();

                if (input.equals("-1")) {
                    return;
                }

                try {
                    int choice = Integer.parseInt(input);
                    if (choice >= 1 && choice <= catalog.size()) {
                        viewProducts(session, catalog.get(choice - 1));
                    } else {
                        System.out.println("Invalid choice");
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input");
                }
            }
        }
    }

    static void viewProducts(Session session, Category category) {
        Category loadedCategory = session.createQuery(
                "SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.items WHERE c.id = :id",
                Category.class)
                .setParameter("id", category.getId())
                .getSingleResult();

        while (true) {
            System.out.println("\n" + loadedCategory.getName() + " Products:");
            for (int i = 0; i < loadedCategory.getItems().size(); i++) {
                Item item = loadedCategory.getItems().get(i);
                System.out.println((i + 1) + ". " + item.getName() +
                        " (" + item.getAvailableStock() + " " + item.getUnit() + " available)");
            }
            System.out.println("-1. Back to Categories");
            System.out.print("Choose product: ");
            String input = scanner.nextLine();

            if (input.equals("-1")) {
                return;
            }

            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= loadedCategory.getItems().size()) {
                    productMenu(session, loadedCategory.getItems().get(choice - 1));
                } else {
                    System.out.println("Invalid choice");
                }
            } catch (Exception e) {
                System.out.println("Invalid input");
            }
        }
    }

    static void productMenu(Session session, Item item) {
        Item managedItem = session.get(Item.class, item.getId());

        while (true) {
            System.out.println("\nProduct: " + managedItem.getName());
            System.out.println("Available: " + managedItem.getAvailableStock() + " " + managedItem.getUnit());
            System.out.println("Price: Rs." + managedItem.getPricePerUnit() + " per " + managedItem.getUnit());
            System.out.println("Choose Quantity:");

            List<Double> quantities = new ArrayList<>(managedItem.getAllowedQuantities());
            Collections.sort(quantities);

            for (int i = 0; i < quantities.size(); i++) {
                System.out.println((i + 1) + ". " + quantities.get(i) + " " + managedItem.getUnit());
            }
            System.out.println((quantities.size() + 1) + ". Back to Products");

            System.out.print("Choose option: ");
            String input = scanner.nextLine();

            try {
                int choice = Integer.parseInt(input);
                if (choice == quantities.size() + 1) {
                    return;
                }
                if (choice >= 1 && choice <= quantities.size()) {
                    double qty = quantities.get(choice - 1);
                    if (qty > managedItem.getAvailableStock()) {
                        System.out.println("Sorry only " + managedItem.getAvailableStock() + " " + managedItem.getUnit() + " available.");
                        continue;
                    }
                    cart.add(new OrderItem(managedItem, qty));
                    System.out.println("Added to cart: " + qty + " " + managedItem.getUnit() + " of " + managedItem.getName());
                    return;
                } else {
                    System.out.println("Invalid choice");
                }
            } catch (Exception e) {
                System.out.println("Invalid input");
            }
        }
    }

    static void displayBillAndDetails() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            System.out.println("\n--- Order Bill ---");
            double totalPrice = 0;
            LocalDateTime now = LocalDateTime.now();

            for (OrderItem cartItem : cart) {
                Order order = new Order();
                order.setUser(user);
                order.setItemName(cartItem.getItem().getName());
                order.setItemUnit(cartItem.getItem().getUnit());
                order.setCategoryName(cartItem.getItem().getCategory().getName());
                order.setQuantity(cartItem.getQuantity());
                order.setUnitPrice(cartItem.getItem().getPricePerUnit());
                order.setOrderDate(now);

                session.persist(order);

                System.out.printf("%s (Category: %s) - %.2f %s @ Rs.%.2f = Rs.%.2f\n",
                        order.getItemName(),
                        order.getCategoryName(),
                        order.getQuantity(),
                        order.getItemUnit(),
                        order.getUnitPrice(),
                        order.getQuantity() * order.getUnitPrice());

                totalPrice += order.getQuantity() * order.getUnitPrice();

                // Update inventory
                Item item = session.get(Item.class, cartItem.getItem().getId());
                item.setAvailableStock(item.getAvailableStock() - cartItem.getQuantity());
            }

            // Delivery calculation
            if (totalPrice >= 500) {
                System.out.println("Free Delivery");
            } else {
                System.out.println("Delivery Charge: Rs.100");
                totalPrice += 100;
            }
            System.out.printf("Amount to Pay: Rs.%.2f\n", totalPrice);

            System.out.println("\n--- Delivery Details ---");
            System.out.println(user);

            transaction.commit();
            cart.clear();
        } catch (Exception e) {
            System.out.println("Error processing order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static void viewOrderHistory() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Order> orders = session.createQuery(
                    "FROM Order WHERE user.id = :userId ORDER BY orderDate DESC",
                    Order.class)
                    .setParameter("userId", user.getId())
                    .getResultList();

            if (orders.isEmpty()) {
                System.out.println("No order history found.");
                return;
            }

            System.out.println("\n--- Your Order History ---");
            for (Order order : orders) {
                System.out.printf("[%s] %s - %.2f %s @ Rs.%.2f (Category: %s)\n",
                        order.getOrderDate(),
                        order.getItemName(),
                        order.getQuantity(),
                        order.getItemUnit(),
                        order.getUnitPrice(),
                        order.getCategoryName());
            }
        } catch (Exception e) {
            System.out.println("Error fetching order history: " + e.getMessage());
        }
    }

    static void initCatalog() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // First load categories with items
            List<Category> loadedCategories = session.createQuery(
                    "SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.items",
                    Category.class).getResultList();

            if (!loadedCategories.isEmpty()) {
                catalog = loadedCategories;

                // Then load allowed quantities in a separate query
                if (!loadedCategories.isEmpty()) {
                    session.createQuery(
                            "SELECT i FROM Item i LEFT JOIN FETCH i.allowedQuantities " +
                                    "WHERE i IN (SELECT i2 FROM Category c JOIN c.items i2)",
                            Item.class).getResultList();
                }
                return;
            }

            // Initialize sample data if database is empty
            Transaction transaction = session.beginTransaction();

            Set<Double> qtyKg = new HashSet<>(Arrays.asList(0.25, 0.5, 1.0));
            Set<Double> qtyLitre = new HashSet<>(Arrays.asList(0.25, 0.5, 1.0));

            // Initialize categories and items
            Category fruits = new Category("Fruits");
            fruits.addItem(new Item("Banana - Yelakki", "kg", 10, qtyKg, 90));
            fruits.addItem(new Item("Banana - Red Banana", "kg", 10, qtyKg, 100));
            fruits.addItem(new Item("Mango - Banganapalli", "kg", 10, qtyKg, 60));
            fruits.addItem(new Item("Papaya", "kg", 10, qtyKg, 30));
            fruits.addItem(new Item("Guava", "kg", 10, qtyKg, 70));

            Category vegetables = new Category("Vegetables");
            vegetables.addItem(new Item("Onion", "kg", 10, qtyKg, 30));
            vegetables.addItem(new Item("Tomato", "kg", 10, qtyKg, 25));
            vegetables.addItem(new Item("Potato", "kg", 10, qtyKg, 40));
            vegetables.addItem(new Item("Carrot", "kg", 10, qtyKg, 70));
            vegetables.addItem(new Item("Brinjal", "kg", 10, qtyKg, 50));

            Category riceCereals = new Category("Rice & Cereals");
            riceCereals.addItem(new Item("Basmati Rice", "kg", 10, qtyKg, 120));
            riceCereals.addItem(new Item("Idli Rice", "kg", 10, qtyKg, 50));
            riceCereals.addItem(new Item("Ragi", "kg", 10, qtyKg, 70));
            riceCereals.addItem(new Item("Wheat", "kg", 10, qtyKg, 35));

            Category oilsGhee = new Category("Oils & Ghee");
            oilsGhee.addItem(new Item("Sunflower Oil", "litre", 10, qtyLitre, 180));
            oilsGhee.addItem(new Item("Coconut Oil", "litre", 10, qtyLitre, 250));
            oilsGhee.addItem(new Item("Ghee", "litre", 10, qtyLitre, 300));

            session.persist(fruits);
            session.persist(vegetables);
            session.persist(riceCereals);
            session.persist(oilsGhee);

            transaction.commit();

            // Reload with proper fetching
            catalog = session.createQuery(
                    "SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.items",
                    Category.class).getResultList();

            session.createQuery(
                    "SELECT i FROM Item i LEFT JOIN FETCH i.allowedQuantities " +
                            "WHERE i IN (SELECT i2 FROM Category c JOIN c.items i2)",
                    Item.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}