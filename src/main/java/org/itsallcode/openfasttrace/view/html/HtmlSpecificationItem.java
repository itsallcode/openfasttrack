package org.itsallcode.openfasttrace.view.html;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
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

import static org.itsallcode.openfasttrace.view.html.CharacterConstants.CHECKMARK;

import java.io.PrintStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.itsallcode.openfasttrace.core.*;
import org.itsallcode.openfasttrace.view.Viewable;

public class HtmlSpecificationItem implements Viewable
{
    private final LinkedSpecificationItem item;
    private final PrintStream stream;
    private final MarkdownConverter converter = new MarkdownConverter();

    public HtmlSpecificationItem(final PrintStream stream, final LinkedSpecificationItem item)
    {
        this.stream = stream;
        this.item = item;
    }

    @Override
    public void render(final int level)
    {
        final String indentation = IndentationHelper.createIndentationPrefix(level);
        final SpecificationItemId id = this.item.getId();
        renderStart(indentation, id);
        renderSummary(indentation, id);
        renderId(indentation, id);
        renderDescription(indentation);
        renderRationale(indentation);
        renderComment(indentation);
        renderNeeds(indentation);
        renderLinks(indentation);
        renderEnd(indentation);
    }

    private void renderId(final String indentation, final SpecificationItemId id)
    {
        this.stream.print(indentation);
        this.stream.print("    <p class=\"id\">");
        this.stream.print(id);
        this.stream.println("</p>");
    }

    protected void renderStart(final String indentation, final SpecificationItemId id)
    {
        this.stream.print(indentation);
        this.stream.print("<section class=\"sitem\" id=\"");
        this.stream.print(id);
        this.stream.println("\">");
        this.stream.print(indentation);
        this.stream.println("  <details>");
    }

    protected void renderSummary(final String indentation, final SpecificationItemId id)
    {
        this.stream.print(indentation);
        this.stream.print("    <summary title=\"");
        this.stream.print(id);
        this.stream.print("\">");
        this.stream.print(CHECKMARK);
        this.stream.print(" <b>");
        this.stream.print(this.item.getTitleWithFallback());
        this.stream.print("</b><small>, rev. ");
        this.stream.print(id.getRevision());
        this.stream.print(", ");
        this.stream.print(id.getArtifactType());
        this.stream.println("</small></summary>");
    }

    protected void renderDescription(final String indentation)
    {
        final String description = this.item.getDescription();
        if (description != null && !description.isEmpty())
        {
            renderMultilineText(indentation, description);
        }
    }

    protected void renderMultilineText(final String indentation, final String text)
    {
        this.stream.print(indentation);
        for (final String line : text.split("[\n\r]+"))
        {
            this.stream.print("    <p>");
            this.stream.print(this.converter.convert(line));
            this.stream.println("</p>");
        }
    }

    private void renderRationale(final String indentation)
    {
        final String rationale = this.item.getItem().getRationale();
        if (rationale != null && !rationale.isEmpty())
        {
            this.stream.print(indentation);
            this.stream.println("    <h6>Rationale:</h6>");
            renderMultilineText(indentation, rationale);
        }
    }

    private void renderComment(final String indentation)
    {
        final String comment = this.item.getItem().getComment();
        if (comment != null && !comment.isEmpty())
        {
            this.stream.print(indentation);
            this.stream.println("    <h6>Comment:</h6>");
            renderMultilineText(indentation, comment);
        }
    }

    private void renderNeeds(final String indentation)
    {
        if ((this.item.getNeedsArtifactTypes() != null
                && !this.item.getNeedsArtifactTypes().isEmpty())
                || (this.item.getUncoveredArtifactTypes() != null
                        && !this.item.getUncoveredArtifactTypes().isEmpty())
                || (this.item.getOverCoveredArtifactTypes() != null
                        && !this.item.getOverCoveredArtifactTypes().isEmpty()))
        {
            this.stream.print(indentation);
            this.stream.print("    <h6>Needs: ");
            this.stream.print(translateArtifactTypeCoverage(this.item));
            this.stream.println("</h6>");
        }
    }

    private String translateArtifactTypeCoverage(final LinkedSpecificationItem item)
    {
        final Comparator<String> byTypeName = (a, b) -> a.replaceFirst("<(?:ins|del)>", "")
                .compareTo(b.replaceFirst("<(?:ins|del)>", ""));

        final Stream<String> uncoveredStream = item.getUncoveredArtifactTypes().stream()
                .map(x -> "<ins>" + x + "</ins>");
        return Stream.concat( //
                Stream.concat( //
                        uncoveredStream, //
                        item.getCoveredArtifactTypes().stream() //
                ), //
                item.getOverCoveredArtifactTypes().stream().map(x -> "<del>" + x + "</del>") //
        ) //
                .sorted(byTypeName) //
                .collect(Collectors.joining(", "));
    }

    private void renderLinks(final String indentation)
    {
        final int totalOut = this.item.countOutgoingLinks();
        if (totalOut > 0)
        {
            this.stream.print(indentation);
            this.stream.print("    <h6>Out: ");
            this.stream.print(totalOut);
            this.stream.println(" total</h6>");
            this.stream.println("    <ul class=\"out\">");
            final Stream<TracedLink> outLinks = this.item.getTracedLinks().stream()
                    .filter(TracedLink::isOutgoing);
            final List<TracedLink> sortedLinks = sortLinkStreamById(outLinks);
            renderLinkEntry(sortedLinks);
            this.stream.println("    </ul>");
        }
        final int totalIn = this.item.countIncomingLinks();
        if (totalIn > 0)
        {
            this.stream.print(indentation);
            this.stream.print("    <h6>In: ");
            this.stream.print(totalIn);
            this.stream.println(" total</h6>");
            this.stream.println("    <ul class=\"in\">");
            final Stream<TracedLink> inLinks = this.item.getTracedLinks().stream()
                    .filter(TracedLink::isIncoming);
            final List<TracedLink> sortedLinks = sortLinkStreamById(inLinks);
            renderLinkEntry(sortedLinks);
            this.stream.println("    </ul>");
        }
    }

    protected List<TracedLink> sortLinkStreamById(final Stream<TracedLink> tracedLinkStream)
    {
        return tracedLinkStream //
                .sorted((a, b) -> a.getOtherLinkEnd().getId().toString()
                        .compareTo(b.getOtherLinkEnd().getId().toString())) //
                .collect(Collectors.toList());
    }

    protected void renderLinkEntry(final List<TracedLink> outLinks)
    {
        for (final TracedLink link : outLinks)
        {
            final SpecificationItemId otherId = link.getOtherLinkEnd().getId();
            this.stream.print("      <li><a href=\"#");
            this.stream.print(otherId);
            this.stream.print("\">");
            this.stream.print(otherId);
            this.stream.print("</a>");
            if (link.getStatus() != LinkStatus.COVERS
                    && link.getStatus() != LinkStatus.COVERED_SHALLOW)
            {
                this.stream.print(" <em>(" + link.getStatus() + ")</em>");
            }
            this.stream.println("</li>");
        }
    }

    protected void renderEnd(final String indentation)
    {
        this.stream.print(indentation);
        this.stream.println("  </details>");
        this.stream.print(indentation);
        this.stream.println("</section>");
    }
}