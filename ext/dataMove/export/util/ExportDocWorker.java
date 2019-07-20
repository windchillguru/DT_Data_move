package ext.dataMove.export.util;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressHelper;

import com.ptc.core.meta.common.TypeIdentifierUtilityHelper;

import ext.dataMove.util.ExportConstants;

public class ExportDocWorker {
	public void writeCSV(String cabinetName, Timestamp ts1, Timestamp ts2,String flag) throws Exception {

		String export_root_dir_path = ExportConstants.EXPORT_ROOT_DIR_PATH + File.separator + "document" + File.separator + cabinetName + File.separator;
		
		String partTargetPath = export_root_dir_path + "document.csv";
		
		String downLoadPrimaryPath=export_root_dir_path + "Primary" + File.separator;
		
		String downLoadContentFile = export_root_dir_path + "ContentFile" + File.separator;
		
		String logPath = export_root_dir_path+System.currentTimeMillis()+"_exportLog.log";

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
		QueryResult qr = ExportUtil.getDocsByCabinetName(cabinetName, ts1, ts2,flag);
		if (qr == null) {
			System.out.println("cabinetName==" + cabinetName);
			return;
		}
		Persistable[] p = null;
		while (qr.hasMoreElements()) {
			p = (Persistable[]) qr.nextElement();
			WTDocument doc = (WTDocument) p[0];
			if (WorkInProgressHelper.isWorkingCopy(doc)) {
				continue;
			}
			StringBuffer buff = new StringBuffer();
			
			String parentFolder = doc.getLocation();
			String version = VersionControlHelper.getVersionIdentifier(doc).getValue();
			String iteration = VersionControlHelper.getIterationIdentifier(doc).getValue();
			String folderPath = parentFolder.replaceAll(cabinetName, "Default");
			String docTypeName = TypeIdentifierUtilityHelper.service.getTypeIdentifier(doc).getTypename();
			String docTypeNameTemp = docTypeName.substring(docTypeName.lastIndexOf("|") + 1);
			String primaryPath="";
			List conFilePath = new ArrayList();
			buff.append("开始导出文档信息，时间为："+new Timestamp(System.currentTimeMillis())+"---\r\n");
			try{
				//获取主内容
				primaryPath = ExportUtil.downLoadPrimaryFile(doc, downLoadPrimaryPath);
			}catch(Exception e){
				//e.printStackTrace();
				buff.append("--"+new Timestamp(System.currentTimeMillis())+"_下载主内容时出现错误"+doc.getNumber()+"\r\n");
			}
			try{
				//获取附件
				conFilePath = ExportUtil.downLoadDocContentFile(doc,downLoadContentFile);
			}catch(Exception e){
				//e.printStackTrace();
				buff.append("--"+new Timestamp(System.currentTimeMillis())+"_下载附件时出现错误"+doc.getNumber()+"\r\n");
			}
			Hashtable hashtable = ExportUtil.getAllIBAValues(doc);
			String description =doc.getDescription();
			System.out.println("description=="+description);
			if(description==null || "".equals(description) || "null".equals(description)){
				description="";				
			}else{
				description=description.replaceAll("," , ExportConstants.COMMA_REPLACE);			
				description=description.replaceAll("\r\n", "");	
			}			
			String title = doc.getTitle();
			if("null".equals(title) || title==null){
				title="";
			}
			String docName =doc.getName();
			docName = docName.replaceAll("," , "_");
			docName = docName.replaceAll("\r\n" , "");
			//String str = ExportUtil.testDownLoadPrimaryFile(doc, downLoadContentFile);
			
			//xml 格式
			//#Document,user,name,title,number,type,description,department,saveIn,teamTemplate,domain,lifecycletemplate,lifecyclestate,typedef,primarycontenttype,path,
			//          format,contdesc,version,iteration,securityLabels,createtime,modifytime
			//#ContentFile,user,path --附件
			
			
			// 组合所有信息
			sb.append("Document");
			sb.append(",");
			sb.append(doc.getCreatorName());
			sb.append(",");
			sb.append(docName);
			sb.append(",");
			sb.append(title); //title
			sb.append(",");
			sb.append(doc.getNumber());
			sb.append(",");
			sb.append("$$Document");//??
			sb.append(",");
			sb.append(description);
			sb.append(",");
			sb.append("ENG");
			sb.append(",");
			sb.append(folderPath);
			sb.append(",");
			sb.append("");//teamTemplate
			sb.append(",");
			sb.append("");//domain
			sb.append(",");
			sb.append(doc.getLifeCycleName());//lifecycletemplate
			sb.append(",");
			sb.append(doc.getState().getState().toString());//lifecyclestate
			sb.append(",");
			sb.append(docTypeNameTemp);//typedef
			sb.append(",");
			sb.append("ApplicationData");//primarycontenttype
			sb.append(",");
			sb.append(primaryPath);//主文件路径path
			sb.append(",");
			sb.append("");//format
			sb.append(",");
			sb.append("");//contdesc
			sb.append(",");
			sb.append(version);//version
			sb.append(",");
			sb.append(iteration);//iteration
			sb.append(",");
			sb.append("");//securityLabels
			sb.append(",");
			sb.append(ExportUtil.parseString(doc.getPersistInfo().getCreateStamp()));//createtime
			sb.append(",");
			sb.append(ExportUtil.parseString(doc.getPersistInfo().getModifyStamp()));//modifytime
			sb.append("\r\n");
			ExportUtil.insertIBAValues(sb, hashtable);
			
			//附件信息(一个或者多个附件)
			if(conFilePath!=null && conFilePath.size()>0){
				for(int n=0;n<conFilePath.size();n++){
					sb.append("ContentFile");
					sb.append(",");
					sb.append(doc.getCreatorName());//创建者
					sb.append(",");
					sb.append(conFilePath.get(n));//附件路径
					sb.append("\r\n");
				}
			}			
			sb.append("\r\n");
			buff.append("结束导出文档，时间为："+new Timestamp(System.currentTimeMillis())+"---\r\n");
			ExportUtil.writeTxt(logPath, buff.toString());
		}
		ExportUtil.writeTxt(partTargetPath, sb.toString());
		// 处理签名信息
		StringBuffer signatures = new StringBuffer();
		String signaturePath = ExportConstants.EXPORT_ROOT_DIR_PATH + File.separator + "Signature" + File.separator + cabinetName + File.separator + "partSignature.csv";
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
