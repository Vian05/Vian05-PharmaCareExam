package pharmacare.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Stores an immutable completed transaction and its purchase details. */
public class Transaction {
    private final String id;
    private final String customerId;
    private final String customerName;
    private final LocalDateTime dateTime;
    private final String paymentMethod;
    private final BigDecimal subtotal;
    private final BigDecimal paymentFee;
    private final BigDecimal total;
    private final TransactionStatus status;
    private final List<TransactionItem> items;

    public Transaction(String id, Customer customer, LocalDateTime dateTime, String paymentMethod,
                       BigDecimal subtotal, BigDecimal paymentFee, BigDecimal total,
                       List<TransactionItem> items) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Transaction ID cannot be empty.");
        if (customer == null) throw new IllegalArgumentException("Customer cannot be null.");
        if (dateTime == null || paymentMethod == null || paymentMethod.isBlank()) {
            throw new IllegalArgumentException("Transaction date and payment method are required.");
        }
        if (subtotal == null || paymentFee == null || total == null) {
            throw new IllegalArgumentException("Transaction amounts are required.");
        }
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("Transaction items cannot be empty.");
        this.id = id;
        this.customerId = customer.getId();
        this.customerName = customer.getName();
        this.dateTime = dateTime;
        this.paymentMethod = paymentMethod;
        this.subtotal = subtotal;
        this.paymentFee = paymentFee;
        this.total = total;
        this.status = TransactionStatus.COMPLETED;
        this.items = List.copyOf(items);
    }

    /** Returns the transaction ID. */
    public String getId() { return id; }

    /** Returns the customer ID. */
    public String getCustomerId() { return customerId; }

    /** Returns the customer name snapshot. */
    public String getCustomerName() { return customerName; }

    /** Returns the transaction date and time. */
    public LocalDateTime getDateTime() { return dateTime; }

    /** Returns the selected payment method. */
    public String getPaymentMethod() { return paymentMethod; }

    /** Returns the subtotal before payment fees. */
    public BigDecimal getSubtotal() { return subtotal; }

    /** Returns the payment fee. */
    public BigDecimal getPaymentFee() { return paymentFee; }

    /** Returns the final amount paid. */
    public BigDecimal getTotal() { return total; }

    /** Returns the transaction status. */
    public TransactionStatus getStatus() { return status; }

    /** Returns an immutable list of purchased item snapshots. */
    public List<TransactionItem> getItems() { return new ArrayList<>(items); }
}
