package assignments.Assign2.part1;

import java.util.Arrays;
import java.util.Scanner;

public class mirrorSum {
	private int number1;
	private int number2;
	public mirrorSum(int n1,int n2) {
		n1 = number1;
		n2= number2;
	}
	//Time Complexity : O(n2)
	public static void  getMirrorSum(int[] arr){

		for(int i=0;i<arr.length;i++) {
			for(int j=i+1;j<arr.length;j++) {
				if(arr[i]+arr[j]==0) {
					System.out.print("["+arr[i]+","+arr[j]+"] ");
				}
			}
		}
	}
	//Time complexity : O(nlogn) or O(logn)
	public static void getMirroSumLogn(int[] arr) {		
		for(int i : arr) {
			if(i<0) {
				
			}
		}
		
	}
	//Time Complexity : O(n)
	public static void getMirrorSumN(int[] arr) {
		
	}
	
	public static void main(String[] args) {
		int n ;
		Scanner sc= new Scanner(System.in);
		n = sc.nextInt();
		int[] arr= new int[n];

		for(int i=0;i<n;i++) {
			arr[i] = sc.nextInt();
		}
		getMirrorSum(arr);
		sc.close();
	}
}




/*
 ip:
 1, 3, -5, 9, -2,-4, 5, 7, 4, 6
10
1 3 -5 9 -2 -4 5 7 4 6
 op:
 [-5,5] and [-4,4]

 Time complexity :
 O(n2)  - Do not sort- compare each element with other find sum to zero  then get i and j value
 https://www.geeksforgeeks.org/print-all-the-pairs-that-contains-the-positive-and-negative-values-of-an-element/
 O(nlogn)
 O(n)

 */
