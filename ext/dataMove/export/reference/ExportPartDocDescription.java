package ext.dataMove.export.reference;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import wt.method.RemoteAccess;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.session.SessionThread;
import ext.dataMove.export.util.ExportUtil;
import ext.dataMove.runnable.ExportPartDocRunnable;
import ext.dataMove.runnable.ExportWTLibraryDocRunnable;
import ext.dataMove.util.ArgsInfo;
import ext.dataMove.util.ExportConstants;

public class ExportPartDocDescription implements RemoteAccess{
	/**
	 * @param rootNumber
	 * @param filePath
	 * @throws Exception
	 */
	public static void process(ArgsInfo argsInfo) throws Exception {
		// 获取修改时间的时间范围
		Timestamp ts1 = null;
		Timestamp ts2 = null;
		if (argsInfo.getBeginTime() != null && !argsInfo.getBeginTime().equals("")) {
			ts1 = ExportUtil.parseTimestamp(argsInfo.getBeginTime() + "  00:00:00");
		}
		if (argsInfo.getEndTime() != null && !argsInfo.getEndTime().equals("")) {
			ts2 = ExportUtil.parseTimestamp(argsInfo.getEndTime() + "  23:59:59");
		}
		
		String productType = argsInfo.getProductType();
		
		List containerList = new ArrayList();
		// 未传入产品库名称，则获取所有的产品库
		if (argsInfo.getCabinet() == null || argsInfo.getCabinet().length() == 0) {
			if("WTLibrary".equals(productType)){
				containerList = ExportUtil.getAllWTLibrary();
			}else{
				containerList = ExportUtil.getAllCabinets();
			}
			
		} else {
			// 获取指定的产品库
			containerList.add(argsInfo.getCabinet());
		}
		try {
			SessionHelper.manager.setAdministrator();
			processThread(containerList, 0, ts1, ts2,productType);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void processThread(List containerList, int start, Timestamp ts1, Timestamp ts2,String productType) {
		try {
			int num = start + ExportConstants.THREAD_COUNT;
			if (num > containerList.size()) {
				num = containerList.size();
			}
			System.out.println("开始执行第" + start + "至" + num + " 个产品库。");
			// log.append("********开始执行第" + START + "至" + num + "
			// 个项目代号**********\r\n");
			List threadList = new ArrayList();
			for (int i = start; i < num; i++) {
				String containerName = (String) containerList.get(i);
				// StringBuffer log = new StringBuffer();
				// log.append("********开始执行第" + (i+1) + "/" +
				// containerList.size() + "
				// 个产品:"+containerName+"**********\r\n");
				// ExportUtil.writeTxt(CSV_FILE_PATH +"Part_log.log",
				// log.toString());
				ExportPartDocRunnable edr = new ExportPartDocRunnable();
				edr.setBeginTime(ts1);
				edr.setEndTime(ts2);
				edr.setContainerName(containerName);
				edr.setProductType(productType);
				SessionThread st = new SessionThread(edr, new SessionContext());
				st.start();
				threadList.add(st);
			}
			for (int i = 0; i < threadList.size(); i++) {
				SessionThread st = (SessionThread) threadList.get(i);
				st.join();
			}
			if (num < containerList.size()) {
				start = num;
				processThread(containerList, start, ts1, ts2,productType);
			} else {
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
