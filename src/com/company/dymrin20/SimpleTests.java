package com.company.dymrin20;

public class SimpleTests {

    @BeforeSuite
    void setUp() {
        System.out.println("Setting up ...");
    }

    @AfterSuite
    void tearDown() {
        System.out.println("Tearing down ...");
    }

    @Test
    void doTestOne() {
        System.out.println(1);
    }

    @Test(order = 5)
    void doTestFive() {
        System.out.println(5);
    }

    @Test(order = 3)
    void doTestThree() {
        System.out.println(3);
    }
}
