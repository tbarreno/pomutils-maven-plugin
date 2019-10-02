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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Base class for all POM utilities.
 * <p>
 * This class contains the common methods for reading and writing the project model on the POM file.
 **/
public class POMUtils
{
    /**
     * POM file to use (default file name).
     */
    private static String DEFAULT_POM_FILE = "pom.xml";

    /**
     * Reads the POM model from the default file name.
     * 
     * @throws XmlPullParserException Problem while reading the POM.
     * @throws IOException I/O error.
     * @throws FileNotFoundException File not found.
     */
    protected static Model loadModel()
        throws FileNotFoundException, IOException, XmlPullParserException
    {
        return ( loadModel( DEFAULT_POM_FILE ) );
    }

    /**
     * Reads the POM model from a file name.
     * 
     * @param pomFile File name.
     * @throws IsNotAPOMException The file is not a valid POM.
     * @throws XmlPullParserException Problem while reading the POM.
     * @throws IOException I/O error.
     */
    protected static Model loadModel( String pom )
        throws FileNotFoundException, IOException, XmlPullParserException
    {
        Model model = null;

        if ( pom == null )
        {
            pom = DEFAULT_POM_FILE;
        }

        File pomFile = new File( pom );

        // Check the file
        if ( !pomFile.exists() )
        {
            throw new FileNotFoundException( "File not found: '" + pom + "'." );
        }

        // Read the file
        FileReader fr = new FileReader( pomFile );
        MavenXpp3Reader xpp3reader = new MavenXpp3Reader();

        // Get the model using the XPP3 library
        model = xpp3reader.read( fr );

        return ( model );
    }

    /**
     * Saves the model on the default POM file.
     * 
     * @param model Maven model object.
     * @throws IOException I/O error.
     */
    protected static void saveModel( Model model )
        throws IOException
    {
        saveModel( model, DEFAULT_POM_FILE, null );
    }

    /**
     * Saves the model on a specified POM file name.
     * 
     * @param model Maven model object.
     * @param pom POM file name.
     * @throws IOException I/O error.
     */
    protected static void saveModel( Model model, String pom )
        throws IOException
    {
        saveModel( model, pom, null );
    }

    /**
     * Saves the model on a specified POM file name, creating a backup file if there's an existing file with this name.
     * 
     * @param model Maven model object.
     * @param pom POM file name.
     * @param pomBackup A backup file name for an existing POM file (optional).
     * @throws IOException I/O error.
     */
    protected static void saveModel( Model model, String pom, String pomBackup )
        throws IOException
    {
        File pomFile = new File( pom );

        // If the output file exists and a backup name is provided...
        if ( pomFile.exists() && pomBackup != null )
        {
            // ...rename the current existing file to the backup one.
            File pomBackupFile = new File( pomBackup );
            pomFile.renameTo( pomBackupFile );
        }

        // Save the model
        MavenXpp3Writer xpp3writer = new MavenXpp3Writer();
        FileWriter writer = new FileWriter( pomFile );

        xpp3writer.write( writer, model );

        writer.close();
    }
}
