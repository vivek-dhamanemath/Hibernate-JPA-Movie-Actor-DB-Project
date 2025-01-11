package com.hibernate.actor_movie.dao;

import com.hibernate.JPA.Hibernate.Actor;
import com.hibernate.JPA.Hibernate.Movie;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MovieDao {

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql-config");

    // To avoid code repetition, we are using a helper method to handle the transaction commit and entity manager close operations.
    private void commitAndClose(EntityManager em) {
        EntityTransaction et = em.getTransaction();
        et.commit();
        em.close();
    }

    // addMovie()
    public void addMovie(Movie movie) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Scanner scanner = new Scanner(System.in);

        // 1. Create movie object and set all the values
        System.out.println("Enter movie ID: ");
        movie.setMovieId(scanner.nextInt());
        scanner.nextLine(); // Consume newline

        System.out.println("Enter movie name: ");
        movie.setMovieName(scanner.nextLine());

        System.out.println("Enter movie director: ");
        movie.setMovieDirector(scanner.nextLine());

        System.out.println("Enter genre: ");
        movie.setGenre(scanner.nextLine());

        System.out.println("Enter verdict: ");
        movie.setVerdict(scanner.nextLine());

        System.out.println("Enter collection: ");
        movie.setCollection(scanner.nextInt());
        scanner.nextLine(); // Consume newline

        // 2. Fetch all actor details and display
        Query query = em.createQuery("FROM Actor");
        List<Actor> actors = query.getResultList();
        for (Actor actor : actors) {
            System.out.println(actor);
        }

        // 3. Take the actor IDs as input (imp and crucial) take string inputs
        System.out.println("Enter actor IDs (comma separated): ");
        String actorIdsInput = scanner.nextLine();
        String[] actorIds = actorIdsInput.split(",");

        // 4. Find the actors by ID
        List<Actor> actorsList = new ArrayList<>();
        for (String actorId : actorIds) {
            Actor actor = em.find(Actor.class, Integer.parseInt(actorId.trim()));
            if (actor != null) {
                actorsList.add(actor);
            }
        }

        // 5. Add the actor object into the movie
        movie.setActor(actorsList);

        // 6. Then save the movie object
        em.merge(movie);
        commitAndClose(em);
    }

    // findMovieByName(): using HQL
    public void findMovieByName(String movieName) {
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("FROM Movie m WHERE m.movieName = :movieName", Movie.class);
        query.setParameter("movieName", movieName);
        List<Movie> movies = query.getResultList();
        for (Movie movie : movies) {
            System.out.println(movie);
        }
        em.close();
    }

    // findAllMoviesByGenre(): using CriteriaBuilder
    public void findAllMoviesByGenre(String genre) {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Movie> cq = cb.createQuery(Movie.class);
        Root<Movie> root = cq.from(Movie.class);
        cq.select(root).where(cb.equal(root.get("genre"), genre));
        List<Movie> movies = em.createQuery(cq).getResultList();
        for (Movie movie : movies) {
            System.out.println(movie);
        }
        em.close();
    }

    // findAllMoviesByDirector(): using HQL
    public void findAllMoviesByDirector(String director) {
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("FROM Movie m WHERE m.movieDirector = :director", Movie.class);
        query.setParameter("director", director);
        List<Movie> movies = query.getResultList();
        for (Movie movie : movies) {
            System.out.println(movie);
        }
        em.close();
    }

    // findAllMovieCollectionGreaterThan(): using CriteriaBuilder
    public void findAllMovieCollectionGreaterThan(int collection) {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Movie> cq = cb.createQuery(Movie.class);
        Root<Movie> root = cq.from(Movie.class);
        cq.select(root).where(cb.greaterThan(root.get("collection"), collection));
        List<Movie> movies = em.createQuery(cq).getResultList();
        for (Movie movie : movies) {
            System.out.println(movie);
        }
        em.close();
    }

    // updateMovieCollectionByVerdict(): using HQL
    public void updateMovieCollectionByVerdict(String verdict, int increment) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Query query = em.createQuery("UPDATE Movie m SET m.collection = m.collection + :increment WHERE m.verdict = :verdict");
        query.setParameter("increment", increment);
        query.setParameter("verdict", verdict);
        query.executeUpdate();
        commitAndClose(em);
    }

    // findAllMoviesByActorId(): using CriteriaBuilder
    public void findAllMoviesByActorId(int actorId) {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Movie> cq = cb.createQuery(Movie.class);
        Root<Movie> root = cq.from(Movie.class);
        cq.select(root).where(cb.isMember(actorId, root.get("actor")));
        List<Movie> movies = em.createQuery(cq).getResultList();
        for (Movie movie : movies) {
            System.out.println(movie);
        }
        em.close();
    }

    // deleteAllMoviesByActorName(): using HQL
    public void deleteAllMoviesByActorName(String actorName) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Query query = em.createQuery("DELETE FROM Movie m WHERE :actorName MEMBER OF m.actor");
        query.setParameter("actorName", actorName);
        query.executeUpdate();
        commitAndClose(em);
    }

    // deleteAllMoviesWithCollectionLessThan(): using CriteriaBuilder
    public void deleteAllMoviesWithCollectionLessThan(int collection) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Movie> cd = cb.createCriteriaDelete(Movie.class);
        Root<Movie> root = cd.from(Movie.class);
        cd.where(cb.lessThan(root.get("collection"), collection));
        em.createQuery(cd).executeUpdate();
        commitAndClose(em);
    }
}

