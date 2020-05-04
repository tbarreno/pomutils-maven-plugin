//
// This Groovy script verifies that the children POM contains the its parent.
//

def pom = new XmlSlurper().parse("target/it/add-parent/child/pom.xml")

println("-- Values of the parent --")
println("     groupId : " + pom.parent.groupId)
println("  artifactId : " + pom.parent.artifactId)
println("     version : " + pom.parent.version)
println("relativePath : " + pom.parent.relativePath)
println("--")

assert pom instanceof groovy.util.slurpersupport.GPathResult

assert pom.parent.groupId == 'it.tests'
assert pom.parent.artifactId == 'add-a-parent'
assert pom.parent.version == '1.0-SNAPSHOT'
assert pom.parent.relativePath == '../'
