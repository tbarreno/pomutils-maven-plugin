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

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Adds a dependency to a POM file.
 * <p>
 * The <code>artifactId</code> and the <code>groupId</code> of the dependency are needed parameters.
 * </p>
 * 
 * @since 1.0.0
 */
@Mojo( name = "add-dependency", requiresProject = true, inheritByDefault = false )
public class AddDependency
    extends AbstractMojo
{
    // PARAMETERS ............................................................

    /**
     * Dependency's <code>ArtifactId</code>.
     */
    @Parameter( property = "artifactId", required = true, readonly = true )
    private String artifactId;

    /**
     * Dependency's <code>GroupId</code>.
     */
    @Parameter( property = "groupId", required = true, readonly = true )
    private String groupId;

    /**
     * Dependency's version (optional).
     */
    @Parameter( property = "version", required = false, readonly = true )
    private String version;

    /**
     * Dependency's <code>systemPath</code>.
     */
    @Parameter( property = "systemPath", required = false, readonly = true )
    private String systemPath;

    /**
     * Dependency's <code>type</code>.
     */
    @Parameter( property = "type", required = false, readonly = true )
    private String type;

    /**
     * Dependency's <code>scope</code>.
     */
    @Parameter( property = "scope", required = false, readonly = true )
    private String scope;

    /**
     * Dependency's <code>optional</code> field.
     */
    @Parameter( property = "optional", required = false, readonly = true, defaultValue = "false" )
    private Boolean optional;

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
        getLog().info( "Adding the dependency " + groupId + ":" + artifactId );

        // Load the model
        Model model;
        try
        {
            model = POMUtils.loadModel( pomFile );
        }
        catch ( IOException | XmlPullParserException e )
        {
            throw new MojoExecutionException( "Error while loading the Maven model.", e );
        }

        // Creating a dependency object
        Dependency dependency = new Dependency();

        dependency.setArtifactId( artifactId );
        dependency.setGroupId( groupId );
        if ( version != null )
        {
            dependency.setVersion( version );
        }
        if ( systemPath != null )
        {
            dependency.setSystemPath( systemPath );
        }
        if ( type != null )
        {
            dependency.setType( type );
        }
        if ( scope != null )
        {
            dependency.setScope( scope );
        }
        if ( optional )
        {
            dependency.setOptional( true );
        }

        // Add the dependency to the model
        model.addDependency( dependency );

        // Save the model
        try
        {
            POMUtils.saveModel( model, pomFile, pomBackup );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "I/O error while writing the Maven model.", e );
        }
    }
}
