package ext.dataMove;

import java.sql.Timestamp;
import java.util.Date;

import com.ptc.core.meta.type.mgmt.server.TypeDefinition;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinitionMaster;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.library.WTLibrary;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pdmlink.PDMLinkProduct;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import ext.dataMove.export.util.ExportUtil;

public class TEST13 {
	public static void main(String[] args) throws WTException {
		QueryResult qr = getParts("RAN-LTEV4商用产品-宏基站硬件开发项目",ExportUtil.parseTimestamp("01/01/1990  00:00:00"),ExportUtil.parseTimestamp("04/01/2019  23:59:59"),"PDMLink");
		Persistable[] p = null;
		while(qr.hasMoreElements()){
			p = (Persistable[]) qr.nextElement();
			WTPart part = (WTPart) p[0];
			if("124375436000".equals(part.getNumber())){
				System.out.println("部件版本："+VersionControlHelper.getVersionIdentifier(part).getValue()+"."+VersionControlHelper
						.getIterationIdentifier(part).getValue());
			}
		}
	}
	public static QueryResult getParts(String cabinetName, Timestamp beginTime, Timestamp endTime,String flag)
			throws WTException {
		try {
			SessionHelper.manager.setAdministrator();
			PDMLinkProduct pdm =null;
			WTLibrary library =null;
			
			QuerySpec qs = new QuerySpec();
			int part = qs.addClassList(WTPart.class, true);	
			int partMaster = qs.addClassList(WTPartMaster.class, false);
			
			
			qs.appendWhere(new SearchCondition(WTPart.class, "masterReference.key.id",
					WTPartMaster.class, "thePersistInfo.theObjectIdentifier.id"),
					new int[] { part, partMaster });
			
			
			if("WTLibrary".equals(flag)){
				library=ExportUtil.getWTLibraryByName(cabinetName);
				// container
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTPartMaster.class, WTPartMaster.CONTAINER_ID, SearchCondition.EQUAL,
						library.getPersistInfo().getObjectIdentifier().getId()), new int[] { partMaster });
			}else if("PDMLink".equals(flag)){
				pdm = ExportUtil.getCabinet(cabinetName);
				// container
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTPartMaster.class, WTPartMaster.CONTAINER_ID, SearchCondition.EQUAL,
						pdm.getPersistInfo().getObjectIdentifier().getId()), new int[] { partMaster });
				
			}
			
			//PDMLinkProduct cabinet = getCabinet(cabinetName);
			
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

			ClassAttribute numAttr = new ClassAttribute(WTPart.class, WTPart.NUMBER);
			qs.appendOrderBy(new OrderBy(numAttr, true), new int[] { 0 });
			
			ClassAttribute viewAttr = new ClassAttribute(WTPart.class, "view.key.id");
			qs.appendOrderBy(new OrderBy(viewAttr, false), new int[] { 0 });			
			
			//大版本排序
			ClassAttribute vsAttr = new ClassAttribute(WTPart.class, "versionInfo.identifier.versionId");
			qs.appendOrderBy(new OrderBy(vsAttr, false), new int[] { 0 });
			
			//修改时间排序
			ClassAttribute IDAAttr = new ClassAttribute(WTPart.class, "thePersistInfo.modifyStamp");
			qs.appendOrderBy(new OrderBy(IDAAttr, false), new int[] { 0 });
			
			System.out.println("qs==>>>>>>>>>>>>"+qs.toString());
			System.out.println(new Date());
			QueryResult qr = PersistenceHelper.manager.find(qs);
			System.out.println(qr.size());
			System.out.println("over  "+new Date());
			return qr;
		} catch (Exception e) {
			System.out.println("*getWGJPartByCabinetName出现了异常：" + e.getMessage());
		}

		return null;
	}
}
