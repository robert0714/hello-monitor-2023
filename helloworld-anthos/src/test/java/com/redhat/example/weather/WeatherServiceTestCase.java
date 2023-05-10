package com.redhat.example.weather;
import java.io.File;
import static java.lang.Thread.sleep;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Client;
import static javax.ws.rs.client.ClientBuilder.newClient;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import static org.jboss.shrinkwrap.api.ShrinkWrap.create;
import static org.jboss.shrinkwrap.api.asset.EmptyAsset.INSTANCE;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.jboss.shrinkwrap.resolver.api.maven.Maven.configureResolver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class WeatherServiceTestCase {
	private static final Logger logger = getLogger(WeatherServiceTestCase.class.getName());

	@ArquillianResource
	private URL url;

    @PersistenceContext(unitName = "primary")
    EntityManager entityManager;
	
	@Deployment
	public static WebArchive createWebDeployment() {
		final WebArchive war = create(WebArchive.class, "country-rest-test.war");
		war.addPackage(CountryService.class.getPackage());
		war.addAsWebInfResource(INSTANCE, "beans.xml");
		
		war.addAsWebInfResource(new FileAsset(new File("src/test/resources/jbossas-ds.xml")), "jbossas-ds.xml");
		
		war.addAsResource(new FileAsset(new File("src/test/resources/META-INF/web.xml")), "META-INF/web.xml");
		war.addAsResource(new FileAsset(new File("src/test/resources/META-INF/persistence-store.xml")),
				"META-INF/persistence.xml");
		war.addAsResource(new FileAsset(new File("src/test/resources/META-INF/microprofile-config.properties")),
				"META-INF/microprofile-config.properties");
		
		war.addAsResource(new FileAsset(new File("src/test/resources/store.import.sql")), "store.import.sql");
		
		
		File[] files = configureResolver()
		//		.workOffline() 
		        .withClassPathResolution(true)
				.withMavenCentralRepo(true) 
				.loadPomFromFile("pom.xml")
				.importRuntimeDependencies()
				.resolve("org.eclipse.microprofile.health:microprofile-health-api:3.1","org.assertj:assertj-core:3.21.0").withTransitivity().asFile();
		
		
		war.addAsLibraries(files);
		
		System.out.println(war.toString(true)); 
		return war;
	}
//	@Test 
	public void testSetSelectedCountry() throws Exception {
		logger.info("start SetSelectedCountry test");
		MyResult myResponse = invokeFuture(url + "api/country/en");
		String responseContent = myResponse.getResponse();
		System.out.println("---------------------------");
		System.out.println(responseContent);
		assertEquals("magic number is: ", "MagicNumber [value=3]", responseContent);
		assertEquals("response is: ", true, myResponse.isOk());
		logger.info("end rest receive messages test");
	}
	
	@Test
	@RunAsClient
	public void testGetList() throws Exception {
		logger.info("start SetSelectedCountry test");		
		MyResult myResponse = invokeCallbackString(url + "api/weather");
		String responseContent = myResponse.getResponse();
		assertEquals("weather content is: ", "{\"cities\":[{\"id\":\"lon\",\"maxTemp\":11,\"minTemp\":7,\"name\":\"London\",\"temp\":9,\"tempFeelsLike\":9,\"weatherType\":\"sunny\",\"wind\":3},{\"id\":\"man\",\"maxTemp\":8,\"minTemp\":5,\"name\":\"Manchester\",\"temp\":7,\"tempFeelsLike\":3,\"weatherType\":\"rainy-7\",\"wind\":10},{\"id\":\"edi\",\"maxTemp\":-6,\"minTemp\":-9,\"name\":\"Edinburgh\",\"temp\":-7,\"tempFeelsLike\":-13,\"weatherType\":\"snowy-4\",\"wind\":7}],\"id\":\"en\",\"name\":\"England\"}", responseContent);
		assertEquals("response is: ", true, myResponse.isOk());
		logger.info("end rest receive messages test");
	} 
	@Test
	@RunAsClient
	public void testReadiness() throws Exception {
		logger.info("start SetSelectedCountry test");		
		MyResult myResponse = invokeCallbackString("http://localhost:9990/health/ready");
		String responseContent = myResponse.getResponse();  
		assertNotNull(responseContent);
		assertEquals("response is: ", true, myResponse.isOk());
		logger.info("end rest receive messages test");
	}
	@Test
	@RunAsClient
	public void testLiveness() throws Exception {
		logger.info("start SetSelectedCountry test");		
		MyResult myResponse = invokeCallbackString("http://localhost:9990/health/live");
		String responseContent = myResponse.getResponse();  
		assertNotNull(responseContent);
		assertEquals("response is: ", true, myResponse.isOk());
		logger.info("end rest receive messages test");
	}
    @Test
    public void testPersistent() { 
       
        Country en = entityManager.find(Country.class,"en");
        Country us = entityManager.find(Country.class,"us");
        Country fr = entityManager.find(Country.class,"fr");
        
        assertThat(en.getName()).isEqualTo("England");
        assertThat(en.getCities().size()).isEqualTo(3);
        assertThat(en.getCities().get(0).getId()).isEqualTo("lon");
        assertThat(en.getCities().get(0).getWeatherType()).isEqualTo("sunny");
        assertThat(en.getCities().get(0).getWind()).isEqualTo(3);
        assertThat(en.getCities().get(0).getTempFeelsLike()).isEqualTo(9);
        assertThat(en.getCities().get(0).getMinTemp()).isEqualTo(7);
        assertThat(en.getCities().get(0).getMaxTemp()).isEqualTo(11);
        assertThat(en.getCities().get(0).getTemp()).isEqualTo(9);
    }
	private MyResult invokeFuture(String url) {
		Client client = newClient();
		WebTarget target = client.target(url);
		final AsyncInvoker asyncInvoker = target.request().async();
		Future<Response> future = asyncInvoker.get();
		try {
			sleep(500);
		} catch (InterruptedException e) {
			logger.log(SEVERE, "error", e);
		}
		final MyResult myResponse = new MyResult();
		try {
			Response response = future.get();
			myResponse.setOk(response.hasEntity());
			myResponse.setResponse(response.readEntity(String.class));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			logger.log(SEVERE, "error", e);
		}
		return myResponse;
	}

	private MyResult invokeCallbackString(String url) {
		Client client = newClient();
		WebTarget target = client.target(url);
		final AsyncInvoker asyncInvoker = target.request().async();
		final MyResult myResponse = new MyResult();

		asyncInvoker.get(new InvocationCallback<String>() {
			@Override
			public void completed(String response) {
				myResponse.setResponse(response);
				myResponse.setOk(true);
			}

			@Override
			public void failed(Throwable arg0) {
				myResponse.setResponse(arg0.getMessage());
				myResponse.setOk(false);
			}
		});
		try {
			sleep(2000);
		} catch (InterruptedException e) {
			logger.log(SEVERE, "error", e);
		}
		return myResponse;
	}

	private MyResult invokeCallbackResponse(String url) {
		Client client = newClient();
		WebTarget target = client.target(url);
		final AsyncInvoker asyncInvoker = target.request().async();
		final MyResult myResponse = new MyResult();

		asyncInvoker.get(new InvocationCallback<Response>() {
			@Override
			public void completed(Response response) {
				myResponse.setResponse(response.readEntity(String.class));
				myResponse.setOk(response.hasEntity());
			}

			@Override
			public void failed(Throwable arg0) {
				myResponse.setResponse(arg0.getMessage());
				myResponse.setOk(false);
			}
		});
		try {
			sleep(2000);
		} catch (InterruptedException e) {
			logger.log(SEVERE, "error", e);
		}
		return myResponse;
	}
}
