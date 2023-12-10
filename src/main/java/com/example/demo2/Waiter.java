package com.example.demo2;

class Waiter implements Runnable {
    private final Restaurant restaurant;

    public Waiter(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                restaurant.reserveTable("Waiter");
                // Simulate time taken to serve a table
                Thread.sleep(3000);
                restaurant.releaseTable();

                // Simulate time taken to prepare food
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            System.out.println("Waiter interrupted");
            Thread.currentThread().interrupt();
        }
    }
}