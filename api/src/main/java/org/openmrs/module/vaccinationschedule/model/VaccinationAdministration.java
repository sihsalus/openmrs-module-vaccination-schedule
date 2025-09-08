package org.openmrs.module.vaccinationschedule.model;

import java.math.BigDecimal;
import java.util.Date;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.Visit;

/**
 * CRITICAL MODEL: Represents an actual vaccination administration Links
 * vaccination schedules with
 * real OpenMRS clinical data (obs, encounters, etc.) This is the bridge between
 * vaccination
 * schedules and actual clinical practice.
 */
public class VaccinationAdministration extends BaseOpenmrsData {

    /**
     * Status enum compatible with FHIR Immunization status codes Maps internal
     * statuses to
     * FHIR-compliant values while maintaining extended functionality
     */
    public enum Status {

        SCHEDULED("completed", "scheduled"), // Internal: scheduled, FHIR: completed when administered
        COMPLETED("completed", "completed"), // Both: completed
        MISSED("entered-in-error", "missed"), // Internal: missed, FHIR: entered-in-error
        CONTRAINDICATED("entered-in-error", "contraindicated"), // Internal: contraindicated, FHIR: entered-in-error
        REFUSED("entered-in-error", "refused"), // Internal: refused, FHIR: entered-in-error
        DEFERRED("completed", "deferred"); // Internal: deferred, FHIR: completed when eventually given

        private final String fhirCode;

        private final String internalCode;

        Status(String fhirCode, String internalCode) {
            this.fhirCode = fhirCode;
            this.internalCode = internalCode;
        }

        /**
         * Get FHIR-compliant status code for OpenMRS FHIR module compatibility
         */
        public String getFhirCode() {
            return fhirCode;
        }

        /**
         * Get internal status code for advanced vaccination schedule functionality
         */
        public String getInternalCode() {
            return internalCode;
        }

        /**
         * Check if this status represents a successfully administered vaccine for FHIR
         */
        public boolean isFhirCompleted() {
            return "completed".equals(fhirCode);
        }

        /**
         * Check if this status represents an error/cancellation for FHIR
         */
        public boolean isFhirError() {
            return "entered-in-error".equals(fhirCode);
        }

        /**
         * Get Status from FHIR code - useful for FHIR module integration
         */
        public static Status fromFhirCode(String fhirCode) {
            for (Status status : values()) {
                if (status.fhirCode.equals(fhirCode)) {
                    return status; // Return first match - COMPLETED for "completed"
                }
            }
            return COMPLETED; // Default fallback
        }

        /**
         * Get Status from internal code - useful for internal operations
         */
        public static Status fromInternalCode(String internalCode) {
            for (Status status : values()) {
                if (status.internalCode.equals(internalCode)) {
                    return status;
                }
            }
            return COMPLETED; // Default fallback
        }
    }

    private Integer administrationId;

    // Core relationships
    private Patient patient;

    private VaccinationScheduleEntry vaccinationScheduleEntry;

    // CRITICAL: Integration with OpenMRS core
    private Obs obs; // The observation recording this vaccination

    private Encounter encounter; // The clinical encounter where vaccine was given

    private Location location; // Where the vaccine was administered

    private Provider provider; // Medical provider who administered

    private Visit visit; // The visit during which vaccine was given

    // Administration details
    private Date administrationDate;

    private User administeredBy; // User who physically gave the vaccine

    private BigDecimal doseAdministered;

    private String doseUnits;

    private String lotNumber;

    private Date expiryDate;

    private String manufacturer;

    private String siteAdministered; // Body site (left arm, right thigh, etc.)

    private String routeAdministered; // Route (IM, SC, oral, etc.)

    private String adverseReaction; // Any adverse reactions noted

    private String notes;

    private Status status;

    // Constructors
    public VaccinationAdministration() {
        this.status = Status.SCHEDULED;
    }

    public VaccinationAdministration(Patient patient, VaccinationScheduleEntry entry, Encounter encounter) {
        this();
        this.patient = patient;
        this.vaccinationScheduleEntry = entry;
        this.encounter = encounter;
    }

    // Getters and Setters
    public Integer getAdministrationId() {
        return administrationId;
    }

    public void setAdministrationId(Integer administrationId) {
        this.administrationId = administrationId;
    }

    @Override
    public Integer getId() {
        return getAdministrationId();
    }

    @Override
    public void setId(Integer id) {
        setAdministrationId(id);
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public VaccinationScheduleEntry getVaccinationScheduleEntry() {
        return vaccinationScheduleEntry;
    }

    public void setVaccinationScheduleEntry(VaccinationScheduleEntry vaccinationScheduleEntry) {
        this.vaccinationScheduleEntry = vaccinationScheduleEntry;
    }

    public Obs getObs() {
        return obs;
    }

    public void setObs(Obs obs) {
        this.obs = obs;
    }

    public Encounter getEncounter() {
        return encounter;
    }

    public void setEncounter(Encounter encounter) {
        this.encounter = encounter;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    public Date getAdministrationDate() {
        return administrationDate;
    }

    public void setAdministrationDate(Date administrationDate) {
        this.administrationDate = administrationDate;
    }

    public User getAdministeredBy() {
        return administeredBy;
    }

    public void setAdministeredBy(User administeredBy) {
        this.administeredBy = administeredBy;
    }

    public BigDecimal getDoseAdministered() {
        return doseAdministered;
    }

    public void setDoseAdministered(BigDecimal doseAdministered) {
        this.doseAdministered = doseAdministered;
    }

    public String getDoseUnits() {
        return doseUnits;
    }

    public void setDoseUnits(String doseUnits) {
        this.doseUnits = doseUnits;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getSiteAdministered() {
        return siteAdministered;
    }

    public void setSiteAdministered(String siteAdministered) {
        this.siteAdministered = siteAdministered;
    }

    public String getRouteAdministered() {
        return routeAdministered;
    }

    public void setRouteAdministered(String routeAdministered) {
        this.routeAdministered = routeAdministered;
    }

    public String getAdverseReaction() {
        return adverseReaction;
    }

    public void setAdverseReaction(String adverseReaction) {
        this.adverseReaction = adverseReaction;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    // Utility methods
    public boolean isCompleted() {
        return Status.COMPLETED.equals(this.status);
    }

    public boolean isMissed() {
        return Status.MISSED.equals(this.status);
    }

    public boolean hasAdverseReaction() {
        return this.adverseReaction != null && !this.adverseReaction.trim().isEmpty();
    }

    /**
     * Gets the vaccine concept from the associated schedule entry
     */
    public org.openmrs.Concept getVaccineConcept() {
        return vaccinationScheduleEntry != null ? vaccinationScheduleEntry.getVaccineConcept() : null;
    }

    /**
     * Gets the dose number from the associated schedule entry
     */
    public Integer getDoseNumber() {
        return vaccinationScheduleEntry != null ? vaccinationScheduleEntry.getDoseNumber() : null;
    }

    /**
     * FHIR Integration: Get dose number in FHIR protocolApplied format
     */
    public Integer getFhirDoseNumber() {
        return getDoseNumber();
    }

    /**
     * FHIR Integration: Check if this administration should create an obs Only
     * completed vaccinations
     * should create obs for FHIR compatibility
     */
    public boolean shouldCreateObs() {
        return status != null && (status.isFhirCompleted() || status == Status.COMPLETED);
    }

    /**
     * OpenMRS Integration: Get obs value for immunization recording Compatible with
     * existing OpenMRS
     * immunization concepts
     */
    public String getObsValue() {
        if (!shouldCreateObs()) {
            return null;
        }

        StringBuilder value = new StringBuilder();
        if (lotNumber != null) {
            value.append("Lot: ").append(lotNumber);
        }
        if (manufacturer != null) {
            if (value.length() > 0)
                value.append(", ");
            value.append("Mfr: ").append(manufacturer);
        }
        if (getDoseNumber() != null) {
            if (value.length() > 0)
                value.append(", ");
            value.append("Dose: ").append(getDoseNumber());
        }

        return value.length() > 0 ? value.toString() : "Administered";
    }

    /**
     * Get display string for frontend compatibility
     */
    public String getDisplayString() {
        String vaccineName = getVaccineConcept() != null ? getVaccineConcept().getName().getName() : "Unknown Vaccine";
        String doseInfo = getDoseNumber() != null ? " (Dose " + getDoseNumber() + ")" : "";
        return vaccineName + doseInfo;
    }

    @Override
    public String toString() {
        return String.format("VaccinationAdministration{id=%d, patient=%s, vaccine=%s, dose=%d, date=%s, status=%s}",
                administrationId, patient != null ? patient.getPatientId() : "null",
                getVaccineConcept() != null ? getVaccineConcept().getName() : "null", getDoseNumber(),
                administrationDate,
                status);
    }
}
