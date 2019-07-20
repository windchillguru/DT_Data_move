package ext.dataMove.export.user;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;

import ext.dataMove.export.util.ExportUtil;
import ext.dataMove.util.ArgsInfo;
import ext.dataMove.util.ExportConstants;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.org.WTUser;
import wt.query.QuerySpec;

public class ExportUsers implements RemoteAccess {

	public static final String xmlHeader = "#User,user,newUser,webServerID,fullName,last,Locale,Email,Description,Title,Organization,Street1,Street2,City,State,Country,ZipCode,ignore,password,DirectoryService,AllowLdapSync";

	public static String USER_FILE_PATH = ExportConstants.EXPORT_ROOT_DIR_PATH + File.separator + "user";
	public static String USER_CSV_FILE_PATH = USER_FILE_PATH + File.separator + "Users.csv";

	/**
	 * @param args
	 */
	public static void process(ArgsInfo argsInfo) throws Exception {
		exportUser(USER_FILE_PATH);
	}

	public static void exportUser(String path) throws Exception {

		try {
			List user_list = searchUsers();
			int total = user_list.size();
			System.out.println("Search WTUser total:" + total);
			StringBuffer log = new StringBuffer();
			Timestamp currentTime = new Timestamp(System.currentTimeMillis());
			String logFilePath = path + File.separator + "exportuser_" + System.currentTimeMillis() + ".log";
			log.append(currentTime + "  开始导出用户！\r\n");
			ExportUtil.writeTxt(logFilePath, log.toString());
			for (int i = 0; i < total; i++) {
				boolean existCsvFile = false;
				File csvFile = new File(USER_CSV_FILE_PATH);
				if (csvFile.exists()) {
					existCsvFile = true;
				}
				StringBuffer sb = new StringBuffer();
				if (!existCsvFile) {
					sb.append(xmlHeader + "\r\n");
				}

				WTUser user = (WTUser) user_list.get(i);

				String userName = user.getName();
				if (userName.equals("Administrator")) {
					continue;
				}
				if (userName.indexOf("#") > -1 || userName.indexOf("<") > -1 || userName.indexOf(">") > -1
						|| userName.indexOf("*") > -1 || userName.indexOf("/") > -1 || userName.indexOf("\\") > -1) {
					continue;
				}
				StringBuffer msg = new StringBuffer();
				currentTime = new Timestamp(System.currentTimeMillis());
				msg.append(currentTime + "	开始处理第" + (i + 1) + "/" + total + " 个用户" + userName + " ！\r\n");
				String fullName = user.getFullName();
				if (fullName == null || fullName.equals("")) {
					fullName = userName;
				}
				String email = user.getEMail();
				if (email == null || email.equals("") || email.equals("null")) {
					email = "";
				}
				sb.append("User,");
				sb.append(",");
				sb.append(userName == null ? "" : userName);
				sb.append(",");
				sb.append(userName == null ? "" : userName);
				sb.append(",");
				sb.append(fullName);
				sb.append(",");
				sb.append(","); // Last
				sb.append("zh_CN");
				sb.append(",");
				sb.append(email);
				sb.append(",");
				sb.append(","); // Description
				sb.append(","); // Title
				sb.append("DTMobile");
				sb.append(",");
				sb.append(","); // Street1
				sb.append(","); // Street2
				sb.append(","); // City
				sb.append(","); // State
				sb.append("CN");
				sb.append(",");
				sb.append(","); // ZipCode
				sb.append(","); // ignore
				sb.append("dtmobile");// password
				sb.append(",");
				sb.append(","); // DirectoryService
				sb.append(","); // AllowLdapSync
				sb.append("\r\n");

				ExportUtil.writeTxt(USER_CSV_FILE_PATH, sb.toString());
				currentTime = new Timestamp(System.currentTimeMillis());
				msg.append(currentTime + "	结束处理第" + (i + 1) + "/" + total + " 个用户" + userName + " ！\r\n");
				ExportUtil.writeTxt(logFilePath, msg.toString());
			}
			log = new StringBuffer();
			log.append(currentTime + "  结束导出用户！\r\n");
			ExportUtil.writeTxt(logFilePath, log.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取系统中的用户
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Vector searchUsers() throws Exception {

		Vector vec = new Vector();
		QuerySpec qs = new QuerySpec(WTUser.class);
		try {
			QueryResult qr = PersistenceHelper.manager.find(qs);
			System.out.println("Search WTUser SQL:" + qs.toString());
			while (qr.hasMoreElements()) {
				WTUser user = (WTUser) qr.nextElement();
				if (user.isDisabled()) {
					continue;
				}
				vec.add(user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vec;
	}

}
