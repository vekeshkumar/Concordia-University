package assignments.Assign3;

import java.util.Scanner;

public class Course implements DirectlyRelatable{
	private String courseID;
	private String courseName;
	private double credit;
	private String preReqID;
	private String coReqID;

	/**
	 * Parameterized Constructor
	 * @param courseId
	 * @param crName
	 * @param cred
	 * @param pRID
	 * @param cRID
	 */
	public Course(String courseId, String crName, double cred, String pRID, String cRID) {
		courseID = courseId;
		courseName = crName;
		credit = cred;
		preReqID= pRID;
		coReqID = cRID;

	}
	/**
	 * Copy Constructor with two parameters
	 * @param cours Course Object
	 * @param value String value
	 */
	public Course(Course cours, String value) {
		courseID = value;
		courseName = cours.courseName;
		credit = cours.credit;
		preReqID = cours.preReqID;
		coReqID = cours.coReqID;		
		//Course newObject = new Course(value,cours.courseName,cours.credit,cours.preReqID,cours.coReqID);
	}
	/**
	 *  Clone Method 
	 */
	public Course clone() {
		System.out.println("Enter the new course ID");
		Scanner sc = new Scanner(System.in);
		String value = sc.next();
		sc.close();
		return new Course(this, value); 

	}

	/**
	 * The method isDirectlyRelated that takes in another Course object C and should return true if
			C is pre-requisite or co-requisite of the current course object, or vise versa (hence
			the courses are directly related); otherwise it returns false.
	 */
	@Override
	public boolean isDirectlyRelatable(Course C) {
		//Check C is pre or co of current course Object or current course object is pre or co of C.
		return( (this.coReqID.equals(C.coReqID)||(this.preReqID.equals(C.preReqID))|| 
				(this.courseID.equals(C.preReqID)|| this.courseID.equals(C.coReqID)))
				);
	}
	/**
	 * Equals method to compare the objects
	 */
	@Override
	public boolean equals(Object otherObject) {

		if (otherObject == null)
			return false;
		else if (getClass( ) != otherObject.getClass( ))
			return false;
		else
		{
			Course otherItem = (Course) otherObject;
			return ((courseID.equalsIgnoreCase(otherItem.courseID) 
					&& courseName.equalsIgnoreCase(otherItem.courseName)) &&
					(coReqID.equalsIgnoreCase(coReqID) && ( preReqID.equalsIgnoreCase(preReqID)))
					&& (credit==otherItem.credit));
		}
	}
	//Getter and Setter Methods
	public String getCourseID() {
		return courseID;
	}
	public void setCourseID(String courseID) {
		this.courseID = courseID;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public double getCredit() {
		return credit;
	}
	public void setCredit(double credit) {
		this.credit = credit;
	}
	public String getPreReqID() {
		return preReqID;
	}
	public void setPreReqID(String preReqID) {
		this.preReqID = preReqID;
	}
	public String getCoReqID() {
		return coReqID;
	}
	public void setCoReqID(String coReqID) {
		this.coReqID = coReqID;
	}
	@Override
	public String toString() {
		return "Course [courseID=" + courseID + ", courseName=" + courseName + ", credit=" + credit + ", preReqID="
				+ preReqID + ", coReqID=" + coReqID + "]";
	}

}
