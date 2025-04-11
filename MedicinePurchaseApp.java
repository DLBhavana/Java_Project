import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

enum PaymentMethod {
    CASH, CHEQUE, CREDIT_CARD, MOBILE_WALLET
}

class Person {
    protected String name;
    protected int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}

class Customer extends Person {
    private String address;

    public Customer(String name, int age, String address) {
        super(name, age);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "Customer Name: " + name + "\nAge: " + age + "\nAddress: " + address;
    }
}

class Medicine {
    private String name;
    private double price;

    public Medicine(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public void displayMedicine() {
        System.out.println("Medicine: " + name + " | Price: ₹" + String.format("%.2f", price));
    }
}

interface MedicinePurchaseInterface {
    void displayAvailableMedicines();

    void addMedicinesToCart(Customer customer, ArrayList<Integer> medicineChoices);

    void removeMedicinesFromCart(Customer customer, ArrayList<Integer> medicineChoices);

    void displayPurchasedItems(Customer customer);

    void displayTotalBill(Customer customer);

    void savePurchaseToFile(Customer customer, Medicine medicine);

    void displayTotalSales();
}

class MedicinePurchaseSystem implements MedicinePurchaseInterface {
    private ArrayList<Medicine> availableMedicines;
    private ArrayList<Medicine> purchasedMedicines;
    private double totalBill;
    private static int totalMedicinesSold = 0;
    private static double totalSalesAmount = 0;

    public MedicinePurchaseSystem() {
        availableMedicines = new ArrayList<>();
        purchasedMedicines = new ArrayList<>();
        totalBill = 0;
        loadMedicines();
    }

    private void loadMedicines() {
        try (BufferedReader br = new BufferedReader(new FileReader("medicines.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String name = parts[0].trim();
                double price = Double.parseDouble(parts[1].trim());
                availableMedicines.add(new Medicine(name, price));
            }
        } catch (IOException e) {
            System.out.println("Error loading medicines from file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error parsing medicine data: " + e.getMessage());
        }
    }

    @Override
    public void displayAvailableMedicines() {
        System.out.println("\nAvailable Medicines:");
        for (int i = 0; i < availableMedicines.size(); i++) {
            System.out.print((i + 1) + ". ");
            availableMedicines.get(i).displayMedicine();
        }
    }

    public void addMedicinesToCart(Customer customer, ArrayList<Integer> medicineChoices) {
        for (int choice : medicineChoices) {
            if (choice < 1 || choice > availableMedicines.size()) {
                System.out.println("Invalid choice: " + choice + ". Skipping...");
                continue;
            }
            Medicine selectedMedicine = availableMedicines.get(choice - 1);
            purchasedMedicines.add(selectedMedicine);
            totalBill += selectedMedicine.getPrice();
            totalMedicinesSold++;
            totalSalesAmount += selectedMedicine.getPrice();
            savePurchaseToFile(customer, selectedMedicine);
            System.out.println("Added " + selectedMedicine.getName() + " to your cart.");
        }
    }

    public void removeMedicinesFromCart(Customer customer, ArrayList<Integer> medicineChoices) {
        for (int choice : medicineChoices) {
            if (choice < 1 || choice > purchasedMedicines.size()) {
                System.out.println("Invalid choice: " + choice + ". Skipping...");
                continue;
            }
            Medicine removedMedicine = purchasedMedicines.remove(choice - 1);
            totalBill -= removedMedicine.getPrice();
            totalMedicinesSold--;
            totalSalesAmount -= removedMedicine.getPrice();
            System.out.println("Removed " + removedMedicine.getName() + " from your cart.");
        }
    }

    public void displayPurchasedItems(Customer customer) {
        System.out.println("\nPurchased Medicines for " + customer.getName() + ":");
        for (int i = 0; i < purchasedMedicines.size(); i++) {
            System.out.print((i + 1) + ". ");
            purchasedMedicines.get(i).displayMedicine();
        }
    }

    public void displayTotalBill(Customer customer) {
        System.out.println("Total Bill for " + customer.getName() + ": ₹" + String.format("%.2f", totalBill));
    }

    public void savePurchaseToFile(Customer customer, Medicine medicine) {
        try (FileWriter writer = new FileWriter("purchase_history.csv", true)) {
            writer.append(customer.getName()).append(",")
                  .append(customer.getAddress()).append(",")
                  .append(medicine.getName()).append(",")
                  .append(String.valueOf(medicine.getPrice())).append("\n");
        } catch (IOException e) {
            System.out.println("Error saving purchase to file: " + e.getMessage());
        }
    }

    public void displayTotalSales() {
        System.out.println("Total Medicines Sold: " + totalMedicinesSold);
        System.out.println("Total Sales Amount: ₹" + String.format("%.2f", totalSalesAmount));
    }

    public void handlePayment(Customer customer) {
        System.out.println("\nSelect Payment Method:");
        System.out.println("1. Cash (Offline)");
        System.out.println("2. Cheque (Offline)");
        System.out.println("3. Credit Card (Online)");
        System.out.println("4. Mobile Wallet (Online)");
        System.out.print("Choose an option (1-4): ");
        Scanner scanner = new Scanner(System.in);
        int paymentChoice = scanner.nextInt();
        switch (paymentChoice) {
            case 1:
                System.out.println("Payment of ₹" + String.format("%.2f", totalBill) + " received in Cash.");
                break;
            case 2:
                System.out.print("Enter Cheque Number: ");
                String chequeNumber = scanner.next();
                System.out.println("Payment of ₹" + String.format("%.2f", totalBill) + " received via Cheque " + chequeNumber + ".");
                break;
            case 3:
                System.out.print("Enter Credit Card Number: ");
                String cardNumber = scanner.next();
                System.out.print("Enter Card Expiry Date (MM/YY): ");
                String expiryDate = scanner.next();
                System.out.println("Payment of ₹" + String.format("%.2f", totalBill) + " received via Credit Card " + cardNumber + ".");
                break;
            case 4:
                System.out.print("Enter Mobile Wallet ID: ");
                String walletId = scanner.next();
                System.out.println("Payment of ₹" + String.format("%.2f", totalBill) + " received via Mobile Wallet " + walletId + ".");
                break;
            default:
                System.out.println("Invalid payment option. Please try again.");
                return;
        }
        totalBill = 0;
        purchasedMedicines.clear();
        System.out.println("Thank you for your purchase, " + customer.getName() + "!");
    }
}

public class MedicinePurchaseApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MedicinePurchaseSystem purchaseSystem = new MedicinePurchaseSystem();
        System.out.println("Welcome to the Medicine Store!");
        
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your age: ");
        int age = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter your address: ");
        String address = scanner.nextLine();
        Customer customer = new Customer(name, age, address);
        
        boolean exit = false;
        while (!exit) {
            System.out.println("\n1. View Available Medicines");
            System.out.println("2. Add Medicines to Cart");
            System.out.println("3. Remove Medicines from Cart");
            System.out.println("4. View Purchased Items");
            System.out.println("5. View Total Bill");
            System.out.println("6. Make Payment");
            System.out.println("7. View Total Sales");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            
            switch (choice) {
                case 1:
                    purchaseSystem.displayAvailableMedicines();
                    break;
                case 2:
                    System.out.print("Enter the numbers of the medicines to add (comma-separated): ");
                    scanner.nextLine(); // Consume newline
                    String[] input = scanner.nextLine().split(",");
                    ArrayList<Integer> addChoices = new ArrayList<>();
                    for (String str : input) {
                        try {
                            addChoices.add(Integer.parseInt(str.trim()));
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input: " + str.trim());
                        }
                    }
                    purchaseSystem.addMedicinesToCart(customer, addChoices);
                    break;
                case 3:
                    System.out.print("Enter the numbers of the medicines to remove (comma-separated): ");
                    scanner.nextLine(); // Consume newline
                    String[] removeInput = scanner.nextLine().split(",");
                    ArrayList<Integer> removeChoices = new ArrayList<>();
                    for (String str : removeInput) {
                        try {
                            removeChoices.add(Integer.parseInt(str.trim()));
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input: " + str.trim());
                        }
                    }
                    purchaseSystem.removeMedicinesFromCart(customer, removeChoices);
                    break;
                case 4:
                    purchaseSystem.displayPurchasedItems(customer);
                    break;
                case 5:
                    purchaseSystem.displayTotalBill(customer);
                    break;
                case 6:
                    purchaseSystem.handlePayment(customer);
                    break;
                case 7:
                    purchaseSystem.displayTotalSales();
                    break;
                case 8:
                    exit = true;
                    System.out.println("Thank you for visiting the Medicine Store!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }
}
