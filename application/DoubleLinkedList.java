package application;

public class DoubleLinkedList {
	private DNode front, back;
	private int size;

	// constructor
	public DoubleLinkedList() {
	}

	// returns the size of the list
	public int size() {
		return size;
	}

	// returns the first element in the list
	public Object getFirst() {
		if (size == 0)
			return null;
		return front.getElement();
	}

	// returns the last element in the list
	public Object getLast() {
		if (size == 0)
			return null;
		return back.getElement();
	}

	// getter for the front node
	public DNode getFront() {
		return front;
	}

	// setter for the front node
	public void setFront(DNode front) {
		this.front = front;
	}

	// getter for the back node
	public DNode getBack() {
		return back;
	}

	// Setter for the back node
	public void setBack(DNode back) {
		this.back = back;
	}

	// Retrieves the element at a specific index in the list
	public Object get(int index) {
		if (index >= size || index < 0)
			return null;
		else if (index == 0)
			return getFirst();
		else if (index == size - 1)
			return getLast();

		DNode current = front;
		for (int i = 0; i < index; i++)
			current = current.getNext();
		return current.getElement();
	}

	// Adds a node to the beginning of the list
	public void addFirst(DNode node) {
		if (size == 0)
			front = back = node;
		else {
			node.setNext(front);
			front.setPrev(node);
			front = node;
		}
		size++;
	}

	// Adds a node to the end of the list
	public void addLast(DNode node) {
		if (size == 0)
			front = back = node;
		else {
			back.setNext(node);
			node.setPrev(back);
			back = node;
		}
		size++;
	}

	// Adds a node to the end of the list (alias for addLast)
	public void add(DNode node) {
		addLast(node);
	}

	// Adds a node at a specific index in the list
	public void add(DNode node, int index) {
		if (index <= 0)
			addFirst(node);
		else if (index >= size)
			addLast(node);
		else {
			DNode current = front;
			for (int i = 0; i < index - 1; i++)
				current = current.getNext();
			node.setNext(current.getNext());
			current.getNext().setPrev(node);
			current.setNext(node);
			node.setPrev(current);
			size++;
		}
	}

	// Removes the first node in the list
	public boolean removeFirst() {
		if (size == 0)
			return false;
		else if (size == 1) {
			front = back = null;
			size--;
			return true;
		}
		front = front.getNext();
		front.setPrev(null);
		size--;
		return true;
	}

	// Removes the last node in the list
	public boolean removeLast() {
		if (size == 0)
			return false;
		back = back.getPrev();
		back.setNext(null);
		size--;
		return true;
	}

	// Removes a node at a specific index in the list
	public boolean remove(int index) {
		if (index == 0)
			return removeFirst();
		else if (index == size - 1)
			return removeLast();
		DNode current = front;
		for (int i = 0; i < index; i++)
			current = current.getNext();
		current.getPrev().setNext(current.getNext());
		current.getNext().setPrev(current.getPrev());
		size--;
		return true;
	}
}