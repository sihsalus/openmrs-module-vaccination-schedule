package org.openmrs.module.vaccinationschedule.api.dao;

import java.util.Date;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.module.vaccinationschedule.model.PatientVaccinationSchedule;
import org.openmrs.module.vaccinationschedule.model.VaccinationAdministration;
import org.openmrs.module.vaccinationschedule.model.VaccinationSchedule;
import org.openmrs.module.vaccinationschedule.model.VaccinationScheduleEntry;
import org.openmrs.module.vaccinationschedule.model.VaccinationScheduleRule;
import org.springframework.stereotype.Repository;

@Repository
public interface VaccinationScheduleDao {
	
	VaccinationSchedule getVaccinationSchedule(Integer scheduleId);
	
	VaccinationSchedule getVaccinationScheduleByUuid(String uuid);
	
	VaccinationSchedule getActiveScheduleByCountry(String countryCode);
	
	List<VaccinationSchedule> getAllSchedules(boolean includeRetired);
	
	List<VaccinationSchedule> getSchedulesByCountry(String countryCode, boolean includeRetired);
	
	VaccinationSchedule saveVaccinationSchedule(VaccinationSchedule schedule);
	
	void deleteVaccinationSchedule(VaccinationSchedule schedule);
	
	// Schedule Entry methods
	VaccinationScheduleEntry getScheduleEntry(Integer entryId);
	
	VaccinationScheduleEntry getScheduleEntryByUuid(String uuid);
	
	VaccinationScheduleEntry saveScheduleEntry(VaccinationScheduleEntry entry);
	
	void deleteScheduleEntry(VaccinationScheduleEntry entry);
	
	List<VaccinationScheduleEntry> getEntriesBySchedule(VaccinationSchedule schedule);
	
	// Schedule Rule methods
	VaccinationScheduleRule getScheduleRule(Integer ruleId);
	
	VaccinationScheduleRule getScheduleRuleByUuid(String uuid);
	
	VaccinationScheduleRule saveScheduleRule(VaccinationScheduleRule rule);
	
	void deleteScheduleRule(VaccinationScheduleRule rule);
	
	List<VaccinationScheduleRule> getRulesBySchedule(VaccinationSchedule schedule);
	
	// Patient Schedule methods
	PatientVaccinationSchedule getPatientSchedule(Integer id);
	
	PatientVaccinationSchedule getPatientScheduleByUuid(String uuid);
	
	PatientVaccinationSchedule savePatientSchedule(PatientVaccinationSchedule patientSchedule);
	
	void deletePatientSchedule(PatientVaccinationSchedule patientSchedule);
	
	List<PatientVaccinationSchedule> getPatientSchedulesByPatient(Patient patient);
	
	List<PatientVaccinationSchedule> getPatientSchedulesBySchedule(VaccinationSchedule schedule);
	
	// CRITICAL: Vaccination Administration methods - Links schedules to actual
	// vaccinations
	VaccinationAdministration getVaccinationAdministration(Integer administrationId);
	
	VaccinationAdministration getVaccinationAdministrationByUuid(String uuid);
	
	VaccinationAdministration saveVaccinationAdministration(VaccinationAdministration administration);
	
	void deleteVaccinationAdministration(VaccinationAdministration administration);
	
	List<VaccinationAdministration> getAdministrationsByPatient(Patient patient);
	
	List<VaccinationAdministration> getAdministrationsByPatient(Patient patient, Date startDate, Date endDate);
	
	List<VaccinationAdministration> getAdministrationsByEncounter(Encounter encounter);
	
	List<VaccinationAdministration> getAdministrationsByLocation(Location location, Date startDate, Date endDate);
	
	List<VaccinationAdministration> getAdministrationsByScheduleEntry(VaccinationScheduleEntry entry);
	
	/**
	 * Find administrations for a specific patient and vaccine concept Used to determine vaccination
	 * history
	 */
	List<VaccinationAdministration> getAdministrationsByPatientAndVaccine(Patient patient,
	        org.openmrs.Concept vaccineConcept);
	
	/**
	 * Check if a patient has received a specific dose of a vaccine Critical for determining vaccination
	 * status
	 */
	VaccinationAdministration getAdministrationByPatientEntryAndDose(Patient patient, VaccinationScheduleEntry entry,
	        Integer doseNumber);
}
