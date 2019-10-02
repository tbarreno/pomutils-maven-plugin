//
// Groovy script for a 'quick-and-dirty' integration test
//

def pom = new XmlSlurper().parse("target/it/delete-dependency/alternate-pom.xml") 

println("-- Current dependencies --")

assert pom instanceof groovy.util.slurpersupport.GPathResult

// Loop over the dependencies 
pom.dependencies.children().each { dependency ->
  println("    groupId : " + dependency.groupId)
  println(" artifactId : " + dependency.artifactId)
  println("    version : " + dependency.version)
  
  assert ! ((dependency.groupId == "com.example") && (dependency.artifactId == "brand-new-library"))
  
  println("--")
}

println("--")
