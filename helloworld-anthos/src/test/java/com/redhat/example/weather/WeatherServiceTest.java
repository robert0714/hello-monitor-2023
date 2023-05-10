package com.redhat.example.weather;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
 
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class WeatherServiceTest {
	
	@InjectMocks
	WeatherService testObject;
	
	@Mock
    SelectedCountry selectedCountry;
	
    @Mock
    EntityManager entityManager;
    
    @Mock
    Country country;

	@Before
	public void setUp() throws Exception { 
		List<City> cities = mock(List.class);
		
//		when(country.getName()).thenReturn("England");
//		when(country.getCities()).thenReturn(cities);
	} 

	@Test
	public void testGetList() {
		when(entityManager.find( any(),any())).thenReturn(country);
		when(selectedCountry.getCode()).thenReturn("");
		
		assertThat(testObject.getList()).isEqualTo(country);
		 
		
		verify(selectedCountry).getCode();
        verify(entityManager).find(Country.class,"");
	}

}
