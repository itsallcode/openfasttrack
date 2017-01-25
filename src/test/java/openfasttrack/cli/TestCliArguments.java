package openfasttrack.cli;

/*
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 - 2017 hamstercommunity
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

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

public class TestCliArguments
{
    private CliArguments arguments;

    @Before
    public void setUp()
    {
        this.arguments = new CliArguments();
    }

    @Test
    public void testGetCommandWithUnnamedValuesNull()
    {
        this.arguments.setUnnamedValues(null);
        assertThat(this.arguments.getCommand(), equalTo(null));
    }

    @Test
    public void testGetCommandWithUnnamedValuesEmpty()
    {
        this.arguments.setUnnamedValues(Collections.emptyList());
        assertThat(this.arguments.getCommand(), equalTo(null));
    }
}