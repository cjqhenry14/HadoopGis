public class get_hilbert_curve_val {
	public int[][] grid;
	public int rank;
	public int size;

	public get_hilbert_curve_val(int n)
	{
		rank = n;
		size = (int) Math.pow(2, n);
		grid = new int[size][size];
		hilbertCurve();
	}

	public void hilbertCurve()
	{
		for (int i = 1; i <= rank; i++) {
			if (i == 1) {
				grid[0][0] = 0;
				grid[0][1] = 3;
				grid[1][0] = 1;
				grid[1][1] = 2;
			} else {
				int tmp_size = (int) Math.pow(2, (i - 1));
				int four_size = (int) Math.pow(4, (i - 1));
				int i_size = (int) Math.pow(2, i);
				for (int j = 0; j < tmp_size; j++)
					for (int k = 0; k < tmp_size; k++) {
						grid[j + tmp_size][k] = grid[j][k] + four_size;
						grid[j + tmp_size][k + tmp_size] = grid[j][k] + 2 * four_size;
						grid[tmp_size - 1 - k][i_size - 1 - j] = (grid[j][k] + 3 * four_size);
					}
				for (int j = 0; j < tmp_size; j++)
					for (int k = 0; k <= j; k++) {
						int temp = grid[j][k];
						grid[j][k] = grid[k][j];
						grid[k][j] = temp;
					}
			}
		}
	}

	public int hilbertCurve(int i, int j)
	{
		hilbertCurve();
		int value = grid[i][j];
		return value;
	}

	public int[] hilbertDecoding(int code) 
	{
		int[] decode = new int[2];
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++) {
				if (grid[i][j] != code)
					continue;
				else {
					decode[0] = i;
					decode[1] = j;
					break;
				}
			}
		return decode;
	}

}
