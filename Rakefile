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
VERSION_NUMBER = "0.1-SNAPSHOT"
NEXT_VERSION = "0.1"

ANTLR               = "org.antlr:antlr:jar:3.0.1"
ANTLR_TEMPLATE      = "org.antlr:stringtemplate:jar:3.0"
ASM                 = "asm:asm:jar:3.1"
COMMONS             = struct(
  :collections      =>"commons-collections:commons-collections:jar:3.1",
  :lang             =>"commons-lang:commons-lang:jar:2.1",
  :logging          =>"commons-logging:commons-logging:jar:1.1",
  :primitives       =>"commons-primitives:commons-primitives:jar:1.0"
)
DERBY               = "org.apache.derby:derby:jar:10.4.1.3"
GERONIMO            = struct(
  :kernel           =>"org.apache.geronimo.modules:geronimo-kernel:jar:2.0.1",
  :transaction      =>"org.apache.geronimo.components:geronimo-transaction:jar:2.0.1",
  :connector        =>"org.apache.geronimo.components:geronimo-connector:jar:2.0.1"
)
HSQLDB              = "hsqldb:hsqldb:jar:1.8.0.7"
JAVAX               = struct(
  :transaction      =>"org.apache.geronimo.specs:geronimo-jta_1.1_spec:jar:1.1",
  :resource         =>"org.apache.geronimo.specs:geronimo-j2ee-connector_1.5_spec:jar:1.0",
  :persistence      =>"javax.persistence:persistence-api:jar:1.0",
  :rest             =>"javax.ws.rs:jsr311-api:jar:1.0"
)
JERSEY              = group("jersey-server", "jersey-client", "jersey-core", :under=>"com.sun.jersey", :version=>"1.0.1")
JETTY               = group("jetty", "jetty-util", "servlet-api-2.5", :under=>"org.mortbay.jetty", :version=>"6.1.11")
LOG4J               = "log4j:log4j:jar:1.2.15"
ODE                 = group("ode-bpel-api", "ode-bpel-compiler", "ode-bpel-dao", "ode-dao-jpa", "ode-runtimes", 
                            "ode-engine", "ode-il-common", "ode-jacob", "ode-scheduler-simple", 
                            "ode-utils", :under=>"org.apache.ode", :version=>"1.3-SNAPSHOT")
OPENJPA             = ["org.apache.openjpa:openjpa:jar:1.1.0",
                       "net.sourceforge.serp:serp:jar:1.13.1"]
TRANQL              = [ "tranql:tranql-connector:jar:1.1", "axion:axion:jar:1.0-M3-dev", COMMONS.primitives ]
WSDL4J              = "wsdl4j:wsdl4j:jar:1.6.2"
XERCES              = "xerces:xercesImpl:jar:2.8.1"

repositories.remote << "http://repo1.maven.org/maven2"
repositories.remote << "http://download.java.net/maven/2"

desc "ODE SimPEL process execution language."
define "simpel" do
  project.version = VERSION_NUMBER
  project.group = "org.apache.ode"

  compile.options.source = "1.5"
  compile.options.target = "1.5"
  manifest["Implementation-Vendor"] = "Apache Software Foundation"
  meta_inf << file("NOTICE")

  pkg_name = "org.apache.ode.simpel.antlr"
  local_libs = file(_("etc/lib/e4x-grammar-0.2.jar")), file(_("etc/lib/rhino-1.7R2pre-patched.jar"))

  define 'lang' do
    
    antlr_task = antlr([_("src/main/antlr/org/apache/ode/simpel/antlr/SimPEL.g"), 
                        _("src/main/antlr/org/apache/ode/simpel/antlr/SimPELWalker.g")], 
                         {:in_package=>pkg_name, :token=>pkg_name})

    # Because of a pending ANTLR bug, we need to insert some additional 
    # code in generated classes.
    task('tweak_antlr' => [antlr_task]) do
      walker = _("target/generated/antlr/org/apache/ode/simpel/antlr/SimPELWalker.java")
      walker_txt = File.read(walker)

      patch_walker = lambda do |regx, offset, txt|
        insrt_idx = 0
        while (insrt_idx = walker_txt.index(regx, insrt_idx+1)) do
          walker_txt.insert(insrt_idx + offset, txt)
        end
      end
      patch_walker[/SimPELWalker.g(.*) \( path_expr \)$/, 37, "lv = (LinkedListTree)input.LT(1);"]
      patch_walker[/SimPELWalker.g(.*) \( rvalue \)$/, 34, "rv = (LinkedListTree)input.LT(1);"]
      patch_walker[/SimPELWalker.g(.*) \( expr \)$/, 34, "e = (LinkedListTree)input.LT(1);"]

      File.open(walker, 'w') { |f| f << walker_txt }
    end

    compile.from antlr_task
    compile.enhance([task('tweak_antlr')])
    compile.with HSQLDB, JAVAX.resource, JAVAX.transaction, COMMONS.lang, COMMONS.logging,
      ODE, LOG4J, WSDL4J, ASM, JERSEY, JAVAX.rest, JETTY, GERONIMO.transaction, XERCES,
      ANTLR, ANTLR_TEMPLATE, local_libs
    test.using :fork => :each
    test.exclude 'SingleshotTest'
    package :jar
  end

  define 'server' do
    compile.with projects("lang"), ODE, 
      LOG4J, JAVAX.transaction
    test.with JAVAX.resource, COMMONS.lang, COMMONS.logging, LOG4J, WSDL4J, ASM, JERSEY, 
      DERBY, TRANQL, OPENJPA, GERONIMO.transaction, GERONIMO.connector, JAVAX.persistence,
      JAVAX.rest, JETTY, XERCES, ANTLR, ANTLR_TEMPLATE, local_libs, COMMONS.collections, 
      local_libs
    package :jar
  end

end

define 'distro' do
  [:version, :group, :manifest, :meta_inf].each { |prop| send "#{prop}=", project("simpel").send(prop) }
  
  local_libs = file(_("etc/lib/e4x-grammar-0.2.jar")), file(_("etc/lib/rhino-1.7R2pre-patched.jar"))

  package(:zip, 'apache-simplex').tap do |zip|
      zip.path('lib').include artifacts(ODE, LOG4J, JAVAX.transaction, JAVAX.resource,
        COMMONS.lang, COMMONS.logging, LOG4J, WSDL4J, ASM, JERSEY, DERBY, TRANQL, OPENJPA, 
        GERONIMO.transaction, GERONIMO.connector, JAVAX.persistence, JAVAX.rest, JETTY, 
        XERCES, ANTLR, ANTLR_TEMPLATE, local_libs, COMMONS.collections, local_libs)

      project('simpel').projects('lang', 'server').map(&:packages).flatten.each do |pkg|
        zip.include(pkg.to_s, :as=>"#{pkg.id}.#{pkg.type}", :path=>'lib')
      end

      zip.path('bin').include _('etc/bin/run'), _('etc/bin/run.bat')
      zip.path('log').include _('etc/log/log4j.properties')
  end
end
