package pharmacare.model;

/** Represents a customer using the PharmaCare application. */
public class Customer {
    private final String id;
    private String name;

    public Customer(String id, String name) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Customer ID cannot be empty.");
        this.id = id.trim();
        setName(name);
    }

    /** Returns the customer ID. */
    public String getId() { return id; }

    /** Returns the customer name. */
    public String getName() { return name; }

    /** Updates the customer name. */
    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Customer name cannot be empty.");
        this.name = name.trim();
    }
}
