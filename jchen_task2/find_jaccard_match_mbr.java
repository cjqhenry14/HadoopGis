import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class find_jaccard_match_mbr {
	/*need to be defined*/
	public static float jac_diff_bound = 0.2f;
	public static int cluster_size_wr_bound = 100;
	public static int tolerant_num = 5;
	/*exceed_num < tolerant_num + cur_num/badjac_percent_div*/
	public static int badjac_percent_div = 5;
	/*in badjac_distance, can tolerate noise*/
	public static int badjac_distance = 400;
	
	public static int x_min = Integer.MAX_VALUE, y_min = Integer.MAX_VALUE;
	public static int x_max = Integer.MIN_VALUE, y_max = Integer.MIN_VALUE;
	public static int cur_num = 1;
	public static float sum_jac = 0;
	public static FileWriter writer;
	public static BufferedWriter bw;
	
	/*sorted_hilbert_res*/
	public static void main(String[] args) {
		String line = null, str_cood;
		boolean isFirst = true;
		float cur_jac;
		int tmp_x, tmp_y;
		int exceed_num = 0;
		int pre_good = 1;
		int line_id = 0;
		try {
			File path = new File("sorted_hilbert_res/");
			File[] file_list = path.listFiles();
			writer = new FileWriter("jaccard_match_mbrs.txt");
            bw = new BufferedWriter(writer);
            
			for (int i = 0; i < file_list.length; i++) {
				
				FileReader reader = new FileReader(file_list[i]);
				BufferedReader br = new BufferedReader(reader);
			
				while ((line = br.readLine()) != null) {
					line_id += 1;
					
					String[] values = line.split("#");
					if(values.length < 3)
						continue;
					 /*System.out.println(values[0]);*/
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
					/*first read*/
					if(isFirst)
					{
						x_min = x_cur_min;
						y_min = y_cur_min;
						x_max = x_cur_max;
						y_max = y_cur_max;
						isFirst = false;
						sum_jac = cur_jac;
					}
					/*not first*/
					else
					{
						/*exceed jaccard bound*/
						if(Math.abs(cur_jac - sum_jac/cur_num) > jac_diff_bound)
						{
							/*if(exceed_num < tolerant_num)*/
							/*if(exceed_num < Math.max(tolerant_num, cur_num/badjac_percent_div))*/
							if(exceed_num < tolerant_num + cur_num/badjac_percent_div)
							{
								if(line_id - pre_good > badjac_distance)
									exceed_num++;
								sum_jac += cur_jac;
								cur_num += 1;
								x_min = Math.min(x_cur_min, x_min);
								y_min = Math.min(y_cur_min, y_min);
								x_max = Math.max(x_cur_max, x_max);
								y_max = Math.max(y_cur_max, y_max);
							}
							else
							{
								/*record pre MBR*/
								record_pre_mbr();
								/*update cur info*/
								/*cur_jac_min = cur_jac;
								cur_jac_max = cur_jac;*/
								x_min = x_cur_min;
								y_min = y_cur_min;
								x_max = x_cur_max;
								y_max = y_cur_max;
								cur_num = 1;
								sum_jac = cur_jac;
								exceed_num = 0;
								pre_good = line_id;
							}
							
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
					
					/*end while*/
				}
				
				br.close();
				reader.close();

			/*end read*/
			}
			/*record last mbr*/
			if(sum_jac > 0)
				record_pre_mbr();
			
			bw.close();
            writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static void record_pre_mbr()
	{
		if(cur_num < cluster_size_wr_bound)
			return;
		float avg_jac = sum_jac/(cur_num);
		/*System.out.println(avg_jac + "#" + "MBR("
				+ x_min + " " + y_min + ","
				+ x_min + " " + y_max + ","
				+ x_max + " " + y_max + ","
				+ x_max + " " + y_min + ","
				+ x_min + " " + y_min + ")"
				+ "#" + cur_num);*/
		String res = avg_jac + "#" + "MBR("
				+ x_min + " " + y_min + ","
				+ x_min + " " + y_max + ","
				+ x_max + " " + y_max + ","
				+ x_max + " " + y_min + ","
				+ x_min + " " + y_min + ")"
				+ "#" + cur_num + "\n";
		
		try {
			bw.write(res);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
