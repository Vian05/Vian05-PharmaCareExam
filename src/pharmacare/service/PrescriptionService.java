package pharmacare.service;

import pharmacare.model.Prescription;
import pharmacare.model.PrescriptionStatus;

import java.util.ArrayList;
import java.util.List;

/** Stores prescriptions and provides pharmacist review operations. */
public class PrescriptionService {
    private final ArrayList<Prescription> prescriptions = new ArrayList<>();
    private int nextId = 1;

    /** Registers a prescription as pending pharmacist review. */
    public Prescription submitPrescription(String customerId, String filePath) {
        Prescription prescription = new Prescription("RX-" + nextId++, customerId, filePath);
        prescriptions.add(prescription);
        return prescription;
    }

    /** Returns a defensive copy of all submitted prescriptions. */
    public List<Prescription> getAllPrescriptions() {
        return new ArrayList<>(prescriptions);
    }

    /** Finds the latest approved prescription for a customer. */
    public boolean hasApprovedPrescription(String customerId) {
        for (Prescription prescription : prescriptions) {
            if (prescription.getCustomerId().equalsIgnoreCase(customerId)
                    && prescription.getStatus() == PrescriptionStatus.APPROVED) return true;
        }
        return false;
    }

    /** Marks one approved prescription as consumed after a successful checkout. */
    public void consumeApprovedPrescription(String customerId) {
        for (Prescription prescription : prescriptions) {
            if (prescription.getCustomerId().equalsIgnoreCase(customerId)
                    && prescription.getStatus() == PrescriptionStatus.APPROVED) {
                prescription.setStatus(PrescriptionStatus.CONSUMED);
                return;
            }
        }
        throw new IllegalArgumentException("No approved prescription is available for this customer.");
    }

    /** Updates a prescription to approved or rejected after pharmacist review. */
    public void reviewPrescription(String prescriptionId, PrescriptionStatus decision) {
        if (decision != PrescriptionStatus.APPROVED && decision != PrescriptionStatus.REJECTED) {
            throw new IllegalArgumentException("Review decision must be APPROVED or REJECTED.");
        }
        for (Prescription prescription : prescriptions) {
            if (prescription.getId().equalsIgnoreCase(prescriptionId)) {
                if (prescription.getStatus() != PrescriptionStatus.PENDING) {
                    throw new IllegalArgumentException("Only pending prescriptions can be reviewed.");
                }
                prescription.setStatus(decision);
                return;
            }
        }
        throw new IllegalArgumentException("Prescription not found: " + prescriptionId);
    }
}
