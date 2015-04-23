import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class sort_by_hilbert {
	public static class Map extends
			Mapper<Object, Text, IntWritable, Text> {
		int div = 10;
		
		int rank = 13;
		String jaccard, polygon1, polygon2;

		get_hilbert_curve_val hc = new get_hilbert_curve_val(rank);
		
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			String[] values = line.split("\t");
			if(values.length < 2)
				return;
			jaccard = values[values.length - 2];
			polygon1 = values[2].substring(10, values[2].length() - 2);
			polygon2 = values[5].substring(10, values[5].length() - 2);
			String[] coords1 = polygon1.split(",");
			String[] coords2 = polygon2.split(",");
			int x_min = Integer.MAX_VALUE, y_min = Integer.MAX_VALUE, x_max = Integer.MIN_VALUE, y_max = Integer.MIN_VALUE;
			int tmp_x, tmp_y;
			int x_sum = 0, y_sum = 0, x_mean = 0, y_mean = 0;
			for (int i = 0; i < coords1.length; ++i)
			{
				String[] xy = coords1[i].split(" ");
				tmp_x = (int)Float.parseFloat(xy[0]);
				tmp_y = (int)Float.parseFloat(xy[1]);
				x_sum += tmp_x;
				y_sum += tmp_y;
				x_min = Math.min(x_min, tmp_x);
				y_min = Math.min(y_min, tmp_y);
				x_max = Math.max(x_max, tmp_x);
				y_max = Math.max(y_max, tmp_y);
			}
			for (int i = 0; i < coords2.length; ++i)
			{
				String[] xy = coords2[i].split(" ");
				tmp_x = (int)Float.parseFloat(xy[0]);
				tmp_y = (int)Float.parseFloat(xy[1]);
				x_sum += tmp_x;
				y_sum += tmp_y;
				x_min = Math.min(x_min, tmp_x);
				y_min = Math.min(y_min, tmp_y);
				x_max = Math.max(x_max, tmp_x);
				y_max = Math.max(y_max, tmp_y);
			}
			int coords_num = coords1.length + coords2.length;
			x_mean = x_sum / coords_num;
			y_mean = y_sum / coords_num;
			
			int hilbert_val = hc.grid[x_mean/div][y_mean/div];
			

			String x_min_str = Integer.toString(x_min);
			String x_max_str = Integer.toString(x_max);
			String y_min_str = Integer.toString(y_min);
			String y_max_str = Integer.toString(y_max);
			String jac_mbr = "#" + jaccard + "#" + "("
					+ x_min_str + " " + y_min_str + ","
					+ x_min_str + " " + y_max_str + ","
					+ x_max_str + " " + y_max_str + ","
					+ x_max_str + " " + y_min_str + ","
					+ x_min_str + " " + y_min_str + ")";
			Text out_val = new Text(jac_mbr);
			

			context.write(new IntWritable(hilbert_val), out_val);
		}
	}

	public static class Reduce extends
			Reducer<IntWritable, Text, IntWritable, Text> {

		public void reduce(IntWritable key, Iterable<Text> values,
				Context context) throws IOException, InterruptedException {
			
			for (Text val : values) {
				context.write(key, val);
			}
			
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "sort_by_hilbert");
		System.out.println("sort_by_hilbert");

		/*job.setNumReduceTasks(10);*/

		job.setJarByClass(sort_by_hilbert.class);

		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Text.class);

		/*FileInputFormat.addInputPath(job, new Path("input"));
		FileOutputFormat.setOutputPath(job, new Path("output"));*/

		FileInputFormat.addInputPath(job, new Path("/user/jchen/task2/t2_join_res"));
		FileOutputFormat.setOutputPath(job, new Path("/user/jchen/task2/sorted_hilbert_res"));

		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}