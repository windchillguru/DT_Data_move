package ext.dataMove.export.user;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import ext.dataMove.export.util.ExportUtil;
import ext.dataMove.util.ArgsInfo;
import ext.dataMove.util.ExportConstants;
import wt.method.RemoteAccess;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTUser;

public class ExportUserGroup implements RemoteAccess {

	public static final String xmlHeader = "#UserGroup,user,groupName,groupNameDirectoryService,userName,userNameDirectoryService,DirectoryService";

	public static String USERGROUP_FILE_PATH = ExportConstants.EXPORT_ROOT_DIR_PATH + File.separator + "UserGroup";
	public static String USERGROUP_CSV_FILE_PATH = USERGROUP_FILE_PATH + File.separator + "UserGroups.csv";

	public static void process(ArgsInfo argsInfo) throws Exception {

		List user_list = ExportUsers.searchUsers();
		List userNameList = new ArrayList();
		for (int i = 0; i < user_list.size(); i++) {
			String name = ((WTUser) user_list.get(i)).getName();
			userNameList.add(name);
		}
		List group_list = ExportGroup.searchGroup();
		int total = group_list.size();
		System.out.println("Search WTGroup total:" + total);

		StringBuffer log = new StringBuffer();
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		String logFilePath = USERGROUP_FILE_PATH + File.separator + "exportusergroup_" + System.currentTimeMillis() + ".log";
		log.append(currentTime + " 开始导出用户和组！\r\n");
		ExportUtil.writeTxt(logFilePath, log.toString());
		for (int i = 0; i < total; i++) {
			boolean existCsvFile = false;
			File csvFile = new File(USERGROUP_CSV_FILE_PATH);
			if (csvFile.exists()) {
				existCsvFile = true;
			}

			StringBuffer sb = new StringBuffer();
			if (!existCsvFile) {
				sb.append(xmlHeader + "\r\n");
			}
			WTGroup group = (WTGroup) group_list.get(i);
			String groupName = group.getName();
			StringBuffer msg = new StringBuffer();
			currentTime = new Timestamp(System.currentTimeMillis());
			msg.append(currentTime + " 开始处理" + (i + 1) + "/" + total + " 个组 " + groupName + " ！\r\n");

			List usergroup_list = getGroupMembersOfUser(group);
			for (int j = 0; j < usergroup_list.size(); j++) {
				WTUser user = (WTUser) usergroup_list.get(j);
				String userName = user.getName();
				if (!userNameList.contains(userName)) {
					System.out.println("忽略" + groupName + "下" + userName);
					continue;
				}
				sb.append("UserGroup,");
				sb.append(",");
				sb.append(groupName + ",");
				sb.append(",");
				sb.append(userName + ",");
				sb.append(",");
				sb.append("\r\n");
			}

			ExportUtil.writeTxt(USERGROUP_CSV_FILE_PATH, sb.toString());
			currentTime = new Timestamp(System.currentTimeMillis());
			msg.append(currentTime + " 结束处理" + (i + 1) + "/" + total + " 个组 " + groupName + " ！\r\n");
			ExportUtil.writeTxt(logFilePath, msg.toString());
		}
		log = new StringBuffer();
		log.append(currentTime + "  结束导出用户与组！\r\n");
		ExportUtil.writeTxt(logFilePath, log.toString());
	}

	/**
	 * 获取组中的成员
	 * 
	 * @param group
	 * @return
	 * @throws Exception
	 */
	public static List getGroupMembersOfUser(WTGroup group) throws Exception {
		if (group == null) {
			return null;
		}
		List users = new ArrayList();
		Enumeration member = group.members();
		try {
			while (member.hasMoreElements()) {
				WTPrincipal principal = (WTPrincipal) member.nextElement();
				if (principal instanceof WTUser) {
					users.add((WTUser) principal);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return users;
	}
}
