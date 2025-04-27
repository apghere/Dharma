//THIRD PROTOTYPE
package com.dharmaapp.hellojavafx;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;

// login page imports
import javafx.application.Platform;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import java.util.Optional;

// sql

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

// Model class for Hindu ritual events
class RitualEvent {
    private LocalDate date;
    private String name;
    private String description;
    private String scriptureReference;
    private String ritualInstructions;

    public RitualEvent(LocalDate date, String name, String description, String scriptureReference, String ritualInstructions) {
        this.date = date;
        this.name = name;
        this.description = description;
        this.scriptureReference = scriptureReference;
        this.ritualInstructions = ritualInstructions;
    }

    public LocalDate getDate() { return date; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getScriptureReference() { return scriptureReference; }
    public String getRitualInstructions() { return ritualInstructions; }
}

// Main application class
public class HelloApplication extends Application {

    // Data structures for ritual events
    private Map<LocalDate, RitualEvent> ritualEventsMap = new HashMap<>();
    private ObservableList<RitualEvent> upcomingEvents = FXCollections.observableArrayList();

    // UI components used across methods
    private Label eventTitleLabel;
    private Label eventDateLabel;
    private TextArea descriptionArea;
    private TextArea scriptureArea;
    private TextArea ritualsArea;
    private ListView<RitualEvent> upcomingRitualsList;

    // Color scheme
    private final String COLOR_PRIMARY = "#FFA500";      // Vibrant orange
    private final String COLOR_SECONDARY = "#800080";    // Purple
    private final String COLOR_ACCENT = "#FF4500";       // Deep orange/red
    private final String COLOR_LIGHT = "#FFF8DC";        // Cream
    private final String COLOR_CALENDAR_HIGHLIGHT = "#FF69B4"; // Pink for calendar highlight

    @Override
    public void start(Stage primaryStage) {

        // Step 1: Show login dialog
        AuthenticationDialog loginDialog = new AuthenticationDialog();
        Optional<Boolean> result = loginDialog.showAndWait();

        // Step 2: Check login result
        if (result.isPresent() && result.get()) {
            // ✅ Successful login: Load the main application
            System.out.println("Login successful!");

            // Load data from the database
            initializeDataFromDatabase();

            // Set up the primary stage
            primaryStage.setTitle("Dharma - Rituals App");

            // Create the main layout
            BorderPane root = new BorderPane();

            // Create the top header
            HBox header = createHeader();
            root.setTop(header);

            // Create the center content with calendar and details
            SplitPane centerContent = createCenterContent();
            root.setCenter(centerContent);

            // Create the bottom status bar
            HBox statusBar = createStatusBar();
            root.setBottom(statusBar);

            // Set the scene
            Scene scene = new Scene(root, 1000, 700);

            // Apply basic styles directly since we're not using a CSS file
            root.setStyle("-fx-font-family: 'Arial';");

            primaryStage.setScene(scene);
            primaryStage.show();

            // Display the first event by default, if available
            if (!upcomingEvents.isEmpty()) {
                displayEventDetails(upcomingEvents.get(0));
            }

        } else {
            // ❌ Failed login: Close the application
            System.out.println("Login failed!");
            Platform.exit();
        }
    }



    /**
     * Connects to the SQL database and loads ritual events into the data structures.
     * Replace 'yourUsername' and 'yourPassword' with your actual database credentials.
     */
    private void initializeDataFromDatabase() {
        String url = "jdbc:mysql:address";
        String dbUsername = "root123";
        String dbPassword = "yourpassword";
        String query = "SELECT name, date, importance, scripture_reference, rituals FROM rituals ORDER BY date";

        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String name = rs.getString("name");
                // Convert SQL Date to LocalDate
                LocalDate date = rs.getDate("date").toLocalDate();
                String importance = rs.getString("importance");
                String scriptureReference = rs.getString("scripture_reference");
                String ritualInstructions = rs.getString("rituals");

                // Create a RitualEvent object from the database row
                RitualEvent event = new RitualEvent(date, name, importance, scriptureReference, ritualInstructions);

                // Populate the data structures
                ritualEventsMap.put(date, event);
                upcomingEvents.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Show an alert if the database connection fails
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Could not load ritual events");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(15, 12, 15, 12));
        header.setSpacing(10);
        header.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-background-radius: 0 0 15 15;");

        Label titleLabel = new Label("Dharma");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: 'Sanskrit Text', 'Arial';");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Search functionality with colorful styling
        TextField searchField = new TextField();
        searchField.setPromptText("Search rituals...");
        searchField.setStyle("-fx-background-radius: 20; -fx-background-color: " + COLOR_LIGHT + ";");

        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: " + COLOR_SECONDARY + "; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-weight: bold;");

        // Simple search action
        searchButton.setOnAction(e -> {
            String searchTerm = searchField.getText().toLowerCase();
            if (searchTerm.isEmpty()) return;

            // Simple search action
            for (RitualEvent event : upcomingEvents) {
                if (event.getName().toLowerCase().contains(searchTerm) ||
                        event.getDescription().toLowerCase().contains(searchTerm)) {  // Fixed line here
                    upcomingRitualsList.getSelectionModel().select(event);
                    displayEventDetails(event);
                    break;
                }
            }
        });


        header.getChildren().addAll(titleLabel, spacer, searchField, searchButton);
        return header;
    }

    private SplitPane createCenterContent() {
        SplitPane splitPane = new SplitPane();

        // Left side - Calendar and list of upcoming events
        VBox calendarSection = createCalendarSection();

        // Right side - Ritual details display
        VBox detailsSection = createDetailsSection();

        splitPane.getItems().addAll(calendarSection, detailsSection);
        splitPane.setDividerPositions(0.3);

        return splitPane;
    }

    private VBox createCalendarSection() {
        VBox calendarSection = new VBox(10);
        calendarSection.setPadding(new Insets(10));
        calendarSection.setStyle("-fx-background-color: linear-gradient(to bottom, " + COLOR_LIGHT + ", white);");

        // Month selection buttons and label
        HBox monthSelection = new HBox(10);
        Button prevMonthBtn = new Button("←");
        prevMonthBtn.setStyle("-fx-background-color: " + COLOR_ACCENT + "; -fx-text-fill: white; -fx-font-weight: bold;");

        Label currentMonthLabel = new Label("April 2025");
        currentMonthLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_SECONDARY + ";");

        Button nextMonthBtn = new Button("→");
        nextMonthBtn.setStyle("-fx-background-color: " + COLOR_ACCENT + "; -fx-text-fill: white; -fx-font-weight: bold;");

        monthSelection.getChildren().addAll(prevMonthBtn, currentMonthLabel, nextMonthBtn);

        // Calendar view with DatePicker
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setShowWeekNumbers(false);
        datePicker.setStyle("-fx-background-color: " + COLOR_LIGHT + "; -fx-background-radius: 5;");

        // Highlight dates with rituals
        datePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        if (!empty && date != null) {
                            if (ritualEventsMap.containsKey(date)) {
                                setStyle("-fx-background-color: " + COLOR_CALENDAR_HIGHLIGHT + "; -fx-text-fill: white; -fx-font-weight: bold;");
                                setTooltip(new Tooltip("Ritual: " + ritualEventsMap.get(date).getName()));
                            }
                        }
                    }
                };
            }
        });

        // When a date is selected, display the corresponding event (if available)
        datePicker.setOnAction(e -> {
            LocalDate selectedDate = datePicker.getValue();
            if (ritualEventsMap.containsKey(selectedDate)) {
                RitualEvent event = ritualEventsMap.get(selectedDate);
                displayEventDetails(event);
                upcomingRitualsList.getSelectionModel().select(event);
            }
        });

        // Upcoming rituals list
        Label upcomingLabel = new Label("Upcoming Rituals");
        upcomingLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_SECONDARY + ";");

        upcomingRitualsList = new ListView<>();
        upcomingRitualsList.setStyle("-fx-background-color: " + COLOR_LIGHT + "; -fx-background-radius: 5;");
        upcomingRitualsList.setCellFactory(param -> new ListCell<RitualEvent>() {
            @Override
            protected void updateItem(RitualEvent item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");
                    setText(item.getDate().format(formatter) + " - " + item.getName());
                    setStyle("-fx-background-color: transparent; -fx-text-fill: " + COLOR_SECONDARY + ";");
                    setOnMouseEntered(event -> setStyle("-fx-background-color: " + COLOR_LIGHT + "; -fx-text-fill: " + COLOR_ACCENT + ";"));
                    setOnMouseExited(event -> setStyle("-fx-background-color: transparent; -fx-text-fill: " + COLOR_SECONDARY + ";"));
                }
            }
        });

        // Set list items and selection handler
        upcomingRitualsList.setItems(upcomingEvents);
        upcomingRitualsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                displayEventDetails(newVal);
            }
        });
        VBox.setVgrow(upcomingRitualsList, Priority.ALWAYS);

        calendarSection.getChildren().addAll(monthSelection, datePicker, upcomingLabel, upcomingRitualsList);
        return calendarSection;
    }

    private VBox createDetailsSection() {
        VBox detailsSection = new VBox(15);
        detailsSection.setPadding(new Insets(15));
        detailsSection.setStyle("-fx-background-color: linear-gradient(to bottom right, white, " + COLOR_LIGHT + ");");

        // Event title
        eventTitleLabel = new Label("Select an Event");
        eventTitleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_PRIMARY + ";");

        // Event date
        eventDateLabel = new Label("");
        eventDateLabel.setStyle("-fx-font-size: 18px; -fx-font-style: italic; -fx-text-fill: " + COLOR_SECONDARY + ";");

        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: " + COLOR_ACCENT + ";");

        // Importance/Description area
        Label descriptionLabel = new Label("Importance:");
        descriptionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + COLOR_SECONDARY + ";");

        descriptionArea = new TextArea();
        descriptionArea.setWrapText(true);
        descriptionArea.setEditable(false);
        descriptionArea.setStyle("-fx-control-inner-background: " + COLOR_LIGHT + "; -fx-border-color: " + COLOR_PRIMARY + "; -fx-border-radius: 5;");

        // Scripture reference area
        Label scriptureLabel = new Label("Scripture Reference:");
        scriptureLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + COLOR_SECONDARY + ";");

        scriptureArea = new TextArea();
        scriptureArea.setWrapText(true);
        scriptureArea.setEditable(false);
        scriptureArea.setStyle("-fx-control-inner-background: " + COLOR_LIGHT + "; -fx-border-color: " + COLOR_PRIMARY + "; -fx-border-radius: 5;");

        // Ritual instructions area
        Label ritualsLabel = new Label("Ritual Instructions:");
        ritualsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + COLOR_SECONDARY + ";");

        ritualsArea = new TextArea();
        ritualsArea.setWrapText(true);
        ritualsArea.setEditable(false);
        ritualsArea.setStyle("-fx-control-inner-background: " + COLOR_LIGHT + "; -fx-border-color: " + COLOR_PRIMARY + "; -fx-border-radius: 5;");

        VBox.setVgrow(descriptionArea, Priority.ALWAYS);
        VBox.setVgrow(scriptureArea, Priority.ALWAYS);
        VBox.setVgrow(ritualsArea, Priority.ALWAYS);

        // Reminder button
        Button reminderButton = new Button("Set Reminder");
        reminderButton.setStyle("-fx-background-color: " + COLOR_ACCENT + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 10 20;");
        reminderButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Reminder Set");
            alert.setHeaderText("Reminder Configured");
            alert.setContentText("You will be reminded of this ritual when the date approaches.");
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle("-fx-background-color: " + COLOR_LIGHT + ";");
            dialogPane.lookupButton(ButtonType.OK).setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white;");
            alert.showAndWait();
        });

        detailsSection.getChildren().addAll(
                eventTitleLabel, eventDateLabel, separator,
                descriptionLabel, descriptionArea,
                scriptureLabel, scriptureArea,
                ritualsLabel, ritualsArea,
                reminderButton
        );

        return detailsSection;
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(5));
        statusBar.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-background-radius: 15 15 0 0;");

        Label statusLabel = new Label("Local Mode • Using live data from the database");
        statusLabel.setStyle("-fx-text-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label versionLabel = new Label("Namah v1.0");
        versionLabel.setStyle("-fx-text-fill: white;");

        statusBar.getChildren().addAll(statusLabel, spacer, versionLabel);
        return statusBar;
    }

    // Method to display event details in the right panel
    private void displayEventDetails(RitualEvent event) {
        if (event == null) return;

        eventTitleLabel.setText(event.getName());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        eventDateLabel.setText(event.getDate().format(formatter));
        descriptionArea.setText(event.getDescription());
        scriptureArea.setText(event.getScriptureReference());
        ritualsArea.setText(event.getRitualInstructions());
    }

//    private BorderPane createCommunityQnASection() {
//        BorderPane qnaPane = new BorderPane();
//        qnaPane.setPadding(new Insets(10));
//
//        // Top: Form to post a new question
//        VBox formBox = new VBox(10);
//        TextField usernameField = new TextField();
//        usernameField.setPromptText("Your Username");
//        TextArea questionField = new TextArea();
//        questionField.setPromptText("Ask a question...");
//        questionField.setPrefRowCount(3);
//        Button postQuestionBtn = new Button("Post Question");
//
//        formBox.getChildren().addAll(new Label("Ask a Question:"), usernameField, questionField, postQuestionBtn);
//        qnaPane.setTop(formBox);
//
//        // Center: ListView to display QnA entries
//        ListView<QnAEntry> qnaListView = new ListView<>();
//        qnaListView.setCellFactory(param -> new ListCell<QnAEntry>() {
//            @Override
//            protected void updateItem(QnAEntry item, boolean empty) {
//                super.updateItem(item, empty);
//                if (empty || item == null) {
//                    setText(null);
//                } else {
//                    String answerText = (item.getAnswer() == null || item.getAnswer().isEmpty()) ? "No answer yet" : item.getAnswer();
//                    setText("Q: " + item.getQuestion() + "\n" +
//                            "Asked by: " + item.getUsername() + " on " + item.getTimestamp() + "\n" +
//                            "A: " + answerText);
//                }
//            }
//        });
//        qnaPane.setCenter(qnaListView);
//
//        // Bottom: Form to post an answer to the selected question
//        HBox answerBox = new HBox(10);
//        TextField answerField = new TextField();
//        answerField.setPromptText("Your Answer");
//        Button postAnswerBtn = new Button("Post Answer");
//        answerBox.getChildren().addAll(answerField, postAnswerBtn);
//        qnaPane.setBottom(answerBox);
//
//        // Instantiate the DAO and load entries
//        CommunityQnADAO qnaDAO = new CommunityQnADAO();
//        refreshQnAEntries(qnaListView, qnaDAO);
//
//        // Action: Post a question
//        postQuestionBtn.setOnAction(e -> {
//            String username = usernameField.getText().trim();
//            String question = questionField.getText().trim();
//            if (!username.isEmpty() && !question.isEmpty()) {
//                qnaDAO.postQuestion(username, question);
//                usernameField.clear();
//                questionField.clear();
//                refreshQnAEntries(qnaListView, qnaDAO);
//            }
//        });
//
//        // Action: Post an answer
//        postAnswerBtn.setOnAction(e -> {
//            QnAEntry selectedEntry = qnaListView.getSelectionModel().getSelectedItem();
//            String answer = answerField.getText().trim();
//            if (selectedEntry != null && !answer.isEmpty()) {
//                qnaDAO.postAnswer(selectedEntry.getId(), answer);
//                answerField.clear();
//                refreshQnAEntries(qnaListView, qnaDAO);
//            }
//        });
//
//        return qnaPane;
//    }
//
//    private void refreshQnAEntries(ListView<QnAEntry> listView, CommunityQnADAO qnaDAO) {
//        listView.getItems().clear();
//        listView.getItems().addAll(qnaDAO.getAllEntries());
//    }



    public static void main(String[] args) {
        launch(args);
    }
}




