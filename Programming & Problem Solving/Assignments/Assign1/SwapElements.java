package assignments.Assign1;

import java.util.Arrays;
import java.util.Scanner;

public class SwapElements {
	public static void main(String[] args) {
	
		Scanner sc =  new Scanner(System.in);
		int n = sc.nextInt(); //n>=1
		//Read the number of values in to the array
		int[] inputArr =  new int[n];
		//Loop through to get the values
		for(int i=0;i<inputArr.length;i++) {
			inputArr[i] = sc.nextInt();
		}
		int[] resultArr = new int[inputArr.length];
		resultArr = swapIntValue(inputArr);
		/*
		 * for(int eachValue : resultArr) { System.out.print(eachValue+"\t"); }
		 */
		System.out.println(Arrays.toString(resultArr));
		sc.close();

	}
	public static int[] swapIntValue(int[] intArr) {
		int length = intArr.length;

		if(length<=1) {
			//return intArr
			return intArr;
		}
		else{
			int midIndex = (length/2);
			if(length%2!=0) {
				//odd
				int midValue= intArr[0]+intArr[length-1];
				if(midValue>9) {
					int sum = 0;					
					while(sum>9 || midValue>0) {
						//divide the values by 10 check digit count
						sum+=midValue%10;
						midValue/=10;
					} 
					intArr[midIndex] = sum;

				}else {
					intArr[midIndex] = midValue;
				}
			}
			for(int i=0;i<midIndex;i++) {
				//swapping values in the index
				int temp=0;
				temp = intArr[i];
				intArr[i]= intArr[length-1-i];
				intArr[length-1-i] = temp;
			}

		}
		return intArr;

	}

}
/*

 [1, 3, 5, 7, 2, 4, 6] =  [6, 4, 2, 7, 5, 3, 1]

 input 
 1 3  5  7  2  4  6

 Space Complexity - O(n)
 Time Complexity  - O(n)

 */