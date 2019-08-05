import { BPartner } from '../../support/utils/bpartner';
import { DiscountSchema } from '../../support/utils/discountschema';
import { Builder } from '../../support/utils/builder';
import { getLanguageSpecific, humanReadableNow } from '../../support/utils/utils';
import { DocumentActionKey, DocumentStatusKey } from '../../support/utils/constants';
import { SalesOrder, SalesOrderLine } from '../../support/utils/sales_order';

describe('Create Sales order', function() {
  const date = humanReadableNow();
  const customer = `CustomerTest ${date}`;
  const productName = `ProductTest ${date}`;
  const productValue = `sales_order_test ${date}`;
  const productCategoryName = `ProductCategoryName ${date}`;
  const productCategoryValue = `ProductCategoryValue ${date}`;
  const discountSchemaName = `DiscountSchemaTest ${date}`;
  const priceSystemName = `PriceSystem ${date}`;
  const priceListName = `PriceList ${date}`;
  const priceListVersionName = `PriceListVersion ${date}`;
  const productType = 'Item';

  before(function() {
    Builder.createBasicPriceEntities(priceSystemName, priceListVersionName, priceListName, true);
    Builder.createBasicProductEntities(
      productCategoryName,
      productCategoryValue,
      priceListName,
      productName,
      productValue,
      productType
    );
    cy.fixture('discount/discountschema.json').then(discountSchemaJson => {
      Object.assign(new DiscountSchema(), discountSchemaJson)
        .setName(discountSchemaName)
        .apply();
    });
    cy.fixture('sales/simple_customer.json').then(customerJson => {
      const bpartner = new BPartner({ ...customerJson, name: customer })
        .setCustomerDiscountSchema(discountSchemaName)
        .setBank(undefined);

      bpartner.apply();
    });

    cy.readAllNotifications();
  });
  it('Create a sales order', function() {
    cy.fixture('misc/misc_dictionary.json').then(miscDictionary => {
      new SalesOrder()
        .setBPartner(customer)
        .setPriceSystem(priceSystemName)
        .addLine(new SalesOrderLine().setProduct(productName).setQuantity(1))
        .setDocumentAction(getLanguageSpecific(miscDictionary, DocumentActionKey.Complete))
        .setDocumentStatus(getLanguageSpecific(miscDictionary, DocumentStatusKey.Completed))
        .apply();
    });
    /** Go to Shipment disposition*/
    cy.openReferencedDocuments('M_ShipmentSchedule');
    cy.selectNthRow(0).dblclick();
    /**Generate shipments */
    // cy.executeQuickAction('M_ShipmentSchedule_EnqueueSelection');
    cy.executeHeaderAction('M_ShipmentSchedule_EnqueueSelection');
    cy.pressStartButton();
    /**Wait for the shipment schedule process to complete */
    cy.waitUntilProcessIsFinished();
    /**Open notifications */
    cy.get('.header-item-badge.icon-lg i', { timeout: 10000 }).click();
    cy.get('.inbox-item-unread .inbox-item-title')
      .filter(':contains("' + customer + '")')
      .first()
      .click();
    cy.waitUntilProcessIsFinished();
    /**Billing - Invoice disposition */
    cy.openReferencedDocuments('C_Invoice_Candidate');
    cy.selectNthRow(0).click();
    /**Generate invoices on billing candidates */
    cy.executeHeaderAction('C_Invoice_Candidate_EnqueueSelectionForInvoicing');
    cy.pressStartButton();
    /**Open notifications */
    cy.get('.header-item-badge.icon-lg i', { timeout: 10000 }).click();
    cy.get('.inbox-item-unread .inbox-item-title')
      .filter(':contains("' + customer + '")')
      .first()
      .click();
  });
});
