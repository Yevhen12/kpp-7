package com.example.demo2;
class Guest implements Runnable {
    public enum GuestType {
        RESERVE_TABLE,
        ORDER_FOOD
    }

    private String name;
    private Restaurant restaurant;
    private GuestType guestType;

    public Guest(String name, Restaurant restaurant, GuestType guestType) {
        this.name = name;
        this.restaurant = restaurant;
        this.guestType = guestType;
    }

    @Override
    public void run() {
        if (GuestType.RESERVE_TABLE.equals(guestType)) {
            reserveTable();
        } else if (GuestType.ORDER_FOOD.equals(guestType)) {
            orderFood();
        }
    }

    private void reserveTable() {
        System.out.println(name + " wants to reserve a table.");
        try {
            restaurant.reserveTable(name);
            // Simulate time taken to reserve a table
            Thread.sleep((long) (Math.random() * 2000 + 1000));
            System.out.println(name + " has reserved a table.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void orderFood() {
        System.out.println(name + " wants to order food.");
        try {
            restaurant.orderFood(name);
            // Simulate time taken to order food
            Thread.sleep((long) (Math.random() * 3000 + 2000));
            System.out.println(name + " has ordered food.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}