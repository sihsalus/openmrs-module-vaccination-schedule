package org.openmrs.module.vaccinationschedule.validator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.vaccinationschedule.model.VaccinationSchedule;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Handler(supports = { VaccinationSchedule.class }, order = 50)
public class VaccinationScheduleValidator implements Validator {
	
	@Override
	public boolean supports(Class<?> clazz) {
		return VaccinationSchedule.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		VaccinationSchedule schedule = (VaccinationSchedule) target;
		
		if (schedule == null) {
			errors.reject("error.general");
			return;
		}
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "vaccinationschedule.name.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "countryCode", "vaccinationschedule.countryCode.required");
		ValidationUtils.rejectIfEmpty(errors, "effectiveDate", "vaccinationschedule.effectiveDate.required");
		
		if (schedule.getName() != null && schedule.getName().length() > 255) {
			errors.rejectValue("name", "vaccinationschedule.name.toolong");
		}
		
		if (schedule.getCountryCode() != null && schedule.getCountryCode().length() > 3) {
			errors.rejectValue("countryCode", "vaccinationschedule.countryCode.toolong");
		}
		
		if (schedule.getEffectiveDate() != null && schedule.getExpiryDate() != null) {
			if (schedule.getEffectiveDate().after(schedule.getExpiryDate())) {
				errors.rejectValue("expiryDate", "vaccinationschedule.expiryDate.beforeEffectiveDate");
			}
		}
		
		if (schedule.getVersion() != null && schedule.getVersion() < 1) {
			errors.rejectValue("version", "vaccinationschedule.version.invalid");
		}
		
		if (schedule.getSourceAuthority() != null && schedule.getSourceAuthority().length() > 255) {
			errors.rejectValue("sourceAuthority", "vaccinationschedule.sourceAuthority.toolong");
		}
	}
}
