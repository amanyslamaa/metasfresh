ALTER TABLE C_RfQ_Topic ADD CONSTRAINT RfQInvitationMailText_CRfQTopi FOREIGN KEY (RfQ_Invitation_MailText_ID) REFERENCES R_MailText DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE C_RfQ_Topic ADD CONSTRAINT RfQInvitationPrintFormat_CRfQT FOREIGN KEY (RfQ_Invitation_PrintFormat_ID) REFERENCES AD_PrintFormat DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE C_RfQ_Topic ADD CONSTRAINT RfQInvitationWithoutQtyMailTex FOREIGN KEY (RfQ_InvitationWithoutQty_MailText_ID) REFERENCES R_MailText DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE C_RfQ_Topic ADD CONSTRAINT RfQInvitationWithoutQtyPrintFo FOREIGN KEY (RfQ_InvitationWithoutQty_PrintFormat_ID) REFERENCES AD_PrintFormat DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE C_RfQ_Topic ADD CONSTRAINT RfQLostMailText_CRfQTopic FOREIGN KEY (RfQ_Lost_MailText_ID) REFERENCES R_MailText DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE C_RfQ_Topic ADD CONSTRAINT RfQLostPrintFormat_CRfQTopic FOREIGN KEY (RfQ_Lost_PrintFormat_ID) REFERENCES AD_PrintFormat DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE C_RfQ_Topic ADD CONSTRAINT RfQWinMailText_CRfQTopic FOREIGN KEY (RfQ_Win_MailText_ID) REFERENCES R_MailText DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE C_RfQ_Topic ADD CONSTRAINT RfQWinPrintFormat_CRfQTopic FOREIGN KEY (RfQ_Win_PrintFormat_ID) REFERENCES AD_PrintFormat DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE C_RfQLine ADD CONSTRAINT CUOM_CRfQLine FOREIGN KEY (C_UOM_ID) REFERENCES C_UOM DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE C_RfQResponseLine ADD CONSTRAINT CBPartner_CRfQResponseLine FOREIGN KEY (C_BPartner_ID) REFERENCES C_BPartner DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE C_RfQResponseLine ADD CONSTRAINT CCurrency_CRfQResponseLine FOREIGN KEY (C_Currency_ID) REFERENCES C_Currency DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE C_RfQResponseLine ADD CONSTRAINT CRfQ_CRfQResponseLine FOREIGN KEY (C_RfQ_ID) REFERENCES C_RfQ DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE C_RfQResponseLine ADD CONSTRAINT CUOM_CRfQResponseLine FOREIGN KEY (C_UOM_ID) REFERENCES C_UOM DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE C_RfQResponseLine ADD CONSTRAINT MProduct_CRfQResponseLine FOREIGN KEY (M_Product_ID) REFERENCES M_Product DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE C_RfQResponseLineQty ADD CONSTRAINT CRfQLine_CRfQResponseLineQty FOREIGN KEY (C_RfQLine_ID) REFERENCES C_RfQLine DEFERRABLE INITIALLY DEFERRED;

