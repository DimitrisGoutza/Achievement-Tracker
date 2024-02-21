package com.achievementtracker.service;

import com.achievementtracker.dao.AchievementDAO;
import com.achievementtracker.dao.CategorizedGameDAO;
import com.achievementtracker.dao.CategoryDAO;
import com.achievementtracker.dao.GameDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class DatabaseStateManager implements CommandLineRunner {
    private final GameDAO gameDAO;
    private final AchievementDAO achievementDAO;
    private final CategoryDAO categoryDAO;
    private final CategorizedGameDAO categorizedGameDAO;
    private final DatabaseInitializer databaseInitializer;

    @Autowired
    DatabaseStateManager(GameDAO gameDAO, AchievementDAO achievementDAO, CategoryDAO categoryDAO,
                                   CategorizedGameDAO categorizedGameDAO, DatabaseInitializer databaseInitializer) {
        this.gameDAO = gameDAO;
        this.achievementDAO = achievementDAO;
        this.categoryDAO = categoryDAO;
        this.categorizedGameDAO = categorizedGameDAO;
        this.databaseInitializer = databaseInitializer;
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("\n\nScanning Database ..");
        boolean DatabaseIsEmpty = (gameDAO.getCount() == 0 && achievementDAO.getCount() == 0 &&
                                categoryDAO.getCount() == 0 && categorizedGameDAO.getCount() == 0);

        Scanner scanner = new Scanner(System.in);
        if (DatabaseIsEmpty) {
            String answer;
            boolean answerIsNotValid = false;
            do {
                System.out.println("---------------------------------------------");
                System.out.println("Database is empty! Do you wish to initialize?   y/n");
                System.out.print("Answer: ");
                answer = scanner.nextLine();
                answerIsNotValid = (!answer.equalsIgnoreCase("y") && !answer.equalsIgnoreCase("n"));
            } while (answerIsNotValid);

            if (answer.equalsIgnoreCase("y")) {
                int choice;
                do {
                    System.out.println("---------------------------------------------");
                    System.out.println("How many game entries to insert?");
                    System.out.println("\t0. Cancel\n" +
                            "\t1. 100\n" +
                            "\t2. 1,000\n" +
                            "\t3. 10,000\n" +
                            "\t4. 30,000\n" +
                            "\t5. All Steam Games");
                    System.out.print("Answer: ");
                    choice = scanner.nextInt();
                } while (choice < 0 || choice > 5);

                if (choice != 0) {
                    long amount = 0;
                    switch (choice) {
                        case 1 -> amount = 100;
                        case 2 -> amount = 1000;
                        case 3 -> amount = 10000;
                        case 4 -> amount = 30000;
                        case 5 -> amount = Long.MAX_VALUE;
                    }

                    System.out.println("\nInitializing Database ..");
                    databaseInitializer.initialize(amount);
                    System.out.println("\n\nDone!");
                }
                run();
            } else {
                System.out.println("---------------------------------------------");
                System.out.println("Database remains empty!");
            }
        } else {
            long gameRecordsCount = gameDAO.getCount();
            long achievementRecordsCount = achievementDAO.getCount();
            long categoryRecordsCount = categoryDAO.getCount();
            int choice;
            do {
                System.out.println("Database is populated with entries!\n\t-> " +gameRecordsCount+ " game(s)\n\t-> " +
                        achievementRecordsCount+ " achievements\n\t-> " +
                        categoryRecordsCount+ " categories");
                System.out.println("---------------------------------------------");
                System.out.println("Do you wish to: \n" +
                        "\t1. Do nothing.\n" +
                        "\t2. Insert more entries (choose total amount).\n" +
                        "\t3. Delete all records.\n");
                System.out.print("Answer: ");
                choice = scanner.nextInt();
                // clean input buffer
                scanner.nextLine();
            } while (choice < 1 || choice > 3);

            int choiceForAmount;
            switch (choice) {
                case 1 -> {
                    return;
                }
                case 2 -> {
                    do {
                        System.out.println("---------------------------------------------");
                        System.out.println("Choose approximate total amount\nPresent game records: " + gameRecordsCount);
                        System.out.println("\t0. Cancel");
                        System.out.println(gameRecordsCount < 100 ? "\t1. 100" : "");
                        System.out.println(gameRecordsCount < 1000 ? "\t2. 1,000" : "");
                        System.out.println(gameRecordsCount < 10000 ? "\t3. 10,000" : "");
                        System.out.println(gameRecordsCount < 30000 ? "\t4. 30,000" : "");
                        System.out.println("\t5. All Steam Games");
                        System.out.print("Answer: ");
                        choiceForAmount = scanner.nextInt();
                        // clean input buffer
                        scanner.nextLine();
                    } while (choiceForAmount < 0 || choiceForAmount > 5);

                    if (choiceForAmount != 0) {
                        long amount = 0;
                        switch (choiceForAmount) {
                            case 1 -> amount = 100;
                            case 2 -> amount = 1000;
                            case 3 -> amount = 10000;
                            case 4 -> amount = 30000;
                            case 5 -> amount = Long.MAX_VALUE;
                        }
                        System.out.println("\nPersisting more entries ..");
                        databaseInitializer.initialize(amount);
                        System.out.println("\n\nDone!");
                    }
                    run();
                }
                case 3 -> {
                    boolean answerIsNotValid = false;
                    String answer;
                    do {
                        System.out.println("---------------------------------------------");
                        System.out.println("Are you sure you wish to delete all records?  y/n");
                        System.out.print("Answer: ");
                        answer = scanner.nextLine();
                        answerIsNotValid = (!answer.equalsIgnoreCase("y") && !answer.equalsIgnoreCase("n"));
                    } while (answerIsNotValid);
                    if (answer.equalsIgnoreCase("y")) {
                        // also cascades deletion to achievements and join table
                        gameDAO.removeAll();
                        categoryDAO.removeAll();
                    }
                    run();
                }
            }
        }
        scanner.close();
    }
}