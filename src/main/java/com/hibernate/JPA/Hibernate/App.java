package com.hibernate.JPA.Hibernate;

import com.hibernate.actor_movie.dao.ActorDao;

import java.util.List;
import java.util.Scanner;

public class App 
{
    public static void main( String[] args )
    {
        ActorDao actorDao = new ActorDao();
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("Choose an option:");
            System.out.println("1. Add Actor");
            System.out.println("2. Find Actor by ID");
            System.out.println("3. Find Actor by Name");
            System.out.println("4. Find Actor by Industry");
            System.out.println("5. Find Actor Between Age");
            System.out.println("6. Find All Actors by Movie Name");
            System.out.println("7. Update All Actor Salary by Movie ID");
            System.out.println("8. Update Actor Nationality by ID");
            System.out.println("9. Delete All Actors by Industry");
            System.out.println("10. Delete All Actors by Movie Name");
            System.out.println("11. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    // Add Actor
                    System.out.println("Enter actor name:");
                    String name = scanner.nextLine();
                    System.out.println("Enter actor age:");
                    int age = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    System.out.println("Enter actor industry:");
                    String industry = scanner.nextLine();
                    System.out.println("Enter actor salary:");
                    int salary = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    System.out.println("Enter actor nationality:");
                    String nationality = scanner.nextLine();
                    
                    Actor newActor = new Actor();
                    newActor.setName(name);
                    newActor.setAge(age);
                    newActor.setIndustry(industry);
                    newActor.setSalary(salary);
                    newActor.setNationality(nationality);
                    actorDao.addActor(newActor);
                    break;
                case 2:
                    // Find Actor by ID
                    System.out.println("Enter actor ID:");
                    int id = scanner.nextInt();
                    Actor actor = actorDao.findActorById(id);
                    System.out.println(actor);
                    break;
                case 3:
                    // Find Actor by Name
                    System.out.println("Enter actor name:");
                    String actorName = scanner.nextLine();
                    List<Actor> actorsByName = actorDao.findActorByName(actorName);
                    actorsByName.forEach(System.out::println);
                    break;
                case 4:
                    // Find Actor by Industry
                    System.out.println("Enter industry:");
                    String actorIndustry = scanner.nextLine();
                    List<Actor> actorsByIndustry = actorDao.findActorByIndustry(actorIndustry);
                    actorsByIndustry.forEach(System.out::println);
                    break;
                case 5:
                    // Find Actor Between Age
                    System.out.println("Enter min age:");
                    int minAge = scanner.nextInt();
                    System.out.println("Enter max age:");
                    int maxAge = scanner.nextInt();
                    List<Actor> actorsByAge = actorDao.findActorBetweenAge(minAge, maxAge);
                    actorsByAge.forEach(System.out::println);
                    break;
                case 6:
                    // Find All Actors by Movie Name
                    System.out.println("Enter movie name:");
                    String movieName = scanner.nextLine();
                    List<Actor> actorsByMovieName = actorDao.findAllActorsByMovieName(movieName);
                    actorsByMovieName.forEach(System.out::println);
                    break;
                case 7:
                    // Update All Actor Salary by Movie ID
                    System.out.println("Enter movie ID:");
                    int movieId = scanner.nextInt();
                    System.out.println("Enter new salary:");
                    double newSalary = scanner.nextDouble();
                    actorDao.updateAllActorSalaryByMovieId(movieId, newSalary);
                    break;
                case 8:
                    // Update Actor Nationality by ID
                    System.out.println("Enter actor ID:");
                    int actorId = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    System.out.println("Enter new nationality:");
                    String newNationality = scanner.nextLine();
                    actorDao.updateActorNationalityById(actorId, newNationality);
                    break;
                case 9:
                    // Delete All Actors by Industry
                    System.out.println("Enter industry:");
                    String delIndustry = scanner.nextLine();
                    actorDao.deleteAllActorsByIndustry(delIndustry);
                    break;
                case 10:
                    // Delete All Actors by Movie Name
                    System.out.println("Enter movie name:");
                    String delMovieName = scanner.nextLine();
                    actorDao.deleteAllActorsByMovieName(delMovieName);
                    break;
                case 11:
                    // Exit
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }
}
