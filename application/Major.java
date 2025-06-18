package application;

class Major {
	private String name;
	private double acceptanceGrade;
	private double tawjihiWeight;
	private double placementWeight;
	private CircularLinkedList students; // Circular list of students in this major
	private int acceptedCount = 0;
	private int rejectedCount = 0;

	public Major(String name, double acceptanceGrade, double tawjihiWeight, double placementWeight) {
		this.name = name;
		this.acceptanceGrade = acceptanceGrade;
		this.tawjihiWeight = tawjihiWeight;
		this.placementWeight = placementWeight;
		this.students = new CircularLinkedList();
	}

	public void addStudent(Student student) {
		students.add(student); // Add student to the circular list
		System.out.println("Student added to major: " + getName());
		students.printList(); // Print list content to verify
	}

//	// Method to calculate the number of accepted students in this major
//	public int getAcceptedCount() {
//		return students.getAcceptedCount();
//	}

	// Method to calculate the number of rejected students in this major
	public int getRejectedCount() {
		return students.getRejectedCount();
	}

	// Getter and Setter for major information
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getAcceptanceGrade() {
		return acceptanceGrade;
	}

	public void setAcceptanceGrade(double acceptanceGrade) {
		this.acceptanceGrade = acceptanceGrade;
	}

	public double getTawjihiWeight() {
		return tawjihiWeight;
	}

	public void setTawjihiWeight(double tawjihiWeight) {
		this.tawjihiWeight = tawjihiWeight;
	}

	public double getPlacementWeight() {
		return placementWeight;
	}

	public void setPlacementWeight(double placementWeight) {
		this.placementWeight = placementWeight;
	}

	// Display students in the major
	public void displayStudents() {
//		System.out.println("Students in Major: " + name);
		students.displayStudents();
	}

	// Method to increment the count of accepted students
	public void incrementAcceptedCount() {
		acceptedCount++;
	}

	// Method to increment the count of rejected students
	public void incrementRejectedCount() {
		rejectedCount++;
	}
}