package de.metas.i18n;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import lombok.EqualsAndHashCode;

/*
 * #%L
 * de.metas.util
 * %%
 * Copyright (C) 2017 metas GmbH
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

@EqualsAndHashCode
final class CompositeTranslatableString implements ITranslatableString
{
	private final ImmutableList<ITranslatableString> list;
	private final String joinString;

	private transient String defaultValue; // lazy
	private transient ImmutableSet<String> adLanguages; // lazy

	public CompositeTranslatableString(final List<ITranslatableString> list, final String joinString)
	{
		this.list = ImmutableList.copyOf(list);
		this.joinString = joinString;
	}

	@Override
	public String toString()
	{
		return list.stream().map(trl -> trl.toString()).collect(Collectors.joining(joinString));
	}

	@Override
	public String translate(String adLanguage)
	{
		return list.stream().map(trl -> trl.translate(adLanguage)).collect(Collectors.joining(joinString));
	}

	@Override
	public String getDefaultValue()
	{
		if (defaultValue == null)
		{
			defaultValue = list.stream().map(trl -> trl.getDefaultValue()).collect(Collectors.joining(joinString));

		}
		return defaultValue;
	}

	@Override
	public Set<String> getAD_Languages()
	{
		if (adLanguages == null)
		{
			adLanguages = list.stream().flatMap(trl -> trl.getAD_Languages().stream()).collect(ImmutableSet.toImmutableSet());
		}
		return adLanguages;
	}

	@Override
	public boolean isTranslatedTo(final String adLanguage)
	{
		return list.stream().anyMatch(trl -> trl.isTranslatedTo(adLanguage));
	}
}
