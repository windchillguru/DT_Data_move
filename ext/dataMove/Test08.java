package ext.dataMove;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;

public class Test08 {
	
	public static void main(String[] args) {
		WTPart part = getPartByNumber("TEST20190426001","1");
		//System.out.println(part.getName());
	}
	
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
					System.out.println("part number =="+part.getNumber());
					return part;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
