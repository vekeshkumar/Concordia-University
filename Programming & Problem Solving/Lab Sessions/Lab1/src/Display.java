import java.util.Scanner;

public class Display {
	public static void reverseColumn(int inputArr[][]) {
		for (int row=0; row < inputArr.length; row++) {
			for (int col=0; col < inputArr[row].length; col++) {				
				if(row<col) {
					System.out.print ("* ");
				
				}else {
					System.out.print (inputArr[row][col] + " ");
				}
					/*if(col<inputArr.length-1 && row<inputArr.length-1) {
						//inputArr[row][col+1] = Integer.parseInt(astreik);
						
						System.out.print ("* ");
						if((col+1)<inputArr.length-1) {
							//inputArr[row][col+2] =Integer.parseInt(astreik);
							System.out.print ("* ");
						}
					}
				}else {
					System.out.print (inputArr[row][col] + " ");
				}*/
			}
			System.out.println();
		}
					
		/*for (int row=0; row < inputArr.length; row++) {
			for (int col=0; col < inputArr[row].length; col++) {
				System.out.print (inputArr[row][col] + " ");

			}
			System.out.println();
		}*/
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
