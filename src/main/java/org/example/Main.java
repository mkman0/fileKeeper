package main.java.org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static Map<String, Keeper> keepers = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);
    private static final String DATA_FILE = "keepers.json";

    public static void main(String[] args) {
        loadKeepersFromJsonFile();

        while (true) {
            System.out.println("\n=== File Keeper CLI ===");
            System.out.println("1. Create a new keeper");
            System.out.println("2. List keepers");
            System.out.println("3. Change keeper properties");
            System.out.println("4. Execute 'keep' method");
            System.out.println("5. Exit");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    createNewKeeper();
                    break;
                case 2:
                    listKeepers();
                    break;
                case 3:
                    changeKeeperProperties();
                    break;
                case 4:
                    executeKeepMethod();
                    break;
                case 5:
                    saveKeepersToJsonFile();
                    System.out.println("Exiting...");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }



    private static void createNewKeeper() {
        System.out.println("\n=== Create New Keeper ===");
        String path = getStringInput("Enter directory path: ");
        int keptFiles = getIntInput("Enter the number of files to keep: ");

        Keeper keeper = new Keeper(path, keptFiles);
        keepers.put(path, keeper);

        System.out.println("Keeper created successfully!");
        System.out.println("Keep in mind that default mode is KEEP_LAST and default sort criteria is BY_CREATION_DATE.");
    }

    private static void listKeepers() {
        System.out.println("\n=== List Keepers ===");
        if (keepers.isEmpty()) {
            System.out.println("No keepers available.");
        } else {
            for (String path : keepers.keySet()) {
                System.out.println("Path: " + path + ", Kept Files: " + keepers.get(path).getKeptFiles() + ", Mode: " + keepers.get(path).getMode() + ", Sort: " + keepers.get(path).getSort());
            }
        }
    }

    private static void changeKeeperProperties() {
        System.out.println("\n=== Change Keeper Properties ===");

        if (keepers.isEmpty()) {
            System.out.println("No keepers available.");
            return;
        }

        // List available keepers
        System.out.println("Available Keepers:");
        int index = 1;
        for (String path : keepers.keySet()) {
            System.out.println(index + ". " + path);
            index++;
        }

        // Choose a keeper
        int keeperChoice = getIntInput("Enter the number of the keeper to modify: ");
        if (keeperChoice >= 1 && keeperChoice <= keepers.size()) {
            String chosenPath = getKeeperPathByIndex(keeperChoice);
            Keeper chosenKeeper = keepers.get(chosenPath);

            // Modify properties of the chosen keeper
            int newKeptFiles = getIntInput("Enter the new number of files to keep: ");
            chosenKeeper.setKeptFiles(newKeptFiles);

            Keeper.Mode newMode = getEnumInput("Enter the new mode (KEEP_FIRST or KEEP_LAST): ", Keeper.Mode.class);
            chosenKeeper.setMode(newMode);

            Keeper.Sort newSort = getEnumInput("Enter the new sort criteria (BY_NAME, BY_MODIFICATION_DATE, BY_SIZE, BY_CREATION_DATE): ", Keeper.Sort.class);
            chosenKeeper.setSort(newSort);

            System.out.println("Keeper properties updated successfully for the chosen keeper.");
        } else {
            System.out.println("Invalid choice. Please enter a valid number.");
        }
    }

    private static void executeKeepMethod() {
        System.out.println("\n=== Execute 'keep' Method ===");

        if (keepers.isEmpty()) {
            System.out.println("No keepers available.");
            return;
        }

        // List available keepers
        System.out.println("Available Keepers:");
        int index = 1;
        for (String path : keepers.keySet()) {
            System.out.println(index + ". " + path);
            index++;
        }

        // Option to execute on all keepers
        System.out.println(index + ". Execute 'keep' method on all keepers");

        // Choose a keeper or option to execute on all
        int choice = getIntInput("Enter the number of the keeper to execute 'keep' method (or " + index + " to execute on all): ");
        if (choice >= 1 && choice <= index) {
            if (choice == index) {
                // Execute 'keep' method on all keepers
                for (Keeper keeper : keepers.values()) {
                    keeper.keep();
                }
                System.out.println("'Keep' method executed successfully for all keepers.");
            } else {
                // Execute 'keep' method on the chosen keeper
                String chosenPath = getKeeperPathByIndex(choice);
                Keeper chosenKeeper = keepers.get(chosenPath);
                chosenKeeper.keep();
                System.out.println("'Keep' method executed successfully for the chosen keeper.");
            }
        } else {
            System.out.println("Invalid choice. Please enter a valid number.");
        }
    }


    private static String getKeeperPathByIndex(int index) {
        int currentIndex = 1;
        for (String path : keepers.keySet()) {
            if (currentIndex == index) {
                return path;
            }
            currentIndex++;
        }
        return null; // Should not reach here if the index is valid
    }

    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextInt();
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.next();
    }

    private static <T extends Enum<T>> T getEnumInput(String prompt, Class<T> enumClass) {
        System.out.print(prompt);
        return Enum.valueOf(enumClass, scanner.next());
    }

    private static void loadKeepersFromTxtFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    String pathsLine = parts[0];
                    String settingsLine = parts[1];

                    Keeper keeper = createKeeperFromLines(pathsLine, settingsLine);
                    if (keeper != null) {
                        keepers.put(keeper.getPath(), keeper);
                    }
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private static Keeper createKeeperFromLines(String pathsLine, String settingsLine) {
        Keeper keeper = null;
        try {
            String[] filePaths = pathsLine.split("\\|");
            int keptFiles = Integer.parseInt(settingsLine.split("\\|")[2]);

            if (filePaths.length > 0) {
                keeper = new Keeper(filePaths[0], keptFiles);
                for (int i = 1; i < filePaths.length; i++) {
                    keeper.getFiles().add(new File(filePaths[i]));
                }
                keeper.setMode(Keeper.Mode.valueOf(settingsLine.split("\\|")[0]));
                keeper.setSort(Keeper.Sort.valueOf(settingsLine.split("\\|")[1]));
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return keeper;
    }


    private static void saveKeepersToJsonFile(){
        try(FileWriter writer = new FileWriter(DATA_FILE)) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Keeper.class, new KeeperSerializer());
            Gson customGson = gsonBuilder.setPrettyPrinting().create();
            String custsomJson = customGson.toJson(keepers);
            writer.write(custsomJson);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadKeepersFromJsonFile(){
        GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(Map.class, new KeeperMapDeserializer());
        gsonBuilder.registerTypeAdapter(Keeper.class , new KeeperDeserializer());
        Gson gson = gsonBuilder.create();
        Type type = new TypeToken<Map<String,Keeper>>(){}.getType();

        try(Reader reader = new FileReader(DATA_FILE)){
            Map<String,Keeper> ks = gson.fromJson(reader,type);
            if(ks != null){
                keepers = ks;
            }

        }catch (IOException e){
            System.err.println("No history found");
        }
    }

}
