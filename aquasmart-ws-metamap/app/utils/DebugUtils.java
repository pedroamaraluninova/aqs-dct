package utils;

public class DebugUtils 
{

	public static void out(String... out)
	{
		if(out.length == 1)
		{
			System.out.println(out[0]);
			return;
		}	
		
		for(String s : out)
			System.out.print(s + " ");
		System.out.println();
	}
	
}
