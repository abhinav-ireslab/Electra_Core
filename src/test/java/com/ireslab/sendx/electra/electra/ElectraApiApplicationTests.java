package com.ireslab.sendx.electra.electra;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.ireslab.sendx.electra.model.UserProfile;
import com.ireslab.sendx.electra.model.UserRegistrationRequest;

@RunWith(SpringRunner.class)
//@SpringBootTest
public class ElectraApiApplicationTests {

	@Test
	public void contextLoads() {
	}

	static ObjectWriter objectWriter = new ObjectMapper().writerWithDefaultPrettyPrinter();

	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
		UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest();
		List<UserProfile> userProfiles = new ArrayList<>();

		UserProfile userProfile = new UserProfile();
		userProfile.setFirstName("Nitin");
		userProfile.setLastName("Malik");
		userProfile.setEmailAddress("nitin.malik@hotmail.com");
		userProfile.setMobileNumber("9711355293");
		userProfile.setUserCorrelationId(UUID.randomUUID().toString());
		userProfiles.add(userProfile);

		userRegistrationRequest.setUsers(userProfiles);

		System.out.println(objectWriter.writeValueAsString(userRegistrationRequest));
	}
}
