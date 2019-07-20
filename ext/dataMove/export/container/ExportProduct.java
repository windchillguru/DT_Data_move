package ext.dataMove.export.container;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import ext.dataMove.export.util.ExportUtil;
import ext.dataMove.util.ArgsInfo;
import ext.dataMove.util.ExportConstants;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.library.WTLibrary;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.pdmlink.PDMLinkProduct;
import wt.project.Role;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;

public class ExportProduct {
	
	public static String PRODUCT_FILE_PATH = ExportConstants.EXPORT_ROOT_DIR_PATH + File.separator + "product";
	public static String LIBRARY_FILE_PATH = ExportConstants.EXPORT_ROOT_DIR_PATH + File.separator + "library";
	public static String PRODUCT_CSV_FILE_PATH = PRODUCT_FILE_PATH + File.separator + "product.csv";
	public static String LIBRARY_CSV_FILE_PATH = LIBRARY_FILE_PATH + File.separator + "library.csv";
	public static void process(ArgsInfo argsInfo) throws Exception {
		exportProduct(PRODUCT_FILE_PATH);
		exportLibrary(LIBRARY_FILE_PATH);
	}
	/**
	 * 
	 * @param filePath
	 */
	public static void exportProduct(String filePath){
		List productList = new ArrayList();
		productList=ExportUtil.getAllCabinets();
		StringBuffer buff = new StringBuffer();
		for(int i=0;i<productList.size();i++){
			PDMLinkProduct product = new PDMLinkProduct();
			product=getProductByName(productList.get(i).toString());
			try {
				ContainerTeam team = ContainerTeamHelper.service.getContainerTeam(product);
				Vector vc = team.getRoles();
				for(int m=0;m<vc.size();m++){
					Enumeration em = team.getPrincipalTarget((Role) vc.get(m));
					
				}
				
			} catch (WTException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			buff.append(product.getContainer());
			buff.append(",");
			buff.append(product.getContainerName());
			buff.append(",");
			buff.append(product.getCreator());
			buff.append(",");
			buff.append(ExportUtil.parseString(product.getCreateTimestamp()));
			buff.append(",");
			buff.append(product.getContainerTemplate().toString());
			buff.append("\r\n");
			
			try {
				ExportUtil.writeTxt(PRODUCT_CSV_FILE_PATH, buff.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * 
	 */
	public static void exportLibrary(String libraryPath){
		List libraryList = new ArrayList();
		libraryList=getAllLibrary();
		StringBuffer buff = new StringBuffer();
		for(int i=0;i<libraryList.size();i++){
			WTLibrary product = new WTLibrary();
			product=getLibyaryByName(libraryList.get(i).toString());
			
			buff.append(product.getContainer());
			buff.append(",");
			buff.append(product.getContainerName());
			buff.append(",");
			buff.append(product.getCreator());
			buff.append(",");
			buff.append(ExportUtil.parseString(product.getCreateTimestamp()));
			buff.append(",");
			buff.append(product.getContainerTemplate().toString());
			buff.append("\r\n");
			
			try {
				ExportUtil.writeTxt(LIBRARY_CSV_FILE_PATH, buff.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * 获取系统所有的产品（名称）集合
	 * 
	 * @return
	 */
	public static List getAllProduct() {
		List productList = new ArrayList();
		try {
			QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				PDMLinkProduct cabinet = (PDMLinkProduct) qr.nextElement();
				System.out.println("cabinet name==" + cabinet.getName());
				productList.add(cabinet.getName());
			}
		} catch (WTException e) {
			System.out.println("***getAllCabinet异常**" + e.getMessage());
		}
		return productList;
	}
	/**
	 * 根据产品名称获取产品
	 * @param ProdcutName
	 * @return
	 */
	public static PDMLinkProduct getProductByName(String productName){
		try {
			QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
			SearchCondition sc = new SearchCondition(PDMLinkProduct.class,PDMLinkProduct.NAME,"=",productName,false);
			qs.appendWhere(sc,new int[] {0});
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while(qr.hasMoreElements()){
				PDMLinkProduct cabinet = (PDMLinkProduct) qr.nextElement();
				return cabinet;
			}
			
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			System.out.println("获取产品出现异常！");
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			System.out.println("获取产品出现异常！");
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 根据存储库名称获取存储库
	 * @param libraryName
	 * @return
	 */
	public static WTLibrary getLibyaryByName(String libraryName){
		try {
			QuerySpec qs = new QuerySpec(WTLibrary.class);
			SearchCondition sc = new SearchCondition(WTLibrary.class,WTLibrary.NAME,"=",libraryName,false);
			qs.appendWhere(sc,new int[] {0});
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while(qr.hasMoreElements()){
				WTLibrary cabinet = (WTLibrary) qr.nextElement();
				return cabinet;
			}
			
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获取所有的存储库
	 * @return
	 */
	public static List getAllLibrary(){
		List libraryList = new ArrayList();
		try {
			QuerySpec qs = new QuerySpec(WTLibrary.class);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				WTLibrary cabinet = (WTLibrary) qr.nextElement();
				System.out.println("cabinet name==" + cabinet.getName());
				libraryList.add(cabinet.getName());
			}
		} catch (WTException e) {
			System.out.println("***getAllCabinet异常**" + e.getMessage());
		}
		return libraryList;
	}
}
