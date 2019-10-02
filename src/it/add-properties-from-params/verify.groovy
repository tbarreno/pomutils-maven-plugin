//
// Groovy script for a 'quick-and-dirty' integration test
//

def pom = new XmlSlurper().parse("target/it/add-properties-from-params/alternate-pom.xml") 

// A property to watch (dirty check)
property_name = "server.hostname"
property_value = "a1b2c3.example.com"

assert pom instanceof groovy.util.slurpersupport.GPathResult

println("-- Current properties --")

// Loop over the dependencies
property_found = false
 
pom.properties.children().each { property ->

  println("  " + property.name() + " : " + property.text())
  
  if ( property.name() == property_name && property.text() == property_value ) {
    property_found = true
  }
}

assert property_found

println("--")
