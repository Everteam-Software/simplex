// Tests calls to the outside environment in SimPEL.

describe 'Javascript functions'
    it 'should work when returning strings'
        var response = request(ROOT + "/fn-return-str", "POST", <wrap>you</wrap>);
        response.payload.toString().should.eql "Howdy you. Yeeha!"
    end
end
