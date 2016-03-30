package openfasttrack.exporter.specobject;

/*
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 hamstercommunity
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.io.Writer;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import openfasttrack.core.LinkedSpecificationItem;
import openfasttrack.exporter.Exporter;
import openfasttrack.exporter.ExporterException;
import openfasttrack.exporter.ExporterFactory;

public class SpecobjectExporterFactory extends ExporterFactory
{
    private final XMLOutputFactory xmlOutputFactory;

    protected SpecobjectExporterFactory()
    {
        super("specobject");
        this.xmlOutputFactory = XMLOutputFactory.newFactory();
    }

    @Override
    protected Exporter createExporter(final Writer writer,
            final List<LinkedSpecificationItem> items)
    {
        final XMLStreamWriter xmlWriter = createXmlWriter(writer);
        return new SpecobjectExporter(items, xmlWriter);
    }

    private XMLStreamWriter createXmlWriter(final Writer writer)
    {
        XMLStreamWriter xmlWriter;
        try
        {
            xmlWriter = this.xmlOutputFactory.createXMLStreamWriter(writer);
        }
        catch (final XMLStreamException e)
        {
            throw new ExporterException("Error creating xml stream writer for writer " + writer, e);
        }
        return xmlWriter;
    }
}