package de.metas.banking;

import de.metas.acct.api.AccountId;
import de.metas.acct.api.AcctSchemaId;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/*
 * #%L
 * de.metas.adempiere.adempiere.base
 * %%
 * Copyright (C) 2020 metas GmbH
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

/**
 * Own (organization) bank account - accounting settings
 */
@Value
@Builder
public class BankAccountAcct
{
	@NonNull
	BankAccountId bankAccountId;

	@NonNull
	AcctSchemaId acctSchemaId;

	@NonNull
	AccountId bankAssetAcct;
	@NonNull
	AccountId unallocatedCashAcct;
	@NonNull
	AccountId bankInTransitAcct;
	@NonNull
	AccountId paymentSelectAcct;
	@NonNull
	AccountId interestRevenueAcct;
	@NonNull
	AccountId interestExpenseAcct;
	@NonNull
	AccountId paymentBankFeeAcct;
}
