/*
	Copyright (C) 2012  Rick Brown

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */
package com.googlecode.jgenhtml.ant;

import com.googlecode.jgenhtml.plugin.JGenHtmlExecuter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

/**
 * This class implements an Ant task to run jgenhtml.
 * The primary benefit of this is to run the main class without forking execution of
 * the JAR in a new JVM.
 *
 * Usage:
	<taskdef name="jgenhtml"
		classname="com.googlecode.jgenhtml.ant.JGenHtmlTask"
		classpath="jgenhtml.jar"/>

	<jgenhtml in="jsTestDriver.conf-coverage.dat" outdir="${outdir}" config="lcovrc"/>

	OR

	<jgenhtml outdir="${outdir}" config="lcovrc">
		<path>
			<fileset dir="${somedir}" includes="*.info"/>
		</path>
	</jgenhtml>
 *
 * @author Rick Brown
 */
public class JGenHtmlTask extends Task
{
	private JGenHtmlExecuter executer = new JGenHtmlExecuter();
	private List<Path> paths = null;

	/**
	 * Add a path which references one or more tracefiles.
	 * @param path The path to add.
	 */
	public void addPath(final Path path)
	{
		if(this.paths == null)
		{
			this.paths = new ArrayList<Path>();
		}
		this.paths.add(path);
	}

	/**
	 * @param outdir The path to the output directory in which output will be generated.
	 */
	public void setOutdir(final String outdir)
	{
		executer.setOutdir(outdir);
	}

	/**
	 *
	 * @param in The path to a tracefile file we want to process
	 */
	public void setIn(final String in)
	{
		executer.addTracefile(in);
	}

	/**
	 *
	 * @param in The path to an lcovrc config file.
	 * If not specified will check in user home directory.
	 */
	public void setConfig(final String config)
	{
		executer.setConfig(config);
	}

	/**
	 * Run jgenhtml with the provided attributes.
	 * Note that if both "in" and nested path are present then both will be used.
	 * @throws BuildException
	 */
	@Override
	public void execute() throws BuildException
	{
		try
		{
			if(this.paths != null)
			{
				Iterator<Path> pathIterator = paths.iterator();
				while(pathIterator.hasNext())
				{
					Path path = pathIterator.next();
					executer.addTracefile(path.list());
				}
			}
			executer.execute();
		}
		catch(IllegalStateException ex)
		{
			throw new BuildException(ex.getMessage(), ex);
		}
	}
}
