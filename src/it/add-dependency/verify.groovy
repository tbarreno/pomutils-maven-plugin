//
// This Groovy script verifies that the POM contains the
// desired dependency.
//

def pom = new XmlSlurper().parse("target/it/add-dependency/pom.xml") 

println("-- Values of the added dependency --")
println("    groupId : " + pom.dependencies.dependency.groupId)
println(" artifactId : " + pom.dependencies.dependency.artifactId)
println("    version : " + pom.dependencies.dependency.version)
println("      scope : " + pom.dependencies.dependency.scope)
println("--")

assert pom instanceof groovy.util.slurpersupport.GPathResult
 
assert pom.dependencies.dependency.groupId == 'com.example'
assert pom.dependencies.dependency.artifactId == 'brand-new-library'
assert pom.dependencies.dependency.version == '1.2.3'
assert pom.dependencies.dependency.scope == 'provided'
