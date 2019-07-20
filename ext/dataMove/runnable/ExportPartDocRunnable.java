package ext.dataMove.runnable;

import java.sql.Timestamp;

import wt.session.SessionHelper;
import ext.dataMove.export.util.ExportDocWorker;
import ext.dataMove.export.util.ExportPartDesWorker;

public class ExportPartDocRunnable implements Runnable{
	private String containerName;
	private Timestamp beginTime;
	private Timestamp endTime;
	private String productType;

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

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public void run() {
		try {
			SessionHelper.manager.setAdministrator();
			ExportPartDesWorker worker = new ExportPartDesWorker();
			worker.writeCSV(containerName, beginTime, endTime,productType);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
