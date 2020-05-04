package info.bluespot.plugins;

import org.apache.maven.model.Parent;

import org.apache.maven.model.Model;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;

@Mojo( name = "add-parent", inheritByDefault = false )
public class AddParent extends AbstractMojo {

    // PARAMETERS ............................................................

    /**
     * Parents's <code>ArtifactId</code>.
     */
    @Parameter( property = "artifactId", required = true, readonly = true )
    private String artifactId;

    /**
     * Parents's <code>GroupId</code>.
     */
    @Parameter( property = "groupId", required = true, readonly = true )
    private String groupId;

    /**
     * Parents's version.
     */
    @Parameter( property = "version", required = true, readonly = true )
    private String version;

    /**
     * Parents's <code>RelativePath</code>.
     */
    @Parameter( property = "relativePath", readonly = true )
    private String relativePath;

    /**
     * Keeps a copy of the current POM file before modifying it.
     */
    @Parameter( property = "pomBackup", readonly = true )
    private String pomBackup;

    /**
     * Specifies a POM file to modify.
     */
    @Parameter( property = "pomFile", readonly = true, defaultValue = "pom.xml" )
    private String pomFile;

    @Override
    public void execute() throws MojoExecutionException {

        getLog().info( "Adding a parent reference to child " + groupId + ":" + artifactId + ":" + version );

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
        Parent parent = new Parent();

        parent.setArtifactId( artifactId );
        parent.setGroupId( groupId );
        parent.setVersion( version );

        if ( relativePath != null )
        {
            parent.setRelativePath( relativePath );
        }

        // Add the parent to the model
        model.setParent( parent );

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
