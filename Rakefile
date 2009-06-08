#
#    Licensed to the Apache Software Foundation (ASF) under one or more
#    contributor license agreements.  See the NOTICE file distributed with
#    this work for additional information regarding copyright ownership.
#    The ASF licenses this file to You under the Apache License, Version 2.0
#    (the "License"); you may not use this file except in compliance with
#    the License.  You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS,
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    See the License for the specific language governing permissions and
#    limitations under the License.
#

gem "buildr", "~>1.3"
require "buildr"
require "buildr/antlr"

# Keep this structure to allow the build system to update version numbers.
VERSION_NUMBER = "0.2-SNAPSHOT"
NEXT_VERSION = "0.3"

ANTLR_RT            = "org.antlr:antlr-runtime:jar:3.1.1"
ASM                 = "asm:asm:jar:3.1"
COMMONS             = struct(
  :collections      =>"commons-collections:commons-collections:jar:3.1",
  :lang             =>"commons-lang:commons-lang:jar:2.1",
  :logging          =>"commons-logging:commons-logging:jar:1.1",
  :primitives       =>"commons-primitives:commons-primitives:jar:1.0"
)
DOM4J               = "dom4j:dom4j:jar:1.6.1"
H2                  = "com.h2database:h2:jar:1.1.111"
HIBERNATE           = [ "org.hibernate:hibernate:jar:3.2.5.ga",
                        "antlr:antlr:jar:2.7.6", "cglib:cglib-nodep:jar:2.1_3"]

JAVAX               = struct(
  :transaction      =>"org.apache.geronimo.specs:geronimo-jta_1.1_spec:jar:1.1",
#  :resource         =>"org.apache.geronimo.specs:geronimo-j2ee-connector_1.5_spec:jar:1.0",
  :persistence      =>"javax.persistence:persistence-api:jar:1.0",
  :rest             =>"javax.ws.rs:jsr311-api:jar:1.0"
)
JERSEY              = group("jersey-server", "jersey-client", "jersey-core", :under=>"com.sun.jersey", :version=>"1.0.1")
JETTY               = group("jetty", "jetty-util", "servlet-api-2.5", :under=>"org.mortbay.jetty", :version=>"6.1.11")
LOG4J               = "log4j:log4j:jar:1.2.15"
MYSQL               = "mysql:mysql-connector:jar:5.0.4"
ODE                 = group("ode-bpel-api", "ode-bpel-compiler", "ode-bpel-dao", "ode-dao-hibernate", 
                            "ode-runtimes", "ode-engine", "ode-il-common", "ode-jacob", 
                            "ode-scheduler-simple", "ode-utils", :under=>"org.apache.ode", :version=>"20090602")
OPENJPA             = ["org.apache.openjpa:openjpa:jar:1.2.1",
                       "net.sourceforge.serp:serp:jar:1.13.1"]
SINGLESHOT          = "com.intalio.singleshot:singleshot:war:20090608"
SIMPEL              = "com.intalio.simpel:simpel:jar:0.2"
SLF4J               = group(%w{ slf4j-api slf4j-log4j12 }, :under=>"org.slf4j", :version=>"1.4.3")
WSDL4J              = "wsdl4j:wsdl4j:jar:1.6.2"
XERCES              = "xerces:xercesImpl:jar:2.8.1"

repositories.remote << "http://repo1.maven.org/maven2"
repositories.remote << "http://download.java.net/maven/2"
repositories.remote << "http://www.intalio.org/public/maven2"

desc "Simplex process execution server, tightly tied to SimPEL."
define "simplex" do
  project.version = VERSION_NUMBER
  project.group = "com.intalio.simplex"

  compile.options.source = "1.5"
  compile.options.target = "1.5"
  manifest["Implementation-Vendor"] = "Intalio, Inc."
  meta_inf << file("NOTICE") << file("LICENSE")

  local_libs = file(_("lib/e4x-grammar-0.2.jar")), file(_("lib/rhino-1.7R2pre-patched.jar")),
    file(_("lib/btm-1.3.2.jar"))

  compile.with local_libs, SIMPEL, ODE, LOG4J, JAVAX.transaction, JERSEY, ASM, HIBERNATE, 
    JAVAX.rest, JAVAX.persistence, JETTY, H2, WSDL4J, COMMONS.logging

  test.with COMMONS.lang, COMMONS.logging, LOG4J, ODE, H2, DOM4J,
    HIBERNATE, JAVAX.persistence, SLF4J,
    XERCES, ANTLR_RT, local_libs, COMMONS.collections
  test.using :fork => :each
  package :jar

  package(:zip, :id=>'simplex-public-html').tap do |p|
    p.include _("src/main/public_html/")
  end

  zip_includes = lambda do |zip|
    zip.include meta_inf + ["README", "src/main/samples/"].map { |f| path_to(f) }

    zip.path('lib').include artifacts(SIMPEL, ODE, LOG4J, JAVAX.transaction,
      COMMONS.lang, COMMONS.logging, LOG4J, WSDL4J, JERSEY, HIBERNATE, H2, ASM,
      JAVAX.persistence, JAVAX.rest, JETTY, SLF4J, DOM4J,
      XERCES, ANTLR_RT, local_libs, COMMONS.collections, local_libs),
      package(:zip, :id=>'simplex-public-html')

    zip.path('conf').include _("src/test/resources/jndi.properties"), 
                             _("src/main/etc/bitronix-default-config.properties")

    packages.each do |pkg|
      unless pkg.id =~ /intalio/
        zip.include(pkg.to_s, :path=>'lib')
      end
    end

    zip.path('bin').include _('src/main/bin/run'), _('src/main/bin/run.bat')
    zip.path('log').include _('src/main/etc/log4j.properties'), _('src/main/etc/logging.properties')
  end

  package(:zip, :id=>'intalio-simplex').path("intalio-#{id}-#{version}").tap do |zip|
    zip_includes[zip]
    zip.path('conf').include _("src/main/etc/database.properties")
  end

  DE_VERSION = 0.1
  package(:zip, {:id=>'intalio-de', :version=>DE_VERSION}).path("intalio-de-#{DE_VERSION}").tap do |zip|
    zip_includes[zip]
    zip.path('conf').include(_("src/main/etc/database.mysql.properties"), :as =>'database.properties')
    zip.path('conf').include(_('target/mysql_schema.sql'))
    zip.path('lib').include artifacts(MYSQL)
    zip.path('webapps/singleshot').merge(artifacts(SINGLESHOT))
  end
  package(:zip, {:id=>'intalio-de', :version=>DE_VERSION}).enhance([task do
    File.open(_('target/mysql_schema.sql'), 'w') do |f| 
      f << File.read(_("src/main/etc/ode_schema.sql"))
      f << File.read(_("src/main/etc/singleshot_schema.sql"))
    end
  end])
end

