package com.redhat.example.weather;

import org.junit.Before;
import org.junit.Test;
 

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class PersistentTest {

    private EntityManager entityManager;
    private EntityTransaction transaction;

    @Before
    public void setUp() {
        entityManager = Persistence.createEntityManagerFactory("ut").createEntityManager();
        transaction = entityManager.getTransaction();
    }

    @Test
    public void test() {
        transaction.begin();

        City part = new City();
        part.setId("01");
        part.setName("Taipei");         
        Country country01 = new Country();
        country01.setId("02");
        country01.setCities(Arrays.asList(part));
        entityManager.merge(country01);
        transaction.commit(); 
       
        Country en = entityManager.find(Country.class,"en");
        Country us = entityManager.find(Country.class,"us");
        Country fr = entityManager.find(Country.class,"fr");
        
        assertThat(en.getName()).isEqualTo("England");
        assertThat(en.getCities().size()).isEqualTo(3);
        assertThat(en.getCities().get(1).getId()).isEqualTo("lon");
        assertThat(en.getCities().get(1).getWeatherType()).isEqualTo("sunny");
        assertThat(en.getCities().get(1).getWind()).isEqualTo(3);
        assertThat(en.getCities().get(1).getTempFeelsLike()).isEqualTo(9);
        assertThat(en.getCities().get(1).getMinTemp()).isEqualTo(7);
        assertThat(en.getCities().get(1).getMaxTemp()).isEqualTo(11);
        assertThat(en.getCities().get(1).getTemp()).isEqualTo(9);
        System.out.println(en);
        System.out.println(en.getCities().get(1));
    }

}