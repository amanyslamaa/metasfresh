package de.metas.contracts.commission.commissioninstance.services;

import java.util.Optional;

import de.metas.contracts.commission.Beneficiary;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.util.TimeUtil;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableList;

import de.metas.bpartner.BPartnerId;
import de.metas.contracts.commission.commissioninstance.businesslogic.CommissionConfig;
import de.metas.contracts.commission.commissioninstance.businesslogic.CreateInstanceRequest;
import de.metas.contracts.commission.commissioninstance.businesslogic.hierarchy.Hierarchy;
import de.metas.contracts.commission.commissioninstance.businesslogic.sales.CommissionTrigger;
import de.metas.contracts.commission.commissioninstance.services.CommissionConfigFactory.ConfigRequestForNewInstance;
import de.metas.invoicecandidate.InvoiceCandidateId;
import de.metas.invoicecandidate.api.IInvoiceCandDAO;
import de.metas.invoicecandidate.model.I_C_Invoice_Candidate;
import de.metas.product.ProductId;
import de.metas.util.Services;
import lombok.NonNull;

/*
 * #%L
 * de.metas.contracts
 * %%
 * Copyright (C) 2019 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

@Service
public class CommissionInstanceRequestFactory
{
	private final CommissionConfigFactory commissionContractFactory;
	private final CommissionHierarchyFactory commissionHierarchyFactory;
	private final CommissionTriggerFactory commissionTriggerFactory;

	private final IInvoiceCandDAO invoiceCandDAO = Services.get(IInvoiceCandDAO.class);

	public CommissionInstanceRequestFactory(
			@NonNull final CommissionConfigFactory commissionContractFactory,
			@NonNull final CommissionHierarchyFactory commissionHierarchyFactory,
			@NonNull final CommissionTriggerFactory commissionTriggerFactory)
	{
		this.commissionContractFactory = commissionContractFactory;
		this.commissionHierarchyFactory = commissionHierarchyFactory;
		this.commissionTriggerFactory = commissionTriggerFactory;
	}

	/** note: if the given IC is a "commission-product-IC" or a purchase-IC, then there won't be requests because these IC's don't have a sales rep */
	public ImmutableList<CreateInstanceRequest> createRequestsForNewSalesInvoiceCandidate(@NonNull final InvoiceCandidateId invoiceCandidateId)
	{
		final I_C_Invoice_Candidate icRecord = invoiceCandDAO.getById(invoiceCandidateId);
		return createRequestFor(icRecord);
	}

	private ImmutableList<CreateInstanceRequest> createRequestFor(@NonNull final I_C_Invoice_Candidate icRecord)
	{
		final BPartnerId salesRepBPartnerId = BPartnerId.ofRepoIdOrNull(icRecord.getC_BPartner_SalesRep_ID());
		if (salesRepBPartnerId == null)
		{
			return ImmutableList.of();
		}

		final Hierarchy hierarchy = commissionHierarchyFactory.createFor(salesRepBPartnerId);

		final ConfigRequestForNewInstance contractRequest = ConfigRequestForNewInstance.builder()
				.commissionHierarchy(hierarchy)
				.customerBPartnerId(BPartnerId.ofRepoId(icRecord.getBill_BPartner_ID()))
				.salesRepBPartnerId(salesRepBPartnerId)
				.date(TimeUtil.asLocalDate(icRecord.getDateOrdered()))
				.salesProductId(ProductId.ofRepoId(icRecord.getM_Product_ID()))
				.build();
		final ImmutableList<CommissionConfig> configs = commissionContractFactory.createForNewCommissionInstances(contractRequest);
		if (configs.isEmpty())
		{
			return ImmutableList.of();
		}

		final Optional<CommissionTrigger> trigger = commissionTriggerFactory.createForNewSalesInvoiceCandidate(InvoiceCandidateId.ofRepoId(icRecord.getC_Invoice_Candidate_ID()));
		if (!trigger.isPresent())
		{
			return ImmutableList.of();
		}

		final ImmutableList.Builder<CreateInstanceRequest> result = ImmutableList.builder();
		for (final CommissionConfig config : configs)
		{
			result.add(createRequest(hierarchy, config, trigger.get()));
		}
		return result.build();
	}

	private CreateInstanceRequest createRequest(
			@NonNull final Hierarchy hierarchy,
			@NonNull final CommissionConfig config,
			@NonNull final CommissionTrigger trigger)
	{
		final CreateInstanceRequest request = CreateInstanceRequest.builder()
				.config(config)
				.hierarchy(hierarchy)
				.trigger(trigger)
				.build();
		return request;
	}

	public Optional<CreateInstanceRequest> createRequestFor(final CreateForecastCommissionInstanceRequest retrieveForecastCommissionPointsRequest)
	{
		final Hierarchy hierarchy = commissionHierarchyFactory.createFor(retrieveForecastCommissionPointsRequest.getSalesRepId());

		final ConfigRequestForNewInstance contractRequest = ConfigRequestForNewInstance.builder()
				.customerBPartnerId( retrieveForecastCommissionPointsRequest.getCustomerId() )
				.salesRepBPartnerId( retrieveForecastCommissionPointsRequest.getSalesRepId() )
				.date( retrieveForecastCommissionPointsRequest.getDateOrdered() )
				.salesProductId( retrieveForecastCommissionPointsRequest.getProductId() )
				.commissionHierarchy(hierarchy)
				.build();

		final ImmutableList<CommissionConfig> configs =
				commissionContractFactory.createForNewCommissionInstances(contractRequest)
					.stream()
				    .filter(config -> config.getContractFor( Beneficiary.of( contractRequest.getSalesRepBPartnerId() ) ) != null)
				    .collect( ImmutableList.toImmutableList() );

		if (configs.size() > 1)
		{
			throw new AdempiereException("Expecting only one active commissionConfig for a sales rep at a certain time!")
					.appendParametersToMessage()
					.setParameter("salesRepBPartnerId", contractRequest.getSalesRepBPartnerId())
					.setParameter("contractRequest", contractRequest)
					.setParameter("hierarchy", hierarchy)
					.setParameter("commissionConfigs", configs);
		}

		if ( configs.isEmpty() )
		{
			return Optional.empty();
		}

		final CommissionTrigger commissionTrigger = commissionTriggerFactory
				.createForForecastQtyAndPrice( retrieveForecastCommissionPointsRequest.getProductPrice(),
											   retrieveForecastCommissionPointsRequest.getForecastQty(),
											   retrieveForecastCommissionPointsRequest.getSalesRepId(),
											   retrieveForecastCommissionPointsRequest.getCustomerId() );

		return Optional.of( createRequest(hierarchy, configs.get(0), commissionTrigger) );
	}
}
