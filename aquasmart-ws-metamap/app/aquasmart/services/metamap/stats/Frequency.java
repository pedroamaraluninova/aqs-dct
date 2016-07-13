package aquasmart.services.metamap.stats;

import java.io.Serializable;

public class Frequency<T extends Serializable> implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public T value;
	public int frequency;
	public Frequency(T value, int freq)
	{ this.value = value; this.frequency = freq; }
}