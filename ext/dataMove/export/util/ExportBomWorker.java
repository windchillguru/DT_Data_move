package ext.dataMove.export.util;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import ext.dataMove.util.ExportConstants;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.part.PartUsesOccurrence;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.templateutil.components.TimeStampComponent;
import wt.vc.VersionControlHelper;
import wt.vc.config.ConfigSpec;
import wt.vc.config.LatestConfigSpec;

public class ExportBomWorker {

	public void writeCSV(String cabinetName, Timestamp ts1, Timestamp ts2,String flag) throws Exception {

		String export_root_dir_path = ExportConstants.EXPORT_ROOT_DIR_PATH + File.separator + "Bom" + File.separator + cabinetName + File.separator;
		String partTargetPath = export_root_dir_path + cabinetName+"_bom.csv";
		String logPath = export_root_dir_path +System.currentTimeMillis()+"_export.log";
		boolean isPartExist = false;
		File partFile = new File(partTargetPath);
		if (partFile.exists()) {
			isPartExist = true;
		}
		StringBuffer sb = new StringBuffer();
		StringBuffer logBuf = new StringBuffer();
		
		if (!isPartExist) {
			sb.append(ExportUtil.insertBomBeginTitle());
		}
		QueryResult qr = ExportUtil.getPartsByCabinetName(cabinetName, ts1, ts2,flag);
		if (qr == null) {
			System.out.println("cabinetName==" + cabinetName);
			return;
		}
		logBuf.append(new Timestamp(System.currentTimeMillis())+"_开始导出Bom---"+qr.size()+"\r\n");
		Persistable[] p = null;
		System.out.println("part set ="+qr.size());
		StringBuffer msg = new StringBuffer();
		while (qr.hasMoreElements()) {
			p = (Persistable[]) qr.nextElement();
			WTPart part = (WTPart) p[0];
			String version = VersionControlHelper.getVersionIdentifier(part).getValue();
			String iteration = VersionControlHelper.getIterationIdentifier(part).getValue();
			ConfigSpec configSpec = new LatestConfigSpec();
			QueryResult links = WTPartHelper.service.getUsesWTPartsWithAllOccurrences(part, configSpec);
			String partView = part.getViewName();
			String partContainer = part.getContainerName();
			
			if("Manufacturing".equals(partView)){
				System.out.println(version);
				try{
					version = version.substring(0,1)+version.substring(2,3);
				}catch(Exception e){
					e.printStackTrace();
					msg.append("--处理部件版本时，出现异常，部件编号为："+part.getNumber()+"版本为："+version+"视图为："+partView+"\r\n");
					continue;
				}
			}
			Object[] objects = null;
			while (links != null && links.hasMoreElements()) {
				System.out.println("links set ="+links.size());
				objects = (Object[]) links.nextElement();
				WTPartUsageLink link = (WTPartUsageLink) objects[0];
				WTPartMaster childPartMaster = (WTPartMaster) link.getRoleBObject();
				
				if(!partContainer.equals(childPartMaster.getContainerName())){
					msg.append(System.currentTimeMillis()+"_部件下关联的其他产品部件："+part.getNumber()+"<>"+childPartMaster.getContainerName()+"\r\n");
				}
				
				System.out.println("name=="+part.getNumber()+"start---");
				//System.out.println("link=="+link);
				String referenceDesignator = getOccurence(link);
				sb.append("OccurrencedAssemblyAdd");
				sb.append(",");
				sb.append(part.getNumber());
				sb.append(",");
				sb.append(version);
				sb.append(",");
				sb.append(childPartMaster.getNumber());
				sb.append(",");
				sb.append(link.getQuantity().getAmount() + "");
				sb.append(",");
				sb.append(link.getQuantity().getUnit().toString());
				sb.append(",");
				sb.append(",");
				sb.append(",");
				sb.append(",");
				sb.append(",");
				sb.append(",");
				sb.append(",");
				sb.append(",");
				if (referenceDesignator != null && referenceDesignator.length() > 0) {
					sb.append(referenceDesignator);
				}
				sb.append(",");
				sb.append(iteration + "");
				sb.append(",");
				sb.append(partView);
				sb.append("\r\n");
			}			
			
		}
		ExportUtil.writeTxt(logPath, msg.toString());
		logBuf.append(new Timestamp(System.currentTimeMillis())+"_导出结束\r\n");
		ExportUtil.writeTxt(logPath, logBuf.toString());
		if(sb.toString().length()>0){
			ExportUtil.writeTxt(partTargetPath, sb.toString());
		}
	}

	public String getOccurence(WTPartUsageLink link) {
		Vector vector = link.getUsesOccurrenceVector();
		//System.out.println("vector size==="+vector.size());
		List values = new ArrayList();
		for (int j = 0; j < vector.size(); j++) {
			PartUsesOccurrence usesOccurrence = (PartUsesOccurrence) vector.get(j);
			if(usesOccurrence.getName()==null)
				continue;
			values.add(usesOccurrence.getName());
		}
		String value = "";
		//System.out.println("values>>>"+values);
		//System.out.println("values   size  ==="+values.size());
			Collections.sort(values);			
			for (int j = 0; j < values.size(); j++) {
				value = value + values.get(j) + "，";
			}
			if (value.length() > 0) {
				value = value.substring(0, value.length() - 1);
			}
			if (value == null || value.length() == 0 || value.equalsIgnoreCase("null")) {
				value = "";
			}
		
		return value;
	}
}
