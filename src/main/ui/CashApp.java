package ui;

import model.*;
import model.boosts.Boost;
import model.exceptions.InsufficientFundsException;
import model.exceptions.InvalidCardException;
import persistence.JsonAccountReader;
import persistence.JsonAccountWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;

import static model.BusinessUser.BusinessType.*;

//Cash App
public class CashApp {
    private static final String JSON_ACCOUNT_STORE = "./data/account.json";
    private final Scanner input;
    private User user;
    private JsonAccountReader jsonAccountReader;
    private JsonAccountWriter jsonAccountWriter;

    private final User cashAppUser =
            new BusinessUser("cashapp", "Vancouver, BC", "CashApp", RETAILER);
    private final Account cashAppAccount = new Account(cashAppUser, 1000000.00);

    //EFFECTS: runs the cash app
    public CashApp() {
        input = new Scanner(System.in);
        jsonAccountWriter = new JsonAccountWriter(JSON_ACCOUNT_STORE);
        jsonAccountReader = new JsonAccountReader(JSON_ACCOUNT_STORE);
        runLoginFlow();
        runApp();
    }

    //MODIFY: this
    //EFFECTS: enters app loop to interact with user
    private void runApp() {
        boolean keepGoing = true;
        String command = null;

        while (keepGoing) {
            displayMenu();

            command = input.next();
            command = command.toLowerCase();

            if (command.equals("q")) {
                keepGoing = false;
            } else {
                processCommand(command);
            }
        }

        System.out.println("\nThanks for using CashApp, don't forget to tell your friends!");
    }

    //MODIFY: this
    //EFFECTS: process user input to login or signup
    private void runLoginFlow() {
        displayWelcomeMessage();
        String command = input.next();
        command = command.toLowerCase();
        processLoginFlow(command);
    }

    //MODIFY: this
    //EFFECTS: processes user command to create account or load previous account
    private void processLoginFlow(String command) {
        if (command.equals("s")) {
            runCreateAccountFlow();
        } else if (command.equals("l")) {
            loadAccountFlow();
        } else {
            System.out.println("Please select s or l to login!");
        }
    }

    //MODIFY: this
    //EFFECTS: loads account from JSON file if it exists

    private void loadAccountFlow() {
        try {
            Account account = jsonAccountReader.read();
            user = account.getUser();

            user.setAccount(account);

            System.out.println("Welcome back " + account.getUser().getUsername()
                    + "! Your info was successfully loaded!");
        } catch (IOException e) {
            System.out.println("Oops! We were unable to read from your file: " + JSON_ACCOUNT_STORE);
            runCreateAccountFlow();
        }
    }

    //MODIFY: this
    //EFFECTS: process user input to create new user and account
    private void runCreateAccountFlow() {
        System.out.println("Would you like to create a personal or business account? \n");
        System.out.println("\tp -> personal");
        System.out.println("\tb -> business");
        String command = input.next();
        command = command.toLowerCase();
        processAccountCreation(command);
    }

    //MODIFY: this
    //EFFECTS: processes user command to create account
    private void processAccountCreation(String command) {
        if (command.equals("p")) {
            createPersonalAccount();
        } else if (command.equals("b")) {
            createBusinessAccount();
        } else {
            System.out.println("Please select p or b to create an account!");
        }
    }

    //MODIFY: this
    //EFFECTS: processes user command to run app flows
    private void processCommand(String command) {
        switch (command) {
            case "b":
                runCheckBalanceFlow();
                break;
            case "f":
                runFundAccountFlow();
                break;
            case "w":
                runWithdrawFundsFlow();
                break;
            case "p":
                runMakePurchaseFlow();
                break;
            case "s":
                runSendMoneyFlow();
                break;
            case "r":
                runRequestMoneyFlow();
                break;
            default:
                processExtraCommands(command);
        }
    }

    //MODIFY: this
    //EFFECTS: processes user command to run app flows
    private void processExtraCommands(String command) {
        switch (command) {
            case "m":
                runBoostFlow();
                break;
            case "c":
                runUpdateCreditCardsFlow();
                break;
            case "h":
                runTransactionHistoryFlow();
                break;
            case "a":
                runReferFriendsFlow();
                break;
            case "save":
                saveAccountFlow();
                break;
        }
    }

    //EFFECTS: display welcome message to user
    private void displayWelcomeMessage() {
        System.out.println("\n"
                + "  ______                       __         ______                      \n"
                + " /      \\                     |  \\       /      \\                     \n"
                + "|  $$$$$$\\  ______    _______ | $$____  |  $$$$$$\\  ______    ______  \n"
                + "| $$   \\$$ |      \\  /       \\| $$    \\ | $$__| $$ /      \\  /      \\ \n"
                + "| $$        \\$$$$$$\\|  $$$$$$$| $$$$$$$\\| $$    $$|  $$$$$$\\|  $$$$$$\\\n"
                + "| $$   __  /      $$ \\$$    \\ | $$  | $$| $$$$$$$$| $$  | $$| $$  | $$\n"
                + "| $$__/  \\|  $$$$$$$ _\\$$$$$$\\| $$  | $$| $$  | $$| $$__/ $$| $$__/ $$\n"
                + " \\$$    $$ \\$$    $$|       $$| $$  | $$| $$  | $$| $$    $$| $$    $$\n"
                + "  \\$$$$$$   \\$$$$$$$ \\$$$$$$$  \\$$   \\$$ \\$$   \\$$| $$$$$$$ | $$$$$$$ \n"
                + "                                                  | $$      | $$      \n"
                + "                                                  | $$      | $$      \n"
                + "                                                   \\$$       \\$$      \n");


        System.out.println("Welcome to Cash App '98! \n");
        System.out.println("Our mission is to create an inclusive economy "
                + "by helping you send, receive, and spend money easier \n");
        System.out.println("Would you like to create an account or login? \n");
        System.out.println("\ts -> sign-up");
        System.out.println("\tl -> login");
    }

    //EFFECTS: display menu
    private void displayMenu() {
        System.out.println("\n=======================================================");
        System.out.println("\nHow can we help you today?\n");
        System.out.println("Select from the following options:\n");
        System.out.println("\tb -> check balance");
        System.out.println("\tf -> fund your account");
        System.out.println("\tw -> withdraw funds");
        System.out.println("\tp -> make purchase");
        System.out.println("\ts -> send money");
        System.out.println("\tr -> request money");
        System.out.println("\tc -> update credit cards");
        System.out.println("\tm -> add boosts to earn cashback!");
        System.out.println("\th -> view transaction history");
        System.out.println("\ta -> refer a friend");
        System.out.println("\tq -> quit app");
        System.out.println("\tsave -> save your account changes!");
        System.out.println("\n=======================================================");
    }


    //MODIFY: this
    //EFFECTS: process user command to create personal account
    private void createPersonalAccount() {
        System.out.println("\nWhat username would you like?");
        String username = input.next();
        username = username.toLowerCase();

        System.out.println("\nWhat is your first name?");
        String firstName = input.next();
        firstName = firstName.toLowerCase();

        System.out.println("\nWhat is your last name?");
        String lastName = input.next();
        lastName = lastName.toLowerCase();

        System.out.println("\nWhere are you from?");
        String location = input.next();
        location = location.toLowerCase();

        user = new PersonalUser(username, location, firstName, lastName);

        System.out.println("\nIt's great for you to join us from "
                + location + "!" + " Your username $" + username + " is ready to be used! Woot!");
    }

    //MODIFY: this
    //EFFECTS: process user command to create business account
    private void createBusinessAccount() {
        System.out.println("\nWhat username would you like?");
        String username = input.next();
        username = username.toLowerCase();

        System.out.println("\nWhat is your company name?");
        String companyName = input.next();
        companyName = companyName.toLowerCase();

        System.out.println("\nWhere is your company headquartered?");
        String location = input.next();
        location = location.toLowerCase();

        System.out.println("\nSelect your type of business");
        System.out.println("\tc -> cafe");
        System.out.println("\tg -> grocery");
        System.out.println("\tr -> retailer");
        System.out.println("\te -> restaurant");
        System.out.println("\to -> other");
        String typeInput = input.next();
        typeInput = typeInput.toLowerCase();

        BusinessUser.BusinessType type = processBusinessType(typeInput);
        user = new BusinessUser(username, location, companyName, type);

        System.out.println("\nIt's great for you to join us from "
                + location + "!" + " Time to take " + companyName + " to the next level! Woot!");
    }

    //EFFECTS: process user command to parse out business type
    private BusinessUser.BusinessType processBusinessType(String input) {
        BusinessUser.BusinessType type;
        switch (input) {
            case "c":
                type = CAFE;
                break;
            case "g":
                type = GROCERY;
                break;
            case "r":
                type = RETAILER;
                break;
            case "e":
                type = RESTAURANT;
                break;
            default:
                type = OTHER;
        }
        return type;
    }

    //EFFECTS: Prints account balance
    private void runCheckBalanceFlow() {
        System.out.println("\nYour balance is $" + user.getAccount().getBalance() + ".");
    }

    //MODIFY: this
    //EFFECTS: process user command to deposit into account
    private void runFundAccountFlow() {
        if (user.getAccount().getCreditCards().size() == 0) {
            runAddCreditCardFlow();
        }

        System.out.println("\nHow much would you like to deposit?");
        int depositAmount = input.nextInt();

        CreditCard creditCard = (CreditCard) user.getAccount().getCreditCards().get(0);

        try {
            user.getAccount().deposit(creditCard, depositAmount);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        System.out.println("\nWe have deposited $" + depositAmount + " into your account.");
        System.out.println("\nYour new balance is $" + user.getAccount().getBalance() + ".");
    }

    //MODIFY: this
    //EFFECTS: process user command to withdraw funds from account balance
    private void runWithdrawFundsFlow() {
        if (user.getAccount().getCreditCards().size() == 0) {
            runAddCreditCardFlow();
        }

        System.out.println("\nYour balance is $" + user.getAccount().getBalance() + ".");

        System.out.println("\nHow much would you like to withdraw?");
        int withdrawAmount = input.nextInt();

        CreditCard creditCard = (CreditCard) user.getAccount().getCreditCards().get(0);

        try {
            user.getAccount().withdraw(creditCard, withdrawAmount);
            System.out.println("\nThe withdraw was successful. It make take a few days until you see it on your card.");
        } catch (InvalidCardException e) {
            System.out.println("Darn, looks like you don't have a valid credit card");
        } catch (InsufficientFundsException e) {
            System.out.println("\nDarn, it looks like you don't have enough funds for that!");
        }

    }

    //MODIFY: this
    //EFFECTS: process user command to add boosts to account
    private void runBoostFlow() {
        System.out.println("CashApp '98 allows you to earn money while you spend! Here are available boosts:\n");
        System.out.println("\t1. * High-Roller Boost *: Earn 10% cashback for any purchase over $1000 ");
        System.out.println("\t2. * Shopaholic Boost *: Earn 5% cashback for any retail purchase");
        System.out.println("\t3. * Foodie Boost *: Earn 3% cashback for any purchase at a cafe or restaurant");
        System.out.println("\nYou are allowed 2 boosts to your account!");
        runUpdateBoostFlow();
    }

    //EFFECTS: display boost menu
    private void runDisplayBoostMenu() {
        System.out.println("\n=======================================================");
        System.out.println("\nSelect from the following options:\n");
        System.out.println("\tl -> list boosts");
        System.out.println("\ta -> add boost");
        System.out.println("\td -> delete boost");
        System.out.println("\tm -> main menu");
        System.out.println("\n=======================================================");
    }

    //MODIFY: this
    //EFFECTS: processes user command to run boost flow
    private void processBoostCommand(String command) {
        switch (command) {
            case "l":
                runListBoostFlow();
                break;
            case "a":
                runAddBoostFlow();
                break;
            case "d":
                runDeleteBoostFlow();
                break;
        }
    }

    //MODIFY: this
    //EFFECTS: if list of credit cards not empty, format and print list of credit cards, else prompts user to add card
    private void runListBoostFlow() {
        if (user.getAccount().getBoosts().size() > 0) {
            System.out.println("\nHere are your Boosts:");
            Iterator<Boost> itr = user.getAccount().getBoosts().iterator();

            while (itr.hasNext()) {
                System.out.println(itr.next().getBoostType());
            }

        } else {
            System.out.println("\nYou have no boosts. Would you like to add one? (y / n)");

            String command = input.next();
            command = command.toLowerCase();

            if (command.equals("y")) {
                runAddBoostFlow();
            }
        }
    }

    //MODIFY this
    //EFFECTS: Runs flow to add boosts to list based on user input
    private void runAddBoostFlow() {
        System.out.println("\nEnter the ID of the boost you want to add (ex. 1):");
        System.out.println("\t1. High-Roller Boost");
        System.out.println("\t2. Shopaholic Boost");
        System.out.println("\t3. Foodie Boost");

        int initialBoostSize = user.getAccount().getBoosts().size();

        int boostNum = input.nextInt();

        switch (boostNum) {
            case 1:
                user.getAccount().addBoost(user.getAccount().getHighRollerBoost());
                break;
            case 2:
                user.getAccount().addBoost(user.getAccount().getShopaholicBoost());
                break;
            case 3:
                user.getAccount().addBoost(user.getAccount().getFoodieBoost());
                break;
        }
        if (initialBoostSize != user.getAccount().getBoosts().size()) {
            System.out.println("Your boost has been added successfully!");
        } else {
            System.out.println("Oops! No boost added! Add boost that you don't currently have!");
        }
    }

    //MODIFY this
    //EFFECTS: Runs flow to delete boosts from list based on user input
    private void runDeleteBoostFlow() {
        runListBoostFlow();
        int initialBoostSize = user.getAccount().getBoosts().size();

        System.out.println("\nEnter the ID of the boost you want to remove (ex. 1):");
        System.out.println("\t1. High-Roller Boost");
        System.out.println("\t2. Shopaholic Boost");
        System.out.println("\t3. Foodie Boost");

        int boostNum = input.nextInt();

        switch (boostNum) {
            case 1:
                user.getAccount().removeBoost(user.getAccount().getHighRollerBoost());
                break;
            case 2:
                user.getAccount().removeBoost(user.getAccount().getShopaholicBoost());
                break;
            case 3:
                user.getAccount().removeBoost(user.getAccount().getFoodieBoost());
                break;
        }
        if (initialBoostSize != user.getAccount().getBoosts().size()) {
            System.out.println("Your boost has been removed successfully!");
        } else {
            System.out.println("Oops! No boost removed yet! Remove a boost that you currently have!");
        }

    }

    //MODIFY: this
    //EFFECTS: displays list of options in credit card menu and process user input
    private void runUpdateBoostFlow() {
        boolean keepGoing = true;
        String command = null;

        while (keepGoing) {
            runDisplayBoostMenu();

            command = input.next();
            command = command.toLowerCase();

            if (command.equals("m")) {
                keepGoing = false;
            } else {
                processBoostCommand(command);
            }
        }
    }

    //MODIFY: this
    //EFFECTS: process user command to create and add credit card to list of credit cards
    private void runAddCreditCardFlow() {
        runCheckCreditCardAvailable();
        CreditCard creditCard = new CreditCard("", 0, 0, 0);

        while (!creditCard.getIsValid()) {
            System.out.println("\nWho issued your credit card (VISA, MasterCard)?");
            String creditCardType = input.next();
            creditCardType = creditCardType.toLowerCase();

            System.out.println("\nWhat is your credit card number?");
            int creditCardNumber = input.nextInt();

            System.out.println("\nWhat is your credit card expiry year (ie: 2020)?");
            int creditCardExpiryYear = input.nextInt();

            System.out.println("\nWhat is your credit card expiry month (ie: 6)?");
            int creditCardExpiryMonth = input.nextInt();

            creditCard = new CreditCard(creditCardType, creditCardNumber,
                    creditCardExpiryYear, creditCardExpiryMonth);
            if (creditCard.getIsValid()) {
                user.getAccount().addCreditCard(creditCard);
                System.out.println("\nYour credit card has been added, nice!");
            } else {
                System.out.println("\nHmm... that card isn't valid. Let's try that again.");
            }
        }
    }

    //EFFECTS: check if user has any credit cards on file

    private void runCheckCreditCardAvailable() {
        if (user.getAccount().getCreditCards().size() == 0) {
            System.out.println("\nIt looks like you don't have a credit card on your account. Let's add one now.");
        } else {
            System.out.println("\nLet's add another credit card to your file!");
        }
    }

    //MODIFY: this
    //EFFECTS: process user command to send money to default cashapp user
    private void runSendMoneyFlow() {
        System.out.println("\nWe're glad you want to try out our send money flow.");
        System.out.println("\nCashApp is currently in development so you can only send to our account.");

        // Prompt the user for input but don't use it until we save usernames in a database/file
        System.out.println("\nWhat is the CashApp username you'd like to send to (psst it's $cashapp)?");
        String username = input.next();

        System.out.println("\nHow much would you like to send?");
        int sendAmount = input.nextInt();

        Transaction transaction = user.getAccount().sendMoney(cashAppAccount, sendAmount);

        if (transaction.getStatus() == Transaction.Status.COMPLETE) {
            System.out.println("\nNice, we got the money. Thanks for helping the development of CashApp.");
            System.out.println("\nYour new balance is $" + user.getAccount().getBalance() + ".");
        } else {
            System.out.println("\nSomething went wrong with the transaction. Perhaps you don't have enough Benjamins.");
        }
    }

    //MODIFY: this
    //EFFECTS: process user command to send money to default cashapp user
    private void runMakePurchaseFlow() {
        System.out.println("\nWe're glad you want to make a purchase!");
        System.out.println("\nCashApp is currently in development so you can only send to our account.");

        // Prompt the user for input but don't use it until we save usernames in a database/file
        System.out.println("\nWhat is the CashApp username you'd like to send to (psst it's $cashapp)?");
        String username = input.next();

        System.out.println("\nWhat is your purchase total?");
        int purchaseAmount = input.nextInt();

        Transaction transaction = user.getAccount().makePurchase(cashAppAccount, purchaseAmount);

        if (transaction.getStatus() == Transaction.Status.COMPLETE) {
            System.out.println("\nNice, you successfully made your purchase and eligible for cashback! "
                    + "Thanks for helping the development of CashApp.");
            System.out.println("\nYour new balance is $" + user.getAccount().getBalance() + ".");
        } else {
            System.out.println("\nSomething went wrong with the transaction. Perhaps you don't have enough Benjamins.");
        }
    }

    //MODIFY: this
    //EFFECTS: process user command to request money from default user
    private void runRequestMoneyFlow() {
        System.out.println("\nWe're glad you want to try out our send money flow.");
        System.out.println("\nCashApp is currently in development so you can only request from our account.");

        // Prompt the user for input but don't use it until we save usernames in a database/file
        System.out.println("\nWhat is the CashApp username you'd like to send to (psst it's $cashapp)?");
        String username = input.next();

        System.out.println("\nHow much would you like to request?");
        int requestAmount = input.nextInt();

        Transaction transaction = user.getAccount().requestMoney(cashAppAccount, requestAmount);

        if (transaction.getStatus() == Transaction.Status.FAILED) {
            System.out.println("\nSomething went wrong with the transaction.");
        } else {
            System.out.println("\nYour money request has been processed.");
            System.out.println("\nCheck your transactions to check the status.");
        }
    }

    //EFFECTS: formats transaction header view
    private void printTransactionHeader() {
        System.out.println("DATE\t\tRECIPIENT\t\tSENDER\t\tAMOUNT\t\tSTATUS");
        System.out.println("=======================================================");
    }

    //EFFECTS: format and print single transaction
    private void printTransaction(Transaction transaction) {
        System.out.println(
                transaction.getDate() + "\t\t" + transaction.getRecipientUsername() + "\t\t"
                        + transaction.getSenderUsername() + "\t\t" + transaction.getAmount() + "\t\t"
                        + transaction.getStatus()
        );
    }

    //EFFECTS: prints all pending transactions
    private void printPendingTransactions() {
        if (user.getAccount().getPendingTransactions().size() > 0) {
            System.out.println("\nHere are your PENDING transactions:");

            printTransactionHeader();
            for (int i = 0; i < user.getAccount().getPendingTransactions().size(); i++) {
                Transaction transaction = (Transaction) user.getAccount().getPendingTransactions().get(i);
                printTransaction(transaction);
            }
        }
    }

    //EFFECTS: prints all failed transactions
    private void printFailedTransactions() {
        if (user.getAccount().getFailedTransactions().size() > 0) {
            System.out.println("\nHere are your FAILED transactions:");

            printTransactionHeader();
            for (int i = 0; i < user.getAccount().getFailedTransactions().size(); i++) {
                Transaction transaction = (Transaction) user.getAccount().getFailedTransactions().get(i);
                printTransaction(transaction);
            }
        }
    }

    //EFFECTS: prints all completed transactions

    private void printCompletedTransactions() {
        if (user.getAccount().getCompletedTransactions().size() > 0) {
            System.out.println("\nHere are your COMPLETED transactions:");

            printTransactionHeader();
            for (int i = 0; i < user.getAccount().getCompletedTransactions().size(); i++) {
                Transaction transaction = (Transaction) user.getAccount().getCompletedTransactions().get(i);
                printTransaction(transaction);
            }
        }
    }
    //EFFECTS: prints all pending, failed, and completed transactions for complete history

    private void runTransactionHistoryFlow() {
        printPendingTransactions();
        printFailedTransactions();
        printCompletedTransactions();
    }

    //MODIFY: this
    //EFFECTS: displays list of options in credit card menu and process user input
    private void runUpdateCreditCardsFlow() {
        boolean keepGoing = true;
        String command = null;

        while (keepGoing) {
            displayCreditCardMenu();

            command = input.next();
            command = command.toLowerCase();

            if (command.equals("m")) {
                keepGoing = false;
            } else {
                processCreditCardCommand(command);
            }
        }
    }

    //EFFECTS: display credit card menu
    private void displayCreditCardMenu() {
        System.out.println("\n=======================================================");
        System.out.println("\nSelect from the following options:\n");
        System.out.println("\tl -> list cards");
        System.out.println("\ta -> add card");
        System.out.println("\td -> delete card");
        System.out.println("\tm -> main menu");
        System.out.println("\n=======================================================");
    }

    //MODIFY: this
    //EFFECTS: processes user command to run app flows
    private void processCreditCardCommand(String command) {
        switch (command) {
            case "l":
                runListCreditCardFlow();
                break;
            case "a":
                runAddCreditCardFlow();
                break;
            case "d":
                runDeleteCreditCardFlow();
                break;
        }
    }

    //EFFECTS: print and format credit card list header
    private void printCreditCardHeader() {
        System.out.println("ID\t\tISSUER\t\tCARD NUM\t\tEXPIRY (M/YY)");
        System.out.println("=======================================================");
    }

    //MODIFY: this
    //EFFECTS: if list of credit cards not empty, format and print list of credit cards, else prompts user to add card
    private void runListCreditCardFlow() {
        if (user.getAccount().getCreditCards().size() > 0) {
            System.out.println("\nHere are your credit cards:");

            printCreditCardHeader();
            for (int i = 0; i < user.getAccount().getCreditCards().size(); i++) {
                CreditCard creditCard = (CreditCard) user.getAccount().getCreditCards().get(i);
                System.out.println(
                        i + 1 + ". "
                                + "\t\t " + creditCard.getCardType()
                                + "\t\t" + creditCard.getCardNumber()
                                + "\t\t" + creditCard.getExpiryMonth()
                                + "/" + creditCard.getExpiryYear()
                );
            }
        } else {
            System.out.println("\nYou have no credit cards. Would you like to add one? (y / n)");

            String command = input.next();
            command = command.toLowerCase();

            if (command.equals("y")) {
                runAddCreditCardFlow();
            }
        }
    }

    //MODIFY this
    //EFFECTS: Runs flow to delete credit card from list based on user input
    private void runDeleteCreditCardFlow() {
        runListCreditCardFlow();

        System.out.println("\nEnter the ID of the card you want to remove (ex. 1):");

        int cardNum = input.nextInt();

        if (cardNum > 0 && cardNum <= user.getAccount().getCreditCards().size()) {
            CreditCard creditCard = (CreditCard) user.getAccount().getCreditCards().get(cardNum - 1);
            user.getAccount().deleteCreditCard(creditCard);

            System.out.println("\nYour credit card has been removed.");
        } else {
            System.out.println("\nThat is not a valid credit card!");
        }
    }

    //MODIFY: this
    //EFFECTS: runs flow to refer friends
    private void runReferFriendsFlow() {

        if (user.getUserType() == User.UserType.BUSINESS) {
            System.out.println("Sorry, business users are unable to use this feature."
                    + "Create a personal account for the ability to refer friends!");
        } else {
            int referralsLeft = user.getReferralCountForReward() - user.getReferralCount();
            System.out.println("You have referred " + user.getReferralCount()
                    + " friends so far. Just " + referralsLeft + " more to go for $"
                    + user.getCashBackForReferral() + " cash back reward!");
            System.out.println("Would you like to refer a friend? y/n");
            processReferFriendsCommand();
        }
    }

    //MODIFY: this
    //EFFECTS: processes user command to to refer friends
    private void processReferFriendsCommand() {
        String command = input.next();
        command = command.toLowerCase();
        if (command.equals("y")) {
            runReferAFriendFlow();
        } else {
            displayMenu();
        }
    }

    //MODIFY: this
    //EFFECTS: processes user input to refer friends
    private void runReferAFriendFlow() {
        System.out.println("We can't wait to welcome your friends to Cash App '98!");
        System.out.println("What's their email?");
        String email = input.next();
        email = email.toLowerCase();
        Boolean validReferral = user.referFriend(email);
        int referralsLeft = user.getReferralCountForReward() - user.getReferralCount();

        if (validReferral) {
            System.out.println("Yay! Your friend will receive an email invitation soon. You now have " + referralsLeft
                    + " referrals to go before your reward!");
        }

        if (user.referralReward()) {
            System.out.println("\n Congrats! $" + user.getCashBackForReferral()
                    + " has been deposited into your account!");
        }

    }

    //EFFECTS: saves account activities to file
    private void saveAccountFlow() {
        try {
            jsonAccountWriter.open();
            jsonAccountWriter.write(user.getAccount());
            jsonAccountWriter.close();
            System.out.println("Hooray! Your account info was successfully saved");
        } catch (FileNotFoundException e) {
            System.out.println("Oops! We were unable to save your account activities to: " + JSON_ACCOUNT_STORE);
        }
    }


}
