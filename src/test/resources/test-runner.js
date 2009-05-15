load('jspec.js');
load('test-helper.js');

JSpec
.exec('js-tests/loops.js')
.exec('js-tests/outcalls.js')
.run({ formatter : JSpec.formatters.Terminal })
.report();