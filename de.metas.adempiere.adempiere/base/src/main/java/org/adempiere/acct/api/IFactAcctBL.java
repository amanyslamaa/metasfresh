package org.adempiere.acct.api;

/*
 * #%L
 * de.metas.adempiere.adempiere.base
 * %%
 * Copyright (C) 2015 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */


import java.util.Map;
import java.util.Properties;

import org.adempiere.util.ISingletonService;
import org.compiere.report.core.RColumn;

public interface IFactAcctBL extends ISingletonService
{

	IFactAcctCubeUpdater createFactAcctCubeUpdater();

	/**
	 * Create the {@link RColumn} for displaying the Ending Balance
	 * 
	 * @param ctx
	 * @param columnName2whereClause a map of "column name" to SQL where clauses which are filtering the accounting dimensions
	 * @return
	 */
	RColumn createEndingBalanceRColumn(Properties ctx, final Map<String, String> columnName2whereClause);

}
