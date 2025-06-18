var stats = {
    type: "GROUP",
name: "All Requests",
path: "",
pathFormatted: "group_missing-name-b06d1",
stats: {
    "name": "All Requests",
    "numberOfRequests": {
        "total": "23",
        "ok": "14",
        "ko": "9"
    },
    "minResponseTime": {
        "total": "3606",
        "ok": "3606",
        "ko": "4008"
    },
    "maxResponseTime": {
        "total": "4014",
        "ok": "3991",
        "ko": "4014"
    },
    "meanResponseTime": {
        "total": "3911",
        "ok": "3847",
        "ko": "4011"
    },
    "standardDeviation": {
        "total": "134",
        "ok": "139",
        "ko": "2"
    },
    "percentiles1": {
        "total": "3973",
        "ok": "3903",
        "ko": "4011"
    },
    "percentiles2": {
        "total": "4010",
        "ok": "3970",
        "ko": "4012"
    },
    "percentiles3": {
        "total": "4012",
        "ok": "3980",
        "ko": "4013"
    },
    "percentiles4": {
        "total": "4014",
        "ok": "3989",
        "ko": "4014"
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
    "count": 14,
    "percentage": 61
},
    "group4": {
    "name": "failed",
    "htmlName": "failed",
    "count": 9,
    "percentage": 39
},
    "meanNumberOfRequestsPerSecond": {
        "total": "0.793",
        "ok": "0.483",
        "ko": "0.31"
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
        "ok": "14",
        "ko": "9"
    },
    "minResponseTime": {
        "total": "3606",
        "ok": "3606",
        "ko": "4008"
    },
    "maxResponseTime": {
        "total": "4014",
        "ok": "3991",
        "ko": "4014"
    },
    "meanResponseTime": {
        "total": "3911",
        "ok": "3847",
        "ko": "4011"
    },
    "standardDeviation": {
        "total": "134",
        "ok": "139",
        "ko": "2"
    },
    "percentiles1": {
        "total": "3973",
        "ok": "3903",
        "ko": "4011"
    },
    "percentiles2": {
        "total": "4010",
        "ok": "3970",
        "ko": "4012"
    },
    "percentiles3": {
        "total": "4012",
        "ok": "3980",
        "ko": "4013"
    },
    "percentiles4": {
        "total": "4014",
        "ok": "3989",
        "ko": "4014"
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
    "count": 14,
    "percentage": 61
},
    "group4": {
    "name": "failed",
    "htmlName": "failed",
    "count": 9,
    "percentage": 39
},
    "meanNumberOfRequestsPerSecond": {
        "total": "0.793",
        "ok": "0.483",
        "ko": "0.31"
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
