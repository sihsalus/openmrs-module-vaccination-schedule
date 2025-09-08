package org.openmrs.module.vaccinationschedule.web.rest;

import java.util.List;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.vaccinationschedule.api.VaccinationScheduleService;
import org.openmrs.module.vaccinationschedule.model.VaccinationAdministration;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * CRITICAL REST RESOURCE: Manages vaccination administration records Links vaccination schedules
 * with actual clinical data (obs, encounters, etc.) This is the bridge between vaccination
 * schedules and real clinical practice.
 */
@Resource(name = RestConstants.VERSION_1
        + "/vaccinationadministration", supportedClass = VaccinationAdministration.class, supportedOpenmrsVersions = {
                "2.6.*", "2.7.*" })
public class VaccinationAdministrationResource extends DelegatingCrudResource<VaccinationAdministration> {
	
	private VaccinationScheduleService getVaccinationScheduleService() {
		return Context.getService(VaccinationScheduleService.class);
	}
	
	@Override
	public VaccinationAdministration getByUniqueId(String uniqueId) {
		return getVaccinationScheduleService().getVaccinationAdministrationByUuid(uniqueId);
	}
	
	@Override
	protected void delete(VaccinationAdministration administration, String reason, RequestContext context)
	        throws ResponseException {
		if (administration == null) {
			return;
		}
		
		administration.setVoided(true);
		administration.setVoidReason(reason);
		administration.setVoidedBy(Context.getAuthenticatedUser());
		administration.setDateVoided(new java.util.Date());
		
		getVaccinationScheduleService().saveVaccinationAdministration(administration);
	}
	
	@Override
	public VaccinationAdministration newDelegate() {
		return new VaccinationAdministration();
	}
	
	@Override
	public VaccinationAdministration save(VaccinationAdministration administration) {
		return getVaccinationScheduleService().saveVaccinationAdministration(administration);
	}
	
	@Override
	public void purge(VaccinationAdministration administration, RequestContext context) throws ResponseException {
		if (administration == null) {
			return;
		}
		getVaccinationScheduleService().deleteVaccinationAdministration(administration);
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof RefRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("administrationDate");
			description.addProperty("status");
			description.addSelfLink();
			return description;
		} else if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("patient", Representation.REF);
			description.addProperty("vaccinationScheduleEntry", Representation.REF);
			description.addProperty("obs", Representation.REF);
			description.addProperty("encounter", Representation.REF);
			description.addProperty("location", Representation.REF);
			description.addProperty("provider", Representation.REF);
			description.addProperty("administrationDate");
			description.addProperty("administeredBy", Representation.REF);
			description.addProperty("doseAdministered");
			description.addProperty("doseUnits");
			description.addProperty("lotNumber");
			description.addProperty("manufacturer");
			description.addProperty("status");
			description.addProperty("voided");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("patient", Representation.DEFAULT);
			description.addProperty("vaccinationScheduleEntry", Representation.DEFAULT);
			description.addProperty("obs", Representation.DEFAULT);
			description.addProperty("encounter", Representation.DEFAULT);
			description.addProperty("location", Representation.DEFAULT);
			description.addProperty("provider", Representation.DEFAULT);
			description.addProperty("visit", Representation.REF);
			description.addProperty("administrationDate");
			description.addProperty("administeredBy", Representation.DEFAULT);
			description.addProperty("doseAdministered");
			description.addProperty("doseUnits");
			description.addProperty("lotNumber");
			description.addProperty("expiryDate");
			description.addProperty("manufacturer");
			description.addProperty("siteAdministered");
			description.addProperty("routeAdministered");
			description.addProperty("adverseReaction");
			description.addProperty("notes");
			description.addProperty("status");
			description.addProperty("voided");
			description.addProperty("auditInfo");
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("patient");
		description.addRequiredProperty("vaccinationScheduleEntry");
		description.addRequiredProperty("obs");
		description.addRequiredProperty("encounter");
		description.addRequiredProperty("administrationDate");
		description.addRequiredProperty("administeredBy");
		
		description.addProperty("location");
		description.addProperty("provider");
		description.addProperty("visit");
		description.addProperty("doseAdministered");
		description.addProperty("doseUnits");
		description.addProperty("lotNumber");
		description.addProperty("expiryDate");
		description.addProperty("manufacturer");
		description.addProperty("siteAdministered");
		description.addProperty("routeAdministered");
		description.addProperty("adverseReaction");
		description.addProperty("notes");
		description.addProperty("status");
		
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() {
		return getCreatableProperties();
	}
	
	@PropertyGetter("display")
	public String getDisplayString(VaccinationAdministration administration) {
		if (administration == null) {
			return "";
		}
		
		String patientName = administration.getPatient() != null ? administration.getPatient().getPersonName().getFullName()
		        : "Unknown Patient";
		String vaccineName = administration.getVaccineConcept() != null
		        ? administration.getVaccineConcept().getName().getName()
		        : "Unknown Vaccine";
		String doseNumber = administration.getDoseNumber() != null ? " (Dose " + administration.getDoseNumber() + ")" : "";
		
		return String.format("%s - %s%s - %s", patientName, vaccineName, doseNumber, administration.getStatus());
	}
	
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		String patientUuid = context.getParameter("patient");
		String encounterUuid = context.getParameter("encounter");
		String locationUuid = context.getParameter("location");
		
		List<VaccinationAdministration> administrations;
		
		if (patientUuid != null) {
			Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
			if (patient == null) {
				return new NeedsPaging<VaccinationAdministration>(java.util.Collections.emptyList(), context);
			}
			administrations = getVaccinationScheduleService().getVaccinationAdministrationsByPatient(patient);
		} else if (encounterUuid != null) {
			org.openmrs.Encounter encounter = Context.getEncounterService().getEncounterByUuid(encounterUuid);
			if (encounter == null) {
				return new NeedsPaging<VaccinationAdministration>(java.util.Collections.emptyList(), context);
			}
			administrations = getVaccinationScheduleService().getVaccinationAdministrationsByEncounter(encounter);
		} else if (locationUuid != null) {
			org.openmrs.Location location = Context.getLocationService().getLocationByUuid(locationUuid);
			if (location == null) {
				return new NeedsPaging<VaccinationAdministration>(java.util.Collections.emptyList(), context);
			}
			// For location-based queries, we might need date filtering
			administrations = getVaccinationScheduleService().getVaccinationAdministrationsByLocation(location, null, null);
		} else {
			throw new ResourceDoesNotSupportOperationException(
			        "VaccinationAdministration search requires patient, encounter, or location parameter");
		}
		
		return new NeedsPaging<VaccinationAdministration>(administrations, context);
	}
	
	@Override
	public String getResourceVersion() {
		return RestConstants.VERSION_1;
	}
}
