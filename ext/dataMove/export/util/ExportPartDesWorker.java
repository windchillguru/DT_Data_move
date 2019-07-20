package ext.dataMove.export.util;

import java.io.File;
import java.sql.Timestamp;

import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressHelper;
import ext.dataMove.util.ExportConstants;

public class ExportPartDesWorker {
	public void writeCSV(String cabinetName, Timestamp ts1, Timestamp ts2,String flag) throws Exception {

		String export_root_dir_path = ExportConstants.EXPORT_ROOT_DIR_PATH + File.separator + "reference" + File.separator + cabinetName + File.separator;
		
		String referencePath = export_root_dir_path + "referenceDoc.csv";
		String referenceLogPath = export_root_dir_path+System.currentTimeMillis()+"_export_log.log";
		boolean isPartExist = false;
		File partFile = new File(referencePath);
		if (partFile.exists()) {
			isPartExist = true;
		}
		StringBuffer sb = new StringBuffer();
		if (!isPartExist) {
			sb.append(ExportUtil.insertPartDocDescribesTitle());
		}
		QueryResult qr = ExportUtil.getDocsByCabinetName(cabinetName, ts1, ts2,flag);
		if (qr == null) {
			System.out.println("cabinetName==" + cabinetName);
			return;
		}
		Persistable[] p = null;
		int index=0;
		while (qr.hasMoreElements()) {
			p = (Persistable[]) qr.nextElement();
			WTDocument doc = (WTDocument) p[0];
			if (WorkInProgressHelper.isWorkingCopy(doc)) {
				continue;
			}
			StringBuffer msg = new StringBuffer();
			msg.append("---"+ new Timestamp(System.currentTimeMillis())+"导出第"+index+"/"+qr.size()+"开始----\r\n");
			//获取文档的必要信息
			String docNumber = doc.getNumber();
			String docVersion = VersionControlHelper.getVersionIdentifier(doc).getValue();
			String docIteration = VersionControlHelper.getIterationIdentifier(doc).getValue();
			
			QueryResult res = ExportUtil.getDesParts(doc);
			if(res==null || res.size()<=0){
				continue;
			}
			System.out.println("docNumber==="+docNumber);
			while(res.hasMoreElements()){
				WTPart part = (WTPart)res.nextElement();
				
				//获取部件的必要信息
				String partNumber = part.getNumber();
				String version = VersionControlHelper.getVersionIdentifier(part).getValue();
				String iteration = VersionControlHelper.getIterationIdentifier(part).getValue();
				String partView = part.getViewName();
				if("Manufacturing".equals(partView)){
					try{
						version = version.substring(0, 1)+version.substring(2, 3);
					}catch(Exception e ){
						e.printStackTrace();
						Timestamp currentTime = new Timestamp(System.currentTimeMillis());
						msg.append(currentTime+" 处理部件编号为   "+ part.getNumber()+" 版本为"+ version +"的"+partView+"视图部件时出现异常\r\n");
						
					}
				}
				
				//信息组合
				//PartDocDescribes,docNumber,docVersion,docIteration,partNumber,partVersion,partIteration,partView,partVariation1,partVariation2,organizationName,organizationID
				sb.append("PartDocDescribes");
				sb.append(",");
				sb.append(docNumber);
				sb.append(",");
				sb.append(docVersion);
				sb.append(",");
				sb.append(docIteration);
				sb.append(",");
				sb.append(partNumber);
				sb.append(",");
				sb.append(version);
				sb.append(",");
				sb.append(iteration);
				sb.append(",");
				sb.append(partView);
				sb.append(",");
				sb.append(",");
				sb.append(",");
				sb.append(",");
				sb.append("\r\n");
			}
			msg.append("***"+ new Timestamp(System.currentTimeMillis())+"导出第"+index+"/"+qr.size()+"结束***\r\n");
			ExportUtil.writeTxt(referenceLogPath, msg.toString());
			index++;
		}
		ExportUtil.writeTxt(referencePath, sb.toString());
	}
}
