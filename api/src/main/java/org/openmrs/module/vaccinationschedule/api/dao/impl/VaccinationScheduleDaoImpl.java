package org.openmrs.module.vaccinationschedule.api.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.vaccinationschedule.api.dao.VaccinationScheduleDao;
import org.openmrs.module.vaccinationschedule.model.PatientVaccinationSchedule;
import org.openmrs.module.vaccinationschedule.model.VaccinationAdministration;
import org.openmrs.module.vaccinationschedule.model.VaccinationSchedule;
import org.openmrs.module.vaccinationschedule.model.VaccinationScheduleEntry;
import org.openmrs.module.vaccinationschedule.model.VaccinationScheduleRule;

public class VaccinationScheduleDaoImpl implements VaccinationScheduleDao {
	
	private DbSessionFactory sessionFactory;
	
	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	private DbSession getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public VaccinationSchedule getVaccinationSchedule(Integer scheduleId) {
		if (scheduleId == null || scheduleId <= 0) {
			return null;
		}
		
		Object result = getSession().get(VaccinationSchedule.class, scheduleId);
		if (result instanceof VaccinationSchedule) {
			return (VaccinationSchedule) result;
		}
		return null;
	}
	
	@Override
	public VaccinationSchedule getVaccinationScheduleByUuid(String uuid) {
		if (uuid == null || uuid.trim().isEmpty()) {
			return null;
		}
		
		Object result = getSession().createCriteria(VaccinationSchedule.class).add(Restrictions.eq("uuid", uuid.trim()))
		        .uniqueResult();
		
		if (result instanceof VaccinationSchedule) {
			return (VaccinationSchedule) result;
		}
		return null;
	}
	
	@Override
	public VaccinationSchedule saveVaccinationSchedule(VaccinationSchedule schedule) {
		if (schedule == null) {
			throw new IllegalArgumentException("VaccinationSchedule cannot be null");
		}
		getSession().saveOrUpdate(schedule);
		return schedule;
	}
	
	@Override
	public void deleteVaccinationSchedule(VaccinationSchedule schedule) {
		getSession().delete(schedule);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<VaccinationSchedule> getAllSchedules(boolean includeRetired) {
		Criteria criteria = getSession().createCriteria(VaccinationSchedule.class);
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		}
		return criteria.list();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<VaccinationSchedule> getSchedulesByCountry(String countryCode, boolean includeRetired) {
		Criteria criteria = getSession().createCriteria(VaccinationSchedule.class)
		        .add(Restrictions.eq("countryCode", countryCode));
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		}
		return criteria.list();
	}
	
	@Override
	public VaccinationSchedule getActiveScheduleByCountry(String countryCode) {
		return (VaccinationSchedule) getSession().createCriteria(VaccinationSchedule.class)
		        .add(Restrictions.eq("countryCode", countryCode)).add(Restrictions.eq("retired", false)).uniqueResult();
	}
	
	@Override
	public VaccinationScheduleEntry getScheduleEntry(Integer entryId) {
		return (VaccinationScheduleEntry) getSession().get(VaccinationScheduleEntry.class, entryId);
	}
	
	@Override
	public VaccinationScheduleEntry getScheduleEntryByUuid(String uuid) {
		return (VaccinationScheduleEntry) getSession().createCriteria(VaccinationScheduleEntry.class)
		        .add(Restrictions.eq("uuid", uuid)).uniqueResult();
	}
	
	@Override
	public VaccinationScheduleEntry saveScheduleEntry(VaccinationScheduleEntry entry) {
		getSession().saveOrUpdate(entry);
		return entry;
	}
	
	@Override
	public void deleteScheduleEntry(VaccinationScheduleEntry entry) {
		getSession().delete(entry);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<VaccinationScheduleEntry> getEntriesBySchedule(VaccinationSchedule schedule) {
		if (schedule == null) {
			return new ArrayList<>();
		}
		return getSession().createCriteria(VaccinationScheduleEntry.class)
		        .add(Restrictions.eq("vaccinationSchedule.scheduleId", schedule.getScheduleId()))
		        .add(Restrictions.eq("voided", false)).list();
	}
	
	// Schedule Rule methods
	
	@Override
	public VaccinationScheduleRule getScheduleRule(Integer ruleId) {
		if (ruleId == null) {
			return null;
		}
		return (VaccinationScheduleRule) getSession().get(VaccinationScheduleRule.class, ruleId);
	}
	
	@Override
	public VaccinationScheduleRule getScheduleRuleByUuid(String uuid) {
		if (uuid == null || uuid.trim().isEmpty()) {
			return null;
		}
		return (VaccinationScheduleRule) getSession().createCriteria(VaccinationScheduleRule.class)
		        .add(Restrictions.eq("uuid", uuid)).uniqueResult();
	}
	
	@Override
	public VaccinationScheduleRule saveScheduleRule(VaccinationScheduleRule rule) {
		if (rule == null) {
			throw new IllegalArgumentException("VaccinationScheduleRule cannot be null");
		}
		getSession().saveOrUpdate(rule);
		return rule;
	}
	
	@Override
	public void deleteScheduleRule(VaccinationScheduleRule rule) {
		if (rule != null) {
			getSession().delete(rule);
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<VaccinationScheduleRule> getRulesBySchedule(VaccinationSchedule schedule) {
		if (schedule == null) {
			return new ArrayList<>();
		}
		return getSession().createCriteria(VaccinationScheduleRule.class)
		        .add(Restrictions.eq("vaccinationSchedule.scheduleId", schedule.getScheduleId()))
		        .add(Restrictions.eq("voided", false)).list();
	}
	
	// Patient Schedule methods
	
	@Override
	public PatientVaccinationSchedule getPatientSchedule(Integer id) {
		if (id == null) {
			return null;
		}
		return (PatientVaccinationSchedule) getSession().get(PatientVaccinationSchedule.class, id);
	}
	
	@Override
	public PatientVaccinationSchedule getPatientScheduleByUuid(String uuid) {
		if (uuid == null || uuid.trim().isEmpty()) {
			return null;
		}
		return (PatientVaccinationSchedule) getSession().createCriteria(PatientVaccinationSchedule.class)
		        .add(Restrictions.eq("uuid", uuid)).uniqueResult();
	}
	
	@Override
	public PatientVaccinationSchedule savePatientSchedule(PatientVaccinationSchedule patientSchedule) {
		if (patientSchedule == null) {
			throw new IllegalArgumentException("PatientVaccinationSchedule cannot be null");
		}
		getSession().saveOrUpdate(patientSchedule);
		return patientSchedule;
	}
	
	@Override
	public void deletePatientSchedule(PatientVaccinationSchedule patientSchedule) {
		if (patientSchedule != null) {
			getSession().delete(patientSchedule);
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<PatientVaccinationSchedule> getPatientSchedulesByPatient(Patient patient) {
		if (patient == null) {
			return new ArrayList<>();
		}
		return getSession().createCriteria(PatientVaccinationSchedule.class)
		        .add(Restrictions.eq("patient.patientId", patient.getPatientId())).add(Restrictions.eq("voided", false))
		        .list();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<PatientVaccinationSchedule> getPatientSchedulesBySchedule(VaccinationSchedule schedule) {
		if (schedule == null) {
			return new ArrayList<>();
		}
		return getSession().createCriteria(PatientVaccinationSchedule.class)
		        .add(Restrictions.eq("vaccinationSchedule.scheduleId", schedule.getScheduleId()))
		        .add(Restrictions.eq("voided", false)).list();
	}
	
	// CRITICAL: Vaccination Administration DAO methods
	
	@Override
	public VaccinationAdministration getVaccinationAdministration(Integer administrationId) {
		if (administrationId == null || administrationId <= 0) {
			return null;
		}
		
		Object result = getSession().get(VaccinationAdministration.class, administrationId);
		if (result instanceof VaccinationAdministration) {
			return (VaccinationAdministration) result;
		}
		return null;
	}
	
	@Override
	public VaccinationAdministration getVaccinationAdministrationByUuid(String uuid) {
		if (uuid == null || uuid.trim().isEmpty()) {
			return null;
		}
		
		Object result = getSession().createCriteria(VaccinationAdministration.class)
		        .add(Restrictions.eq("uuid", uuid.trim())).uniqueResult();
		
		if (result instanceof VaccinationAdministration) {
			return (VaccinationAdministration) result;
		}
		return null;
	}
	
	@Override
	public VaccinationAdministration saveVaccinationAdministration(VaccinationAdministration administration) {
		if (administration == null) {
			throw new IllegalArgumentException("VaccinationAdministration cannot be null");
		}
		
		getSession().saveOrUpdate(administration);
		return administration;
	}
	
	@Override
	public void deleteVaccinationAdministration(VaccinationAdministration administration) {
		if (administration != null) {
			getSession().delete(administration);
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<VaccinationAdministration> getAdministrationsByPatient(Patient patient) {
		if (patient == null) {
			return new ArrayList<>();
		}
		
		return getSession().createCriteria(VaccinationAdministration.class)
		        .add(Restrictions.eq("patient.patientId", patient.getPatientId())).add(Restrictions.eq("voided", false))
		        .addOrder(Order.desc("administrationDate")).list();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<VaccinationAdministration> getAdministrationsByPatient(Patient patient, Date startDate, Date endDate) {
		if (patient == null) {
			return new ArrayList<>();
		}
		
		Criteria criteria = getSession().createCriteria(VaccinationAdministration.class)
		        .add(Restrictions.eq("patient.patientId", patient.getPatientId())).add(Restrictions.eq("voided", false));
		
		if (startDate != null) {
			criteria.add(Restrictions.ge("administrationDate", startDate));
		}
		if (endDate != null) {
			criteria.add(Restrictions.le("administrationDate", endDate));
		}
		
		return criteria.addOrder(Order.desc("administrationDate")).list();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<VaccinationAdministration> getAdministrationsByEncounter(Encounter encounter) {
		if (encounter == null) {
			return new ArrayList<>();
		}
		
		return getSession().createCriteria(VaccinationAdministration.class)
		        .add(Restrictions.eq("encounter.encounterId", encounter.getEncounterId()))
		        .add(Restrictions.eq("voided", false)).addOrder(Order.asc("administrationDate")).list();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<VaccinationAdministration> getAdministrationsByLocation(Location location, Date startDate, Date endDate) {
		if (location == null) {
			return new ArrayList<>();
		}
		
		Criteria criteria = getSession().createCriteria(VaccinationAdministration.class)
		        .add(Restrictions.eq("location.locationId", location.getLocationId())).add(Restrictions.eq("voided", false));
		
		if (startDate != null) {
			criteria.add(Restrictions.ge("administrationDate", startDate));
		}
		if (endDate != null) {
			criteria.add(Restrictions.le("administrationDate", endDate));
		}
		
		return criteria.addOrder(Order.desc("administrationDate")).list();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<VaccinationAdministration> getAdministrationsByScheduleEntry(VaccinationScheduleEntry entry) {
		if (entry == null) {
			return new ArrayList<>();
		}
		
		return getSession().createCriteria(VaccinationAdministration.class)
		        .add(Restrictions.eq("vaccinationScheduleEntry.entryId", entry.getEntryId()))
		        .add(Restrictions.eq("voided", false)).addOrder(Order.desc("administrationDate")).list();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<VaccinationAdministration> getAdministrationsByPatientAndVaccine(Patient patient,
	        org.openmrs.Concept vaccineConcept) {
		if (patient == null || vaccineConcept == null) {
			return new ArrayList<>();
		}
		
		return getSession().createCriteria(VaccinationAdministration.class).createAlias("vaccinationScheduleEntry", "entry")
		        .add(Restrictions.eq("patient.patientId", patient.getPatientId()))
		        .add(Restrictions.eq("entry.vaccineConcept.conceptId", vaccineConcept.getConceptId()))
		        .add(Restrictions.eq("voided", false)).addOrder(Order.asc("entry.doseNumber"))
		        .addOrder(Order.desc("administrationDate")).list();
	}
	
	@Override
	public VaccinationAdministration getAdministrationByPatientEntryAndDose(Patient patient, VaccinationScheduleEntry entry,
	        Integer doseNumber) {
		if (patient == null || entry == null || doseNumber == null) {
			return null;
		}
		
		Object result = getSession().createCriteria(VaccinationAdministration.class)
		        .createAlias("vaccinationScheduleEntry", "entry")
		        .add(Restrictions.eq("patient.patientId", patient.getPatientId()))
		        .add(Restrictions.eq("entry.entryId", entry.getEntryId()))
		        .add(Restrictions.eq("entry.doseNumber", doseNumber)).add(Restrictions.eq("voided", false))
		        .addOrder(Order.desc("administrationDate")).setMaxResults(1).uniqueResult();
		
		if (result instanceof VaccinationAdministration) {
			return (VaccinationAdministration) result;
		}
		return null;
	}
}
