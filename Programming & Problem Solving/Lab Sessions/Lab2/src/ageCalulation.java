import java.util.Scanner;
import java.util.*;
import java.io.*;
public class ageCalulation {
	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		int n = sc.nextInt();
		sc.nextLine();
		//2DARRAY
		int dobList[][]= new int[n][3];
		int todayDate[] = new int[3];
		
		if(n>=1 && n<=25) {
			for(int i=0;i<n;i++) {
				for(int j=0;j<3;j++) {
					dobList[i][j]=sc.nextInt();
				}				
			}
		}
	
		
		System.out.println();
		for(int i=0; i<n; i++) {
			for(int j=0; j<3;j++) {
				System.out.print(dobList[i][j] + "\t");
			}
			System.out.println();
		}
		
	}
}

/*
3
25 10 2007

*/

