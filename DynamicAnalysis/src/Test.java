// A test driver program for Person and its subclasses
public class Test {
	public static void main(String[] args) {
		// Test Student class
		Student s1 = new Student("Tom", "Douglas");
		
		Course c1 = new Course("IM101","Management");
		s1.addCourseGrade(c1, new Grade(90,"Management"));
		
		Student s2 = new Student("John", "Quick");
		s2.addCourseGrade(c1, new Grade(70,"Management"));
		
		s1.printGrades();
		System.out.println("Average is " + s1.getAverageGrade());
		
		s2.printGrades();
		System.out.println("Average is " + s2.getAverageGrade());

		// Test Teacher class
		Teacher t1 = new Teacher("Paul Tan", "8 sunset way");
		System.out.println(t1);

		String Course_1 = "IM101";
		String Course_2 = "IM102";
		String Course_3 = "IM103";

		if (t1.addCourse(Course_1)) {
			System.out.println(Course_1 + " added.");
		} else {
			System.out.println(Course_1 + " cannot be added.");
		}

		if (t1.removeCourse(Course_1)) {
			System.out.println(Course_1 + " removed.");
		} else {
			System.out.println(Course_1 + " cannot be removed.");
		}
	}
}
