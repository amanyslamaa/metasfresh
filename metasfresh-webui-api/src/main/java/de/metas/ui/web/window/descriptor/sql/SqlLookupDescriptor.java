package de.metas.ui.web.window.descriptor.sql;

import java.util.Set;

import org.adempiere.ad.expression.api.ICachedStringExpression;
import org.adempiere.ad.expression.api.IStringExpression;
import org.adempiere.ad.expression.api.TranslatableParameterizedStringExpression;
import org.adempiere.ad.expression.api.impl.CompositeStringExpression;
import org.adempiere.ad.security.IUserRolePermissions;
import org.adempiere.ad.security.impl.AccessSqlStringExpression;
import org.adempiere.ad.validationRule.IValidationRule;
import org.adempiere.ad.validationRule.impl.CompositeValidationRule;
import org.adempiere.ad.validationRule.impl.NullValidationRule;
import org.adempiere.util.Check;
import org.compiere.model.I_M_AttributeSetInstance;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MLookupInfo;
import org.compiere.util.CtxName;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Evaluatee;
import org.compiere.util.Evaluatees;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.metas.i18n.TranslatableParameterizedString;
import de.metas.ui.web.window.WindowConstants;
import de.metas.ui.web.window.model.sql.DocActionValidationRule;

/*
 * #%L
 * metasfresh-webui-api
 * %%
 * Copyright (C) 2016 metas GmbH
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

public final class SqlLookupDescriptor
{
	public static final Builder builder()
	{
		return new Builder();
	}

	public static final CtxName SQL_PARAM_FilterSql = CtxName.parse("SqlFilter");
	public static final CtxName SQL_PARAM_ValidationRuleSql = CtxName.parse("SqlValidationRule");
	public static final CtxName SQL_PARAM_Offset = CtxName.parse("SqlOffset/0");
	public static final CtxName SQL_PARAM_Limit = CtxName.parse("SqlLimit/1000");
	public static final CtxName SQL_PARAM_KeyId = CtxName.parse("SqlKeyId");

	public static final String SQL_PARAM_VALUE_ShowInactive_Yes = "Y"; // i.e. show all
	public static final String SQL_PARAM_VALUE_ShowInactive_No = "N";
	public static final CtxName SQL_PARAM_ShowInactive = CtxName.parse("SqlShowInactive/N");

	public static final CtxName SQL_PARAM_AD_Language = CtxName.parse(Env.CTXNAME_AD_Language);

	private static final int WINDOWNO_Dummy = 99999;

	private final boolean numericKey;
	private final Set<String> dependsOnFieldNames;

	private final IValidationRule validationRule;
	private final String sqlTableName;
	private final ICachedStringExpression sqlForFetchingExpression;
	private final ICachedStringExpression sqlForCountingExpression;
	private final ICachedStringExpression sqlForFetchingDisplayNameByIdExpression;
	private final int entityTypeIndex;

	private SqlLookupDescriptor(final Builder builder)
	{
		super();
		numericKey = builder.numericKey;
		dependsOnFieldNames = ImmutableSet.copyOf(builder.dependsOnFieldNames);

		validationRule = builder.validationRule;
		sqlTableName = builder.sqlTableName;
		sqlForFetchingExpression = builder.sqlForFetchingExpression;
		sqlForCountingExpression = builder.sqlForCountingExpression;
		sqlForFetchingDisplayNameByIdExpression = builder.sqlForFetchingDisplayNameByIdExpression;
		entityTypeIndex = builder.entityTypeIndex;
	}

	public boolean isNumericKey()
	{
		return numericKey;
	}

	public String getSqlTableName()
	{
		return sqlTableName;
	}

	public IStringExpression getSqlForFetchingExpression()
	{
		return sqlForFetchingExpression;
	}

	public IStringExpression getSqlForCountingExpression()
	{
		return sqlForCountingExpression;
	}

	public IStringExpression getSqlForFetchingDisplayNameByIdExpression()
	{
		return sqlForFetchingDisplayNameByIdExpression;
	}

	public IStringExpression getSqlForFetchingDisplayNameByIdExpression(final String sqlKeyColumn)
	{
		final Evaluatee ctx = Evaluatees.ofMap(ImmutableMap.<String, Object> builder()
				.put(SQL_PARAM_KeyId.getName(), sqlKeyColumn)
				.put(SQL_PARAM_ShowInactive.getName(), SQL_PARAM_VALUE_ShowInactive_Yes)
				.build());

		return sqlForFetchingDisplayNameByIdExpression.resolvePartial(ctx);
	}

	public int getEntityTypeIndex()
	{
		return entityTypeIndex;
	}

	public Set<String> getDependsOnFieldNames()
	{
		return dependsOnFieldNames;
	}

	public IValidationRule getValidationRule()
	{
		return validationRule;
	}

	public static final class Builder
	{
		// Parameters
		private String columnName;
		private Integer displayType;
		private Integer AD_Reference_Value_ID;
		private Integer AD_Val_Rule_ID;

		//
		// Built/prepared values
		private boolean numericKey;
		private Set<String> dependsOnFieldNames;

		private IValidationRule validationRule;
		private String sqlTableName;
		private ICachedStringExpression sqlForFetchingExpression;
		private ICachedStringExpression sqlForCountingExpression;
		private ICachedStringExpression sqlForFetchingDisplayNameByIdExpression;
		private int entityTypeIndex = -1;

		private Builder()
		{
			super();
		}

		public SqlLookupDescriptor build()
		{
			Check.assumeNotEmpty(columnName, "columnName is not empty");

			final int Column_ID = 0;
			final boolean IsParent = false;

			if (displayType == DisplayType.PAttribute && AD_Reference_Value_ID <= 0)
			{
				numericKey = true;
				setSqlExpressions_PAttribute(columnName);
				validationRule = NullValidationRule.instance;
				dependsOnFieldNames = ImmutableSet.of();
			}
			else
			{
				final MLookupInfo lookupInfo = MLookupFactory.getLookupInfo(WINDOWNO_Dummy, Column_ID, displayType, columnName, AD_Reference_Value_ID, IsParent, AD_Val_Rule_ID);

				numericKey = lookupInfo.isNumericKey();
				setSqlExpressions(lookupInfo);
				validationRule = lookupInfo.getValidationRule();

				//
				// Case: DocAction button => inject the DocActionValidationRule
				// FIXME: hardcoded
				if (displayType == DisplayType.Button && WindowConstants.FIELDNAME_DocAction.equals(columnName))
				{
					validationRule = CompositeValidationRule.compose(validationRule, DocActionValidationRule.instance);
				}

				dependsOnFieldNames = ImmutableSet.copyOf(validationRule.getParameters());
			}

			return new SqlLookupDescriptor(this);
		}

		private void setSqlExpressions(final MLookupInfo lookupInfo)
		{
			//
			// WHERE
			final IStringExpression sqlWhereFinal = buildSqlWhere(lookupInfo);

			//
			// ORDER BY
			String lookup_SqlOrderBy = lookupInfo.getOrderBySqlPart();
			if (Check.isEmpty(lookup_SqlOrderBy, true))
			{
				lookup_SqlOrderBy = String.valueOf(MLookupFactory.COLUMNINDEX_DisplayName);
			}

			//
			// Set the SQLs
			{
				sqlTableName = lookupInfo.getTableName();
				sqlForFetchingExpression = buildSqlForFetching(lookupInfo, sqlWhereFinal, lookup_SqlOrderBy)
						.caching();
				sqlForCountingExpression = buildSqlForCounting(lookupInfo, sqlWhereFinal)
						.caching();
				sqlForFetchingDisplayNameByIdExpression = buildSqlForFetchingDisplayNameById(lookupInfo)
						.caching();

				if (lookupInfo.isQueryHasEntityType())
				{
					entityTypeIndex = MLookupFactory.COLUMNINDEX_EntityType;
				}
			}
		}

		private void setSqlExpressions_PAttribute(final String columnName)
		{
			final String tableName = I_M_AttributeSetInstance.Table_Name;
			final String keyColumnNameFQ = tableName + "." + I_M_AttributeSetInstance.COLUMNNAME_M_AttributeSetInstance_ID;
			final String displayColumnSql = tableName + "." + I_M_AttributeSetInstance.COLUMNNAME_Description;
			final CompositeStringExpression.Builder sqlSelectFrom = IStringExpression.composer()
					.append("SELECT ")
					.append(" ").append(keyColumnNameFQ) // Key
					.append(", NULL") // Value
					.append(",").append(displayColumnSql) // DisplayName
					.append(", M_AttributeSetInstance.IsActive") // IsActive
					.append(", NULL") // EntityType
					.append(" FROM ").append(tableName);
			//
			// WHERE
			final StringBuilder sqlWhereFinal = new StringBuilder();
			{
				// Validation Rule's WHERE
				if (sqlWhereFinal.length() > 0)
				{
					sqlWhereFinal.append("\n AND ");
				}
				sqlWhereFinal.append(" /* validation rule */ ").append("(").append(SQL_PARAM_ValidationRuleSql).append(")");

				// Filter's WHERE
				if (sqlWhereFinal.length() > 0)
				{
					sqlWhereFinal.append("\n AND ");
				}
				sqlWhereFinal.append(" /* filter */ ").append("(").append(displayColumnSql).append(") ILIKE ").append(SQL_PARAM_FilterSql); // #1
			}

			//
			// ORDER BY
			String lookup_SqlOrderBy = I_M_AttributeSetInstance.COLUMNNAME_Description;
			if (Check.isEmpty(lookup_SqlOrderBy, true))
			{
				lookup_SqlOrderBy = String.valueOf(MLookupFactory.COLUMNINDEX_DisplayName);
			}

			//
			// Assemble the SQLs
			final IStringExpression sqlForFetching = IStringExpression.composer()
					.append(sqlSelectFrom) // SELECT ... FROM ...
					.append("\n WHERE \n").append(sqlWhereFinal) // WHERE
					.append("\n ORDER BY ").append(lookup_SqlOrderBy) // ORDER BY
					.append("\n OFFSET ").append(SQL_PARAM_Offset.toStringWithMarkers())
					.append("\n LIMIT ").append(SQL_PARAM_Limit.toStringWithMarkers()) // LIMIT
					.wrap(AccessSqlStringExpression.wrapper(tableName, IUserRolePermissions.SQL_FULLYQUALIFIED, IUserRolePermissions.SQL_RO)) // security
					.build();
			final IStringExpression sqlForCounting = IStringExpression.composer()
					.append("SELECT COUNT(1) FROM ").append(tableName) // SELECT .. FROM ...
					.append(" WHERE ").append(sqlWhereFinal) // WHERE
					.wrap(AccessSqlStringExpression.wrapper(tableName, IUserRolePermissions.SQL_FULLYQUALIFIED, IUserRolePermissions.SQL_RO)) // security
					.build();
			;
			final IStringExpression sqlForFetchingDisplayNameById = IStringExpression.composer()
					.append("SELECT ").append(displayColumnSql) // SELECT
					.append("\n FROM ").append(tableName) // FROM
					.append("\n WHERE ").append(keyColumnNameFQ).append("=").append(SQL_PARAM_KeyId)
					.build();

			//
			// Set the SQLs
			{
				sqlTableName = tableName;
				sqlForFetchingExpression = sqlForFetching.caching();
				sqlForCountingExpression = sqlForCounting.caching();
				sqlForFetchingDisplayNameByIdExpression = sqlForFetchingDisplayNameById.caching();
			}
		}

		private static final IStringExpression buildSqlWhere(final MLookupInfo lookupInfo)
		{
			final String tableName = lookupInfo.getTableName();
			final String lookup_SqlWhere = lookupInfo.getWhereClauseSqlPart();
			final TranslatableParameterizedString displayColumnSql = lookupInfo.getDisplayColumnSql();

			final CompositeStringExpression.Builder sqlWhereFinal = IStringExpression.composer();

			// Static lookup's WHERE
			if (!Check.isEmpty(lookup_SqlWhere, true))
			{

				sqlWhereFinal.append(" /* lookup where clause */ ").append("(").append(lookup_SqlWhere).append(")");
			}

			// Validation Rule's WHERE
			sqlWhereFinal.appendIfNotEmpty("\n AND ");
			sqlWhereFinal.append(" /* validation rule */ ").append("(\n").append(SQL_PARAM_ValidationRuleSql).append("\n)\n");

			// Filter's WHERE
			sqlWhereFinal.appendIfNotEmpty("\n AND ");
			sqlWhereFinal.append(" /* filter */ ").append("(").append(displayColumnSql).append(") ILIKE ").append(SQL_PARAM_FilterSql); // #1

			// IsActive WHERE
			sqlWhereFinal.appendIfNotEmpty("\n AND ");
			sqlWhereFinal.append(" /* active */ ('").append(SQL_PARAM_ShowInactive.toStringWithMarkers()).append("'='Y' OR ").append(tableName).append(".IsActive='Y')");

			return sqlWhereFinal.build();
		}

		private final IStringExpression buildSqlForFetching(final MLookupInfo lookupInfo, final IStringExpression sqlWhere, final String sqlOrderBy)
		{
			final String tableName = lookupInfo.getTableName();
			return IStringExpression.composer()
					.append(lookupInfo.getSelectSqlPart()) // SELECT .. FROM ...
					.append("\n WHERE \n").append(sqlWhere) // WHERE
					.append("\n ORDER BY ").append(sqlOrderBy) // ORDER BY
					.append("\n OFFSET ").append(SQL_PARAM_Offset) // OFFSET
					.append("\n LIMIT ").append(SQL_PARAM_Limit) // LIMIT
					.wrapIfTrue(!lookupInfo.isSecurityDisabled(), AccessSqlStringExpression.wrapper(tableName, IUserRolePermissions.SQL_FULLYQUALIFIED, IUserRolePermissions.SQL_RO)) // security
					.build();
		}

		private final IStringExpression buildSqlForCounting(final MLookupInfo lookupInfo, final IStringExpression sqlWhere)
		{
			final String tableName = lookupInfo.getTableName();
			return IStringExpression.composer()
					.append("SELECT COUNT(1) FROM ")
					.append(lookupInfo.getFromSqlPart()) // SELECT .. FROM ...
					.append(" WHERE ").append(sqlWhere) // WHERE
					.wrapIfTrue(!lookupInfo.isSecurityDisabled(), AccessSqlStringExpression.wrapper(tableName, IUserRolePermissions.SQL_FULLYQUALIFIED, IUserRolePermissions.SQL_RO)) // security
					.build();
		}

		private final IStringExpression buildSqlForFetchingDisplayNameById(final MLookupInfo lookupInfo)
		{
			final IStringExpression displayColumnSQL = TranslatableParameterizedStringExpression.of(lookupInfo.getDisplayColumnSql());
			// useBaseLanguage ? lookupInfo.getDisplayColumnSQL_BaseLang() : lookupInfo.getDisplayColumnSQL_Trl();
			final IStringExpression fromSqlPart = TranslatableParameterizedStringExpression.of(lookupInfo.getFromSqlPart());
			// useBaseLanguage ? lookupInfo.getFromSqlPart_BaseLang() : lookupInfo.getFromSqlPart_Trl();
			final String keyColumnFQ = lookupInfo.getKeyColumnFQ();
			final int displayType = lookupInfo.getDisplayType();
			final String whereClauseSqlPart = lookupInfo.getWhereClauseSqlPart(); // assuming this is constant!

			final IStringExpression sqlForFetchingDisplayNameById = IStringExpression.composer()
					.append("SELECT ").append(displayColumnSQL) // SELECT ...
					.append("\n FROM ").append(fromSqlPart) // FROM
					.append("\n WHERE ").append(keyColumnFQ).append("=").append(SQL_PARAM_KeyId)
					.append(" ")
					// FIXME: make it better: this is actually adding the AD_Ref_List.AD_Reference_ID=....
					.append(DisplayType.List == displayType || DisplayType.Button == displayType ? " AND " + whereClauseSqlPart : "")
					.build();

			return sqlForFetchingDisplayNameById;
		}

		public Builder setColumnName(final String columnName)
		{
			this.columnName = columnName;
			return this;
		}

		public Builder setDisplayType(final int displayType)
		{
			this.displayType = displayType;
			return this;
		}

		public Builder setAD_Reference_Value_ID(final int AD_Reference_Value_ID)
		{
			this.AD_Reference_Value_ID = AD_Reference_Value_ID;
			return this;
		}

		public Builder setAD_Val_Rule_ID(final int AD_Val_Rule_ID)
		{
			this.AD_Val_Rule_ID = AD_Val_Rule_ID;
			return this;
		}
	}
}
