// Testing SimPEL issue #18 with a forall counter variable
// already defined.
load("../test-helper.js");

var response = request(ROOT + "/forallcounterpredef", "POST", <start>start</start>);
print(response);
print(typeof response.payload);
assert("Predefining the forall counter variable failed.", response.status == 201)
assert("Predefining the forall counter variable failed.", response.payload.text() == "ok")
