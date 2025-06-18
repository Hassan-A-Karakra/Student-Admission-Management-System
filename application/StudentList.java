package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class StudentLinkedList {
	StudentNode head;
	private DoubleLinkedList major;

	//// time Complexity O(n)
// inserts a new student node at the end of the list
	public void insertStudent(Student student) {
		StudentNode newNode = new StudentNode(student);
		if (head == null) {
			/// if the list is empty ----> set the head to point to the new node
			head = newNode;
			newNode.next = head;
		} else if (head.data.getAdmissionMark() <= student.getAdmissionMark()) {
			//// **if the new student admission mark is higher than or equal to the head,
			// make it the new head
			StudentNode last = head;
			while (last.next != head) { // find the last node
				last = last.next;
			}
			newNode.next = head;
			head = newNode;
			last.next = head; // update the last node to point to the new head
		} else {
			// insert the student in the appropriate position based on admission mark
			StudentNode current = head;
			while (current.next != head && current.next.data.getAdmissionMark() > student.getAdmissionMark()) {
				current = current.next;
			}
			newNode.next = current.next;
			current.next = newNode;
		}
	}

	// time Complexity O(n)
	// deletes a student node by student ID
	public boolean deleteStudent(String studentId) {
		if (head == null) {
			return false; // the list is empty
		}

		// if the student to delete is the head
		if (head.data.getId().equals(studentId)) {
			if (head.next == head) { // only one node in the list
				head = null; // set head to null if it the only node
				return true;
			}

			// Find the last node to update its next reference
			StudentNode last = head;
			while (last.next != head) {
				last = last.next;
			}

			head = head.next; // Move head to the next node
			last.next = head; // Update the last node to point to the new head
			return true;
		}

		StudentNode current = head;
		// Loop until we find the student or complete one full cycle
		while (current.next != head && !current.next.data.getId().equals(studentId)) {
			current = current.next;
		}

		if (current.next == head) {
			return false;
		}

		// remove the node
		current.next = current.next.next;
		return true; // student deleted successfully
	}

	// Time Complexity O(n)
	// updates a student details by student ID in a circular linked list
	public boolean updateStudent(String studentId, String newName, double newTawjihiGrade,
			double newPlacementTestGrade) {
		if (head == null) {
			return false; // The list is empty
		}

		StudentNode current = head;
		do {
			if (current.data.getId().equals(studentId)) {
				current.data.setName(newName);
				current.data.setTawjihiGrade(newTawjihiGrade);
				current.data.setPlacementTestGrade(newPlacementTestGrade);
				return true;
			}
			current = current.next;
		} while (current != head); // Stop when we loop back to head

		return false; // Student with the given ID not found
	}

	// Time Complexity O(n)
	// searches for a student by ID in a circular linked list and returns the
	// student data
	public Object searchStudent(String id) {
		if (head == null) {
			return null; // the list is empty
		}

		StudentNode current = head;
		do {
			if (current.data.getId().equals(id.trim())) { // Match found
				return current.data;
			}
			current = current.next;
		} while (current != head); // stop when we loop back to the head

		return null; // student with the given ID not found
	}

	/// time Complexity O(n) //
	/// retrieves all students in a specified major
	public ObservableList<Student> getStudentsByMajor(String majorName) {
		ObservableList<Student> studentsInMajor = FXCollections.observableArrayList();
		StudentNode current = head;
		while (current != null) {
			if (current.data.getChosenMajor().equalsIgnoreCase(majorName)) {
				studentsInMajor.add(current.data);
			}
			current = current.next;
		}
		return studentsInMajor;
	}

	// Time Complexite O(1)
	/// Gets the front node of the list
	public StudentNode getFront() {
		return head;
	}

	// Time Complexity O(1)
	// Sets a new node as the head of the list
	public void setFront(StudentNode newNode) {
		this.head = newNode;
	}
}
