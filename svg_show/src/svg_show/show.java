package svg_show;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class show {
	public static FileReader reader;
	public static BufferedReader br;
	public static FileWriter writer;
	public static BufferedWriter bw;
    
	//fill="rgb(145,145,145)" 0-黑，200-淡灰
	public static void write_circle(int x, int y, float jaccard) throws IOException{
		int color;
		if(jaccard < 0.5)
			color = 210;
		else
			color = 200 - (int)((jaccard - 0.5f)  * 400);
		if(color < 0)
			color = 0;
		//<circle cx="50" cy="50" r="4"fill="red" />
		//bw.write("<circle cx=\""+ x +"\" cy=\""+y+"\" r=\"5\" fill=\""+color+"\" />\n");
		bw.write("<circle cx=\""+ x +"\" cy=\""+y+"\" r=\"10\" fill=\"rgb("+color + ',' + color + ','+ color +")\" />\n");
	}
	public static int[] get_centroid(String[] coords){
		int[] ans = new int[2];
		int tmp_x, tmp_y;
		int x_sum = 0;
		int y_sum = 0;
		for (int j = 0; j < coords.length - 1; ++j)
		{
			String[] xy = coords[j].split(" ");
			tmp_x = Integer.parseInt(xy[0]);
			tmp_y = Integer.parseInt(xy[1]);
			x_sum += tmp_x;
			y_sum += tmp_y;
		}
		ans[0] = x_sum/(coords.length - 1);
		ans[1] = y_sum/(coords.length - 1);
		return ans;
	}
	
	public static void read_write_polygon() throws IOException{
		reader = new FileReader("dataset/sorted_hilbert.txt");
		br = new BufferedReader(reader);
		
		String line = null, str_cood;
		float cur_jac;
		
		while ((line = br.readLine()) != null) {
			String[] values = line.split("#");
			if(values.length < 3)
				continue;
			cur_jac = Float.parseFloat(values[1]);
			str_cood = values[2].substring(1, values[2].length() - 1);
			String[] coords = str_cood.split(",");
			int []xy = get_centroid(coords);
			int x = xy[0];
			int y = xy[1];
			write_circle(x, y, cur_jac);
		}
		
		br.close();
		reader.close();
	}
	
	
	//<polyline points="10,10 10,40 40,40 40,10 10,10" style="fill:white;stroke:red;stroke-width:4" />
		public static void write_mbr(String mbr, float jaccard) throws IOException{
			float opt = 0.1f;
			if(jaccard>=0.6f && jaccard<0.7f)
				opt = 0.2f;
			if(jaccard>=0.7f && jaccard<0.72f)
				opt = 0.3f;
			if(jaccard>=0.72f && jaccard<0.74f)
				opt = 0.4f;
			if(jaccard>=0.74f && jaccard<0.76f)
				opt = 0.5f;
			if(jaccard>=0.76f && jaccard<0.78f)
				opt = 0.6f;
			if(jaccard>=0.78f && jaccard<0.80f)
				opt = 0.7f;
			if(jaccard>=0.80f && jaccard<0.82f)
				opt = 0.8f;
			if(jaccard>=0.82f && jaccard<0.9f)
				opt = 0.9f;
			if(jaccard>=0.9f && jaccard<1.0f)
				opt = 1.0f;
			//<circle cx="50" cy="50" r="4"fill="red" />
			//bw.write("<circle cx=\""+ x +"\" cy=\""+y+"\" r=\"5\" fill=\""+color+"\" />\n");
			bw.write("<polyline points=\""+mbr+"\" style=\"fill:rgba(124,240,10,"+opt+");stroke:red;stroke-width:4\" />\n");
		}
	
	public static void read_write_simjac_mbr() throws IOException{
		reader = new FileReader("dataset/jaccard_match_mbrs.txt");
		br = new BufferedReader(reader);
		
		String line = null, str_cood;
		float cur_jac;
		//0.7148273#MBR(1298 46,1298 18307,44122 18307,44122 46,1298 46)#1819
		while ((line = br.readLine()) != null) {
			String[] values = line.split("#");
			cur_jac = Float.parseFloat(values[0]);
			str_cood = values[1].substring(4, values[1].length() - 1);
			//String[] coords = str_cood.split(",");
			str_cood = str_cood.replace(',', '.');
			str_cood = str_cood.replace(' ', ',');
			str_cood = str_cood.replace('.', ' ');
			write_mbr(str_cood, cur_jac);
		}
		
		br.close();
		reader.close();
	}
	public static void main(String[] args) throws IOException{
		
		writer = new FileWriter("svg_file/test.html");
		bw = new BufferedWriter(writer);
		
		bw.write("<!DOCTYPE html><html><body><svg height=\"50000\" width=\"50000\">\n");
		
		read_write_polygon();
		read_write_simjac_mbr();
		
		bw.write("</svg></body></html>");
		
		bw.close();
        writer.close();
	}
}
