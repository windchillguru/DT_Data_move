package ext.dataMove.export.user;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import ext.dataMove.export.util.ExportUtil;
import ext.dataMove.util.ArgsInfo;
import ext.dataMove.util.ExportConstants;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.org.WTGroup;
import wt.query.QuerySpec;
import wt.util.WTException;

public class ExportGroup implements RemoteAccess {

	public static final String xmlHeader = "#Group,user,groupName,description,DirectoryService";

	public static String GROUP_FILE_PATH = ExportConstants.EXPORT_ROOT_DIR_PATH + File.separator + "Group";
	public static String GROUP_CSV_FILE_PATH = GROUP_FILE_PATH + File.separator + "Groups.csv";

	public static void process(ArgsInfo argsInfo) throws Exception {

		List group_list = searchGroup();
		int total = group_list.size();
		System.out.println("Search WTGroup total:" + total);

		Timestamp currentTime = new Timestamp(System.currentTimeMillis());

		StringBuffer log = new StringBuffer();
		String logFilePath = GROUP_FILE_PATH + File.separator + "exportgroup_" + System.currentTimeMillis() + ".log";
		log.append(currentTime + "开始导出组\r\n");
		ExportUtil.writeTxt(logFilePath, log.toString());
		for (int i = 0; i < group_list.size(); i++) {
			boolean existCsvFile = false;
			File csvFile = new File(GROUP_CSV_FILE_PATH);
			if (csvFile.exists()) {
				existCsvFile = true;
			}
			StringBuffer sb = new StringBuffer();
			if (!existCsvFile) {
				sb.append(xmlHeader + "\r\n");
			}
			WTGroup group = (WTGroup) group_list.get(i);
			String groupName = group.getName();
			if (groupName.indexOf("Admin") > -1) {
				continue;
			}
			String description = group.getDescription();
			// description = ExportUtil.processChar(description);
			// if (description == null || description.equals("")
			// || description.equals("null")) {
			// description = "";
			// }
			StringBuffer msg = new StringBuffer();
			currentTime = new Timestamp(System.currentTimeMillis());
			msg.append(currentTime + "开始处理第" + (i + 1) + "/" + total + "个" + groupName + "\r\n");
			sb.append("Group,");
			sb.append(",");
			sb.append(groupName);
			sb.append(",");
			sb.append(description);
			sb.append(",");
			sb.append(",\r\n");
			ExportUtil.writeTxt(GROUP_CSV_FILE_PATH, sb.toString());
			currentTime = new Timestamp(System.currentTimeMillis());
			msg.append(currentTime + "结束处理第" + (i + 1) + "/" + total + "个组" + groupName + "\r\n");
			ExportUtil.writeTxt(logFilePath, msg.toString());
		}
		log = new StringBuffer();
		log.append(currentTime + "组导出完成\r\n");
		ExportUtil.writeTxt(logFilePath, log.toString());
	}

	/**
	 * ?
	 * 
	 * @return
	 * @throws Exception
	 */
	public static List searchGroup() throws Exception {
		List list = new ArrayList();
		try {
			QuerySpec qs = new QuerySpec(WTGroup.class);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			System.out.println("Search WTGroup SQL:" + qs.toString());
			long btime = System.currentTimeMillis();
			while (qr.hasMoreElements()) {
				WTGroup group = (WTGroup) qr.nextElement();
				String groupName = group.getName();
				if (groupName.equals("Administrators") || groupName.equalsIgnoreCase("Authors")
						|| groupName.equals("Business Entity Authors") || groupName.equals("CREATORS")) {
					continue;
				}
				if (group.isDisabled() || group.isInheritedDomain()) {
					continue;
				}
				list.add(group);
			}
			long etime = System.currentTimeMillis();
			System.out.println("Search WTGroup Cost:" + (etime - btime) / 1000 + " s");
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}
}
