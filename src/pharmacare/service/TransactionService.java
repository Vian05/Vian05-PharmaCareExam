package pharmacare.service;

import pharmacare.model.CartItem;
import pharmacare.model.Customer;
import pharmacare.model.Transaction;
import pharmacare.model.TransactionItem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Stores completed transactions and provides customer history and sales summaries. */
public class TransactionService {
    private final ArrayList<Transaction> transactions = new ArrayList<>();
    private int nextId = 1;

    /** Creates and stores a transaction snapshot before the cart is cleared. */
    public Transaction recordTransaction(Customer customer, List<CartItem> cartItems,
                                         String paymentMethod, BigDecimal subtotal,
                                         BigDecimal fee, BigDecimal total) {
        if (cartItems == null || cartItems.isEmpty()) throw new IllegalArgumentException("Transaction items cannot be empty.");
        List<TransactionItem> items = new ArrayList<>();
        for (CartItem item : cartItems) {
            items.add(new TransactionItem(item.getMedicine().getId(), item.getMedicine().getName(),
                    item.getQuantity(), item.getMedicine().getPrice()));
        }
        Transaction transaction = new Transaction(String.format("TRX-%05d", nextId++), customer,
                LocalDateTime.now(), paymentMethod, subtotal, fee, total, items);
        transactions.add(transaction);
        return transaction;
    }

    /** Returns a defensive copy of all transactions. */
    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    /** Returns transaction history belonging to one customer ID. */
    public List<Transaction> getCustomerHistory(String customerId) {
        List<Transaction> history = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getCustomerId().equalsIgnoreCase(customerId)) history.add(transaction);
        }
        history.sort(Comparator.comparing(Transaction::getDateTime).reversed());
        return history;
    }

    /** Filters completed transactions by a calendar date. */
    public List<Transaction> getDailyTransactions(LocalDate date) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getDateTime().toLocalDate().equals(date)) result.add(transaction);
        }
        return result;
    }

    /** Filters completed transactions by calendar month and year. */
    public List<Transaction> getMonthlyTransactions(YearMonth month) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (YearMonth.from(transaction.getDateTime()).equals(month)) result.add(transaction);
        }
        return result;
    }

    /** Calculates revenue from the final paid totals in a transaction collection. */
    public BigDecimal getRevenue(List<Transaction> records) {
        BigDecimal revenue = BigDecimal.ZERO;
        for (Transaction transaction : records) revenue = revenue.add(transaction.getTotal());
        return revenue;
    }

    /** Counts quantities sold by medicine and returns the top three products. */
    public Map<String, Integer> getTopSellingMedicines(List<Transaction> records) {
        Map<String, Integer> quantities = new LinkedHashMap<>();
        for (Transaction transaction : records) {
            for (TransactionItem item : transaction.getItems()) {
                quantities.merge(item.getMedicineName(), item.getQuantity(), Math::addExact);
            }
        }
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(quantities.entrySet());
        sorted.sort(Map.Entry.<String, Integer>comparingByValue().reversed());
        Map<String, Integer> topThree = new LinkedHashMap<>();
        for (int i = 0; i < Math.min(3, sorted.size()); i++) {
            topThree.put(sorted.get(i).getKey(), sorted.get(i).getValue());
        }
        return topThree;
    }

    /** Breaks down transaction counts and revenue by payment method. */
    public Map<String, BigDecimal> getPaymentBreakdown(List<Transaction> records) {
        Map<String, BigDecimal> breakdown = new LinkedHashMap<>();
        for (Transaction transaction : records) {
            breakdown.merge(transaction.getPaymentMethod(), transaction.getTotal(), BigDecimal::add);
        }
        return breakdown;
    }
}
