
import java.util.Arrays;
import java.util.Scanner;

public class BasketBall {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);		
		int numberOfPlayers = sc.nextInt();
		int rank = sc.nextInt();
		sc.nextLine();		
		String[][] indata = new String[numberOfPlayers][5];

		String[] names = new String[numberOfPlayers];
		int[][] values = new int[numberOfPlayers][4];

		int i,j;

		for(i=0; i<numberOfPlayers; i++) {
			for(j=0; j<5;j++) {
				indata[i][j] = sc.nextLine();
			}
		}
		sc.close();


		for(i=0; i<numberOfPlayers; i++) {
			names[i] = indata[i][0];
		}



		int k =0, l;


		for(i=0; i<numberOfPlayers; i++) {
			l =0;
			for(j=1; j<5;j++) {
				values[k][l] = Integer.parseInt(indata[i][j]);
				l++;
			}
			k++;
		}



		double[] score = new double[numberOfPlayers];


		for(i=0; i<numberOfPlayers; i++) {
			score[i] = (values[i][0]+(2*values[i][1])+(3*values[i][2])) * 1000;
			score[i] = score[i]/(values[i][3]);
			score[i] = Math.round(score[i]);
		}



		int[] scoref = new int[numberOfPlayers];

		for(i=0;i<numberOfPlayers;i++) {
			scoref[i] = (int)score[i];
		}
		for(i =0; i<names.length;i++) {
			names[i]= names[i]+ scoref[i];
		}

		Arrays.sort(scoref);





		String str= Integer.toString(scoref[scoref.length-rank]);

		for(i =0; i<names.length;i++) {
			if (names[i].contains(str)){
				int ind= names[i].indexOf(str);
				System.out.print(names[i].substring(0, ind) +" " +scoref[scoref.length-rank]);
				break;
			}
		}
	}
}