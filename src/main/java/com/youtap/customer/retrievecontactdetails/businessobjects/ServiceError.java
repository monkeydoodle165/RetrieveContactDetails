package com.youtap.customer.retrievecontactdetails.businessobjects;

import java.util.HashMap;

public class ServiceError{
	public static enum Errors {
		NO_PARAMETERS_ENTERED_ERROR,
		MULTI_PARAMTERS_ENTERED_ERROR,
		INVALID_ID_ERROR, 
		JSON_CONVERSION_ERROR,
		NO_RESULTS_FOUND_ERROR,
		ERROR_PROCESSING_EXCEPTION
	}
	
	public static HashMap<Errors, String> errorMessages = new HashMap<Errors, String> () {
		private static final long serialVersionUID = -2232238612227367397L;
	{
		put(Errors.NO_PARAMETERS_ENTERED_ERROR, 	"Please submit an id or username in order to search");
		put(Errors.MULTI_PARAMTERS_ENTERED_ERROR,	"Please submit an id or username, not both");
		put(Errors.INVALID_ID_ERROR, 				"Please enter a valid id");
		put(Errors.JSON_CONVERSION_ERROR, 			"Error ocurred while converting the output to JSON");
		put(Errors.NO_RESULTS_FOUND_ERROR, 			"No results found for given inputs");
		put(Errors.ERROR_PROCESSING_EXCEPTION, 		"Error processing exception");
	}};
	
	private String error;
	
	public ServiceError(String error) {
		this.error = error;
	}
	
	public String getError() {
		return error;
	}
	
	public void setError(String error) {
		this.error = error;
	}
}
