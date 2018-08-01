package aefCMS.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.WebApps;

import biz.opengate.zkComponents.draggableTree.DraggableTreeElement;
import biz.opengate.zkComponents.draggableTree.DraggableTreeModel;

public class IndexVM {
	
	//PATHS
	
	private final String CONTEXT_PATH 	   = WebApps.getCurrent().getServletContext().getRealPath("/");	//TODO change this (v. github issues)
	private final String REL_LIBRARY_PATH  = "WEB-INF/cms_library";
	private final String ABS_LIBRARY_PATH  = CONTEXT_PATH + REL_LIBRARY_PATH;
	private final String POPUPS_PATH	   = "/WEB-INF/popups/" + "popup_";		//ex.  add -> /WEB-INF/popups/popup_add.zul
	
	//ATTRIBUTES
	
	private Library lib;
	private HtmlRenderer iframeRenderer;
	
	private List<String> libraryElementList;
	
	private PageTree model;
	
	private DraggableTreeModel draggableTreeModel;
	private DraggableTreeElement draggableSelectedElement;
	
	private String selectedPopupType;
	private String selectedLibraryElement;
	
	//GETTERS SETTERS
	
	public List<String> getLibraryElementList() {		
		if (libraryElementList == null) {
			libraryElementList = new ArrayList<String>();
			for(LibraryElement libEl : lib.getElements())
				libraryElementList.add(libEl.getName());
			libraryElementList.sort(null);	
		}
		return libraryElementList;
	}
	
	//TODO CHANGE
	public DraggableTreeModel getDraggableTreeModel() {
		PageElement root = model.getRoot();
		DraggableTreeElement draggableTreeRoot = new DraggableTreeElement(null, root.getType().getName());
		if (root.getChildren().size() > 0) {
			for (PageElement child : root.getChildren()) {
				createDraggableTreeElement(child, draggableTreeRoot);
			}
		}
		draggableTreeRoot.recomputeSpacersRecursive();
		return new DraggableTreeModel(draggableTreeRoot);
	}
	
	public DraggableTreeElement getDraggableSelectedElement() {
		return draggableSelectedElement;
	}

	public void setDraggableSelectedElement(DraggableTreeElement draggableSelectedElement) {
		this.draggableSelectedElement = draggableSelectedElement;
	}
	
	public String getSelectedPopupPath() {
		String path = null;
		if (selectedPopupType != null)
			path = POPUPS_PATH + selectedPopupType + ".zul";	
		return path;
	}
	
	public String getSelectedLibraryElement() {
		return selectedLibraryElement;
	}
	
	@NotifyChange("selectedLibraryElementZul")
	public void setSelectedLibraryElement(String selectedLibraryElement) {
		this.selectedLibraryElement = selectedLibraryElement;
	}
	
	public String getSelectedLibraryElementZul() {
		String path = null;
		if (selectedLibraryElement != null)
			path = REL_LIBRARY_PATH + "/" + selectedLibraryElement + "/" + "mask.zul";
		return path;
	}
	
	//INITIALIZATION

	@Init
	@NotifyChange("draggableTreeModel")
	public void init() throws IOException, Exception {
		
		lib = new Library(new File(ABS_LIBRARY_PATH));
		
		iframeRenderer = new HtmlRenderer(ABS_LIBRARY_PATH);
		
		//TODO DRAFT
		//init pageTree
		Map<String, String> stdPageAttributes = new HashMap<String, String>();
		stdPageAttributes.put("title", "My Web Page");
		PageElement stdPage = new PageElement(lib.getElement("stdPage"), stdPageAttributes);
		model = new PageTree(stdPage);
		
	}
	
	//POPUPS
	
	@Command
	@NotifyChange("selectedPopupPath")
	public void openPopup(@BindingParam("popupType") String popupType) {
		selectedPopupType = popupType;
	}
	
	@Command
	@NotifyChange("selectedPopupPath")
	public void closePopup() {
		selectedPopupType = null;
	}
	
	//UTILITIES
	
	//TODO CHANGE
	private void createDraggableTreeElement(PageElement node, DraggableTreeElement parent) {
		DraggableTreeElement draggableTreeNode = new DraggableTreeElement(parent, node.getType().getName());
		if (node.getChildren().size() > 0) {
			for (PageElement child : node.getChildren()) {
				createDraggableTreeElement(child, draggableTreeNode);
			}
		}
	}
}
	
	
	
	
	/*****************************************************************************************/
	
	
//	private DraggableTreeCmsElement root;
//	private DraggableTreeModel model;
//	private DraggableTreeCmsElement selectedElement;
//	
//	private List<String> fragmentTypeList;
//	private String selectedFragmentType;
//	
//	private String selectedPopup;
//	private String popupType;
//
//	private PageManipulator pageManip;
//	private FragmentGenerator fragmentGen;
//	
//	
//	/////GETTERS SETTERS/////
//	
//	public DraggableTreeCmsElement getRoot() {
//		if (root == null) {
//			Map<String, String> rootDataMap = new HashMap<String, String>();
//			rootDataMap.put("id", "generated-page-root");
//			root = new DraggableTreeCmsElement(null, rootDataMap);
//		}
//		return root;
//	}
//	
//	public DraggableTreeModel getModel() {
//		
//		if (model == null) {
//			model = new DraggableTreeModel(getRoot());
//		}
//		return model;
//	}
//	
//	public void setSelectedElement(DraggableTreeCmsElement selectedElement) {
//		this.selectedElement = selectedElement;
//	}
//	
//	public DraggableTreeCmsElement getSelectedElement() {
//		return selectedElement;
//	}
//
//	
//	public List<String>  getFragmentTypeList() {
//		if (fragmentTypeList == null) {
//			fragmentTypeList = new ArrayList<String>();
//			for (FragmentType i : FragmentType.values()) {
//				fragmentTypeList.add(i.toString());
//			}
//		}
//		return fragmentTypeList;
//	}
//	
//	public String getSelectedFragmentType() {
//		return selectedFragmentType;
//	}
//	
//	@NotifyChange("selectedFragmentTypeZul")
//	public void setSelectedFragmentType(String selectedFragmentType) {
//		this.selectedFragmentType = selectedFragmentType;
//	}
//	
//	//NOTE: zul templates must be in /WEB-INF/zul_templates dir and their name must be *all* lowercase
//	//TODO: manage to remove the "all lowercase" restriction for zul template files
//	public String getSelectedFragmentTypeZul() { 	//NOTE chosen type -> get the zul file (different from below)
//		if (selectedFragmentType == null) {
//			return null;
//		}
//	
//		return "/WEB-INF/zul_templates/" + selectedFragmentType.toLowerCase() + ".zul";
//	}
//	
//	public String getSelectedElementTypeZul() {		//NOTE chosen element -> get the type -> get the zul file (different from above)
//		return "/WEB-INF/zul_templates/" + selectedElement.getElementDataMap().get("fragmentType").toLowerCase() + ".zul";
//	}
//	
//	
//	public String getSelectedPopup() {
//		return selectedPopup;
//	}
//
//	public void setSelectedPopup(String selectedPopup) {
//		this.selectedPopup = selectedPopup;
//	}
//	
//	public String getPopupType() {
//		return popupType;
//	}
//
//	public void setPopupType(String popupType) {
//		this.popupType = popupType;
//	}
//	
//	
//	/////STARTUP/////
//	
//	@AfterCompose
//	public void startup() throws Exception {
//	    
//	    //initialize PageManipulator
//		String mainPagePath = WebApps.getCurrent().getServletContext().getRealPath("generated-page.html");	//uses ZK to resolve the path to the mainpage
//																											//NOTE the main page file must be inside the root dir of the webapp
//		/* DEBUG 
//		 * System.out.println("**DEBUG** ZK returned mainPagePath: " + mainPagePath); 
//		 */
//		pageManip = new PageManipulator(new File(mainPagePath), true, true);	//the last arg is to activate debug output
//	
//	    //initialize FragmentGenerator
//		String templatesFolderName = "velocity_templates";	//NOTE the folder for the templates must be inside WEB-INF
//		fragmentGen = new FragmentGenerator(templatesFolderName);
//	    
//	}
//	
//	
//	/////POPUP WINDOWS/////
//	
//	@Command
//	@NotifyChange({"selectedPopup","popupType"})
//	public void openPopup(@BindingParam("popupType") String popupType) {
//		selectedPopup = "/WEB-INF/" + "popup_" + popupType + ".zul";	//ex.  add -> /WEB-INF/popup_add.zul
//		this.popupType = popupType;
//	}
//	
//	@Command
//	@NotifyChange({"selectedPopup", "popupType"})
//	public void closePopup() {
//		selectedPopup = null;
//		popupType = null;
//		forceIframeRefresh();	//TODO delete this from here?
//	}
//		
////	create new component and attach it to DOM
////	Executions.createComponents("/WEB-INF/zul_templates/title.zul", null,null);		
//
//	/////TREE OPERATIONS/////
//	
//	@GlobalCommand
//	@NotifyChange({"model","selectedFragmentType","selectedPopup", "popupType"})
//	public void addElementGlobal(@BindingParam("pipeHashMap") Map<String, String> pipeHashMap) throws Exception {
//		System.out.println("**DEBUG** addElementGlobal received pipeHashMap: " + pipeHashMap);
//		
//		/*create new node and save it into draggableTree*/
//		DraggableTreeCmsElement newDraggableTreeCmsElement =  new DraggableTreeCmsElement(selectedElement, pipeHashMap);	//NOTE the pipeHashMap is COPIED into the new element
//		Map<String, String> newElDataMap = newDraggableTreeCmsElement.getElementDataMap();	//just saved for later brevity
//		newElDataMap.put("parentId", selectedElement.getElementDataMap().get("id"));
//		newElDataMap.put("siblingsPosition", ( (Integer)selectedElement.getChilds().indexOf(newDraggableTreeCmsElement) ).toString());	//can't call .toString on primitive type int, so I use Integer
//	
//		/*create new DOM node and write it to output page*/
//		//generate fragment code with Velocity
//		String newFragmentHtml = fragmentGen.generateFragmentHtml(FragmentType.valueOf(newElDataMap.get("fragmentType")), newElDataMap);
//		//rebuild the output page with the new fragment
//		pageManip.addFragment(newFragmentHtml, newElDataMap.get("parentId"), Integer.parseInt(newElDataMap.get("siblingsPosition")));																
//		
//		forceIframeRefresh();
//		closePopup();
//		selectedFragmentType = null;	//reset to show empty values to the next add
//	}
//	
//	@Command
//	@NotifyChange({"model","selectedPopup","popupType"})
//	public void removeElement() throws Exception {
//		//remove from output page
//		pageManip.removeFragment(selectedElement.getElementDataMap().get("id"));
//		forceIframeRefresh();
//		
//		//remove from draggableTree
//		DraggableTreeComponent.removeFromParent(selectedElement);
//		root.recomputeSpacersRecursive();
//		
//		closePopup();
//	}
//	
//	@GlobalCommand
//	@NotifyChange({"model","selectedPopup","popupType"})
//	public void modifyElementGlobal(@BindingParam("pipeHashMap") Map<String, String> pipeHashMap) throws Exception {
//		System.out.println("**DEBUG** modifyElementGlobal received pipeHashMap: " + pipeHashMap);
//		
//		String oldId = selectedElement.getDescription();	//NOTE the description field of DraggableTreeElement is the one shown in the tree, so we previously set it to equal the id
//		
//		//update draggableTree
//		selectedElement.setDescription(pipeHashMap.get("id"));	//we now set the description with the *updated* id
//		selectedElement.setElementDataMap(pipeHashMap);
//		
//		//update DOM e html page
//		String newFragmentHtml = fragmentGen.generateFragmentHtml(FragmentType.valueOf(pipeHashMap.get("fragmentType")), pipeHashMap);
//		pageManip.updateFragment(newFragmentHtml, oldId);
//
//		forceIframeRefresh();
//		closePopup();
//	}
//	
//	//TODO ***************  moveElement()  ************************
//	
//	
//	/////UTILITIES/////
//	private void forceIframeRefresh() {		
//		//force iframe refresh (using client-side js)
//		Clients.evalJavaScript("document.getElementsByTagName(\"iframe\")[0].contentWindow.location.reload(true);");	//see: https://stackoverflow.com/questions/13477451/can-i-force-a-hard-refresh-on-an-iframe-with-javascript?lq=1
//		System.out.println("**DEBUG** Forced Iframe refresh.");
//	}
//	
//}




///////////////////////////////////////////////////////////////////////////////////////////////////////////
//////COMMANDS		
//@Command
//@NotifyChange("*")
//public void deleteNode(){
//DraggableTreeComponent.removeFromParent(selectedElement);
//root.recomputeSpacersRecursive();
//}

//@NotifyChange("*")
//public void addComponent() throws Exception{
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////// CHECK DATA
//String errString=null;

//System.out.println( fragmentMap.get(selectedFragment));
//System.out.println( fragmentMap.get(selectedFragment).toString());

//errString=checkFields(idList, selectedFragment, attributeDataMap, fragmentMap.get(selectedFragment));
//if ((errString.equals(""))==true) {
//
//Clients.showNotification("Tutto OK");	
//
////attributeDataMap=generateFragment();
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////// INITIALIZING COMPONENT ELEMENTS WITH MAIN PAGE ELEMENTS
////componentIdList=mainPageIdList;
////componentSelectedElement = mainPageselectedElement;
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////// NODE ADDING
////new DraggableTreeCmsElement(componentSelectedElement, fragmentId, selectedFragment, attributeDataMap);
////componentIdList.add(fragmentId);
////Map<String, Object> args = new HashMap<String, Object>();
////args.put("selectedElement", componentSelectedElement);
////args.put("idList", componentIdList);
////BindUtils.postGlobalCommand(null, null, "reloadMainPageTree", args);
////// RESET WINDOW SELECTIONS OR CONTENT
////resetPopUpSelectionAndBack();
//}else {
//addPopupVisibility=true;
//Clients.showNotification(errString);	
//}
//}

//@Command
//@NotifyChange("attributeDataMap")
//public void resetHashMap() {
//for(String currentKey:attributeDataMap.values()) {
//attributeDataMap.put(currentKey, "");
//}


//}

//@Command
//@NotifyChange("*")
//public void resetPopUpSelectionAndBack() {
//resetHashMap();
//addPopupVisibility=false;
//}
