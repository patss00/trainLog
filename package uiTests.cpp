package uiTests.views;

import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import uiTests.Base;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.testng.AssertJUnit.*;

public class Vs03Nachricht extends Base {

	//TODO:note: Insertions to test this dialog
	// Test for Message header tab:
	// INSERT INTO "BRIGHTEN"."DELJIT_KOPF" (MSGID, FILEID, DATEINAME, DATEITYP, JOBTYP, SATZART, FREIGABENR, PARTNER, LOGFILENAME, MONTAGELINIE, LFDNR, ERFASSUNGSPUNKT, LIEFERANT, EMPFAENGER, MODELLTYP, ZEITPUNKT, KENNNR, FAHRGESTELLNR, SONDERSPEZ, NACHBESTELLINFO, VORSERIENKENNUNG, TEILEARTGRUPPE, ABRUFGRUPPE, SEQUENZINFO, ABLADESTELLE, DOKUMENTNR, ANSTELLZEIT, ERSTELLT, EMPFANGEN, MLALT, NOTORGA, NOTORGACACHE, NOTORGANACHLIEF, VEHICLETYPE, COLORCODE, COLORDESCRIPTION, TYPEKEY, PRODNR, PTIMEMSEC, FACHNR, MAXFACHNR, STATUS, ANGELEGT) VALUES ('111111', '294', 'Test1234', 'A', 'A', 'A', '97A', 'DLR', 'DLR', '1', '9883', 'M100', '15219827', '28', '3B', TO_DATE('2018-07-24 09:13:00', 'YYYY-MM-DD HH24:MI:SS'), '0114010017', '1', 'sp1', 'info1', '111111', 'AA', 'VDA4916', 'VDA4916', 'VDA4916', '00075', TO_DATE('2018-07-24 09:13:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2018-07-24 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2018-08-01 10:32:38', 'YYYY-MM-DD HH24:MI:SS'), '1', 'N', 'N', 'N', '90763313', '1', 'color1', '1', '1', '600', '2', '11', 'F', TO_DATE('2023-01-09 15:33:21', 'YYYY-MM-DD HH24:MI:SS'))
	// For message position tab:
	// INSERT INTO "BRIGHTEN"."DELJIT_POS" (MSGID, POSITION, SACHNR, TEILEART, MENGE, BEDARFSORT, LAGERPLATZ, TEXT, STATUS, ANGELEGT) VALUES ('111111', '1', 'sn1', 'AA', '5', 'idk', 'loc1', N'text', '0', TO_DATE('2023-01-12 16:45:26', 'YYYY-MM-DD HH24:MI:SS'))
	// INSERT INTO "BRIGHTEN"."SACHNR_STAMM" (MONTAGELINIE, MODELLTYP, SACHNR, TYP, ARTNR, EINBAUTAKT, GROESSE, GROESSENEINHEIT, FAHRZEUGSUMME, SUMMENVARIANTE, BEZEICHNUNG, BEZEICHNUNG2, RACKNR, LIEFNR, GRUPPE, SACHNRLIEF, ABLADESTELLE, UNTERZONE, ZSB, SACHSYNONYM, GEWICHT, CHARGENBEZUG, TSLTYP, FAHRZEUGBEZUG, FZGERFASSUNG, WEERFASSUNG, LWMSSYNC, ABLAGEPOS, STATUS, ANGELEGT, AKTIV, SACHNRLIEF2) VALUES ('1', '3B', 'sn1', '0', '2', '1', '2', '3', '4', '5', N'6', N'7', '1', '1000', 'AA', '1', '1', '2', 'N', '3', '4', '7', '8', '8', '2', '5', '3', '4', '0', TO_DATE('2022-10-27 16:18:06', 'YYYY-MM-DD HH24:MI:SS'), 'J', 'SLIEF')

	// region Vars
	// region Common Vars
	Random r = new Random();
	Vs03 vs03;
	// region Tab Common Data
	final String idCD_IdBox = "vs03-idBox";
	final String idCD_PlusButton = "vs03-plusButton";
	final String idCD_MinButton = "vs03-minButton";
	final String idCD_FirstButton = "vs03-firstButton";
	final String idCD_LastButton = "vs03-lastButton";
	// endregion
	// region Tab Message header
	final String messageHeaderTab = "vs03-kopf";
	boolean tabMessageHeader = true;
	final String idMH_NameBox = "vs03-nameBox";
	final String idMH_FileIDBox = "vs03-fileIDBox";
	final String idMH_LogBox = "vs03-logBox";
	final String idMH_TypBox = "vs03-typBox";
	final String idMH_JobBox = "vs03-jobBox";
	final String idMH_ArtBox = "vs03-artBox";
	final String idMH_FreiBox = "vs03-freiBox";
	final String idMH_PartnerBox = "vs03-partnerBox";
	final String idMH_MlBox = "vs03-mlBox";
	final String idMH_LfdBox = "vs03-lfdBox";
	final String idMH_EpunktBox = "vs03-epunktBox";
	final String idMH_LiefBox = "vs03-liefBox";
	final String idMH_AblBox = "vs03-ablBox";
	final String idMH_DokBox = "vs03-dokBox";
	final String idMH_EmpfangenBox = "vs03-empfangenBox";
	final String idMH_VorBox = "vs03-vorBox";
	final String idMH_TeilBox = "vs03-teilBox";
	final String idMH_FachBox = "vs03-fachBox";
	final String idMH_MaxFachBox = "vs03-maxFachBox";
	final String idMH_EmpfBox = "vs03-empfBox";
	final String idMH_ModellBox = "vs03-modellBox";
	final String idMH_ZeitBox = "vs03-zeitBox";
	final String idMH_KennBox = "vs03-kennBox";
	final String idMH_FahrBox = "vs03-fahrBox";
	final String idMH_SpezBox = "vs03-spezBox";
	final String idMH_GrpBox = "vs03-grpBox";
	final String idMH_AnstellBox = "vs03-anstellBox";
	final String idMH_SeqInfoBox = "vs03-seqInfoBox";
	final String idMH_NoBox = "vs03-noBox";
	final String idMH_NotorgaPufferBox = "vs03-notorgaPufferBox";
	final String idMH_NotorgaNachlieferungBox = "vs03-notorgaNachlieferungBox";
	final String idMH_StatusBox = "vs03-statusBox";
	final String idMH_BearbDauerBox = "vs03-bearbDauerBox";
	final String idMH_ErstelltBox = "vs03-erstelltBox";
	final String idMH_AngelegtBox = "vs03-angelegtBox";
	final String idMH_KennButton = "vs03-kennButton";
	final String idMH_AbrufButton = "vs03-abrufButton";
	final String idMH_DateiButton = "vs03-dateiButton";
	final String idMH_SaveButton = "vs03-saveButton";
	// endregion
	// region Tab Message position
	final String messagePositionTab = "vs03-pos";
	boolean tabMessagePosition = true;
	final String idMP_PosGrid = "vs03-posGrid";
	final String idMP_PosPlusButton = "vs03-posPlusButton";
	final String idMP_PosMinusButton = "vs03-posMinusButton";
	final String idMP_EditButton = "vs03-editButton";
	final String idMP_ShowSachNrButton = "vs03-showSachNrButton";
	List<MessagePosition> messagePositionListAdd = new ArrayList<>();
	List<MessagePosition> messagePositionListConfirm = new ArrayList<>();
	// endregion
	// region Tab Modal Add Position
	final String idMAP_PosBox = "vs03-addPos-posBox";
	final String idMAP_SachBox = "vs03-addPos-sachBox";
	final String idMAP_MengeBox = "vs03-addPos-mengeBox";
	final String idMAP_ArtBox = "vs03-addPos-artBox";
	final String idMAP_OrtBox = "vs03-addPos-ortBox";
	final String idMAP_SaveButton = "vs03-addPos-saveButton";
	final String idMAP_CancelButton = "vs03-addPos-cancelButton";
	// endregion
	// region Tab Reprint
	final String reprintTab = "vs03-nachdruck";
	boolean tabReprint = true;
	List<String> r_ProtocolList = new ArrayList<>();
	final String idR_NachdruckBox = "vs03-nachdruckBox";
	final String idR_ProtoButton = "vs03-protoButton";
	final String idR_FileLabelButton = "vs03-fileLabelButton";
	final String idR_TagLabelButton = "vs03-tagLabelButton";
	//endregion
	//endregion

	@BeforeClass
	void goToVs03Dialog() {
		goToDialogAndAssertTitleWithLogMsg("vs03", "Messages");
	}

	public Vs03Nachricht(){
		r_ProtocolList.add("Reprint main protocol");
		r_ProtocolList.add("Reprint special protocol");
		r_ProtocolList.add("Redo file conversions");
		r_ProtocolList.add("Redo vehicle conversions");
		reset();
	}

	@Test(priority = 1, testName = "Type messageID And Verify Data")
	public void typeMessageIDAndVerifyData() {
		logMsg("(vs03) Testcase #1 starts: Type messageID And Verify Data");
		createCommonData(vs03.getMessageID());
		confirmMethods(vs03);
		logMsg("(vs03) Leaving");
		closeErrorOrNewOrGoHomePresenceByIdCss();
	}

	// region Tab Reprint Tests - all need to be manually reset
	@Test(priority = 2, testName = "Execute reprint")
	public void executeReprint() {
		//TODO:note:the entry status on table PROTOKOLLAUFTRAEGE is set to 0 after clicking. to test more than once, that entry needs to be manually updated to P, for example
		reset();
		logMsg("(vs03) Testcase #2 starts: Execute reprint");
		createCommonData(vs03.getMessageID());
		confirmMessageHeader(vs03.getMessageHeader());
		if(tabReprint) {
			tabR_ClickExecuteButton(vs03.getReprint());
		}
		logMsg("(vs03) Leaving");
		closeErrorOrNewOrGoHomePresenceByIdCss();
	}

	@Test(priority = 3, testName = "Reprint File Label")
	public void reprintFileLabel() {
		//TODO:note:the entry status on table LABELAUFTRAGE needs to be manually updated to J each test
		reset();
		logMsg("(vs03) Testcase #3 starts: Reprint File Label");
		createCommonData(vs03.getMessageID());
		confirmMessageHeader(vs03.getMessageHeader());
		if(tabReprint) {
			tabR_ClickReprintFileLabel();
		}
		logMsg("(vs03) Leaving");
		closeErrorOrNewOrGoHomePresenceByIdCss();
	}

	@Test(priority = 4, testName = "Commodity File Label")
	public void reprintCommodityLabel() {
		//TODO:note:the entry status on table LABELAUFTRAGE needs to be manually updated to J each test
		reset();
		logMsg("(vs03) Testcase #4 starts: Commodity File Label");
		createCommonData(vs03.getMessageID());
		confirmMessageHeader(vs03.getMessageHeader());
		if(tabReprint) {
			tabR_ClickReprintCommodityLabel();
		}
		logMsg("(vs03) Leaving");
		closeErrorOrNewOrGoHomePresenceByIdCss();
	}
	// endregion

	// region Buttons
	@Test(priority = 5, testName = "Tab Message Header - End Buttons")
	public void tabMH_EndButtons() {
		//TODO:note: 'show file content' button can't be tested because we need to have specific files on our machines and on certain directories. 'save as' button can't be tested because it only downloads a file and we can't access it via tests
		reset();
		logMsg("(vs03) Testcase #5 starts: Tab Message Header - End Buttons");
		createCommonData(vs03.getMessageID());
		confirmCommonData(vs03.getMessageID());
		confirmMessageHeader(vs03.getMessageHeader());
		tabMH_ClickResort();
		tabMP_clickShowVehicleID();
		logMsg("(vs03) Leaving");
		closeErrorOrNewOrGoHomePresenceByIdCss();
	}

	@Test(priority = 6, testName = "Tab Message Position - Show part number button")
	public void tabMP_ShowPartNrButton() {
		reset();
		logMsg("(vs03) Testcase #6 starts: Tab Message Position - Show part number button");
		createCommonData(vs03.getMessageID());
		confirmCommonData(vs03.getMessageID());
		confirmMessageHeader(vs03.getMessageHeader());
		confirmMPGrid(vs03.getMessagePositions().getMessagePositionListConfirm(), false);
		tabMP_showPartNr();
		logMsg("(vs03) Leaving");
		closeErrorOrNewOrGoHomePresenceByIdCss();
	}

	@Test(priority = 7, testName = "Tab Message Position - Edit entry button")
	public void tabMP_EditEntryButton() {
		reset();
		logMsg("(vs03) Testcase #7 starts: Tab Message Position - Edit entry button");
		createCommonData(vs03.getMessageID());
		addMPGrid(vs03.getMessagePositions().getMessagePositionListAdd());
		confirmMPGrid(vs03.getMessagePositions().getMessagePositionListAdd(), true);
		vs03.getMessagePositions().getMessagePositionListAdd().get(0).setPartNr(alpha3());
		vs03.getMessagePositions().getMessagePositionListAdd().get(0).setQuantity(numeric3());
		vs03.getMessagePositions().getMessagePositionListAdd().get(0).setPartType(alpha3());
		vs03.getMessagePositions().getMessagePositionListAdd().get(0).setUsageArea(alpha3());
		tabMP_EditButton(vs03.getMessagePositions().getMessagePositionListAdd().get(0));
		confirmMPGrid(vs03.getMessagePositions().getMessagePositionListAdd(), false);
		logMsg("(vs03) Leaving");
		closeErrorOrNewOrGoHomePresenceByIdCss();
	}
	// endregion

	public void reset(){
		messagePositionListConfirm.clear();
		messagePositionListAdd.clear();
		messagePositionListConfirm.add(new MessagePosition("1", "sn1", "5", "AA", "1", "6", "idk"));
		messagePositionListAdd.add(new MessagePosition("", alpha3(), numeric3(), alpha3(), "1", "---", alpha3()));

		vs03 = new Vs03("111111", new MessageHeader("Test1234", "111111", "294", "AA", "DLR", "28", "Call off", "3B", "Standard", "24.07.2018 00:00:00", "Productive record", "0114010017", "97A", "", "DLR", "sp1", "1", "VDA4916", "9883", "24.07.2018 00:00:00", "M100", "VDA4916", "15219827", false, "VDA4916", false, "00075", false, "2", "F", "11", "600", "01.08.2018 00:00:00", "24.07.2018 00:00:00", "09.01.2023 00:00:00"), new MessagePositions(messagePositionListAdd, messagePositionListConfirm), getRandomListValue(r_ProtocolList));
	}

	public void confirmMethods(Vs03 vs03){
		logMsg("(vs03) Confirming item " + vs03.getMessageID());
		confirmCommonData(vs03.getMessageID());
		if(tabMessageHeader)
			confirmMessageHeader(vs03.getMessageHeader());

		if(tabMessagePosition){
			addMPGrid(vs03.getMessagePositions().getMessagePositionListAdd());
			confirmMPGrid(vs03.getMessagePositions().getMessagePositionListAdd(), false);
		}
	}

	public void createCommonData(String messageID){
		logMsg("(vs03) Creating item " + vs03.getMessageID());
		scrollWaitElementByIdPresence(idCD_IdBox);
		fillTextField(messageID, idCD_IdBox);
		waitClickId(messageHeaderTab);
		waitPage(wait);
		assertFalse(getValId(idMH_NameBox).isEmpty());
		waitPage(wait);
	}

	public void confirmCommonData(String messageID){
		logMsg("(vs03) Confirming item " + vs03.getMessageID());
		scrollWaitElementByIdPresence(idCD_IdBox);
		assertEquals(messageID, getValId(idCD_IdBox));
	}

	public void confirmMessageHeader(MessageHeader messageHeader){
		logMsg("(vs03) Confirming message header tab");
		scrollClickId(messageHeaderTab);
		waitPage(wait);
		scrollWaitElementByIdPresence(idMH_NameBox);
		assertEquals(messageHeader.getFileName(), getValId(idMH_NameBox));
		assertEquals(messageHeader.getPreseriesID(), getValId(idMH_VorBox));
		assertEquals(messageHeader.getFileID(), getValId(idMH_FileIDBox));
		assertEquals(messageHeader.getCommodity(), getValId(idMH_TeilBox));
		assertEquals(messageHeader.getLogFileName(), getValId(idMH_LogBox));
		assertEquals(messageHeader.getRecipientNo(), getValId(idMH_EmpfBox));
		assertEquals(messageHeader.getFileType(), getValId(idMH_TypBox));
		assertEquals(messageHeader.getModelType(), getValId(idMH_ModellBox));
		assertEquals(messageHeader.getJobType(), getValId(idMH_JobBox));
		assertEquals(messageHeader.getMessageTime(), getValId(idMH_ZeitBox));
		assertEquals(messageHeader.getRecordType(), getValId(idMH_ArtBox));
		assertEquals(messageHeader.getVehicleID(), getValId(idMH_KennBox));
		assertEquals(messageHeader.getFileVersion(), getValId(idMH_FreiBox));
		//TODO:codeFix? Chassis ID comes empty from the source code query but it's set on the db
		assertEquals(messageHeader.getChassisID(), getValId(idMH_FahrBox));
		assertEquals(messageHeader.getEdiPartner(), getValId(idMH_PartnerBox));
		assertEquals(messageHeader.getSpecialSpec(), getValId(idMH_SpezBox));
		assertEquals(messageHeader.getAssemblyLine(), getValId(idMH_MlBox));
		assertEquals(messageHeader.getCallOffGroup(), getValId(idMH_GrpBox));
		assertEquals(messageHeader.getSequenceNumber(), getValId(idMH_LfdBox));
		assertEquals(messageHeader.getSupplyTime(), getValId(idMH_AnstellBox));
		assertEquals(messageHeader.getControlPoint(), getValId(idMH_EpunktBox));
		assertEquals(messageHeader.getSequenceInfo(), getValId(idMH_SeqInfoBox));
		assertEquals(messageHeader.getSupplier(), getValId(idMH_LiefBox));
		assertEquals(boolYesNo(messageHeader.getEmergencyCallOff()), getValId(idMH_NoBox));
		assertEquals(messageHeader.getUnloadingArea(), getValId(idMH_AblBox));
		assertEquals(boolYesNo(messageHeader.getNotorgaPuffer()), getValId(idMH_NotorgaPufferBox));
		assertEquals(messageHeader.getDocumentNo(), getValId(idMH_DokBox));
		assertEquals(boolYesNo(messageHeader.getNotorgaNachlief()), getValId(idMH_NotorgaNachlieferungBox));
		assertEquals(messageHeader.getTrayNo(), getValId(idMH_FachBox));
		assertEquals(messageHeader.getStatus(), getValId(idMH_StatusBox));
		assertEquals(messageHeader.getMaxTrayNo(), getValId(idMH_MaxFachBox));
		assertEquals(messageHeader.getProcessingTime(), getValId(idMH_BearbDauerBox));
		assertEquals(messageHeader.getReceived(), getValId(idMH_EmpfangenBox));
		assertEquals(messageHeader.getCreated1(), getValId(idMH_ErstelltBox));
		assertEquals(messageHeader.getCreated2(), getValId(idMH_AngelegtBox));
	}

	public void addMPGrid(List<MessagePosition> messagePositionListAdd){
		scrollClickId(messagePositionTab);
		waitPage(wait);

		for(MessagePosition mp : messagePositionListAdd) {
			logMsg("(vs03) Adding items to message position grid: " + convertObjToString(mp));
			scrollClickId(idMP_PosPlusButton);

			scrollWaitElementByIdPresence(idMAP_PosBox);
			messagePositionListAdd.get(messagePositionListAdd.indexOf(mp)).setPos((getValId(idMAP_PosBox)));

			fillTextField(mp.getPartNr(), idMAP_SachBox);
			fillTextField(mp.getQuantity(), idMAP_MengeBox);
			fillTextField(mp.getPartType(), idMAP_ArtBox);
			fillTextField(mp.getUsageArea(), idMAP_OrtBox);

			waitClickId(idMAP_SaveButton);
			waitClickId(idConfirmOk);
			waitPage(wait);
		}
	}
	public void confirmMPGrid(List<MessagePosition> messagePositionListConfirm, boolean click){
		logMsg("(vs03) Confirming items in message position grid: " + messagePositionListConfirm.toString());
		scrollClickId(messagePositionTab);
		scrollWaitElementByIdPresence(idMP_PosGrid);
		List<WebElement> tableRows = getTableRows(idMP_PosGrid);
		logMsg("(vs03) Rows found:%d", tableRows.size());
		boolean found = false;

		for (MessagePosition item : messagePositionListConfirm) {
			logMsg("(vs03) Item: <%s>", convertObjToString(item));
			if (found)
				break;

			for (int i = 0; i < tableRows.size(); i++) {
				WebElement tr = tableRows.get(i);
				logMsg("(vs03) tableRow: <%s>", tr.toString());
				if (tdEquals(tr, 1, item.getPos()) &&
						tdEquals(tr, 2, item.getPartNr()) &&
						tdEquals(tr, 3, item.getQuantity()) &&
						tdEquals(tr, 4, item.getPartType()) &&
						tdEquals(tr, 5, item.getRackNo()) &&
						tdEquals(tr, 6, item.getDescription()) &&
						tdEquals(tr, 7, item.getUsageArea()))
				{
					assertTrue(true);
					found = true;
					if(click){
						clickTr(tr);
					}
					break;
				}
			}

			if (!found) {
				fail("Vs03 " + convertObjToString(item) + " not found");
			}
		}
	}

	public void tabR_ClickReprintFileLabel(){
		logMsg("(vs03) Reprinting file label");
		scrollClickId(reprintTab);
		waitClickId(idR_FileLabelButton);
		if(isBeingDisplayedWithCss(cssErrorNotification)){
			fail("Error reprinting commodity label");
		}
	}

	public void tabR_ClickReprintCommodityLabel(){
		logMsg("(vs03) Reprinting commodity label");
		scrollClickId(reprintTab);
		waitClickId(idR_TagLabelButton);
		if(isBeingDisplayedWithCss(cssErrorNotification)){
			fail("Error reprinting file label");
		}
	}

	public void tabR_ClickExecuteButton(String reprint){
		logMsg("(vs03) Reprinting protocol: " + reprint);
		scrollClickId(reprintTab);
		scrollWaitElementByIdPresence(idR_NachdruckBox);
		selectComboBoxValue(idR_NachdruckBox, reprint);
		waitClickId(idR_ProtoButton);
		if(isBeingDisplayedWithCss(cssErrorNotification)){
			fail("Error reprinting protocol with value " + reprint);
		}
	}

	public void tabMP_clickShowVehicleID(){
		logMsg("(vs03) Clicking 'Show VehicleID' button");
		scrollClickId(messageHeaderTab);
		clickAndConfirmChangeOfDialog("Vehicle data", idMH_KennButton);
		waitPage(wait);
	}

	public void tabMP_showPartNr(){
		logMsg("(vs03) Clicking 'Show part number' button");
		scrollClickId(messagePositionTab);
		clickAndConfirmChangeOfDialog("Parts", idMP_ShowSachNrButton);
		waitPage(wait);
	}

	public void tabMP_EditButton(MessagePosition mp){
		logMsg("(vs03) Clicking 'Edit' button");
		scrollClickId(idMP_EditButton);
		scrollWaitElementByIdPresence(idMAP_PosBox);
		mp.setPos(getValId(idMAP_PosBox));

		fillTextField(mp.getPartNr(), idMAP_SachBox);
		fillTextField(mp.getQuantity(), idMAP_MengeBox);
		fillTextField(mp.getPartType(), idMAP_ArtBox);
		fillTextField(mp.getUsageArea(), idMAP_OrtBox);

		waitClickId(idMAP_SaveButton);
		waitClickId(idConfirmOk);
		waitPage(wait);
	}

	public void tabMH_ClickResort() {
		logMsg("(vs03) Clicking 'Resort' button");
		scrollClickId(messageHeaderTab);
		clickAndConfirmChangeOfDialog("Sort call offs", idMH_AbrufButton);
	}

	class MessagePosition {
		String pos;
		String partNr;
		String quantity;
		String partType;
		String rackNo;
		String description;
		String usageArea;

		public MessagePosition(String pos, String partNr, String quantity, String partType, String rackNo, String description, String usageArea) {
			this.pos = pos;
			this.partNr = partNr;
			this.quantity = quantity;
			this.partType = partType;
			this.rackNo = rackNo;
			this.description = description;
			this.usageArea = usageArea;
		}

		public String getPos() {
			return pos;
		}

		public void setPos(String pos) {
			this.pos = pos;
		}

		public String getPartNr() {
			return partNr;
		}

		public void setPartNr(String partNr) {
			this.partNr = partNr;
		}

		public String getQuantity() {
			return quantity;
		}

		public void setQuantity(String quantity) {
			this.quantity = quantity;
		}

		public String getPartType() {
			return partType;
		}

		public void setPartType(String partType) {
			this.partType = partType;
		}

		public String getRackNo() {
			return rackNo;
		}

		public void setRackNo(String rackNo) {
			this.rackNo = rackNo;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getUsageArea() {
			return usageArea;
		}

		public void setUsageArea(String usageArea) {
			this.usageArea = usageArea;
		}
	}

	public class MessageHeader {
		String fileName;
		String preseriesID;
		String fileID;
		String commodity;
		String logFileName;
		String recipientNo;
		String fileType;
		String modelType;
		String jobType;
		String messageTime;
		String recordType;
		String vehicleID;
		String fileVersion;
		String chassisID;
		String ediPartner;
		String specialSpec;
		String assemblyLine;
		String callOffGroup;
		String sequenceNumber;
		String supplyTime;
		String controlPoint;
		String sequenceInfo;
		String supplier;
		boolean emergencyCallOff;
		String unloadingArea;
		boolean notorgaPuffer;
		String documentNo;
		boolean notorgaNachlief;
		String trayNo;
		String status;
		String maxTrayNo;
		String processingTime;
		String received;
		String created1;
		String created2;

		public MessageHeader(String fileName, String preseriesID, String fileID, String commodity, String logFileName, String recipientNo, String fileType, String modelType, String jobType, String messageTime, String recordType, String vehicleID, String fileVersion, String chassisID, String ediPartner, String specialSpec, String assemblyLine, String callOffGroup, String sequenceNumber, String supplyTime, String controlPoint, String sequenceInfo, String supplier, boolean emergencyCallOff, String unloadingArea, boolean notorgaPuffer, String documentNo, boolean notorgaNachlief, String trayNo, String status, String maxTrayNo, String processingTime, String received, String created1, String created2) {
			this.fileName = fileName;
			this.preseriesID = preseriesID;
			this.fileID = fileID;
			this.commodity = commodity;
			this.logFileName = logFileName;
			this.recipientNo = recipientNo;
			this.fileType = fileType;
			this.modelType = modelType;
			this.jobType = jobType;
			this.messageTime = messageTime;
			this.recordType = recordType;
			this.vehicleID = vehicleID;
			this.fileVersion = fileVersion;
			this.chassisID = chassisID;
			this.ediPartner = ediPartner;
			this.specialSpec = specialSpec;
			this.assemblyLine = assemblyLine;
			this.callOffGroup = callOffGroup;
			this.sequenceNumber = sequenceNumber;
			this.supplyTime = supplyTime;
			this.controlPoint = controlPoint;
			this.sequenceInfo = sequenceInfo;
			this.supplier = supplier;
			this.emergencyCallOff = emergencyCallOff;
			this.unloadingArea = unloadingArea;
			this.notorgaPuffer = notorgaPuffer;
			this.documentNo = documentNo;
			this.notorgaNachlief = notorgaNachlief;
			this.trayNo = trayNo;
			this.status = status;
			this.maxTrayNo = maxTrayNo;
			this.processingTime = processingTime;
			this.received = received;
			this.created1 = created1;
			this.created2 = created2;
		}
		public String getFileName() { return fileName; }

		public void setFileName(String fileName) { this.fileName = fileName; }

		public String getPreseriesID() { return preseriesID; }

		public void setPreseriesID(String preseriesID) { this.preseriesID = preseriesID; }

		public String getFileID() { return fileID; }

		public void setFileID(String fileID) { this.fileID = fileID; }

		public String getCommodity() { return commodity; }

		public void setCommodity(String commodity) { this.commodity = commodity; }

		public String getLogFileName() { return logFileName; }

		public void setLogFileName(String logFileName) { this.logFileName = logFileName; }

		public String getRecipientNo() { return recipientNo; }

		public void setRecipientNo(String recipientNo) { this.recipientNo = recipientNo; }

		public String getFileType() { return fileType; }

		public void setFileType(String fileType) { this.fileType = fileType; }

		public String getModelType() { return modelType; }

		public void setModelType(String modelType) { this.modelType = modelType; }

		public String getJobType() { return jobType; }

		public void setJobType(String jobType) { this.jobType = jobType; }

		public String getMessageTime() { return messageTime; }

		public void setMessageTime(String messageTime) { this.messageTime = messageTime; }

		public String getRecordType() { return recordType; }

		public void setRecordType(String recordType) { this.recordType = recordType; }

		public String getVehicleID() { return vehicleID; }

		public void setVehicleID(String vehicleID) { this.vehicleID = vehicleID; }

		public String getFileVersion() { return fileVersion; }

		public void setFileVersion(String fileVersion) { this.fileVersion = fileVersion; }

		public String getChassisID() { return chassisID; }

		public void setChassisID(String chassisID) { this.chassisID = chassisID; }

		public String getEdiPartner() { return ediPartner; }

		public void setEdiPartner(String ediPartner) { this.ediPartner = ediPartner; }

		public String getSpecialSpec() { return specialSpec; }

		public void setSpecialSpec(String specialSpec) { this.specialSpec = specialSpec; }

		public String getAssemblyLine() { return assemblyLine; }

		public void setAssemblyLine(String assemblyLine) { this.assemblyLine = assemblyLine; }

		public String getCallOffGroup() { return callOffGroup; }

		public void setCallOffGroup(String callOffGroup) { this.callOffGroup = callOffGroup; }

		public String getSequenceNumber() { return sequenceNumber; }

		public void setSequenceNumber(String sequenceNumber) { this.sequenceNumber = sequenceNumber; }

		public String getSupplyTime() { return supplyTime; }

		public void setSupplyTime(String supplyTime) { this.supplyTime = supplyTime; }

		public String getControlPoint() { return controlPoint; }

		public void setControlPoint(String controlPoint) { this.controlPoint = controlPoint; }

		public String getSequenceInfo() { return sequenceInfo; }

		public void setSequenceInfo(String sequenceInfo) { this.sequenceInfo = sequenceInfo; }

		public String getSupplier() { return supplier; }

		public void setSupplier(String supplier) { this.supplier = supplier; }

		public boolean getEmergencyCallOff() { return emergencyCallOff; }

		public void setEmergencyCallOff(boolean emergencyCallOff) { this.emergencyCallOff = emergencyCallOff; }

		public String getUnloadingArea() { return unloadingArea; }

		public void setUnloadingArea(String unloadingArea) { this.unloadingArea = unloadingArea; }

		public boolean getNotorgaPuffer() { return notorgaPuffer; }

		public void setNotorgaPuffer(boolean notorgaPuffer) { this.notorgaPuffer = notorgaPuffer; }

		public String getDocumentNo() { return documentNo; }

		public void setDocumentNo(String documentNo) { this.documentNo = documentNo; }

		public boolean getNotorgaNachlief() { return notorgaNachlief; }

		public void setNotorgaNachlief(boolean notorgaNachlief) { this.notorgaNachlief = notorgaNachlief; }

		public String getTrayNo() { return trayNo; }

		public void setTrayNo(String trayNo) { this.trayNo = trayNo; }

		public String getStatus() { return status; }

		public void setStatus(String status) { this.status = status; }

		public String getMaxTrayNo() { return maxTrayNo; }

		public void setMaxTrayNo(String maxTrayNo) { this.maxTrayNo = maxTrayNo; }

		public String getProcessingTime() { return processingTime; }

		public void setProcessingTime(String processingTime) { this.processingTime = processingTime; }

		public String getReceived() { return received; }

		public void setReceived(String received) { this.received = received; }

		public String getCreated1() { return created1; }

		public void setCreated1(String created1) { this.created1 = created1; }

		public String getCreated2() { return created2; }

		public void setCreated2(String created2) { this.created2 = created2; }

	}

	public class Vs03 {
		String messageID;
		MessageHeader messageHeader;
		MessagePositions messagePositions;
		String reprint;

		public Vs03(String messageID, MessageHeader messageHeader, MessagePositions messagePositions, String reprint) {
			this.messageID = messageID;
			this.messageHeader = messageHeader;
			this.messagePositions = messagePositions;
			this.reprint = reprint;
		}
		public String getMessageID() { return messageID; }

		public void setMessageID(String messageID) { this.messageID = messageID; }

		public MessageHeader getMessageHeader() { return messageHeader; }

		public void setMessageHeader(MessageHeader messageHeader) { this.messageHeader = messageHeader; }

		public MessagePositions getMessagePositions() { return messagePositions; }

		public void setMessagePositions(MessagePositions messagePositions) { this.messagePositions = messagePositions; }

		public String getReprint() { return reprint; }

		public void setReprint(String reprint) { this.reprint = reprint; }

	}

	public class MessagePositions {
		List<MessagePosition> messagePositionListAdd;
		List<MessagePosition> messagePositionListConfirm;

		public MessagePositions(List<MessagePosition> messagePositionListAdd, List<MessagePosition> messagePositionListConfirm) {
			this.messagePositionListAdd = messagePositionListAdd;
			this.messagePositionListConfirm = messagePositionListConfirm;
		}
		public List<MessagePosition> getMessagePositionListAdd() { return messagePositionListAdd; }

		public void setMessagePositionListAdd(List<MessagePosition> messagePositionListAdd) { this.messagePositionListAdd = messagePositionListAdd; }

		public List<MessagePosition> getMessagePositionListConfirm() { return messagePositionListConfirm; }

		public void setMessagePositionListConfirm(List<MessagePosition> messagePositionListConfirm) { this.messagePositionListConfirm = messagePositionListConfirm; }

	}
}
