package application;

public class StudentAdmission {

	private String studentId;
	private String name;
	private double admissionMark;
	private String suggestedMajor;

	public StudentAdmission(String studentId, String name, double admissionMark, String suggestedMajor) {
		this.studentId = studentId;
		this.name = name;
		this.admissionMark = admissionMark;
		this.suggestedMajor = suggestedMajor;
	}

	public String getStudentId() {
		return studentId;
	}

	public String getName() {
		return name;
	}

	public double getAdmissionMark() {
		return admissionMark;
	}

	public String getSuggestedMajor() {
		return suggestedMajor;
	}

}
