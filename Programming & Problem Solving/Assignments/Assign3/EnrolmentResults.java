package assignments.Assign3;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import assignments.Assign3.CourseList.CourseNode;

/**
 * Vekesh Kumar Dhayalan 40152354
 * Assignment 3 
 * 21-04-21*/
/**
 * 
 * @author Vekesh
 */
//Exception class to show no element is present
class NoSuchElementException extends Exception{
	private static final long serialVersionUID = 1L;
	public NoSuchElementException() {
		super("Index is not valid; So element doesn't exist.");
	}
	public NoSuchElementException(String msg) {
		super("No value exits at the index"+msg);

	}
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return super.getMessage();
	}
}

//Course List class is a linkedlist class which can store the information of the courses in the inner class CourseNode
class CourseList {
	/**
	 * 
	 * @author Vekesh
	 *
	 */
	class CourseNode{
		private Course courseObject;
		private CourseNode CourseNodeObj;
		private CourseNode cNPtrObj;
		//pointer to course node object
		/**
		 * Default Constructor
		 */
		public CourseNode() {
			// TODO Auto-generated constructor stub
			courseObject = null;
			CourseNodeObj= null;
			cNPtrObj = null;
		}
		/**
		 * Parametrized Constructor
		 * @param courseObj
		 * @param CNObj
		 */
		public CourseNode(Course courseObj, CourseNode CNObj) {
			courseObject = courseObj;
			CourseNodeObj = CNObj;
		}
		/**
		 * Copy Constructor
		 * @param cObj
		 */
		public CourseNode(CourseNode cObj) {
			CourseNodeObj = cObj;
			cNPtrObj = cObj.cNPtrObj;
			courseObject = cObj.courseObject;
		}
		/**
		 * Clone method
		 */
		public CourseNode clone() {
			return new CourseNode(this);
		}
		//Getter and setter object
		public Course getCourseObject() {
			return courseObject;
		}
		public void setCourseObject(Course courseObject) {
			this.courseObject = courseObject;
		}
		public CourseNode getCourseNodeObj() {
			return CourseNodeObj;
		}
		public void setCourseNodeObj(CourseNode CourseNodeObj) {
			this.CourseNodeObj = CourseNodeObj;
		}
		public CourseNode getcNPtrObj() {
			return cNPtrObj;
		}
		public void setcNPtrObj(CourseNode cNPtrObj) {
			this.cNPtrObj = cNPtrObj;
		}
	}
	private CourseNode head; //point to the first node in this list object
	private CourseNode tail;
	private  int size; //current size of the list (how many nodes are in the list);

	public CourseNode getTail() {
		return tail;
	}
	public void setTail(CourseNode tail) {
		this.tail = tail;
	}
	public CourseNode getHead() {
		return head;
	}
	public void setHead(CourseNode head) {
		this.head = head;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	//default constructor
	public CourseList() {
		head = null;
		tail = null;
		size = 0;
	}
	//copy constructor
	public CourseList(CourseList clObj) {
		this.head = clObj.head;
		this.size = clObj.size;
		this.tail = clObj.tail;
	}
	/**
	 * Add to Start - Adding the node value at the start
	 * @param courseObj
	 */
	//Handling the insert - function to add the element in the start of the linkedlist
	public void  addToStart(Course courseObj) {
		//creates a node with that passed object and inserts this node at the head of the list;
		if(find(courseObj.getCourseID(),false)==null) {
			head = new CourseNode(courseObj, head);
			size++;
		}else {
			System.out.println("\n The course "+courseObj.getCourseName()+" is  already present in the list, you cannot add this course again.\n");
		}
	}
	/**
	 * Inserting the node at the particular index
	 * @param courseObj
	 * @param index
	 * @throws NoSuchElementException
	 */
	public void insertAtIndex(Course courseObj, int index) throws NoSuchElementException {
		//index to be 0 or size -1
		if(index==-1) {
			throw new NoSuchElementException();
			//Terminate the program exit or stop
		}else {
			CourseNode  target, previous;
			/*Otherwise, node pointed by that index is deleted from the list. The method must properly handle all special cases.*/
			target = getNode(0, index, head);
			if(index ==0) {
				head = target;
			}
			CourseNode newNode = new CourseNode(courseObj,target);
			previous = target.getcNPtrObj();
			if(previous!=null) {
				previous.setCourseNodeObj(newNode);
			}
				
			target.setCourseObject(newNode.getCourseObject());
			newNode.setcNPtrObj(previous);
			newNode.setCourseNodeObj(target);
			
			//System.out.println(newNode.getCourseObject().toString());
			//System.out.println("New Node has been inserted in the required index successfully!!.");
		}

	}
	/**
	 * Remove the node from the index
	 * @param index
	 * @throws NoSuchElementException
	 */
	public void deleteFromIndex(int index) throws NoSuchElementException  {
		if(index==-1) {
			throw new NoSuchElementException();
			//Terminate the program exit or stop
		}else {
			CourseNode target, previous,next;
			target = getNode(0, index, head);
			/*Otherwise, node pointed by that index is deleted from the list. The method must properly handle all special cases.*/
			// handle a few special cases
			if (index == - 1)
				tail = tail.getcNPtrObj();
			if (index == 0)
				head = head.getCourseNodeObj();
			// now delete the object
			if(index>1)
				previous = getNode(0, index-2, head).getCourseNodeObj();
			else
				previous = target.getcNPtrObj().CourseNodeObj;			
			System.out.println(previous.courseObject.toString());
			next = target.getCourseNodeObj();
			System.out.println(next.getCourseObject().toString());

			if (previous != null)
				previous.setCourseNodeObj(next);

			if (next!= null)
				next.setcNPtrObj(previous);

			size-= 1;
			//System.out.println("Required node has been deleted from the list from the index.");
		}

	}
	/**
	 * Delete method  to remove the node from the start
	 * @return
	 */
	public boolean deleteFromStart() {
		//deletes the first node in the list
		if(head!=null) {
			head = head.CourseNodeObj;
			return true;
		}else {
			return false;
		}

	}
	/**
	 * Replace the  course object based on the index
	 * @param courseOb
	 * @param indx
	 * @throws NoSuchElementException 
	 */
	public void replaceAtIndex(Course courseOb, int indx) throws NoSuchElementException {
		if(indx==-1) {
			System.out.println("Nothing can be replaced");
		}else {
			deleteFromIndex(indx);
			insertAtIndex(courseOb, indx);
			//System.out.println("The given node is replaced with the given value successfully!.");
		}
		
		//the object in the node at the passed index is to be replaced by the passed object;

	}
	/**
	 * Size - get the size of the list
	 * @return count
	 */
	public int size( )
	{   
		int count = 0;
		CourseNode position = head;
		while (position != null)
		{
			count++;
			position = position.CourseNodeObj;
		}
		return count;
	}
	/**
	 * find -Search for the courseId in the courseNode
	 * @param courseID
	 * @return
	 */
	public CourseNode find(String courseID, boolean isPrint) {
		CourseNode position = head;
		String itemAtPosition;
		int count=0;
		while (position != null)
		{
			itemAtPosition = position.courseObject.getCourseID();
			count++;
			if (itemAtPosition.equals(courseID)) {
				if(isPrint)
					System.out.println(count);
				return position;				
			}	                
			position = position.CourseNodeObj;	
		}					
		return null; //target was not found
	}
	/**
	 * Contains method to check whether the node is present in the list
	 * @param courseID
	 * @return
	 */
	public boolean contains(String courseID, boolean isPrint) {
		/*The method returns true if a course with that courseID is in the list;
		otherwise, the method returns false.*/
		return (find(courseID, isPrint)!=null); 
	}
	@Override
	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
		return super.equals(arg0);
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	public void outputValues() {
		CourseNode position = head;
		while(position!=null) {
			System.out.println(position.courseObject.toString());
			position = position.CourseNodeObj;
		}
	}
	protected CourseNode getNode(int current, int selected, CourseNode node){

		while(current != selected){
		
			node = node.getCourseNodeObj();
			current += 1;
		}
		//System.out.println(node.courseObject.to);
		return node;
	}
}

public class EnrolmentResults{
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Hi, This small application is developed for the assignment 3.\n");
		//Two empty list  from course list class
		CourseList cL1 = new CourseList();
		//Using copy constructor
		//we need to handle the duplicates
		Scanner scan = null;
		Scanner fileInp= null;
		try {
			scan = new Scanner(new FileInputStream("C:\\Users\\Vekesh\\eclipse-workspace\\COMP6481_PSAP\\src\\assignments\\Assign3\\Syllabus.txt"));
			int count =1;
			String courseId=null;
			String courseName= null;
			int credit=0;
			String preRequisite= null;
			String coRequisite=null;
			int index=0;
			while(scan.hasNextLine()) {
				String value = scan.nextLine();
				/**
				 * Pattern for reading the Course Id, Course Name, and credit .First value is the till first space is the course id
				 * for second trim all the space till the word comes and till next space is seen ([a-zA-Z]+(?:_[a-zA-Z]+)*) last word is the credit*/
				switch(count) {
				case 1:
					Pattern p1 =  Pattern.compile("^([\\S]+)");
					Matcher matcher = p1.matcher(value);
					if(matcher.find()) {
						courseId = matcher.group();
					}
					Pattern p2 =  Pattern.compile("(\\S*[a-zA-Z]+(?:_[a-zA-Z]+)\\S*)");
					Matcher matcher2 = p2.matcher(value);
					if(matcher2.find()) {
						courseName = matcher2.group();
					}
					credit = Integer.parseInt(value.substring(value.length()-1, value.length()));

					break;
				case 2:
					//Pattern to read the Pre-requisite
					Pattern p3 = Pattern.compile("\\S*[a-zA-Z0-9]+\\S");
					Matcher matcher3 = p3.matcher(value);
					if(matcher3.find()) {
						preRequisite = matcher3.group();
					}
					break;
				case 3:
					Pattern p4 = Pattern.compile("\\S*[a-zA-Z0-9]+\\S");
					Matcher matcher4 = p4.matcher(value);
					if(matcher4.find()) {
						coRequisite = matcher4.group();
					}
					count=0;
					index++;				
					cL1.addToStart(new Course(courseId,courseName,credit,preRequisite,coRequisite));
					courseId = null;
					courseName = null;
					credit =0;
					preRequisite =null;
					coRequisite = null;
					if(scan.hasNext()) {
						scan.nextLine();
					}
					break;
				}
				count++;
			}
			CourseList cL2= new CourseList(cL1);
			//cL1.outputValues();
			if(cL1.equals(cL2)) {
				System.out.println("The two course Lists are equal");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean isExit = true;

		//LinkedList operations

		//InsertAt Index
		/*try {
			//Replacing at the index 
			/*
			cL1.replaceAtIndex(new Course("SOEN528","System_Hardwarezzzzz",3,"COMP248","COMP376"), 5);
			System.out.println("After replacing COMP218");
			cL1.outputValues();
			*/
			//Deleting
			/*
			cL1.deleteFromIndex(5);
			System.out.println("after deletings");
			cL1.outputValues();
			 
			*/
		
			//inserting
			 /*
			cL1.insertAtIndex(new Course("COMP538","System_Hardware",3,"COMP248",null), 5);
			System.out.println("After inserting");
			cL1.outputValues();
			//cL1.insertAtIndex(new Course("COMP548","Advanced_Operating_Systems",3,"COMP348",null), -1);
			

		} catch (NoSuchElementException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/

		while(isExit) {			
			System.out.println("*************************MENU******************************\n Please enter the option : \n 1.To Process the file : \n 2.To Search for the course Id. \n 3. Exit ");
			scan = new Scanner(System.in);
			int type = scan.nextInt();
			scan.nextLine();
			switch(type) {
			case 1:
				//Requests
				System.out.println("Please enter the name of file need to be processed:\n");	
				String FileName = scan.nextLine();
				try {
					fileInp =  new Scanner(new FileInputStream("C:\\Users\\Vekesh\\eclipse-workspace\\COMP6481_PSAP\\src\\assignments\\Assign3\\"+FileName));
					ArrayList<String> finishedCourses= new ArrayList<String>();
					ArrayList<String> requestedCourses= new ArrayList<String>();
					boolean isFC=false;
					boolean isRC= false;
					while(fileInp.hasNext()) {
						String value = fileInp.next();
						if(value.equalsIgnoreCase("Finished")) {
							isFC = true;							
						}else if(value.equalsIgnoreCase("Requested")) {
							isFC = false;
							isRC = true;
						}
						if(isFC && !value.equalsIgnoreCase("Finished")) {
							finishedCourses.add(value);							
						}
						if(isRC && !value.equalsIgnoreCase("Requested") ) {
							requestedCourses.add(value);
						}
					}	
					if(requestedCourses.size()<=0) {
						System.out.println("No enrolment courses found.");
					}else {
						for(int i=0;i<requestedCourses.size();i++) {
							String id = requestedCourses.get(i);
							if(cL1.contains(id,false)) {
								CourseNode cnObj = cL1.find(id,false);
								String preRequestId = cnObj.getCourseObject().getPreReqID();
								String coRequestId = cnObj.getCourseObject().getCoReqID();

								if(finishedCourses.contains(preRequestId)){
									String extraInfo = (finishedCourses.contains(coRequestId)?(" and "+coRequestId) :"");
									System.out.println("Student can enroll in "+ id+" course as he/she has completed the pre-requisite "+preRequestId+extraInfo+".");
								}else if(requestedCourses.contains(coRequestId)) {
									//check if the co-requisite of the course if found in the requested Course List
									System.out.println( "Student can enrol in "+id+" course as he/she is enrolling for co-requisite  "+coRequestId+".");
								}
								else {
									System.out.println( "Student can't enroll in "+id+" course as he/she doesn't have sufficient background needed.");
								}
							}
							//for each  requested course, find the preRequest and coRequest ids
							//Also check if its present in the finished list. if not print the  message.
						}
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					System.out.println("The requested file is not in the directory, please  provide the correct file name.");
					//e.printStackTrace();
				}
				break;
			case 2:
				System.out.println("Please enter the courseId to search in the list");
				String reqId = scan.nextLine();

				if(cL1.contains(reqId,true)) {
					System.out.println("The requested Course Id is present in the course List");
				}else {
					System.out.println("The requested course id is  not available in the course list. PLease try again with new course id");
				}
				/*
				 * Prompt the user to enter a few courseIDs and search the list that you created
					from the input file for these values. Make sure to display the number of iterations performed.
				 */
				break;
			case 3:
				scan.close();
				System.out.println("Terminating the application, Bye!!");
				System.exit(0);
				break;
			}
		}
		scan.close();

	}
}

