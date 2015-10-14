package org.adempiere.archive.spi.impl;

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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.I_AD_Archive;
import org.compiere.util.CLogger;

/**
 * Database archive storage
 * 
 * @author tsa
 * 
 */
public class DBArchiveStorage extends AbstractArchiveStorage
{
	private static final CLogger logger = CLogger.getCLogger(DBArchiveStorage.class);

	@Override
	public I_AD_Archive newArchive(final Properties ctx, final String trxName)
	{
		final I_AD_Archive archive = super.newArchive(ctx, trxName);
		archive.setIsFileSystem(false);
		return archive;
	}

	/**
	 * Get Binary Data. (inflate)
	 * 
	 * @return inflated data
	 */
	@Override
	public byte[] getBinaryData(final I_AD_Archive archive)
	{
		byte[] deflatedData = archive.getBinaryData();
		// m_deflated = null;
		// m_inflated = null;
		if (deflatedData == null)
			return null;
		//
		logger.fine("ZipSize=" + deflatedData.length);
		// m_deflated = new Integer(deflatedData.length);
		if (deflatedData.length == 0)
			return null;

		byte[] inflatedData = null;
		try
		{
			ByteArrayInputStream in = new ByteArrayInputStream(deflatedData);
			ZipInputStream zip = new ZipInputStream(in);
			ZipEntry entry = zip.getNextEntry();
			if (entry != null) // just one entry
			{
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buffer = new byte[2048];
				int length = zip.read(buffer);
				while (length != -1)
				{
					out.write(buffer, 0, length);
					length = zip.read(buffer);
				}
				//
				inflatedData = out.toByteArray();
				logger.fine("Size=" + inflatedData.length + " - zip=" + entry.getCompressedSize()
						+ "(" + entry.getSize() + ") "
						+ (entry.getCompressedSize() * 100 / entry.getSize()) + "%");
				// m_inflated = new Integer(inflatedData.length);
			}
		}
		catch (Exception e)
		{
			// logger.log(Level.SEVERE, "", e);
			inflatedData = null;
			throw new AdempiereException(e);
		}
		return inflatedData;
	} // getBinaryData

	@Override
	public void setBinaryData(I_AD_Archive archive, byte[] inflatedData)
	{
		if (inflatedData == null || inflatedData.length == 0)
			throw new IllegalArgumentException("InflatedData is NULL");
		// m_inflated = new Integer(inflatedData.length);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ZipOutputStream zip = new ZipOutputStream(out);
		zip.setMethod(ZipOutputStream.DEFLATED);
		zip.setLevel(Deflater.BEST_COMPRESSION);
		zip.setComment("adempiere");
		//
		byte[] deflatedData = null;
		try
		{
			ZipEntry entry = new ZipEntry("AdempiereArchive");
			entry.setTime(System.currentTimeMillis());
			entry.setMethod(ZipEntry.DEFLATED);
			zip.putNextEntry(entry);
			zip.write(inflatedData, 0, inflatedData.length);
			zip.closeEntry();
			logger.fine(entry.getCompressedSize() + " (" + entry.getSize() + ") "
					+ (entry.getCompressedSize() * 100 / entry.getSize()) + "%");
			//
			// zip.finish();
			zip.close();
			deflatedData = out.toByteArray();
			logger.fine("Length=" + inflatedData.length);
			// m_deflated = new Integer(deflatedData.length);
		}
		catch (Exception e)
		{
			// log.log(Level.SEVERE, "saveLOBData", e);
			// deflatedData = null;
			// m_deflated = null;
			throw new AdempiereException(e);
		}

		archive.setBinaryData(deflatedData);
		archive.setIsFileSystem(false);
	}
}
