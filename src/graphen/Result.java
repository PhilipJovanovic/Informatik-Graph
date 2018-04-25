package graphen;

import java.util.Arrays;

public class Result {
	private String message;
	private int[] pfad;
	
	public Result(String message, int[] pfad) {
		this.message = message;
		this.pfad = pfad;
	}

	public String getMessage() {
		return message;
	}

	public int[] getPfad() {
		return pfad;
	}
	public String toString() {
		return message + "\n" + Arrays.toString(pfad);
	}
}
