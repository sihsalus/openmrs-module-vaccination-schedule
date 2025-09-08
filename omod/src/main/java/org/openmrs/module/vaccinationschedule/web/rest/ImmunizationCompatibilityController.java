package org.openmrs.module.vaccinationschedule.web.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.vaccinationschedule.api.VaccinationScheduleService;
import org.openmrs.module.vaccinationschedule.model.VaccinationAdministration;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * FRONTEND COMPATIBILITY CONTROLLER Provides endpoints compatible with OpenMRS
 * patient chart
 * immunizations app This controller acts as a bridge between the existing
 * frontend and our advanced
 * vaccination schedule system, maintaining backward compatibility.
 */
@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/immunization")
public class ImmunizationCompatibilityController {

    private VaccinationScheduleService getVaccinationScheduleService() {
        return Context.getService(VaccinationScheduleService.class);
    }

    /**
     * Frontend-compatible endpoint for getting patient immunizations Compatible
     * with:
     * openmrs-esm-patient-chart/packages/esm-patient-immunizations-app Expected by
     * frontend: GET
     * /ws/rest/v1/immunization?patient={uuid}&v=custom:(uuid,display,obsDatetime,value,concept:(uuid,display),person:(uuid,display),encounter:(uuid),location:(uuid,display))
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getImmunizations(@RequestParam("patient") String patientUuid,
            @RequestParam(value = "concept", required = false) String conceptUuid,
            @RequestParam(value = "v", defaultValue = "default") String representation) {

        Map<String, Object> result = new HashMap<>();

        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        if (patient == null) {
            result.put("results", new ArrayList<>());
            return result;
        }

        // Get vaccinations from our advanced system
        List<VaccinationAdministration> administrations = getVaccinationScheduleService()
                .getVaccinationAdministrationsByPatient(patient);

        // Filter by concept if specified (for specific vaccine queries)
        if (conceptUuid != null) {
            org.openmrs.Concept filterConcept = Context.getConceptService().getConceptByUuid(conceptUuid);
            if (filterConcept != null) {
                administrations = administrations.stream().filter(
                        admin -> admin.getVaccineConcept() != null
                                && admin.getVaccineConcept().getUuid().equals(conceptUuid))
                        .collect(Collectors.toList());
            }
        }

        // Convert to frontend-compatible format
        List<Map<String, Object>> frontendResults = administrations.stream().filter(admin -> admin.shouldCreateObs()) // Only
                                                                                                                      // show
                                                                                                                      // completed
                                                                                                                      // vaccinations
                .map(admin -> convertToFrontendFormat(admin, representation)).collect(Collectors.toList());

        result.put("results", frontendResults);
        return result;
    }

    /**
     * Convert VaccinationAdministration to format expected by frontend
     */
    private Map<String, Object> convertToFrontendFormat(VaccinationAdministration admin, String representation) {
        Map<String, Object> result = new HashMap<>();

        // Basic properties expected by frontend
        result.put("uuid", admin.getUuid());
        result.put("display", admin.getDisplayString());
        result.put("obsDatetime", admin.getAdministrationDate());
        result.put("value", admin.getObsValue());

        // Concept (vaccine) information
        if (admin.getVaccineConcept() != null) {
            Map<String, Object> concept = new HashMap<>();
            concept.put("uuid", admin.getVaccineConcept().getUuid());
            concept.put("display", admin.getVaccineConcept().getName().getName());
            result.put("concept", concept);
        }

        // Person (patient) information
        if (admin.getPatient() != null) {
            Map<String, Object> person = new HashMap<>();
            person.put("uuid", admin.getPatient().getUuid());
            person.put("display", admin.getPatient().getPersonName().getFullName());
            result.put("person", person);
        }

        // Encounter information
        if (admin.getEncounter() != null) {
            Map<String, Object> encounter = new HashMap<>();
            encounter.put("uuid", admin.getEncounter().getUuid());
            result.put("encounter", encounter);
        }

        // Location information
        if (admin.getLocation() != null) {
            Map<String, Object> location = new HashMap<>();
            location.put("uuid", admin.getLocation().getUuid());
            location.put("display", admin.getLocation().getName());
            result.put("location", location);
        }

        // Enhanced data from vaccination schedule (not in standard obs)
        if ("full".equals(representation) || "custom".equals(representation)) {
            Map<String, Object> vaccinationInfo = new HashMap<>();
            vaccinationInfo.put("lotNumber", admin.getLotNumber());
            vaccinationInfo.put("manufacturer", admin.getManufacturer());
            vaccinationInfo.put("expiryDate", admin.getExpiryDate());
            vaccinationInfo.put("doseNumber", admin.getDoseNumber());
            vaccinationInfo.put("siteAdministered", admin.getSiteAdministered());
            vaccinationInfo.put("routeAdministered", admin.getRouteAdministered());
            vaccinationInfo.put("status", admin.getStatus() != null ? admin.getStatus().getInternalCode() : null);
            vaccinationInfo.put("fhirStatus", admin.getStatus() != null ? admin.getStatus().getFhirCode() : null);
            vaccinationInfo.put("adverseReaction", admin.getAdverseReaction());
            vaccinationInfo.put("notes", admin.getNotes());

            result.put("vaccinationScheduleInfo", vaccinationInfo);
        }

        return result;
    }

    /**
     * FHIR Module Integration: Get immunizations in FHIR-compatible format This
     * endpoint can be used by
     * the OpenMRS FHIR module to expose immunization data
     */
    @RequestMapping(value = "/fhir", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getFhirImmunizations(@RequestParam("patient") String patientUuid) {

        Map<String, Object> bundle = new HashMap<>();

        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        if (patient == null) {
            bundle.put("entry", new ArrayList<>());
            return bundle;
        }

        List<VaccinationAdministration> administrations = getVaccinationScheduleService()
                .getVaccinationAdministrationsByPatient(patient);

        // Convert to FHIR Bundle format
        List<Map<String, Object>> fhirEntries = administrations.stream().filter(admin -> admin.shouldCreateObs())
                .map(this::convertToFhirFormat).collect(Collectors.toList());

        bundle.put("resourceType", "Bundle");
        bundle.put("type", "searchset");
        bundle.put("total", fhirEntries.size());
        bundle.put("entry", fhirEntries);

        return bundle;
    }

    /**
     * Convert to FHIR Immunization resource format
     */
    private Map<String, Object> convertToFhirFormat(VaccinationAdministration admin) {
        Map<String, Object> resource = new HashMap<>();
        resource.put("resourceType", "Immunization");
        resource.put("id", admin.getUuid());
        resource.put("status", admin.getStatus() != null ? admin.getStatus().getFhirCode() : "completed");

        // Vaccine code
        if (admin.getVaccineConcept() != null) {
            Map<String, Object> vaccineCode = new HashMap<>();
            vaccineCode.put("text", admin.getVaccineConcept().getName().getName());
            resource.put("vaccineCode", vaccineCode);
        }

        // Patient reference
        if (admin.getPatient() != null) {
            Map<String, Object> patient = new HashMap<>();
            patient.put("reference", "Patient/" + admin.getPatient().getUuid());
            patient.put("display", admin.getPatient().getPersonName().getFullName());
            resource.put("patient", patient);
        }

        // Encounter reference
        if (admin.getEncounter() != null) {
            Map<String, Object> encounter = new HashMap<>();
            encounter.put("reference", "Encounter/" + admin.getEncounter().getUuid());
            resource.put("encounter", encounter);
        }

        // Location reference
        if (admin.getLocation() != null) {
            Map<String, Object> location = new HashMap<>();
            location.put("reference", "Location/" + admin.getLocation().getUuid());
            location.put("display", admin.getLocation().getName());
            resource.put("location", location);
        }

        // Occurrence date
        resource.put("occurrenceDateTime", admin.getAdministrationDate());

        // Lot number
        if (admin.getLotNumber() != null) {
            resource.put("lotNumber", admin.getLotNumber());
        }

        // Expiration date
        if (admin.getExpiryDate() != null) {
            resource.put("expirationDate", admin.getExpiryDate());
        }

        // Manufacturer
        if (admin.getManufacturer() != null) {
            Map<String, Object> manufacturer = new HashMap<>();
            manufacturer.put("display", admin.getManufacturer());
            resource.put("manufacturer", manufacturer);
        }

        // Protocol applied (dose number)
        if (admin.getDoseNumber() != null) {
            Map<String, Object> protocolApplied = new HashMap<>();
            Map<String, Object> doseNumber = new HashMap<>();
            doseNumber.put("positiveInt", admin.getDoseNumber());
            protocolApplied.put("doseNumber", doseNumber);

            List<Map<String, Object>> protocolList = new ArrayList<>();
            protocolList.add(protocolApplied);
            resource.put("protocolApplied", protocolList);
        }

        // Wrap in FHIR entry format
        Map<String, Object> entry = new HashMap<>();
        entry.put("resource", resource);
        entry.put("fullUrl", "Immunization/" + admin.getUuid());

        return entry;
    }
}
