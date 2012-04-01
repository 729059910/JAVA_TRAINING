package ex01_06;

public class Fibonacci {

	/**
	 * Define title
	 */
	static final String title = "Fibonacci!";

	/**
	 * @param args
	 *
	 * 値が50未満のフィボナッチ数列を表示する
	 */
	public static void main(String[] args) {
		// Define Title
		// Show Title
		System.out.println(title);

		int lo = 1;
		int hi = 1;
		System.out.println(lo);
		while (hi < 50)
		{
			System.out.println(hi);
			hi = lo + hi;	// 新しいhi
			lo = hi - lo;	// 新しいloは、（合計 - 古いlo）
							// すなわち、古いhi
		}

	}

}
