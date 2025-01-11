# Hibernate JPA Actor Movie Database

This project demonstrates the use of Hibernate JPA to manage a database of actors and movies. It includes entity mappings, CRUD operations, and various queries using both HQL and CriteriaBuilder.

## Table of Contents

- [Setup](#setup)
- [Entity Mappings](#entity-mappings)
  - [Actor](#actor)
  - [Movie](#movie)
- [DAO Classes](#dao-classes)
  - [ActorDao](#actordao)
  - [MovieDao](#moviedao)
- [Usage](#usage)
- [Example Queries](#example-queries)

## Setup

1. **Clone the repository:**
   ```sh
   git clone <repository-url>
   cd Hibernate_JPA_Actor_MovieDB
   ```

2. **Configure the database:**
   Ensure you have a MySQL database running and update the `persistence.xml` file with your database credentials.
   ```xml
   <!-- filepath: /src/main/resources/META-INF/persistence.xml -->
   <persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence" version="2.1">
       <persistence-unit name="mysql-config">
           <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
           <class>com.hibernate.JPA.Hibernate.Actor</class>
           <class>com.hibernate.JPA.Hibernate.Movie</class>
           <properties>
               <property name="javax.persistence.jdbc.driver" value="com.mysql.cj.jdbc.Driver"/>
               <property name="javax.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/jpa_actor_moviedb"/>
               <property name="javax.persistence.jdbc.user" value="root"/>
               <property name="javax.persistence.jdbc.password" value="FE321869"/>
               <property name="hibernate.dialect" value="org.hibernate.dialect.MySQLDialect"/>
               <property name="hibernate.hbm2ddl.auto" value="update"/>
               <property name="hibernate.show_sql" value="true"/>
               <property name="hibernate.format_sql" value="true"/>
           </properties>
       </persistence-unit>
   </persistence>
   ```

3. **Build and run the project:**
   Use your preferred IDE to build and run the project. The main class is `App.java`.

## Entity Mappings

### Actor

The `Actor` entity represents an actor in the database. It is mapped to the `Actor` table and has a many-to-many relationship with the `Movie` entity.

```java
// filepath: /src/main/java/com/hibernate/JPA/Hibernate/Actor.java
@Entity
public class Actor {
    @Id
    private int id;
    private String name;
    private int age;
    private String industry;
    private double salary;
    private String nationality;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "Actor_Movie",
        joinColumns = @JoinColumn(name = "Actor_id"),
        inverseJoinColumns = @JoinColumn(name = "movie_id")
    )
    private List<Movie> movies;

    // Getters and setters
    // toString method
}
```

### Movie

The `Movie` entity represents a movie in the database. It is mapped to the `Movie` table and has a many-to-many relationship with the `Actor` entity.

```java
// filepath: /src/main/java/com/hibernate/JPA/Hibernate/Movie.java
@Entity
public class Movie {
    @Id
    private int movieId;
    private String movieName;
    private String movieDirector;
    private String genre;
    private String verdict;
    private int collection;

    @ManyToMany(mappedBy = "movies")
    private List<Actor> actor;

    // Getters and setters
    // toString method
}
```

## DAO Classes

### ActorDao

The `ActorDao` class provides CRUD operations and various queries for the `Actor` entity.

```java
// filepath: /src/main/java/com/hibernate/actor_movie/dao/ActorDao.java
public class ActorDao {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql-config");

    private void commitAndClose(EntityManager em) {
        EntityTransaction et = em.getTransaction();
        et.commit();
        em.close();
    }

    public void addActor(Actor actor) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(actor);
        commitAndClose(em);
    }

    public Actor findActorById(int id) {
        EntityManager em = emf.createEntityManager();
        Actor actor = em.find(Actor.class, id);
        em.close();
        return actor;
    }

    public List<Actor> findActorByName(String name) {
        EntityManager em = emf.createEntityManager();
        List<Actor> actors = em.createQuery("FROM Actor a WHERE a.name = :name", Actor.class)
                .setParameter("name", name)
                .getResultList();
        em.close();
        return actors;
    }

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

    public void updateActorNationalityById(int id, String newNationality) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("UPDATE Actor SET nationality = :nationality WHERE id = :id")
        .setParameter("nationality", newNationality)
        .setParameter("id", id)
        .executeUpdate();
        commitAndClose(em);
    }

    public void deleteAllActorsByIndustry(String industry) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Actor WHERE industry = :industry")
        .setParameter("industry", industry)
        .executeUpdate();
        commitAndClose(em);
    }

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
```

### MovieDao

The `MovieDao` class provides CRUD operations and various queries for the `Movie` entity.

```java
// filepath: /src/main/java/com/hibernate/actor_movie/dao/MovieDao.java
public class MovieDao {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("mysql-config");

    private void commitAndClose(EntityManager em) {
        EntityTransaction et = em.getTransaction();
        et.commit();
        em.close();
    }

    public void addMovie(Movie movie) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Scanner scanner = new Scanner(System.in);

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

        Query query = em.createQuery("FROM Actor");
        List<Actor> actors = query.getResultList();
        for (Actor actor : actors) {
            System.out.println(actor);
        }

        System.out.println("Enter actor IDs (comma separated): ");
        String actorIdsInput = scanner.nextLine();
        String[] actorIds = actorIdsInput.split(",");

        List<Actor> actorsList = new ArrayList<>();
        for (String actorId : actorIds) {
            Actor actor = em.find(Actor.class, Integer.parseInt(actorId.trim()));
            if (actor != null) {
                actorsList.add(actor);
            }
        }

        movie.setActor(actorsList);
        em.merge(movie);
        commitAndClose(em);
    }

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

    public void updateMovieCollectionByVerdict(String verdict, int increment) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Query query = em.createQuery("UPDATE Movie m SET m.collection = m.collection + :increment WHERE m.verdict = :verdict");
        query.setParameter("increment", increment);
        query.setParameter("verdict", verdict);
        query.executeUpdate();
        commitAndClose(em);
    }

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

    public void deleteAllMoviesByActorName(String actorName) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Query query = em.createQuery("DELETE FROM Movie m WHERE :actorName MEMBER OF m.actor");
        query.setParameter("actorName", actorName);
        query.executeUpdate();
        commitAndClose(em);
    }

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
```

## Usage

1. **Run the application:**
   The main class is `App.java`. It provides a menu-driven interface to interact with the database.

2. **Menu options:**
   - Add Actor
   - Find Actor by ID
   - Find Actor by Name
   - Find Actor by Industry
   - Find Actor Between Age
   - Find All Actors by Movie Name
   - Update All Actor Salary by Movie ID
   - Update Actor Nationality by ID
   - Delete All Actors by Industry
   - Delete All Actors by Movie Name
   - Add Movie
   - Find Movie by Name
   - Find All Movies by Genre
   - Find All Movies by Director
   - Find All Movies with Collection Greater Than
   - Update Movie Collection by Verdict
   - Find All Movies by Actor ID
   - Delete All Movies by Actor Name
   - Delete All Movies with Collection Less Than
   - Exit

## Example Queries

### Add Actor

```java
System.out.println("Enter actor ID: ");
int actorId = scanner.nextInt();
scanner.nextLine(); // consume newline
System.out.println("Enter actor name: ");
String name = scanner.nextLine();
System.out.println("Enter actor age: ");
int age = scanner.nextInt();
scanner.nextLine(); // consume newline
System.out.println("Enter actor industry: ");
String industry = scanner.nextLine();
System.out.println("Enter actor salary: ");
int salary = scanner.nextInt();
scanner.nextLine(); // consume newline
System.out.println("Enter actor nationality: ");
String nationality = scanner.nextLine();

Actor newActor = new Actor();
newActor.setId(actorId);
newActor.setName(name);
newActor.setAge(age);
newActor.setIndustry(industry);
newActor.setSalary(salary);
newActor.setNationality(nationality);

actorDao.addActor(newActor);
```

### Find Actor by Name

```java
System.out.println("Enter actor name: ");
String actorName = scanner.nextLine();
List<Actor> actorsByName = actorDao.findActorByName(actorName);
actorsByName.forEach(System.out::println);
```

### Find All Actors by Movie Name

```java
System.out.println("Enter movie name: ");
String movieName = scanner.nextLine();
List<Actor> actorsByMovieName = actorDao.findAllActorsByMovieName(movieName);
actorsByMovieName.forEach(System.out::println);
```

### Add Movie

```java
Movie newMovie = new Movie();
movieDao.addMovie(newMovie);
```

### Find Movie by Name

```java
System.out.println("Enter movie name: ");
String findMovieName = scanner.nextLine();
movieDao.findMovieByName(findMovieName);
```

### Update Movie Collection by Verdict

```java
System.out.println("Enter verdict: ");
String verdict = scanner.nextLine();
System.out.println("Enter increment amount: ");
int increment = scanner.nextInt();
movieDao.updateMovieCollectionByVerdict(verdict, increment);
```

### Delete All Actors by Movie Name

```java
System.out.println("Enter movie name: ");
String delMovieName = scanner.nextLine();
actorDao.deleteAllActorsByMovieName(delMovieName);
```

This README file provides a comprehensive overview of the project, including setup instructions, entity mappings, DAO classes, usage examples, and example queries. It should help you understand and work with the project effectively.
