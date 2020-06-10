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

import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static info.bluespot.plugins.utils.ArtifactUtils.matches;

/**
 * Removes a dependency from the POM.
 *
 * <p>This goal looks for a matching dependency and removes it from the POM file.
 *
 * @since 1.0.0
 */
@Mojo(name = "delete-dependency", requiresProject = true, inheritByDefault = false)
public class DeleteDependency extends AbstractMojo {
  // PARAMETERS ............................................................

  /** Dependency <code>ArtifactId</code>. */
  @Parameter(property = "artifactId", required = false, readonly = true)
  private String artifactId;

  /** Dependency <code>GroupId</code>. */
  @Parameter(property = "groupId", required = true, readonly = true)
  private String groupId;

  /** Dependency version. */
  @Parameter(property = "version", required = false, readonly = true)
  private String version;

  /** Keeps a copy of the current POM file before modifying it. */
  @Parameter(property = "pomBackup", required = false, readonly = true)
  private String pomBackup;

  /** Specifies a POM file to modify. */
  @Parameter(property = "pomFile", required = false, readonly = true, defaultValue = "pom.xml")
  private String pomFile;

  /** Dependency <code>ModifyDependencyManagement</code>. */
  @Parameter(property = "modifyDependencyManagement", required = false)
  private Boolean modifyDependencyManagement;

  // METHODS ...............................................................

  /** Main goal method. */
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    // Just check if all the GAV is null
    if (artifactId == null && groupId == null && version == null) {
      throw new MojoExecutionException(
          "An ArtifactId, GroupId or Version parameter is needed for dependency deletion.");
    }

    getLog().info("Removing dependency: '" + groupId + ":" + artifactId + "'");

    // Loads the model
    Model model;
    try {
      model = POMUtils.loadModel(pomFile);
    } catch (IOException | XmlPullParserException e) {
      // Exception loading the model
      throw new MojoExecutionException("Error while loading the Maven project model:", e);
    }

    model = deleteDependency(model, groupId, artifactId, version, modifyDependencyManagement);

    // Save the model
    try {
      POMUtils.saveModel(model, pomFile, pomBackup);
    } catch (IOException e) {
      // Exception
      throw new MojoExecutionException("Error while writing the POM file:", e);
    }
  }

  private Model deleteDependency(
      Model model,
      String groupId,
      String artifactId,
      String version,
      Boolean modifyDependencyManagement) {
    if (modifyDependencyManagement != null && model.getDependencyManagement() != null) {

      DependencyManagement dependencyManagement = model.getDependencyManagement();
      List<Dependency> newDependencyManagementDependencyList = new ArrayList<>();

      for (Dependency dependency : dependencyManagement.getDependencies()) {
        // If a matching dependency is found, just skip it; if not, the dependency is added to the
        // new list.
        if (matches(dependency, groupId, artifactId, version)) {
          getLog().info("Dependency found in dependency management (removed).");
        } else {
          newDependencyManagementDependencyList.add(dependency);
        }
      }
      dependencyManagement.setDependencies(newDependencyManagementDependencyList);
      model.setDependencyManagement(dependencyManagement);
    }

    // Gets the dependency list and iterate over it
    List<Dependency> newDependencyList = new ArrayList<>();

    for (Dependency dependency : model.getDependencies()) {
      // If a matching dependency is found, just skip it; if not, the dependency
      // is added to the new list.
      if (matches(dependency, groupId, artifactId, version)) {
        getLog().info("Dependency found (removed).");
      } else {
        newDependencyList.add(dependency);
      }
    }

    // Set the new (filtered) dependency list
    model.setDependencies(newDependencyList);
    return model;
  }
}
