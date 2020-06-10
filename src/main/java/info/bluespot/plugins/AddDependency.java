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
 * Adds a dependency to a POM file.
 *
 * <p>The <code>artifactId</code> and the <code>groupId</code> of the dependency are needed
 * parameters.
 *
 * @since 1.0.0
 */
@Mojo(name = "add-dependency", requiresProject = true, inheritByDefault = false)
public class AddDependency extends AbstractMojo {
  // PARAMETERS ............................................................

  /** Dependency's <code>ArtifactId</code>. */
  @Parameter(property = "artifactId", required = true, readonly = true)
  private String artifactId;

  /** Dependency's <code>GroupId</code>. */
  @Parameter(property = "groupId", required = true, readonly = true)
  private String groupId;

  /** Dependency's version (optional). */
  @Parameter(property = "version", required = false, readonly = true)
  private String version;

  /** Dependency's <code>systemPath</code>. */
  @Parameter(property = "systemPath", required = false, readonly = true)
  private String systemPath;

  /** Dependency's <code>type</code>. */
  @Parameter(property = "type", required = false, readonly = true)
  private String type;

  /** Dependency's <code>scope</code>. */
  @Parameter(property = "scope", required = false, readonly = true)
  private String scope;

  /** Dependency's <code>optional</code> field. */
  @Parameter(property = "optional", required = false, readonly = true, defaultValue = "false")
  private Boolean optional;

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
    getLog().info("Adding the dependency " + groupId + ":" + artifactId);

    // Load the model
    Model model;
    try {
      model = POMUtils.loadModel(pomFile);
    } catch (IOException | XmlPullParserException e) {
      throw new MojoExecutionException("Error while loading the Maven model.", e);
    }

    // Creating a dependency object
    Dependency dependency = new Dependency();

    dependency.setArtifactId(artifactId);
    dependency.setGroupId(groupId);
    if (version != null) {
      dependency.setVersion(version);
    }
    if (systemPath != null) {
      dependency.setSystemPath(systemPath);
    }
    if (type != null) {
      dependency.setType(type);
    }
    if (scope != null) {
      dependency.setScope(scope);
    }
    if (optional) {
      dependency.setOptional(true);
    }

    model =
        addDependency(model, dependency, groupId, artifactId, version, modifyDependencyManagement);

    // Save the model
    try {
      POMUtils.saveModel(model, pomFile, pomBackup);
    } catch (IOException e) {
      throw new MojoExecutionException("I/O error while writing the Maven model.", e);
    }
  }

  private Model addDependency(
      Model model,
      Dependency dependency,
      String groupId,
      String artifactId,
      String version,
      Boolean modifyDependencyManagement) {
    if (modifyDependencyManagement != null) {
      if (model.getDependencyManagement() != null) {

        DependencyManagement dependencyManagement = model.getDependencyManagement();
        List<Dependency> newDependencyManagementDependencyList = new ArrayList<>();

        for (Dependency dependencyTemp : dependencyManagement.getDependencies()) {
          // If a matching dependency is found, remove it; if not, the dependency is added to the
          // new list.
          if (matches(dependencyTemp, groupId, artifactId, version)) {
            getLog().info("Duplicated dependency found in dependency management.");
            newDependencyManagementDependencyList.remove(dependencyTemp);
          } else {
            newDependencyManagementDependencyList.add(dependencyTemp);
          }
        }

        getLog().info("Add requesting dependency to dependency management.");
        newDependencyManagementDependencyList.add(dependency);

        dependencyManagement.setDependencies(newDependencyManagementDependencyList);
        model.setDependencyManagement(dependencyManagement);
      } else {
        DependencyManagement dependencyManagement = new DependencyManagement();
        dependencyManagement.addDependency(dependency);
        model.setDependencyManagement(dependencyManagement);
      }
    }

    List<Dependency> newDependencyList = new ArrayList<>();

    for (Dependency dependencyTemp : model.getDependencies()) {
      // If a matching dependency is found, remove it; if not, the dependency is added to the new
      // list.
      if (matches(dependencyTemp, groupId, artifactId, version)) {
        getLog().info("Duplicated dependency found in dependencies.");
        newDependencyList.remove(dependencyTemp);
      } else {
        newDependencyList.add(dependencyTemp);
      }
    }

    getLog().info("Add requesting dependency to dependencies.");
    newDependencyList.add(dependency);

    model.setDependencies(newDependencyList);
    return model;
  }
}
