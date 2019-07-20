package ext.dataMove;

import com.ptc.core.meta.common.TypeIdentifierUtilityHelper;

import COM.rsa.jsafe.ar;
import ext.dataMove.export.util.ExportUtil;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

public class Test2 implements RemoteAccess{

	public static void main(String[] args) throws Exception {
		if (!RemoteMethodServer.ServerFlag) {
			try {
				RemoteMethodServer rms = RemoteMethodServer.getDefault();
				rms.setUserName("wcadmin");
				rms.setPassword("wcadmin");
				String method = "process";
				String klass = Test2.class.getName();
				Class[] types = {};
				Object[] values = { };
				rms.invoke(method, klass, null, types, values);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			process();
		}
		
	}

	public static void process() {
		String number = "AD2.361.00001";
		try {
			QuerySpec qSpec  = new QuerySpec(WTPart.class);
			qSpec.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, "=",number),new int[]{0});
			QueryResult qr = PersistenceHelper.manager.find(qSpec);
			if(qr.hasMoreElements()){
				WTPart part = (WTPart) qr.nextElement();
				System.out.println("type="+part.getType());
				System.out.println("partType="+part.getPartType());
				System.out.println("getDisplayType="+part.getDisplayType());
				System.out.println("typeName="+TypeIdentifierUtilityHelper.service.getTypeIdentifier(part).getTypename());
			}
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
	}

}

