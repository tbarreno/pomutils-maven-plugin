//
// Groovy script for a 'quick-and-dirty' integration test
//

def pom = new XmlSlurper().parse("target/it/add-properties-from-file/alternate-pom.xml") 

// A property to watch (dirty check)
property_name = "dev.server.hostname"
property_value = "a1b2c3.example.com"

assert pom instanceof groovy.util.slurpersupport.GPathResult

println("-- Current properties in the 'deployment' profile --")

// Loop over the dependencies
property_found = false
 
pom.profiles.children().find{ profile -> profile.id.text() == "deployment" }.properties.children().each { property ->

  println("  " + property.name() + " : " + property.text())
  
  if ( property.name() == property_name && property.text() == property_value ) {
    property_found = true
  }
}

assert property_found

println("--")
