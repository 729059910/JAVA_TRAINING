package ex01_08;

public class Point {

	public double x, y;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Point a = new Point();
		Point b = new Point();

		a.set_by_mnl(2, 5);
		a.show();
		b.set_by_mnl(8, 7);
		a.set_by_obj(b);
		a.show();

	}

	public void show()
	{
		System.out.println("x = " + x + ", y = " + y);
	}

	public void set_by_mnl(int new_x, int new_y)
	{
		x = new_x;
		y = new_y;
	}

	public void set_by_obj(Point that)
	{
		x = that.x;
		y = that.y;
	}

}
