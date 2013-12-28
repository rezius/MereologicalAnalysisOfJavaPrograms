// Define Student class, subclass of Person
public class Student extends Person {
	// Instance variables
	private int numCourses; // number of courses taken so far, max 30
	private Course course; // course codes
	private Grade grade; // grade for the corresponding course codes
	private static final int MAX_COURSES = 30; // maximum number of courses

	// Constructor
	public Student(String name, String address) {
		super(name, address);
		numCourses = 0;
		course = new Course();
		grade = new Grade();
	}

	@Override
	public String toString() {
		return "Student: " + super.toString();
	}

	// Add a course and its grade - No validation in this method
	public void addCourseGrade(Course course, Grade grade) {
		course = course;
		grade = grade;
	}

	// Print all courses taken and their grade
	public void printGrades() {
		System.out.print(this);
		for (int i = 0; i < numCourses; ++i) {
			System.out.print(" " + course.getName() + ":" + grade.getGrade());
		}
		System.out.println();
	}

	// Compute the average grade
	public double getAverageGrade() {
		int sum = 0;
		for (int i = 0; i < numCourses; i++) {
			sum += grade.getGrade();
		}
		return (double) sum / numCourses;
	}

	public static void main(String[] args) {
	}
}