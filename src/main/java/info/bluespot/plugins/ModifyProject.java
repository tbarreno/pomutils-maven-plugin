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

import org.apache.maven.model.Model;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Modifies the basic project information: GroupId, ArtifactId, etc.
 * <p>
 * Note: this goal doesn't support multi-module project at this moment. Changing any GAV component will break parent
 * relationships or dependencies in multi-module setups.
 * </p>
 * 
 * @since 1.0.0
 */
@Mojo( name = "modify-project", requiresProject = true, inheritByDefault = false )
public class ModifyProject
    extends AbstractMojo
{
    // PARAMETERS ............................................................

    /**
     * New <code>ArtifactId</code>.
     */
    @Parameter( property = "artifactId", required = false, readonly = true )
    private String artifactId;

    /**
     * New <code>GroupId</code>.
     */
    @Parameter( property = "groupId", required = false, readonly = true )
    private String groupId;

    /**
     * New version.
     */
    @Parameter( property = "version", required = false, readonly = true )
    private String version;

    /**
     * New project <code>Name</code>.
     */
    @Parameter( property = "name", required = false, readonly = true )
    private String name;

    /**
     * New <code>description</code>.
     */
    @Parameter( property = "description", required = false, readonly = true )
    private String description;

    /**
     * New <code>URL</code>.
     */
    @Parameter( property = "url", required = false, readonly = true )
    private String url;

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

        // Modify the project information...
        if ( groupId != null )
        {
            getLog().info( "Setting the groupId to '" + groupId + "'" );
            model.setGroupId( groupId );
        }

        if ( artifactId != null )
        {
            getLog().info( "Setting the artifactId to '" + artifactId + "'" );
            model.setArtifactId( artifactId );
        }

        if ( version != null )
        {
            getLog().info( "Setting the version to '" + version + "'" );
            model.setVersion( version );
        }

        if ( name != null )
        {
            getLog().info( "Setting the name to '" + name + "'" );
            model.setName( name );
        }

        if ( description != null )
        {
            getLog().info( "Setting the description to '" + description + "'" );
            model.setDescription( description );
        }

        if ( url != null )
        {
            getLog().info( "Setting the URL to '" + url + "'" );
            model.setUrl( url );
        }

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
}
