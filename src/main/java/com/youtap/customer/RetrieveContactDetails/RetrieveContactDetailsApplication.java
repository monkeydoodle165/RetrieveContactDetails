package com.youtap.customer.RetrieveContactDetails;

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

@SpringBootApplication
@RestController
public class RetrieveContactDetailsApplication {

	//Urls
	private static final String USER_CONTACTS_ENDPOINT = "/getusercontacts";
	private static final String USERS_API_URL = "https://jsonplaceholder.typicode.com/users";
	
	//Errors
	private static final String NO_PARAMETERS_ENTERED_ERROR = "Please enter either an id or name in order to search";
	private static final String JSON_CONVERSION_ERROR = "Error ocurred while converting the output to JSON";
	private static final String NO_RESULTS_FOUND_ERROR = "No results found for given inputs";
	

	public static void main(String[] args) {
		SpringApplication.run(RetrieveContactDetailsApplication.class, args);
	}
	
	@GetMapping(USER_CONTACTS_ENDPOINT)
	public String getUserContacts(@RequestParam(value = "name", required=false) String name, @RequestParam(value = "id", required=false) String id) {		
		
		if(ObjectUtils.isEmpty(id) && ObjectUtils.isEmpty(name)) {
			return NO_PARAMETERS_ENTERED_ERROR;
		}
		
		//Request and Response mappers
		RestTemplate restTemplate = new RestTemplate();
		ObjectMapper objectMapper = new ObjectMapper();
		
		//Retrieve Users
		ResponseEntity<User[]> users = restTemplate.getForEntity(USERS_API_URL, User[].class);
		
		//Initialise User
		User result = null;
		
		//Search for a user by either name or id
		for(User user : users.getBody()) {
			if(!ObjectUtils.isEmpty(id)) {
				if(user.getId() == Integer.parseInt(id)) {
					result = user;
				}
			}
			if(!ObjectUtils.isEmpty(name)) {
				if(user.getName().equals(name)) {
					result = user;		
				}
			}
		}
		
		if(result == null) {
			return NO_RESULTS_FOUND_ERROR;
		}
		
		//Map the response message
		ContactInformation output = new ContactInformation(result.getId(), result.getEmail(), result.getPhone());
		try {
			return objectMapper.writeValueAsString(output);
		} catch (JsonProcessingException e) {
			return JSON_CONVERSION_ERROR;
		}
	}
}
