package application;

public class Driver {
	public static void main(String[] args) {
		MajorLinkedList majorList = new MajorLinkedList();
		System.out.println("---------------------------------------------------");
		majorList.insertMajor(new Major("Computer Science", 85, 0.7, 0.3));
		majorList.insertMajor(new Major("Engineering", 90, 0.6, 0.4));
		majorList.insertMajor(new Major("Medicine", 97, 0.8, 0.2));
		majorList.insertMajor(new Major("Cybersecurity", 80, 0.7, 0.3));

		System.out.println(majorList.searchMajor("Computer Science"));
		System.out.println(majorList.searchMajor("Engineering"));
		System.out.println(majorList.searchMajor("Medicine"));
		System.out.println(majorList.searchMajor("Cybersecurity"));

		System.out.println(majorList.deleteMajor("Engineering"));
		System.out.println(majorList.searchMajor("Engineering"));

		majorList.insertMajor(new Major("Computer Science", 85, 0.7, 0.3));
		System.out.println("---------------------------------------------------");

	}

}
