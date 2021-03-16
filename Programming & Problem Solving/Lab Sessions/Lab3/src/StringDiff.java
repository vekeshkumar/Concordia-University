import java.util.Scanner;

public class StringDiff {
	public static void main(String[] args) {
		Scanner sc  = new Scanner(System.in);

		int N= sc.nextInt();
		sc.nextLine();
		int result[] = new int[N];
		for(int i=0;i<N;i++) {
			String[] nArr= new String[2];
			for(int j=0;j<nArr.length;j++) {
				nArr[j] = sc.nextLine();
			}
			char c1[] = nArr[0].toCharArray();
			char c2[] = nArr[1].toCharArray();
			
			result[i] =checkDiff(c1, c2);
		}
		for(int eachValue: result) {
			System.out.println(eachValue);
		}
		sc.close();
	}
	public static int checkDiff(char[] c1,char c2[]) {
		boolean iscontinuous = true;
		int counter = 0;
		int length = (c1.length<c2.length)? c1.length: c2.length;
		for(int j=0;j<length;j++) {
			if(c1[j]==c2[j] && iscontinuous) {
				//System.out.println("coming inside");
				counter++;
				iscontinuous = true;
			}else {
				iscontinuous = false;
				break;
			}
		}
		return counter;
	}
}
/*
5
tony
tony
dan
tony
one two three
one two four
a
b
abcd
abcde

2
tony
tony
dan
tony
1
one two three
one two four
a
b
abcd
abcde
 */
