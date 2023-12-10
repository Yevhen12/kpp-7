package com.example.demo2;

import java.util.concurrent.Semaphore;

class Restaurant {
    private final int tableCount;
    private final int waiterCount;
    private final int maxTablesPerWaiter;

    private Semaphore tables;
    private Semaphore waiters;

    public Restaurant(int tableCount, int waiterCount, int maxTablesPerWaiter) {
        this.tableCount = tableCount;
        this.waiterCount = waiterCount;
        this.maxTablesPerWaiter = maxTablesPerWaiter;

        tables = new Semaphore(tableCount, true);
        waiters = new Semaphore(waiterCount, true);
    }

    public boolean reserveTable(String guestName) {
        try {
            tables.acquire();
            System.out.println(guestName + " reserved a table.");
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void releaseTable() {
        tables.release();
    }

    public void orderFood(String guestName) {
        try {
            waiters.acquire();
            System.out.println(guestName + " ordered food.");
            // Simulate waiter preparing food
            Thread.sleep(2000);
            System.out.println(guestName + "'s food is ready.");
            waiters.release();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
