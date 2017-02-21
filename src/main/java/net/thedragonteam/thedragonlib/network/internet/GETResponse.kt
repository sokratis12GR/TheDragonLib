package net.thedragonteam.thedragonlib.network.internet

class GETResponse {

    var statuscode = 200
    var result = ""
    var target = ""
    var params = ""
    var useragent = ""

    constructor(code: Int) {

        statuscode = code
        result = ""
        target = ""
        params = ""
        useragent = ""

    }

    constructor(res: String) {

        statuscode = 200
        result = res
        target = ""
        params = ""
        useragent = ""

    }

    constructor(code: Int, res: String) {

        statuscode = code
        result = res
        target = ""
        params = ""
        useragent = ""

    }

    constructor(code: Int, res: String, tar: String) {

        statuscode = code
        result = res
        target = tar
        params = ""
        useragent = ""

    }

    constructor(code: Int, res: String, tar: String, par: String) {

        statuscode = code
        result = res
        target = tar
        params = par
        useragent = ""

    }

    constructor(code: Int, res: String, tar: String, par: String, agent: String) {

        statuscode = code
        result = res
        target = tar
        params = par
        useragent = agent

    }

}
