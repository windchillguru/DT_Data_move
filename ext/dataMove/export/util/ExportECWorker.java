package ext.dataMove.export.util;

import java.io.File;
import java.sql.Timestamp;
import java.util.Hashtable;

import ext.dataMove.util.ExportConstants;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeIssue;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.part.WTPart;

public class ExportECWorker {

	public void writeCSV(String cabinetName, Timestamp ts1, Timestamp ts2, String type) throws Exception {

		String export_root_dir_path = ExportConstants.EXPORT_ROOT_DIR_PATH + File.separator + "EC" + File.separator + cabinetName + File.separator;
		String partTargetPath = export_root_dir_path + type + ".csv";
		String relatedObjects = export_root_dir_path + type + "_LinkObjects.csv";
		QueryResult qr = ExportUtil.getChangesByCabinetName(cabinetName, ts1, ts2, type);
		if (qr == null) {
			System.out.println("cabinetName==" + cabinetName);
			return;
		}
		Persistable[] p = null;
		String oraKey = "";
		if (type.equalsIgnoreCase("ECN")) {
			oraKey = "wt.change2.WTChangeOrder2";
		} else if (type.equalsIgnoreCase("SJECR")) {
			oraKey = "wt.change2.WTChangeRequest2|com.swiet.SJECR";
		} else if (type.equalsIgnoreCase("JSECR")) {
			oraKey = "wt.change2.WTChangeRequest2|com.swiet.JSECR";
		} else if (type.equalsIgnoreCase("GYECR")) {
			oraKey = "wt.change2.WTChangeRequest2|com.swiet.GYECR";
		} else if (type.equalsIgnoreCase("ISSUE")) {
			oraKey = "wt.change2.WTChangeIssue";
		} else if (type.equalsIgnoreCase("ECA")) {
			oraKey = "wt.change2.WTChangeActivity2";
		}
		File ecFile = new File(partTargetPath);
		boolean flag = false;
		if (!ecFile.exists()) {
			flag = true;
		}
		StringBuffer sb = new StringBuffer();
		if (!flag) {
			sb.append("#begin,ecnumber,ecname,state,folder,typedef,containername\r\n");
		}
		StringBuffer objsb = new StringBuffer();
		File eclinkFile = new File(relatedObjects);
		if (!eclinkFile.exists()) {
			objsb.append("#changeableNumber,version,iteration,changeableTypedef,ecnumber,ectypedef,changetype\r\n");
		}
		while (qr.hasMoreElements()) {
			p = (Persistable[]) qr.nextElement();
			// 处理问题报告
			if (p[0] instanceof WTChangeIssue) {
				WTChangeIssue issue = (WTChangeIssue) p[0];
				String number = issue.getNumber();
				String name = issue.getName();
				String state = issue.getLifeCycleState().toString();
				String folder = issue.getFolderPath();
				sb.append("BeginISSUE");
				sb.append(",");
				sb.append(number);
				sb.append(",");
				sb.append(name);
				sb.append(",");
				sb.append(state);
				sb.append(",");
				sb.append(folder);
				sb.append(",");
				sb.append(oraKey);
				sb.append(",");
				sb.append(cabinetName);
				sb.append("\r\n");
				Hashtable hashtable = ExportUtil.getAllIBAValues(issue);
				ExportUtil.insertIBAValues(sb, hashtable);
				// 处理问题报告和对象关系
				QueryResult issueR = ChangeHelper2.service.getChangeables(issue);
				dealQueryResult(objsb, issueR, number, oraKey, null);
			} else if (p[0] instanceof WTChangeOrder2) {
				// 处理ECN
				WTChangeOrder2 ecn = (WTChangeOrder2) p[0];
				String number = ecn.getNumber();
				String name = ecn.getName();
				String state = ecn.getLifeCycleState().toString();
				String folder = ecn.getFolderPath();
				sb.append("BeginECN");
				sb.append(",");
				sb.append(number);
				sb.append(",");
				sb.append(name);
				sb.append(",");
				sb.append(state);
				sb.append(",");
				sb.append(folder);
				sb.append(",");
				sb.append(oraKey);
				sb.append(",");
				sb.append(cabinetName);
				sb.append("\r\n");
				Hashtable hashtable = ExportUtil.getAllIBAValues(ecn);
				ExportUtil.insertIBAValues(sb, hashtable);
				// 处理和对象关系（ecn和eca的关系）
				QueryResult qResult = ChangeHelper2.service.getChangeActivities(ecn);
				if (qResult != null && qResult.hasMoreElements()) {
					StringBuffer eclinkSB = new StringBuffer();
					String relatedEC = export_root_dir_path + "ECNAndECALink.csv";
					File file = new File(relatedEC);
					if (!file.exists()) {
						eclinkSB.append("#ecn,eca\r\n");
					}
					while (qResult.hasMoreElements()) {
						WTChangeActivity2 eca = (WTChangeActivity2) qResult.nextElement();
						eclinkSB.append(number);
						eclinkSB.append(",");
						eclinkSB.append(eca.getNumber());
						eclinkSB.append("\r\n");
					}
					if (eclinkSB.toString().length() > 0) {
						ExportUtil.writeTxt(relatedEC, eclinkSB.toString());
					}
				}
			} else if (p[0] instanceof WTChangeRequest2) {
				// 处理ecr
				WTChangeRequest2 ecr = (WTChangeRequest2) p[0];
				String number = ecr.getNumber();
				String name = ecr.getName();
				String state = ecr.getLifeCycleState().toString();
				String folder = ecr.getFolderPath();
				sb.append("BeginECR");
				sb.append(",");
				sb.append(number);
				sb.append(",");
				sb.append(name);
				sb.append(",");
				sb.append(state);
				sb.append(",");
				sb.append(folder);
				sb.append(",");
				sb.append(oraKey);
				sb.append(",");
				sb.append(cabinetName);
				sb.append("\r\n");
				Hashtable hashtable = ExportUtil.getAllIBAValues(ecr);
				ExportUtil.insertIBAValues(sb, hashtable);
				// 处理和对象关系
				QueryResult issueR = ChangeHelper2.service.getChangeables(ecr);
				dealQueryResult(objsb, issueR, number, oraKey, null);
				// 处理ecr和ecn关系
				QueryResult qResult = ChangeHelper2.service.getChangeOrders(ecr);
				if (qResult != null && qResult.hasMoreElements()) {
					StringBuffer eclinkSB = new StringBuffer();
					String relatedEC = export_root_dir_path + "ECRAndECNLink.csv";
					File file = new File(relatedEC);
					if (!file.exists()) {
						eclinkSB.append("#ecr,ecn\r\n");
					}
					while (qResult.hasMoreElements()) {
						WTChangeOrder2 ecn = (WTChangeOrder2) qResult.nextElement();
						eclinkSB.append(number);
						eclinkSB.append(",");
						eclinkSB.append(ecn.getNumber());
						eclinkSB.append("\r\n");
					}
					if (eclinkSB.toString().length() > 0) {
						ExportUtil.writeTxt(relatedEC, eclinkSB.toString());
					}
				}
			} else if (p[0] instanceof WTChangeActivity2) {
				// 处理ECA
				WTChangeActivity2 eca = (WTChangeActivity2) p[0];
				String number = eca.getNumber();
				String name = eca.getName();
				String state = eca.getLifeCycleState().toString();
				String folder = eca.getFolderPath();
				sb.append("BeginECA");
				sb.append(",");
				sb.append(number);
				sb.append(",");
				sb.append(name);
				sb.append(",");
				sb.append(state);
				sb.append(",");
				sb.append(folder);
				sb.append(",");
				sb.append(oraKey);
				sb.append(",");
				sb.append(cabinetName);
				sb.append("\r\n");
				Hashtable hashtable = ExportUtil.getAllIBAValues(eca);
				ExportUtil.insertIBAValues(sb, hashtable);
				// 处理和对象关系
				QueryResult ecnr = ChangeHelper2.service.getChangeablesBefore(eca);
				dealQueryResult(objsb, ecnr, number, oraKey, "before");
				ecnr = ChangeHelper2.service.getChangeablesAfter(eca);
				dealQueryResult(objsb, ecnr, number, oraKey, "after");
			}
		}
		if (sb.toString().length() > 0) {
			ExportUtil.writeTxt(partTargetPath, sb.toString());
		}
		if (objsb.toString().length() > 0) {
			ExportUtil.writeTxt(relatedObjects, objsb.toString());
		}
	}

	public static void dealQueryResult(StringBuffer objsb, QueryResult qr, String number, String oraKey, String type) {
		String childNumber = "";
		String version = "";
		String iteration = "";
		String objType = "";
		while (qr.hasMoreElements()) {
			Object obj = qr.nextElement();
			if (obj instanceof WTPart) {
				WTPart part = (WTPart) obj;
				childNumber = part.getNumber();
				version = part.getVersionIdentifier().getValue();
				iteration = part.getIterationIdentifier().getValue();
				objType = "part";
			} else if (obj instanceof WTDocument) {
				WTDocument doc = (WTDocument) obj;
				childNumber = doc.getNumber();
				version = doc.getVersionIdentifier().getValue();
				objType = "doc";
			} else if (obj instanceof EPMDocument) {
				EPMDocument doc = (EPMDocument) obj;
				childNumber = doc.getNumber();
				version = doc.getVersionIdentifier().getValue();
				iteration = doc.getIterationIdentifier().getValue();
				objType = "epm";
			}
			objsb.append(childNumber);
			objsb.append(",");
			objsb.append(version);
			objsb.append(",");
			objsb.append(iteration);
			objsb.append(",");
			objsb.append(objType);
			objsb.append(",");
			objsb.append(number);
			objsb.append(",");
			objsb.append(oraKey);
			if (type != null) {
				objsb.append(",");
				objsb.append(type);
			}
			objsb.append("\r\n");
		}
	}
}
