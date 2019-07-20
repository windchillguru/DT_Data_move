package ext.dataMove.util;

import java.io.File;

public class ExportConstants {

	/**
	 * 产品库文档导出标记
	 */
	public static final String DATATYPE_DOC = "doc";
	/**
	 * 构件导出标记
	 */
	public static final String DATATYPE_PART = "part";
	/**
	 * 用户和组导出标记
	 */
	public static final String DATATYPE_USERGROUP = "userAndGroup";
	/**
	 * 结构导出标记
	 */
	public static final String DATATYPE_BOM = "bom";
	/**
	 * 变更对象导出标记
	 */
	public static final String DATATYPE_EC = "ec";
	/**
	 * 导出产品文件夹目录
	 */
	public static final String DATATYPE_FOLDER = "subFolder";
	/**
	 * 下载实体文件标记
	 */
	public static final String DATATYPE_CONTENT = "content";
	/**
	 * 导出时执行的线程数
	 */
	public static int THREAD_COUNT = 10;
	
	/**
	 * 存储库的part
	 */
	public static final String DATATYPE_LIBRARY_PART="libraryPart";
	
	/**
	 * 存储库的BOM
	 */
	public static final String DATATYPE_LIBRARY_BOM="libraryBom";
	
	/**
	 * 存储库的文档
	 */
	public static final String DATATYPE_LIBRARY_DOC="libraryDoc";
	
	/**
	 * 导出部件与文档的关系
	 */
	public static final String DATATYPE_PART_DESCRIPTION = "descriptionByDoc";
	
	/**
	 * 签名信息分割符
	 */
	public static String SPLIT_SIGNATURE = "##";

	public static String COMMA_REPLACE = "，";

	/**
	 * 导出根路径
	 */
	public static String EXPORT_ROOT_DIR_PATH = "";

	static {
		EXPORT_ROOT_DIR_PATH = "D:" + File.separator + "PDMData";
	}
	

}
