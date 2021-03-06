package model;


import model.boosts.*;
import model.exceptions.InsufficientFundsException;
import model.exceptions.InvalidCardException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {
    private Account testAccountA;
    private Account testAccountB;
    private Account testBusinessAccount;
    private User testUserA;
    private User testUserB;
    private BusinessUser testBusinessUser;
    private CreditCard testCard;
    private Boost highRoller;
    private Boost shopaholic;
    private Boost foodie;

    @BeforeEach
    void setUp() {
        testUserA = new PersonalUser("$alicelovescake", "Vancouver", "Alice", "Zhao");
        testCard = new CreditCard("Visa", 123456, 2025, 12);
        testBusinessUser = new BusinessUser(
                "$amazon", "Seattle", "Amazon", BusinessUser.BusinessType.RETAILER);
        testAccountA = new Account(testUserA, 100.50);
        testBusinessAccount = new Account(testBusinessUser, 5000);
        testUserB = new PersonalUser
                ("$moneymaker", "Toronto", "Bob", "Marley");
        testAccountB = new Account(testUserB, 100);
        highRoller = new HighRollerBoost();
        shopaholic = new ShopaholicBoost();
        foodie = new FoodieBoost();
    }

    @Test
    void testConstructor() {
        assertEquals(testUserA, testAccountA.getUser());
        assertEquals(100.50, testAccountA.getBalance());
        assertTrue(testAccountA.getId().length() > 0);
    }

    @Test
    void testDeposit() {
        testAccountA.addCreditCard(testCard);
        try {
            testAccountA.deposit(testCard, 500.50);
            //pass
        } catch (Exception e) {
            fail("should not have caught invalid card exception");
        }

        assertEquals(601.0, testAccountA.getBalance());
    }

    @Test
    void testDepositInvalidCreditCard() {
        CreditCard invalidCard = new CreditCard("Visa", 2324, 1990, 1);
        testAccountA.addCreditCard(invalidCard);
        try {
            testAccountA.deposit(invalidCard, 500.50);
            fail("Should have caught invalid card exception");
        } catch (Exception e) {
            //pass
        }

        assertEquals(100.50, testAccountA.getBalance());
    }

    @Test
    void testWithdrawSufficientFunds() {
        testAccountA.addCreditCard(testCard);
        try {
            assertTrue(testAccountA.withdraw(testCard, 50.50));
        } catch (Exception e){
            fail ("Should not have caught exception");
        }

        assertEquals(50.0, testAccountA.getBalance());
    }

    @Test
    void testMultipleDeposits() {
        testAccountA.addCreditCard(testCard);
        try {
            testAccountA.deposit(testCard, 500.50);
            testAccountA.deposit(testCard, 10.50);
            //pass
        } catch (Exception e) {
            fail("should not have caught invalid card exception");
        }

        assertEquals(611.50, testAccountA.getBalance());
    }

    @Test
    void testMultipleWithdrawInsufficientFunds() {
        testAccountA.addCreditCard(testCard);
        try {
            assertTrue(testAccountA.withdraw(testCard, 10.00));
        } catch (Exception e ){
            fail("No Exception here");
        }

        assertEquals(90.50, testAccountA.getBalance());
        try {
            assertTrue(testAccountA.withdraw(testCard, 20.00));
        } catch (Exception e ){
            fail("No Exception here");
        }

        assertEquals(70.50, testAccountA.getBalance());

        try {
            testAccountA.withdraw(testCard, 100.00);
        } catch (InsufficientFundsException e ){
            //pass
        } catch (InvalidCardException e) {
            fail("card should be valid");
        }

        assertEquals(70.50, testAccountA.getBalance());
    }

    @Test
    void testWithdrawlInvalidCard() {
        CreditCard testExpiredYearCard = new CreditCard
                ("Visa", 17897, 1999, 11);
        testAccountA.addCreditCard(testExpiredYearCard);
        try {
            testAccountA.withdraw(testExpiredYearCard, 100.00);
        } catch (InsufficientFundsException e ){
            fail("funds should be valid");
        } catch (InvalidCardException e) {
            //pass
        }
    }

    @Test
    void testSendMoney() {
        testAccountA.sendMoney(testAccountB, 50);
        //Both accountA and B should have added a new transaction to their respective list
        assertEquals(1, testAccountA.getCompletedTransactions().size());
        assertEquals(1, testAccountB.getCompletedTransactions().size());
        //Balance updated
        assertEquals(50.50, testAccountA.getBalance());
        assertEquals(150, testAccountB.getBalance());
    }

    @Test
    void testSendMoneyMultipleInsufficientFunds() {
        testAccountA.sendMoney(testAccountB, 60.0);
        testAccountA.sendMoney(testAccountB, 200.0);

        assertEquals(1, testAccountA.getCompletedTransactions().size());
        assertEquals(1, testAccountB.getCompletedTransactions().size());
        assertEquals(1, testAccountA.getFailedTransactions().size());
        assertEquals(1, testAccountB.getFailedTransactions().size());
        assertEquals(40.50, testAccountA.getBalance());
        assertEquals(160, testAccountB.getBalance());

    }

    @Test
    void testReceiveMoney() {
        testAccountA.receiveMoney(50.0);
        assertEquals(150.50, testAccountA.getBalance());
        testAccountA.receiveMoney(200.0);
        assertEquals(350.50, testAccountA.getBalance());
    }

    @Test
    void testMakePurchase() {
        testAccountA.makePurchase(testBusinessAccount, 50);
        assertEquals(1, testAccountA.getCompletedTransactions().size());
        assertEquals(50.50, testAccountA.getBalance());
        assertEquals(5050.0, testBusinessAccount.getBalance());
    }

    @Test
    void testMakeMultiplePurchaseInsufficientFunds() {
        testAccountA.makePurchase(testBusinessAccount, 10.00);
        testAccountA.makePurchase(testBusinessAccount, 1000.00);
        assertEquals(1, testAccountA.getCompletedTransactions().size());
        assertEquals(1, testAccountA.getFailedTransactions().size());
        assertEquals(1, testBusinessAccount.getCompletedTransactions().size());
        assertEquals(1, testBusinessAccount.getFailedTransactions().size());
        assertEquals(90.50, testAccountA.getBalance());
        assertEquals(5010, testBusinessAccount.getBalance());
    }

    @Test
    void testMakeRequest() {
        Transaction transaction = testAccountA.requestMoney(testAccountB, 200);
        assertTrue(testAccountA.getPendingTransactions().contains(transaction));
        assertEquals(1, testAccountB.getPendingTransactions().size());

        testAccountA.removeFromTransactions(transaction);
        assertFalse(testAccountA.getPendingTransactions().contains(transaction));
        testAccountA.addToTransactions(transaction);
        assertTrue(testAccountA.getPendingTransactions().contains(transaction));
    }

    @Test
    void testMakeMultipleRequests() {
        //sets up additional users/accounts to request money from
        User testUserC = new PersonalUser
                ("$moneymaker", "Toronto", "Bob", "Marley");
        Account testAccountC = new Account(testUserC, 500);

        //test account makes money request to test account 2
        testAccountA.requestMoney(testAccountB, 200);
        testAccountA.requestMoney(testAccountC, 500);
        testAccountA.requestMoney(testAccountB, 700);
        testAccountA.sendMoney(testAccountB, 1);
        assertEquals(3, testAccountA.getPendingTransactions().size());
        assertEquals(2, testAccountB.getPendingTransactions().size());
        assertEquals(1, testAccountC.getPendingTransactions().size());
        assertEquals(1, testAccountA.getCompletedTransactions().size());

    }

    @Test
    void testAddCreditCard() {
        testAccountA.addCreditCard(testCard);
        assertEquals(1, testAccountA.getCreditCards().size());
    }

    @Test
    void testAddInvalidCreditCard() {
        CreditCard testCard2 = new CreditCard("Visa", 123456, 2000, 12);
        testAccountA.addCreditCard(testCard2);
        assertEquals(0, testAccountA.getCreditCards().size());
    }

    @Test
    void testAddMultipleCreditCards() {
        CreditCard testCard2 = new CreditCard("Mastercard", 45679, 2300, 11);
        testAccountA.addCreditCard(testCard);
        testAccountA.addCreditCard(testCard2);
        assertEquals(2, testAccountA.getCreditCards().size());
    }

    @Test
    void testRemoveCreditCard() {
        testAccountA.addCreditCard(testCard);
        testAccountA.deleteCreditCard(testCard);
        assertEquals(0, testAccountA.getCreditCards().size());
    }

    @Test
    void testRemoveMultipleCreditCards() {
        CreditCard testCard2 = new CreditCard("Mastercard", 45679, 2300, 11);
        CreditCard testCard3 = new CreditCard("American Express", 1245679, 2100, 10);
        testAccountA.addCreditCard(testCard);
        testAccountA.addCreditCard(testCard2);
        testAccountA.addCreditCard(testCard3);
        testAccountA.deleteCreditCard(testCard3);
        testAccountA.deleteCreditCard(testCard);

        assertEquals(1, testAccountA.getCreditCards().size());
        assertTrue(testAccountA.getCreditCards().contains(testCard2));
    }

    @Test
    void testAddSingleBoost() {
        assertTrue(testAccountA.getBoosts().isEmpty());
        assertTrue(testAccountA.addBoost(highRoller));
        assertTrue(testAccountA.getBoosts().size() == 1);
        assertTrue(testAccountA.getBoosts().contains(highRoller));
    }

    @Test
    void testAddMultipleBoosts() {
        assertTrue(testAccountA.getBoosts().isEmpty());
        assertTrue(testAccountA.addBoost(highRoller));
        assertTrue(testAccountA.getBoosts().size() == 1);
        assertTrue(testAccountA.getBoosts().contains(highRoller));

        assertTrue(testAccountA.addBoost(shopaholic));
        assertTrue(testAccountA.getBoosts().size() == 2);
        assertTrue(testAccountA.getBoosts().contains(shopaholic));

        assertFalse(testAccountA.addBoost(foodie));
        assertTrue(testAccountA.getBoosts().size() == 2);
        assertFalse(testAccountA.getBoosts().contains(foodie));
    }

    @Test
    void testRemoveSingleBoost() {
        assertTrue(testAccountA.addBoost(highRoller));
        assertTrue(testAccountA.addBoost(shopaholic));
        assertTrue(testAccountA.removeBoost(shopaholic));
        assertTrue(testAccountA.getBoosts().size() == 1);
        assertFalse(testAccountA.getBoosts().contains(shopaholic));
    }

    @Test
    void testRemoveAllBoosts() {
        assertTrue(testAccountA.addBoost(highRoller));
        assertTrue(testAccountA.addBoost(shopaholic));
        assertTrue(testAccountA.removeBoost(shopaholic));
        assertTrue(testAccountA.getBoosts().size() == 1);
        assertFalse(testAccountA.getBoosts().contains(shopaholic));

        assertTrue(testAccountA.removeBoost(highRoller));
        assertTrue(testAccountA.getBoosts().isEmpty());
    }

    @Test
    void testRemoveBoostsNotExist() {
        assertTrue(testAccountA.addBoost(highRoller));
        assertTrue(testAccountA.addBoost(shopaholic));
        assertFalse(testAccountA.removeBoost(foodie));
        assertTrue(testAccountA.getBoosts().size() == 2);
        assertTrue(testAccountA.getBoosts().contains(shopaholic));
        assertTrue(testAccountA.getBoosts().contains(highRoller));
    }

    @Test
    void testMakePurchaseBoost() {
        testAccountA.addBoost(highRoller);
        try {
            testAccountA.deposit(testCard, 1000);
        } catch (Exception e) {
            fail("Should not have caught invalid card exception");
        }

        testAccountA.makePurchase(testBusinessAccount, 1000);
        assertEquals(200.5, testAccountA.getBalance());
    }

    @Test
    void testMakePurchaseNoBoost() {
        testAccountA.addBoost(highRoller);
        try {
            testAccountA.deposit(testCard, 1000);
            // pass
        } catch (Exception e) {
            fail("Should not have caught invalid card exception");
        }

        testAccountA.makePurchase(testBusinessAccount, 999);
        assertEquals(101.5, testAccountA.getBalance());
    }
}

