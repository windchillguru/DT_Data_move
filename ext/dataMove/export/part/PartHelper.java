package ext.dataMove.export.part;

import java.util.ArrayList;
import java.util.List;

import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.util.WTException;

public class PartHelper implements RemoteAccess{
	public static List getParentParts(List partmasterList){
		List list = new ArrayList();
		for(int m=0;m<partmasterList.size();m++){
			try {
				QueryResult qr = WTPartHelper.service.getUsedByWTParts((WTPartMaster) partmasterList.get(m));
				if(qr.size()>0){
					list.add(partmasterList.get(m));
				}
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return list;
	}
}
