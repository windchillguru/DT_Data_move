package ext.dataMove;

import java.io.File;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ext.dataMove.export.util.ExportUtil;
import ext.dataMove.util.ExportConstants;

public class Test03 {
	public static void main(String[] args) throws ParseException {
		/*Map map = new HashMap();
		List tlist = new ArrayList();
		List tlist1 = new ArrayList();
		List tlist2 = new ArrayList();
		List keyList = new ArrayList();
		tlist.add("asdf");
		tlist1.add("asdf");
		tlist2.add("asdf");
		map.put(String.valueOf(1), tlist);
		tlist1.add("scde");
		tlist2.add("scde");
		map.put(String.valueOf(6), tlist2);
		map.put(String.valueOf(2), tlist1);
		tlist2.add("bgt");
		map.put(String.valueOf(3), tlist2);
		map.put(String.valueOf(5), tlist2);
		map.put(String.valueOf(8), tlist2);
		Iterator it = map.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry entry = (Entry) it.next();
			keyList.add(entry.getKey());
		}
		
		Collections.sort(keyList);
		
		while(it.hasNext()){
			Map.Entry entry = (Entry) it.next();
			List list = (List) entry.getValue();
			System.out.println("start output....");
			for(int m=0;m<list.size();m++){
				System.out.println(list.get(m));
			}
		}
		System.out.println(keyList);*/
		String xulie = "2.1.1";
		System.out.println(xulie.length());
		
		System.out.println(xulie.substring(0,1));
		System.out.println(xulie.substring(2,3));
		System.out.println(xulie.substring(4,5));
		System.out.println(xulie.replace('.','a'));
		/*String time1="2019-4-22 10:00:03";
		String time2="2019-4-22 11:32:01";
		SimpleDateFormat sf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
		Date date1= sf.parse(time1);
		Date date2= sf.parse(time2);
		String str = ExportUtil.getTimeDifference(date1, date2);
		System.out.println(str);
		String startDate =new SimpleDateFormat("YYYY-MM-dd HH:mm:ss ").format(new Date());
		System.out.println(startDate);*/
		//String endDate =new SimpleDateFormat("YYYY-MM-dd HH:mm:ss ").format(new Date());
		//SimpleDateFormat sf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
		//System.out.println("查询IBA时间差为：  "+ExportUtil.getTimeDifference(sf.parse(endDate), sf.parse(endDate))+" （秒）");
		
		
		/*String export_root_dir_path = ExportConstants.EXPORT_ROOT_DIR_PATH
				+ File.separator + "Part" + File.separator + "test"
				+ File.separator;
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		String logPath = export_root_dir_path  + "_partLog.log";
		File file = new File(logPath);
		if(!file.exists())
			file.mkdirs();
		*/
		
		/*StringBuffer sb = new StringBuffer();
		sb.append("aaa");
		try {
			ExportUtil.writeTxt(logPath, sb.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
