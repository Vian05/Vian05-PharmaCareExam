package pharmacare.service;

import pharmacare.model.LimitedMedicine;
import pharmacare.model.Medicine;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Manages the medicine catalog and stock operations in memory. */
public class PharmacyService {
    private final ArrayList<Medicine> medicines = new ArrayList<>();

    /** Adds a medicine after checking that its ID is unique. */
    public void addMedicine(Medicine medicine) {
        if (medicine == null) throw new IllegalArgumentException("Medicine cannot be null.");
        if (findMedicine(medicine.getId()) != null) {
            throw new IllegalArgumentException("Medicine ID already exists: " + medicine.getId());
        }
        medicines.add(medicine);
    }

    /** Returns a defensive copy of the current medicine catalog. */
    public List<Medicine> getAllMedicines() {
        return new ArrayList<>(medicines);
    }

    /** Finds a medicine by its ID, or returns null when it does not exist. */
    public Medicine findMedicine(String id) {
        if (id == null) return null;
        for (Medicine medicine : medicines) {
            if (medicine.getId().equalsIgnoreCase(id.trim())) return medicine;
        }
        return null;
    }

    /** Updates common medicine details and the limited-category purchase cap when applicable. */
    public void updateMedicine(String id, String name, String description,
                               BigDecimal price, Integer maxPurchaseQuantity) {
        Medicine medicine = requireMedicine(id);
        if (medicine instanceof LimitedMedicine && maxPurchaseQuantity == null) {
            throw new IllegalArgumentException("A purchase limit is required for this medicine.");
        }
        medicine.setName(name);
        medicine.setDescription(description);
        medicine.setPrice(price);

        if (medicine instanceof LimitedMedicine limitedMedicine) {
            limitedMedicine.setMaxPurchaseQuantity(maxPurchaseQuantity);
        }
    }

    /** Deletes a medicine by ID and returns whether a record was removed. */
    public boolean deleteMedicine(String id) {
        Medicine medicine = findMedicine(id);
        return medicine != null && medicines.remove(medicine);
    }

    /** Adds stock to a medicine identified by ID. */
    public void restockMedicine(String id, int quantity) {
        requireMedicine(id).addStock(quantity);
    }

    /** Returns medicines whose stock is below the supplied threshold. */
    public List<Medicine> getLowStockMedicines(int threshold) {
        if (threshold < 0) throw new IllegalArgumentException("Threshold cannot be negative.");
        ArrayList<Medicine> result = new ArrayList<>();
        for (Medicine medicine : medicines) {
            if (medicine.getStock() < threshold) result.add(medicine);
        }
        return result;
    }

    /** Finds a medicine and throws a clear error when the ID is unknown. */
    private Medicine requireMedicine(String id) {
        Medicine medicine = findMedicine(id);
        if (medicine == null) throw new IllegalArgumentException("Medicine not found: " + id);
        return medicine;
    }
}
