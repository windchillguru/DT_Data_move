package ext.dataMove.util;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import ext.dataMove.export.util.ExportUtil;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentHelper;
import wt.epm.EPMDocumentMaster;
import wt.epm.structure.EPMStructureHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.library.WTLibrary;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.VersionControlException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;

public class EPMDocumentUtil23 implements RemoteAccess{
	
	/**
	 * 根据master对象获取它的父对象的集合
	 * @param epmMasterList
	 * @return
	 */
	public static List getEpmRoot(List epmMasterList){
		List list = new ArrayList();
		if(!RemoteMethodServer.ServerFlag){
			Class[] aclass = {List.class};
			Object[] obj ={epmMasterList};
			try {
				RemoteMethodServer.getDefault().invoke("getEpmRoot", EPMDocumentUtil23.class.getName(), null, aclass, obj);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for(int m=0;m<epmMasterList.size();m++){
			try {
				QueryResult qr = EPMStructureHelper.service.navigateUsedBy((EPMDocumentMaster)epmMasterList.get(m), null, false);
				if(qr.size()<1){
					list.add(epmMasterList.get(m));
				}
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}
	/**
	 * 获取EPMDocumentMaster对象集合
	 * @param cabinet
	 * @return
	 */
	public static List getEPMDocumentMasterBy(String cabinet){
		List list = new ArrayList();
		try {
			SessionHelper.manager.setAdministrator();
			QuerySpec qs = new QuerySpec(EPMDocumentMaster.class);
			qs.appendWhere(new SearchCondition(EPMDocumentMaster.class, EPMDocumentMaster.DOC_TYPE,
					SearchCondition.EQUAL, "CADASSEMBLY"), new int[] { 0 });
			//qs.appendAnd();
			/*String ti = "05/01/2019 23:59:59";
			qs.appendWhere(new SearchCondition(EPMDocumentMaster.class, EPMDocumentMaster.CREATE_TIMESTAMP,
					SearchCondition.LESS_THAN_OR_EQUAL, ExportUtil.parseTimestamp(ti)), new int[] { 0 });
					*/
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if(qr.size()>0){
				System.out.println("size=="+qr.size());
				while(qr.hasMoreElements()){
					EPMDocumentMaster epmMaster = (EPMDocumentMaster)qr.nextElement();
					EPMDocument  epm = getEpmDocumentByNumber(epmMaster.getNumber());
					if(epm!=null){
						QueryResult masterQr = EPMStructureHelper.service.navigateUsedBy(epmMaster, null, false);
						System.out.println(epmMaster.getNumber());
						QueryResult epmrQr = EPMStructureHelper.service.navigateUses(epm, null, false);
						
							System.out.println("..."+epmrQr.size());
							if(masterQr.size()<1 && epmrQr.size()>0){
								list.add(epmMaster);
							}
					}
					
					
					
				}
			}
			
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	public static EPMDocument getEpmDocumentByNumber(String epmNumber){
		
		try {
			QuerySpec qs = new QuerySpec(EPMDocument.class);
			qs.appendWhere(new SearchCondition(EPMDocument.class, EPMDocument.NUMBER,SearchCondition.EQUAL, epmNumber.toUpperCase()), new int[] { 0 });
			qs.appendAnd();
			SearchCondition latest = VersionControlHelper.getSearchCondition(EPMDocument.class, true);
			qs.appendSearchCondition(latest);
			
			QueryResult qr = PersistenceHelper.manager.find(qs);
			LatestConfigSpec ls = new LatestConfigSpec();
			QueryResult qrls = ls.process(qr);
			while(qrls.hasMoreElements()){
				EPMDocument epm = (EPMDocument) qrls.nextElement();
				return epm;
			}
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (VersionControlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
