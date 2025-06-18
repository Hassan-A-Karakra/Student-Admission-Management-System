package application;

public class SNode {
	private Student Student;
	private SNode next;

	public SNode(Object element) {
		this(element, null);
	}

	public SNode(Object element, SNode next) {
		this.Student = Student;
		this.next = next;

	}

	public Object getElement() {
		return Student;
	}

	public void setElement(Object element) {
		this.Student = Student;
	}

	public SNode getNext() {
		return next;
	}

	public void setNext(SNode next) {
		this.next = next;
	}

}
