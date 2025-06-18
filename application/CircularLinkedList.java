package application;

public class CircularLinkedList {
	private SNode head;
	private SNode front, back;
	private int size;
	private int acceptedCount;
	private int rejectedCount;

	public CircularLinkedList() {
		head = null;
		size = 0;
		acceptedCount = 0;
		rejectedCount = 0;
	}

	public Object getFirst() {
		if (size == 0)
			return null;
		return front.getElement();
	}

	public Object getLast() {
		if (size == 0)
			return null;
		return back.getElement();
	}

	public void add(Student student) {
		SNode newNode = new SNode(student);
		if (head == null) {
			head = newNode;
			head.setNext(head);
			front = head;
			back = head;
		} else {
			back.setNext(newNode);
			newNode.setNext(head); // Make the new node point to the head to make the list circular
			back = newNode;
		}
		size++;
		if (student.isAccepted()) {
			acceptedCount++; // Increment accepted count if the student is accepted
		} else {
			rejectedCount++; // Increment rejected count if the student is rejected
		}
//	    System.out.println("Added student: " + student.getName() + " with ID: " + student.getId());
	}

	public int getRejectedCount() {
		int count = 0;
		SNode current = head;
		if (current == null) {
//			System.out.println("List is empty"); // check if there are no students
			return count;
		}
		do {
			Student student = (Student) current.getElement();
			if (student != null && !student.isAccepted()) { // Condition for rejecting the student, with null check
				count++;
			}
			current = current.getNext();
		} while (current != head);
//		System.out.println("Rejected Count: " + count); // Print count for verification
		return count;
	}

	// display all students in the list
	public void displayStudents() {
		if (head == null) {
//			System.out.println("The list is empty.");
			return;
		}

		SNode current = head;
		do {
			System.out.println("Student: " + ((Student) current.getElement()).getName());
			current = current.getNext();
		} while (current != head);
	}

	public void printList() {
		// TODO Auto-generated method stub

	}
}
