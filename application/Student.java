package application;

class Student {
	private String id;
	private String name;
	private double tawjihiGrade;
	private double placementTestGrade;
	private String chosenMajor;
	private double admissionMark;

//	private static final double ACCEPTANCE_THRESHOLD = 85.0;

	//// constructor
	public Student(String id, String name, double tawjihiGrade, double placementTestGrade, String chosenMajor) {
		this.id = id;
		this.name = name;
		this.tawjihiGrade = tawjihiGrade;
		this.placementTestGrade = placementTestGrade;
		this.chosenMajor = chosenMajor;
		calculateAdmissionMark();
	}

	//// calculates the admission mark based on weights
	private void calculateAdmissionMark() {
		double tawjihiWeight = 0.7;
		double placementWeight = 0.3;
		this.admissionMark = (tawjihiGrade * tawjihiWeight) + (placementTestGrade * placementWeight);
	}

	/// gets the student ID
	public String getId() {
		return id;
	}

	/// sets the student ID
	public void setId(String id) {
		this.id = id;
	}

	/// gets the student name
	public String getName() {
		return name;
	}

	/// sets the student name
	public void setName(String name) {
		this.name = name;
	}

	/// gets the Tawjihi grade
	public double getTawjihiGrade() {
		return tawjihiGrade;
	}

	/// sets the Tawjihi grade
	public void setTawjihiGrade(double tawjihiGrade) {
		this.tawjihiGrade = tawjihiGrade;
	}

	/// gets the Placement Test grade
	public double getPlacementTestGrade() {
		return placementTestGrade;
	}

	/// sets the Placement Test grade
	public void setPlacementTestGrade(double placementTestGrade) {
		this.placementTestGrade = placementTestGrade;
	}

	/// gets the chosen major
	public String getChosenMajor() {
		return chosenMajor;
	}

	/// gets the chosen major
	public void setChosenMajor(String chosenMajor) {
		this.chosenMajor = chosenMajor;
	}

	/// gets the calculated admission mark
	public double getAdmissionMark() {
		return admissionMark;
	}

	/// sets the admission mark
	public void setAdmissionMark(double admissionMark) {
		this.admissionMark = admissionMark;
	}

	/// checks if the student meets the acceptance threshold
	public boolean isAccepted() {
		double threshold = 60.0; // example acceptance threshold
//		System.out.println("Admission Mark for " + name + ": " + admissionMark);
		return this.admissionMark >= threshold;
	}
}