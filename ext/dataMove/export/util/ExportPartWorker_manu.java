package ext.dataMove.export.util;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import com.ptc.core.meta.common.TypeIdentifierUtilityHelper;

import ext.dataMove.util.ExportConstants;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressHelper;

public class ExportPartWorker_manu {

	public void writeCSV(String cabinetName, Timestamp ts1, Timestamp ts2,
			String flag) throws Exception {

		String export_root_dir_path = ExportConstants.EXPORT_ROOT_DIR_PATH
				+ File.separator + "Part" + File.separator + cabinetName
				+ File.separator;
		String partTargetPath = export_root_dir_path + cabinetName
				+ "_part.csv";
		

		String dt_model = "com.datangmobile.dtproduct";
		String dt_selfmade = "com.datangmobile.selfmade";
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		String logPath = export_root_dir_path +System.currentTimeMillis()+ "_partLog.log";
		StringBuffer log = new StringBuffer();
		boolean isPartExist = false;
		File partFile = new File(partTargetPath);
		if (partFile.exists()) {
			isPartExist = true;
		}
		StringBuffer sb = new StringBuffer();
		if (!isPartExist) {
			sb.append(ExportUtil.insertPartBeginTitle());
		}
		List list = new ArrayList();
		QueryResult qr = ExportUtil.getPartsByCabinetName(cabinetName, ts1,
				ts2, flag);
		if (qr == null) {
			System.out.println("cabinetName==" + cabinetName);
			return;
		}
		Persistable[] p = null;
		int index=1;
		log.append(currentTime + "  开始导出部件！\r\n");
		ExportUtil.writeTxt(logPath, log.toString());
		while (qr.hasMoreElements()) {
						
			StringBuffer msg = new StringBuffer();
			p = (Persistable[]) qr.nextElement();
			WTPart part = (WTPart) p[0];
			msg.append("---"+currentTime+"导入第"+index+"/"+qr.size()+"开始----\r\n");
			if (WorkInProgressHelper.isWorkingCopy(part)) {
				continue;
			}
			// 获取签名信息，最后处理
			list.addAll(ExportUtil.getSignature(part));
			String endItem = part.getType();
			String partTypeName = TypeIdentifierUtilityHelper.service
					.getTypeIdentifier(part).getTypename();
			String partTypeNameTemp = partTypeName.substring(partTypeName
					.lastIndexOf("|") + 1);
			if ("wt.part.WTPart".equals(partTypeNameTemp)) {
				partTypeNameTemp = dt_selfmade;
			} else if ("wt.part.WTProduct".equals(partTypeNameTemp)) {
				partTypeNameTemp = dt_model;
			}

			Hashtable hashtable = ExportUtil.getAllIBAValues(part);
			String source = part.getSource().toString();
			String parentFolder = part.getLocation();
			String version = VersionControlHelper.getVersionIdentifier(part)
					.getValue();
			String iteration = VersionControlHelper
					.getIterationIdentifier(part).getValue();
			
			String folderPath ="";
			if("统一硬件平台(BOM模式）".equals(cabinetName)){
				folderPath = parentFolder;
			}else{				
				folderPath = parentFolder.replaceAll(cabinetName, "Default");
			}
			String partView = part.getViewName();
			
			String type = part.getPartType().toString();// separable
			String xdView = "";
			if("Manufacturing".equals(partView)){
				System.out.println("部件编号是："+part.getNumber());				
				try{					
					xdView = version.substring(2, 3);//工艺视图的修订版本
				}catch(Exception e){
					e.printStackTrace();
					currentTime = new Timestamp(System.currentTimeMillis());
					msg.append(currentTime+" 处理部件编号为   "+ part.getNumber()+" "+ version +"的部件时出现异常\r\n");
					continue;
				}
			}
				if ("1".equals(iteration) && "1".equals(xdView)) {
					System.out.println("找到一个制造视图》》》" + partView);
					WTPart viewPart = ExportUtil.getPartByNumber(part.getNumber(), version.substring(0, 1));
					String partIteration = VersionControlHelper.getIterationIdentifier(viewPart).getValue();
					sb.append("NewViewVersion");
					sb.append(",");
					String partName= part.getName();
					partName = partName.replaceAll(",",ExportConstants.COMMA_REPLACE);
					partName = partName.replaceAll("\r\n","");
					sb.append(partName);
					sb.append(",");
					StringBuffer buff = new StringBuffer();
					buff.append("D5T_");
					buff.append(part.getNumber().replaceAll(",",ExportConstants.COMMA_REPLACE));
					sb.append(buff.toString());
					sb.append(",");
					sb.append(version.substring(0, 1));//新建视图关联的part版本
					sb.append(",");
					//sb.append(partIteration);由于同一部件上下文不一样导致，部分数据获取不到，暂时写1，处理异常数据后，修改回去
					sb.append("1");
					sb.append(",");
					sb.append("Design");
					sb.append(",");
					sb.append(",");
					sb.append(",");
					sb.append(type);//
					sb.append(",");
					sb.append(",");
					sb.append(",");
					sb.append(source);
					sb.append(",");
					sb.append(folderPath);
					sb.append(",");
					sb.append(part.getLifeCycleName());
					sb.append(",");
					sb.append(partView);// 部件视图
					sb.append(",");
					sb.append(",");
					sb.append(",");
					sb.append(",");
					sb.append(part.getState().getState().toString());
					sb.append(",");
					sb.append(partTypeNameTemp);// 部件类型
					sb.append(",");
					Set set = ExportUtil.changeManufacturing(part, version, "3454");
					String mVersion = String.valueOf(set.size()+1);
					sb.append(mVersion);//新建视图的版本
					sb.append(",");
					sb.append(iteration);
					sb.append(",");
					sb.append(",");
					sb.append(",");
					sb.append("\r\n");
					ExportUtil.insertIBAValues(sb, hashtable);
					sb.append("\r\n");
					msg.append(currentTime+" 新建制造视图部件编号为   "+ part.getNumber()+" "+ version +"成功\r\n");
			} else {
				sb.append("BeginWTPart");
				sb.append(",");
				sb.append(part.getCreatorName().replaceAll(",",
						ExportConstants.COMMA_REPLACE));
				sb.append(",");
				String partName= part.getName();
				partName = partName.replaceAll(",",ExportConstants.COMMA_REPLACE);
				partName = partName.replaceAll("\r\n","");
				sb.append(partName);
				sb.append(",");

				StringBuffer buff = new StringBuffer();
				buff.append("D5T_");
				buff.append(part.getNumber().replaceAll(",",
						ExportConstants.COMMA_REPLACE));
				sb.append(buff.toString());
				sb.append(",");
				sb.append(type);//

				sb.append(",");
				sb.append(",");
				sb.append(",");
				sb.append(",");
				sb.append(source);
				sb.append(",");
				sb.append(folderPath);
				sb.append(",");
				sb.append(part.getLifeCycleName());
				sb.append(",");
				sb.append(partView);// 部件视图
				sb.append(",");
				sb.append(",");
				sb.append(",");
				sb.append(",");
				sb.append(part.getState().getState().toString());
				sb.append(",");
				sb.append(partTypeNameTemp);// 部件类型
				sb.append(",");
				if("Manufacturing".equals(partView)){					
					//version = version.substring(0, 1)+version.substring(2, 3);
					
					//转换三层的视图版本到两层
					Set set = ExportUtil.changeManufacturing(part, version, "3454");
					version = String.valueOf(set.size()+1);
					
				}
				sb.append(version);
				sb.append(",");
				sb.append(iteration);
				sb.append(",");
				sb.append(endItem.equals("成品") ? "yes" : "no");
				sb.append(",");
				sb.append(",");
				sb.append(",");
				sb.append(",");
				sb.append(",");
				sb.append(ExportUtil.parseString(part.getPersistInfo().getCreateStamp()));
				sb.append(",");
				sb.append(ExportUtil.parseString(part.getPersistInfo().getModifyStamp()));
				sb.append(",");
				sb.append(",");
				sb.append(",");
				sb.append(part.getDefaultUnit().toString());
				sb.append("\r\n");
				ExportUtil.insertIBAValues(sb, hashtable);
				sb.append("EndWTPart");
				sb.append("\r\n");
				msg.append(currentTime+" 处理制造视图部件编号为   "+ part.getNumber()+" "+ version +"成功\r\n");
			}
			//msg.append("---"+currentTime+"导入第"+index+"/"+qr.size()+"结束----\r\n");
			ExportUtil.writeTxt(logPath, msg.toString());
			index++;
		}
		log.append(currentTime + "  结束导出部件！\r\n");
		ExportUtil.writeTxt(partTargetPath, sb.toString());		
		ExportUtil.writeTxt(logPath, log.toString());
		// 处理签名信息
		StringBuffer signatures = new StringBuffer();
		String signaturePath = ExportConstants.EXPORT_ROOT_DIR_PATH
				+ File.separator + "Signature" + File.separator + cabinetName
				+ File.separator + "partSignature.csv";
		File signFile = new File(signaturePath);
		if (!signFile.getParentFile().exists()) {
			signFile.getParentFile().mkdirs();
		}
		for (int i = 0; i < list.size(); i++) {
			String signs = (String) list.get(i);
			signs.replaceAll(ExportConstants.SPLIT_SIGNATURE, ",");
			signatures.append(signs).append("\r\n");
		}
		if (signatures.toString().length() > 0) {
			ExportUtil.writeTxt(signaturePath, signatures.toString());
		}
	}
}
