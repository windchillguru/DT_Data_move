package ext.dataMove;

import java.util.ArrayList;
import java.util.List;

public class Test04 {
	public static void main(String[] args) {
		/*List list = new ArrayList();
		list.add("1");
		list.add("2");
		list.add("e");
		list.add("4");
		list.add("5");
		list.add("6");
		for(int m=0;m<list.size();m++){
			try{
				int num = Integer.parseInt((String) list.get(m));
				System.out.println(num);
			}catch(Exception e){
				e.printStackTrace();
				continue;
			}
			System.out.println("aaaaa");
		}
		*/
		String ss = "统一硬件平台(BOM模式）";
		String pss = "统一硬件平台(BOM模式/FOLDER）";
		if("统一硬件平台(BOM模式）".equals(ss)){
			ss="统一硬件平台(BOM模式)";
		}
		
		//String aa = pss.replaceFirst(ss, "");
		System.out.println(ss);
	}
}
