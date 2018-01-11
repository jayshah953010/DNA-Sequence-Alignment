import java.lang.*;
import java.io.*;
import java.util.*;

public class Sequence_Alignment {
	public static void main(String[] args) throws IOException 
	{
		
		
		/*System.out.println("Type of Alignment is " + args[0]);
		System.out.println("Query file sequence is in  " + args[1] );
		System.out.println("Datafile file sequence is in  " + args[2] );
		System.out.println("Alphabet is in " + args[3] );
		System.out.println("Scoring Matrix is in   " + args[4] );
		System.out.println("Value of k is =  " + args[5] );
		System.out.println("Gap Penalty is  " + args[6] );*/
		
		//Taking the type of Alignment 
		int type_of_alignment = Integer.parseInt(args[0]);
		
		//Taking Query Id and Query Sequence in a HashMap and String
		HashMap<String,String> Query_Map = InputFile(args[1]);
		//System.out.println("Query File Done");
		//Taking Sequence Id and Sequence in a HashMap and String
		HashMap<String,String> Database_Map = InputFile(args[2]);
		//System.out.println("Database File Done");
		//Taking Alphabet file as an Input
		HashMap<Character,Integer> Alphabet = InputAlphabet(args[3]);
		
		//Making a Scoring Matrix
		int[][] Scoring_Matrix = InputMatrix(args[4],Alphabet.size());
 	   
 	   
		//Taking number of nearest neighbor from the argument
		int k = Integer.parseInt(args[5]);
		
		PriorityQueue<Alignment> pq = new PriorityQueue<Alignment>(k,new Comparator<Alignment>(){
			public int compare(Alignment a1,Alignment a2)
			{
				if(a1.score>a2.score) return 1;
				if(a1.score<a2.score) return -1;
				return 0;
			}
		});
		
		//Taking gap penalty from the argument
		
		int m = Integer.parseInt(args[6]);
		int count = 0;
		
		Set set1 = Query_Map.entrySet();
		Iterator i1 = set1.iterator();
		
		while(i1.hasNext())
		{
			long startTime = System.currentTimeMillis();
			Map.Entry<String,String> me1 = (Map.Entry<String,String>) i1.next();
			Set set2 = Database_Map.entrySet();
			Iterator i2 = set2.iterator();
			String s1_id = me1.getKey();
			String s1 = me1.getValue();
			
			while(i2.hasNext())
			{
				Map.Entry<String,String> me2 = (Map.Entry<String,String>) i2.next();
				String s2_id = me2.getKey();
				String s2 = me2.getValue();
				String s3 = new String(s2);
				
				int[][] Alignment_Matrix;
				switch(type_of_alignment)
				{
					case 1: 
						Alignment a1 = Global_Alignment(s1,s2,Scoring_Matrix,Alphabet,m,s1_id,s2_id);
						//System.out.println("S1_id = " + s1_id + "Score : " + a1.score);
						pq.add(a1);
						if(pq.size()>k) pq.poll();
						break;
					case 2:
						Alignment a2 = Local_Alignment(s1,s2,Scoring_Matrix,Alphabet,m,s1_id,s2_id);
						//System.out.println("S1_id = " + s1_id + "Score : " + a2.score);
						pq.add(a2);
						if(pq.size()>k) pq.poll();
						break;
					case 3:
						Alignment a3 = Dovetail_Alignment(s1,s2,Scoring_Matrix,Alphabet,m,s1_id,s2_id);
						//System.out.println("S1_id = " + s1_id + "Score : " + a3.score);
						pq.add(a3);
						if(pq.size()>k) pq.poll();
				}
					
				
			}
			/*long endTime = System.currentTimeMillis();
			long Totaltime = endTime - startTime;
			System.out.println(" s1_length " + s1.length() + " Totaltime : " + Totaltime);*/
			//long endTime = System.currentTimeMillis();
			
		}
		
		//long Totaltime = endTime - startTime;
		//System.out.println("Priority Queue Size is : " + pq.size());
		int size = pq.size();
		for(int i=0;i<size;i++)
		{
			//System.out.println("i = " + i + "pq size is " + pq.size());
			Alignment a = pq.poll();
			System.out.println("Score : " + a.score);
			System.out.println(a.id1 + " " + a.start1 + " " + a.s1);
			System.out.println(a.id2 + " " + a.start2 + " " + a.s2);
			System.out.println("\n");
		}
	}
	public static HashMap<String,String> InputFile(String arg) throws IOException
	{
		HashMap<String,String> hm = new HashMap();
		BufferedReader br = new BufferedReader(new FileReader(arg));
		StringBuilder s = new StringBuilder();
		String line = "";
		String[] temp,temp1;
		while( (line = br.readLine()) !=null )
		{
			if(line.startsWith(">hsa"))
			{
				if(s!=null)s.append(",");
				temp = line.split(" ");
				temp1 = temp[0].split(":");
				s.append(temp1[1] + ":");
			//	System.out.println(s);
			}
			else s.append(line);
		}
		//temp = s.toString().split(",");
		//s.toString();
		temp = s.toString().split(",");
		for(int i=1;i<temp.length;i++)
		{
			temp1 = temp[i].split(":");
			//System.out.print("yoo" + temp1[1]);
			hm.put(temp1[0],temp1[1]);
			//System.out.println(temp1[0]);
		}
		br.close();
		return hm;
	}
	
	public static HashMap<Character,Integer> InputAlphabet(String arg) throws IOException
	{
		HashMap<Character,Integer> hm = new HashMap();
		BufferedReader br = new BufferedReader(new FileReader(arg));
		String s = br.readLine();
		char[] c = s.toCharArray();
		for(int i=0;i<c.length;i++) hm.put(c[i],i);
		
		return hm;
	}
	public static int[][] InputMatrix(String arg,int n) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(arg));
		String line = "";
		int[][] ScoreMatrix = new int[n][n];
		int i=0;
		while((line = br.readLine())!=null)
		{
			StringTokenizer stk = new StringTokenizer(line);
			int j=0;
			while(stk.hasMoreTokens())
			{
				ScoreMatrix[i][j] = Integer.parseInt(stk.nextToken());
				j++;
			}
			i++;
		}
		return ScoreMatrix;
	}
	public static Alignment Global_Alignment(String s1, String s2,int[][] Scoring_Matrix,HashMap<Character,Integer> Alphabet,int gap_penalty,String id1,String id2)
	{
		
		int[][] Alignment_Matrix = new int[s1.length()+1][s2.length()+1];
		s1 = s1.toUpperCase();
		s2 = s2.toUpperCase();
		//Alignment_Matrix[0][0] = 0;
		int i=0,j=1;
		for(j=1;j<=s2.length();j++) Alignment_Matrix[i][j] = Alignment_Matrix[i][j-1] + gap_penalty;
		j=0;
		for(i=1;i<=s1.length();i++) Alignment_Matrix[i][j] = Alignment_Matrix[i-1][j] + gap_penalty;
		for(i=1;i<=s1.length();i++)
		{
			for(j=1;j<=s2.length();j++)
			{
				//System.out.println(s1.charAt(i-1) + "," + s2.charAt(j-1));
				int x = Alphabet.get(s1.charAt(i-1));
				int y = Alphabet.get(s2.charAt(j-1));
				int score = Scoring_Matrix[x][y];
				int temp = Math.max(Alignment_Matrix[i-1][j]+gap_penalty,Alignment_Matrix[i][j-1]+gap_penalty);
				Alignment_Matrix[i][j] = Math.max(temp,Alignment_Matrix[i-1][j-1]+score);
			}
		}
		int score = Alignment_Matrix[s1.length()][s2.length()];
		i = s1.length();
		j = s2.length();
		
		StringBuilder sbA = new StringBuilder();
		StringBuilder sbB = new StringBuilder();
		while(i>0 || j>0)
		{
			if(i>0 && j>0 && Alignment_Matrix[i][j] == Alignment_Matrix[i-1][j-1] + Scoring_Matrix[Alphabet.get(s1.charAt(i-1))][Alphabet.get(s2.charAt(j-1))])
			{
				sbA.insert(0,s1.charAt(i-1));
				sbB.insert(0,s2.charAt(j-1));
				i--;
				j--;
			}
			else if(i>0 && Alignment_Matrix[i][j] == Alignment_Matrix[i-1][j] + gap_penalty)
			{
				sbA.insert(0,s1.charAt(i-1));
				sbB.insert(0,'-');
				i--;
			}
			else
			{
				sbA.insert(0,'-');
				sbB.insert(0,s2.charAt(j-1));
				j--;
			}
				
			
		}
		
		Alignment a1 = new Alignment(score,0,id1,sbA.toString(),0,id2,sbB.toString()); 
		return a1;
	}
	
	public static Alignment Local_Alignment(String s1, String s2,int[][] Scoring_Matrix,HashMap<Character,Integer> Alphabet,int gap_penalty,String id1,String id2)
	{
		int[][] Alignment_Matrix = new int[s1.length()+1][s2.length()+1],
				Dir_Matrix = new int[s1.length()+1][s2.length()+1];
		s1 = s1.toUpperCase();
		s2 = s2.toUpperCase();
		int maxValue = Integer.MIN_VALUE, maxi = 0 , maxj = 0;
		
		for (int i = 1; i <=s1.length(); i++)
		{
			for (int j = 1; j <=s2.length(); j++)
			{
				Dir_Matrix[i][j] = Alignment_Matrix[i][j] = Math.max(Alignment_Matrix[i][j - 1] + gap_penalty, Alignment_Matrix[i - 1][j] + gap_penalty);
				Alignment_Matrix[i][j] = Math.max(Alignment_Matrix[i][j], Alignment_Matrix[i - 1][j - 1]
						+ Scoring_Matrix[Alphabet.get(s1.charAt(i - 1))][Alphabet.get(s2.charAt(j - 1))]);
				Alignment_Matrix[i][j] = Math.max(Alignment_Matrix[i][j], 0);

				if (Alignment_Matrix[i][j] >= maxValue)
				{
					maxValue = Alignment_Matrix[i][j];
					maxi = i;
					maxj = j;
				}

			}
		}
		StringBuilder sbA = new StringBuilder("");
		StringBuilder sbB = new StringBuilder("");
		int i = maxi;
		int j = maxj;
		while ((i > 0 || j > 0) && (Alignment_Matrix[i][j] != 0))
		{
			if (i > 0 && j > 0 && Alignment_Matrix[i][j] == (Alignment_Matrix[i - 1][j - 1]
					+ Scoring_Matrix[Alphabet.get(s1.charAt(i - 1))][Alphabet.get(s2.charAt(j - 1))]))
			{
				String st1 = sbA.toString();
				sbA = new StringBuilder();
				sbA.append(s1.charAt(i - 1));
				sbA.append(st1);
				String st2 = sbB.toString();
				sbB = new StringBuilder();
				sbB.append(s2.charAt(j - 1));
				sbB.append(st2);
				i--;
				j--;
			}
			else if (i > 0 && Alignment_Matrix[i][j] == Alignment_Matrix[i - 1][j] + gap_penalty) 
			{
				String st1 = sbA.toString();
				sbA = new StringBuilder();
				sbA.append(s1.charAt(i - 1));
				sbA.append(st1);
				String st2 = sbB.toString();
				sbB = new StringBuilder();
				sbB.append('-');
				sbB.append(st2);
				i--;
			} 
			else if (j > 0 && Alignment_Matrix[i][j] == Alignment_Matrix[i][j - 1] + gap_penalty) {
				String st1 = sbA.toString();
				sbA = new StringBuilder();
				sbA.append('-');
				sbA.append(st1);
				String st2 = sbB.toString();
				sbB = new StringBuilder();
				sbB.append(s2.charAt(j - 1));
				sbB.append(st2);
				j--;
			}
			else
			{
				break;
			}
			
		}
		Alignment a1 = new Alignment(maxValue,i+1,id1,sbA.toString(),j+1,id2,sbB.toString());
		return a1;
	}
	
	
	public static Alignment Dovetail_Alignment(String s1, String s2,int[][] Scoring_Matrix,HashMap<Character,Integer> Alphabet,int gap_penalty,String id1,String id2)
	{
		int[][] Alignment_Matrix = new int[s1.length() + 1][s2.length() + 1];
		s1 = s1.toUpperCase();
		s2 = s2.toUpperCase();
		int maxcv = Integer.MIN_VALUE, maxrv = Integer.MIN_VALUE , ci = 0 , cj = 0 , ri = 0 , rj = 0 ;
		for(int i=1;i<=s1.length();i++)
		{
			for(int j=1;j<=s2.length();j++)
			{
				Alignment_Matrix[i][j] = Math.max(Alignment_Matrix[i][j - 1] + gap_penalty, Alignment_Matrix[i - 1][j] + gap_penalty);
				Alignment_Matrix[i][j] = Math.max(Alignment_Matrix[i][j], Alignment_Matrix[i - 1][j - 1]
						+ Scoring_Matrix[Alphabet.get(s1.charAt(i - 1))][Alphabet.get(s2.charAt(j - 1))]);
				
				
				if (j == s2.length() && Alignment_Matrix[i][j] >= maxcv)
				{
					maxcv = Alignment_Matrix[i][j];
					ci = i;
					cj = j;
				}
				
				if(i == s1.length() && Alignment_Matrix[i][j] >= maxrv)
				{
					maxrv = Alignment_Matrix[i][j];
					rj = j;
					ri = i;
				}
			}
		}
		StringBuilder sbA = new StringBuilder("");
		StringBuilder sbB = new StringBuilder("");
		int maxvalue = (maxcv >= maxrv) ? maxcv : maxrv;
		int i = (maxcv >= maxrv) ? ci : ri;
		int j = (maxcv >= maxrv) ? cj : rj;
		while (i > 0 && j > 0) {
			if (i > 0 && j > 0 && Alignment_Matrix[i][j] == (Alignment_Matrix[i - 1][j - 1]
					+ Scoring_Matrix[Alphabet.get(s1.charAt(i - 1))][Alphabet.get(s2.charAt(j - 1))]))
			{
				String st1 = sbA.toString();
				sbA = new StringBuilder();
				sbA.append(s1.charAt(i - 1));
				sbA.append(st1);
				String st2 = sbB.toString();
				sbB = new StringBuilder();
				sbB.append(s2.charAt(j - 1));
				sbB.append(st2);
				i--;
				j--;
			}
			else if (i > 0 && Alignment_Matrix[i][j] == Alignment_Matrix[i - 1][j] + gap_penalty)
			{
				String st1 = sbA.toString();
				sbA = new StringBuilder();
				sbA.append(s1.charAt(i - 1));
				sbA.append(st1);
				String st2 = sbB.toString();
				sbB = new StringBuilder();
				sbB.append('-');
				sbB.append(st2);
				i--;
			}
			else
			{
				String st1 = sbA.toString();
				sbA = new StringBuilder();
				sbA.append('-');
				sbA.append(st1);
				String st2 = sbB.toString();
				sbB = new StringBuilder();
				sbB.append(s2.charAt(j - 1));
				sbB.append(st2);
				j--;
			} 
		}
		Alignment a1 = new Alignment(maxvalue,i,id1,sbA.toString(),j,id2, sbB.toString());
		return a1;
	}
	
}


