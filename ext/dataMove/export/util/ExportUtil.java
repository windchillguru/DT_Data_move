package ext.dataMove.export.util;

import java.beans.PropertyVetoException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeIssue;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.folder.SubFolder;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.IBAHolder;
import wt.iba.value.litevalue.AbstractValueView;
import wt.iba.value.litevalue.BooleanValueDefaultView;
import wt.iba.value.litevalue.FloatValueDefaultView;
import wt.iba.value.litevalue.IntegerValueDefaultView;
import wt.iba.value.litevalue.StringValueDefaultView;
import wt.iba.value.service.IBAValueHelper;
import wt.inf.library.WTLibrary;
import wt.org.WTUser;
import wt.org.electronicIdentity.ElectronicIdentification;
import wt.org.electronicIdentity.SignatureLink;
import wt.org.electronicIdentity.UserElectronicIDLink;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartMaster;
import wt.pdmlink.PDMLinkProduct;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;

import com.ptc.core.meta.type.mgmt.server.TypeDefinition;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinition;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinitionMaster;

import ext.dataMove.util.ExportConstants;

public class ExportUtil {

	private static Locale LOCALE = Locale.CHINA;

	/**
	 * 通过一个文件柜的名称获取文件柜
	 * 
	 * @param cabinetName
	 *            文件柜的名称
	 * @return
	 */
	public static PDMLinkProduct getCabinet(String cabinetName) {

		try {
			QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
			SearchCondition sc = new SearchCondition(PDMLinkProduct.class, PDMLinkProduct.NAME, "=", cabinetName,
					false);
			qs.appendWhere(sc, new int[] { 0 });
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				PDMLinkProduct cabinet = (PDMLinkProduct) qr.nextElement();
				return cabinet;
			}
		} catch (WTException e) {
			System.out.println("*getCabinet出现了异常：" + e.getMessage());
		}
		return null;
	}

	/**获取所有的产品库
	 * get all cabinets
	 * 
	 * @return
	 */
	public static List getAllCabinets() {
		List list = new ArrayList();
		try {
			QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				PDMLinkProduct cabinet = (PDMLinkProduct) qr.nextElement();
				System.out.println("cabinet name==" + cabinet.getName());
				list.add(cabinet.getName());
			}
		} catch (WTException e) {
			System.out.println("***getAllCabinet异常**" + e.getMessage());
		}
		return list;
	}

	/**
	 * 获取对象的签名信息，返回签名信息集合
	 * 
	 * @param obj
	 * @throws Exception
	 */
	public static List getSignature(WTObject obj) throws Exception {
		String number = "";
		String type = "part";
		String version = "";
		String iteration = "";
		if (obj instanceof WTDocument) {
			WTDocument document = (WTDocument) obj;
			number = document.getNumber();
			type = "doc";
			version = VersionControlHelper.getVersionIdentifier(document).getValue();
			iteration = VersionControlHelper.getIterationIdentifier(document).getValue();
		} else if (obj instanceof WTPart) {
			WTPart part = (WTPart) obj;
			number = part.getNumber();
			version = VersionControlHelper.getVersionIdentifier(part).getValue();
			iteration = VersionControlHelper.getIterationIdentifier(part).getValue();
		}

		QuerySpec qs = new QuerySpec(SignatureLink.class);
		SearchCondition sc = new SearchCondition(SignatureLink.class, "roleAObjectRef.key.id", "=",
				PersistenceHelper.getObjectIdentifier(obj).getId());
		qs.appendWhere(sc, new int[] { 0 });
		QueryResult qr = PersistenceHelper.manager.find(qs);
		String vote = "";
		String role = "";
		String comments = "";
		String userName = "";
		List list = new ArrayList();
		while (qr.hasMoreElements()) {
			SignatureLink link = (SignatureLink) qr.nextElement();
			vote = link.getVote();
			role = link.getRole();
			comments = link.getComments().replace(",", "，");
			userName = getUserNameBySignature(link.getSignature());
			list.add(type + ExportConstants.SPLIT_SIGNATURE + number + ExportConstants.SPLIT_SIGNATURE + version
					+ ExportConstants.SPLIT_SIGNATURE + iteration + ExportConstants.SPLIT_SIGNATURE + userName
					+ ExportConstants.SPLIT_SIGNATURE + vote + ExportConstants.SPLIT_SIGNATURE + role
					+ ExportConstants.SPLIT_SIGNATURE + comments);
		}
		return list;
	}

	private static String getUserNameBySignature(ElectronicIdentification signature) throws Exception {

		QueryResult qr = PersistenceHelper.manager.navigate(signature, "user", UserElectronicIDLink.class, true);
		if (qr.hasMoreElements()) {
			WTUser user = (WTUser) qr.nextElement();
			return user.getName();
		}
		return null;
	}

	public boolean isEmpty(String cs) {
		return cs == null || cs.length() == 0;
	}

	public void makeFolder(String tagertPath) {
		File file = new File(tagertPath);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * 获取某时间段内某产品库下所有的非厂商构件和序列化构件
	 * 
	 * @param cabinetName
	 * @param beginTime
	 * @param endTime
	 * @return
	 * @throws WTException
	 */
	public static QueryResult getPartsByCabinetName(String cabinetName, Timestamp beginTime, Timestamp endTime,String flag)
			throws WTException {
		try {
			SessionHelper.manager.setAdministrator();
			PDMLinkProduct pdm =null;
			WTLibrary library =null;
			
			QuerySpec qs = new QuerySpec();
			int part = qs.addClassList(WTPart.class, true);
			int definition = qs.addClassList(WTTypeDefinition.class, false);
			int definMaster = qs.addClassList(WTTypeDefinitionMaster.class, false);
			
			if("WTLibrary".equals(flag)){
				library=ExportUtil.getWTLibraryByName(cabinetName);
				// container
				qs.appendWhere(new SearchCondition(WTPart.class, WTPart.CONTAINER_ID, SearchCondition.EQUAL,
						library.getPersistInfo().getObjectIdentifier().getId()), new int[] { part });
			}else if("PDMLink".equals(flag)){
				pdm = ExportUtil.getCabinet(cabinetName);
				// container
				qs.appendWhere(new SearchCondition(WTPart.class, WTPart.CONTAINER_ID, SearchCondition.EQUAL,
						pdm.getPersistInfo().getObjectIdentifier().getId()), new int[] { part });
			}
			
			//PDMLinkProduct cabinet = getCabinet(cabinetName);
			
			
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(TypeDefinition.class, "masterReference.key.id",
					WTTypeDefinitionMaster.class, "thePersistInfo.theObjectIdentifier.id"),
					new int[] { definition, definMaster });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(TypeDefinition.class, "thePersistInfo.theObjectIdentifier.id",
					WTPart.class, "typeDefinitionReference.key.id"), new int[] { definition, part });
			qs.appendAnd();
			qs.appendWhere(
					new SearchCondition(WTTypeDefinitionMaster.class, "intHid", SearchCondition.NOT_LIKE, "%supplier%"),
					new int[] { definMaster });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTTypeDefinitionMaster.class, "intHid", SearchCondition.NOT_LIKE,
					"%wt.part.WTSerialNumberedPart%"), new int[] { definMaster });
			
			if (beginTime != null) {
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTPart.class, WTPart.CREATE_TIMESTAMP,
						SearchCondition.GREATER_THAN_OR_EQUAL, beginTime), new int[] { part });
			}
			if (endTime != null) {
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTPart.class, WTPart.CREATE_TIMESTAMP,
						SearchCondition.LESS_THAN_OR_EQUAL, endTime), new int[] { part });
			}
			// 已作废 SWIET_YZH
			//qs.appendAnd();
			//qs.appendWhere(new SearchCondition(WTPart.class, WTPart.LIFE_CYCLE_STATE, SearchCondition.NOT_EQUAL, "SWIET_YZH"),new int[] { part });
			// 已取消CANCELLED
			//qs.appendAnd();
			//qs.appendWhere(new SearchCondition(WTPart.class, WTPart.LIFE_CYCLE_STATE, SearchCondition.NOT_EQUAL, "CANCELLED"),new int[] { part });
			ClassAttribute numAttr = new ClassAttribute(WTPart.class, WTPart.NUMBER);
			qs.appendOrderBy(new OrderBy(numAttr, true), new int[] { 0 });
			
			ClassAttribute viewAttr = new ClassAttribute(WTPart.class, "view.key.id");
			qs.appendOrderBy(new OrderBy(viewAttr, false), new int[] { 0 });
			
			//小版本排序
			//ClassAttribute verAttr = new ClassAttribute(WTPart.class, "iterationInfo.identifier.iterationId");
			//qs.appendOrderBy(new OrderBy(verAttr, false), new int[] { 0 });
			
			//大版本排序
			ClassAttribute vsAttr = new ClassAttribute(WTPart.class, "versionInfo.identifier.versionId");
			qs.appendOrderBy(new OrderBy(vsAttr, false), new int[] { 0 });
			
			//修改时间排序
			ClassAttribute IDAAttr = new ClassAttribute(WTPart.class, "thePersistInfo.modifyStamp");
			qs.appendOrderBy(new OrderBy(IDAAttr, false), new int[] { 0 });
			
			System.out.println("qs==>>>>>>>>>>>>"+qs.toString());
			QueryResult qr = PersistenceHelper.manager.find(qs);
			return qr;
		} catch (Exception e) {
			System.out.println("*getWGJPartByCabinetName出现了异常：" + e.getMessage());
		}

		return null;
	}

	/**
	 * 
	 * @param object
	 *            Object to be validated
	 * @return String
	 * @exception wt.util.WTException
	 * @throws ParseException 
	 **/

	public static Hashtable getAllIBAValues(WTObject obj) throws WTException, ParseException {

		Hashtable hashtable = new Hashtable();
		//System.out.println("开始时间为： "+new Date());
		try {
			if (obj instanceof IBAHolder) {
				IBAHolder ibaholder = (IBAHolder) obj;
				DefaultAttributeContainer dac = getContainer(ibaholder);

				if (dac != null) {
					AbstractValueView avv[] = null;
					avv = dac.getAttributeValues();
					for (int j = 0; j < avv.length; j++) {
						String thisIBAName = avv[j].getDefinition().getName();
						String thisIBAClass = (avv[j].getDefinition()).getAttributeDefinitionClassName();
						if (thisIBAClass.equals("wt.iba.definition.FloatDefinition")) {
							float value = (float) ((FloatValueDefaultView) avv[j]).getValue();
							hashtable.put(thisIBAName, new Float(value));
						} else if (thisIBAClass.equals("wt.iba.definition.IntegerDefinition")) {
							long value = ((IntegerValueDefaultView) avv[j]).getValue();
							hashtable.put(thisIBAName, String.valueOf(value));
						} else if (thisIBAClass.equals("wt.iba.definition.StringDefinition")) {
							String value = ((StringValueDefaultView) avv[j]).getValue();
							hashtable.put(thisIBAName, value);
						} else if (thisIBAClass.equals("wt.iba.definition.BooleanDefinition")) {
							String value = ((BooleanValueDefaultView) avv[j]).getValueAsString();
							hashtable.put(thisIBAName, value);
						}
					}
				}
			}
		} catch (RemoteException rexp) {
			System.out.println(" ** !!!!! ** ERROR Getting IBAS");
			rexp.printStackTrace();
		}
		
		//System.out.println("结束时间为： "+new Date());
		return hashtable;
	}

	public static DefaultAttributeContainer getContainer(IBAHolder ibaholder) throws WTException, RemoteException {

		ibaholder = IBAValueHelper.service.refreshAttributeContainer(ibaholder, null, LOCALE, null);
		DefaultAttributeContainer defaultattributecontainer = (DefaultAttributeContainer) ibaholder
				.getAttributeContainer();

		return defaultattributecontainer;
	}

	public static synchronized void writeTxt(String logFilePath, String string) throws Exception {
		try {
			File file = new File(logFilePath);
			File parentFolder = file.getParentFile();
			if (!parentFolder.exists()) {
				parentFolder.mkdirs();
			}
			BufferedWriter out = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(logFilePath, true), "GBK"));
			out.write(string);
			out.flush();
			out.close();
		} catch (Exception e) {
			if (e.getMessage().indexOf("正在") != -1) {
				Thread.sleep(10);
				writeTxt(logFilePath, string);
			}
		}
	}

	public static Timestamp parseTimestamp(String paramString) throws WTException {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		try {
			return new Timestamp(sdf.parse(paramString).getTime());
		} catch (ParseException e) {
			System.out.println("*parseTimestamp异常：" + e.getMessage());
		}
		return null;
	}

	public static String getStringValue(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		return s.trim();
	}

	/**
	 * 插入创建文档表头开始部分
	 * 
	 * @param lineNo
	 * @param excelHelper
	 */
	public static String insertDocBeginTitle() {
		String title = "#BeginWTDocument,user,name,title,number,type,description,department,saveIn,teamTemplate,domain,lifecycletemplate,lifecyclestate,typedef,version,iteration,securityLabels,createTimestamp,modifyTimestamp\n"
				+ "#IBAValue,definition,value1,value2,dependency_id\r\n" + "#endIBAHolder\r\n"
				+ "#ContentFile,user,path\r\n"
				+ "#EndWTDocument,primarycontenttype,path,format,contdesc,parentContainerPath\r\n";
		return title;
	}

	public static void insertBeginWTDocumentInfo(StringBuffer sb, String user, String name, String docNumber,
			String type, String desciption, String saveIn, String lifeCycleState, String typedef, String version,
			String itration, Timestamp createTimeStamp, Timestamp modifyTimeStamp) {
		sb.append("BeginWTDocument");
		sb.append(",");
		sb.append(user);
		sb.append(",");
		sb.append("");
		sb.append(",");
		sb.append(",");
		sb.append(docNumber);
		sb.append(",");
		sb.append(type);
		sb.append(",");
		sb.append("");
		sb.append(",");
		sb.append("ENG");
		sb.append(",");
		sb.append(saveIn);
		sb.append(",");
		sb.append(",");
		sb.append(",");
		sb.append(",");
		sb.append(lifeCycleState);
		sb.append(",");
		sb.append(typedef);
		sb.append(",");
		sb.append(version);
		sb.append(",");
		sb.append(itration);
		sb.append(",");
		sb.append(",");
		sb.append(parseString(createTimeStamp));
		sb.append(",");
		sb.append(parseString(modifyTimeStamp));
		sb.append("\r\n");
	}

	/**
	 * 设置IBA属性
	 * 
	 * @param sb
	 *            StringBuffer
	 * @param ibaMap
	 */
	public static void insertIBAValues(StringBuffer sb, Hashtable hashtable) {
		Iterator it = hashtable.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			String value = (String) hashtable.get(key);
			if (value == null || value.length() == 0) {
				value = "";
			}
			if(key.indexOf("_cancel")!=-1){
				continue;
			}
			sb.append("IBAValue");
			sb.append(",");
			sb.append(key.replace(',', '，'));
			sb.append(",");
			String ibaValue = value.replace(',', '，');
			ibaValue = ibaValue.replaceAll("\r\n", "");
			sb.append(ibaValue);
			sb.append("\r\n");
		}
		sb.append("endIBAHolder");
		sb.append("\r\n");
	}
	/**
	 * 部件导入格式标题
	 * @return
	 */
	public static String insertPartBeginTitle() {
		String title = "#BeginWTPart,user,partName,partNumber,type,genericType,collapsible,logicbasePath,source,folder,lifecycle,view,variation1,variation2,teamTemplate,lifecyclestate,typedef,version"
				+ ",iteration,enditem,traceCode,organizationName,organizationID,securityLabels,createTimestamp,modifyTimestamp,minRequired,maxAllowed,defaultUnit,serviceable,servicekit,authoringLanguage\r\n"
				+ "#IBAValue,definition,value1,value2,dependency_id\r\n" + "#endIBAHolder\r\n"
				+ "#EndWTPart,parentContainerPath\r\n";

		return title;
	}
	/**
	 * 部件与文档关系格式标题
	 */
	public static String insertPartDocDescribesTitle(){
		String title = "#PartDocDescribes,docNumber,docVersion,docIteration,partNumber,partVersion,partIteration,partView,partVariation1,partVariation2,organizationName,organizationID\r\n";
		return title;
	}
	
	/**
	 * 时间格式转换
	 * 
	 * @param ts
	 * @return
	 */
	public static String parseString(Timestamp ts) {
		if (ts == null) {
			return "";
		}
		String tsStr = "";
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		try {
			tsStr = sdf.format(ts);
		} catch (Exception e) {
			System.out.println("parseString*出现了异常：" + e.getMessage());
			;
		}
		return tsStr;
	}

	public static String insertBomBeginTitle() {
		String title = "#OccurrencedAssemblyAdd,assemblyPartNumber,assemblyPartVersion,constituentPartNumber,constituentPartQty,constituentPartUnit,componentId,inclusionOption,quantityOption,reference,lineNumber,findNumber,occurrenceLocation,referenceDesignator,assemblyPartIteration,assemblyPartView,assemblyPartVariation1,assemblyPartVariation2,organizationName,organizationID\r\n";
		return title;
	}

	/**
	 * 获取某产品库，某时间段、某个类型的数据
	 * 
	 * @param cabinetName
	 * @param beginTime
	 * @param endTime
	 * @param type
	 * @return
	 */
	public static QueryResult getChangesByCabinetName(String cabinetName, Timestamp beginTime, Timestamp endTime,
			String type) {
		try {
			SessionHelper.manager.setAdministrator();
			Class cls = WTChangeRequest2.class;
			String oraKey = "";
			if (type.equalsIgnoreCase("ECN")) {
				cls = WTChangeOrder2.class;
				oraKey = "wt.change2.WTChangeOrder2";
			} else if (type.equalsIgnoreCase("SJECR")) {
				oraKey = "wt.change2.WTChangeRequest2|com.swiet.SJECR";
			} else if (type.equalsIgnoreCase("JSECR")) {
				oraKey = "wt.change2.WTChangeRequest2|com.swiet.JSECR";
			} else if (type.equalsIgnoreCase("GYECR")) {
				oraKey = "wt.change2.WTChangeRequest2|com.swiet.GYECR";
			} else if (type.equalsIgnoreCase("ISSUE")) {
				cls = WTChangeIssue.class;
				oraKey = "wt.change2.WTChangeIssue";
			} else if (type.equalsIgnoreCase("ECA")) {
				cls = WTChangeActivity2.class;
				oraKey = "wt.change2.WTChangeActivity2";
			}
			PDMLinkProduct cabinet = getCabinet(cabinetName);
			QuerySpec qs = new QuerySpec();
			int obj = qs.addClassList(cls, true);
			int definition = qs.addClassList(WTTypeDefinition.class, false);
			int definMaster = qs.addClassList(WTTypeDefinitionMaster.class, false);
			qs.appendWhere(new SearchCondition(cls, "containerReference.key.id", SearchCondition.EQUAL,
					cabinet.getPersistInfo().getObjectIdentifier().getId()), new int[] { obj });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(TypeDefinition.class, "masterReference.key.id",
					WTTypeDefinitionMaster.class, "thePersistInfo.theObjectIdentifier.id"),
					new int[] { definition, definMaster });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(TypeDefinition.class, "thePersistInfo.theObjectIdentifier.id", cls,
					"typeDefinitionReference.key.id"), new int[] { definition, obj });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTTypeDefinitionMaster.class, "intHid", SearchCondition.EQUAL, oraKey),
					new int[] { definMaster });
			if (beginTime != null) {
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(cls, "thePersistInfo.createStamp",
						SearchCondition.GREATER_THAN_OR_EQUAL, beginTime), new int[] { obj });
			}
			if (endTime != null) {
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(cls, "thePersistInfo.createStamp",
						SearchCondition.LESS_THAN_OR_EQUAL, endTime), new int[] { obj });
			}
			// 已作废 SWIET_YZH
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(cls, "state.state", SearchCondition.NOT_EQUAL, "SWIET_YZH"),
					new int[] { obj });
			// 已取消CANCELLED
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(cls, "state.state", SearchCondition.NOT_EQUAL, "CANCELLED"),
					new int[] { obj });
			QueryResult qr = PersistenceHelper.manager.find(qs);
			return qr;
		} catch (Exception e) {
			System.out.println("*getChangesByCabinetName出现了异常：" + e.getMessage());
		}
		return null;
	}

	/**
	 * 获取指定产品下的所有文档
	 * @param cabinetName
	 * @param beginTime
	 * @param endTime
	 * @return
	 * @throws WTException
	 */
	public static QueryResult getDocsByCabinetName(String cabinetName, Timestamp beginTime, Timestamp endTime,String flag)
			throws WTException {
		try {
			SessionHelper.manager.setAdministrator();
			PDMLinkProduct pdm =null;
			WTLibrary library =null;
			
			QuerySpec qs = new QuerySpec();
			int wtdoc = qs.addClassList(WTDocument.class, true);
			// int definition = qs.addClassList(WTTypeDefinition.class, false);
			// int definMaster = qs.addClassList(WTTypeDefinitionMaster.class,
			// false);
			// container
			if("WTLibrary".equals(flag)){
				library=ExportUtil.getWTLibraryByName(cabinetName);
				qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.CONTAINER_ID, SearchCondition.EQUAL,
						library.getPersistInfo().getObjectIdentifier().getId()), new int[] { wtdoc });
			}
			if("PDMLink".equals(flag)){
				pdm = getCabinet(cabinetName);
				qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.CONTAINER_ID, SearchCondition.EQUAL,
						pdm.getPersistInfo().getObjectIdentifier().getId()), new int[] { wtdoc });
			}
			
			/*
			 * qs.appendAnd(); qs.appendWhere(new
			 * SearchCondition(TypeDefinition.class, "masterReference.key.id",
			 * WTTypeDefinitionMaster.class,
			 * "thePersistInfo.theObjectIdentifier.id"), new int[] { definition,
			 * definMaster }); qs.appendAnd(); qs.appendWhere(new
			 * SearchCondition(TypeDefinition.class,
			 * "thePersistInfo.theObjectIdentifier.id", WTPart.class,
			 * "typeDefinitionReference.key.id"), new int[] { definition, part
			 * }); qs.appendAnd(); qs.appendWhere(new
			 * SearchCondition(WTTypeDefinitionMaster.class, "intHid",
			 * SearchCondition.NOT_LIKE, "%supplier%"), new int[] { definMaster
			 * }); qs.appendAnd(); qs.appendWhere(new
			 * SearchCondition(WTTypeDefinitionMaster.class, "intHid",
			 * SearchCondition.NOT_LIKE, "%wt.part.WTSerialNumberedPart%"), new
			 * int[] { definMaster });
			 * 
			 */
			if (beginTime != null) {
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.CREATE_TIMESTAMP,
						SearchCondition.GREATER_THAN_OR_EQUAL, beginTime), new int[] { wtdoc });
			}
			if (endTime != null) {
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.CREATE_TIMESTAMP,
						SearchCondition.LESS_THAN_OR_EQUAL, endTime), new int[] { wtdoc });
			}
			// 已作废 SWIET_YZH
			/*
			 * qs.appendAnd(); qs.appendWhere(new SearchCondition(WTPart.class,
			 * WTPart.LIFE_CYCLE_STATE, SearchCondition.NOT_EQUAL, "SWIET_YZH"),
			 * new int[] { part }); // 已取消CANCELLED qs.appendAnd();
			 * qs.appendWhere(new SearchCondition(WTPart.class,
			 * WTPart.LIFE_CYCLE_STATE, SearchCondition.NOT_EQUAL, "CANCELLED"),
			 * new int[] { part });
			 */
			ClassAttribute numAttr = new ClassAttribute(WTDocument.class, WTDocument.NUMBER);
			qs.appendOrderBy(new OrderBy(numAttr, true), new int[] { 0 });
			
			//ClassAttribute verAttr = new ClassAttribute(WTDocument.class, "iterationInfo.identifier.iterationId");
			//qs.appendOrderBy(new OrderBy(verAttr, false), new int[] { 0 });
			
			ClassAttribute vsAttr = new ClassAttribute(WTDocument.class, "versionInfo.identifier.versionId");
			qs.appendOrderBy(new OrderBy(vsAttr, false), new int[] { 0 });
			
			ClassAttribute IDAAttr = new ClassAttribute(WTDocument.class, "thePersistInfo.modifyStamp");
			qs.appendOrderBy(new OrderBy(IDAAttr, false), new int[] { 0 });
			
			System.out.println("qs==>>>>>>>>>>>>"+qs.toString());
			
			QueryResult qr = PersistenceHelper.manager.find(qs);
			return qr;
		} catch (Exception e) {
			System.out.println("*getWGJPartByCabinetName出现了异常：" + e.getMessage());
		}

		return null;
	}
	/**
	 * 通过存储库获取存储库的文件夹结构 
	 * @param library
	 * @return
	 */
	public static Map getFolders(WTLibrary library){
		Map map = new HashMap();
		try {
			QuerySpec qspec = new QuerySpec(SubFolder.class);
			qspec.appendWhere(new SearchCondition(SubFolder.class,SubFolder.CONTAINER_ID,SearchCondition.EQUAL,library.getPersistInfo().getObjectIdentifier().getId()),new int[]{0});
			QueryResult qr = PersistenceHelper.manager.find(qspec);
			while(qr.hasMoreElements()){
				SubFolder folder = (SubFolder)qr.nextElement();
				String folderPath = folder.getFolderPath();
				if(folderPath.startsWith("/System")){
					continue;
				}
				String[] fp = folderPath.split("/");
				List list = null;
				int num = fp.length;
				if(map.containsKey(String.valueOf(num))){
					list = (List)map.get(String.valueOf(num));
				}else{
					list = new ArrayList();
				}
				if(list.contains(folderPath)){
					continue;
				}
				list.add(folderPath);
				map.put(String.valueOf(num), list);
			}
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 通过产品对象获取产品下的文件夹结构
	 * @param pdmProduct
	 * @return
	 */
	public static Map getSubFolders(PDMLinkProduct pdmProduct){
		Map map = new HashMap();
		try {
			QuerySpec qspec = new QuerySpec(SubFolder.class);
			qspec.appendWhere(new SearchCondition(SubFolder.class,SubFolder.CONTAINER_ID,SearchCondition.EQUAL,pdmProduct.getPersistInfo().getObjectIdentifier().getId()),new int[]{0});
			QueryResult qr = PersistenceHelper.manager.find(qspec);
			while(qr.hasMoreElements()){
				SubFolder folder = (SubFolder)qr.nextElement();
				String folderPath = folder.getFolderPath();
				if(folderPath.startsWith("/System")){
					continue;
				}
				String[] fp = folderPath.split("/");
				List list = null;
				int num = fp.length;
				if(map.containsKey(String.valueOf(num))){
					list = (List)map.get(String.valueOf(num));
				}else{
					list = new ArrayList();
				}
				if(list.contains(folderPath)){
					continue;
				}
				list.add(folderPath);
				map.put(String.valueOf(num), list);
			}
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 获取存储库
	 * @param cabinetName
	 * @return
	 */
	public static WTLibrary getWTLibraryByName(String cabinetName) {

		try {
			QuerySpec qs = new QuerySpec(WTLibrary.class);
			SearchCondition sc = new SearchCondition(WTLibrary.class, WTLibrary.NAME, "=", cabinetName,
					false);
			qs.appendWhere(sc, new int[] { 0 });
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				WTLibrary cabinet = (WTLibrary) qr.nextElement();
				return cabinet;
			}
		} catch (WTException e) {
			System.out.println("*getCabinet出现了异常：" + e.getMessage());
		}
		return null;
	}
	
	/**获取所有的存储库
	 * get all cabinets
	 * 
	 * @return
	 */
	public static List getAllWTLibrary() {
		List list = new ArrayList();
		try {
			QuerySpec qs = new QuerySpec(WTLibrary.class);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				WTLibrary cabinet = (WTLibrary) qr.nextElement();
				System.out.println("cabinet name==" + cabinet.getName());
				list.add(cabinet.getName());
			}
		} catch (WTException e) {
			System.out.println("***getAllCabinet异常**" + e.getMessage());
		}
		return list;
	}
	
	public static String getTimeDifference(Date firstDate,Date secondDate){
		//一天的的毫秒数
		long d = 1000*60*60*24;
		//一小时的毫秒数
		long h = 1000*60*60;
		//一分钟的毫秒数
		long m = 1000*60;
		long timeDiff = secondDate.getTime()-firstDate.getTime();
		long min = timeDiff %d % h % m;
		long ns = timeDiff /1000;
		return ns+"";
		
	}
	
	/**
	 * 根据文档对象下载文档所有附件，并返回路径集合
	 * @param wtDoc
	 * @param temDir
	 * @return
	 * @throws PropertyVetoException 
	 * @throws WTException 
	 * @throws IOException 
	 */
	public static List downLoadDocContentFile(WTDocument wtDoc,String temDir) throws WTException, PropertyVetoException, IOException{
		
		List filePathList = new ArrayList();		
			String downLoadDirectStr = temDir + wtDoc.getNumber();
			File file = new File(downLoadDirectStr);
			if(!file.exists()){
				file.mkdirs();
			}
				wt.content.ContentHolder contenHolder = ContentHelper.service.getContents((ContentHolder)wtDoc);
				QueryResult qs = ContentHelper.service.getContentsByRole(contenHolder, ContentRoleType.SECONDARY);
				if(qs !=null && qs.size()>0){
					while(qs.hasMoreElements()){
						ContentItem ci = (ContentItem) qs.nextElement();
						if(ci instanceof ApplicationData){
							ApplicationData applicationData = (ApplicationData)ci;
							String appFileName = applicationData.getFileName();
							appFileName = appFileName.replaceAll(",", "_");
							System.out.println("-----------附件----------");
							System.out.println("contentFile applicationName=="+appFileName);
							InputStream inputStream = ContentServerHelper.service.findContentStream(applicationData);
							String fileAbsolutePath ="";
							fileAbsolutePath = temDir + wtDoc.getNumber() + File.separator +appFileName;
							FileOutputStream out = new FileOutputStream(fileAbsolutePath);
							byte bt[] = new byte[2048];
							int j;
							while((j=inputStream.read(bt, 0, bt.length))>=0){
								out.write(bt, 0, j);
							}
							out.close();
							filePathList.add(fileAbsolutePath);
						 }
				    }
				}
		return filePathList;
	}
	
	/**
	 * 根据文档对象下载文件的主内容
	 * @param wtDoc
	 * @param temDir
	 * @return
	 * @throws PropertyVetoException 
	 * @throws WTException 
	 * @throws IOException 
	 */
	public static String downLoadPrimaryFile(WTDocument wtDoc ,String temDir) throws WTException, PropertyVetoException, IOException{
					
			String downLoadDirectStr = temDir + wtDoc.getNumber();
			File file = new File(downLoadDirectStr);
			if(!file.exists()){
				file.mkdirs();
			}
			wt.content.ContentHolder contenHolder = ContentHelper.service.getContents((ContentHolder)wtDoc);
			QueryResult qs = ContentHelper.service.getContentsByRole(contenHolder, ContentRoleType.PRIMARY);
				ApplicationData applicationData=null;
				if(qs!=null && qs.size()>0){
					while(qs.hasMoreElements()){
						ContentItem ci = (ContentItem) qs.nextElement();
						if(ci instanceof ApplicationData){
							applicationData = (ApplicationData) ci;
							String appFileName = applicationData.getFileName();
							appFileName = appFileName.replaceAll(",", "_");
							System.out.println("-----------主内容----------");
							System.out.println("primary application=="+appFileName);
							String fileAbsolutePath = temDir + wtDoc.getNumber() + File.separator + appFileName;
							ContentServerHelper.service.writeContentStream(applicationData, fileAbsolutePath);
							return fileAbsolutePath;
						}
					}	
				}
		return "";
	}
	
public static String testDownLoadPrimaryFile(WTDocument wtDoc ,String temDir){
		
		try {
			
			String downLoadDirectStr = temDir + wtDoc.getNumber();
			File file = new File(downLoadDirectStr);
			if(!file.exists()){
				file.mkdirs();
			}
			ContentItem contenHolder = ContentHelper.service.getPrimary(wtDoc);
				ApplicationData applicationData=null;
						if(contenHolder instanceof ApplicationData){
							applicationData = (ApplicationData) contenHolder;
							String appFileName = applicationData.getFileName();
							appFileName = appFileName.replaceAll(",", "_");
							System.out.println("-----------主内容----------");
							System.out.println("primary application=="+appFileName);
							String fileAbsolutePath = temDir + wtDoc.getNumber() + File.separator + appFileName;
							ContentServerHelper.service.writeContentStream(applicationData, fileAbsolutePath);
							return fileAbsolutePath;
						}	
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("文件下载失败");
			return "";
		}
		return "";
	}

/**
 * 获取文档所说明的部件
 * 
 * @param doc
 */
	public static QueryResult getDesParts(WTDocument doc) throws WTException {
		System.out.println("开始查找部件所属part:");
		//LatestConfigSpec lcs = new LatestConfigSpec();
		QueryResult qr = PersistenceHelper.manager.navigate(doc, WTPartDescribeLink.DESCRIBES_ROLE,
				WTPartDescribeLink.class, true);
		System.out.println("qr==="+qr.size());
		//qr = lcs.process(qr);
		//System.out.println("返回的qr==="+qr.size());
		return qr;
	}

/**
 * 获取文档所参考的部件
 * 
 * @param doc
 * @return 2013-4-26 上午11:10:33
 * @throws WTException
 */
	public static QueryResult getReferencePart(WTDocument doc) throws WTException {
		QueryResult qr = PersistenceHelper.manager.navigate((WTDocumentMaster) doc.getMaster(), "referencedBy",
				wt.part.WTPartReferenceLink.class);
		return qr;
	}
	/**
	 * 获取指定大版本的最新部件
	 * @param partNumber
	 * @param partVersion
	 * @return
	 */
	public static WTPart getPartByNumber(String partNumber,String partVersion){
		try {
			SessionHelper.manager.setAdministrator();
			
			QuerySpec qs = new QuerySpec(WTPart.class);	
			
			qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL,partNumber.toUpperCase()));
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTPart.class, "versionInfo.identifier.versionId", SearchCondition.EQUAL,partVersion));
			
			QueryResult qr = PersistenceHelper.manager.find(qs);
			LatestConfigSpec ls = new LatestConfigSpec();
			QueryResult lsQr = ls.process(qr);
			System.out.println(lsQr.size());
			while(lsQr.hasMoreElements()){
				WTPart part = (WTPart) lsQr.nextElement();
				String temp =  VersionControlHelper.getVersionIdentifier(part).getValue();
				if(temp.equals(partVersion) && !"Manufacturing".equals(part.getViewName())){
					System.out.println("part number =="+part.getNumber()+"  "+part.getIterationDisplayIdentifier());
					return part;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获取传入对象下的版本数量总和（为解决工艺视图版本的转换）
	 * @param part
	 * @param version
	 * @param viewid
	 * @return
	 */
	public static Set changeManufacturing(WTPart part,String version,String viewid){
		Set set = new HashSet();
		try {
			WTPartMaster master = (WTPartMaster) part.getMaster();
			ReferenceFactory rf = new ReferenceFactory();
			long oid =master.getPersistInfo().getObjectIdentifier().getId();
			QuerySpec qs = new QuerySpec(WTPart.class);
			qs.appendWhere(new SearchCondition(WTPart.class, "masterReference.key.id", SearchCondition.EQUAL,oid));
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTPart.class, "view.key.id", SearchCondition.EQUAL,Long.parseLong(viewid)));
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTPart.class, "versionInfo.identifier.versionId", SearchCondition.LESS_THAN,version));
			System.out.println("sql=="+qs.toString());
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while(qr.hasMoreElements()){
				WTPart pt = (WTPart)qr.nextElement();
				set.add(VersionControlHelper.getVersionIdentifier(pt).getValue());
			}
			return set;
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return set;
	}
}
