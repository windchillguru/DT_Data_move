package ext.dataMove;

import java.util.ArrayList;
import java.util.List;

import ext.dataMove.util.EPMDocumentUtil23;

public class TestEpm27 {
	public static void main(String[] args) {
		List epmList = new ArrayList();
		List rootList = new ArrayList();
		epmList = EPMDocumentUtil23.getEPMDocumentMasterBy("TD六期基站硬件平台项目");
		System.out.println("epmList size=="+epmList.size());
		//rootList = EPMDocumentUtil12.getEpmRoot(epmList);
		//System.out.println("count size=="+rootList.size());
		/*for(int m=0;m<rootList.size();m++){
			EPMDocumentMaster epm = (EPMDocumentMaster)rootList.get(m);
			//System.out.println("epm 编号>"+epm.getNumber()+"epm 名称>"+epm.getName()+"epm 产品名称>"+epm.getContainerName());
			System.out.println("epm 编号>"+epm.getNumber());
			System.out.println("epm doctype=="+epm.getDocType().toString());
		}*/
		//System.out.println("count size=="+rootList.size());
		
		
	}
}
