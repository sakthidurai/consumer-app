package com.app.consumer;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
@EnableEurekaClient
@ComponentScan("com.app.*")
public class ConsumerAppApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ConsumerAppApplication.class, args);
		
	}
}

@RestController
@RequestMapping("/consumer")
class ConsumerAppController {

	@Autowired
	private DiscoveryClient discoveryClient;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@RequestMapping(value = "/receiveMessage", method= RequestMethod.GET, produces ="application/JSON")
	public List<ProducerAppResponse> getEmployee() throws Exception {
		
		List<ServiceInstance> instances=discoveryClient.getInstances("producer-app");
		ServiceInstance serviceInstance=instances.get(0);
		
		String baseUrl=serviceInstance.getUri().toString();
		System.out.println("the base url is "+baseUrl);
		
		baseUrl=baseUrl+"/producer/receiveMessage";
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response=null;
		try{
		response=restTemplate.exchange(baseUrl,
				HttpMethod.GET, getHeaders(),String.class);
		}catch (Exception ex)
		{
			System.out.println(ex);
		}
		List<ProducerAppResponse> producerResponse = objectMapper.readValue(response.getBody(), new TypeReference<List<ProducerAppResponse>>(){});
		System.out.println(response.getBody());
		return producerResponse;
		
	}
	private  HttpEntity<?> getHeaders() throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		return new HttpEntity<>(headers);
	}
}
class ProducerAppResponse{
	
	public ProducerAppResponse(){
		
	}
	public ProducerAppResponse(String name){
		this.name = name;
	}

	
	Long id;

	String name;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "Producer [id=" + id + ", name=" + name + "]";
	}
	
}
