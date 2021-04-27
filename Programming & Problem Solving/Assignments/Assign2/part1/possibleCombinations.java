package assignments.Assign2.part1;

import java.util.Scanner;

public class possibleCombinations {
	public static void main(String[] args) {
		int n;
		Scanner sc= new Scanner(System.in);
		n= sc.nextInt();
		int[] arr = new int[n];
		for(int i =0;i<n;i++) {
			arr[i] = sc.nextInt();
		}
		sc.nextLine();
		int r = sc.nextInt();
		getCombinationIterative(arr,r);
		//getCombinationRecursive(arr,r);
		
		sc.close();

	}

	private static void getCombinationRecursive(int[] arr, int r) {
		// TODO Auto-generated method stub

	}

	private static void getCombinationIterative(int[] arr, int r) {
		System.out.println(arr.length);
		if(r==2) {
			for(int i=0;i<arr.length;i++) {				
				for(int j=i+1;j<arr.length;j++) {
					System.out.print(arr[i]+","+arr[j]+"\t");
				}
				System.out.println();
			}
		}
		



	}
}

/*
 {1,2,3,4,5}
 r is numbers in the combination
 iterative and recursive solution
 Time complexity
https://www.geeksforgeeks.org/print-all-possible-combinations-of-r-elements-in-a-given-array-of-size-n/
https://ideone.com/ywsqBz
https://www.geeksforgeeks.org/subset-sum-problem-dp-25/
https://www.geeksforgeeks.org/make-combinations-size-k/
https://medium.com/enjoy-algorithm/find-all-possible-combinations-of-k-numbers-from-1-to-n-88f8e3fad33c
 (1,2), (1,3), (1,4), (1, 5), (2,3), (2,4), (2,5), (3,4), (3,5) and (4,5) for r = 2.
5
1 2 3 4 5
2 
 */