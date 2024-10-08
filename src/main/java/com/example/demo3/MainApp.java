package com.example.demo3;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import javafx.util.Duration;

public class MainApp extends Application {
    Integer updatedQty = 0;
    Integer inStockNum = 0;
    Boolean additionalDrugParameter = false;
    Boolean additionalPurchaseParameter = false;
    Boolean additionalSupplyParameter = false;
    int count = 1;
    int buyId = 0;



    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create buttons for the left section
        Button dashboardButton = new Button("Dashboard");
        Button inventoryButton = new Button("Add Drugs");
        Button purchaseButton = new Button("Purchase");
        Button purchaseHistoryButton = new Button("Purchase History");
        Button supplyButton = new Button("Supply");
        Button suppliersButton = new Button("Suppliers");
        Label searchTitle = new Label("Pharm");

        // Set fixed size for the buttons
        dashboardButton.setMaxWidth(Double.MAX_VALUE);
        inventoryButton.setMaxWidth(Double.MAX_VALUE);
        purchaseButton.setMaxWidth(Double.MAX_VALUE);
        purchaseHistoryButton.setMaxWidth(Double.MAX_VALUE);
        supplyButton.setMaxWidth(Double.MAX_VALUE);
        suppliersButton.setMaxWidth(Double.MAX_VALUE);
        dashboardButton.setPrefHeight(50);
        inventoryButton.setPrefHeight(50);
        purchaseButton.setPrefHeight(50);
        purchaseHistoryButton.setPrefHeight(50);
        supplyButton.setPrefHeight(50);
        suppliersButton.setPrefHeight(50);
        searchTitle.setId("searchTitle");

        // Create VBox container for the left section
        VBox leftSection = new VBox(10, searchTitle, dashboardButton, inventoryButton, purchaseHistoryButton, purchaseButton, suppliersButton, supplyButton);
        leftSection.setId("leftSection");

        // Create StackPane container for the right section
        StackPane rightSection = new StackPane();
        rightSection.setId("rightSection");

        // Create containers for different right section content
        VBox dashboardContent = createDashboardContent();
        dashboardContent.setId("dashboardContent");

        VBox inventoryContent = addDrugsComponent();
        inventoryContent.setId("inventoryContent");

        VBox purchaseContent = addPurchaseComponent();
        purchaseContent.setId("purchaseContent");

        VBox purchaseHistoryContent = createPurchaseHistoryContent();
        purchaseHistoryContent.setId("purchaseHistoryContent");

        VBox supplyContent = addSupplierComponent();
        supplyContent.setId("supplyContent");

        VBox suppliersContent = createSuppliersContent();
        suppliersContent.setId("suppliersContent");



        // Set event handlers for buttons
        dashboardButton.setOnAction(event -> {
            rightSection.getChildren().clear();
            loadDrugsApi(dashboardContent, null);
            rightSection.getChildren().add(dashboardComponent(dashboardContent));

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
            loadPurchasesApi(purchaseHistoryContent, null);
            rightSection.getChildren().add(purchaseHistoryComponent(purchaseHistoryContent));
        });

        supplyButton.setOnAction(actionEvent -> {
            rightSection.getChildren().clear();
            rightSection.getChildren().add(supplyContent);
        });

        suppliersButton.setOnAction(actionEvent -> {
            rightSection.getChildren().clear();
            loadSuppliersApi(suppliersContent, null);
            rightSection.getChildren().add(createScrollableSuppliersContent(suppliersContent));
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
        loadDrugsApi(dashboardContent, null);

        loadPurchasesApi(purchaseHistoryContent, null);

        loadSuppliersApi(suppliersContent, null);
    }



// **********Dashboard**********

//    Create the contents for the dashboard component
    private VBox createDashboardContent() {
        Label dashboardTitle = new Label("Dashboard");
        dashboardTitle.setId("dashboard-title");
        TextField searchBar = new TextField();
        searchBar.setPromptText("Search for drugs");
        searchBar.setPrefWidth(480 * 0.6);
        searchBar.setPrefHeight(30);
        searchBar.setId("search-field");
        Button searchButton = new Button("Search");
        searchButton.setPrefHeight(30);
        searchButton.setId("search-btn");
        searchButton.setStyle("-fx-background-color: #44483E");
        HBox searchContainer = new HBox(10, searchBar, searchButton);

        GridPane headerPane = new GridPane();
        headerPane.getStyleClass().add("header-pane"); // Apply CSS class to GridPane

        // Define column constraints to match 1fr 2fr 7fr 1fr 2fr
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(10);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(20);

        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(50);

        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPercentWidth(10);

        ColumnConstraints col5 = new ColumnConstraints();
        col5.setPercentWidth(10);

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
        categoryHeader.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px;");
        headerPane.add(categoryHeader, 4, 0);

        VBox drugList = new VBox(0);
        drugList.setId("drugList"); // Set an ID for drugList

        VBox dashboardContent = new VBox(10, dashboardTitle, searchContainer, headerPane, drugList);
        dashboardContent.setId("dash-content");
        dashboardContent.setPrefHeight(2000);
        searchBar.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if(Objects.equals(searchBar.getText(), "")){
                    additionalDrugParameter = false;
                    loadDrugsApi(dashboardContent, null);
                }
            }
        });
        searchButton.setOnAction(actionEvent -> {
            if(Objects.equals(searchBar.getText(), "")){
                additionalDrugParameter = false;
                loadDrugsApi(dashboardContent, null);
            }
            additionalDrugParameter = true;
            loadDrugsApi(dashboardContent, searchBar.getText());
        });
        VBox dashboardContainer = new VBox(dashboardContent);
        return dashboardContainer;
    }

//    Represents the dashboard component: it wraps content in scrollable container.
    private ScrollPane dashboardComponent(VBox dashboardContent) {
        ScrollPane scrollPane = new ScrollPane(dashboardContent);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

//    Loads drugs from drugs api
    private void loadDrugsApi(VBox dashboardContent, String addedParam) {
        new Thread(() -> {
            try {
                URL url = new URL("http://localhost:8080/drugs");
                if(addedParam == null){
                    url = new URL("http://localhost:8080/drugs");
                }
                if (additionalDrugParameter){
                    url = new URL("http://localhost:8080/drugs/"+ addedParam);
                }
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

//    Updates dashboard content when new drugs load
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
            idData.getStyleClass().add("data-pane-row");
            dataPane.add(idData, 0, i+1);

            Label nameData = new Label(drug.getDrugName());
            nameData.getStyleClass().add("data-pane-row");
            dataPane.add(nameData, 1, i+1);

            Label descriptionData = new Label(drug.getDescription());
            descriptionData.getStyleClass().add("data-pane-row");
            dataPane.add(descriptionData, 2, i+1);

            Label qtyData = new Label(String.valueOf(drug.getQuantity()));
            qtyData.getStyleClass().add("data-pane-row");
            dataPane.add(qtyData, 3, i+1);

            Label categoryData= new Label(String.join(", ", drug.getCategory()));
            categoryData.getStyleClass().add("data-pane-row");
            dataPane.add(categoryData, 4, i+1);
        }

//        VBox node1 = (VBox) dashboardContent.getChildren().getLast();
//            node1.getChildren().;
//        node1.getChildren().add(dataPane);
        VBox node = (VBox) dashboardContent.lookup("#drugList");
        if(node != null) {
            node.getChildren().clear();
            node.getChildren().add(dataPane);
        } else{
            System.out.println("empty node");
        }
    }


// **********ADD DRUGS**********

//    Add Drugs -Component which allows the addition of drugs to dashboard
    public VBox addDrugsComponent() {
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
                addDrugApi(nameValue, descValue, quantityValue, categoryValue, 0, date, time);
                Label label = new Label("Successfully Added");
                label.setTextAlignment(TextAlignment.CENTER);
                label.setStyle("-fx-text-fill: #76ea76; -fx-font-size: 14px;");
                addToInventoryContent.getChildren().add(label);
                nameBar.setText("");
                categoryBar.setText("");
                quantityBar.setText("");
                descBar.setText("");
                controlSuccessMsgDuration(addToInventoryContent, label, Duration.seconds(2));
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

//    Api to add drug to dashboard
    private void addDrugApi(String drugName, String description, int quantity, String category, int buyerId, String date, String time) {
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

//    Displays success message
    private void controlSuccessMsgDuration(VBox parent, Label label, Duration duration) {
        label.setVisible(true); // Show the label
        PauseTransition pause = new PauseTransition(duration);
        pause.setOnFinished(event -> {
            label.setVisible(false);
            parent.getChildren().remove(label);
        }); // Hide the label after the duration
        pause.play();
    }


// **********Purchase History**********

    //    Creates the contents for the purchase history component
    private VBox createPurchaseHistoryContent() {
        Label dashboardTitle = new Label("Purchase History");
        dashboardTitle.setId("dashboard-title");
        TextField searchBar = new TextField();
        searchBar.setPromptText("Search for drugs");
        searchBar.setPrefWidth(480 * 0.6);
        searchBar.setPrefHeight(30);
        searchBar.setId("search-field");

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
        searchBar.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if(Objects.equals(searchBar.getText(), "")){
                    additionalPurchaseParameter = false;
                    loadPurchasesApi(purchaseHistoryContent, null);
                }
            }
        });
        searchButton.setOnAction(actionEvent -> {
            if(Objects.equals(searchBar.getText(), "")){
                additionalPurchaseParameter = false;
                loadPurchasesApi(purchaseHistoryContent, null);
            }
            additionalPurchaseParameter = true;
            loadPurchasesApi(purchaseHistoryContent, searchBar.getText());
        });
        VBox purchaseHistoryContainer = new VBox(purchaseHistoryContent);
        return purchaseHistoryContainer;
    }

    //    Represents the Purchase History component: it wraps content in scrollable container.
    private ScrollPane purchaseHistoryComponent(VBox purchaseHistoryContent) {
        ScrollPane scrollPane = new ScrollPane(purchaseHistoryContent);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    //    Loads purchases from purchase api
    private void loadPurchasesApi(VBox purchaseHistoryContent, String addedParam) {
        new Thread(() -> {
            try {
                URL url = new URL("http://localhost:8080/purchase");
                if(addedParam == null){
                    url = new URL("http://localhost:8080/purchase");
                }
                if (additionalPurchaseParameter == true){
                    url = new URL("http://localhost:8080/purchase/" + addedParam);
                }
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

    //    Updates Purchase History content when new purchases load
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


        VBox node = (VBox) purchaseHistoryContent.lookup("#purchaseList");
        if(node != null) {
            node.getChildren().clear();
            node.getChildren().add(gridPane);
        } else{
            System.out.println("empty node");
        }

    }


//    **********MAKE PURCHASE**********

//  Make purchases -Component which allows the purchase.
    public VBox addPurchaseComponent() {
        Label name = new Label("Name");
        name.setId("inv-name-label");
        Label purchaseQty = new Label("Purchase Qty.");
        purchaseQty.setId("inv-category-label");
        Label inStockQty = new Label("In-stock Qty.");
        inStockQty.setId("inv-quantity-label");
        TextField nameBar = new TextField("Drug Name");
        nameBar.setId("namebar");
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
                Boolean post = false;
                String nameValue = nameBar.getText();
                String qtyValue = purchaseQtyBar.getText();
                getDrugToPurchaseDetail(updatedQty, nameValue, Integer.valueOf(qtyValue), post);
                inStockQtyBar.setText(String.valueOf(inStockNum));
                //System.out.println("Text changed from " + oldValue + " to " + newValue);
                // You can add your logic here to handle the text change event
                // For example, you can make a GET request to fetch the drug details
            }
        });
        add.setOnAction(actionEvent -> {
                    try {
                        Boolean post = true;
                        String nameValue = nameBar.getText();
                        Integer qtyValue = Integer.parseInt(purchaseQtyBar.getText());
                        updatedQty = Integer.parseInt(inStockQtyBar.getText()) - qtyValue;
                        getDrugToPurchaseDetail(updatedQty, nameValue, qtyValue, post);
                        Label label = new Label("Purchase Successful");
                        label.setTextAlignment(TextAlignment.CENTER);
                        label.setStyle("-fx-text-fill: #76ea76; -fx-font-size: 14px;");
                        addToInventoryContent.getChildren().add(label);
                        nameBar.setText("Drug Name");
                        purchaseQtyBar.setText("0");
                        inStockQtyBar.setText("");
                        controlSuccessMsgDuration(addToInventoryContent, label, Duration.seconds(2));
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

//  Finds out whether drug to be purchased exists
    private void getDrugToPurchaseDetail(Integer updatedQty, String drugName, Integer quantityVal, Boolean post) {
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
                    inStockNum = drug.get("quantity").asInt();
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

                System.out.println(foundDrug.get("drugName").asText());

                // Make the POST request to create a purchase
                if(post) {
                    purchaseApi(buyerIdentity, foundDrug, currentDate, currentTime, quantityVal);
                }

                // Update the drug information
                String category = foundDrug.get("category").isArray() ?
                        String.join(", ", objectMapper.convertValue(foundDrug.get("category"), List.class)) :
                        foundDrug.get("category").asText();

                checkForDrugToPurchaseApi(
                        foundDrug.get("id").asInt(),
                        foundDrug.get("drugName").asText(),
                        foundDrug.get("description").asText(),
                        updatedQty,
                        category,
                        buyerIdentity,
                        currentDate,
                        currentTime
                );
            } else {
                inStockNum = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//  Api to get drugs (for checking whether specified drug exists) (Confirm this)
    private void checkForDrugToPurchaseApi(int drugId, String drugName, String description, int quantity, String category, int buyerId, String date, String time) {
        try {
            URL url = new URL("http://localhost:8080/drugs/" + String.valueOf(drugId));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = String.format(
                    "{\"drugName\": \"%s\", \"description\": \"%s\", \"quantity\": %d, \"category\": [\"%s\"], \"buyerId\": %d, \"date\": \"%s\", \"time\": \"%s\"}",
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

    //  Api to make a purchase
    private void purchaseApi(Integer buyerIdentity, JsonNode drug, String currentDate, String currentTime, Integer quantityVal) {
        try {
            URL url = new URL("http://localhost:8080/purchase");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = String.format(
                    "{\"buyerId\": %s, \"drug\": %s, \"quantity\": %s, \"date\": \"%s\", \"time\": \"%s\"}",
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


//   **********Purchase History**********

    //    Creates the contents for the Suppliers Component
    private VBox createSuppliersContent() {
        Label suppliersTitle = new Label("Suppliers");
        suppliersTitle.setId("dashboard-title");
        TextField searchBar = new TextField();
        searchBar.setPromptText("Search for suppliers");
        searchBar.setPrefWidth(480 * 0.6);
        searchBar.setPrefHeight(30);
        searchBar.setId("search-field");
        Button searchButton = new Button("Search");
        searchButton.setPrefHeight(30);
        searchButton.setId("search-btn");
        HBox searchContainer = new HBox(10, searchBar, searchButton);

        GridPane headerPane = new GridPane();
        headerPane.getStyleClass().add("header-pane"); // Apply CSS class to GridPane

        // Define column constraints to match .5fr, 2fr, 1fr, 1fr, 2fr, 1fr, 3fr
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(5); // .5fr equivalent

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(20); // 2fr equivalent

        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(10); // 1fr equivalent

        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPercentWidth(10); // 1fr equivalent

        ColumnConstraints col5 = new ColumnConstraints();
        col5.setPercentWidth(20); // 1fr equivalent

        ColumnConstraints col6 = new ColumnConstraints();
        col6.setPercentWidth(10); // 1fr equivalent

        ColumnConstraints col7 = new ColumnConstraints();
        col7.setPercentWidth(35); // 3fr equivalent

        // Add column constraints to the GridPane
        headerPane.getColumnConstraints().addAll(col1, col2, col3, col4, col5, col6, col7);

        // Create and add labels
        Label idHeader = new Label("Sup. ID");
        idHeader.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
        headerPane.add(idHeader, 0, 0);

        Label nameHeader = new Label("Sup. Name");
        nameHeader.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
        headerPane.add(nameHeader, 1, 0);

        Label drugNameHeader = new Label("Drug Name");
        drugNameHeader.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
        headerPane.add(drugNameHeader, 2, 0);

        Label phoneNumberHeader = new Label("Phone Number");
        phoneNumberHeader.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
        headerPane.add(phoneNumberHeader, 3, 0);

        Label addressHeader = new Label("Street Address");
        addressHeader.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
        headerPane.add(addressHeader, 4, 0);

        Label countryHeader = new Label("Country");
        countryHeader.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
        headerPane.add(countryHeader, 5, 0);

        Label emailHeader = new Label("Email");
        emailHeader.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
        headerPane.add(emailHeader, 6, 0);

        VBox supplierList = new VBox(0);
        supplierList.setId("supplierList"); // Set an ID for supplierList

        VBox suppliersContent = new VBox(10, suppliersTitle, searchContainer, headerPane, supplierList);
        suppliersContent.setId("dash-content");
        suppliersContent.setPrefHeight(2000);

        searchBar.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if(Objects.equals(searchBar.getText(), "")){
                    additionalSupplyParameter = false;
                    loadSuppliersApi(suppliersContent, null);
                }
            }
        });
        searchButton.setOnAction(actionEvent -> {
            if(Objects.equals(searchBar.getText(), "")){
                additionalSupplyParameter = false;
                loadSuppliersApi(suppliersContent, null);
            }
            additionalSupplyParameter = true;
            loadSuppliersApi(suppliersContent, searchBar.getText());
        });

        VBox suppliersContainer = new VBox(suppliersContent);
        return suppliersContainer;
    }

    //    Represents the Suppliers component: it wraps content in scrollable container.
    private ScrollPane createScrollableSuppliersContent(VBox supplierContent) {
        ScrollPane scrollPane = new ScrollPane(supplierContent);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    //    Loads suppliers from suppliers API
    private void loadSuppliersApi(VBox suppliersContent, String addedParam) {
        new Thread(() -> {
            try {
                URL url = new URL("http://localhost:8080/suppliers");
                if(addedParam == null){
                    url = new URL("http://localhost:8080/suppliers");
                }
                if (additionalSupplyParameter){
                    url = new URL("http://localhost:8080/suppliers/"+ addedParam);
                }
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
                Type listType = new TypeToken<List<Supplier>>() {}.getType();
                List<Supplier> suppliers = gson.fromJson(sb.toString(), listType);

                javafx.application.Platform.runLater(() -> updateSuppliersContent(suppliersContent, suppliers));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    //    Updates Suppliers content when new suppliers are loaded
    private void updateSuppliersContent(VBox suppliersContent, List<Supplier> suppliers) {
        GridPane gridPane = new GridPane();

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(5); // 0.5fr equivalent

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(20); // 2fr equivalent

        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(10); // 1fr equivalent

        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPercentWidth(10); // 1fr equivalent

        ColumnConstraints col5 = new ColumnConstraints();
        col5.setPercentWidth(20); // 1fr equivalent

        ColumnConstraints col6 = new ColumnConstraints();
        col6.setPercentWidth(10); // 1fr equivalent

        ColumnConstraints col7 = new ColumnConstraints();
        col7.setPercentWidth(35); // 3fr equivalent

        gridPane.getColumnConstraints().addAll(col1, col2, col3, col4, col5, col6, col7);
        gridPane.getStyleClass().add("data-pane");

        // Add rows for each supplier
        for (int i = 0; i < suppliers.size(); i++) {
            Supplier supplier = suppliers.get(i);

            Label supIdLabel = new Label(String.valueOf(supplier.getSupplierId()));
            supIdLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
            gridPane.add(supIdLabel, 0, i + 1);

            Label supNameLabel = new Label(supplier.getName());
            supNameLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
            gridPane.add(supNameLabel, 1, i + 1);

            Label drugNameLabel = new Label(supplier.getDrug().getDrugName());
            drugNameLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
            gridPane.add(drugNameLabel, 2, i + 1);

            Label phoneNumberLabel = new Label(supplier.getPhone());
            phoneNumberLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
            gridPane.add(phoneNumberLabel, 3, i + 1);

            Label addressLabel = new Label(supplier.getStreetAddress());
            addressLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
            gridPane.add(addressLabel, 4, i + 1);

            Label countryLabel = new Label(supplier.getCountry());
            countryLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
            gridPane.add(countryLabel, 5, i + 1);

            Label emailLabel = new Label(supplier.getEmail());
            emailLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px");
            gridPane.add(emailLabel, 6, i + 1);
        }

        VBox node = (VBox) suppliersContent.lookup("#supplierList");
        if(node != null) {
            node.getChildren().clear();
            node.getChildren().add(gridPane);
        } else{
            System.out.println("empty node");
        }
    }


//        **********Add Supplier**********

    //  Add supplier -Component which allows the addition of a supplier.
    public VBox addSupplierComponent() {
        Label name = new Label("Name");
        name.setId("inv-name-label");
        Label email = new Label("Email");
        email.setId("inv-name-label");
        Label drugName = new Label("Drug name");
        drugName.setId("inv-category-label");
        Label phoneNumber = new Label("Phone Number");
        phoneNumber.setId("inv-quantity-label");
        Label streetAddress = new Label("Street Address");
        streetAddress.setId("inv-category-label");
        Label country = new Label("Country");
        country.setId("inv-category-label");

        TextField nameBar = new TextField();
        nameBar.setId("namebar");
        TextField emailBar = new TextField();
        emailBar.setId("namebar");
        TextField drugNameBar = new TextField();
        drugNameBar.setId("categorybar");
        TextField phoneNumberBar = new TextField();
        phoneNumberBar.setId("categorybar");
        TextField streetAddressBar = new TextField();
        streetAddressBar.setId("categorybar");
        TextField countryBar = new TextField();
        countryBar.setId("categorybar");

        Button add = new Button("Supply");
        add.setId("add-to-inv-btn");

        VBox nameColumn = new VBox(0, name, nameBar);
        VBox emailColumn = new VBox(0, email, emailBar);
        VBox drugNameColumn = new VBox(0, drugName, drugNameBar);
        drugNameColumn.setId("inv-category-column");
        drugNameColumn.setPrefWidth(317);
        VBox phoneNumberColumn = new VBox(0, phoneNumber, phoneNumberBar);
        phoneNumberColumn.setId("inv-quantity-column");
        phoneNumberColumn.setPrefWidth(317);
        VBox streetAddressColumn = new VBox(0, streetAddress, streetAddressBar);
        streetAddressColumn.setId("inv-quantity-column");
        streetAddressColumn.setPrefWidth(317);
        VBox countryColumn = new VBox(0, country, countryBar);
        countryColumn.setId("inv-quantity-column");
        countryColumn.setPrefWidth(317);

        HBox secondRow = new HBox(20, drugNameColumn, phoneNumberColumn);
        HBox thirdRow = new HBox(20, streetAddressColumn, countryColumn);
        secondRow.setId("add-inv-second-row");
        VBox inputBox = new VBox(0, nameColumn, secondRow, thirdRow, emailColumn);
        VBox addToInventoryContent = new VBox(15, inputBox, add);

        add.setOnAction(actionEvent -> {
            try {
                String nameValue = nameBar.getText();
                String emailValue = emailBar.getText();
                String drugNameValue = drugNameBar.getText();
                String phoneNumberValue = phoneNumberBar.getText();
                String streetAddressValue = streetAddressBar.getText();
                String countryValue = countryBar.getText();

                // Fetch the drug details by drug name
                Drug drug = checkForDrugToAddSupplier(drugNameValue);

                if (drug != null) {
                    // Make the POST request to add a supplier
                    addSupplierApi(nameValue, phoneNumberValue, emailValue, streetAddressValue, countryValue, drug);

                    Label label = new Label("Supplier Added Successfully");
                    label.setTextAlignment(TextAlignment.CENTER);
                    label.setStyle("-fx-text-fill: #76ea76; -fx-font-size: 14px;");
                    addToInventoryContent.getChildren().add(label);
                    controlSuccessMsgDuration(addToInventoryContent, label, Duration.seconds(2));
                } else {
                    Label label = new Label("Drug Not Found");
                    label.setTextAlignment(TextAlignment.CENTER);
                    label.setStyle("-fx-text-fill: #ea7676; -fx-font-size: 14px;");
                    addToInventoryContent.getChildren().add(label);
                    controlSuccessMsgDuration(addToInventoryContent, label, Duration.seconds(2));
                }
                nameBar.setText("");
                emailBar.setText("");
                drugNameBar.setText("");
                phoneNumberBar.setText("");
                streetAddressBar.setText("");
                countryBar.setText("");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        addToInventoryContent.setId("add-to-inv-container");
        VBox addToInventoryContainer = new VBox(addToInventoryContent);
        addToInventoryContainer.setStyle("-fx-alignment: center");

        return addToInventoryContainer;
    }

    //  Api to get drugs (for checking whether specified drug exists)
    private Drug checkForDrugToAddSupplier(String drugName) {
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

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode drugs = objectMapper.readTree(content.toString());

            for (JsonNode drug : drugs) {
                if (drug.get("drugName").asText().toLowerCase().equals(drugName.toLowerCase())) {
                    return objectMapper.treeToValue(drug, Drug.class);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //  Api to add a supplier
    private void addSupplierApi(String name, String phone, String email, String streetAddress, String country, Drug drug) {
        try {
            URL url = new URL("http://localhost:8080/suppliers");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String currentDate = now.format(dateFormatter);
            String currentTime = now.format(timeFormatter);
            Integer bId = 20;

            String jsonInputString = String.format(
                    "{\"name\": \"%s\", \"phone\": \"%s\", \"email\": \"%s\", \"streetAddress\": \"%s\", \"country\": \"%s\", \"drug\": {\"id\": %d, \"drugName\": \"%s\", \"description\": \"%s\", \"quantity\": %d, \"category\": [\"%s\"], \"buyerId\": %d, \"date\": \"%s\", \"time\": \"%s\"}}",
                    name, phone, email, streetAddress, country, drug.getId(), drug.getDrugName(), drug.getDescription(), drug.getQuantity(), String.join("\", \"", drug.getCategory()), bId, currentDate, currentTime
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



//    **********Launch Application**********
    public static void main(String[] args) {
        launch();
    }
}

// Drug Class
class Drug {
    private int id;
    private String drugName;
    private String description;
    private int quantity;
    private List<String> category;
    private Integer buyerId;
    private String date;
    private String time;

    public Integer getBuyerId() {
        return buyerId;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

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

//Purchase Class
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

//Supplier Class
class Supplier {
    private int supplierId;
    private String name;
    private String phone;
    private String email;
    private String streetAddress;
    private String country;
    private Drug drug;

    // Getters and setters
    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Drug getDrug() {
        return drug;
    }

    public void setDrug(Drug drug) {
        this.drug = drug;
    }
}
