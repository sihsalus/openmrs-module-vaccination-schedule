package org.openmrs.module.vaccinationschedule.api.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.vaccinationschedule.api.VaccinationScheduleService;
import org.openmrs.module.vaccinationschedule.api.dao.VaccinationScheduleDao;
import org.openmrs.module.vaccinationschedule.model.PatientVaccinationSchedule;
import org.openmrs.module.vaccinationschedule.model.PatientVaccinationStatus;
import org.openmrs.module.vaccinationschedule.model.VaccinationAdministration;
import org.openmrs.module.vaccinationschedule.model.VaccinationSchedule;
import org.openmrs.module.vaccinationschedule.model.VaccinationScheduleEntry;
import org.openmrs.module.vaccinationschedule.model.VaccinationScheduleRule;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class VaccinationScheduleServiceImpl extends BaseOpenmrsService implements VaccinationScheduleService {

	private static final Log log = LogFactory.getLog(VaccinationScheduleServiceImpl.class);

	private VaccinationScheduleDao vaccinationScheduleDao;

	public void setVaccinationScheduleDao(VaccinationScheduleDao vaccinationScheduleDao) {
		this.vaccinationScheduleDao = vaccinationScheduleDao;
	}

	@Override
	public VaccinationSchedule saveVaccinationSchedule(VaccinationSchedule schedule) {
		if (schedule == null) {
			throw new APIException("VaccinationSchedule cannot be null");
		}

		if (schedule.getName() == null || schedule.getName().trim().isEmpty()) {
			throw new APIException("VaccinationSchedule name is required");
		}

		if (schedule.getCountryCode() != null && schedule.getCountryCode().length() > 3) {
			throw new APIException("Country code cannot exceed 3 characters");
		}

		if (schedule.getEffectiveDate() == null) {
			throw new APIException("Effective date is required");
		}

		return vaccinationScheduleDao.saveVaccinationSchedule(schedule);
	}

	@Override
	public VaccinationSchedule getVaccinationSchedule(Integer scheduleId) {
		return vaccinationScheduleDao.getVaccinationSchedule(scheduleId);
	}

	@Override
	public VaccinationSchedule getVaccinationScheduleByUuid(String uuid) {
		return vaccinationScheduleDao.getVaccinationScheduleByUuid(uuid);
	}

	@Override
	public VaccinationSchedule getActiveScheduleByCountry(String countryCode) {
		return vaccinationScheduleDao.getActiveScheduleByCountry(countryCode);
	}

	@Override
	public List<VaccinationSchedule> getAllSchedules(boolean includeRetired) {
		return vaccinationScheduleDao.getAllSchedules(includeRetired);
	}

	@Override
	public List<VaccinationSchedule> getSchedulesByCountry(String countryCode, boolean includeRetired) {
		return vaccinationScheduleDao.getSchedulesByCountry(countryCode, includeRetired);
	}

	@Override
	public VaccinationSchedule retireVaccinationSchedule(VaccinationSchedule schedule, String reason) {
		schedule.setRetired(true);
		schedule.setRetireReason(reason);
		schedule.setRetiredBy(Context.getAuthenticatedUser());
		schedule.setDateRetired(new Date());
		return saveVaccinationSchedule(schedule);
	}

	@Override
	public VaccinationSchedule unretireVaccinationSchedule(VaccinationSchedule schedule) {
		schedule.setRetired(false);
		schedule.setRetireReason(null);
		schedule.setRetiredBy(null);
		schedule.setDateRetired(null);
		return saveVaccinationSchedule(schedule);
	}

	@Override
	public VaccinationScheduleEntry saveScheduleEntry(VaccinationScheduleEntry entry) {
		if (entry == null) {
			throw new APIException("VaccinationScheduleEntry cannot be null");
		}

		if (entry.getVaccineConcept() == null) {
			throw new APIException("Vaccine concept is required");
		}

		if (entry.getDoseNumber() == null || entry.getDoseNumber() <= 0) {
			throw new APIException("Dose number must be positive");
		}

		if (entry.getAgeInDaysMin() == null || entry.getAgeInDaysRecommended() == null) {
			throw new APIException("Age in days (min and recommended) are required");
		}

		if (entry.getAgeInDaysMin() > entry.getAgeInDaysRecommended()) {
			throw new APIException("Minimum age cannot be greater than recommended age");
		}

		if (entry.getAgeInDaysMax() != null && entry.getAgeInDaysRecommended() > entry.getAgeInDaysMax()) {
			throw new APIException("Recommended age cannot be greater than maximum age");
		}

		return vaccinationScheduleDao.saveScheduleEntry(entry);
	}

	@Override
	public VaccinationScheduleEntry getScheduleEntry(Integer entryId) {
		return vaccinationScheduleDao.getScheduleEntry(entryId);
	}

	@Override
	public VaccinationScheduleEntry getScheduleEntryByUuid(String uuid) {
		return vaccinationScheduleDao.getScheduleEntryByUuid(uuid);
	}

	@Override
	public List<VaccinationScheduleEntry> getEntriesBySchedule(VaccinationSchedule schedule) {
		if (schedule == null) {
			return new ArrayList<>();
		}
		return schedule.getActiveEntries();
	}

	@Override
	public List<VaccinationScheduleEntry> getEntriesForAge(VaccinationSchedule schedule, int ageInDays) {
		if (schedule == null) {
			return new ArrayList<>();
		}
		return schedule.getEntriesByAge(ageInDays);
	}

	@Override
	public VaccinationScheduleEntry voidScheduleEntry(VaccinationScheduleEntry entry, String reason) {
		entry.setVoided(true);
		entry.setVoidReason(reason);
		entry.setVoidedBy(Context.getAuthenticatedUser());
		entry.setDateVoided(new Date());
		return saveScheduleEntry(entry);
	}

	@Override
	public VaccinationScheduleEntry unvoidScheduleEntry(VaccinationScheduleEntry entry) {
		entry.setVoided(false);
		entry.setVoidReason(null);
		entry.setVoidedBy(null);
		entry.setDateVoided(null);
		return saveScheduleEntry(entry);
	}

	@Override
	public VaccinationScheduleRule saveScheduleRule(VaccinationScheduleRule rule) {
		if (rule == null) {
			throw new APIException("VaccinationScheduleRule cannot be null");
		}

		if (rule.getVaccinationSchedule() == null) {
			throw new APIException("VaccinationSchedule is required for rule");
		}

		if (rule.getRuleType() == null) {
			throw new APIException("Rule type is required");
		}

		return vaccinationScheduleDao.saveScheduleRule(rule);
	}

	@Override
	public VaccinationScheduleRule getScheduleRule(Integer ruleId) {
		if (ruleId == null) {
			return null;
		}
		return vaccinationScheduleDao.getScheduleRule(ruleId);
	}

	@Override
	public VaccinationScheduleRule getScheduleRuleByUuid(String uuid) {
		if (uuid == null || uuid.trim().isEmpty()) {
			return null;
		}
		return vaccinationScheduleDao.getScheduleRuleByUuid(uuid);
	}

	@Override
	public List<VaccinationScheduleRule> getRulesBySchedule(VaccinationSchedule schedule) {
		if (schedule == null) {
			return new ArrayList<>();
		}
		return vaccinationScheduleDao.getRulesBySchedule(schedule);
	}

	@Override
	public VaccinationScheduleRule voidScheduleRule(VaccinationScheduleRule rule, String reason) {
		if (rule == null) {
			throw new APIException("VaccinationScheduleRule cannot be null");
		}

		rule.setVoided(true);
		rule.setVoidReason(reason);
		rule.setVoidedBy(Context.getAuthenticatedUser());
		rule.setDateVoided(new Date());

		return vaccinationScheduleDao.saveScheduleRule(rule);
	}

	@Override
	public VaccinationScheduleRule unvoidScheduleRule(VaccinationScheduleRule rule) {
		if (rule == null) {
			throw new APIException("VaccinationScheduleRule cannot be null");
		}

		rule.setVoided(false);
		rule.setVoidReason(null);
		rule.setVoidedBy(null);
		rule.setDateVoided(null);

		return vaccinationScheduleDao.saveScheduleRule(rule);
	}

	@Override
	public PatientVaccinationSchedule assignScheduleToPatient(Patient patient, VaccinationSchedule schedule) {
		if (patient == null) {
			throw new APIException("Patient cannot be null");
		}

		if (schedule == null) {
			throw new APIException("VaccinationSchedule cannot be null");
		}

		PatientVaccinationSchedule patientSchedule = new PatientVaccinationSchedule(patient, schedule,
				Context.getAuthenticatedUser());

		return vaccinationScheduleDao.savePatientSchedule(patientSchedule);
	}

	@Override
	public PatientVaccinationSchedule getPatientSchedule(Patient patient) {
		if (patient == null) {
			return null;
		}

		List<PatientVaccinationSchedule> schedules = vaccinationScheduleDao.getPatientSchedulesByPatient(patient);
		return schedules.isEmpty() ? null : schedules.get(0);
	}

	@Override
	public PatientVaccinationSchedule getPatientScheduleByUuid(String uuid) {
		if (uuid == null || uuid.trim().isEmpty()) {
			return null;
		}
		return vaccinationScheduleDao.getPatientScheduleByUuid(uuid);
	}

	@Override
	public List<PatientVaccinationSchedule> getPatientSchedulesBySchedule(VaccinationSchedule schedule) {
		if (schedule == null) {
			return new ArrayList<>();
		}
		return vaccinationScheduleDao.getPatientSchedulesBySchedule(schedule);
	}

	@Override
	public PatientVaccinationSchedule updatePatientScheduleStatus(PatientVaccinationSchedule patientSchedule,
			PatientVaccinationSchedule.Status status) {
		if (patientSchedule == null) {
			throw new APIException("PatientVaccinationSchedule cannot be null");
		}

		if (status == null) {
			throw new APIException("Status cannot be null");
		}

		patientSchedule.setStatus(status);
		return vaccinationScheduleDao.savePatientSchedule(patientSchedule);
	}

	@Override
	public PatientVaccinationSchedule voidPatientSchedule(PatientVaccinationSchedule patientSchedule, String reason) {
		if (patientSchedule == null) {
			throw new APIException("PatientVaccinationSchedule cannot be null");
		}

		patientSchedule.setVoided(true);
		patientSchedule.setVoidReason(reason);
		patientSchedule.setVoidedBy(Context.getAuthenticatedUser());
		patientSchedule.setDateVoided(new Date());

		return vaccinationScheduleDao.savePatientSchedule(patientSchedule);
	}

	@Override
	public PatientVaccinationSchedule unvoidPatientSchedule(PatientVaccinationSchedule patientSchedule) {
		if (patientSchedule == null) {
			throw new APIException("PatientVaccinationSchedule cannot be null");
		}

		patientSchedule.setVoided(false);
		patientSchedule.setVoidReason(null);
		patientSchedule.setVoidedBy(null);
		patientSchedule.setDateVoided(null);

		return vaccinationScheduleDao.savePatientSchedule(patientSchedule);
	}

	@Override
	public List<PatientVaccinationStatus> calculateVaccinationStatuses(Patient patient) {
		PatientVaccinationSchedule patientSchedule = getPatientSchedule(patient);
		if (patientSchedule == null) {
			return new ArrayList<>();
		}

		List<PatientVaccinationStatus> statuses = new ArrayList<>();
		List<VaccinationScheduleEntry> entries = patientSchedule.getVaccinationSchedule().getActiveEntries();

		for (VaccinationScheduleEntry entry : entries) {
			PatientVaccinationStatus status = new PatientVaccinationStatus(patient, entry);

			if (isVaccineAdministered(patient, entry.getVaccineConcept(), entry.getDoseNumber())) {
				List<Obs> vaccineObs = getAdministeredVaccinesForConcept(patient, entry.getVaccineConcept());
				if (!vaccineObs.isEmpty()) {
					status.setImmunizationObs(vaccineObs.get(0));
				}
			}

			statuses.add(status);
		}

		return statuses;
	}

	@Override
	public List<PatientVaccinationStatus> getDueVaccinations(Patient patient, Date asOfDate) {
		return calculateVaccinationStatuses(patient).stream()
				.filter(status -> status.getStatus() == PatientVaccinationStatus.Status.DUE)
				.collect(Collectors.toList());
	}

	@Override
	public List<PatientVaccinationStatus> getOverdueVaccinations(Patient patient, Date asOfDate) {
		return calculateVaccinationStatuses(patient).stream()
				.filter(status -> status.getStatus() == PatientVaccinationStatus.Status.OVERDUE)
				.collect(Collectors.toList());
	}

	@Override
	public List<PatientVaccinationStatus> getUpcomingVaccinations(Patient patient, int daysAhead) {
		Date futureDate = new Date(System.currentTimeMillis() + (daysAhead * 24 * 60 * 60 * 1000L));
		return calculateVaccinationStatuses(patient).stream()
				.filter(status -> status.getStatus() == PatientVaccinationStatus.Status.PENDING)
				.filter(status -> status.getDueDate() != null && status.getDueDate().before(futureDate))
				.collect(Collectors.toList());
	}

	@Override
	public List<PatientVaccinationStatus> getVaccinationCalendar(Patient patient, Date startDate, Date endDate) {
		return calculateVaccinationStatuses(patient).stream().filter(status -> status.getDueDate() != null)
				.filter(status -> !status.getDueDate().before(startDate) && !status.getDueDate().after(endDate))
				.collect(Collectors.toList());
	}

	@Override
	public void recordVaccineAdministration(Patient patient, VaccinationScheduleEntry entry, Encounter encounter,
			Obs immunizationObs) {
		if (patient == null) {
			throw new APIException("Patient cannot be null");
		}

		if (entry == null) {
			throw new APIException("VaccinationScheduleEntry cannot be null");
		}

		if (encounter == null) {
			throw new APIException("Encounter cannot be null");
		}

		if (immunizationObs == null) {
			throw new APIException("Immunization observation cannot be null");
		}

		// Set up the observation for the vaccine administration
		immunizationObs.setPerson(patient);
		immunizationObs.setEncounter(encounter);
		immunizationObs.setConcept(entry.getVaccineConcept());
		immunizationObs.setObsDatetime(encounter.getEncounterDatetime());

		// Save the observation
		Context.getObsService().saveObs(immunizationObs, null);
	}

	@Override
	public boolean isVaccineAdministered(Patient patient, Concept vaccineConcept, Integer doseNumber) {
		List<Obs> vaccineObs = getAdministeredVaccinesForConcept(patient, vaccineConcept);
		return vaccineObs.size() >= doseNumber;
	}

	@Override
	public List<Obs> getAdministeredVaccines(Patient patient) {
		if (patient == null) {
			return new ArrayList<>();
		}

		// Get all immunization observations for the patient
		List<Obs> immunizationObs = Context.getObsService().getObservationsByPerson(patient);

		// Filter for vaccine-related observations
		return immunizationObs.stream().filter(obs -> obs.getConcept() != null && isVaccineConcept(obs.getConcept()))
				.collect(Collectors.toList());
	}

	@Override
	public List<Obs> getAdministeredVaccinesForConcept(Patient patient, Concept vaccineConcept) {
		if (patient == null || vaccineConcept == null) {
			return new ArrayList<>();
		}

		// Get observations for the specific vaccine concept
		List<Obs> vaccineObs = Context.getObsService().getObservationsByPersonAndConcept(patient, vaccineConcept);

		return vaccineObs.stream().filter(obs -> !obs.getVoided()).collect(Collectors.toList());
	}

	@Override
	public List<VaccinationScheduleRule> evaluateRulesForPatient(Patient patient, VaccinationScheduleEntry entry) {
		if (patient == null || entry == null) {
			return new ArrayList<>();
		}

		List<VaccinationScheduleRule> rules = getRulesBySchedule(entry.getVaccinationSchedule());

		return rules.stream().filter(rule -> rule.isApplicable(entry)).collect(Collectors.toList());
	}

	@Override
	public boolean hasContraindications(Patient patient, VaccinationScheduleEntry entry) {
		return !getContraindications(patient, entry).isEmpty();
	}

	@Override
	public List<VaccinationScheduleRule> getContraindications(Patient patient, VaccinationScheduleEntry entry) {
		return evaluateRulesForPatient(patient, entry).stream()
				.filter(rule -> rule.getRuleType() == VaccinationScheduleRule.RuleType.CONTRAINDICATION)
				.filter(rule -> rule.evaluateRule(patient, new Date())).collect(Collectors.toList());
	}

	@Override
	public PatientVaccinationStatus markVaccineAsContraindicated(Patient patient, VaccinationScheduleEntry entry,
			String reason) {
		PatientVaccinationStatus status = new PatientVaccinationStatus(patient, entry);
		status.setContraindicationReason(reason);
		return status;
	}

	@Override
	public PatientVaccinationStatus markVaccineAsSkipped(Patient patient, VaccinationScheduleEntry entry,
			String reason) {
		PatientVaccinationStatus status = new PatientVaccinationStatus(patient, entry);
		status.setStatus(PatientVaccinationStatus.Status.SKIPPED);
		return status;
	}

	@Override
	public List<PatientVaccinationStatus> getVaccinationHistory(Patient patient) {
		return calculateVaccinationStatuses(patient).stream()
				.filter(status -> status.getStatus() == PatientVaccinationStatus.Status.APPLIED)
				.collect(Collectors.toList());
	}

	@Override
	public boolean validateDoseSequence(Patient patient, VaccinationScheduleEntry entry) {
		if (entry.getDoseNumber() <= 1) {
			return true;
		}

		return isVaccineAdministered(patient, entry.getVaccineConcept(), entry.getDoseNumber() - 1);
	}

	@Override
	public List<VaccinationScheduleEntry> getNextRecommendedVaccinations(Patient patient, int maxResults) {
		return getDueVaccinations(patient, new Date()).stream().limit(maxResults)
				.map(PatientVaccinationStatus::getScheduleEntry).collect(Collectors.toList());
	}

	private boolean isVaccineConcept(Concept concept) {
		// Check if the concept is a vaccine by looking at its concept class
		// This is a simplified implementation - in a real system, you'd have
		// a more sophisticated way to identify vaccine concepts
		if (concept.getConceptClass() != null) {
			String conceptClassName = concept.getConceptClass().getName();
			return conceptClassName != null && (conceptClassName.toLowerCase().contains("vaccine")
					|| conceptClassName.toLowerCase().contains("immunization"));
		}

		// Alternative: check if concept name contains vaccine-related keywords
		if (concept.getName() != null) {
			String conceptName = concept.getName().getName().toLowerCase();
			return conceptName.contains("vaccine") || conceptName.contains("immunization")
					|| conceptName.contains("vaccination");
		}

		return false;
	}

	// CRITICAL: Vaccination Administration service methods

	@Override
	public VaccinationAdministration saveVaccinationAdministration(VaccinationAdministration administration) {
		if (administration == null) {
			throw new APIException("VaccinationAdministration cannot be null");
		}

		// Validate required fields
		if (administration.getPatient() == null) {
			throw new APIException("Patient is required for VaccinationAdministration");
		}
		if (administration.getVaccinationScheduleEntry() == null) {
			throw new APIException("VaccinationScheduleEntry is required for VaccinationAdministration");
		}
		if (administration.getEncounter() == null) {
			throw new APIException("Encounter is required for VaccinationAdministration");
		}
		if (administration.getAdministrationDate() == null) {
			throw new APIException("Administration date is required for VaccinationAdministration");
		}

		// Set default status if not provided
		if (administration.getStatus() == null) {
			administration.setStatus(VaccinationAdministration.Status.COMPLETED);
		}

		// FHIR/OpenMRS Integration: Create or update obs for FHIR module compatibility
		Obs immunizationObs = administration.getObs();
		if (immunizationObs == null && administration.shouldCreateObs()) {
			immunizationObs = createImmunizationObs(administration);
		} else if (immunizationObs != null && administration.shouldCreateObs()) {
			updateImmunizationObs(immunizationObs, administration);
		}

		// Save obs first if needed
		if (immunizationObs != null) {
			immunizationObs = Context.getObsService().saveObs(immunizationObs,
					"Vaccination administration via vaccination schedule module");
			administration.setObs(immunizationObs);
		}

		return vaccinationScheduleDao.saveVaccinationAdministration(administration);
	}

	/**
	 * FHIR Integration: Create obs for immunization compatible with OpenMRS FHIR
	 * module
	 */
	private Obs createImmunizationObs(VaccinationAdministration administration) {
		Obs obs = new Obs();
		obs.setPerson(administration.getPatient());
		obs.setConcept(administration.getVaccineConcept());
		obs.setEncounter(administration.getEncounter());
		obs.setLocation(administration.getLocation());
		obs.setObsDatetime(administration.getAdministrationDate());
		obs.setValueText(administration.getObsValue());
		obs.setComment("Created by vaccination schedule module - FHIR compatible");

		// Add metadata as obs group members for rich data
		addObsGroupMembers(obs, administration);

		return obs;
	}

	/**
	 * Update existing obs with vaccination administration data
	 */
	private void updateImmunizationObs(Obs obs, VaccinationAdministration administration) {
		obs.setObsDatetime(administration.getAdministrationDate());
		obs.setValueText(administration.getObsValue());
		obs.setLocation(administration.getLocation());
		obs.setComment("Updated by vaccination schedule module - FHIR compatible");

		// Clear and re-add group members
		obs.getGroupMembers().clear();
		addObsGroupMembers(obs, administration);
	}

	/**
	 * Add rich metadata as obs group members
	 */
	private void addObsGroupMembers(Obs parentObs, VaccinationAdministration administration) {
		// Lot number
		if (administration.getLotNumber() != null) {
			Obs lotObs = createTextObs(parentObs, "VACCINE_LOT_NUMBER", administration.getLotNumber());
			if (lotObs != null)
				parentObs.addGroupMember(lotObs);
		}

		// Manufacturer
		if (administration.getManufacturer() != null) {
			Obs mfrObs = createTextObs(parentObs, "VACCINE_MANUFACTURER", administration.getManufacturer());
			if (mfrObs != null)
				parentObs.addGroupMember(mfrObs);
		}

		// Dose number
		if (administration.getDoseNumber() != null) {
			Obs doseObs = createNumericObs(parentObs, "VACCINE_DOSE_NUMBER",
					administration.getDoseNumber().doubleValue());
			if (doseObs != null)
				parentObs.addGroupMember(doseObs);
		}

		// Site administered
		if (administration.getSiteAdministered() != null) {
			Obs siteObs = createTextObs(parentObs, "VACCINE_SITE", administration.getSiteAdministered());
			if (siteObs != null)
				parentObs.addGroupMember(siteObs);
		}

		// Route administered
		if (administration.getRouteAdministered() != null) {
			Obs routeObs = createTextObs(parentObs, "VACCINE_ROUTE", administration.getRouteAdministered());
			if (routeObs != null)
				parentObs.addGroupMember(routeObs);
		}

		// Adverse reaction
		if (administration.hasAdverseReaction()) {
			Obs reactionObs = createTextObs(parentObs, "ADVERSE_REACTION", administration.getAdverseReaction());
			if (reactionObs != null)
				parentObs.addGroupMember(reactionObs);
		}
	}

	/**
	 * Helper to create text obs for metadata
	 */
	private Obs createTextObs(Obs parentObs, String conceptName, String value) {
		try {
			Concept concept = Context.getConceptService().getConceptByName(conceptName);
			if (concept == null) {
				// Try to find by mapping or create a simple text concept
				concept = getOrCreateTextConcept(conceptName);
			}

			if (concept != null) {
				Obs obs = new Obs();
				obs.setPerson(parentObs.getPerson());
				obs.setConcept(concept);
				obs.setEncounter(parentObs.getEncounter());
				obs.setLocation(parentObs.getLocation());
				obs.setObsDatetime(parentObs.getObsDatetime());
				obs.setValueText(value);
				return obs;
			}
		} catch (Exception e) {
			// Log but don't fail - metadata is optional
			log.warn("Could not create obs for concept: " + conceptName, e);
		}
		return null;
	}

	/**
	 * Helper to create numeric obs for metadata
	 */
	private Obs createNumericObs(Obs parentObs, String conceptName, Double value) {
		try {
			Concept concept = Context.getConceptService().getConceptByName(conceptName);
			if (concept == null) {
				concept = getOrCreateNumericConcept(conceptName);
			}

			if (concept != null) {
				Obs obs = new Obs();
				obs.setPerson(parentObs.getPerson());
				obs.setConcept(concept);
				obs.setEncounter(parentObs.getEncounter());
				obs.setLocation(parentObs.getLocation());
				obs.setObsDatetime(parentObs.getObsDatetime());
				obs.setValueNumeric(value);
				return obs;
			}
		} catch (Exception e) {
			// Log but don't fail - metadata is optional
			log.warn("Could not create obs for concept: " + conceptName, e);
		}
		return null;
	}

	/**
	 * Get or create text concept for metadata
	 */
	private Concept getOrCreateTextConcept(String name) {
		// In production, these concepts should be pre-created
		// This is a fallback for development/testing
		return null; // Return null to avoid creating concepts dynamically
	}

	/**
	 * Get or create numeric concept for metadata
	 */
	private Concept getOrCreateNumericConcept(String name) {
		// In production, these concepts should be pre-created
		// This is a fallback for development/testing
		return null; // Return null to avoid creating concepts dynamically
	}

	@Override
	public VaccinationAdministration getVaccinationAdministration(Integer administrationId) {
		return vaccinationScheduleDao.getVaccinationAdministration(administrationId);
	}

	@Override
	public VaccinationAdministration getVaccinationAdministrationByUuid(String uuid) {
		return vaccinationScheduleDao.getVaccinationAdministrationByUuid(uuid);
	}

	@Override
	public void deleteVaccinationAdministration(VaccinationAdministration administration) {
		if (administration != null) {
			vaccinationScheduleDao.deleteVaccinationAdministration(administration);
		}
	}

	@Override
	public List<VaccinationAdministration> getVaccinationAdministrationsByPatient(Patient patient) {
		if (patient == null) {
			return new ArrayList<>();
		}
		return vaccinationScheduleDao.getAdministrationsByPatient(patient);
	}

	@Override
	public List<VaccinationAdministration> getVaccinationAdministrationsByPatient(Patient patient, Date startDate,
			Date endDate) {
		if (patient == null) {
			return new ArrayList<>();
		}
		return vaccinationScheduleDao.getAdministrationsByPatient(patient, startDate, endDate);
	}

	@Override
	public List<VaccinationAdministration> getVaccinationAdministrationsByEncounter(Encounter encounter) {
		if (encounter == null) {
			return new ArrayList<>();
		}
		return vaccinationScheduleDao.getAdministrationsByEncounter(encounter);
	}

	@Override
	public List<VaccinationAdministration> getVaccinationAdministrationsByLocation(Location location, Date startDate,
			Date endDate) {
		if (location == null) {
			return new ArrayList<>();
		}
		return vaccinationScheduleDao.getAdministrationsByLocation(location, startDate, endDate);
	}

	@Override
	public List<VaccinationAdministration> getVaccinationAdministrationsByScheduleEntry(
			VaccinationScheduleEntry entry) {
		if (entry == null) {
			return new ArrayList<>();
		}
		return vaccinationScheduleDao.getAdministrationsByScheduleEntry(entry);
	}

	@Override
	public List<VaccinationAdministration> getVaccinationAdministrationsByPatientAndVaccine(Patient patient,
			Concept vaccineConcept) {
		if (patient == null || vaccineConcept == null) {
			return new ArrayList<>();
		}
		return vaccinationScheduleDao.getAdministrationsByPatientAndVaccine(patient, vaccineConcept);
	}

	@Override
	public VaccinationAdministration getVaccinationAdministrationByPatientEntryAndDose(Patient patient,
			VaccinationScheduleEntry entry, Integer doseNumber) {
		if (patient == null || entry == null || doseNumber == null) {
			return null;
		}
		return vaccinationScheduleDao.getAdministrationByPatientEntryAndDose(patient, entry, doseNumber);
	}
}
