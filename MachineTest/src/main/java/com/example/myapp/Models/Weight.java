package com.example.myapp.Models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Weight {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String username; 

	private Double weight;

	private LocalDateTime addedTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public LocalDateTime getAddedTime() {
		return addedTime;
	}

	public void setAddedTime(LocalDateTime addedTime) {
		this.addedTime = addedTime;
	}
}
