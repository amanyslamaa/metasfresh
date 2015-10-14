package org.adempiere.ad.expression.api;

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


import java.util.List;

/**
 * String Expression
 * 
 * @author tsa
 * 
 */
public interface IStringExpression extends IExpression<String>
{
	/**
	 * Null String Expression Object
	 */
	IStringExpression NULL = NullStringExpression.instance;

	/**
	 * Gets internal string expression chunks. Don't use it directly, the API will use it only
	 * 
	 * @return
	 */
	List<Object> getExpressionChunks();

	@Override
	IExpressionEvaluator<IStringExpression, String> getEvaluator();
}
