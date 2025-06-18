package application;

class MajorNode {
	Major data;
	MajorNode next, prev;

	public MajorNode(Major data) {
		this.data = data;
		this.next = null;
		this.next = prev;

	}

	public Major getData() {
		return data;
	}

	public MajorNode getPrev() {
		return prev;
	}

	public void setPrev(MajorNode prev) {
		this.prev = prev;
	}

	public void setData(Major data) {
		this.data = data;
	}

	public MajorNode getNext() {
		return next;
	}

	public void setNext(MajorNode next) {
		this.next = next;
	}

}