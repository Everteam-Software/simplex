load('jspec.js');
load('test-helper.js');

JSpec
.exec('js-tests/loops.js')
.run({ formatter : JSpec.formatters.Terminal })
.report();