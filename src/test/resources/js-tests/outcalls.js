// Tests calls to the outside environment in SimPEL.

describe 'Javascript functions'
    it 'should work when returning strings'
        var response = request(ROOT + "/fn-return-str", "POST", <wrap>you</wrap>);
        response.payload.toString().should.eql "Howdy you. Yeeha!"
    end

    it "should work when manipulating returned complex elements"
        var response = request(ROOT + "/row-subelmt-length", "POST", <wrap>you</wrap>);
        response.payload.toString().should.eql "20"
    end
end
