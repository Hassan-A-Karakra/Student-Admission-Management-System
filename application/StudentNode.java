package application;

class StudentNode {
	Student data;
	StudentNode next;

	public StudentNode(Student data) {
		this.data = data;
		this.next = null;
	}

	public Student getData() {
		return data;
	}

	public void setData(Student data) {
		this.data = data;
	}

	public void setNext(StudentNode next) {
		this.next = next;
	}

	public Student getElement() {
		// TODO Auto-generated method stub
		return null;
	}

	public StudentNode getNext() {
		// TODO Auto-generated method stub
		return null;
	}
}
