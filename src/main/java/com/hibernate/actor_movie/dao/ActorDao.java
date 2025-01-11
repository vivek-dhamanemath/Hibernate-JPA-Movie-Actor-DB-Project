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
import javax.persistence.criteria.Subquery;

import com.hibernate.JPA.Hibernate.Actor;
import com.hibernate.JPA.Hibernate.Movie;

import java.util.List;

public class ActorDao {
	
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql-config");

	// To avoid code repetition, we are using a helper method to handle the transaction commit and entity manager close operations.
	private void commitAndClose(EntityManager em) {
		EntityTransaction et = em.getTransaction();
		et.commit();
		em.close();
	}

	// addActor(): using inbuilt methods
	public void addActor(Actor actor) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.merge(actor);
		commitAndClose(em);
	}

	// addMovie(): using inbuilt method
	public void addMovie(Movie movie) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.persist(movie);
		commitAndClose(em);
	}

	// findActorById(): using inbuilt methods
	public Actor findActorById(int id) {
		EntityManager em = emf.createEntityManager();
		Actor actor = em.find(Actor.class, id);
		em.close();
		return actor;
	}

	// findActorByName(): using HQL with alias
	public List<Actor> findActorByName(String name) {
		EntityManager em = emf.createEntityManager();
		List<Actor> actors = em.createQuery("FROM Actor a WHERE a.name = :name", Actor.class)
				.setParameter("name", name)
				.getResultList();
		em.close();
		return actors;
	}

	// findActorByIndustry(): using CriteriaBuilder
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

	// findActorBetweenAge(): using CriteriaBuilder
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

	// findAllActorsByMovieName(): using CriteriaBuilder
	public List<Actor> findAllActorsByMovieName(String movieName) {
		EntityManager em = emf.createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Actor> cq = cb.createQuery(Actor.class);
		Root<Actor> root = cq.from(Actor.class);

		Subquery<Integer> subquery = cq.subquery(Integer.class);
		Root<Movie> movieRoot = subquery.from(Movie.class);
		subquery.select(movieRoot.get("movieId")).where(cb.equal(movieRoot.get("movieName"), movieName));

		cq.select(root).where(cb.in(root.join("movies").get("movieId")).value(subquery));
		List<Actor> actors = em.createQuery(cq).getResultList();
		em.close();
		return actors;
	}

	// updateAllActorSalaryByMovieId(): using CriteriaBuilder
	public void updateAllActorSalaryByMovieId(int movieId, double newSalary) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<Actor> cu = cb.createCriteriaUpdate(Actor.class);
		Root<Actor> root = cu.from(Actor.class);
		cu.set(root.get("salary"), newSalary).where(cb.isMember(movieId, root.get("movies")));
		em.createQuery(cu).executeUpdate();
		commitAndClose(em);
	}

	// updateActorNationalityById(): using HQL
	public void updateActorNationalityById(int id, String newNationality) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.createQuery("UPDATE Actor SET nationality = :nationality WHERE id = :id")
		.setParameter("nationality", newNationality)
		.setParameter("id", id)
		.executeUpdate();
		commitAndClose(em);
	}

	// deleteAllActorsByIndustry(): using HQL
	public void deleteAllActorsByIndustry(String industry) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.createQuery("DELETE FROM Actor WHERE industry = :industry")
		.setParameter("industry", industry)
		.executeUpdate();
		commitAndClose(em);
	}

	// deleteAllActorsByMovieName(): using CriteriaBuilder
	public void deleteAllActorsByMovieName(String movieName) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<Actor> cd = cb.createCriteriaDelete(Actor.class);
		Root<Actor> root = cd.from(Actor.class);

		Subquery<Integer> subquery = cd.subquery(Integer.class);
		Root<Movie> movieRoot = subquery.from(Movie.class);
		subquery.select(movieRoot.get("movieId")).where(cb.equal(movieRoot.get("movieName"), movieName));

		cd.where(cb.in(root.join("movies").get("movieId")).value(subquery));
		em.createQuery(cd).executeUpdate();
		commitAndClose(em);
	}
}

