package ext.dataMove.runnable;

import java.sql.Timestamp;

import ext.dataMove.export.util.ExportECWorker;
import wt.session.SessionHelper;

public class ExportECRunnable implements Runnable {

	private String containerName;
	private Timestamp beginTime;
	private Timestamp endTime;
	private String type;

	/**
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            要设置的 type
	 */
	public void setType(String type) {
		this.type = type;
	}

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
			ExportECWorker worker = new ExportECWorker();
			worker.writeCSV(containerName, beginTime, endTime,type);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
