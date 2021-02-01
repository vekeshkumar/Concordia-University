package labs.Lab2;

import java.util.Scanner;
/**
 * 
 * @author Vekesh
 * Basic Problem - Age check 
 */
public class ageCheck {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int n;
		n = sc.nextInt();
		int darray[][] = new int[n][3];
		for(int i=0;i<n;i++) {
			for(int j=0;j<3;j++) {
				darray[i][j]=sc.nextInt();
			}
		}
		/*
		 * for(int i=0;i<n;i++) { for(int j=0;j<3;j++) {
		 * System.out.print(darray[i][j]+"\t"); } System.out.println(); }
		 */
		for(int i=0;i<n;i++) {
			for(int j=2;j>=0;j--) {
				int tempValue = darray[i][j];
				if(2021-tempValue>13 && j==2)
				{ System.out.println("old enough");
				break;}

				else if(2021-tempValue==13 && j==2) {
					if(darray[i][j-1]<2) {
						System.out.println("old enough");break;
					}else if(darray[i][j-1]==2) {
						if(darray[i][j-2]==1) {
							System.out.println("old enough"); break;
						}else {
							System.out.println("too young");break;
						}
					}
				}					
				else {
					System.out.println("too young");break;
				}
			}
		}

	}

}
/*
4
20 05 2007
31 01 2008
01 02 2008
12 12 2002

Check age as of 01-02-2021
as 13

output 
old enough
too young
 */

