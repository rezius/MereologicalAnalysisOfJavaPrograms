public class Grade {

	private int grade;
	private String subject;

	public Grade() {
	}

	public Grade(int grade, String subject) {
		super();
		this.grade = grade;
		this.subject = subject;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

}
