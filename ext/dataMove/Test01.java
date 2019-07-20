package ext.dataMove;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;

public class Test01 {
	public static void main(String[] args) {
		String docNumber = "0000000041";
		try {
			WTDocument doc = getWTDocByNumber(docNumber);
			QueryResult qr = getDesParts(doc);
			while(qr.hasMoreElements()){
				WTPart part = (WTPart)qr.nextElement();
				System.out.println("说明 method >>>"+part.getNumber());
			}
			
			QueryResult qrt = getReferencePart(doc);
			while(qrt.hasMoreElements()){
				WTPart part = (WTPart)qr.nextElement();
				System.out.println("参考  method >>>"+part.getNumber());
			}

		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static WTDocument getWTDocByNumber(String number) throws WTException {
		WTDocument doc = null;
		QuerySpec qs = null;
		QueryResult qr = null;
		qs = new QuerySpec(WTDocument.class);
		SearchCondition temp = new SearchCondition(WTDocument.class, wt.doc.WTDocument.NUMBER, SearchCondition.EQUAL,
				number.toUpperCase());
		qs.appendSearchCondition(temp);
		qs.appendAnd();
		SearchCondition latest = VersionControlHelper.getSearchCondition(wt.doc.WTDocument.class, true);
		qs.appendSearchCondition(latest);
		qr = PersistenceHelper.manager.find(qs);
		LatestConfigSpec ls = new LatestConfigSpec();
		QueryResult qrls = ls.process(qr);
		while (qrls.hasMoreElements()) {
			doc = (WTDocument) qrls.nextElement();
			return doc;

		}
		return doc;
	}

	/**
	 * 获取文档所说明的部件
	 * 
	 * @param doc
	 */
	public static QueryResult getDesParts(WTDocument doc) throws WTException {
		LatestConfigSpec lcs = new LatestConfigSpec();
		QueryResult qr = PersistenceHelper.manager.navigate(doc, WTPartDescribeLink.DESCRIBES_ROLE,
				WTPartDescribeLink.class, true);
		qr = lcs.process(qr);
		return qr;
	}

	/**
	 * 获取文档所参考的部件
	 * 
	 * @param doc
	 * @return 2013-4-26 上午11:10:33
	 * @throws WTException
	 */
	public static QueryResult getReferencePart(WTDocument doc) throws WTException {
		QueryResult qr = PersistenceHelper.manager.navigate((WTDocumentMaster) doc.getMaster(), "referencedBy",
				wt.part.WTPartReferenceLink.class);
		return qr;
	}
}
