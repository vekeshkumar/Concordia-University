package assignments.Assign1.Part2;
import java.util.InputMismatchException;
import java.util.Scanner;

//-------------------------------------------------
//Assignment 1
// Vekesh Kumar Dhayalan
// Written by : Vekesh Kumar, 40152354
//-------------------------------------------------

public class Book {
	//4 attributes
	private String title;
	private String name;
	private long ISBN;
	private double price;
	static int noOfObjectsCreated=0;
	//parameterized constructor
	public Book(String aName, String bTitle, long ISBNNo, double bPrice) {
		this.name = aName;
		this.title = bTitle;
		this.ISBN = ISBNNo;
		this.price = bPrice;
		noOfObjectsCreated+=1;

	}
	//default constructor
	public Book() {
		title = "Interstellar";
		name= "Christopher Nolan";
		ISBN = 934459890;
		price=45.55;
	}
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getISBN() {
		return ISBN;
	}

	public void setISBN(long iSBN) {
		ISBN = iSBN;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "Author: "+name +"\n"
				+ "Title: "+title+"\n"
				+ "ISBN: "+ISBN+" #\n"
				+ "Price: $ "+price+" \n";
	}

	//function to find number of created book objects and return no books created
	public static int findNumberOfCreatedBooks(Book bookObj) {
		return bookObj.noOfObjectsCreated;
	}

	//Function to find whether two book objects are equal
	public boolean equals(Book bookObj) {
		// TODO Auto-generated method stub
		return this.price==bookObj.price && this.ISBN==(bookObj.ISBN);
	}

	public static void findBooksBy(String authName, Book[] bookArr) {
		//get all book objects of author
		boolean noBookFounds =true;
		for(int i=0;i<bookArr.length;i++) {
			if(bookArr[i]!=null && bookArr[i].getName().equalsIgnoreCase(authName)) {
				noBookFounds =false; 
				System.out.println(bookArr[i].toString());
			}
		}
		if(noBookFounds) {
			System.out.println("No books found for searched author");
		}
	}
	public static void findCheaperThan(double bPrice, Book[] bookArr) {
		boolean noBookFounds =true;
		for(int i=0;i<bookArr.length;i++) {
			if(bookArr[i]!=null && bookArr[i].getPrice()<bPrice) {
				noBookFounds =false; 
				System.out.println(bookArr[i].toString());
			}
		}
		if(noBookFounds) {
			System.out.println("No books found cheaper than give price");
		}

	}
	private static boolean newbookEntry(String name2, String title2, long iSBN2, double price2, Book[] bookObj, int arrayCount) {

		Book bookValue=  new Book(name2,title2,iSBN2,price2);
		bookObj[arrayCount] =bookValue;
		System.out.println("Book: #"+arrayCount+1);
		System.out.print(bookObj[arrayCount].toString()+"\n");
		return true;
	}
	private static boolean checkPassword(String input,String orgPwd) {
		return (input.equalsIgnoreCase(orgPwd)?true:false);
	}
	public static void main(String[] args) {
		boolean continueOperation = true;
		Scanner sc= new Scanner(System.in);
		final String password="password";
		int arrayCount=0;
		int attemptCount =0;
		System.out.println("Welcome to the Book Store");
		System.out.println("Please enter the maximum number of books that store can hold");
		try {
			int maxBookCount = sc.nextInt();
			Book inventory[]= new Book[maxBookCount];
			while(continueOperation) {
				System.out.println("What do you want to do? \n"
						+ " 1. Enter new books (password required) \n"
						+ " 2. Change information of a book (password required) \n"
						+ " 3. Display all books by a specific author \n"
						+ " 4. Display all books under a certain a price \n"
						+ " 5. Quit \n "
						+ "Please enter your choice >");
				int option = sc.nextInt();
				sc.nextLine();
				outerLoop:
				switch (option) {
				case 1:
					int wrongAttempts=0;
					while(wrongAttempts<3) {
						System.out.println("Attempt "+wrongAttempts+":");
						String input = sc.nextLine();
						if(!checkPassword(input,password)){
							attemptCount++;
							wrongAttempts++;
							if(attemptCount>=12) {
								System.out.println("Program detected suspicious activities and will terminate immediately!");
								continueOperation=false;
							}
						}else {
							System.out.println("Please enter how many books to be added:");
							int bookInputCount = sc.nextInt();
							sc.nextLine();

							if(bookInputCount>inventory.length && arrayCount ==0) {
								System.out.println("You can add only maximum of  "+(inventory.length-arrayCount)+" books");
								bookInputCount-=(inventory.length-arrayCount);
							}
							if(arrayCount==inventory.length){
								System.out.println("Sorry Max limit reached, No more books can be added in the inventory");
							}
							if(bookInputCount>0) {
								while(arrayCount<=inventory.length && arrayCount<bookInputCount) {
									System.out.println("Please enter the details in order of 1.Author 2.title 3.ISBN 4.price");
									String name = sc.nextLine();
									String title = sc.nextLine();
									long ISBN = sc.nextLong();
									double price = sc.nextDouble();
									boolean isSuccess =newbookEntry(name,title, ISBN,price, inventory,arrayCount);
									sc.nextLine(); 
									if(isSuccess)
										arrayCount++;
								}

							}else {
								System.out.println("No more space to add books");
							}
							break;
						}
					}				 

					break;
				case 2:
					//another while loop for this case. and another switch inside this or create another function for this scenario
					int  wrongAttemp2=0;
					while(wrongAttemp2<3) {
						System.out.println("Attempt "+wrongAttemp2+":");
						String input2 = sc.nextLine();
						if(!checkPassword(input2,password)){
							wrongAttemp2++;
						}else {
							boolean continueUpdate = true;
							while(continueUpdate) {
								System.out.println("Please enter the Book number:");
								int index = sc.nextInt();
								//try index again till its correct
								boolean isValidIndex = (index-1 < inventory.length && inventory[index-1]!=null && index >0);
								if(!isValidIndex) {
									System.out.println("Please enter a valid input");	
									System.out.println("Please enter 1 to continue to try with different Book index or Enter 2 to quit ");
									int  choice= sc.nextInt();	
									switch (choice) {
										case 1:
											continue;
										case 2:
											continueUpdate=false;										
											break outerLoop;
										default :										
											break;
									}
																
								}
								else {
									boolean quitChange =false;
									while(!quitChange) {
										System.out.println("Book: #"+index+"\n"+inventory[index-1].toString());
										System.out.println("What information would you like to change? \n 1. author \n 2. title \n 3. ISBN  \n 4. price \n 5. Quit \n Enter your choice >");
										int intOpt = sc.nextInt();
										sc.nextLine();
										innerLoop:
										switch (intOpt) {
										case 1:
											//change the author
											System.out.println("Please enter the author:");
											String authName = sc.nextLine();
											inventory[index-1].setName(authName);
											System.out.println("Book: #"+index+"\n"+inventory[index-1].toString());
											break;
										case 2:
											//change the title
											System.out.println("Please enter the title of the book:");
											String tempTitle = sc.nextLine();
											inventory[index-1].setTitle(tempTitle);
											System.out.println("Book: #"+index);
											System.out.println(inventory[index-1].toString());
											break;
										case 3:
											//change the ISBN
											System.out.println("Please enter the ISBN #");
											long tempISBN = sc.nextLong();
											inventory[index-1].setISBN(tempISBN);
											System.out.println("Book: #"+index);
											System.out.println(inventory[index-1].toString());
											break;
										case 4:
											//change the price
											System.out.println("Please enter the price $:");
											double tempPrice = sc.nextDouble();
											inventory[index-1].setPrice(tempPrice);
											System.out.println("Book: #"+index);
											System.out.println(inventory[index-1].toString());
											break;
										case 5:
											//Quit
											quitChange =true;										
											//go to main menu:
											break outerLoop;
										default:
											System.out.println("Please enter a valid input between 1 and 5");
											break;
										}
									}
									
								}
							}
						}
					}		
					break;
				case 3:
					String authName = sc.nextLine();
					findBooksBy(authName, inventory);
					break;				
				case 4:
					double cheaperValue = sc.nextDouble();
					findCheaperThan(cheaperValue, inventory);
					break;
				case 5:
					System.out.println("Existing the application, Bye!!");
					continueOperation = false;
					break;
				default:
					break;
				}
			}
		} catch (InputMismatchException e) {
			System.out.println("Not a number! Exiting the application");
		}
		
		//Create array of books object
		
		sc.close();
	}
}
//After 12 incorrect password attempts Msg before exit- Program detected suspicious activities and will terminate immediately!

/*
Nolan Christopher
Interstellar
123456789
50.2


Nolan
Origin
123456789
10.00


Dan Brown 
Da Vinci Code
34567891
12.99
*/