package com.example.myapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.myapp.dto.UserDto;
import com.example.myapp.service.UserService;

@Controller

public class TrackerController {
	
	
		 @Autowired
		    private UserService userService;
	    @GetMapping("/tracker")
	    public String trackerPage() {
	        return "trackerHome"; 
	    }
	    @GetMapping({"/home"})
	    public String home() {
	        return "home"; 
	    }

	    @GetMapping("/about")
	    public String about() {
	        return "about"; 
	    }

	    @GetMapping("/contact")
	    public String contact() {
	        return "contact"; 
	    }
	   
	    
	    @GetMapping("/registration")
	    public String getRegistrationPage(@ModelAttribute("user") UserDto userDto) {
	        return "register";
	    }
	    
	    @PostMapping("/registration")
	    public String saveUser(@ModelAttribute("user") UserDto userDto, Model model) {
	        if (userService.existsByEmail(userDto.getEmail())) {
	            model.addAttribute("error", "An account with this email already exists. Please log in or use 'Forgot Password'.");
	            return "register";
	        }

	        userService.save(userDto);
	        model.addAttribute("message", "Registered Successfully! Please login !");
	        return "register";
	    }

	    @GetMapping("/login")
	    public String login() {
	        return "login";
	    }
	}
