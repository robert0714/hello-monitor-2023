package com.redhat.example.weather;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
 

public class SelectedCountryTest {
	
    private SelectedCountry testObject;

	@Before
	public void setUp() throws Exception {
		testObject = new SelectedCountry();  
	}

	@After
	public void tearDown() throws Exception {
	}

	 

	@Test
	public void testSetCode() {
		testObject.setCode("code");
		assertThat(testObject.getCode()).isEqualTo("code");
	}

}
