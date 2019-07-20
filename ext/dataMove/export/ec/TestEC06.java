package ext.dataMove.export.ec;

import java.util.List;
import java.util.Map;

import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeRequest2;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.vc.VersionControlException;
import wt.vc.VersionControlHelper;

public class TestEC06 {
	public static void main(String[] args) {
		String ecrNo="02241";//成品
		String ecrNO1="02221";
		WTChangeRequest2 wr1 = ECUtil03.getChangeRequest(ecrNO1);
		WTChangeRequest2 wr = ECUtil03.getChangeRequest(ecrNo);
		Map map = ECUtil03.getChangeRequestResult(wr1);
		System.out.println("map  size=============="+map.size());
		List partList = (List) map.get("wtPart");
		System.out.println("size=============="+partList.size());
		for(int n=0;n<partList.size();n++){
			WTPart part = (WTPart) partList.get(n);
			System.out.println("part number ======"+part.getNumber());
			try {
				System.out.println("part version ======"+VersionControlHelper.getVersionIdentifier(part).getValue()+"."+VersionControlHelper.getIterationIdentifier(part)
						.getValue());
			} catch (VersionControlException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		List list = ECUtil03.getChangeRequestEnditem(wr);
		System.out.println("lsit size==="+list.size());
		for(int m=0;m<list.size();m++){
			System.out.println("part number =="+ ((WTPartMaster)list.get(m)).getNumber());
		}
		/*Map map1 = ECUtil.getChangeRequestResult(wr1);
		List partList1 = (List) map1.get("wtPart");
		for(int n=0;n<partList1.size();n++){
			WTPart part = (WTPart) partList1.get(n);
			System.out.println(part.getNumber());
			try {
				System.out.println(VersionControlHelper.getVersionIdentifier(part).getValue()+"."+VersionControlHelper.getIterationIdentifier(part)
						.getValue());
			} catch (VersionControlException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
	}
}
