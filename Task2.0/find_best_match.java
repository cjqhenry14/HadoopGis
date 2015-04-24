import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.PriorityQueue;


public class find_best_match {
	
	public static int poly_sum = 20;//need to be defined
	public static int consec_num_bound = 3;// >=3 need to be defined
	public static float jac_diff_bound = 0.2f;//need to be defined
	public static int heap_size_bound = 6;//need to be defined
	public static poly[] poly_list;//sort by hilbert value
	public static poly[] jac_list;//sort by jaccard
	
	public static class poly {
		//int hc_val;no need, use hc_orderid instead
		int hc_orderid;//do need it! to find minHeap.top's corresponding pos in poly_list
		float jaccard;
		String str_cood;//17028 4047,17028 4062,17038 4062,17038 4047,17028 4047
	}
	
	
	public static void main(String[] args) {
		PriorityQueue<poly> minHeap = new PriorityQueue<poly>(heap_size_bound,jacComparator);
		
		poly_list = new poly[poly_sum];//sorted by hc_val
		jac_list = new poly[heap_size_bound];//size=heap_size; sort by jaccard

		int idx = 0;
		int hc_idx = 0;//hc_orderid
		int actual_polylist_size = 0;
		
		try {
			FileReader reader = new FileReader("test_res.txt");
			BufferedReader br = new BufferedReader(reader);

			String line = null;
			while ((line = br.readLine()) != null) {//read from the file(sorted by hc_val)
				String[] values = line.split("#");
				//make new ploy obj, put into poly_list
				poly pl = new poly();
				pl.hc_orderid = hc_idx++;// map relative sorted hc_val to consecutive hc_orderid
				pl.jaccard = Float.parseFloat(values[1]);
				pl.str_cood = values[2].substring(1, values[2].length() - 1);
				poly_list[idx++] = pl;
				
				// put into minHeap(top k by jaccard)
				if (pl.hc_orderid < heap_size_bound) {
					minHeap.add(pl);
				} else if (pl.jaccard > minHeap.peek().jaccard)/* meet capacity& > top*/
				{
					minHeap.poll();
					minHeap.add(pl);
				}
			}
			actual_polylist_size = idx;
			
			br.close();
			reader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//=================  get cluster start and end index ===============
		//from minHeap to jac_list, sort by jaccard(->)
		idx = 0;
		while (!minHeap.isEmpty()) {
			poly head = minHeap.poll();
			jac_list[idx++] = head;
		}
		
		//get cluster with max&similar jaccard
		int start = -1;//cluster start 
		int end = -1;//cluster end 
		for (int i = heap_size_bound - 1; i >= 0; i--)
		{
			poly cur_poly = jac_list[i];
			float cur_jac = cur_poly.jaccard;
			int cur_pos = cur_poly.hc_orderid;//current pos in poly_list
			int r, l;
			//System.out.println("c  " + cur_pos + " " + cur_jac);
			for (r = cur_pos+1; r < actual_polylist_size; r++)//go right in poly_list
			{
				//System.out.println("c  " + cur_pos + " " + cur_jac + " " + Math.abs(poly_list[r].jaccard - cur_jac));
				if(Math.abs(poly_list[r].jaccard - cur_jac) > jac_diff_bound)
					break;
			}
			for (l = cur_pos-1; l >= 0; l--)//go left
			{
				if(Math.abs(poly_list[l].jaccard - cur_jac) > jac_diff_bound) 
					break;
			}
			if(r - l - 1 >= consec_num_bound)//get cluster with max and similar jaccard
			{
				start = l + 1;
				end = r - 1;
				break;
			}
			//else continue, select next jaccard
		}
		System.out.println("in poly_list: start-> " + start + "  end-> " + end);//get start and end index
		//end of get start and end
		
		//=================  get cluster MBR ===============
		get_best_cluster_mbr(start, end);//[start, end]
	}
	
	public static void get_best_cluster_mbr(int start, int end)
	{
		if(start == -1 || end == -1)
		{
			System.out.println("no answer!");
			return;
		}
		int x_min = Integer.MAX_VALUE, y_min = Integer.MAX_VALUE, x_max = Integer.MIN_VALUE, y_max = Integer.MIN_VALUE;
		int tmp_x, tmp_y;
		float avg_jac = 0.0f, sum_jac = 0.0f;
		for (int i = start; i <= end; i++)
		{
			poly cur_poly = poly_list[i];
			sum_jac += cur_poly.jaccard;
			String[] coords = cur_poly.str_cood.split(",");
			//System.out.println(cur_poly.str_cood);
			for (int j = 0; j < coords.length; ++j)// get each coordinate's x,y
			{
				String[] xy = coords[j].split(" ");
				tmp_x = Integer.parseInt(xy[0]);
				tmp_y = Integer.parseInt(xy[1]);
				x_min = Math.min(x_min, tmp_x);
				y_min = Math.min(y_min, tmp_y);
				x_max = Math.max(x_max, tmp_x);
				y_max = Math.max(y_max, tmp_y);
			}
		}
		//print final result
		avg_jac = sum_jac/(end - start + 1);
		//<hilbert_value, jaccard, x_min y_min, x_min y_max, x_max y_max, x_max y_min, x_min y_min>
		String x_min_str = Integer.toString(x_min);
		String x_max_str = Integer.toString(x_max);
		String y_min_str = Integer.toString(y_min);
		String y_max_str = Integer.toString(y_max);
		System.out.println(avg_jac + "\t" + "MBR("
				+ x_min_str + " " + y_min_str + ","
				+ x_min_str + " " + y_max_str + ","
				+ x_max_str + " " + y_max_str + ","
				+ x_max_str + " " + y_min_str + ","
				+ x_min_str + " " + y_min_str + ")");
	}
	
	public static Comparator<poly> jacComparator = new Comparator<poly>() {//use in minHeap
		@Override
		public int compare(poly c1, poly c2) {
			return (int) Float.compare(c1.jaccard, c2.jaccard);
		}
	};
}
