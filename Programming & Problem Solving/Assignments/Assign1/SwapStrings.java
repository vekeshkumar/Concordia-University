package assignments.Assign1;
import java.util.Scanner;

public class SwapStrings {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String input = sc.nextLine();
		System.out.println(swapTheStrings(input));
		sc.close();
	}
	public static int convertSingleDigit(int n) {
		int sum = 0; 
	    while(n > 0 || sum > 9) 
	    { 
	        if(n == 0) 
	        { 
	            n = sum; 
	            sum = 0; 
	        } 
	        sum += n % 10; 
	        n /= 10; 
	    } 
	    return sum; 

	}
	public static String swapTheStrings(String inptStr) {
		//first swap the alternative pair of characters(alphabets)
		String[] strArr= new String[inptStr.length()];
		strArr = inptStr.trim().split("");
		//check for even or odd
		int length = strArr.length;

		for(int i=0; i<length;i++) {
			if(i<length) {
				String temp;
				temp = strArr[i];					
				strArr[i]= strArr[i+1];
				strArr[i+1] = temp;
				i=i+3;
			}
		}
		for(int i=0; i<length;i++) {
			if(strArr[i].equalsIgnoreCase("a")||strArr[i].equalsIgnoreCase("e")||strArr[i].equalsIgnoreCase("i")||strArr[i].equalsIgnoreCase("o")||strArr[i].equalsIgnoreCase("u")) {
				int asciiValue=0;
				char c = strArr[i].trim().charAt(0);
				asciiValue =  c;
				if(asciiValue>9) {
					strArr[i] = String.valueOf(convertSingleDigit(asciiValue));		
					}
				else {
					strArr[i] = String.valueOf(asciiValue);
				}
			}
		}
		String result = "";
		for(String eachValue : strArr) {
			result+=eachValue;
		}

		return result;
	}

}
/*
Notes - No special characters in the string 
Swap every alternate pair of every consecutive characters
assignment1 - 7ss6gnm2nt1-
s7s6ngm2tn1
alternate2 -7lt2rn7t22-l7t2nr7t22



 *
 */
