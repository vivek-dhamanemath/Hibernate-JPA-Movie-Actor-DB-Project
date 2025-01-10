package com.hibernate.actor_movie.dao;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class MovieDao {
	
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql-config");
	
	
	//1.create movie object and set all the values
	//2.fetch all actor details and display
	//3.take the actor id's as input (imp and crucial) take string inputs
	//4.find the actor's by id
	//5.add the actor object into the movie
	//6.then save the movie object
	
	
	
}
