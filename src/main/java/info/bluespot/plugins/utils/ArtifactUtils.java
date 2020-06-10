package info.bluespot.plugins.utils;

import org.apache.maven.model.Dependency;

public class ArtifactUtils {

  /**
   * Auxiliary method for match a dependency with 'whatever' has been provided (the <code>groupId
   * </code>, the </code>artifactId</code>, both of them, all the GAV coordinates, etc.).
   *
   * @param dependency A dependency object from the POM.
   * @return 'true' if there's any match.
   */
  public static boolean matches(
      Dependency dependency, String groupId, String artifactId, String version) {
    // Look for all the matches
    return (matchesGroupId(dependency, groupId)
        && matchesArtifactId(dependency, artifactId)
        && matchesVersion(dependency, version));
  }

  /**
   * Auxiliary method for check a <code>GroupId</code> match.
   *
   * @param dependency A dependency object from the POM.
   * @return 'true' if the <code>GroupId</code> matches.
   */
  private static boolean matchesGroupId(Dependency dependency, String groupId) {
    // If the GroupId is not provided, it's OK (the match could be over the ArtifactId or the
    // version)
    if (groupId == null) {
      return (true);
    }

    if (groupId.equals(dependency.getGroupId())) {
      return (true);
    } else {
      return (false);
    }
  }

  /**
   * Auxiliary method for check a <code>ArtifactId</code> match.
   *
   * @param dependency A dependency object from the POM.
   * @return 'true' if the <code>ArtifactId</code> matches.
   */
  private static boolean matchesArtifactId(Dependency dependency, String artifactId) {
    // If the ArtifactId is not provided, it's OK (the match could be over the GroupId or the
    // version)
    if (artifactId == null) {
      return (true);
    }

    if (artifactId.equals(dependency.getArtifactId())) {
      return (true);
    } else {
      return (false);
    }
  }

  /**
   * Auxiliary method for check a <code>Version</code> match.
   *
   * @param dependency A dependency object from the POM.
   * @return 'true' if the <code>Version</code> matches.
   */
  private static boolean matchesVersion(Dependency dependency, String version) {
    // If the Version is not provided, it's OK
    if (version == null) {
      return (true);
    }

    if (version.equals(dependency.getVersion())) {
      return (true);
    } else {
      return (false);
    }
  }
}
