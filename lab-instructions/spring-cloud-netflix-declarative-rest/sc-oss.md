# Spring Cloud Netflix: Declarative Rest Client with Feign

<!-- TOC depth:6 withLinks:1 updateOnSave:1 orderedList:0 -->

- [Spring Cloud Netflix: Declarative Rest Client with Feign](#spring-cloud-netflix-declarative-rest-client-with-feign)
	- [Requirements](#requirements)
	- [Exercises](#exercises)
		- [Start the  `config-server`,  `service-registry`, and `fortune-service`](#start-the-config-server-service-registry-and-fortune-service)
		- [Setup `greeting-feign`](#setup-greeting-feign)
		- [Deploy the `greeting-feign` to PCF](#deploy-the-greeting-feign-to-pcf)
<!-- /TOC -->

## Requirements

[Lab Requirements](../requirements.md)

## Exercises

### Start the  `config-server`,  `service-registry`, and `fortune-service`

1) Start the `config-server` in a terminal window.  You may have a terminal windows still open from previous labs.  They may be reused for this lab.

```bash
$ cd $SPRING_CLOUD_SERVICES_LABS_HOME/config-server
$ mvn clean spring-boot:run
```

2) Start the `service-registry`

```bash
$ cd $SPRING_CLOUD_SERVICES_LABS_HOME/service-registry
$ mvn clean spring-boot:run
```

3) Start the `fortune-service`

```bash
$ cd $SPRING_CLOUD_SERVICES_LABS_HOME/fortune-service
$ mvn clean spring-boot:run
```

### Setup `greeting-feign`
1) ) Review the $SPRING_CLOUD_SERVICES_LABS_HOME/greeting-feign/pom.xml file. By adding `spring-cloud-starter-feign` to the classpath this application can create feign clients.

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-feign</artifactId>
</dependency>
```

1) Review the the following file: `$SPRING_CLOUD_SERVICES_LABS_HOME/greeting-feign/src/main/java/io/pivotal/greeting/FortuneServiceClient.java`.  Notice the `@FeignClient`.  For this interface, we don't need to write the implementation.

```java
@FeignClient("fortune-service")
public interface FortuneServiceClient {

	 @RequestMapping(method = RequestMethod.GET, value = "/")
	 String getFortune();
}

```
2) Review the the following file: `$SPRING_CLOUD_SERVICES_LABS_HOME/greeting-feign/src/main/java/io/pivotal/greeting/GreetingController.java`.  Notice the `FortuneServiceClient` being autowired in.

```java
@Controller
public class GreetingController {

	Logger logger = LoggerFactory
			.getLogger(GreetingController.class);




	@Autowired
	private FortuneServiceClient fortuneServiceClient;

	@RequestMapping("/")
	String getGreeting(Model model){

		logger.debug("Adding greeting");
		model.addAttribute("msg", "Greetings!!!");


        String fortune = fortuneServiceClient.getFortune();

		logger.debug("Adding fortune");
		model.addAttribute("fortune", fortune);

		//resolves to the greeting.vm velocity template
		return "greeting";
	}
}

```

3) Review the the following file: `$SPRING_CLOUD_SERVICES_LABS_HOME/greeting-feign/src/main/java/io/pivotal/GreetingFeignApplication.java`.  Notice the `@EnableFeignClients` annotation.  This enables feign client creation.

```java
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class GreetingRibbonFeignApplication {

    public static void main(String[] args) {
        SpringApplication.run(GreetingRibbonFeignApplication.class, args);
    }

}

```


2) Open a new terminal window.  Start the `greeting-feign` app.

 ```bash
$ cd $SPRING_CLOUD_SERVICES_LABS_HOME/greeting-feign
$ mvn clean spring-boot:run
```

3) After the a few moments, check the `service-registry` [dashboard](http://localhost:8761).  Confirm the `greeting-ribbon` app is registered.


4) [Browse](http://localhost:8080/) to the `greeting-ribbon` application.  Confirm you are seeing fortunes.  Refresh as desired.  Also review the terminal output for the `greeting-ribbon` app.


### Deploy the `greeting-feign` to PCF

1) Package, push, bind services and set environment variables for `greeting-feign`.

```
$ mvn clean package
$ cf push greeting-feign -p target/greeting-feign-0.0.1-SNAPSHOT.jar -m 512M --random-route --no-start
$ cf bind-service greeting-feign config-server
$ cf set-env greeting-feign SPRING_PROFILES_ACTIVE dev
$ cf start greeting-feign
```

2) Refresh the `greeting-feign` `/` endpoint.  Review the logs for `greeting-feign` and `fortune-service`.
