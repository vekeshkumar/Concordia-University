package assignments.Assign1;
import java.util.Collections;
import java.util.Scanner;
import java.util.TreeMap;

public class smallestSquaresReversed {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int n = sc.nextInt();
		int intArr[] = new int[n];
		for(int i=0;i<n;i++) {
			intArr[i]= sc.nextInt();
		}		
		findSmallLargeDiff(intArr);
		sc.close();
	}
	public static int reverseNumber(int n) {
		int reversedNumber =0;
		while(n!=0) {
			reversedNumber = reversedNumber*10+  n%10;
			n/=10;
		}
		return reversedNumber;
	}
	public static void findSmallLargeDiff(int[] inputArr) {
		//Could use hashmap
		TreeMap<Integer, Integer> smallDifRev = new TreeMap<Integer,Integer>();
		TreeMap<Integer, Integer> hightDifRev = new TreeMap<Integer,Integer>();
		//smallestSquared Reversed Difference
		int temp1, temp2,temp3,temp4=0;
		for(int i=0;i<inputArr.length;i++) {
			if(i+1<inputArr.length) {
					temp1= reverseNumber((int)Math.pow(inputArr[i], 2));
					temp2=reverseNumber((int)Math.pow(inputArr[i+1], 2));
					temp3= reverseNumber((int)Math.pow(inputArr[i], 3));
					temp4=reverseNumber((int)Math.pow(inputArr[i+1], 3));
					
				//i can use insertion sort with hashmap
				smallDifRev.put(((temp1>temp2)?(temp1-temp2):(temp2-temp1)),i);	
				hightDifRev.put(((temp3>temp4)?(temp3-temp4):(temp3-temp4)),i);
			}
		}
		int fSmallIndx,sSmallIndx=0;
		fSmallIndx = smallDifRev.get(Collections.min(smallDifRev.descendingKeySet()));
		sSmallIndx =fSmallIndx+1;
		System.out.println("The two elements with the smallest difference of their squares reversed are:"+inputArr[fSmallIndx]+" at index "+fSmallIndx+" and "+inputArr[sSmallIndx]+" at index "+sSmallIndx);
		int fBigIndx,sBigIndx=0;
		fBigIndx = hightDifRev.get(Collections.max(hightDifRev.descendingKeySet()));
		sBigIndx =fBigIndx+1;
		System.out.println("The two elements with the highest difference of their cubes reversed are:"+inputArr[fBigIndx]+" at index "+fBigIndx+" and "+inputArr[sBigIndx]+" at index "+sBigIndx);
		System.out.println(hightDifRev.toString());
	}
}

/*
20, 23, 21, 3, 50, 77, 72, 11, 29, 13] 
20 23 21 3 50 77 72 11 29 13

20, 23,21, 3, 50, 77, 72, 11, 29, 13]

 */

