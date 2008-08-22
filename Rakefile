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
VERSION_NUMBER = "1.2-SNAPSHOT"
NEXT_VERSION = "1.2"

ANTLR   = "org.antlr:antlr:jar:3.0.1"
COMMONS             = struct(
  :logging          =>"commons-logging:commons-logging:jar:1.1",
  :lang             =>"commons-lang:commons-lang:jar:2.1"
)
GERONIMO            = struct(
  :kernel           =>"org.apache.geronimo.modules:geronimo-kernel:jar:2.0.1",
  :transaction      =>"org.apache.geronimo.components:geronimo-transaction:jar:2.0.1",
  :connector        =>"org.apache.geronimo.components:geronimo-connector:jar:2.0.1"
)
HSQLDB              = "hsqldb:hsqldb:jar:1.8.0.7"
JAVAX               = struct(
  :transaction      =>"org.apache.geronimo.specs:geronimo-jta_1.1_spec:jar:1.1",
  :resource         =>"org.apache.geronimo.specs:geronimo-j2ee-connector_1.5_spec:jar:1.0"
)
LOG4J               = "log4j:log4j:jar:1.2.15"
ODE                 = group("ode-bpel-api", "ode-bpel-compiler", "ode-bpel-dao", "ode-bpel-obj", 
                            "ode-bpel-runtime", "ode-il-common", "ode-jacob", "ode-scheduler-simple", 
                            "ode-utils", :under=>"org.apache.ode", :version=>"1.3-SNAPSHOT")
WSDL4J              = "wsdl4j:wsdl4j:jar:1.6.2"
XERCES              = "xerces:xercesImpl:jar:2.9.0"

repositories.remote << "http://repo1.maven.org/maven2"
repositories.remote << "http://people.apache.org/~mriou/ode-1.2RC1/"

desc "ODE SimPEL process execution language."
define "simpel" do
  project.version = VERSION_NUMBER
  project.group = "org.apache.ode"

  compile.options.source = "1.5"
  compile.options.target = "1.5"
  manifest["Implementation-Vendor"] = "Apache Software Foundation"
  meta_inf << file("NOTICE")

  pkg_name = "org.apache.ode.simpel.antlr"
  antlr_task = antlr([_("src/main/antlr/org/apache/ode/simpel/antlr/SimPEL.g"), 
                             _("src/main/antlr/org/apache/ode/simpel/antlr/SimPELWalker.g")], 
                              {:in_package=>pkg_name, :token=>pkg_name})

  # Because of a pending ANTLR bug, we need to insert some additional 
  # code in generated classes.
  task('tweak_antlr' => [antlr_task]) do
    walker = _("target/generated/antlr/org/apache/ode/simpel/antlr/SimPELWalker.java")
    walker_txt = File.read(walker)

    patch_walker = lambda do |regx, offset, txt|
      insrt_idx = walker_txt.index(regx)
      walker_txt.insert(insrt_idx + offset, txt)
    end
    patch_walker[/SimPELWalker.g(.*)ns_id$/, 51, "ids = (LinkedListTree)input.LT(1);"]
    patch_walker[/SimPELWalker.g(.*) \( path_expr \)$/, 37, "lv = (LinkedListTree)input.LT(1);"]
    patch_walker[/SimPELWalker.g(.*) \( rvalue \)$/, 34, "rv = (LinkedListTree)input.LT(1);"]

    File.open(walker, 'w') { |f| f << walker_txt }
  end

  compile.from antlr_task
  compile.enhance([task('tweak_antlr')])
  compile.with HSQLDB, JAVAX.resource, JAVAX.transaction, COMMONS.lang, COMMONS.logging, ODE, LOG4J, 
    WSDL4J, GERONIMO.transaction, XERCES,
    file(_("lib/e4x-grammar-0.1.jar")), ANTLR, file(_("lib/rhino-1.7R2pre-patched.jar"))
  package :jar
end
