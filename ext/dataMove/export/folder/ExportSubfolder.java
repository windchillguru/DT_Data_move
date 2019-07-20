package ext.dataMove.export.folder;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import wt.inf.library.WTLibrary;
import wt.method.RemoteAccess;
import wt.org.WTGroup;
import wt.org.WTUser;
import wt.pdmlink.PDMLinkProduct;
import ext.dataMove.export.util.ExportUtil;
import ext.dataMove.util.ArgsInfo;
import ext.dataMove.util.ExportConstants;

public class ExportSubfolder implements RemoteAccess{
	
	
	public static final String xmlHeader = "#SubFolder,user,folderPath,adminDomain";

	public static String FOLDER_FILE_PATH = ExportConstants.EXPORT_ROOT_DIR_PATH + File.separator + "subfolder";
	public static String FOLDER_CSV_FILE_PATH = FOLDER_FILE_PATH + File.separator + "subfolder.csv";

	public static void process(ArgsInfo argsInfo) throws Exception {
		
		String productName= argsInfo.getCabinet();
		PDMLinkProduct pdm =null;
		WTLibrary library =null;
		if(productName.indexOf("WTLibrary_")!=-1){
			library=ExportUtil.getWTLibraryByName(productName.replaceAll("WTLibrary_", ""));
		}else if(productName.indexOf("PDMLink_")!=-1){
			pdm = ExportUtil.getCabinet(productName.replaceAll("PDMLink_", ""));
		}
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		StringBuffer log = new StringBuffer();
		String logFilePath = FOLDER_FILE_PATH + File.separator + "exportfolder_" + System.currentTimeMillis() + ".log";
		ExportUtil.writeTxt(logFilePath, log.toString());
						
		Map map = new HashMap();
		if(library!=null){
			map = ExportUtil.getFolders(library);
		}else if(pdm!=null){
			map = ExportUtil.getSubFolders(pdm);
		}
		List keyList = new ArrayList();
		Iterator itkey = map.entrySet().iterator();
		while(itkey.hasNext()){
			Map.Entry entry = (Entry) itkey.next();
			keyList.add(entry.getKey());
		}
		Collections.sort(keyList);
		for(int n=0;n<keyList.size();n++){

			List list = (List) map.get(keyList.get(n));
			StringBuffer sb = new StringBuffer();
			boolean existCsvFile = false;
			File csvFile = new File(FOLDER_CSV_FILE_PATH);
			if (csvFile.exists()) {
				existCsvFile = true;
			}
			if (!existCsvFile) {
				sb.append(xmlHeader + "\r\n");
			}
	
			for(int m =0;m<list.size();m++){
				sb.append("SubFolder");
				sb.append(",");
				sb.append(",");
				sb.append(list.get(m));
				sb.append(",");
				sb.append(",\r\n");
			}
			ExportUtil.writeTxt(FOLDER_CSV_FILE_PATH, sb.toString());
		}
		

	}
}
