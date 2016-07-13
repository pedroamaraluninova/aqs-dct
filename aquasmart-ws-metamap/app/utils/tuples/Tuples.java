package utils.tuples;

public class Tuples {
	
	
	
	
	
	public static <A> Tuple tuple( A one)
	{
		return new Tuple1<A>(one);
	}
	
	public static <A,B> Tuple tuple( A one, B two)
	{
		return new Tuple2<A,B>(one,two);
	}
	
	public static <A,B,C> Tuple tuple( A one, B two, C three)
	{
		return new Tuple3<A,B,C>(one,two,three);
	}
	
	


}
