package org.adempiere.ad.service;

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


import java.util.Properties;

import org.adempiere.util.ISingletonService;
import org.compiere.model.I_AD_Message;

public interface IADMessageDAO extends ISingletonService
{

	/**
	 * 
	 * @param ctx
	 * @param value
	 * @return {@link I_AD_Message} or <code>null</code> if not found
	 */
	I_AD_Message retrieveByValue(Properties ctx, String value);

	I_AD_Message retrieveById(Properties ctx, int adMessageId);

	/**
	 * Gets AD_Message_ID
	 * 
	 * @param ctx
	 * @param value AD_Message.Value
	 * @return AD_Message_ID
	 */
	int retrieveIdByValue(Properties ctx, String value);

}
