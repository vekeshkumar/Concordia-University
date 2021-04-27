package assignments.Assign2.part2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * 
 * @author Vekesh Kumar Dhayalan
 * @version 1.0
 *
 */
/**
 * Custom Exception class to throw exception when invalid file format other jpeg present in the folder.
 * 
 *
 */
class InvalidFileException extends Exception{
	//log file is valid and the file paths in it are valid.
	public InvalidFileException() {
		super("Error, the given File is Invalid!!");

	}
	/**
	 * Parameterized Constructor for displaying the invalid file name
	 * @param fileName
	 */
	public InvalidFileException(String fileName) {

		//System.err.println("Error, The File is Invalid");
		//“Error: Input file named XXX cannot be found.
		super("Error: Input file name "+fileName+" is invalid");
	}
	/**
	 * Overriding the existing getMessage function to get the message from constructor of Invalid File Exception class. 
	 */
	public String getMessage() {
		return super.getMessage();
	}

}
/**
 * @exception Custom exception class EmptyFolderException is throws an exception 
 * when an empty folder is identified.
 */
class EmptyFolderException extends Exception{
	/**
	 *  Default constructor
	 */
	public EmptyFolderException() {
		super("Error, the  folder is empty!!");		
	}
	/**
	 * 
	 * @param msg
	 */
	public EmptyFolderException(String msg) {
		super("Error. The folder named "+msg+" is empty!!");
		//System.err.println(msg);
	}
	/**
	 * Overriding the existing getMessage function to get the message from constructor of Invalid File Exception class. 
	 */
	public String getMessage() {
		return super.getMessage();
	}
}
/**
 * 
 * @author Vekesh
 */
class FileOperations{
	/**
	 * 
	 * @param pw
	 * @param path
	 * @throws InValidFileException and EmptyFolder Exception
	 * 
	 */
	public static void recursiveListing(PrintWriter pw, String path) throws InvalidFileException, EmptyFolderException  {
		/**
		 * Creating file object and setting the path
		 * Checking for  all the directories, subdirectories and files present in the path.
			writing the directory and files details in log.txt Recursive approach to list all files 
			if encounters invalid files or empty directory exception is thrown 
		 */
		File fileVar = new File(path);
		if(fileVar.isDirectory()) {		
			File[] filesList = fileVar.listFiles();	
			//System.out.println(filesList.length);

			for(File eachFile: filesList) {
				//if(eachFile.isDirectory() && eachFile.length()>0) {}
				if(eachFile.isDirectory()) {						
					pw.println("directory:"+eachFile.getPath());
					System.out.println("directory:"+eachFile.getPath());
					recursiveListing(pw, eachFile.getPath());
				}else {
					if(eachFile.isFile() && (eachFile.getName().contains(".jpeg")|| eachFile.getName().contains(".jpg")|| eachFile.getName().contains(".png"))) {
						System.out.println("\t file:"+eachFile.getPath());
						pw.println("\t file:"+eachFile.getPath());
					}else {
						throw new InvalidFileException(eachFile.getName());
					}						
				}
			}

		}else{
			throw new EmptyFolderException("No folder named Data found");
		}
		//read the folder then files in the folder and print them in one pattern

	}
	/**
	 * 
	 * @param pw
	 * @param logFilePath
	 * @throws IOException
	 */
	public static void processTheFiles(String logFilePath, String exceptionPath) throws InvalidFileException, EmptyFolderException, IOException {
		//read the file and get only folder file names and create html files
		Scanner scan=null;
		try {
			scan = new Scanner(new FileInputStream("C:\\Users\\Vekesh\\eclipse-workspace\\COMP6481_PSAP\\src\\assignments\\Assign2\\part2\\log.txt"));
			/**
			 * Read everyline and checking if the file contains directory and create html file
			 * Write the contents required for the html file
			 * Check any valid files present in the directory write them else throw empty folder exception  or invalid file exception
			 */
			while(scan.hasNextLine()){	
				String value = scan.nextLine();
				if(value.contains("directory:")) {
					//get value after directory:
					int length="directory:".length();
					String path = value.substring(length,value.length());					
					String op = value.replaceAll("^.*[\\/\\\\]", "").trim();
					PrintWriter out= null;
					try {						
						File  f= new File(path);
						if(f.isDirectory()) {
							File[] filesList = f.listFiles();
							int len = filesList.length;
							if(filesList.length>0) {
								out = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Users\\Vekesh\\eclipse-workspace\\COMP6481_PSAP\\src\\assignments\\Assign2\\part2\\"+op+".html")));
								System.out.println(op+".html  file is created");
								out.println("<html><head><link rel=\"stylesheet\" href=\"assignment3.css\"></head>");
								out.println("<body>");	
								out.println("<div style=\"text-align:center\">");
								out.println(" <h1>Assignment 3</h1>");
								out.println("</div>");
								int count=0;
								boolean isAdd = false;
								for(File eachFile: filesList) {
									isAdd= (count==0 || count%4==0)?(true):false;
									if(isAdd) {
										out.println("<div class=\"row\">");	
									}
									if(eachFile.isFile() && (eachFile.getName().contains(".jpeg")|| eachFile.getName().contains(".jpg")|| eachFile.getName().contains(".png"))) {
										out.println("\t <div class=\"column\">");
										out.println("\t \t <img src=\""+eachFile.getAbsolutePath()+"\">");
										out.println("\t </div>");
									}else {
										throw new InvalidFileException(eachFile.getName());
									}
									count++;
									if(len==count || count%4==0) {
										out.println("</div>");
									}
								}										
							}else {
								//throw new EmptyFolderException("Empty folder found");
								throw new EmptyFolderException(f.getName());
							}
						}
						out.println("</body></html>");
						out.close();
					} catch (IOException e) {
						// TODO: handle exception
					} catch (EmptyFolderException e) {
						out = new PrintWriter(new BufferedWriter(new FileWriter(logFilePath,true)));
						out.println(e.getMessage());
						out.close();
						System.err.println(e.getMessage());
					}
				}
			}
			scan.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
/**
 * 
 * @author Vekesh
 *
 */
public class FileManipulations {
	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {	
		/**
		 * This is the main method
		 * which is very important for
		 * execution for a java program.
		 */

		PrintWriter pw = null;
		Scanner sc = new Scanner(System.in);
		boolean isRepeating = true;
		int option;
		String dataPath ="C:\\Users\\Vekesh\\eclipse-workspace\\COMP6481_PSAP\\src\\assignments\\Assign2\\part2\\Data";
		String logFilePath ="C:\\Users\\Vekesh\\eclipse-workspace\\COMP6481_PSAP\\src\\assignments\\Assign2\\part2\\log.txt";
		String exceptionPath ="C:\\Users\\Vekesh\\eclipse-workspace\\COMP6481_PSAP\\src\\assignments\\Assign2\\part2\\";
		/**
		 * pw is the PrinterWriter object created for file writing
		 * Scanner object sc is used to read the contents from the file.
		 * isRepating variable is used for checking the operation is continuous
		 * dataPath contains the Path location of Data folder
		 * logFilePath contains the path location where the log file exist
		 * exceptionPath contains the path location , where exceptionFile is stored/created. 
		 */

		//While loop to repeat the process
		while(isRepeating) {
			System.out.println("/******************************/ \n 1.) List files\n 2.)Process files\n 3.)Exit\nPlease enter the option:");
			//reading the input based on that- switching to the functionalities.
			option = sc.nextInt();
			switch(option) {
			case 1:
				//This is the case for Listing  all the  files present in the directory
				try {
					// creating the PrintWriter object
					pw = new PrintWriter(new BufferedWriter(new FileWriter(logFilePath)));

					//try catch block
					try {
						//calling the listing function
						FileOperations.recursiveListing(pw, dataPath);
						//if successful it print this information
						System.out.println("Correct file and folders are logged in log.txts");
					} catch (EmptyFolderException e) {
						/**
						 * When exception is catched its writtent in exception log file;
						 * Also writing the message to the file and printing in the console.
						 */
						pw = new PrintWriter(new BufferedWriter(new FileWriter(exceptionPath+"exception.txt",true)));

						pw.println(e.getMessage());
						System.out.println(e.getMessage());
						//e.printStackTrace();
					}
				} catch (InvalidFileException ifEx) {
					// TODO: handle exception
					String s = ifEx.getMessage();
					System.err.println(s);
				}
				finally {
					pw.close();
				}
				break;
			case 2:
				//process files overwrite the files
				/**
				 * Checking for the log file exists then override the content of the log file with new listing values
				 * and then process the directory and file path.
				 * Also, catch the Invalid file exceptions and log the exception in the exception log file.
				 */
				File logFile = new File(logFilePath);
				if(logFile.exists()) {				
					try {
						pw = new PrintWriter(new BufferedWriter(new FileWriter(logFilePath)));
						try {
							FileOperations.recursiveListing(pw, dataPath);

						} catch (EmptyFolderException e) {
							pw = new PrintWriter(new BufferedWriter(new FileWriter(exceptionPath+"exception.txt",true)));
							pw.println(e.getMessage());
							System.out.println(e.getMessage());
						}
						//Processing the file
						pw.close();
						try {
							FileOperations.processTheFiles(logFilePath,exceptionPath);
						} catch (EmptyFolderException e) {
							PrintWriter out1 = new PrintWriter(new BufferedWriter(new FileWriter(exceptionPath+"exception.txt",true)));
							out1.println(e.getMessage());
							System.out.println(e.getMessage());
							out1.close();
						}						

					} catch (InvalidFileException e) {
						e.printStackTrace();
						String s = e.getMessage();
						System.out.println(s);
						//Log error message
					}finally {
						pw.close();
					}

				}else {
					//over write
					pw = new PrintWriter(new BufferedWriter(new FileWriter(logFilePath)));
					try {
						FileOperations.recursiveListing(pw,dataPath);

					} catch (EmptyFolderException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					catch (InvalidFileException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					try {
						FileOperations.processTheFiles(logFilePath,exceptionPath);
					} catch (EmptyFolderException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						pw = new PrintWriter(new BufferedWriter(new FileWriter(exceptionPath+"exception.txt",true)));
						pw.println(e.getMessage());
						System.out.println(e.getMessage());
					}
					catch (InvalidFileException e) {
						// TODO Auto-generated catch block
						System.err.println(e.getMessage());
					}
					finally {
						pw.close();
					}

				}

				break;
			case 3:
				//exit
				isRepeating=false;
				System.out.println("Exiting the application");
				System.exit(0);
				//close all files opened
				pw.close();
				break;
			default:
				break;
			}
		}
		sc.close();
	}
}
/*
//File f= new File("C:\\Users\\Vekesh\\eclipse-workspace\\COMP6481_PSAP\\src\\assignments\\Assign2\\part2\\"+op+".html");
//if(f.exists() && f.isFile()&& f.getName().equalsIgnoreCase(op)) {

Test cases:
-> When all given is correct log into log.txt and create html file, with folder name and with required image.
-> While creating the listing the folder with correct content is present, while processing its empty then we need to throw exception 
	 and only create html file for valid data.


Test cases:
-> When all given is correct log into log.txt and create html file, with folder name and with required image.
-> While creating the listing the folder with correct content is present, while processing its empty then we need to throw exception 
	 and only create html file for valid data.


Create log.txt file - open log.txt file


•	JavaDoc documentations 1 pt


Task 1  - 2
1.	Listing all the directories, subdirectories and files present in the path.
2.	Open log.txt 
3.	Write file structure detailed  directory and then files as show in image
4.	Recursive approach to list all files 
5.	Use exception handling while using  File class


Task2:  1 pt
1)List files, 
(2) Process files,
(3) Exit.
Create log.txt files
second process, if already present overwrite the file
exit - close all the opened files

Task3 : 2 pts
Two exception classes
Exception
InvalidFileException- check log file is valid and the file paths inside is valid
EmptyFolderException - check for empty directories
Constructors for exceptions 
default error message :“Error: Input file named XXX cannot be found."
also custom error messages- 
They must have constructors to allow a default error message  They should also allow for a custom message to be set as
error message. All the exceptions should be logged and the files which are valid must be processed

Throw exception:
•	When Data folder is not present  and record in the log file.
•	When Any folder is not empty then record in the log file
•	When its not a .jpeg file dont process it , log the exception Invalid file exception


Task 4 -  4

a. Go through each subdirectory inside folder named “Data” and create a web page
for each of the subdirectories with their respective name and an .html extension.
b. You should search for jpg files present in each subdirectory and put them in an
html file with the subdirectory name. A sample html file is provided along with
this assignment.
c. The CSS file required for presentation is also made available along with this
assignment. You should simply create HTML files like the one provided just the
file names in the <img> tag will change.
d. You should have four columns per row like the sample html file provided to you.
e. You should use the same class names as the assignment3.html file for
presentation style.

 */
