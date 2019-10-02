//
// Groovy script for a 'quick-and-dirty' integration test
//

def pom = new XmlSlurper().parse("target/it/modify-project/alternate-pom.xml") 

println("-- Project information --")
println("    groupId  : " + pom.groupId)
println(" artifactId  : " + pom.artifactId)
println("    version  : " + pom.version)
println(" description : " + pom.description)
println("--")

assert pom instanceof groovy.util.slurpersupport.GPathResult
 
assert pom.groupId == 'com.example.testing'
assert pom.artifactId == 'brand-new-project'
assert pom.version == '1.2.3-BETA'
assert pom.description == 'Build 20191002-BETA'
