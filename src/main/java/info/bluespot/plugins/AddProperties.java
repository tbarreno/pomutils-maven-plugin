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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Adds properties to a POM file.
 * <p>
 * Properties can be provided in a Java property file or via the '<code>properties</code>' parameter as a list (comma
 * separated).
 * </p>
 * <p>
 * The properties can be stored in the general <code>properties</code> section of the POM or in a specific profile using
 * the <code>profile</code> parameter.
 * </p>
 *
 * @since 1.0.0
 */
@Mojo( name = "add-properties", requiresProject = false, inheritByDefault = false )
public class AddProperties
    extends AbstractMojo
{
    // PARAMETERS ............................................................

    /**
     * Property file. All contained properties will be loaded onto the POM file.
     */
    @Parameter( property = "propertiesFile", required = false, readonly = true )
    private String propertiesFile;

    /**
     * Adds a set of properties (comma separated).
     *
     * <pre>
     *   -Dproperties=key1=value1,key2="value 2",key.3="value 3"
     * </pre>
     */
    @Parameter( property = "properties", required = false, readonly = true )
    private String[] properties;

    /**
     * Profile to put the properties on.
     */
    @Parameter( property = "profile", required = false, readonly = true )
    private String profile;

    /**
     * Saves a copy of the current POM before the modification.
     */
    @Parameter( property = "pomBackup", required = false, readonly = true )
    private String pomBackup;

    /**
     * Selects a different POM file to act on.
     */
    @Parameter( property = "pomFile", required = false, readonly = true, defaultValue = "pom.xml" )
    private String pomFile;

    /**
     * Properties encoding (UTF-8 by default).
     */
    @Parameter( property = "propertiesEncoding", required = false, readonly = true, defaultValue = "UTF-8" )
    private String propertiesEncoding;

    // METHODS ...............................................................

    /**
     * Main goal method.
     */
    @Override
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        // Check: we need a properties file or the properties list
        if ( propertiesFile == null && properties == null )
        {
            getLog().warn( "This goal needs the properties list parameter or a properties file." );
            return;
        }

        // Load the model
        Model model;
        try
        {
            model = POMUtils.loadModel( pomFile );
        }
        catch ( IOException | XmlPullParserException e )
        {
            throw new MojoExecutionException( "Error loading the Maven model:", e );
        }

        // Get the properties from the file or the list parameter
        Properties properties;
        try
        {
            properties = loadProperties();
        }
        catch ( IOException ioe )
        {
            throw new MojoExecutionException( "I/O error while reading the properties file.", ioe );
        }

        // Check if a profile is provided
        if ( profile == null )
        {
            // Add the properties to the general section on the POM
            addToProperties( model, properties );
        }
        else
        {
            // Add the properties to a specific profile
            addToProfile( model, properties, profile );
        }

        // Save the model
        try
        {
            POMUtils.saveModel( model, pomFile, pomBackup );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "I/O error while writing the POM file.", e );
        }
    }

    /**
     * Generates a properties map from a specified properties file or the list parameter.
     *
     * @throws IOException I/O error reading the properties file.
     * @return The properties map.
     */
    private Properties loadProperties()
        throws IOException
    {
        Properties mergedProperties = new Properties();

        // Check if the properties file is provided
        if ( propertiesFile != null )
        {
            File file = new File( propertiesFile );

            if ( file.canRead() )
            {
                InputStream is = new FileInputStream( file );
                mergedProperties.load( new InputStreamReader( is, Charset.forName( propertiesEncoding ) ) );
            }
        }

        // Check if the properties parameter is defined
        if ( properties != null )
        {
            // Split the properties by the '=' character (Maven has already generated a list from the parameter)
            for ( String entry : properties )
            {
                int equalsPosition = entry.indexOf( "=" );
                mergedProperties.put( entry.substring( 0, equalsPosition ), entry.substring( equalsPosition + 1 ) );
            }
        }

        return ( mergedProperties );
    }

    /**
     * Adds the properties to the general section on the POM model.
     * 
     * @param model Maven model.
     * @param properties Property map for inclusion.
     */
    private void addToProperties( Model model, Properties properties )
    {
        getLog().info( "Adding general properties to the model." );

        // Get the current properties and add the new ones
        Properties modelProperties = model.getProperties();

        Enumeration<Object> keys = properties.keys();
        while ( keys.hasMoreElements() )
        {
            Object key = keys.nextElement();
            modelProperties.put( key, properties.get( key ) );
        }

        model.setProperties( modelProperties );
    }

    /**
     * Adds the properties to a specific profile section.
     * 
     * @param model Maven model.
     * @param properties Property map for inclusion.
     * @param profile Name (ID) of the profile to add the profile in.
     */
    private void addToProfile( Model model, Properties properties, String profile )
    {
        getLog().info( "Adding properties to the profile: '" + profile + "'" );

        // Get the current profile list
        Profile modelProfile = null;
        for ( Profile profileIdx : model.getProfiles() )
        {
            if ( profileIdx.getId().equals( profile ) )
            {
                // Profile found
                modelProfile = profileIdx;
                break;
            }
        }

        // If we haven't found the profile, just create it
        if ( modelProfile == null )
        {
            // Create the profile
            modelProfile = new Profile();
            modelProfile.setId( profile );

            // Add the properties
            Enumeration<Object> keys = properties.keys();
            while ( keys.hasMoreElements() )
            {
                Object key = keys.nextElement();
                modelProfile.addProperty( (String) key, (String) properties.get( key ) );
            }

            // Add the profile to the current model
            model.addProfile( modelProfile );
        }
        else
        {
            // Add the profile to the existing profile
            Properties profileProperties = modelProfile.getProperties();

            Enumeration<Object> keys = properties.keys();
            while ( keys.hasMoreElements() )
            {
                Object key = keys.nextElement();
                profileProperties.put( key, properties.get( key ) );
            }

            modelProfile.setProperties( profileProperties );
        }
    }
}
