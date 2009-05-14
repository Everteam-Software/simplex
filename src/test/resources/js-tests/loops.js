describe 'Forall loop'
    // Testing SimPEL issue #18 with a forall counter variable
    // already defined.
    describe 'with a predefined variable named like counter'
        it "shouldn't fault"
            var response = request(ROOT + "/forall-counter-predef", "POST", <start>start</start>);

            response.status.should.eql 201
            response.payload.toString().should.match /ok/
        end
    end
end

describe 'While loop'
    it 'should loop based on an increment'
        var response = request(ROOT + "/basic-while", "POST", <counter>10</counter>);
        print(".. "+response.payload.toString());
        parseInt(response.payload.toString()).should.eql 55
    end
end


/*
JSpec.describe('Forall loop', function() {
    describe('with a predefined variable named like counter', function() {
        it("should work", function() {
            var response = request(ROOT + "/forallcounterpredef", "POST", <start>start</start>);
            expect(response.status).to(be, 201);
            expect(response.payload.toString()).to(eql, "ok");
        });
    })
});
*/
