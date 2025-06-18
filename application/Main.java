package application;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main extends Application {

	private TableView<Student> studentTable;
	private TableView<Major> majorTable;
	private ObservableList<Student> studentsData = FXCollections.observableArrayList();
	private ObservableList<Major> majorsData = FXCollections.observableArrayList();

	private TableView<StudentAdmission> admissionTable;
	private ObservableList<StudentAdmission> admissionsData = FXCollections.observableArrayList();
	private ObservableList<Student> rejectedStudentsData = FXCollections.observableArrayList();
	private TableView<Student> rejectedStudentsTable;

//	private BorderPane pane = new BorderPane();
	private BorderPane studentPane;
	private BorderPane majorPane;

	private Label labelWelcome;
	private TextArea rejectedStudentsTextArea = new TextArea();

//	private StudentLinkedList studentList;
	private MajorLinkedList majorList = new MajorLinkedList();
	private AdmissionSystem admissionSystem = new AdmissionSystem();
	private StudentLinkedList studentList = new StudentLinkedList();

	private File studentFilePath = null;
	private File majorFilePath = null;

	private boolean isMajorFileLoaded = false; // Diversity represents the loading status of specialization files

	//// ---------------------------------------------------------------------

	@Override
	public void start(Stage primaryStage) {
		// Call the welcome window first
		showWelcomeScreen(primaryStage);
	}

	private void showWelcomeScreen(Stage primaryStage) {

		VBox welcomePane = new VBox(20);
		welcomePane.setAlignment(Pos.CENTER);
		welcomePane.setPadding(new Insets(20));
		welcomePane.setStyle("-fx-background-color: linear-gradient(to bottom, #acb6e5, #86fde8);"
				+ "-fx-border-color: #4CAF50; -fx-border-width: 3; -fx-border-radius: 15;"
				+ "-fx-background-radius: 15;");

		Label welcomeLabel = new Label("Welcome to the student and majors management system");
		welcomeLabel.setStyle(
				"-fx-font-size: 24px; -fx-font-weight: bold; -fx-font-style: italic; " + "-fx-text-fill: #333333;");

		Button enterButton = new Button("Go to the program -->");
		enterButton.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; "
				+ "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 20;");

		butoonEffect(enterButton);

		enterButton.setOnAction(e -> {

			showMainApplication(primaryStage);
		});

		welcomePane.getChildren().addAll(welcomeLabel, enterButton);

		Scene welcomeScene = new Scene(welcomePane, 800, 500);
		primaryStage.setScene(welcomeScene);
		primaryStage.setTitle("Student Admission Management System");
		primaryStage.show();
	}

	private void showMainApplication(Stage primaryStage) {
		BorderPane mainLayout = new BorderPane();

		Button saveStudentsButton = new Button("Save Students");
		Button saveMajorsButton = new Button("Save Majors");
		Button backButton = new Button("<-- Back to Welcome Screen");

		butoonEffect(saveStudentsButton);
		butoonEffect(saveMajorsButton);
		butoonEffect(backButton);

		saveMajorsButton.setOnAction(e -> {
			try {
				if (majorFilePath == null) {
					FileChooser fileChooser = new FileChooser();
					majorFilePath = fileChooser.showSaveDialog(primaryStage);
				}
				if (majorFilePath != null) {
					saveMajorsToFile(majorFilePath);
					showAlert("Save", "Majors data has been saved successfully.");
				} else {
					showAlert("Save Error", "Major file not loaded. Cannot save majors data.");
				}
			} catch (IOException ex) {
				showErrorAlert("Save Error", "Failed to save majors data",
						"An error occurred while saving majors data.");
				ex.printStackTrace();
			}
		});

		saveStudentsButton.setOnAction(e -> {
			try {
				if (studentFilePath == null) {
					FileChooser fileChooser = new FileChooser();
					studentFilePath = fileChooser.showSaveDialog(primaryStage);
				}
				if (studentFilePath != null) {
					saveStudentsToFile(studentFilePath);
					showAlert("Save", "Students data has been saved successfully.");
				} else {
					showAlert("Save Error", "Student file not loaded. Cannot save students data.");
				}
			} catch (IOException ex) {
				showErrorAlert("Save Error", "Failed to save students data",
						"An error occurred while saving students data.");
				ex.printStackTrace();
			}
		});

		backButton.setOnAction(e -> {
			showWelcomeScreen(primaryStage);
		});

		HBox backButtonBox = new HBox(backButton);
		backButtonBox.setAlignment(Pos.CENTER_LEFT);
		backButtonBox.setPadding(new Insets(10));

		VBox saveButtonsBox = new VBox(10, saveMajorsButton, saveStudentsButton);
		saveButtonsBox.setAlignment(Pos.TOP_RIGHT);
		saveButtonsBox.setPadding(new Insets(10));

		BorderPane topBar = new BorderPane();
		topBar.setLeft(backButtonBox);
		topBar.setRight(saveButtonsBox);
		topBar.setStyle("-fx-background-color: linear-gradient(to right, #ff9a9e, #fad0c4);");

		// Create tabs
		TabPane tabPane = new TabPane();
		Tab majorTab = createMajorManagementTab(primaryStage);
		Tab studentTab = createStudentManagementTab(primaryStage);
		Tab admissionTab = createAdmissionManagementTab();
		Tab statsTab = createStatisticsTab();
		Tab rejectedStudentsTab = createRejectedStudentsTab();
		tabPane.setStyle("-fx-background-color: linear-gradient(to bottom, #ffecd2, #fcb69f);");

		tabPane.getTabs().addAll(majorTab, studentTab, admissionTab, statsTab, rejectedStudentsTab);

		// Place topBar at the top and TabPane in the middle
		mainLayout.setTop(topBar);
		mainLayout.setCenter(tabPane);

		Scene scene = new Scene(mainLayout, 1200, 700);
		primaryStage.setScene(scene);
		tabPane.setPrefSize(200, 800);
		primaryStage.setTitle("Student and Major Management System");
		primaryStage.setScene(scene);
		primaryStage.setResizable(true);
		primaryStage.show();
	}

	//// ---------------------------------------------------------------------

	private Tab createMajorManagementTab(Stage primaryStage) {

		Tab tab = new Tab("Major Management");
		tab.setStyle("-fx-background-color: linear-gradient(to bottom, #acb6e5, #86fde8);");

		majorPane = new BorderPane();
		majorPane.setStyle("-fx-background-color: linear-gradient(to right, #f5f7fa, #c3cfe2);");

		MenuBar menuBar = new MenuBar();
		menuBar.setStyle("-fx-background-color: #6A5ACD; -fx-text-fill: white;");

		Menu fileMenu = new Menu("File");
		fileMenu.setStyle("-fx-background-color: linear-gradient(to right, #fce38a, #f38181);");

		MenuItem openFileItem = new MenuItem("Open Major File");
		openFileItem.setStyle("-fx-background-color: linear-gradient(to right, #fce38a, #f38181);");
		openFileItem.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showOpenDialog(primaryStage);
			if (file != null) {
				readMajorFile(file);
			}
		});
		fileMenu.getItems().add(openFileItem);

		Menu operationsMenu = new Menu("Operations");
		operationsMenu.setStyle("-fx-background-color: linear-gradient(to right, #d4fc79, #96e6a1);");

		MenuItem insertMajorItem = new MenuItem("Insert Major");
		MenuItem deleteMajorItem = new MenuItem("Delete Major");
		MenuItem updateMajorItem = new MenuItem("Update Major");
		MenuItem searchMajorItem = new MenuItem("Search Major");
		MenuItem viewStudentsInMajorItem = new MenuItem("View Students in Major");

		String operationItemStyle = "-fx-background-color: linear-gradient(to right, #d4fc79, #96e6a1);";
		insertMajorItem.setStyle(operationItemStyle);
		deleteMajorItem.setStyle(operationItemStyle);
		updateMajorItem.setStyle(operationItemStyle);
		searchMajorItem.setStyle(operationItemStyle);
		viewStudentsInMajorItem.setStyle(operationItemStyle);

		insertMajorItem.setOnAction(e -> insertMajor());
		deleteMajorItem.setOnAction(e -> deleteMajor());
		updateMajorItem.setOnAction(e -> updateMajor());
		searchMajorItem.setOnAction(e -> searchMajor());
		viewStudentsInMajorItem.setOnAction(e -> showStudentView());

		operationsMenu.getItems().addAll(insertMajorItem, deleteMajorItem, updateMajorItem, searchMajorItem,
				viewStudentsInMajorItem);
		menuBar.getMenus().addAll(fileMenu, operationsMenu);

		majorTable = new TableView<>();
		majorTable.setStyle("-fx-background-color: linear-gradient(to right, #acb6e5, #86fde8);");
		majorTable.setRowFactory(tv -> new TableRow<Major>() {
			@Override
			protected void updateItem(Major major, boolean empty) {
				super.updateItem(major, empty);
				if (empty || major == null) {

					setStyle("-fx-background-color: linear-gradient(to right, #acb6e5, #86fde8);");
				} else {

					setStyle("-fx-background-color: rgba(233, 245, 232, 0.4);");
				}
			}
		});
		majorTable.setItems(majorsData);
		setupMajorTable();

		HBox mainContent = new HBox(20, majorTable);
		mainContent.setAlignment(Pos.CENTER_LEFT);
		mainContent.setPadding(new Insets(15));
		mainContent.setStyle("-fx-background-color: #f5f7fa; -fx-border-radius: 5;");
		mainContent.setStyle("-fx-background-color: #f0f4f8; -fx-border-radius: 10;");

		majorPane.setTop(menuBar);
		majorPane.setCenter(mainContent);

		tab.setContent(majorPane);
		return tab;
	}

	private void setupMajorTable() {
		majorTable.setPrefWidth(670);

		TableColumn<Major, String> secondarySchoolWeightColumn = new TableColumn<>("Secondary School Grade Weight");
		secondarySchoolWeightColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getTawjihiWeight())));
		secondarySchoolWeightColumn.setPrefWidth(220);

		TableColumn<Major, String> placementWeightColumn = new TableColumn<>("Placement Test Grade Weight");
		placementWeightColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getPlacementWeight())));
		placementWeightColumn.setPrefWidth(200);

		TableColumn<Major, String> acceptanceGradeColumn = new TableColumn<>("Acceptance Grade");
		acceptanceGradeColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getAcceptanceGrade())));
		acceptanceGradeColumn.setPrefWidth(150);

		TableColumn<Major, String> majorNameColumn = new TableColumn<>("Major");
		majorNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
		majorNameColumn.setPrefWidth(130);

		majorTable.getColumns().setAll(secondarySchoolWeightColumn, placementWeightColumn, acceptanceGradeColumn,
				majorNameColumn);
	}

	private void readMajorFile(File file) {
		try (Scanner in = new Scanner(file)) {
			if (in.hasNextLine()) {
				String header = in.nextLine();
				if (!header
						.equals("Secondary School Grade Weight|Placement Test Grade Weight|Acceptance Grade|Major")) {
					showAlert("File Error",
							"This is not the correct file format, Please select the correct Major file.");
					return;
				}
			}
			while (in.hasNextLine()) {
				String line = in.nextLine();
				if (line.isEmpty())/// if the exist line empty , complete to read file
					continue;

				String[] splitLine = line.split("\\|");
				if (splitLine.length < 4)
					continue;

				String name = splitLine[3].trim();
				double secondarySchoolWeight = Double.parseDouble(splitLine[0]);
				double placementTestWeight = Double.parseDouble(splitLine[1]);
				double acceptanceGrade = Double.parseDouble(splitLine[2]);

				/// make search if the name major exist before ,
				if (majorList.searchMajor(name) != null)
					continue;

				Major major = new Major(name, acceptanceGrade, secondarySchoolWeight, placementTestWeight);
				majorList.insertMajor(major);
				majorsData.add(major);
			}
			sortMajorsAlphabeticallyManual();

			refreshTableFromLinkedList();
			majorTable.refresh();
			isMajorFileLoaded = true;

		} catch (IOException | NumberFormatException e) {
			showErrorAlert("Error", "File Read Error", "Failed to read major data from the file.");
			e.printStackTrace();
		}
	}

	//// ---------------------------------------------------------------------

	private Tab createStudentManagementTab(Stage primaryStage) {
		Tab tab = new Tab("Student Management");
		tab.setStyle("-fx-background-color: linear-gradient(to bottom, #acb6e5, #86fde8);");

		studentPane = new BorderPane();
		studentPane.setStyle("-fx-background-color: linear-gradient(to right, #acb6e5, #86fde8);");

		MenuBar menuBar = new MenuBar();
		menuBar.setStyle("-fx-background-color: #6A5ACD; -fx-text-fill: white;");

		Menu fileMenu = new Menu("File");
		fileMenu.setStyle("-fx-background-color: #f38181; -fx-text-fill: white;");

		MenuItem openFileItem = new MenuItem("Open Student File");
		openFileItem.setStyle("-fx-background-color: #fce38a; -fx-text-fill: #333;");
		openFileItem.setOnAction(e -> {
			if (!isMajorFileLoaded) {
				showAlert("File Load Error", "Please load the Major file first before opening the Student file.");
				return;
			}
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showOpenDialog(primaryStage);
			if (file != null) {
				readStudentFile(file, rejectedStudentsData);
			}
		});
		fileMenu.getItems().add(openFileItem);

		Menu operationsMenu = new Menu("Operations");
		operationsMenu.setStyle("-fx-background-color: #96e6a1; -fx-text-fill: #333;");

		MenuItem insertItem = new MenuItem("Insert Student");
		MenuItem deleteItem = new MenuItem("Delete Student");
		MenuItem updateItem = new MenuItem("Update Student");
		MenuItem searchItem = new MenuItem("Search Student");

		String itemStyle = "-fx-background-color: #96e6a1; -fx-text-fill: #333;";
		insertItem.setStyle(itemStyle);
		deleteItem.setStyle(itemStyle);
		updateItem.setStyle(itemStyle);
		searchItem.setStyle(itemStyle);

		insertItem.setOnAction(e -> insertStudent());
		deleteItem.setOnAction(e -> deleteStudent());
		updateItem.setOnAction(e -> updateStudent());
		searchItem.setOnAction(e -> searchStudent());

		operationsMenu.getItems().addAll(insertItem, deleteItem, updateItem, searchItem);
		menuBar.getMenus().addAll(fileMenu, operationsMenu);

		studentTable = new TableView<>();
		studentTable.setItems(studentsData);
		studentTable.setStyle("-fx-background-color: linear-gradient(to right, #acb6e5, #86fde8);");

		studentTable.setRowFactory(tv -> new TableRow<Student>() {
			@Override
			protected void updateItem(Student student, boolean empty) {
				super.updateItem(student, empty);
				if (empty || student == null) {

					setStyle("-fx-background-color: linear-gradient(to right, #acb6e5, #86fde8);");
				} else {

					setStyle("-fx-background-color: rgba(233, 245, 232, 0.4);");
				}
			}
		});

		setupStudentTable();

		labelWelcome = new Label("WELCOME TO STUDENT MANAGEMENT");
		labelWelcome.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #333;"
				+ "-fx-background-color: linear-gradient(to right, #ffdde1, #ee9ca7);"
				+ "-fx-padding: 15px; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #fff;");

		HBox studentHBox = new HBox(20, studentTable, labelWelcome);
		studentHBox.setAlignment(Pos.CENTER_LEFT);
		studentHBox.setPadding(new Insets(10));
		studentHBox.setStyle("-fx-background-color: #f0f4f8; -fx-border-radius: 10;");

		studentPane.setTop(menuBar);
		studentPane.setCenter(studentHBox);

		tab.setContent(studentPane);
		return tab;
	}

	private void setupStudentTable() {
		studentTable.setPrefWidth(600);

		TableColumn<Student, String> idColumn = new TableColumn<>("ID");
		idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
		idColumn.setPrefWidth(70);

		TableColumn<Student, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
		nameColumn.setPrefWidth(100);

		TableColumn<Student, String> tawjihiColumn = new TableColumn<>("Tawjihi Grade");
		tawjihiColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getTawjihiGrade())));
		tawjihiColumn.setPrefWidth(120);

		TableColumn<Student, String> placementColumn = new TableColumn<>("Placement Test Grade");
		placementColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getPlacementTestGrade())));
		placementColumn.setPrefWidth(150);

		TableColumn<Student, String> majorColumn = new TableColumn<>("Chosen Major");
		majorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getChosenMajor()));
		majorColumn.setPrefWidth(120);

		studentTable.getColumns().addAll(idColumn, nameColumn, tawjihiColumn, placementColumn, majorColumn);
	}

	private void readStudentFile(File file, ObservableList<Student> rejectedStudentsData) {
		try (Scanner in = new Scanner(file)) {
			if (in.hasNextLine()) {
				String header = in.nextLine();
				if (!header.trim().equalsIgnoreCase("Student ID|Student Name|Grade|Placement Test Grade|Major")) {
					showAlert("File Error",
							"This is not the correct student file format. Please select the correct file.");
					return;

				}
			}
			while (in.hasNextLine()) {
				String line = in.nextLine();
				if (line.isEmpty()) {
					continue;
				}

				String[] splitLine = line.split("\\|");
				if (splitLine.length < 5) {
					continue;
				}

				String id = splitLine[0];
				String name = splitLine[1];
				double tawjihiGrade = Double.parseDouble(splitLine[2]);
				double placementTestGrade = Double.parseDouble(splitLine[3]);
				String chosenMajor = splitLine[4];

				double admissionMark = admissionSystem.calculateAdmissionMark(tawjihiGrade, placementTestGrade);

				Student student = new Student(id, name, tawjihiGrade, placementTestGrade, chosenMajor);
				student.setAdmissionMark(admissionMark);

				if ("Not Accepted".equals(chosenMajor)) {
					rejectedStudentsData.add(student);
				} else {
					studentsData.add(student);
				}
			}

			// Call the sort function after all students have entered
			sortStudentsData();

			studentTable.setItems(studentsData);
			studentTable.refresh();
			rejectedStudentsTable.refresh();

		} catch (IOException |

				NumberFormatException e) {
			showErrorAlert("Error", "File Read Error", "Failed to read student data from the file.");
			e.printStackTrace();
		}
	}

	//// ---------------------------------------------------------------------

	private Tab createAdmissionManagementTab() {

		Tab tab = new Tab("Admission Management");
		tab.setStyle("-fx-background-color: linear-gradient(to bottom, #acb6e5, #86fde8);");

		BorderPane admissionPane = new BorderPane();
		admissionPane.setStyle("-fx-background-color: linear-gradient(to right, #acb6e5, #86fde8);");

		// create the table and data columns
		admissionTable = new TableView<>();
//		setTableStyle(admissionTable);
		admissionTable.setStyle("-fx-background-color: linear-gradient(to right, #43cea2, #185a9d);");
		admissionTable.setRowFactory(tv -> new TableRow<StudentAdmission>() {
			@Override
			protected void updateItem(StudentAdmission admission, boolean empty) {
				super.updateItem(admission, empty);
				if (empty || admission == null) {

					setStyle("-fx-background-color: linear-gradient(to right, #acb6e5, #86fde8);");
				} else {

					setStyle("-fx-background-color: rgba(233, 245, 232, 0.4);");
				}
			}
		});
		TableColumn<StudentAdmission, String> idColumn = new TableColumn<>("Student ID");
		idColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
		idColumn.setPrefWidth(80);

		TableColumn<StudentAdmission, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameColumn.setPrefWidth(100);

		TableColumn<StudentAdmission, Double> admissionMarkColumn = new TableColumn<>("Admission Mark");
		admissionMarkColumn.setCellValueFactory(new PropertyValueFactory<>("admissionMark"));
		admissionMarkColumn.setPrefWidth(100);

		TableColumn<StudentAdmission, String> suggestedMajorColumn = new TableColumn<>("Suggested Major");
		suggestedMajorColumn.setCellValueFactory(new PropertyValueFactory<>("suggestedMajor"));
		suggestedMajorColumn.setPrefWidth(120);

		admissionTable.getColumns().addAll(idColumn, nameColumn, admissionMarkColumn, suggestedMajorColumn);
		admissionTable.setItems(admissionsData);

		// input interface using GridPane
		GridPane formGrid = new GridPane();
		formGrid.setPadding(new Insets(10));
		formGrid.setHgap(10);
		formGrid.setVgap(10);

		// set up fields and labels
		Label studentIdLabel = new Label("Student ID:");
		TextField studentIdField = new TextField();
		IconedTextFieled(studentIdLabel, studentIdField);

		studentIdField.setPromptText("Enter Student ID");
		formGrid.add(studentIdLabel, 0, 0);
		formGrid.add(studentIdField, 1, 0);

		Label nameLabel = new Label("Name:");
		TextField nameField = new TextField();
		IconedTextFieled(nameLabel, nameField);

		nameField.setPromptText("Enter Student Name");
		formGrid.add(nameLabel, 0, 1);
		formGrid.add(nameField, 1, 1);

		Label tawjihiGradeLabel = new Label("Tawjihi Grade:");
		TextField tawjihiGradeField = new TextField();
		IconedTextFieled(tawjihiGradeLabel, tawjihiGradeField);

		tawjihiGradeField.setPromptText("Enter Tawjihi Grade");
		formGrid.add(tawjihiGradeLabel, 0, 2);
		formGrid.add(tawjihiGradeField, 1, 2);

		Label placementTestLabel = new Label("Placement Test Grade:");
		TextField placementTestField = new TextField();
		IconedTextFieled(placementTestLabel, placementTestField);

		placementTestField.setPromptText("Enter Placement Test Grade");
		formGrid.add(placementTestLabel, 0, 3);
		formGrid.add(placementTestField, 1, 3);

		Label admissionMarkLabel = new Label("Admission Mark:");
		admissionMarkLabel.setStyle("-fx-border-color: #6A5ACD;" // SlateBlue border color
				+ "-fx-font-size: 14;\n" + "-fx-border-width: 1;" + "-fx-border-radius: 50;"
				+ "-fx-font-weight: Bold;\n" + "-fx-background-color: #E6E6FA;" // Lavender background
				+ "-fx-background-radius: 50 0 0 50");
		Label admissionMarkValue = new Label();
		admissionMarkValue.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");

		formGrid.add(admissionMarkLabel, 0, 4);
		formGrid.add(admissionMarkValue, 1, 4);

		ComboBox<String> suggestedMajorsComboBox = new ComboBox<>();
		suggestedMajorsComboBox.setPromptText("Available Majors for Student");
		butoonEffect(suggestedMajorsComboBox);

		formGrid.add(suggestedMajorsComboBox, 1, 5);

		Button calculateButton = new Button("Calculate Admission Mark");
		butoonEffect(calculateButton);

		calculateButton.setOnAction(
				e -> calculateAdmission(studentIdField.getText(), nameField.getText(), tawjihiGradeField.getText(),
						placementTestField.getText(), admissionMarkValue, suggestedMajorsComboBox));
		formGrid.add(calculateButton, 1, 6);

		Button addMajorButton = new Button("Add Major");
		butoonEffect(addMajorButton);

		addMajorButton.setOnAction(e -> {
			String studentId = studentIdField.getText().trim();
			String studentName = nameField.getText().trim();

			if (studentId.isEmpty() || studentName.isEmpty() || tawjihiGradeField.getText().isEmpty()
					|| placementTestField.getText().isEmpty()) {
				showAlert("Input Error", "Please fill in all fields.");
				return;
			}
			/// check if the exist other student in the same id in the table
			boolean idExistsInStudents = studentsData.stream().anyMatch(student -> student.getId().equals(studentId));
			if (idExistsInStudents) {
				showAlert("Duplicate ID Error",
						"A student with ID " + studentId + " already exists in the students list.");
				return;
			}

			try {
				double tawjihiGrade = Double.parseDouble(tawjihiGradeField.getText().trim());
				double placementTestGrade = Double.parseDouble(placementTestField.getText().trim());
				double admissionMark = admissionSystem.calculateAdmissionMark(tawjihiGrade, placementTestGrade);

				// check if the student is qualified for the major
				String selectedMajor = suggestedMajorsComboBox.getValue();
				if (selectedMajor != null) {
					// accept the student
					StudentAdmission admission = new StudentAdmission(studentId, studentName, admissionMark,
							selectedMajor);
					admissionsData.add(admission);
					admissionTable.refresh();

					Student newStudent = new Student(studentId, studentName, tawjihiGrade, placementTestGrade,
							selectedMajor);
					studentsData.add(newStudent);
					studentList.insertStudent(newStudent);
					studentTable.refresh();

					admissionMarkValue.setText("Student added with selected major: " + selectedMajor);
				} else {
					// The student is not accepted, add to the unaccepted list
					Student rejectedStudent = new Student(studentId, studentName, tawjihiGrade, placementTestGrade,
							"Not Accepted");
					rejectedStudentsData.add(rejectedStudent);
					rejectedStudentsTable.refresh();

					// Add unaccepted student information to TextArea
					String rejectedStudentInfo = "ID: " + rejectedStudent.getId() + ", Name: "
							+ rejectedStudent.getName() + ", Tawjihi Grade: " + rejectedStudent.getTawjihiGrade()
							+ ", Placement Test Grade: " + rejectedStudent.getPlacementTestGrade()
							+ ", Status: Not Accepted\n";
					rejectedStudentsTextArea.appendText(rejectedStudentInfo);

					showAlert("No Major Selected", "Student not accepted for any available major.");
				}
			} catch (NumberFormatException ex) {
				showAlert("Invalid Input", "Please enter valid numeric grades for Tawjihi and Placement Test.");
			}
		});

		formGrid.add(addMajorButton, 1, 7);

		Button clearButton = new Button("Clear");
		butoonEffect(clearButton);

		clearButton.setOnAction(e -> {
			studentIdField.clear();
			nameField.clear();
			tawjihiGradeField.clear();
			placementTestField.clear();
			admissionMarkValue.setText("");
			suggestedMajorsComboBox.getItems().clear();
		});
		formGrid.add(clearButton, 1, 8);

		// TextArea for unaccepted students
		rejectedStudentsTextArea.setEditable(false);
		rejectedStudentsTextArea.setPromptText("List of Rejected Students");
		formGrid.add(new Label("Rejected Students:"), 0, 9);
		formGrid.setStyle("-fx-border-color: #6A5ACD;" + "-fx-font-size: 14;\n" + "-fx-border-width: 1;"
				+ "-fx-border-radius: 50;" + "-fx-font-weight: Bold;\n" + "-fx-background-color: #E6E6FA;"
				+ "-fx-background-radius: 50 0 0 50");
		formGrid.add(rejectedStudentsTextArea, 1, 9);

		HBox mainBox = new HBox(20, admissionTable, formGrid);
		mainBox.setAlignment(Pos.CENTER_LEFT);
		mainBox.setPadding(new Insets(10));

		admissionPane.setCenter(mainBox);
		tab.setContent(admissionPane);
		return tab;
	}

	private void calculateAdmission(String studentId, String name, String tawjihiGradeStr, String placementTestStr,
			Label admissionMarkLabel, ComboBox<String> suggestedMajorsComboBox) {
		try {
			double tawjihiGrade = Double.parseDouble(tawjihiGradeStr);
			double placementTestGrade = Double.parseDouble(placementTestStr);

// calculate acceptance rate
			double admissionMark = admissionSystem.calculateAdmissionMark(tawjihiGrade, placementTestGrade);
			admissionMarkLabel.setText("Admission Mark: " + String.format("%.2f", admissionMark));

// display available specializations based on average
			ObservableList<String> suitableMajors = getSuitableMajors(admissionMark);
			suggestedMajorsComboBox.getItems().clear();

			if (suitableMajors.isEmpty()) {
/// if the student is not qualified for any major show the average along with
// the nonacceptance letter
				admissionMarkLabel.setText("Admission Mark: " + String.format("%.2f", admissionMark)
						+ "\nStudent Not Accepted based on Admission Mark.");
			} else {
				suggestedMajorsComboBox.getItems().addAll(suitableMajors);
				suggestedMajorsComboBox.setPromptText("Available Majors for Student");
			}

		} catch (NumberFormatException e) {
			showErrorAlert("Error", "Invalid Input",
					"Please enter valid numeric grades for Tawjihi and Placement Test.");

		}
	}

	//// ---------------------------------------------------------------------

	private Tab createStatisticsTab() {
		Tab tab = new Tab("Statistics");
		tab.setStyle("-fx-background-color: linear-gradient(to bottom, #acb6e5, #86fde8);");

		VBox vBox = new VBox(15);
		vBox.setPadding(new Insets(20));
		vBox.setAlignment(Pos.CENTER);

		Button totalAcceptedButton = new Button("Total Accepted Per Major");
		totalAcceptedButton.setOnAction(e -> showTotalAcceptedPerMajor());
		butoonEffect(totalAcceptedButton);

		Button totalRejectedButton = new Button("Total Rejected Per Major");
		totalRejectedButton.setOnAction(e -> showTotalRejectedPerMajor());
		butoonEffect(totalRejectedButton);

		Button overallAcceptanceRateButton = new Button("Overall Acceptance Rate");
		overallAcceptanceRateButton.setOnAction(e -> showOverallAcceptanceRate());
		butoonEffect(overallAcceptanceRateButton);

		Button topStudentsButton = new Button("Top N Students");
		topStudentsButton.setOnAction(e -> showTopNStudents());
		butoonEffect(topStudentsButton);

		vBox.getChildren().addAll(totalAcceptedButton, totalRejectedButton, overallAcceptanceRateButton,
				topStudentsButton);
		vBox.setStyle("-fx-background-color: #E6E6FA; " + "-fx-border-color: #6A5ACD; " + "-fx-border-width: 2; "
				+ "-fx-border-radius: 15; " + "-fx-background-radius: 15;");

		tab.setContent(vBox);
		return tab;
	}

	private void showTotalAcceptedPerMajor() {
		StringBuilder result = new StringBuilder("Total Accepted Per Major:\n\n");
		for (Major major : majorsData) {
			int count = 0;
			for (Student student : studentsData) {
				if (student.getChosenMajor().equals(major.getName())) {
					count++;
				}
			}
			result.append(major.getName()).append(": ").append(count).append("\n");
		}
		showAlert("Total Accepted Per Major", result.toString());
	}

	private void showTotalRejectedPerMajor() {
		StringBuilder result = new StringBuilder("Total Rejected Per Major:\n\n");
		for (Major major : majorsData) {
			int count = 0;
			for (Student student : rejectedStudentsData) {
				if (student.getChosenMajor().equals(major.getName())) {
					count++;
				}
			}
			result.append(major.getName()).append(": ").append(count).append("\n");
		}
		showAlert("Total Rejected Per Major", result.toString());
	}

	private void showOverallAcceptanceRate() {
		int totalAccepted = studentsData.size();
		int totalRejected = rejectedStudentsData.size();
		int totalEvaluated = totalAccepted + totalRejected;

		double acceptanceRate;
		if (totalEvaluated > 0) {
			acceptanceRate = ((double) totalAccepted / totalEvaluated) * 100;
		} else {
			acceptanceRate = 0;
		}

		String message = "Total Accepted: " + totalAccepted + "\nTotal Rejected: " + totalRejected
				+ "\nTotal Evaluated (Accepted + Rejected): " + totalEvaluated + "\nAcceptance Rate: "
				+ String.format("%.2f", acceptanceRate) + "%";
		showAlert("Overall Acceptance Rate", message);
	}

	private void showTopNStudents() {
		TextInputDialog inputDialog = new TextInputDialog();
		inputDialog.setTitle("Top N Students");
		inputDialog.setHeaderText("Enter the number of top students you want to see:");
		inputDialog.setContentText("Number of students:");
///** ifPresent--> if the value exists (the user enters something), the ifPresent function executes the code inside 
		inputDialog.showAndWait().ifPresent(input -> {///
			try {
				int topN = Integer.parseInt(input);
				if (topN > 0) {
					// sort the studentsData directly by swapping elements within it
					for (int i = 0; i < studentsData.size() - 1; i++) {
						for (int j = i + 1; j < studentsData.size(); j++) {
							if (studentsData.get(i).getAdmissionMark() < studentsData.get(j).getAdmissionMark()) {
								// swap elements within studentsData directly
								Student temp = studentsData.get(i);
								studentsData.set(i, studentsData.get(j));
								studentsData.set(j, temp);
							}
						}
					}

					// display top N students from sorted studentsData
					StringBuilder result = new StringBuilder("Top ").append(topN).append(" Students:\n\n");
					for (int i = 0; i < Math.min(topN, studentsData.size()); i++) {
						Student student = studentsData.get(i);
						result.append("ID: ").append(student.getId()).append(", Name: ").append(student.getName())
								.append(", Admission Mark: ").append(student.getAdmissionMark()).append("\n");
					}
					showAlert("Top " + topN + " Students", result.toString());
				} else {
					showAlert("Invalid Input", "Please enter a positive integer.");
				}
			} catch (NumberFormatException e) {
				showAlert("Invalid Input", "Please enter a valid integer.");
			}
		});
	}

	//// ---------------------------------------------------------------------

	private Tab createRejectedStudentsTab() {
		Tab tab = new Tab("Rejected Students");
		tab.setStyle("-fx-background-color: linear-gradient(to bottom, #acb6e5, #86fde8);");

		// Add the data of unaccepted students to the same file
		rejectedStudentsTable = new TableView<>();
		rejectedStudentsTable.setStyle("-fx-background-color: linear-gradient(to right, #acb6e5, #86fde8);");

		TableColumn<Student, String> idColumn = new TableColumn<>("ID");
		idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
		idColumn.setPrefWidth(100);

		TableColumn<Student, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
		nameColumn.setPrefWidth(100);

		TableColumn<Student, String> tawjihiColumn = new TableColumn<>("Tawjihi Grade");
		tawjihiColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getTawjihiGrade())));
		tawjihiColumn.setPrefWidth(100);

		TableColumn<Student, String> placementColumn = new TableColumn<>("Placement Test Grade");
		placementColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getPlacementTestGrade())));
		placementColumn.setPrefWidth(150);

		TableColumn<Student, String> majorColumn = new TableColumn<>("Chosen Major");
		majorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getChosenMajor()));
		majorColumn.setPrefWidth(100);

		rejectedStudentsTable.getColumns().addAll(idColumn, nameColumn, tawjihiColumn, placementColumn, majorColumn);
		rejectedStudentsTable.setItems(rejectedStudentsData);

		rejectedStudentsTable.setRowFactory(tv -> new TableRow<Student>() {
			@Override
			protected void updateItem(Student student, boolean empty) {
				super.updateItem(student, empty);
				if (empty || student == null) {

					setStyle("-fx-background-color: rgba(211, 224, 234, 0.3);");
				} else {

					setStyle("-fx-background-color: rgba(233, 245, 232, 0.4);");
				}
			}
		});

		TextField studentIdField = new TextField();
		studentIdField.setPromptText("Enter Student ID to find the reason for Rejection");
		studentIdField.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #6A5ACD; -fx-border-radius: 10;");

		// button to display the reason for rejection
		Button reasonButton = new Button("Show Rejection Reason");
		reasonButton.setOnAction(e -> {
			String enteredId = studentIdField.getText().trim();
			if (enteredId.isEmpty()) {
				showAlert("Input Error", "Please enter a Student ID ");
				return;
			}

			Student rejectedStudent = rejectedStudentsData.stream().filter(student -> student.getId().equals(enteredId))
					.findFirst().orElse(null);

			if (rejectedStudent != null) {
				double admissionMark = admissionSystem.calculateAdmissionMark(rejectedStudent.getTawjihiGrade(),
						rejectedStudent.getPlacementTestGrade());
				showAlert("Rejection Reason", "The admission mark for student " + enteredId + " is: " + admissionMark);
			} else {
				showAlert("Student Not Found", "No rejected student found with ID: " + enteredId);
			}
		});
		butoonEffect(reasonButton);

		// Clear button to clear the field
		Button clearButton = new Button("Clear");
		clearButton.setOnAction(e -> studentIdField.clear());
		butoonEffect(clearButton);

		VBox layout = new VBox(15, rejectedStudentsTable, studentIdField, reasonButton, clearButton);
		layout.setPadding(new Insets(20));
		layout.setStyle("-fx-background-color: #E6E6FA; -fx-border-color: #6A5ACD; -fx-border-radius: 10;"
				+ "-fx-background-radius: 10;");
		layout.setAlignment(Pos.CENTER);

		tab.setContent(layout);
		return tab;
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setContentText(message);
		alert.showAndWait();
	}

	//// ---------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// ------------------------------------------------------------------------
//
	//
	private void sortMajorsAlphabeticallyManual() {
		int n = majorsData.size();
		for (int i = 0; i < n - 1; i++) {
			for (int j = 0; j < n - i - 1; j++) {
				if (majorsData.get(j).getName().compareToIgnoreCase(majorsData.get(j + 1).getName()) > 0) {
					// swap between items in alphabetical order
					Major temp = majorsData.get(j);
					majorsData.set(j, majorsData.get(j + 1));
					majorsData.set(j + 1, temp);
				}
			}
		}
	}

	private void sortStudentsData() {
		int n = studentsData.size();
		for (int i = 0; i < n - 1; i++) {
			for (int j = 0; j < n - i - 1; j++) {
				if (studentsData.get(j).getAdmissionMark() < studentsData.get(j + 1).getAdmissionMark()) {
					// exchange places between two students if the first has a lower acceptance mark
					// than the second
					Student temp = studentsData.get(j);
					studentsData.set(j, studentsData.get(j + 1));
					studentsData.set(j + 1, temp);
				}
			}
		}
	}

	/// *************************************************************
	/// *************************************************************

	// Display an alert when there is an error
	private void showErrorAlert(String title, String header, String content) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

////*************************************************************
	/// ------------------------------------------------------------
////*************************************************************

	private void butoonEffect(Node b) {
		b.setOnMouseMoved(e -> {
			b.setStyle("-fx-border-radius: 25 25 25 25;\n" + "-fx-font-size: 15;\n"
					+ "-fx-font-family: Times New Roman;\n" + "-fx-font-weight: Bold;\n" + "-fx-text-fill: #FFFFFF;\n" // White
																														// text
																														// color
					+ "-fx-background-color: #4CAF50;\n" // Green background on hover
					+ "-fx-border-color: #4CAF50;\n" // Green border on hover
					+ "-fx-border-width: 3.5;\n" + "-fx-background-radius: 25 25 25 25;");
		});

		b.setOnMouseExited(e -> {
			b.setStyle("-fx-border-radius: 25 25 25 25;\n" + "-fx-font-size: 15;\n"
					+ "-fx-font-family: Times New Roman;\n" + "-fx-font-weight: Bold;\n" + "-fx-text-fill: #FFFFFF;\n" // White
																														// text
																														// color
					+ "-fx-background-color: #3E3E3E;\n" // Dark gray background when not hovered
					+ "-fx-border-color: #4CAF50;\n" // Green border when not hovered
					+ "-fx-border-width: 3.5;\n" + "-fx-background-radius: 25 25 25 25;");
		});
	}

	private void IconedTextFieled(Node l, Node t) {
		l.setStyle("-fx-border-color: #6A5ACD;" // SlateBlue border color
				+ "-fx-font-size: 14;\n" + "-fx-border-width: 1;" + "-fx-border-radius: 50;"
				+ "-fx-font-weight: Bold;\n" + "-fx-background-color: #E6E6FA;" // Lavender background
				+ "-fx-background-radius: 50 0 0 50");

		t.setStyle("-fx-border-radius: 0 50 50 0;\n" + "-fx-font-size: 14;\n" + "-fx-font-family: Times New Roman;\n"
				+ "-fx-font-weight: Bold;\n" + "-fx-background-color: #FFFFFF;\n" // White background for text field
				+ "-fx-border-color: #6A5ACD;\n" // SlateBlue border color
				+ "-fx-border-width: 3.5;" + "-fx-background-radius: 0 50 50 0");
	}

////*************************************************************
	/// ------------------------------------------------------------
////*************************************************************

	private void insertStudent() {

		GridPane formGrid = new GridPane();
		formGrid.setPadding(new Insets(15));
		formGrid.setHgap(10);
		formGrid.setVgap(12);
		formGrid.setAlignment(Pos.CENTER_LEFT);

		Label lblID = new Label("ID:");
		TextField tfID = new TextField();
		IconedTextFieled(lblID, tfID);

		Label lblName = new Label("Name:");
		TextField tfName = new TextField();
		IconedTextFieled(lblName, tfName);

		Label lblTawjihiGrade = new Label("Tawjihi Grade:");
		TextField tfTawjihiGrade = new TextField();
		IconedTextFieled(lblTawjihiGrade, tfTawjihiGrade);

		Label lblPlacementTestGrade = new Label("Placement Test Grade:");
		TextField tfPlacementTestGrade = new TextField();
		IconedTextFieled(lblPlacementTestGrade, tfPlacementTestGrade);

		Label lblChosenMajor = new Label("Chosen Major:");
		ComboBox<String> cbChosenMajor = new ComboBox<>();
		ObservableList<String> majorNames = FXCollections.observableArrayList();
		for (Major major : majorsData) {
			majorNames.add(major.getName());
		}
		cbChosenMajor.setItems(majorNames);
		cbChosenMajor.setPromptText("Select Major");
		IconedTextFieled(lblChosenMajor, cbChosenMajor);

		Button btAdd = new Button("Add");
		Button btClear = new Button("Clear");
		butoonEffect(btAdd);
		butoonEffect(btClear);

		Label lblResult = new Label();
		ComboBox<String> suggestedMajorsComboBox = new ComboBox<>();
		IconedTextFieled(lblResult, suggestedMajorsComboBox); // ComboBox for suggested specializations

		HBox buttonBox = new HBox(5);
		buttonBox.setAlignment(Pos.CENTER_LEFT);
		buttonBox.getChildren().addAll(btAdd, btClear);

		formGrid.add(lblID, 0, 0);
		formGrid.add(tfID, 1, 0);
		formGrid.add(lblName, 0, 1);
		formGrid.add(tfName, 1, 1);
		formGrid.add(lblTawjihiGrade, 0, 2);
		formGrid.add(tfTawjihiGrade, 1, 2);
		formGrid.add(lblPlacementTestGrade, 0, 3);
		formGrid.add(tfPlacementTestGrade, 1, 3);
		formGrid.add(lblChosenMajor, 0, 4);
		formGrid.add(cbChosenMajor, 1, 4);
		formGrid.add(buttonBox, 1, 5);
		formGrid.add(lblResult, 1, 6);
		formGrid.add(suggestedMajorsComboBox, 1, 7);
		formGrid.setStyle("-fx-border-color: #6A5ACD;" + "-fx-font-size: 14;\n" + "-fx-border-width: 1;"
				+ "-fx-border-radius: 50;" + "-fx-font-weight: Bold;\n" + "-fx-background-color: #E6E6FA;"
				+ "-fx-background-radius: 50 0 0 50");
		btClear.setOnAction(e -> {
			tfID.clear();
			tfName.clear();
			tfTawjihiGrade.clear();
			tfPlacementTestGrade.clear();
			cbChosenMajor.getSelectionModel().clearSelection();
			lblResult.setText("");
			suggestedMajorsComboBox.getItems().clear();
		});

		btAdd.setOnAction(e -> {
			try {
				String id = tfID.getText().trim();
				String name = tfName.getText().trim();
				String chosenMajorName = cbChosenMajor.getValue();

				if (id.isEmpty() || name.isEmpty() || chosenMajorName == null || tfTawjihiGrade.getText().isEmpty()
						|| tfPlacementTestGrade.getText().isEmpty()) {
					lblResult.setText("Please fill in all fields!");
					return;
				}

				if (!id.matches("[a-zA-Z0-9]+")) {
					lblResult.setText("ID should contain only letters and numbers!");
					return;
				}

				double tawjihiGrade;
				double placementTestGrade;
				try {
					tawjihiGrade = Double.parseDouble(tfTawjihiGrade.getText().trim());
					placementTestGrade = Double.parseDouble(tfPlacementTestGrade.getText().trim());
				} catch (NumberFormatException ex) {
					lblResult.setText("Please enter valid numeric grades!");
					return;
				}

				Major chosenMajor = majorList.searchMajor(chosenMajorName);
				double admissionMark = admissionSystem.calculateAdmissionMark(tawjihiGrade, placementTestGrade);

				// check if the student is qualified for the major
				if (chosenMajor != null && admissionMark < chosenMajor.getAcceptanceGrade()) {
					lblResult.setText("Student cannot register for " + chosenMajorName + ". Choose a suitable major.");

					suggestedMajorsComboBox.getItems().clear();
					suggestedMajorsComboBox.getItems().addAll(getSuitableMajors(admissionMark));
					suggestedMajorsComboBox.setPromptText("Available Majors for Student");
					return;
				}

				int resultIndex = insertStudentToList(id, name, tawjihiGrade, placementTestGrade, chosenMajorName);

				if (resultIndex != -1) {/// if equal -1 it is mean the student exist
					studentTable.refresh();
					lblResult.setText("Student added successfully!");
					suggestedMajorsComboBox.getItems().clear();
				} else {
					lblResult.setText("Student already exists!");
				}
			} catch (Exception ex) {
				lblResult.setText("An unexpected error occurred!");
				ex.printStackTrace();
			}
		});

		suggestedMajorsComboBox.setOnAction(e -> {
			String selectedMajor = suggestedMajorsComboBox.getValue();
			if (selectedMajor != null) {

				// get student data from fields
				String id = tfID.getText().trim();
				String name = tfName.getText().trim();
				double tawjihiGrade = Double.parseDouble(tfTawjihiGrade.getText().trim());
				double placementTestGrade = Double.parseDouble(tfPlacementTestGrade.getText().trim());

				// verify that the student does not already exist in the table
				if (studentList.searchStudent(id) == null) {
					// add the student to the table with the chosen specialization
					insertStudentToList(id, name, tawjihiGrade, placementTestGrade, selectedMajor);
					lblResult.setText("Student added with selected major: " + selectedMajor);
					studentTable.refresh();
				} else {
					lblResult.setText("Student already exists!");
				}
			}
		});

		HBox mainContent = new HBox(20, studentTable, formGrid);
		mainContent.setPadding(new Insets(10));
		mainContent.setAlignment(Pos.CENTER_LEFT);
		studentPane.setCenter(mainContent);
	}

	private int insertStudentToList(String id, String name, double tawjihiGrade, double placementTestGrade,
			String chosenMajor) {
		// verify that the student already exists
		if (studentList.searchStudent(id) != null) {
			// show a message that the requester already exists
			showAlert("Error", "Student with ID " + id + " already exists!");
			return -1; // the student already exists
		}

		// Create a new student and node
		Student newStudent = new Student(id, name, tawjihiGrade, placementTestGrade, chosenMajor);
		double admissionMark = admissionSystem.calculateAdmissionMark(tawjihiGrade, placementTestGrade);
		newStudent.setAdmissionMark(admissionMark);

		// Add the student to the studentsData list
		studentsData.add(newStudent);

		sortStudentsData();

		studentList.insertStudent(newStudent);

		return 1; // The addition was completed successfully
	}

	private void updateStudent() {
		GridPane formGrid = new GridPane();
		formGrid.setPadding(new Insets(15));
		formGrid.setHgap(10);
		formGrid.setVgap(12);
		formGrid.setAlignment(Pos.CENTER_LEFT);

		Label lblID = new Label("ID:");
		TextField tfID = new TextField();
		IconedTextFieled(lblID, tfID);

		Label lblName = new Label("Name:");
		TextField tfName = new TextField();
		IconedTextFieled(lblName, tfName);

		Label lblTawjihiGrade = new Label("Tawjihi Grade:");
		TextField tfTawjihiGrade = new TextField();
		IconedTextFieled(lblTawjihiGrade, tfTawjihiGrade);

		Label lblPlacementTestGrade = new Label("Placement Test Grade:");
		TextField tfPlacementTestGrade = new TextField();
		IconedTextFieled(lblPlacementTestGrade, tfPlacementTestGrade);

		Label lblChosenMajor = new Label("Chosen Major:");
		ComboBox<String> cbChosenMajor = new ComboBox<>();
		ObservableList<String> majorNames = FXCollections.observableArrayList();
		for (Major major : majorsData) {
			majorNames.add(major.getName());
		}
		cbChosenMajor.setItems(majorNames);
		cbChosenMajor.setPromptText("Select Major");
		IconedTextFieled(lblChosenMajor, cbChosenMajor);

		Label lblResult = new Label();
		ComboBox<String> suggestedMajorsComboBox = new ComboBox<>();
		suggestedMajorsComboBox.setPromptText("Available Majors");
		IconedTextFieled(lblResult, suggestedMajorsComboBox);

		Button btUpdate = new Button("Update");
		Button btClear = new Button("Clear");
		butoonEffect(btUpdate);
		butoonEffect(btClear);

		HBox buttonBox = new HBox(10, btUpdate, btClear);
		buttonBox.setAlignment(Pos.CENTER);

		formGrid.add(lblID, 0, 0);
		formGrid.add(tfID, 1, 0);
		formGrid.add(lblName, 0, 1);
		formGrid.add(tfName, 1, 1);
		formGrid.add(lblTawjihiGrade, 0, 2);
		formGrid.add(tfTawjihiGrade, 1, 2);
		formGrid.add(lblPlacementTestGrade, 0, 3);
		formGrid.add(tfPlacementTestGrade, 1, 3);
		formGrid.add(lblChosenMajor, 0, 4);
		formGrid.add(cbChosenMajor, 1, 4);
		formGrid.add(buttonBox, 1, 5);
		formGrid.add(lblResult, 1, 6);
		formGrid.add(suggestedMajorsComboBox, 1, 7);
		formGrid.setStyle("-fx-border-color: #6A5ACD;" + "-fx-font-size: 14;\n" + "-fx-border-width: 1;"
				+ "-fx-border-radius: 50;" + "-fx-font-weight: Bold;\n" + "-fx-background-color: #E6E6FA;"
				+ "-fx-background-radius: 50 0 0 50");

		btClear.setOnAction(e -> {
			tfID.clear();
			tfName.clear();
			tfTawjihiGrade.clear();
			tfPlacementTestGrade.clear();
			cbChosenMajor.getSelectionModel().clearSelection();
			lblResult.setText("");
			suggestedMajorsComboBox.getItems().clear();
		});

		btUpdate.setOnAction(e -> {
			try {
				String id = tfID.getText().trim();

				// Search for the student directly in studentsData
				Student student = studentsData.stream().filter(st -> st.getId().equals(id)).findFirst().orElse(null);

				if (student != null) {
					student.setName(tfName.getText().trim());
					double tawjihiGrade = Double.parseDouble(tfTawjihiGrade.getText().trim());
					double placementTestGrade = Double.parseDouble(tfPlacementTestGrade.getText().trim());

					/// calculate acceptance rate
					double admissionMark = admissionSystem.calculateAdmissionMark(tawjihiGrade, placementTestGrade);
					String chosenMajorName = cbChosenMajor.getValue();

					// search for the specialty in the list of specializations
					Major chosenMajor = majorList.searchMajor(chosenMajorName);

					if (chosenMajor != null && admissionMark < chosenMajor.getAcceptanceGrade()) {
						lblResult.setText(
								"Student cannot register for " + chosenMajorName + ". Choose a suitable major.");

						// populates the suggestedMajorsComboBox with available majors based on the
						// modifier
						suggestedMajorsComboBox.getItems().clear();
						suggestedMajorsComboBox.getItems().addAll(getSuitableMajors(admissionMark));
						suggestedMajorsComboBox.setPromptText("Available Majors for Student");
						return;
					}

					// update the student data if he qualifies for the chosen specialization
					student.setTawjihiGrade(tawjihiGrade);
					student.setPlacementTestGrade(placementTestGrade);
					if (chosenMajorName != null) {
						student.setChosenMajor(chosenMajorName);
					}

					// Update data in TableView
					int index = studentsData.indexOf(student);
					if (index >= 0) {
						studentsData.set(index, student);
					}
					lblResult.setText("Student updated successfully!");
					suggestedMajorsComboBox.getItems().clear();
				} else {
					lblResult.setText("Student not found!");
				}
			} catch (NumberFormatException ex) {
				lblResult.setText("Please enter valid numeric grades!");
			} catch (Exception ex) {
				lblResult.setText("An unexpected error occurred!");
				ex.printStackTrace();
			}
		});

		HBox mainContent = new HBox(20, studentTable, formGrid);
		mainContent.setPadding(new Insets(10));
		mainContent.setAlignment(Pos.CENTER_LEFT);
		studentPane.setCenter(mainContent);
		studentTable.refresh();
	}

	private void searchStudent() {

		GridPane formGrid = new GridPane();
		formGrid.setPadding(new Insets(15));
		formGrid.setHgap(10);
		formGrid.setVgap(12);
		formGrid.setAlignment(Pos.CENTER_LEFT);

		Label lblID = new Label("ID:");
		TextField tfID = new TextField();
		IconedTextFieled(lblID, tfID);

		Label lblResult = new Label();
		lblResult.setStyle("-fx-font-size: 14px; -fx-font-family: Arial; -fx-text-fill: #4CAF50;");

		Button btSearch = new Button("Search");
		Button btClear = new Button("Clear");
		butoonEffect(btSearch);
		butoonEffect(btClear);

		HBox buttonBox = new HBox(10, btSearch, btClear);
		buttonBox.setAlignment(Pos.CENTER);

		formGrid.add(lblID, 0, 0);
		formGrid.add(tfID, 1, 0);
		formGrid.add(buttonBox, 1, 1);
		formGrid.add(lblResult, 1, 2);
		formGrid.setStyle("-fx-border-color: #6A5ACD;" // SlateBlue border color
				+ "-fx-font-size: 14;\n" + "-fx-border-width: 1;" + "-fx-border-radius: 50;"
				+ "-fx-font-weight: Bold;\n" + "-fx-background-color: #E6E6FA;" // Lavender background
				+ "-fx-background-radius: 50 0 0 50");

		btClear.setOnAction(e -> {
			tfID.clear();
			lblResult.setText("");
		});

		btSearch.setOnAction(e -> {
			String id = tfID.getText().trim();
			Student student = null;
			for (Student st : studentsData) {
				if (st.getId().equals(id)) {
					student = st;
					break;
				}
			}

			if (student != null) {
				// Display all student information in lblResult
				lblResult.setText(String.format(
						"Student Found:\nName: %s\nTawjihi Grade: %.2f\nPlacement Test Grade: %.2f\nChosen Major: %s",
						student.getName(), student.getTawjihiGrade(), student.getPlacementTestGrade(),
						student.getChosenMajor() != null ? student.getChosenMajor() : "None"));
				lblResult.setStyle("-fx-text-fill: #4CAF50;");
			} else {
				lblResult.setText("Student not found!");
				lblResult.setStyle("-fx-text-fill: #FF0000;");
			}
		});

		HBox mainContent = new HBox(20, studentTable, formGrid);
		mainContent.setPadding(new Insets(10));
		mainContent.setAlignment(Pos.CENTER_LEFT);
		studentPane.setCenter(mainContent);
		studentTable.refresh();
	}

	private void deleteStudent() {

		GridPane formGrid = new GridPane();
		formGrid.setPadding(new Insets(15));
		formGrid.setHgap(10);
		formGrid.setVgap(12);
		formGrid.setAlignment(Pos.CENTER_LEFT);

		Label lblID = new Label("ID:");
		TextField tfID = new TextField();
		IconedTextFieled(lblID, tfID);

		Label lblResult = new Label();
		lblResult.setStyle("-fx-font-size: 14px; -fx-font-family: Arial;");

		Button btDelete = new Button("Delete");
		Button btClear = new Button("Clear");
		butoonEffect(btDelete);
		butoonEffect(btClear);

		HBox buttonBox = new HBox(10, btDelete, btClear);
		buttonBox.setAlignment(Pos.CENTER);

		formGrid.add(lblID, 0, 0);
		formGrid.add(tfID, 1, 0);
		formGrid.add(buttonBox, 1, 1);
		formGrid.add(lblResult, 1, 2);
		formGrid.setStyle("-fx-border-color: #6A5ACD;" // SlateBlue border color
				+ "-fx-font-size: 14;\n" + "-fx-border-width: 1;" + "-fx-border-radius: 50;"
				+ "-fx-font-weight: Bold;\n" + "-fx-background-color: #E6E6FA;" // Lavender background
				+ "-fx-background-radius: 50 0 0 50");

		btClear.setOnAction(e -> {
			tfID.clear();
			lblResult.setText("");
		});

		btDelete.setOnAction(e -> {
			String id = tfID.getText().trim();

			// Verify that only numbers are entered in the ID
			if (id.isEmpty()) {
				lblResult.setText("Please enter a valid ID.");
				lblResult.setStyle("-fx-text-fill: #FF0000;");
				return;
			} else if (!id.matches("[a-zA-Z0-9]+")) {
				lblResult.setText("ID should contain only letters and numbers!");
				lblResult.setStyle("-fx-text-fill: #FF0000;");
				return;
			}

			// Display a confirmation message before deletion
			Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
			confirmationAlert.setTitle("Confirmation");
			confirmationAlert.setHeaderText("Delete Student");
			confirmationAlert.setContentText("Are you sure you want to delete this student with ID: " + id + "?");

			confirmationAlert.showAndWait().ifPresent(response -> {
				if (response == ButtonType.OK) {
					Student studentToDelete = null;
					for (Student student : studentsData) {
						if (student.getId().equals(id)) {
							studentToDelete = student;
							break;
						}
					}

					boolean isDeleted = studentList.deleteStudent(id);

					if (studentToDelete != null) {
						studentsData.remove(studentToDelete);
						studentList.deleteStudent(id);
						studentTable.refresh();
						lblResult.setText("Student deleted successfully!");
						lblResult.setStyle("-fx-text-fill: #4CAF50;");
					} else {
						lblResult.setText("Student not found!");
						lblResult.setStyle("-fx-text-fill: #FF0000;");
					}
				} else {
					lblResult.setText("Deletion cancelled.");
					lblResult.setStyle("-fx-text-fill: #FF8C00;");
				}
			});
		});

		HBox mainContent = new HBox(20, studentTable, formGrid);
		mainContent.setPadding(new Insets(10));
		mainContent.setAlignment(Pos.CENTER_LEFT);
		studentPane.setCenter(mainContent);
		studentTable.refresh();
	}

	private ObservableList<String> getSuitableMajors(double admissionMark) {
		ObservableList<String> suitableMajors = FXCollections.observableArrayList();

		MajorNode current = majorList.head;
		while (current != null) {
			if (admissionMark >= current.data.getAcceptanceGrade()) {
				suitableMajors.add(current.data.getName());
			}
			current = current.next;
		}

		return suitableMajors;
	}

////*************************************************************
	/// ------------------------------------------------------------
////*************************************************************

	private void insertMajor() {

		GridPane formGrid = new GridPane();
		formGrid.setPadding(new Insets(15));
		formGrid.setHgap(10);
		formGrid.setVgap(12);
		formGrid.setAlignment(Pos.CENTER_LEFT);

		Label lblName = new Label("Major Name:");
		TextField tfMajorName = new TextField();
		IconedTextFieled(lblName, tfMajorName);

		Label lblAcceptanceGrade = new Label("Acceptance Grade:");
		TextField tfAcceptanceGrade = new TextField();
		IconedTextFieled(lblAcceptanceGrade, tfAcceptanceGrade);

		Label lblTawjihiWeight = new Label("Tawjihi Weight:");
		TextField tfTawjihiWeight = new TextField();
		IconedTextFieled(lblTawjihiWeight, tfTawjihiWeight);

		Label lblPlacementWeight = new Label("Placement Test Weight:");
		TextField tfPlacementWeight = new TextField();
		IconedTextFieled(lblPlacementWeight, tfPlacementWeight);

		Label lblResult = new Label();

		Button btAdd = new Button("Add");
		Button btClear = new Button("Clear");
		butoonEffect(btAdd);
		butoonEffect(btClear);

		HBox buttonBox = new HBox(10, btAdd, btClear);
		buttonBox.setAlignment(Pos.CENTER);

		formGrid.add(lblName, 0, 0);
		formGrid.add(tfMajorName, 1, 0);
		formGrid.add(lblAcceptanceGrade, 0, 1);
		formGrid.add(tfAcceptanceGrade, 1, 1);
		formGrid.add(lblTawjihiWeight, 0, 2);
		formGrid.add(tfTawjihiWeight, 1, 2);
		formGrid.add(lblPlacementWeight, 0, 3);
		formGrid.add(tfPlacementWeight, 1, 3);
		formGrid.add(buttonBox, 1, 4);
		formGrid.add(lblResult, 1, 5);
		formGrid.setStyle("-fx-border-color: #6A5ACD;" + "-fx-font-size: 14;\n" + "-fx-border-width: 1;"
				+ "-fx-border-radius: 50;" + "-fx-font-weight: Bold;\n" + "-fx-background-color: #E6E6FA;"
				+ "-fx-background-radius: 50 0 0 50");
		btClear.setOnAction(e -> {
			tfMajorName.clear();
			tfAcceptanceGrade.clear();
			tfTawjihiWeight.clear();
			tfPlacementWeight.clear();
			lblResult.setText("");
		});

		btAdd.setOnAction(e -> {
			try {
				String name = tfMajorName.getText().trim();

				// Check whether the major exists in MajorLinkedList
				if (majorList.searchMajor(name) != null) {
					lblResult.setText("Major already exists in the system! Cannot add duplicate major.");
					return;
				}

				/// verify digital input
				if (!tfAcceptanceGrade.getText().trim().matches("\\d+(\\.\\d+)?")
						|| !tfTawjihiWeight.getText().trim().matches("\\d+(\\.\\d+)?")
						|| !tfPlacementWeight.getText().trim().matches("\\d+(\\.\\d+)?")) {
					lblResult.setText("Please enter valid numeric values!");
					return;
				}

				double acceptanceGrade = Double.parseDouble(tfAcceptanceGrade.getText().trim());
				double tawjihiWeight = Double.parseDouble(tfTawjihiWeight.getText().trim());
				double placementWeight = Double.parseDouble(tfPlacementWeight.getText().trim());

				// Check the range of values
				if (acceptanceGrade < 0 || acceptanceGrade > 100 || tawjihiWeight < 0 || tawjihiWeight > 100
						|| placementWeight < 0 || placementWeight > 100) {
					lblResult.setText("Grades must be between 0 and 100!");
					return;
				}

				Major major = new Major(name, acceptanceGrade, tawjihiWeight, placementWeight);
				majorList.insertMajor(major);
				refreshTableFromLinkedList();

				lblResult.setText("Major added successfully!");
			} catch (NumberFormatException ex) {
				lblResult.setText("Invalid input format!");
			}
		});

		HBox mainContent = new HBox(20, majorTable, formGrid);
		mainContent.setPadding(new Insets(10));
		mainContent.setAlignment(Pos.CENTER_LEFT);
		majorPane.setCenter(mainContent);

	}

	private void deleteMajor() {
		GridPane formGrid = new GridPane();
		formGrid.setPadding(new Insets(15));
		formGrid.setHgap(10);
		formGrid.setVgap(12);
		formGrid.setAlignment(Pos.CENTER_LEFT);

		Label lblName = new Label("Select Major to Delete:");
		ComboBox<String> cbMajorName = new ComboBox<>();

		ObservableList<String> majorNames = FXCollections.observableArrayList();
		for (Major major : majorsData) {
			majorNames.add(major.getName());
		}
		cbMajorName.setItems(majorNames);
		cbMajorName.setPromptText("Select Major");
		IconedTextFieled(lblName, cbMajorName);

		Label lblResult = new Label();

		Button btDelete = new Button("Delete");
		Button btClear = new Button("Clear");
		butoonEffect(btDelete);
		butoonEffect(btClear);

		HBox buttonBox = new HBox(10, btDelete, btClear);
		buttonBox.setAlignment(Pos.CENTER);

		formGrid.add(lblName, 0, 0);
		formGrid.add(cbMajorName, 1, 0);
		formGrid.add(buttonBox, 1, 1);
		formGrid.add(lblResult, 1, 2);
		formGrid.setStyle("-fx-border-color: #6A5ACD;" + "-fx-font-size: 14;\n" + "-fx-border-width: 1;"
				+ "-fx-border-radius: 50;" + "-fx-font-weight: Bold;\n" + "-fx-background-color: #E6E6FA;"
				+ "-fx-background-radius: 50 0 0 50");

		btClear.setOnAction(e -> {
			cbMajorName.getSelectionModel().clearSelection();
			lblResult.setText("");
		});

		btDelete.setOnAction(e -> {
			String name = cbMajorName.getValue();

			if (name == null) {
				lblResult.setText("Please select a Major.");
				return;
			}

			Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
			confirmationAlert.setTitle("Confirmation Dialog");
			confirmationAlert.setHeaderText("Delete Major");
			confirmationAlert.setContentText("Are you sure you want to delete the selected major?");

			confirmationAlert.showAndWait().ifPresent(response -> {
				if (response == ButtonType.OK) {
					boolean deleted = majorList.deleteMajor(name);

					if (deleted) {
						refreshTableFromLinkedList();
						lblResult.setText("Major deleted successfully!");
					} else {
						lblResult.setText("Major not found in MajorLinkedList!");
					}
				} else {
					lblResult.setText("Deletion cancelled.");
				}
			});
		});

		HBox mainContent = new HBox(20, majorTable, formGrid);
		mainContent.setPadding(new Insets(10));
		mainContent.setAlignment(Pos.CENTER_LEFT);
		majorPane.setCenter(mainContent);
	}

	private void refreshTableFromLinkedList() {
		majorsData.clear();
		MajorNode current = majorList.head;
		while (current != null) {
			majorsData.add(current.data);
			current = current.next;
		}
		majorTable.refresh();
	}

	private void updateMajor() {
		GridPane formGrid = new GridPane();
		formGrid.setPadding(new Insets(15));
		formGrid.setHgap(10);
		formGrid.setVgap(12);
		formGrid.setAlignment(Pos.CENTER_LEFT);

		Label lblSelectMajor = new Label("Select Major:");
		ComboBox<String> cbSelectMajor = new ComboBox<>();
		ObservableList<String> majorNames = FXCollections.observableArrayList();
		for (Major major : majorsData) {
			majorNames.add(major.getName());
		}
		cbSelectMajor.setItems(majorNames);
		cbSelectMajor.setPromptText("Select Major");
		IconedTextFieled(lblSelectMajor, cbSelectMajor);

		Label lblAcceptanceGrade = new Label("Acceptance Grade:");
		TextField tfAcceptanceGrade = new TextField();
		IconedTextFieled(lblAcceptanceGrade, tfAcceptanceGrade);

		Label lblTawjihiWeight = new Label("Tawjihi Weight:");
		TextField tfTawjihiWeight = new TextField();
		IconedTextFieled(lblTawjihiWeight, tfTawjihiWeight);

		Label lblPlacementWeight = new Label("Placement Test Weight:");
		TextField tfPlacementWeight = new TextField();
		IconedTextFieled(lblPlacementWeight, tfPlacementWeight);

		Label lblResult = new Label();

		Button btUpdate = new Button("Update");
		Button btClear = new Button("Clear");
		butoonEffect(btUpdate);
		butoonEffect(btClear);

		HBox buttonBox = new HBox(10, btUpdate, btClear);
		buttonBox.setAlignment(Pos.CENTER);

		formGrid.add(lblSelectMajor, 0, 0);
		formGrid.add(cbSelectMajor, 1, 0);
		formGrid.add(lblAcceptanceGrade, 0, 1);
		formGrid.add(tfAcceptanceGrade, 1, 1);
		formGrid.add(lblTawjihiWeight, 0, 2);
		formGrid.add(tfTawjihiWeight, 1, 2);
		formGrid.add(lblPlacementWeight, 0, 3);
		formGrid.add(tfPlacementWeight, 1, 3);
		formGrid.add(buttonBox, 1, 4);
		formGrid.add(lblResult, 1, 5);
		formGrid.setStyle("-fx-background-color: #E6E6FA; " + "-fx-border-color: #6A5ACD; " + "-fx-border-width: 2px; "
				+ "-fx-border-radius: 10px; " + "-fx-background-radius: 10px; " + "-fx-padding: 10px; "
				+ "-fx-font-size: 14px; " + "-fx-font-weight: bold;");

		cbSelectMajor.setOnAction(e -> {
			String selectedMajorName = cbSelectMajor.getValue();
			Major selectedMajor = majorList.searchMajor(selectedMajorName);
			if (selectedMajor != null) {
				tfAcceptanceGrade.setText(String.valueOf(selectedMajor.getAcceptanceGrade()));
				tfTawjihiWeight.setText(String.valueOf(selectedMajor.getTawjihiWeight()));
				tfPlacementWeight.setText(String.valueOf(selectedMajor.getPlacementWeight()));
				lblResult.setText("");
			} else {
				lblResult.setText("Major not found!");
			}
		});

		btClear.setOnAction(e -> {
			cbSelectMajor.getSelectionModel().clearSelection();
			tfAcceptanceGrade.clear();
			tfTawjihiWeight.clear();
			tfPlacementWeight.clear();
			lblResult.setText("");
		});

		btUpdate.setOnAction(e -> {
			String selectedMajorName = cbSelectMajor.getValue();
			Major majorToUpdate = majorList.searchMajor(selectedMajorName);

			if (majorToUpdate != null) {
				// check the entered values ​​to ensure they are numbers
				if (!tfAcceptanceGrade.getText().trim().matches("\\d+(\\.\\d+)?")
						|| !tfTawjihiWeight.getText().trim().matches("\\d+(\\.\\d+)?")
						|| !tfPlacementWeight.getText().trim().matches("\\d+(\\.\\d+)?")) {
					lblResult.setText("Please enter valid numeric values!");
					return;
				}

				try {
					double acceptanceGrade = Double.parseDouble(tfAcceptanceGrade.getText().trim());
					double tawjihiWeight = Double.parseDouble(tfTawjihiWeight.getText().trim());
					double placementWeight = Double.parseDouble(tfPlacementWeight.getText().trim());

					// Verify values
					if (acceptanceGrade < 0 || acceptanceGrade > 100 || tawjihiWeight < 0 || tawjihiWeight > 100
							|| placementWeight < 0 || placementWeight > 100) {
						lblResult.setText("Grades must be between 0 and 100!");
						return;
					}

					majorToUpdate.setAcceptanceGrade(acceptanceGrade);
					majorToUpdate.setTawjihiWeight(tawjihiWeight);
					majorToUpdate.setPlacementWeight(placementWeight);

					refreshTableFromLinkedList();
					lblResult.setText("Major updated successfully!");
				} catch (NumberFormatException ex) {
					lblResult.setText("Invalid input format!");
				}
			} else {
				lblResult.setText("Major not found!");
			}
		});

		HBox mainContent = new HBox(20, majorTable, formGrid);
		mainContent.setPadding(new Insets(10));
		mainContent.setAlignment(Pos.CENTER_LEFT);
		majorPane.setCenter(mainContent);
	}

	private void searchMajor() {
		GridPane formGrid = new GridPane();
		formGrid.setPadding(new Insets(15));
		formGrid.setHgap(10);
		formGrid.setVgap(12);
		formGrid.setAlignment(Pos.CENTER_LEFT);

		Label lblName = new Label("Select Major:");
		ComboBox<String> cbMajorName = new ComboBox<>();
		ObservableList<String> majorNames = FXCollections.observableArrayList();
		for (Major major : majorsData) {
			majorNames.add(major.getName());
		}
		cbMajorName.setItems(majorNames);
		cbMajorName.setPromptText("Select Major");
		IconedTextFieled(lblName, cbMajorName);

		Label lblResult = new Label();

		Button btSearch = new Button("Search");
		Button btClear = new Button("Clear");
		butoonEffect(btSearch);
		butoonEffect(btClear);

		HBox buttonBox = new HBox(10, btSearch, btClear);
		buttonBox.setAlignment(Pos.CENTER);

		formGrid.add(lblName, 0, 0);
		formGrid.add(cbMajorName, 1, 0);
		formGrid.add(buttonBox, 1, 1);
		formGrid.add(lblResult, 1, 2);
		formGrid.setStyle("-fx-background-color: #E6E6FA; " + "-fx-border-color: #6A5ACD; " + "-fx-border-width: 2px; "
				+ "-fx-border-radius: 10px; " + "-fx-background-radius: 10px; " + "-fx-padding: 10px; "
				+ "-fx-font-size: 14px; " + "-fx-font-weight: bold;");

		btClear.setOnAction(e -> {
			cbMajorName.getSelectionModel().clearSelection();
			lblResult.setText("");
		});

		btSearch.setOnAction(e -> {
			String selectedMajorName = cbMajorName.getValue();

			if (selectedMajorName == null) {
				lblResult.setText("Please select a Major.");
				return;
			}

			Major major = majorList.searchMajor(selectedMajorName);
			if (major != null) {
				lblResult.setText("Major Found: " + major.getName() + "\nAcceptance Grade: "
						+ major.getAcceptanceGrade() + "\nTawjihi Weight: " + major.getTawjihiWeight()
						+ "\nPlacement Weight: " + major.getPlacementWeight());
			} else {
				lblResult.setText("Major not found!");
			}
		});

		HBox mainContent = new HBox(20, majorTable, formGrid);
		mainContent.setPadding(new Insets(10));
		mainContent.setAlignment(Pos.CENTER_LEFT);
		majorPane.setCenter(mainContent);
	}

	private void showStudentView() {

		ComboBox<String> majorComboBox = new ComboBox<>();
		majorComboBox.setItems(FXCollections.observableArrayList(majorsData.stream().map(Major::getName).toList()));
		majorComboBox.setPromptText("Select Major");
		majorComboBox.setStyle("-fx-border-color: #6A5ACD; " + "-fx-font-size: 14; " + "-fx-border-width: 2; "
				+ "-fx-border-radius: 15; " + "-fx-background-color: #E6E6FA; " + "-fx-background-radius: 15; "
				+ "-fx-font-weight: bold;");

		// Set up the TextArea to display student names
		TextArea studentsTextArea = new TextArea();
		studentsTextArea.setEditable(false);
		studentsTextArea.setWrapText(true);
		studentsTextArea.setStyle("-fx-border-color: #6A5ACD; " + "-fx-font-size: 14; " + "-fx-border-width: 2; "
				+ "-fx-border-radius: 15; " + "-fx-background-color: #F8F8FF; " + "-fx-background-radius: 15; "
				+ "-fx-font-weight: bold;");

		Button nextButton = new Button("Next --->");
		Button previousButton = new Button("<--- Previous");
		butoonEffect(nextButton);
		butoonEffect(previousButton);

		VBox studentViewBox = new VBox(10, majorComboBox, studentsTextArea, new HBox(10, previousButton, nextButton));
		studentViewBox.setPadding(new Insets(10));
		studentViewBox.setAlignment(Pos.CENTER_LEFT);
		studentViewBox.setStyle("-fx-border-color: #6A5ACD; " + "-fx-border-width: 2; " + "-fx-border-radius: 15; "
				+ "-fx-background-color: #E6E6FA; " + "-fx-background-radius: 15;");

		final int[] currentIndex = { 0 };

		majorComboBox.setOnAction(e -> {
			String selectedMajor = majorComboBox.getValue();
			if (selectedMajor != null) {
				displayStudentsInMajor(selectedMajor, studentsTextArea);
			} else {
				studentsTextArea.clear();
			}
		});

		nextButton.setOnAction(e -> {
			currentIndex[0] = (currentIndex[0] + 1) % majorsData.size();
			majorComboBox.getSelectionModel().select(currentIndex[0]);
			displayStudentsInMajor(majorComboBox.getValue(), studentsTextArea);
		});

		previousButton.setOnAction(e -> {
			currentIndex[0] = (currentIndex[0] - 1 + majorsData.size()) % majorsData.size();
			majorComboBox.getSelectionModel().select(currentIndex[0]);
			displayStudentsInMajor(majorComboBox.getValue(), studentsTextArea);
		});

		// Update the majorPane interface to display the majors table with the student
		// display interface
		HBox mainContentWithStudentView = new HBox(20, majorTable, studentViewBox);
		mainContentWithStudentView.setAlignment(Pos.CENTER_LEFT);
		mainContentWithStudentView.setPadding(new Insets(10));

		majorPane.setCenter(mainContentWithStudentView);
	}

	/// --function to display the names of students for each major in TextArea
	private void displayStudentsInMajor(String majorName, TextArea studentsTextArea) {
		studentsTextArea.clear();
		if (majorName != null && !majorName.isEmpty()) {
			// Check if student data is loaded
			if (studentsData.isEmpty()) {
				studentsTextArea.setText("No students data loaded.");
				return;
			}

			StringBuilder studentDetails = new StringBuilder();
			for (Student student : studentsData) {
				// Check if the student's major matches the selected major
				if (majorName.equals(student.getChosenMajor())) {
					studentDetails.append("ID: ").append(student.getId()).append("\n").append("Name: ")
							.append(student.getName()).append("\n").append("Tawjihi Grade: ")
							.append(student.getTawjihiGrade()).append("\n").append("Placement Test Grade: ")
							.append(student.getPlacementTestGrade()).append("\n").append("Chosen Major: ")
							.append(student.getChosenMajor()).append("\n").append("--------------------------------\n");
				}
			}

			// Check if any student details were added, otherwise show a message indicating
			// no students are enrolled in this major
			if (studentDetails.length() > 0) {
				studentsTextArea.setText(studentDetails.toString());
			} else {
				studentsTextArea.setText("No students enrolled in this major.");
			}
		} else {
			studentsTextArea.setText("Major not found.");
		}
	}

////*************************************************************
	/// ------------------------------------------------------------
////*************************************************************

	private void saveStudentsToFile(File studentFilePath) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(studentFilePath))) {

			writer.write("Student ID|Student Name|Grade|Placement Test Grade|Major"); // Header
			writer.newLine();

			// Write the data of accepted and unaccepted students in the same file as
			// regular students
			for (Student student : studentsData) {
				String line = String.format("%s|%s|%.2f|%.2f|%s", student.getId(), student.getName(),
						student.getTawjihiGrade(), student.getPlacementTestGrade(), student.getChosenMajor());
				writer.write(line);
				writer.newLine();
			}
//
			// add the data of unaccepted students to the same file
			for (Student student : rejectedStudentsData) {
				String line = String.format("%s|%s|%.2f|%.2f|%s", student.getId(), student.getName(),
						student.getTawjihiGrade(), student.getPlacementTestGrade(), student.getChosenMajor());
				writer.write(line);
				writer.newLine();
			}
		}
	}

	private void saveMajorsToFile(File majorFilePath) throws IOException {

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(majorFilePath))) {
			writer.write("Secondary School Grade Weight|Placement Test Grade Weight|Acceptance Grade|Major");
			writer.newLine();
			for (Major major : majorsData) {
				String line = String.format("%.2f|%.2f|%.2f|%s", major.getTawjihiWeight(), // Secondary School Grade
																							// Weight
						major.getPlacementWeight(), major.getAcceptanceGrade(), major.getName());
				writer.write(line);
				writer.newLine();
			}
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}