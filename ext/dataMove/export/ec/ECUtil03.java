package ext.dataMove.export.ec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;

public class ECUtil03 implements RemoteAccess{
	
	/**
	 * 根据更改通告的编号获取更改通告对象
	 * @param ecNo
	 * @return
	 */
	public static WTChangeOrder2 getChangeOrder2ByNumber(String ecNo){
		try {
			QuerySpec qs = new QuerySpec(WTChangeOrder2.class);
			qs.appendWhere(new SearchCondition(WTChangeOrder2.class,"number",SearchCondition.EQUAL,ecNo),new int[]{0});
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while(qr.hasMoreElements()){
				WTChangeOrder2 wo = (WTChangeOrder2)qr.nextElement();
				return wo;
			}
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 根据编号获取对应的更改请求
	 * @param ecrNo
	 * @return
	 */
	public static WTChangeRequest2 getChangeRequest(String ecrNo){
		try {
			QuerySpec qs = new QuerySpec(WTChangeRequest2.class);
			qs.appendWhere(new SearchCondition(WTChangeRequest2.class,"number",SearchCondition.EQUAL,ecrNo),new int []{0});
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while(qr.hasMoreElements()){
				WTChangeRequest2 wr = (WTChangeRequest2)qr.nextElement();
				return wr;
			}
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 根据更改请求，获取受影响的数据
	 * @param wr
	 * @return
	 */
	public static Map getChangeRequestResult(WTChangeRequest2 wr){
		Map map = new HashMap();
		List docList = new ArrayList();
		List epmList = new ArrayList();
		List partList = new ArrayList();
		
		try {
			QueryResult qr = ChangeHelper2.service.getChangeables(wr);
			while(qr.hasMoreElements()){
				Object obj = qr.nextElement();
				if(obj instanceof WTDocument){
					docList.add(obj);
				}else if(obj instanceof EPMDocument){
					epmList.add(obj);
				}else if(obj instanceof WTPart){
					partList.add(obj);
				}
			}
			map.put("wtDocument", docList);
			map.put("epmDocument", epmList);
			map.put("wtPart", partList);
			return map;
		} catch (ChangeException2 e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 获取受影响的成品
	 * @param wr
	 * @return
	 */
	public static List getChangeRequestEnditem(WTChangeRequest2 wr){
		List list = new ArrayList();
		try {
			QueryResult qr = ChangeHelper2.service.getSubjectProducts(wr);
			
			System.out.println("qr==="+qr.size());
			while(qr.hasMoreElements()){
				Object obj = qr.nextElement();
				if(obj instanceof WTPartMaster){
					list.add(obj);
				}
			}
		} catch (ChangeException2 e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 获取EC的附件
	 */
	public static void getECContentfile(){
		
	}
	
	/**
	 * 设置受影响的成品
	 */
	public static void setAffectEnditem(){
		
	}
}
