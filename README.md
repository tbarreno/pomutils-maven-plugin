# pomutils-maven-plugin

This [Maven](https://maven.apache.org/) plugin contains goals for project's
POM modification:

- Adding and deleting dependencies
- Adding or modifying properties
- Modifying the project information (ArfifactID, GroupID, etc.)

## Purpose

This plugin allows DevOps teams to alter some project aspects using a
standard Maven execution. It could be useful in some build pipelines
for testing or parallel target environments.

## Usage

There're two common parameters for all goals:

- `pomFile`: Sets the POM file to modify (by default this plugin takes the
  existing '`pom.xml`' file in the current directory).
- `pomBackup` : Sets a file to create a backup file before the modifications.


### Adding a dependency

The parameters for adding a dependency to the POM are the same from a
`dependency` node on the POM file:

- `groupId`
- `artifactId`
- `version` (optional)
- `type` (optional, default: 'jar')
- `scope` (optional)
- `systemPath` (optional)
- `classifier` (optional)
- `optional` (optional)

This goal just adds a new dependency:

```bash
$ mvn info.bluespot:pomutils-maven-plugin:1.0.0:add-dependency -DgroupId=com.example -DartifactId=my-library -Dversion=1.0.0 -Dscope=provided
```

The output just shows the library information: 

```bash
[INFO] Scanning for projects...
[INFO] 
[INFO] ----------------------< it.tests:add-a-dependecy >----------------------
[INFO] Building add-a-dependecy 1.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- pomutils-maven-plugin:1.0.0:add-dependency (default-cli) @ add-dependency ---
[INFO] Adding the dependency com.example:my-library
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 0.567 s
[INFO] Finished at: 2019-10-02T14:49:10+02:00
[INFO] ------------------------------------------------------------------------
```

And the resulting file:

```xml
<project>
...
  <dependencies>
    <dependency>
      <groupId>com.example</groupId>
      <artifactId>my-library</artifactId>
      <version>1.0.0</version>
      <scope>provided</scope>
    </dependency>
  </dependencies>
...
</project>
```

### Deleting a dependency

This goal removes a `dependency` entry from the POM. The parameter are: 

- `groupId`
- `artifactId`
- `version` (optional)

For example:

```bash
$ mvn info.bluespot:pomutils-maven-plugin:1.0.0:delete-dependency -DgroupId=com.example -DartifactId=my-library
```

The output is:

```bash
[INFO] Scanning for projects...
[INFO] 
[INFO] ----------------------< it.tests:add-a-dependecy >----------------------
[INFO] Building add-a-dependecy 1.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- pomutils-maven-plugin:1.0.0:delete-dependency (default-cli) @ delete-dependecy ---
[INFO] Removing dependency: 'com.example:my-library'
[INFO] Dependency found (removed).
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 0.581 s
[INFO] Finished at: 2019-10-02T15:10:29+02:00
[INFO] ------------------------------------------------------------------------
```

### Adding properties

Properties can be added from a standard Java properties files or from the command line. You can
also specify the `profile` to put properties on. The parameters are:

- `properties`: A comma separated 'key-value' map
- `propertiesFile`: A file with the properties
- `propertiesEncoding`: The properties file encoding (default UTF-8)

Properties are replaced if they previously exists.

For example:

```bash
$ mvn info.bluespot:pomutils-maven-plugin:1.0.0:add-properties -Dproperties=server.hostname=abc.example.com,server.port=8080,server.name="My server",server.env=dev
```

Show this output:

```bash
[INFO] Scanning for projects...
[INFO] 
[INFO] ----------------------< it.tests:add-a-dependecy >----------------------
[INFO] Building add-a-dependecy 1.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- pomutils-maven-plugin:1.0.0:add-properties (default-cli) @ add-properties ---
[INFO] Adding general properties to the model.
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 0.569 s
[INFO] Finished at: 2019-10-02T15:20:47+02:00
[INFO] ------------------------------------------------------------------------
```

And the resulting POM:

```xml
<project>
...
  <properties>
    <server.port>8080</server.port>
    <server.name>My server</server.name>
    <server.hostname>abc.example.com</server.hostname>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <server.env>dev</server.env>
  </properties>
...
</project>
```

You can change a value by selecting an existing property:

```bash
$ mvn info.bluespot:pomutils-maven-plugin:1.0.0:add-properties -Dproperties=server.env=PRODUCTION
```

```bash
```xml
<project>
...
  <properties>
    <server.port>8080</server.port>
    <server.name>My server</server.name>
    <server.hostname>abc.example.com</server.hostname>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <server.env>PRODUCTION</server.env>
  </properties>
...
</project>
```

### Modifying basic information about the project

The goal `modify-project` allows developers to refactor a project by changing
their basic properties:

- `artifactId`
- `groupId`
- `version`
- `name`
- `description`
- `url`

Note that this goal doesn't updates children modules at this moment: it will
broke multi-module projects if a parent POM's GAV is modified.

Use example:

```bash
$ mvn info.bluespot:pomutils-maven-plugin:1.0.0:modify-project -Ddescription="Build 20191002-001" -Dname="My Project"
```

```bash
[INFO] Scanning for projects...
[INFO] 
[INFO] ----------------------< it.tests:add-a-dependecy >----------------------
[INFO] Building add-a-dependecy 1.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- pomutils-maven-plugin:1.0.0:modify-project (default-cli) @ modify-project ---
[INFO] Setting the name to 'My Project'
[INFO] Setting the description to 'Build 20191002-001'
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 0.557 s
[INFO] Finished at: 2019-10-02T15:31:39+02:00
[INFO] ------------------------------------------------------------------------
```

The POM file will result:

```xml
<project>
...
  <groupId>it.tests</groupId>
  <artifactId>modify-project</artifactId>
  <version>1.0-SNAPSHOT</version>
  <name>My Project</name>
  <description>Build 20191002-001</description>
...
</project>
```

## LICENSE

This plugin is released under the [Apache Licence v2](https://www.apache.org/licenses/LICENSE-2.0).
