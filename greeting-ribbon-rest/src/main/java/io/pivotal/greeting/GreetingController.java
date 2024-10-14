package io.pivotal.greeting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class GreetingController {

	Logger logger = LoggerFactory
			.getLogger(GreetingController.class);

	

	
	@Autowired
	@LoadBalanced
	private RestTemplate restTemplate;
	
	@RequestMapping("/")
	String getGreeting(Model model){
        
		logger.debug("Adding greeting");
		model.addAttribute("msg", "Greetings!!!");
			
		
        String fortune = restTemplate.getForObject("http://fortune-service", String.class);

		logger.debug("Adding fortune");
		model.addAttribute("fortune", fortune);
		
		//resolves to the greeting.vm velocity template
		return "greeting";
	}
	
	
}
