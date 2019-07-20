package ext.dataMove.runnable;

import java.sql.Timestamp;

import ext.dataMove.export.util.ExportDocWorker;
import wt.session.SessionHelper;

public class ExportDocRunnable implements Runnable{
	private String containerName;
	private Timestamp beginTime;
	private Timestamp endTime;

	/**
	 * @return beginTime
	 */
	public Timestamp getBeginTime() {
		return beginTime;
	}

	/**
	 * @param beginTime
	 *            要设置的 beginTime
	 */
	public void setBeginTime(Timestamp beginTime) {
		this.beginTime = beginTime;
	}

	/**
	 * @return endTime
	 */
	public Timestamp getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime
	 *            要设置的 endTime
	 */
	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return containerName
	 */
	public String getContainerName() {
		return containerName;
	}

	/**
	 * @param containerName
	 *            要设置的 containerName
	 */
	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public void run() {
		try {
			SessionHelper.manager.setAdministrator();
			ExportDocWorker worker = new ExportDocWorker();
			worker.writeCSV(containerName, beginTime, endTime,"PDMLink");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

