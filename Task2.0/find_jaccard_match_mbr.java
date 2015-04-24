import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class find_jaccard_match_mbr {

	public static float jac_diff_bound = 0.2f;
	public static float cur_jac_min;
	public static float cur_jac_max;
	public static int x_min = Integer.MAX_VALUE, y_min = Integer.MAX_VALUE;
	public static int x_max = Integer.MIN_VALUE, y_max = Integer.MIN_VALUE;
	public static int cur_num = 1;
	public static float sum_jac = 0;

	public static void main(String[] args) {
		String line = null, str_cood;
		boolean isFirst = true;
		float cur_jac;
		int tmp_x, tmp_y;
		try {
			File path = new File("sorted_hilbert_res/");
			File[] file_list = path.listFiles();
			for (int i = 0; i < file_list.length; i++) {
				
				FileReader reader = new FileReader(file_list[i]);
				BufferedReader br = new BufferedReader(reader);
				
				while ((line = br.readLine()) != null) {
					
					String[] values = line.split("#");
					if(values.length < 3)
						continue;

					cur_jac = Float.parseFloat(values[1]);
					str_cood = values[2].substring(1, values[2].length() - 1);
					String[] coords = str_cood.split(",");
					int x_cur_min = Integer.MAX_VALUE, y_cur_min = Integer.MAX_VALUE;
					int x_cur_max = Integer.MIN_VALUE, y_cur_max = Integer.MIN_VALUE;
					for (int j = 0; j < coords.length; ++j)
					{
						String[] xy = coords[j].split(" ");
						tmp_x = Integer.parseInt(xy[0]);
						tmp_y = Integer.parseInt(xy[1]);
						x_cur_min = Math.min(x_cur_min, tmp_x);
						y_cur_min = Math.min(y_cur_min, tmp_y);
						x_cur_max = Math.max(x_cur_max, tmp_x);
						y_cur_max = Math.max(y_cur_max, tmp_y);
					}

					if(isFirst)
					{
						cur_jac_min = cur_jac;
						cur_jac_max = cur_jac;
						x_min = x_cur_min;
						y_min = y_cur_min;
						x_max = x_cur_max;
						y_max = y_cur_max;
						isFirst = false;
						sum_jac = cur_jac;
					}

					else
					{

						if(Math.abs(cur_jac - cur_jac_min) > jac_diff_bound
								|| Math.abs(cur_jac - cur_jac_max) > jac_diff_bound)
						{

							record_pre_mbr();

							cur_jac_min = cur_jac;
							cur_jac_max = cur_jac;
							x_min = x_cur_min;
							y_min = y_cur_min;
							x_max = x_cur_max;
							y_max = y_cur_max;
							cur_num = 1;
							sum_jac = cur_jac;
						}
						else
						{
							sum_jac += cur_jac;
							cur_num += 1;
							x_min = Math.min(x_cur_min, x_min);
							y_min = Math.min(y_cur_min, y_min);
							x_max = Math.max(x_cur_max, x_max);
							y_max = Math.max(y_cur_max, y_max);
						}
					}

				}
				
				br.close();
				reader.close();

			}

			if(sum_jac > 0)
				record_pre_mbr();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static void record_pre_mbr()
	{
		float avg_jac = sum_jac/(cur_num);
		System.out.println(avg_jac + "#" + "MBR("
				+ x_min + " " + y_min + ","
				+ x_min + " " + y_max + ","
				+ x_max + " " + y_max + ","
				+ x_max + " " + y_min + ","
				+ x_min + " " + y_min + ")"
				+ "#" + cur_num);
	}

}
