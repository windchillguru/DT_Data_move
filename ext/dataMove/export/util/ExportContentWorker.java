package ext.dataMove.export.util;

import java.io.File;
import java.sql.Timestamp;

import ext.dataMove.util.ExportConstants;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

public class ExportContentWorker {

	public void writeCSV(String cabinetName, Timestamp ts1, Timestamp ts2) throws Exception {
		String export_root_dir_path = ExportConstants.EXPORT_ROOT_DIR_PATH + File.separator + "Content" + File.separator + cabinetName + File.separator;

		QueryResult qr = ExportUtil.getDocsByCabinetName(cabinetName, ts1, ts2,"PDMLink");
		if (qr == null) {
			return;
		}
		Persistable[] p = null;
		while (qr.hasMoreElements()) {
			p = (Persistable[]) qr.nextElement();
			if (WorkInProgressHelper.isWorkingCopy((Workable) p[0])) {
				continue;
			}
			String versionValue = VersionControlHelper.getVersionIdentifier((Versioned) p[0]).getValue();
			String iterationValue = VersionControlHelper.getIterationIdentifier((Versioned) p[0]).getValue();
			// 获取文档的编号
			String number = "";
			if (p[0] instanceof WTDocument) {
				number = ((WTDocument) p[0]).getNumber();
			} else if (p[0] instanceof EPMDocument) {
				number = ((EPMDocument) p[0]).getNumber();
			}
			String parentPath = export_root_dir_path + number + File.separator + versionValue + "." + iterationValue;

			QueryResult primaryqr = ContentHelper.service.getContentsByRole((ContentHolder) p[0], ContentRoleType.PRIMARY);
			while (primaryqr.hasMoreElements()) {
				Object obj = primaryqr.nextElement();
				if (!(obj instanceof ApplicationData)) {
					continue;
				}
				ApplicationData appData = (ApplicationData) obj;
				String fileName = appData.getFileName();

				File parentFolder = new File(parentPath);
				if (!parentFolder.exists()) {
					parentFolder.mkdirs();
				}
				String path = parentPath + File.separator + fileName;
				File file = new File(path);
				if (file.exists() && file.canRead()) {
					// continue;
				} else {
					try {
						ContentServerHelper.service.writeContentStream(appData, path);
					} catch (Exception e) {
						System.out.println("Exception: " + e.getMessage());
						e.printStackTrace();
					}
				}
			}

			QueryResult contentqr = ContentHelper.service.getContentsByRole((ContentHolder) p[0], ContentRoleType.SECONDARY);
			while (contentqr.hasMoreElements()) {

				Object obj = contentqr.nextElement();
				if (!(obj instanceof ApplicationData)) {
					continue;
				}
				ApplicationData appData = (ApplicationData) obj;
				String fileName = appData.getFileName();

				File parentFolder = new File(parentPath);
				if (!parentFolder.exists()) {
					parentFolder.mkdirs();
				}
				String path = parentPath + File.separator + fileName;
				File file = new File(path);
				if (file.exists() && file.canRead()) {
					// continue;
				} else {
					try {
						ContentServerHelper.service.writeContentStream(appData, path);
					} catch (Exception e) {
						System.out.println("Exception: " + e.getMessage());
						e.printStackTrace();
					}
				}

			}
		}
	}
}
