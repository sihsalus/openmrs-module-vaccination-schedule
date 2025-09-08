package org.openmrs.module.vaccinationschedule.api;

import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.vaccinationschedule.model.*;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface VaccinationScheduleService extends OpenmrsService {
	
	String MANAGE_VACCINATION_SCHEDULES = "Manage Vaccination Schedules";
	
	String VIEW_VACCINATION_SCHEDULES = "View Vaccination Schedules";
	
	String ASSIGN_VACCINATION_SCHEDULES = "Assign Vaccination Schedules";
	
	String ADMINISTER_VACCINES = "Administer Vaccines";
	
	@Authorized(MANAGE_VACCINATION_SCHEDULES)
	VaccinationSchedule saveVaccinationSchedule(VaccinationSchedule schedule);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	VaccinationSchedule getVaccinationSchedule(Integer scheduleId);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	VaccinationSchedule getVaccinationScheduleByUuid(String uuid);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	VaccinationSchedule getActiveScheduleByCountry(String countryCode);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	List<VaccinationSchedule> getAllSchedules(boolean includeRetired);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	List<VaccinationSchedule> getSchedulesByCountry(String countryCode, boolean includeRetired);
	
	@Authorized(MANAGE_VACCINATION_SCHEDULES)
	VaccinationSchedule retireVaccinationSchedule(VaccinationSchedule schedule, String reason);
	
	@Authorized(MANAGE_VACCINATION_SCHEDULES)
	VaccinationSchedule unretireVaccinationSchedule(VaccinationSchedule schedule);
	
	@Authorized(MANAGE_VACCINATION_SCHEDULES)
	VaccinationScheduleEntry saveScheduleEntry(VaccinationScheduleEntry entry);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	VaccinationScheduleEntry getScheduleEntry(Integer entryId);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	VaccinationScheduleEntry getScheduleEntryByUuid(String uuid);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	List<VaccinationScheduleEntry> getEntriesBySchedule(VaccinationSchedule schedule);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	List<VaccinationScheduleEntry> getEntriesForAge(VaccinationSchedule schedule, int ageInDays);
	
	@Authorized(MANAGE_VACCINATION_SCHEDULES)
	VaccinationScheduleEntry voidScheduleEntry(VaccinationScheduleEntry entry, String reason);
	
	@Authorized(MANAGE_VACCINATION_SCHEDULES)
	VaccinationScheduleEntry unvoidScheduleEntry(VaccinationScheduleEntry entry);
	
	@Authorized(MANAGE_VACCINATION_SCHEDULES)
	VaccinationScheduleRule saveScheduleRule(VaccinationScheduleRule rule);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	VaccinationScheduleRule getScheduleRule(Integer ruleId);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	VaccinationScheduleRule getScheduleRuleByUuid(String uuid);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	List<VaccinationScheduleRule> getRulesBySchedule(VaccinationSchedule schedule);
	
	@Authorized(MANAGE_VACCINATION_SCHEDULES)
	VaccinationScheduleRule voidScheduleRule(VaccinationScheduleRule rule, String reason);
	
	@Authorized(MANAGE_VACCINATION_SCHEDULES)
	VaccinationScheduleRule unvoidScheduleRule(VaccinationScheduleRule rule);
	
	@Authorized(ASSIGN_VACCINATION_SCHEDULES)
	PatientVaccinationSchedule assignScheduleToPatient(Patient patient, VaccinationSchedule schedule);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	PatientVaccinationSchedule getPatientSchedule(Patient patient);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	PatientVaccinationSchedule getPatientScheduleByUuid(String uuid);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	List<PatientVaccinationSchedule> getPatientSchedulesBySchedule(VaccinationSchedule schedule);
	
	@Authorized(ASSIGN_VACCINATION_SCHEDULES)
	PatientVaccinationSchedule updatePatientScheduleStatus(PatientVaccinationSchedule patientSchedule,
	        PatientVaccinationSchedule.Status status);
	
	@Authorized(ASSIGN_VACCINATION_SCHEDULES)
	PatientVaccinationSchedule voidPatientSchedule(PatientVaccinationSchedule patientSchedule, String reason);
	
	@Authorized(ASSIGN_VACCINATION_SCHEDULES)
	PatientVaccinationSchedule unvoidPatientSchedule(PatientVaccinationSchedule patientSchedule);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	List<PatientVaccinationStatus> calculateVaccinationStatuses(Patient patient);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	List<PatientVaccinationStatus> getDueVaccinations(Patient patient, Date asOfDate);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	List<PatientVaccinationStatus> getOverdueVaccinations(Patient patient, Date asOfDate);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	List<PatientVaccinationStatus> getUpcomingVaccinations(Patient patient, int daysAhead);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	List<PatientVaccinationStatus> getVaccinationCalendar(Patient patient, Date startDate, Date endDate);
	
	@Authorized(ADMINISTER_VACCINES)
	void recordVaccineAdministration(Patient patient, VaccinationScheduleEntry entry, Encounter encounter,
	        Obs immunizationObs);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	boolean isVaccineAdministered(Patient patient, Concept vaccineConcept, Integer doseNumber);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	List<Obs> getAdministeredVaccines(Patient patient);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	List<Obs> getAdministeredVaccinesForConcept(Patient patient, Concept vaccineConcept);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	List<VaccinationScheduleRule> evaluateRulesForPatient(Patient patient, VaccinationScheduleEntry entry);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	boolean hasContraindications(Patient patient, VaccinationScheduleEntry entry);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	List<VaccinationScheduleRule> getContraindications(Patient patient, VaccinationScheduleEntry entry);
	
	@Authorized(ADMINISTER_VACCINES)
	PatientVaccinationStatus markVaccineAsContraindicated(Patient patient, VaccinationScheduleEntry entry, String reason);
	
	@Authorized(ADMINISTER_VACCINES)
	PatientVaccinationStatus markVaccineAsSkipped(Patient patient, VaccinationScheduleEntry entry, String reason);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	List<PatientVaccinationStatus> getVaccinationHistory(Patient patient);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	boolean validateDoseSequence(Patient patient, VaccinationScheduleEntry entry);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	List<VaccinationScheduleEntry> getNextRecommendedVaccinations(Patient patient, int maxResults);
	
	// CRITICAL: Vaccination Administration methods - Links schedules to actual vaccinations
	
	@Authorized(ADMINISTER_VACCINES)
	VaccinationAdministration saveVaccinationAdministration(VaccinationAdministration administration);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	VaccinationAdministration getVaccinationAdministration(Integer administrationId);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	VaccinationAdministration getVaccinationAdministrationByUuid(String uuid);
	
	@Authorized(ADMINISTER_VACCINES)
	void deleteVaccinationAdministration(VaccinationAdministration administration);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	List<VaccinationAdministration> getVaccinationAdministrationsByPatient(Patient patient);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	List<VaccinationAdministration> getVaccinationAdministrationsByPatient(Patient patient, Date startDate, Date endDate);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	List<VaccinationAdministration> getVaccinationAdministrationsByEncounter(Encounter encounter);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	List<VaccinationAdministration> getVaccinationAdministrationsByLocation(Location location, Date startDate, Date endDate);
	
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	List<VaccinationAdministration> getVaccinationAdministrationsByScheduleEntry(VaccinationScheduleEntry entry);
	
	/**
	 * Find administrations for a specific patient and vaccine concept Used to determine vaccination
	 * history
	 */
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	List<VaccinationAdministration> getVaccinationAdministrationsByPatientAndVaccine(Patient patient,
	        Concept vaccineConcept);
	
	/**
	 * Check if a patient has received a specific dose of a vaccine Critical for determining vaccination
	 * status
	 */
	@Authorized(VIEW_VACCINATION_SCHEDULES)
	VaccinationAdministration getVaccinationAdministrationByPatientEntryAndDose(Patient patient,
	        VaccinationScheduleEntry entry, Integer doseNumber);
}
