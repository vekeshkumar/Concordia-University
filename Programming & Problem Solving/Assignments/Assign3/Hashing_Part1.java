package assignments.Assign3;
import java.util.Scanner;

public class Hashing_Part1 {
	static int[] hashTable = new int[7];
	public static void main(String[] args) {
		//Create a hash table with size 7	- 19 26 13 48 17
		//h(k) = k%7				
		int[] values = new int[5];

		Scanner sc = new Scanner(System.in);
		for(int i=0;i<5;i++) {
			values[i] = sc.nextInt();
		}
		//separate chaining
		System.out.println("HashTable for Separate Chaining");
		//Linear Probing
		System.out.println("HashTable Linear Probing");
		linearProbing(values);

		sc.close();
	}
	private static int hashKey(int key) {
		System.out.println(key%7);
		return (key%7);
	}
	private static int hashKey2(int key) {
		System.out.println("collision");
		System.out.println("hashkey"+(5-key%5));
		return (5-key%5);
	}
	private static boolean isOccupied(int index) {
		return (hashTable[index]!=0);
	}
	public static void linearProbing(int[] values) {
		//Linear Probing
		for(int i=0;i<values.length;i++) {
			int hashKey = hashKey(values[i]);
			hashKey2(hashKey);
			if(isOccupied(hashKey)) {
				//hashKey2(hashKey);
				int stopIndex = hashKey;
				if(stopIndex==hashTable.length-1) {
					hashKey =0;
				}else {
					hashKey++;
				}
				while(isOccupied(hashKey) && hashKey!=stopIndex) {
					hashKey =(hashKey+1)%hashTable.length;
				}
			}

			if(hashTable[hashKey]!=0) {
				System.out.println("The index  or the position of the hash table has been used by another number "+hashTable[hashKey]);
			}else {
				hashTable[hashKey]= values[i];
			}

		}
		for(int i=0;i<hashTable.length;i++) {
			System.out.println("Index :"+i+"\t"+"value :"+hashTable[i]);
		}

	}
}

/*
 Separate Chaining -
 Linear probing
 Double Hashing- h'(k) = 5-(k%5)*/
//COnverting the key in to hashcode using Object.hashCode() - we can also overwrite the function
//Loadfactor =#items/capacity - size/capacity - decides when to resize
//Open addressing or linear probing

