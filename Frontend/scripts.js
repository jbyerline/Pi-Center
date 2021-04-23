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

  apiRequest(userCredentials, "storage");
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

function apiRequest(credentials, requestType) {
  $.ajax({
    url: 'http://byerline.me:8081/stats/' + requestType,
    type: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    data: JSON.stringify(credentials),
    success: function(response) {
      if(requestType == "storage") {
        fillStorageData(response);
      }
    }
  });
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
        count++;
        addData(cpuChart, count, response.cpuTemp[0]);
        cpuPolling(credentials, count);
      }
    });
  }, 1000 * count);
}

function fillStorageData(data) {
  var nav = $("#storage-nav");
  var content = $("#storage-content");
  var navTemplate = $.trim($("#nav-item-template").html());
  var contentTemplate = $.trim($("#content-item-template").html());

  $.each(data, function(index, obj) {
    var x = navTemplate.replace(/{{link}}/ig, "#nav"+index);
    x = x.replace(/{{name}}/ig, obj.fileSystemName);

    var y = contentTemplate.replace(/{{link}}/ig, "nav"+index);
    y = y.replace(/{{use-percent}}/ig, obj.percentageUsed);
    y = y.replace(/{{name}}/ig, obj.fileSystemName);
    y = y.replace(/{{size}}/ig, obj.totalSize);
    y = y.replace(/{{space-used}}/ig, obj.amountUsed);
    y = y.replace(/{{space-available}}/ig, obj.amountAvailable);
    y = y.replace(/{{mount-path}}/ig, obj.mountPath);

    if(index == 0) {
      x = x.replace(/{{active}}/ig, " active");
      y = y.replace(/{{active}}/ig, " show active");
    }
    else {
      x = x.replace(/{{active}}/ig, "");
      y = y.replace(/{{active}}/ig, "");
    }

    nav.append(x);
    content.append(y);
  });
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
