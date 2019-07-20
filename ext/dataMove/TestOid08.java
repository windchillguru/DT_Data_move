package ext.dataMove;

import java.util.Set;

import wt.fc.PersistenceHelper;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import ext.dataMove.export.util.ExportUtil;

public class TestOid08 {
	public static void main(String[] args) {
		WTPart part = ExportUtil.getPartByNumber("124330297000", "4");
		//WTPartMaster master = (WTPartMaster) part.getMaster();
		//String oid =master.getPersistInfo().getObjectIdentifier().getStringValue();
		//System.out.println("oid=="+oid);
		Set set = ExportUtil.changeManufacturing(part, "31", "26632");
		System.out.println("size=="+set.size());
	}
}
