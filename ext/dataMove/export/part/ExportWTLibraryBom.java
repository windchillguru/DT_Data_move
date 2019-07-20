package ext.dataMove.export.part;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import wt.method.RemoteAccess;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.session.SessionThread;
import ext.dataMove.export.util.ExportUtil;
import ext.dataMove.runnable.ExportBomRunnable;
import ext.dataMove.runnable.ExportLibraryBomRunnable;
import ext.dataMove.util.ArgsInfo;
import ext.dataMove.util.ExportConstants;

public class ExportWTLibraryBom implements RemoteAccess{
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
		List containerList = new ArrayList();
		// 未传入产品库名称，则获取所有的存储库
		if (argsInfo.getCabinet() == null || argsInfo.getCabinet().length() == 0) {
			containerList = ExportUtil.getAllWTLibrary();
		} else {
			// 获取指定的存储库
			containerList.add(argsInfo.getCabinet());
		}
		try {
			SessionHelper.manager.setAdministrator();
			processThread(containerList, 0, ts1, ts2);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void processThread(List containerList, int start, Timestamp ts1, Timestamp ts2) {
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
				ExportLibraryBomRunnable edr = new ExportLibraryBomRunnable();
				edr.setBeginTime(ts1);
				edr.setEndTime(ts2);
				edr.setContainerName(containerName);
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
				processThread(containerList, start, ts1, ts2);
			} else {
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
