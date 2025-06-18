package application;

public class DNode {
	private Object element;
	private DNode next, prev;

	public DNode(Object element) {
		this(element, null, null);
	}

	public DNode(Object element, DNode next, DNode prev) {
		this.element = element;
		this.next = next;
		this.prev = prev;
	}

	public Object getElement() {
		return element;
	}

	public void setElement(Object element) {
		this.element = element;
	}

	public DNode getNext() {
		return next;
	}

	public void setNext(DNode next) {
		this.next = next;
	}

	public DNode getPrev() {
		return prev;
	}

	public void setPrev(DNode prev) {
		this.prev = prev;
	}

}
