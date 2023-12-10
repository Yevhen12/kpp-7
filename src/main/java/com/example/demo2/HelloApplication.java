package com.example.demo2;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class HelloApplication extends Application {
    private VBox root;
    private static final int MAX_THREADS = 10;
    private List<ThreadInfoPane> threadInfoPanes = new ArrayList<>();
    private Restaurant restaurant;

    @Override
    public void start(Stage primaryStage) {
        root = new VBox(10);
        Scene scene = new Scene(root, 600, 400);

        addControls();

        primaryStage.setTitle("Thread Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addControls() {
        HBox controls = new HBox(10);

        TextField threadCountField = new TextField("1");
        threadCountField.setPrefColumnCount(3);

        Button createButton = new Button("Create");
        createButton.setOnAction(event -> createThreads(Integer.parseInt(threadCountField.getText())));

        Button stopAllButton = new Button("Stop All");
        stopAllButton.setOnAction(event -> stopAllThreads());

        Button startRestaurantButton = new Button("Start Restaurant");
        startRestaurantButton.setOnAction(event -> startRestaurant());

        controls.getChildren().addAll(
                new Label("Thread Count:"), threadCountField,
                createButton, stopAllButton, startRestaurantButton);

        root.getChildren().add(controls);
    }

    private void createThreads(int count) {
        if (count < 1 || count > MAX_THREADS) {
            return;
        }

        for (int i = 0; i < count; i++) {
            ThreadInfoPane threadInfoPane = new ThreadInfoPane("Thread " + (i + 1));
            threadInfoPanes.add(threadInfoPane);
            root.getChildren().add(threadInfoPane);
            threadInfoPane.startThread();
        }
    }

    private void stopAllThreads() {
        threadInfoPanes.forEach(ThreadInfoPane::stopThread);
    }

    private void startRestaurant() {
        restaurant = new Restaurant(5, 2, 2); // 2 waiters

        // Start guests
        for (int i = 0; i < 5; i++) {
            threadInfoPanes.get(i).setGuestType(Guest.GuestType.RESERVE_TABLE);
            threadInfoPanes.get(i).setRestaurant(restaurant);
            new Thread(threadInfoPanes.get(i)).start();
        }

        // Start waiters
        for (int i = 5; i < 7; i++) {
            threadInfoPanes.get(i).setGuestType(Guest.GuestType.ORDER_FOOD);
            threadInfoPanes.get(i).setRestaurant(restaurant);
            new Thread(threadInfoPanes.get(i)).start();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }

    private class ThreadInfoPane extends VBox implements Runnable {
        private final Label nameLabel;
        private final Label stateLabel;
        private final Label priorityLabel;
        private final Label timeLabel;

        private Thread thread;
        private Button pauseResumeButton;
        private volatile boolean paused = false;

        private Restaurant restaurant;
        private Guest.GuestType guestType;

        public ThreadInfoPane(String name) {
            nameLabel = new Label("Name: " + name);
            stateLabel = new Label("State: ");
            priorityLabel = new Label("Priority: ");
            timeLabel = new Label("Time: ");

            pauseResumeButton = new Button("Pause");
            pauseResumeButton.setDisable(true);
            pauseResumeButton.setOnAction(event -> togglePause());

            getChildren().addAll(nameLabel, stateLabel, priorityLabel, timeLabel, pauseResumeButton);
        }

        public void startThread() {
            if (thread != null) {
                thread.interrupt();
            }

            thread = new Thread(this);
            thread.setPriority((int) (Math.random() * 10) + 1);
            thread.start();

            pauseResumeButton.setDisable(false);
        }

        public void stopThread() {
            if (thread != null) {
                thread.interrupt();
                pauseResumeButton.setDisable(true);
            }
        }

        public synchronized void togglePause() {
            paused = !paused;

            if (!paused) {
                notify();
            }

            pauseResumeButton.setText(paused ? "Resume" : "Pause");
        }

        public void setRestaurant(Restaurant restaurant) {
            this.restaurant = restaurant;
        }

        public void setGuestType(Guest.GuestType guestType) {
            this.guestType = guestType;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    synchronized (this) {
                        while (paused) {
                            wait();
                        }
                    }

                    Platform.runLater(() -> {
                        stateLabel.setText("State: " + thread.getState());
                        priorityLabel.setText("Priority: " + thread.getPriority());
                        timeLabel.setText("Time: " + System.currentTimeMillis());
                    });

                    // Simulate guest or waiter behavior
                    if (guestType == Guest.GuestType.RESERVE_TABLE) {
                        restaurant.reserveTable(nameLabel.getText());
                        // Simulate time taken to reserve a table
                        Thread.sleep((long) (Math.random() * 2000 + 1000));
                        restaurant.releaseTable();
                    } else if (guestType == Guest.GuestType.ORDER_FOOD) {
                        restaurant.orderFood(nameLabel.getText());
                        // Simulate time taken to order food
                        Thread.sleep((long) (Math.random() * 3000 + 2000));
                    }

                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted");
                Thread.currentThread().interrupt();
            }
        }
    }
}