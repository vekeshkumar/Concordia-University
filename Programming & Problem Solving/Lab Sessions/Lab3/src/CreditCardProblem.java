import java.util.Scanner;

public class CreditCardProblem {
	public static void main(String[] args) {
		Scanner sc  = new Scanner(System.in);
		String input = sc.nextLine();
		System.out.println(checkDigit(input));		
		sc.close();
	}
	public static String checkDigit(String inpValue) {

		String result ="INVALID";
		String[] inputArr = inpValue.split("");
		int sum=0;
		int counter = 1;
		for(int i=inputArr.length-1;i>=0;i--) {
			int tempValue=0;
			if(counter%2==0) {
				tempValue= Integer.parseInt(inputArr[i])*2;
				if(tempValue>9) {
					//convert it to string  array and then add them into new temp value
					String splitInt[]= String.valueOf(tempValue).split("");
					tempValue = Integer.parseInt(splitInt[0])+Integer.parseInt(splitInt[1]);
					sum+=tempValue;
				}else{
					sum+=tempValue; 
				}
			}else{
				sum+=Integer.parseInt(inputArr[i]);
			}
			counter++;
		}
		if(sum%10==0) {
			result ="VALID";
		}else {
			int n = sum%10;
			int lastDigit = Integer.parseInt(inputArr[inputArr.length-1]);
			int checkDigit = (lastDigit>=n)? lastDigit-n: (10-n)+lastDigit ;
			result = result+" "+checkDigit;
		}

		return result;
	}
	
}
/*
 49927398716
513467882134
432876126

4 + 6 +



 *
 *
 *
 */
