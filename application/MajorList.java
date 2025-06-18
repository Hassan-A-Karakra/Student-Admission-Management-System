package application;

class MajorLinkedList {

	MajorNode head;
	private DoubleLinkedList major;

	public void insertMajor(Major newMajor) {// O(n)
		MajorNode newNode = new MajorNode(newMajor);

		// if the list is empty
		if (head == null || newMajor.getName().compareTo(head.data.getName()) < 0) {
			newNode.next = head;
			if (head != null) {
				head.prev = newNode;
			}
			head = newNode;
		} else {
			MajorNode current = head;

			// find the correct location to list the new special
			while (current.next != null && newMajor.getName().compareTo(current.next.data.getName()) > 0) {
				current = current.next;
			}

			// insert the new specialty into the correct location
			newNode.next = current.next;
			if (current.next != null) {
				current.next.prev = newNode;
			}
			newNode.prev = current;
			current.next = newNode;
		}
	}

	public boolean deleteMajor(String majorName) {/// O(n)
		if (head == null) {
			return false; // the list mean is empty
		}

		// if the major is at the head
		if (head.data.getName().equalsIgnoreCase(majorName)) {
			head = head.next; // update head to the next node
			if (head != null) {
				head.prev = null; // set the new head prev to null
			}
			return true;
		}

		MajorNode current = head;
		while (current != null && !current.data.getName().equalsIgnoreCase(majorName)) {
			current = current.next;
		}

		// if the major is not found
		if (current == null) {
			return false;
		}

		if (current.next != null) {
			current.next.prev = current.prev; // update prev of the next node
		}
		if (current.prev != null) {
			current.prev.next = current.next; // update next of the previous node
		}

		return true; // major was successfully deleted
	}

	public Major searchMajor(String name) {/// O(n)
		MajorNode current = head;
		while (current != null) {
			if (current.data.getName().equalsIgnoreCase(name)) {
				return current.data; // return the major if found
			}
			current = current.next;
		}
		return null; // major not found
	}

}