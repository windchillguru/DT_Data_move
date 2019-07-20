package ext.dataMove.export;

import java.text.SimpleDateFormat;
import java.util.Date;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import ext.dataMove.export.document.ExportContent;
import ext.dataMove.export.document.ExportDocument;
import ext.dataMove.export.document.ExportWTLibraryDocument;
import ext.dataMove.export.ec.ExportEC;
import ext.dataMove.export.folder.ExportSubfolder;
import ext.dataMove.export.part.ExportBom;
import ext.dataMove.export.part.ExportPart;
import ext.dataMove.export.part.ExportWTLibraryBom;
import ext.dataMove.export.part.ExportWTLibraryPart;
import ext.dataMove.export.reference.ExportPartDocDescription;
import ext.dataMove.export.user.ExportGroup;
import ext.dataMove.export.user.ExportGroupGroup;
import ext.dataMove.export.user.ExportUserGroup;
import ext.dataMove.export.user.ExportUsers;
import ext.dataMove.export.util.ExportUtil;
import ext.dataMove.util.ArgsInfo;
import ext.dataMove.util.ExportConstants;

public class ExportAll implements RemoteAccess {

	public static String xmlDocHeader = ExportUtil.insertDocBeginTitle();

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		System.out.println("start time = " + sdf.format(new Date()));
		ArgsInfo argsInfo = ArgsInfo.getInstance(args);
		if (args == null || args[0] == null) {
			System.out.println("please input arguments!");
			return;
		}
		if (!RemoteMethodServer.ServerFlag) {
			try {
				RemoteMethodServer rms = RemoteMethodServer.getDefault();
				rms.setUserName("wcadmin");
				rms.setPassword("wcadmin");
				String method = "process";
				String klass = ExportAll.class.getName();
				Class[] types = { ArgsInfo.class };
				Object[] values = { argsInfo };
				rms.invoke(method, klass, null, types, values);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			process(argsInfo);
		}
	}

	public static void process(ArgsInfo argsInfo) throws Exception {
		String type = argsInfo.getType();
		if (type.equalsIgnoreCase(ExportConstants.DATATYPE_DOC)) {
			ExportDocument.process(argsInfo);
		} else if (type.equalsIgnoreCase(ExportConstants.DATATYPE_PART)) {
			ExportPart.process(argsInfo);
		} else if (type.equalsIgnoreCase(ExportConstants.DATATYPE_USERGROUP)) {
			ExportUsers.process(argsInfo);
			ExportGroup.process(argsInfo);
			ExportUserGroup.process(argsInfo);
			ExportGroupGroup.process(argsInfo);
		} else if (type.equalsIgnoreCase(ExportConstants.DATATYPE_BOM)) {
			ExportBom.process(argsInfo);
		} else if (type.equalsIgnoreCase(ExportConstants.DATATYPE_EC)) {
			ExportEC.process(argsInfo);
		} else if (type.equalsIgnoreCase(ExportConstants.DATATYPE_CONTENT)) {
			ExportContent.process(argsInfo);
		} else if(type.equalsIgnoreCase(ExportConstants.DATATYPE_FOLDER)){
			ExportSubfolder.process(argsInfo);
		} else if(type.equalsIgnoreCase(ExportConstants.DATATYPE_LIBRARY_PART)){
			ExportWTLibraryPart.process(argsInfo);
		} else if(type.equalsIgnoreCase(ExportConstants.DATATYPE_LIBRARY_BOM)){
			ExportWTLibraryBom.process(argsInfo);
		} else if(type.equalsIgnoreCase(ExportConstants.DATATYPE_LIBRARY_DOC)){
			ExportWTLibraryDocument.process(argsInfo);
		} else if(type.equalsIgnoreCase(ExportConstants.DATATYPE_PART_DESCRIPTION)){
			ExportPartDocDescription.process(argsInfo);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		System.out.println("end time = " + sdf.format(new Date()));
	}
}
