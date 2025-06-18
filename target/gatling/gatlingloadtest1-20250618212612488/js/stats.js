var stats = {
    type: "GROUP",
name: "All Requests",
path: "",
pathFormatted: "group_missing-name-b06d1",
stats: {
    "name": "All Requests",
    "numberOfRequests": {
        "total": "23",
        "ok": "17",
        "ko": "6"
    },
    "minResponseTime": {
        "total": "3628",
        "ok": "3628",
        "ko": "4009"
    },
    "maxResponseTime": {
        "total": "4084",
        "ok": "4084",
        "ko": "4029"
    },
    "meanResponseTime": {
        "total": "3926",
        "ok": "3895",
        "ko": "4015"
    },
    "standardDeviation": {
        "total": "115",
        "ok": "118",
        "ko": "8"
    },
    "percentiles1": {
        "total": "3956",
        "ok": "3945",
        "ko": "4011"
    },
    "percentiles2": {
        "total": "4009",
        "ok": "3981",
        "ko": "4019"
    },
    "percentiles3": {
        "total": "4028",
        "ok": "4033",
        "ko": "4027"
    },
    "percentiles4": {
        "total": "4072",
        "ok": "4074",
        "ko": "4029"
    },
    "group1": {
    "name": "t < 800 ms",
    "htmlName": "t < 800 ms",
    "count": 0,
    "percentage": 0
},
    "group2": {
    "name": "800 ms <= t < 1200 ms",
    "htmlName": "t >= 800 ms <br> t < 1200 ms",
    "count": 0,
    "percentage": 0
},
    "group3": {
    "name": "t >= 1200 ms",
    "htmlName": "t >= 1200 ms",
    "count": 17,
    "percentage": 74
},
    "group4": {
    "name": "failed",
    "htmlName": "failed",
    "count": 6,
    "percentage": 26
},
    "meanNumberOfRequestsPerSecond": {
        "total": "0.793",
        "ok": "0.586",
        "ko": "0.207"
    }
},
contents: {
"req_handle-request-33e49": {
        type: "REQUEST",
        name: "Handle Request",
path: "Handle Request",
pathFormatted: "req_handle-request-33e49",
stats: {
    "name": "Handle Request",
    "numberOfRequests": {
        "total": "23",
        "ok": "17",
        "ko": "6"
    },
    "minResponseTime": {
        "total": "3628",
        "ok": "3628",
        "ko": "4009"
    },
    "maxResponseTime": {
        "total": "4084",
        "ok": "4084",
        "ko": "4029"
    },
    "meanResponseTime": {
        "total": "3926",
        "ok": "3895",
        "ko": "4015"
    },
    "standardDeviation": {
        "total": "115",
        "ok": "118",
        "ko": "8"
    },
    "percentiles1": {
        "total": "3956",
        "ok": "3945",
        "ko": "4011"
    },
    "percentiles2": {
        "total": "4009",
        "ok": "3981",
        "ko": "4019"
    },
    "percentiles3": {
        "total": "4028",
        "ok": "4033",
        "ko": "4027"
    },
    "percentiles4": {
        "total": "4072",
        "ok": "4074",
        "ko": "4029"
    },
    "group1": {
    "name": "t < 800 ms",
    "htmlName": "t < 800 ms",
    "count": 0,
    "percentage": 0
},
    "group2": {
    "name": "800 ms <= t < 1200 ms",
    "htmlName": "t >= 800 ms <br> t < 1200 ms",
    "count": 0,
    "percentage": 0
},
    "group3": {
    "name": "t >= 1200 ms",
    "htmlName": "t >= 1200 ms",
    "count": 17,
    "percentage": 74
},
    "group4": {
    "name": "failed",
    "htmlName": "failed",
    "count": 6,
    "percentage": 26
},
    "meanNumberOfRequestsPerSecond": {
        "total": "0.793",
        "ok": "0.586",
        "ko": "0.207"
    }
}
    }
}

}

function fillStats(stat){
    $("#numberOfRequests").append(stat.numberOfRequests.total);
    $("#numberOfRequestsOK").append(stat.numberOfRequests.ok);
    $("#numberOfRequestsKO").append(stat.numberOfRequests.ko);

    $("#minResponseTime").append(stat.minResponseTime.total);
    $("#minResponseTimeOK").append(stat.minResponseTime.ok);
    $("#minResponseTimeKO").append(stat.minResponseTime.ko);

    $("#maxResponseTime").append(stat.maxResponseTime.total);
    $("#maxResponseTimeOK").append(stat.maxResponseTime.ok);
    $("#maxResponseTimeKO").append(stat.maxResponseTime.ko);

    $("#meanResponseTime").append(stat.meanResponseTime.total);
    $("#meanResponseTimeOK").append(stat.meanResponseTime.ok);
    $("#meanResponseTimeKO").append(stat.meanResponseTime.ko);

    $("#standardDeviation").append(stat.standardDeviation.total);
    $("#standardDeviationOK").append(stat.standardDeviation.ok);
    $("#standardDeviationKO").append(stat.standardDeviation.ko);

    $("#percentiles1").append(stat.percentiles1.total);
    $("#percentiles1OK").append(stat.percentiles1.ok);
    $("#percentiles1KO").append(stat.percentiles1.ko);

    $("#percentiles2").append(stat.percentiles2.total);
    $("#percentiles2OK").append(stat.percentiles2.ok);
    $("#percentiles2KO").append(stat.percentiles2.ko);

    $("#percentiles3").append(stat.percentiles3.total);
    $("#percentiles3OK").append(stat.percentiles3.ok);
    $("#percentiles3KO").append(stat.percentiles3.ko);

    $("#percentiles4").append(stat.percentiles4.total);
    $("#percentiles4OK").append(stat.percentiles4.ok);
    $("#percentiles4KO").append(stat.percentiles4.ko);

    $("#meanNumberOfRequestsPerSecond").append(stat.meanNumberOfRequestsPerSecond.total);
    $("#meanNumberOfRequestsPerSecondOK").append(stat.meanNumberOfRequestsPerSecond.ok);
    $("#meanNumberOfRequestsPerSecondKO").append(stat.meanNumberOfRequestsPerSecond.ko);
}
