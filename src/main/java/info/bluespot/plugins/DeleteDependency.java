package info.bluespot.plugins;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Removes a dependency from the POM.
 * <p>
 * This goal looks for a matching dependency and removes it from the POM file.
 * </p>
 * 
 * @since 1.0.0
 */
@Mojo( name = "delete-dependency", requiresProject = true, inheritByDefault = false )
public class DeleteDependency
    extends AbstractMojo
{
    // PARAMETERS ............................................................

    /**
     * Dependency <code>ArtifactId</code>.
     */
    @Parameter( property = "artifactId", required = false, readonly = true )
    private String artifactId;

    /**
     * Dependency <code>GroupId</code>.
     */
    @Parameter( property = "groupId", required = true, readonly = true )
    private String groupId;

    /**
     * Dependency version.
     */
    @Parameter( property = "version", required = false, readonly = true )
    private String version;

    /**
     * Keeps a copy of the current POM file before modifying it.
     */
    @Parameter( property = "pomBackup", required = false, readonly = true )
    private String pomBackup;

    /**
     * Specifies a POM file to modify.
     */
    @Parameter( property = "pomFile", required = false, readonly = true, defaultValue = "pom.xml" )
    private String pomFile;

    // METHODS ...............................................................

    /**
     * Main goal method.
     */
    @Override
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        // Just check if all the GAV is null
        if ( artifactId == null && groupId == null && version == null )
        {
            throw new MojoExecutionException( "An ArtifactId, GroupId or Version parameter is needed for dependency deletion." );
        }

        getLog().info( "Removing dependency: '" + groupId + ":" + artifactId + "'" );

        // Loads the model
        Model model;
        try
        {
            model = POMUtils.loadModel( pomFile );
        }
        catch ( IOException | XmlPullParserException e )
        {
            // Exception loading the model
            throw new MojoExecutionException( "Error while loading the Maven project model:", e );
        }

        // Gets the dependency list and iterate over it
        List<Dependency> dependencyList = model.getDependencies();
        Vector<Dependency> newDependencyList = new Vector<Dependency>();

        for ( Dependency dependency : dependencyList )
        {
            // It a matching dependency is found, just skip it; if not, the dependency
            // is added to the new list.
            if ( matches( dependency ) )
            {
                getLog().info( "Dependency found (removed)." );
            }
            else
            {
                newDependencyList.add( dependency );
            }
        }

        // Set the new (filtered) dependency list
        model.setDependencies( newDependencyList );

        // Save the model
        try
        {
            POMUtils.saveModel( model, pomFile, pomBackup );
        }
        catch ( IOException e )
        {
            // Exception
            throw new MojoExecutionException( "Error while writing the POM file:", e );
        }
    }

    /**
     * Auxiliary method for match a dependency with 'whatever' has been provided (the <code>groupId</code>, the
     * </code>artifactId</code>, both of them, all the GAV coordinates, etc.).
     * 
     * @param dependency A dependency object from the POM.
     * @return 'true' if there's any match.
     */
    private boolean matches( Dependency dependency )
    {
        // Look for all the matches
        return ( matchesGroupId( dependency ) && matchesArtifactId( dependency ) && matchesVersion( dependency ) );
    }

    /**
     * Auxiliary method for check a <code>GroupId</code> match.
     * 
     * @param dependency A dependency object from the POM.
     * @return 'true' if the <code>GroupId</code> matches.
     */
    private boolean matchesGroupId( Dependency dependency )
    {
        // If the GroupId is not provided, it's OK (the match could be over the ArtifactId or the version)
        if ( groupId == null )
        {
            return ( true );
        }

        if ( groupId.equals( dependency.getGroupId() ) )
        {
            return ( true );
        }
        else
        {
            return ( false );
        }
    }

    /**
     * Auxiliary method for check a <code>ArtifactId</code> match.
     * 
     * @param dependency A dependency object from the POM.
     * @return 'true' if the <code>ArtifactId</code> matches.
     */
    private boolean matchesArtifactId( Dependency dependency )
    {
        // If the ArtifactId is not provided, it's OK (the match could be over the GroupId or the version)
        if ( artifactId == null )
        {
            return ( true );
        }

        if ( artifactId.equals( dependency.getArtifactId() ) )
        {
            return ( true );
        }
        else
        {
            return ( false );
        }
    }

    /**
     * Auxiliary method for check a <code>Version</code> match.
     * 
     * @param dependency A dependency object from the POM.
     * @return 'true' if the <code>Version</code> matches.
     */
    private boolean matchesVersion( Dependency dependency )
    {
        // If the Version is not provided, it's OK
        if ( version == null )
        {
            return ( true );
        }

        if ( version.equals( dependency.getVersion() ) )
        {
            return ( true );
        }
        else
        {
            return ( false );
        }
    }

}
