package com.example.demo3;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.util.Duration;

public class MainApp extends Application {
    Integer inStockNum = 0;
    int count = 1;
    int buyId = 0;
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create buttons for the left section
        Button dashboardButton = new Button("Dashboard");
        Button inventoryButton = new Button("Inventory");
        Button purchaseButton = new Button("Purchase");
        Button purchaseHistoryButton = new Button("Purchase History");
        Label searchTitle = new Label("Pharm");

        // Set fixed size for the buttons
        dashboardButton.setMaxWidth(Double.MAX_VALUE);
        inventoryButton.setMaxWidth(Double.MAX_VALUE);
        purchaseButton.setMaxWidth(Double.MAX_VALUE);
        purchaseHistoryButton.setMaxWidth(Double.MAX_VALUE);
        dashboardButton.setPrefHeight(50);
        inventoryButton.setPrefHeight(50);
        purchaseButton.setPrefHeight(50);
        purchaseHistoryButton.setPrefHeight(50);
        searchTitle.setId("searchTitle");

        // Create VBox container for the left section
        VBox leftSection = new VBox(10, searchTitle, dashboardButton, inventoryButton, purchaseButton, purchaseHistoryButton);
        leftSection.setId("leftSection");

        // Create StackPane container for the right section
        StackPane rightSection = new StackPane();
        rightSection.setId("rightSection");

        // Create containers for different right section content
        VBox dashboardContent = createDashboardContent();
        dashboardContent.setId("dashboardContent");

        VBox inventoryContent = createAddToInventoryContent();
        inventoryContent.setId("inventoryContent");

        VBox purchaseContent = createAddToPurchaseContent();
        purchaseContent.setId("purchaseContent");

        VBox purchaseHistoryContent = createPurchaseHistoryContent();
        purchaseHistoryContent.setId("purchaseHistoryContent");



        // Set event handlers for buttons
        dashboardButton.setOnAction(event -> {
                rightSection.getChildren().clear();
                rightSection.getChildren().add(createScrollableDashboardContent(dashboardContent));

        });

        inventoryButton.setOnAction(event -> {
            rightSection.getChildren().clear();
            rightSection.getChildren().add(inventoryContent);
        });

        purchaseButton.setOnAction(event -> {
            rightSection.getChildren().clear();
            rightSection.getChildren().add(purchaseContent);
        });

        purchaseHistoryButton.setOnAction(actionEvent -> {
            rightSection.getChildren().clear();
            rightSection.getChildren().add(createScrollablePurchaseHistoryContent(purchaseHistoryContent));
        });

        // Create a SplitPane and add the left and right sections
        SplitPane splitPane = new SplitPane(leftSection, rightSection);
        splitPane.setDividerPositions(0.25);

        // Create a scene and set the stylesheet
        Scene scene = new Scene(splitPane, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/com/example/demo3/styles/styles.css").toExternalForm());

        primaryStage.setTitle("Pharm");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Load data and update the dashboardContent
        loadInventoryData(dashboardContent);

        loadHistoryData(purchaseHistoryContent);
    }

    public VBox createAddToInventoryContent() {
        Label name = new Label("Name");
        name.setId("inv-name-label");
        Label category = new Label("Category");
        category.setId("inv-category-label");
        Label quantity = new Label("Quantity");
        quantity.setId("inv-quantity-label");
        Label desc = new Label("Drug Description");
        desc.setId("inv-desc-label");
        TextField nameBar = new TextField();
        nameBar.setId("namebar");
        TextField categoryBar = new TextField();
        categoryBar.setId("categorybar");
        TextField quantityBar = new TextField();
        quantityBar.setId("quantitybar");
        TextField descBar = new TextField();
        descBar.setId("descbar");
        Button add = new Button("Add");
        add.setId("add-to-inv-btn");
        VBox nameColumn = new VBox(0, name, nameBar);
        VBox categoryColumn = new VBox(0, category, categoryBar);
        categoryColumn.setId("inv-category-column");
        categoryColumn.setPrefWidth(317);
        VBox quantityColumn = new VBox(0, quantity, quantityBar);
        quantityColumn.setId("inv-quantity-column");
        quantityColumn.setPrefWidth(317);
        VBox descColumn = new VBox(0, desc, descBar);
        HBox secondRow = new HBox(20, categoryColumn, quantityColumn);
        secondRow.setId("add-inv-second-row");
        VBox inputBox = new VBox(0, nameColumn, secondRow, descColumn);
        VBox addToInventoryContent = new VBox(15, inputBox, add);
        add.setOnAction(actionEvent -> {
            try {
                String nameValue = nameBar.getText();
                String categoryValue = categoryBar.getText();
                Integer quantityValue = Integer.parseInt(quantityBar.getText());
                String descValue = descBar.getText();
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                String date = now.format(dateFormatter);
                String time = now.format(timeFormatter);
                makePostRequest(nameValue, descValue, quantityValue, categoryValue, 0, date, time);
                Label label = new Label("Successfully Added");
                label.setTextAlignment(TextAlignment.CENTER);
                label.setStyle("-fx-text-fill: #76ea76; -fx-font-size: 14px;");
                addToInventoryContent.getChildren().add(label);
                nameBar.setText("");
                categoryBar.setText("");
                quantityBar.setText("");
                descBar.setText("");
                showLabelForDuration(addToInventoryContent, label, Duration.seconds(2));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        );
        addToInventoryContent.setId("add-to-inv-container");
//        addToInventoryContent.setPrefHeight(730);
        VBox addToInventoryContainer = new VBox(addToInventoryContent);
        addToInventoryContainer.setStyle("-fx-alignment: center");
//        addToInventoryContainer.setPrefHeight(Double.MAX_VALUE);
//        addToInventoryContainer.setVgrow(dashboardContent, Priority.ALWAYS);
        return addToInventoryContainer;
    }
    public VBox createAddToPurchaseContent() {
        Label name = new Label("Name");
        name.setId("inv-name-label");
        Label purchaseQty = new Label("Purchase Qty.");
        purchaseQty.setId("inv-category-label");
        Label inStockQty = new Label("In-stock Qty.");
        inStockQty.setId("inv-quantity-label");
        TextField nameBar = new TextField("Drug Name");
        nameBar.setId("namebar");
//        nameBar.textProperty().addListener(new ChangeListener<String>() {
//
//            @Override
//            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//                //System.out.println("Text changed from " + oldValue + " to " + newValue);
//                label.setText(nameBar.getText());
//                // You can add your logic here to handle the text change event
//                // For example, you can make a GET request to fetch the drug details
//            }
//        });
        TextField purchaseQtyBar = new TextField("0");
        purchaseQtyBar.setId("categorybar");
        TextField inStockQtyBar = new TextField();
        inStockQtyBar.setId("quantitybar");
        inStockQtyBar.setText(String.valueOf(inStockNum));
        inStockQtyBar.setEditable(false);
        Button add = new Button("Purchase");
        add.setId("add-to-inv-btn");
        VBox nameColumn = new VBox(0, name, nameBar);
        VBox purchaseQtyColumn = new VBox(0, purchaseQty, purchaseQtyBar);
        purchaseQtyColumn.setId("inv-category-column");
        purchaseQtyColumn.setPrefWidth(317);
        VBox inStockColumn = new VBox(0, inStockQty, inStockQtyBar);
        inStockColumn.setId("inv-quantity-column");
        inStockColumn.setPrefWidth(317);
        HBox secondRow = new HBox(20, purchaseQtyColumn, inStockColumn);
        secondRow.setId("add-inv-second-row");
        VBox inputBox = new VBox(0, nameColumn, secondRow);
        VBox addToInventoryContent = new VBox(15, inputBox, add);
        nameBar.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String nameValue = nameBar.getText();
                String qtyValue = purchaseQtyBar.getText();
                getDrugDetails(nameValue, Integer.valueOf(qtyValue));
                inStockQtyBar.setText(String.valueOf(inStockNum));
                //System.out.println("Text changed from " + oldValue + " to " + newValue);
                // You can add your logic here to handle the text change event
                // For example, you can make a GET request to fetch the drug details
            }
        });
        add.setOnAction(actionEvent -> {
            try {
                String nameValue = nameBar.getText();
                Integer qtyValue = Integer.parseInt(purchaseQtyBar.getText());
                getDrugDetails(nameValue, qtyValue);
                Label label = new Label("Purchase Successful");
                label.setTextAlignment(TextAlignment.CENTER);
                label.setStyle("-fx-text-fill: #76ea76; -fx-font-size: 14px;");
                addToInventoryContent.getChildren().add(label);
                nameBar.setText("Drug Name");
                purchaseQtyBar.setText("0");
                inStockQtyBar.setText("");
                showLabelForDuration(addToInventoryContent, label, Duration.seconds(2));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        );
        addToInventoryContent.setId("add-to-inv-container");
//        addToInventoryContent.setPrefHeight(730);
        VBox addToInventoryContainer = new VBox(addToInventoryContent);
        addToInventoryContainer.setStyle("-fx-alignment: center");
//        addToInventoryContainer.setPrefHeight(Double.MAX_VALUE);
//        addToInventoryContainer.setVgrow(dashboardContent, Priority.ALWAYS);
        return addToInventoryContainer;
    }

    private void showLabelForDuration(VBox parent, Label label, Duration duration) {
        label.setVisible(true); // Show the label
        PauseTransition pause = new PauseTransition(duration);
        pause.setOnFinished(event -> {
            label.setVisible(false);
            parent.getChildren().remove(label);
        }); // Hide the label after the duration
        pause.play();
    }

    private void getDrugDetails(String drugName, Integer quantityVal) {
        try {
            URL url = new URL("http://localhost:8080/drugs");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            conn.disconnect();

            // Parse the JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode drugs = objectMapper.readTree(content.toString());

            // Find the drug by name
            JsonNode foundDrug = null;
            for (JsonNode drug : drugs) {
                if (drug.get("drugName").asText().toLowerCase().equals(drugName.toLowerCase())) {
                    foundDrug = drug;
                    inStockNum = Integer.parseInt(String.valueOf(drug.get("quantity")));
                    break;
                }
            }

            if (foundDrug != null) {
                // Get current date and time
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                String currentDate = now.format(dateFormatter);
                String currentTime = now.format(timeFormatter);
                buyId += 1;
                Integer buyerIdentity = buyId;

                // Make the POST request to create a purchase
                makePurchasePostRequest(buyerIdentity, foundDrug, currentDate, currentTime, quantityVal);
            } else {
                inStockNum = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makePurchasePostRequest(Integer buyerIdentity, JsonNode drug, String currentDate, String currentTime, Integer quantityVal) {
        try {
            URL url = new URL("http://localhost:8080/purchase");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = String.format(
                    "{\"purchaseId\": 3, \"buyerId\": %s, \"drug\": %s, \"quantity\": %s, \"date\": \"%s\", \"time\": \"%s\"}",
                    buyerIdentity, drug.toString(), quantityVal, currentDate, currentTime
            );

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            System.out.println("Response Code: " + code);

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makePostRequest(String drugName, String description, int quantity, String category, int buyerId, String date, String time) {
        try {
            URL url = new URL("http://localhost:8080/drugs");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = String.format(
                    "{\"drugName\": \"%s\", \"description\": \"%s\", \"quantity\": \"%d\", \"category\": [\"%s\"], \"buyerId\": %d, \"date\": \"%s\", \"time\": \"%s\"}",
                    drugName, description, quantity, category, buyerId, date, time
            );

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            System.out.println("Response Code: " + code);

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createDashboardContent() {
        Label dashboardTitle = new Label("Dashboard");
        dashboardTitle.setId("dashboard-title");
        TextField searchBar = new TextField();
        searchBar.setPromptText("Search for drugs");
        searchBar.setPrefWidth(480 * 0.6);
        searchBar.setPrefHeight(30);
        Button searchButton = new Button("Search");
        searchButton.setPrefHeight(30);
        searchButton.setId("search-btn");
        HBox searchContainer = new HBox(10, searchBar, searchButton);


        GridPane headerPane = new GridPane();
        headerPane.getStyleClass().add("header-pane"); // Apply CSS class to GridPane

        // Define column constraints to match 1fr 2fr 7fr 1fr 2fr
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(10); // 1fr equivalent

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(20); // 2fr equivalent

        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(50); // 7fr equivalent

        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPercentWidth(10); // 1fr equivalent

        ColumnConstraints col5 = new ColumnConstraints();
        col5.setPercentWidth(10); // 2fr equivalent

        // Add column constraints to the GridPane
        headerPane.getColumnConstraints().addAll(col1, col2, col3, col4, col5);

        // Create and add labels
        Label idHeader = new Label("ID");
        idHeader.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
        headerPane.add(idHeader, 0, 0);

        Label nameHeader = new Label("Name");
        nameHeader.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
        headerPane.add(nameHeader, 1, 0);

        Label descriptionHeader = new Label("Description");
        descriptionHeader.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
        headerPane.add(descriptionHeader, 2, 0);

        Label qtyHeader = new Label("Qty");
        qtyHeader.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
        headerPane.add(qtyHeader, 3, 0);

        Label categoryHeader = new Label("Category");
        categoryHeader.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
        headerPane.add(categoryHeader, 4, 0);



        VBox drugList = new VBox(0);
        drugList.setId("drugList"); // Set an ID for drugList

        VBox dashboardContent = new VBox(10, dashboardTitle, searchContainer,headerPane, drugList);
        dashboardContent.setId("dash-content");
        dashboardContent.setPrefHeight(2000);
        VBox dashboardContainer = new VBox(dashboardContent);
        return dashboardContainer;
    }

    private ScrollPane createScrollableDashboardContent(VBox dashboardContent) {
        ScrollPane scrollPane = new ScrollPane(dashboardContent);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private void loadInventoryData(VBox dashboardContent) {
        new Thread(() -> {
            try {
                URL url = new URL("http://localhost:8080/drugs");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

                StringBuilder sb = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }

                conn.disconnect();

                Gson gson = new Gson();
                Type listType = new TypeToken<List<Drug>>() {}.getType();
                List<Drug> drugs = gson.fromJson(sb.toString(), listType);

                javafx.application.Platform.runLater(() -> updateDashboardContent(dashboardContent, drugs));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateDashboardContent(VBox dashboardContent, List<Drug> drugs) {

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(10); // 1fr equivalent

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(20); // 2fr equivalent

        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(50); // 7fr equivalent

        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPercentWidth(10); // 1fr equivalent

        ColumnConstraints col5 = new ColumnConstraints();
        col5.setPercentWidth(10); // 2fr equivalent

        GridPane dataPane = new GridPane();
        dataPane.setStyle("    -fx-padding: 10px;\n" +
                "    -fx-background-color: #74796D;\n" +
                "    -fx-vgap: 10px;\n" +
                "    /*-fx-alignment: center-left;*/\n" +
                "    -fx-border-radius: 15px;\n" +
                "    -fx-background-radius: 15px;");
        dataPane.getStyleClass().add("data-pane"); // Apply CSS class to GridPane
        dataPane.getColumnConstraints().addAll(col1, col2, col3, col4, col5);



        // Add rows for each drug
        for (int i = 0; i < drugs.size(); i++) {
            Drug drug = drugs.get(i);

            Label idData= new Label(String.valueOf(drug.getId()));
            idData.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
            dataPane.add(idData, 0, i+1);

            Label nameData = new Label(drug.getDrugName());
            nameData.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
            dataPane.add(nameData, 1, i+1);

            Label descriptionData = new Label(drug.getDescription());
            descriptionData.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
            dataPane.add(descriptionData, 2, i+1);

            Label qtyData = new Label(String.valueOf(drug.getQuantity()));
            qtyData.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
            dataPane.add(qtyData, 3, i+1);

            Label categoryData= new Label(String.join(", ", drug.getCategory()));
            categoryData.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
            dataPane.add(categoryData, 4, i+1);

        }

        VBox node1 = (VBox) dashboardContent.getChildren().getLast();
        node1.getChildren().add(dataPane);

        //I need to set drugList to add dataPane
//        for (javafx.scene.Node node : dashboardContent.getChildren()) {
//            if (node instanceof VBox) {
//                VBox vbox = (VBox) node;
//                if ("drugList".equals(vbox.getId())) {
//                    vbox.getChildren().add(dataPane);
//                    break;
//                }
//            }
//        }
    }

    private String truncateDescription(String description) {
        String[] words = description.split(" ");
        if (words.length > 7) {
            return String.join(" ", java.util.Arrays.copyOfRange(words, 0, 7)) + "...";
        }
        return description;
    }

    private VBox createPurchaseHistoryContent() {
        Label dashboardTitle = new Label("Purchase History");
        dashboardTitle.setId("dashboard-title");
        TextField searchBar = new TextField();
        searchBar.setPromptText("Search for drugs");
        searchBar.setPrefWidth(480 * 0.6);
        searchBar.setPrefHeight(30);
        Button searchButton = new Button("Search");
        searchButton.setPrefHeight(30);
        searchButton.setId("search-btn");
        HBox searchContainer = new HBox(10, searchBar, searchButton);


        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("header-pane"); // Apply CSS class to GridPane

// Define column constraints to match 1fr 2fr 1fr 2fr 2fr 2fr 1fr
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(10); // 1fr equivalent

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(20); // 2fr equivalent

        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(10); // 1fr equivalent

        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPercentWidth(20); // 2fr equivalent

        ColumnConstraints col5 = new ColumnConstraints();
        col5.setPercentWidth(20); // 2fr equivalent

        ColumnConstraints col6 = new ColumnConstraints();
        col6.setPercentWidth(20); // 2fr equivalent

        ColumnConstraints col7 = new ColumnConstraints();
        col7.setPercentWidth(10); // 1fr equivalent

// Add column constraints to the GridPane
        gridPane.getColumnConstraints().addAll(col1, col2, col3, col4, col5, col6, col7);

// Create and add labels
        Label idHeader = new Label("Purch. ID");
        idHeader.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
        gridPane.add(idHeader, 0, 0);

        Label nameHeader = new Label("Drug Name");
        nameHeader.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
        gridPane.add(nameHeader, 1, 0);

        Label quantity = new Label("Qty.");
        quantity.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
        gridPane.add(quantity, 2, 0);

        Label category = new Label("Category");
        category.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
        gridPane.add(category, 3, 0);

        Label date = new Label("Date");
        date.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
        gridPane.add(date, 4, 0);

        Label time = new Label("Time");
        time.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
        gridPane.add(time, 5, 0);

        Label buyerId = new Label("Buyer Id");
        buyerId.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
        gridPane.add(buyerId, 6, 0);


        VBox purchaseList = new VBox(0);
        purchaseList.setId("purchaseList"); // Set an ID for drugList

        VBox purchaseHistoryContent = new VBox(10, dashboardTitle, searchContainer,gridPane, purchaseList);
        purchaseHistoryContent.setId("dash-content");
        purchaseHistoryContent.setPrefHeight(2000);
        VBox purchaseHistoryContainer = new VBox(purchaseHistoryContent);
        return purchaseHistoryContainer;
    }

    private ScrollPane createScrollablePurchaseHistoryContent(VBox purchaseHistoryContent) {
        ScrollPane scrollPane = new ScrollPane(purchaseHistoryContent);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }


    private void loadHistoryData(VBox purchaseHistoryContent) {
        new Thread(() -> {
            try {
                URL url = new URL("http://localhost:8080/purchase");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

                StringBuilder sb = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }

                conn.disconnect();

                Gson gson = new Gson();
                Type listType = new TypeToken<List<Purchase>>() {}.getType();
                List<Purchase> purchases = gson.fromJson(sb.toString(), listType);

                javafx.application.Platform.runLater(() -> updatePurchaseHistoryContent(purchaseHistoryContent, purchases));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void updatePurchaseHistoryContent(VBox purchaseHistoryContent, List<Purchase> purchases) {
        GridPane gridPane = new GridPane();

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(10); // 1fr equivalent

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(20); // 2fr equivalent

        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(10); // 1fr equivalent

        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPercentWidth(20); // 2fr equivalent

        ColumnConstraints col5 = new ColumnConstraints();
        col5.setPercentWidth(20); // 2fr equivalent

        ColumnConstraints col6 = new ColumnConstraints();
        col6.setPercentWidth(20); // 2fr equivalent

        ColumnConstraints col7 = new ColumnConstraints();
        col7.setPercentWidth(10); // 1fr equivalent

        gridPane.getColumnConstraints().addAll(col1, col2, col3, col4, col5, col6, col7);
        gridPane.getStyleClass().add("data-pane"); // Apply CSS class to GridPane

        // Add rows for each drug
        for (int i = 0; i < purchases.size(); i++) {
            Purchase purchase = purchases.get(i);

            Label idLabel = new Label(String.valueOf(purchase.getPurchaseId()));
            idLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
            gridPane.add(idLabel, 0, i + 1);

            Label nameLabel = new Label(purchase.getDrug().getDrugName());
            nameLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
            gridPane.add(nameLabel, 1, i + 1);

            Label quantityLabel = new Label(purchase.getQuantity().toString());
            quantityLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
            gridPane.add(quantityLabel, 2, i + 1);

            Label categoryLabel = new Label(String.join(", ", purchase.getDrug().getCategory()));
            categoryLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
            gridPane.add(categoryLabel, 3, i + 1);

            Label dateLabel = new Label(purchase.getDate().toString());
            dateLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
            gridPane.add(dateLabel, 4, i + 1);

            Label timeLabel = new Label(purchase.getTime().toString());
            timeLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
            gridPane.add(timeLabel, 5, i + 1);

        }

//        purchaseHistoryContent.getChildren().add(gridPane);
//        VBox node1 = (VBox) dashboardContent.getChildren().getLast();
//        node1.getChildren().add(dataPane);

        VBox node2 = (VBox) purchaseHistoryContent.getChildren().getLast();
        node2.getChildren().add(gridPane);

    }


    public static void main(String[] args) {
        launch();
    }
}

// Drug class
class Drug {
    private int id;
    private String drugName;
    private String description;
    private int quantity;
    private List<String> category;

    public int getId() {
        return id;
    }

    public String getDrugName() {
        return drugName;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public List<String> getCategory() {
        return category;
    }
}

class Purchase {
    private Integer purchaseId;
    private Integer buyerId;
    private Drug drug;
    private Integer quantity;

    private String date;
    private String time;

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public Integer getPurchaseId() {
        return purchaseId;
    }

    public Integer getBuyerId() {
        return buyerId;
    }

    public Drug getDrug() {
        return drug;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
