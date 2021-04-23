// "username": "pi",
// "ipAddress": "byerline.me",
// "port": 2223,
// "password": "demo123"

var userCredentials = {
  username: getUrlParameter("user"),
  ipAddress: getUrlParameter("ip"),
  port: getUrlParameter("port"),
  password: getUrlParameter("password")
};

if(userCredentials.username != null && userCredentials.ipAddress != null && userCredentials.port != null && userCredentials.password != null) {
  apiRequest(userCredentials, "cpu");
  cpuPolling(userCredentials, 0);
}

$("form[name='login']").validate({
  rules: {
    user: "required",
    ip: "required",
    port: "required",
    password: {
      required: true,
      minlength: 4
    }
  },
  messages: {
    user: "Please enter your username",
    ip: "Please enter machine's IP address",
    port: "Please enter a valid port number",
    password: {
      required: "Please provide a password",
      minlength: "Your password must be at least 4 characters long"
    }
  },
  submitHandler: function(form) {
    form.submit();
  }
});

// https://stackoverflow.com/questions/19491336/how-to-get-url-parameter-using-jquery-or-plain-javascript
function getUrlParameter(sParam) {
    var sPageURL = window.location.search.substring(1),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return typeof sParameterName[1] === undefined ? true : decodeURIComponent(sParameterName[1]);
        }
    }
    return null;
};

function fillPageData(requestType, response) {
  console.log(requestType);
  console.log(response);

  // if(requestType == "cpu") {
  //   $("#cpuInfo").text(response);
  // }

  // apiRequest(userCredentials, "cpu");
  // apiRequest(userCredentials, "storage");
  // apiRequest(userCredentials, "hardwareInfo");
  // apiRequest(userCredentials, "process");
}

function apiRequest(credentials, requestType) {
  var xhttp = new XMLHttpRequest();
  var request = "http://byerline.me:8081/stats/" + requestType;
  xhttp.open("POST", request);
  xhttp.setRequestHeader("Content-type", "application/json");
  xhttp.send(JSON.stringify(credentials));

  xhttp.onload = function () {
    if (xhttp.readyState === xhttp.DONE) {
        if (xhttp.status === 200) {
            // console.log(xhttp.response);
            // console.log(xhttp.responseText);
            fillPageData(requestType, xhttp.responseText);
        }
    }
  };
}

function cpuPolling(credentials, count){
  setTimeout(() => {
    $.ajax({
      url: 'http://byerline.me:8081/stats/cpu',
      type: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      data: JSON.stringify(credentials),
      success: function(response) {
        // cpuArray[count++] = response.cpuTemp[0];
        // console.log(cpuArray);
        count++;
        addData(cpuChart, count, response.cpuTemp[0]);
        cpuPolling(credentials, count);
      }
    });
  }, 1000 * count);
}


// ---------- CHART JS -----------
var ctx = document.getElementById('cpuChart').getContext('2d');
var cpuChart = new Chart(ctx, {
    type: 'line',
    data: {
      labels: [0],
      datasets: [{
        data: [],
        label: "CPU Temp",
        borderColor: "#ECA72C",
        fill: false
      }]
    },
    options: {
      scales: {
          y: {
              ticks: {
                  // Include a dollar sign in the ticks
                  callback: function(value, index, values) {
                      return value + " f";
                  }
              }
          },
          x: {
              ticks: {
                  // Include a dollar sign in the ticks
                  callback: function(value, index, values) {
                      return value + " s";
                  }
              }
          }
      }
    }
});

function addData(chart, label, data) {
    chart.data.labels.push(label);
    chart.data.datasets.forEach((dataset) => {
        dataset.data.push(data);
    });
    chart.update();
}
