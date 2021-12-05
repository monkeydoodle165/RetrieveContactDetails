package com.youtap.customer.retrievecontactdetails;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youtap.customer.retrievecontactdetails.businessobjects.ContactInformation;
import com.youtap.customer.retrievecontactdetails.businessobjects.ServiceError;
import com.youtap.customer.retrievecontactdetails.businessobjects.ServiceError.Errors;
import com.youtap.customer.retrievecontactdetails.businessobjects.User;

/**
 * Microservice that retrieves a list of Users from a webservice and returns the id, email and phone number
 * for the user which matches the id or name specified as a parameter.
 * 
 * @author Jason Knight
 *
 */
@SpringBootApplication
@RestController
public class RetrieveContactDetailsApplication {

	//Urls
	private static final String USER_CONTACTS_ENDPOINT = "/getusercontacts";
	private static final String USERS_API_URL = "https://jsonplaceholder.typicode.com/users";

	public static void main(String[] args) {
		SpringApplication.run(RetrieveContactDetailsApplication.class, args);
	}
	
	@GetMapping(USER_CONTACTS_ENDPOINT)
	public String getUserContacts(@RequestParam(value = "name", required = false) String name, @RequestParam(value = "id", required = false) String id) {		
		ObjectMapper objectMapper = new ObjectMapper();
		int idInt = 0;
		
		try {
			if(inputsEmpty(name, id)) {
				throw new Exception(ServiceError.errorMessages.get(Errors.NO_PARAMETERS_ENTERED_ERROR));
			}
			
			if(inputsNotEmpty(name, id)) {
				throw new Exception(ServiceError.errorMessages.get(Errors.MULTI_PARAMTERS_ENTERED_ERROR));
			}
			
			try {
				if(!ObjectUtils.isEmpty(id)) {
					idInt = Integer.parseInt(id);
				}
			} catch (Exception e) {
				throw new Exception(ServiceError.errorMessages.get(Errors.INVALID_ID_ERROR));
			}
			
			//Request and Response mappers
			RestTemplate restTemplate = new RestTemplate();
			
			//Retrieve Users
			ResponseEntity<User[]> users = restTemplate.getForEntity(USERS_API_URL, User[].class);
			
			//Search for user
			User result = filterUsers(users, idInt, name);
			
			if(result == null) {
				throw new Exception(ServiceError.errorMessages.get(Errors.NO_RESULTS_FOUND_ERROR));
			}
			
			//Map the response message
			ContactInformation output = new ContactInformation(result.getId(), result.getEmail(), result.getPhone());
			try {
				return objectMapper.writeValueAsString(output);
			} catch (JsonProcessingException e) {
				throw new Exception(ServiceError.errorMessages.get(Errors.JSON_CONVERSION_ERROR));
			}
		} catch (Exception e) {
			//write error response
			try {
				return objectMapper.writeValueAsString(buildErrorMessage(e.getMessage()));
			} catch (JsonProcessingException e1) {
				return ServiceError.errorMessages.get(Errors.ERROR_PROCESSING_EXCEPTION);
			}
		}
	}
	
	private User filterUsers(ResponseEntity<User[]> users, int id, String name) {
		//Search for a user by either name or id
		for(User user : users.getBody()) {
			if(id != 0) {
				if(user.getId() == id) {
					return user;
				}
			}
			if(!ObjectUtils.isEmpty(name)) {
				if(user.getName().equals(name)) {
					return user;		
				}
			}
		}
		return null;
	}

	private boolean inputsEmpty(String id, String name) {
		return (ObjectUtils.isEmpty(id) && ObjectUtils.isEmpty(name));
	}
	
	private boolean inputsNotEmpty(String id, String name) {
		return (!ObjectUtils.isEmpty(id) && !ObjectUtils.isEmpty(name));
	}
	
	private ServiceError buildErrorMessage(String errorMessage) {
		ServiceError error = new ServiceError(errorMessage);
		
		return error;
	}
}
