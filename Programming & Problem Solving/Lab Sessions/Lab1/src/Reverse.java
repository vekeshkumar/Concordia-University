import java.util.Scanner;

/**
 * 
 * @author v_dhayal
 *
 */
public class Reverse {

	public static void reverseColumn(int inputArr[][]) {
//		for (int row=inputArr.length; row >1; row--) {
//			for(int col=inputArr.length; col >1; col--){
//				/*if(row>1) {
//					System.out.println(inputArr[row][col]);
//					inputArr[row][col]= inputArr[row][col];
//				}*/
//				System.out.print(inputArr[row][col]+" ");
//				
//				
//			}
//			System.out.println();
//		}		
		for (int row=0; row < inputArr.length; row++) {			
			for (int col=0; col < inputArr[row].length; col++) {				
				if(row<inputArr.length-1) {
					inputArr[row][col]= inputArr[row+1][col];
				}else {
				
				}
			}
		}
		for(int row=0;row<inputArr.length;row++) {
			for(int col=0;col<inputArr.length;col++) {
				System.out.print(inputArr[row][col]+" "); //space trim
			}
			System.out.println();
		}
	}
	public static void main(String[] args) {		
		int N;
		Scanner sc= new Scanner(System.in);
		N=sc.nextInt();
		int darray[][] = new int[N][N];
		for(int row=0;row<N;row++) {
			for(int col=0;col<N;col++) {
				darray[row][col] = sc.nextInt();
			}
		}
		reverseColumn(darray);
		sc.close();
	}
}


/*Time complexity 
It will be traversing each row which is an array for about 3 times
O(3N2) is obviously to 
O(N2)

Space Complexity
For s
O(n2)*/
