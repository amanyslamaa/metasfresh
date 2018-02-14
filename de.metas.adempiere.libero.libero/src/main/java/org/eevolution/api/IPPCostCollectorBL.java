package org.eevolution.api;

import org.adempiere.util.ISingletonService;
import org.eevolution.api.impl.ReceiptCostCollectorCandidate.ReceiptCostCollectorCandidateBuilder;
import org.eevolution.model.I_PP_Cost_Collector;
import org.eevolution.model.I_PP_Order;
import org.eevolution.model.I_PP_Order_BOMLine;
import org.eevolution.model.I_PP_Order_Node;
import org.eevolution.model.X_PP_Cost_Collector;
import org.eevolution.model.validator.PP_Order_BOMLine;

public interface IPPCostCollectorBL extends ISingletonService
{
	/**
	 * Creates an plain and empty receipt cost collector candidate.
	 * 
	 * @return receipt cost collector candidate
	 */
	ReceiptCostCollectorCandidateBuilder createReceiptCostCollectorCandidate();

	/**
	 * Create and process material issue cost collector. The given qtys are converted to the UOM of the given <code>orderBOMLine</code>. The Cost collector's type is determined from the given
	 * <code>orderBOMLine</code> alone.
	 * <p>
	 * Note that this method is also used internally to handle the case of a "co-product receipt".
	 * 
	 * @return processed cost collector
	 */
	I_PP_Cost_Collector createIssue(ComponentIssueCreateRequest request);

	/**
	 * Checks if the <code>CostCollectorType</code> of the given <code>cc</code> is about receiving any kind of materials (finished goods or co/by-products) from underlying manufacturing order.
	 * 
	 * More specifically, if the type is <code>MaterialReceipt</code> (=> regular receipts) or <code>MixVariance</code> (=> co-product receipts), then the method returns <code>true</code>.
	 * 
	 * @param cc
	 * @return true if given cost collector is about receiving materials (i.e. regular receipts or co-product receipts)
	 * @see #isMaterialReceipt(I_PP_Cost_Collector, boolean)
	 */
	boolean isMaterialReceipt(I_PP_Cost_Collector cc);

	/**
	 * Checks if the <code>CostCollectorType</code> of the given <code>cc</code> is about receiving any kind of materials (finished goods or co/by-products) from underlying manufacturing order.
	 * 
	 * More specifically, this method returns <code>true</code> if the type is
	 * <ul>
	 * <li><code>MaterialReceipt</code> (=> regular receipts)
	 * <li>or <code>MixVariance</code> (=> by/co-product receipts) and <code>considerCoProductsAsReceipt</code> is <code>true</code>
	 * </ul>
	 * 
	 * @param cc
	 * @param considerCoProductsAsReceipt true if by/co-products "issuing" shall be considered as material receipts
	 * @return true if given cost collector is about receiving materials from manufacturing order
	 */
	boolean isMaterialReceipt(I_PP_Cost_Collector cc, boolean considerCoProductsAsReceipt);

	/**
	 * Checks if given cost collector is about issuing materials.
	 * 
	 * More specifically, this method returns <code>true</code> if the cost collector type is
	 * <ul>
	 * <li>{@link X_PP_Cost_Collector#COSTCOLLECTORTYPE_ComponentIssue} - actual material issues
	 * <li>{@link X_PP_Cost_Collector#COSTCOLLECTORTYPE_MethodChangeVariance} and PP_Order_BOMLine_ID is set - issuing a product which is different from the one from BOM Line
	 * <li>{@link X_PP_Cost_Collector#COSTCOLLECTORTYPE_MixVariance}, PP_Order_BOMLine_ID is set and <code>considerCoProductsAsIssue</code> - negative "issuing" by/co-products, i.e. receiving
	 * by/co-products
	 * </ul>
	 * 
	 * @param cc
	 * @param considerCoProductsAsIssue true if negative by/co-products issuing shall be considered as issue (and not receipt)
	 * @return true if given cost collector is about issuing materials to manufacturing order
	 */
	boolean isMaterialIssue(I_PP_Cost_Collector cc, boolean considerCoProductsAsIssue);

	/**
	 * Checks if given cost collector is about receiving (i.e. negative issue) co/by-products.
	 * 
	 * More specifically, this method returns <code>true</code> if the cost collector type is {@link X_PP_Cost_Collector#COSTCOLLECTORTYPE_MixVariance}.
	 * 
	 * @param cc
	 * @return true if given cost collector is about co/by-products.
	 */
	boolean isCoOrByProductReceipt(I_PP_Cost_Collector cc);

	/**
	 * Checks if given cost collector is about reporting on an manufacturing order activity ({@link I_PP_Order_Node}).
	 * 
	 * @param cc
	 * @return
	 */
	boolean isActivityControl(I_PP_Cost_Collector cc);

	/**
	 * Create Receipt (finished good or co/by-products). If the given <code>candidate</code>'s {@link PP_Order_BOMLine} is not <code>!= null</code>, then a finished product receipt is created.
	 * Otherwise a co-product receipt is created. Note that under the hood a co-product receipt is a "negative issue".
	 * 
	 * @param candidate the candidate to create the receipt from.
	 * 
	 * @return processed cost collector
	 */
	I_PP_Cost_Collector createReceipt(IReceiptCostCollectorCandidate candidate);

	I_PP_Cost_Collector createActivityControl(ActivityControlCreateRequest request);

	void createUsageVariance(I_PP_Order_BOMLine line);

	void createUsageVariance(I_PP_Order_Node activity);

	/**
	 * Checks if given cost collector is a reversal.
	 * 
	 * We consider given cost collector as a reversal if it's ID is bigger then the Reveral_ID.
	 * 
	 * @param cc cost collector
	 * @return true if given cost collector is actually a reversal.
	 */
	boolean isReversal(I_PP_Cost_Collector cc);

	boolean isFloorStock(I_PP_Cost_Collector cc);

	void setPP_Order(I_PP_Cost_Collector cc, I_PP_Order order);

}
