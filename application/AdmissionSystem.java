package application;

public class AdmissionSystem {

	private MajorLinkedList majorList;

	public AdmissionSystem() {
	}

	public double calculateAdmissionMark(double tawjihiGrade, double placementTestGrade) {
		double tawjihiWeight = 0.7;
		double placementWeight = 0.3;
		return (tawjihiGrade * tawjihiWeight) + (placementTestGrade * placementWeight);
	}

}
