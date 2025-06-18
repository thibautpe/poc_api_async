var stats = {
    type: "GROUP",
name: "All Requests",
path: "",
pathFormatted: "group_missing-name-b06d1",
stats: {
    "name": "All Requests",
    "numberOfRequests": {
        "total": "23",
        "ok": "11",
        "ko": "12"
    },
    "minResponseTime": {
        "total": "3727",
        "ok": "3727",
        "ko": "4007"
    },
    "maxResponseTime": {
        "total": "4145",
        "ok": "4000",
        "ko": "4145"
    },
    "meanResponseTime": {
        "total": "3958",
        "ok": "3890",
        "ko": "4021"
    },
    "standardDeviation": {
        "total": "96",
        "ok": "95",
        "ko": "37"
    },
    "percentiles1": {
        "total": "4007",
        "ok": "3916",
        "ko": "4010"
    },
    "percentiles2": {
        "total": "4010",
        "ok": "3978",
        "ko": "4010"
    },
    "percentiles3": {
        "total": "4013",
        "ok": "3990",
        "ko": "4072"
    },
    "percentiles4": {
        "total": "4116",
        "ok": "3998",
        "ko": "4130"
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
    "count": 11,
    "percentage": 48
},
    "group4": {
    "name": "failed",
    "htmlName": "failed",
    "count": 12,
    "percentage": 52
},
    "meanNumberOfRequestsPerSecond": {
        "total": "0.793",
        "ok": "0.379",
        "ko": "0.414"
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
        "ok": "11",
        "ko": "12"
    },
    "minResponseTime": {
        "total": "3727",
        "ok": "3727",
        "ko": "4007"
    },
    "maxResponseTime": {
        "total": "4145",
        "ok": "4000",
        "ko": "4145"
    },
    "meanResponseTime": {
        "total": "3958",
        "ok": "3890",
        "ko": "4021"
    },
    "standardDeviation": {
        "total": "96",
        "ok": "95",
        "ko": "37"
    },
    "percentiles1": {
        "total": "4007",
        "ok": "3916",
        "ko": "4010"
    },
    "percentiles2": {
        "total": "4010",
        "ok": "3978",
        "ko": "4010"
    },
    "percentiles3": {
        "total": "4013",
        "ok": "3990",
        "ko": "4072"
    },
    "percentiles4": {
        "total": "4116",
        "ok": "3998",
        "ko": "4130"
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
    "count": 11,
    "percentage": 48
},
    "group4": {
    "name": "failed",
    "htmlName": "failed",
    "count": 12,
    "percentage": 52
},
    "meanNumberOfRequestsPerSecond": {
        "total": "0.793",
        "ok": "0.379",
        "ko": "0.414"
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
