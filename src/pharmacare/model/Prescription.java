package pharmacare.model;

/** Stores a simulated uploaded prescription and its pharmacist review status. */
public class Prescription {
    private final String id;
    private final String customerId;
    private final String filePath;
    private PrescriptionStatus status;

    public Prescription(String id, String customerId, String filePath) {
        this.id = requireText(id, "Prescription ID");
        this.customerId = requireText(customerId, "Customer ID");
        this.filePath = requireText(filePath, "Prescription file path");
        this.status = PrescriptionStatus.PENDING;
    }

    /** Returns the prescription ID. */
    public String getId() { return id; }

    /** Returns the customer ID associated with this prescription. */
    public String getCustomerId() { return customerId; }

    /** Returns the uploaded file path. */
    public String getFilePath() { return filePath; }

    /** Returns the current review status. */
    public PrescriptionStatus getStatus() { return status; }

    /** Updates the prescription review status. */
    public void setStatus(PrescriptionStatus status) {
        if (status == null) throw new IllegalArgumentException("Prescription status cannot be null.");
        this.status = status;
    }

    /** Validates and trims a required text value. */
    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " cannot be empty.");
        return value.trim();
    }
}
