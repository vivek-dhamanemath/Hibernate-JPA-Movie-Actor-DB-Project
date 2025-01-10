package com.hibernate.actor_movie.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

import com.hibernate.JPA.Hibernate.Actor;

import java.util.List;

public class ActorDao 
{
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql-config");

	//using inbuilt methods
		public void addActor(Actor actor){
		EntityManager em = emf.createEntityManager();
		EntityTransaction et = em.getTransaction();
		et.begin();
		em.persist(actor);
		et.commit();
		em.close();
	}

	//using inbuilt methods
		public Actor findActorById(int id){
		EntityManager em = emf.createEntityManager();
		Actor actor = em.find(Actor.class, id);
		em.close();
		return actor;
	}

	//using HQL with alias
		public List<Actor> findActorByName(String name) {
		EntityManager em = emf.createEntityManager();
		List<Actor> actors = em.createQuery("FROM Actor a WHERE a.name = :name", Actor.class)
				.setParameter("name", name)
				.getResultList();
		em.close();
		return actors;
	}

	//using CriteriaBuilder
		public List<Actor> findActorByIndustry(String industry) {
		EntityManager em = emf.createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Actor> cq = cb.createQuery(Actor.class);
		Root<Actor> root = cq.from(Actor.class);
		cq.select(root).where(cb.equal(root.get("industry"), industry));
		List<Actor> actors = em.createQuery(cq).getResultList();
		em.close();
		return actors;
	}

	//using CriteriaBuilder
		public List<Actor> findActorBetweenAge(int minAge, int maxAge) {
		EntityManager em = emf.createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Actor> cq = cb.createQuery(Actor.class);
		Root<Actor> root = cq.from(Actor.class);
		cq.select(root).where(cb.between(root.get("age"), minAge, maxAge));
		List<Actor> actors = em.createQuery(cq).getResultList();
		em.close();
		return actors;
	}

	//using CriteriaBuilder
		public List<Actor> findAllActorsByMovieName(String movieName) {
		EntityManager em = emf.createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Actor> cq = cb.createQuery(Actor.class);
		Root<Actor> root = cq.from(Actor.class);
		cq.select(root).where(cb.equal(root.join("movies").get("name"), movieName));
		List<Actor> actors = em.createQuery(cq).getResultList();
		em.close();
		return actors;
	}

	//using CriteriaBuilder
		public void updateAllActorSalaryByMovieId(int movieId, double newSalary) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction et = em.getTransaction();
		et.begin();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<Actor> cu = cb.createCriteriaUpdate(Actor.class);
		Root<Actor> root = cu.from(Actor.class);
		cu.set("salary", newSalary).where(cb.equal(root.join("movies").get("id"), movieId));
		em.createQuery(cu).executeUpdate();
		et.commit();
		em.close();
	}

	//using HQL
		public void updateActorNationalityById(int id, String newNationality) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction et = em.getTransaction();
		et.begin();
		em.createQuery("UPDATE Actor SET nationality = :nationality WHERE id = :id")
		.setParameter("nationality", newNationality)
		.setParameter("id", id)
		.executeUpdate();
		et.commit();
		em.close();
	}

	//using HQL
		public void deleteAllActorsByIndustry(String industry) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction et = em.getTransaction();
		et.begin();
		em.createQuery("DELETE FROM Actor WHERE industry = :industry")
		.setParameter("industry", industry)
		.executeUpdate();
		et.commit();
		em.close();
	}

	//using CriteriaBuilder
		public void deleteAllActorsByMovieName(String movieName) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction et = em.getTransaction();
		et.begin();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<Actor> cd = cb.createCriteriaDelete(Actor.class);
		Root<Actor> root = cd.from(Actor.class);
		cd.where(cb.equal(root.join("movies").get("name"), movieName));
		em.createQuery(cd).executeUpdate();
		et.commit();
		em.close();
	}
}

