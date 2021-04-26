// "username": "pi",
// "ipAddress": "byerline.me",
// "port": 2223,
// "password": "demo123"

// DOM Ready
$(function() {
  var userCredentials = {
    username: getUrlParameter("user"),
    ipAddress: getUrlParameter("ip"),
    port: getUrlParameter("port"),
    password: getUrlParameter("password")
  };

  if(userCredentials.username != null && userCredentials.ipAddress != null && userCredentials.port != null && userCredentials.password != null) {
    apiRequest(userCredentials, "storage");
    apiRequest(userCredentials, "hardwareInfo");

    // apiRequest(userCredentials, "cpu");
    cpuPolling(userCredentials, 0);
  }

  $( "#run-command" ).click(function() {
    var input = $("#command-input").val();
    userCredentials.commands = [input];

    // console.log(userCredentials);
    apiRequest(userCredentials, "command");
  });

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

  function apiRequest(credentials, requestType) {
    $.ajax({
      url: 'http://byerline.me:8081/stats/' + requestType,
      type: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      data: JSON.stringify(credentials),
      success: function(response) {
        // console.log(response);
        if(requestType == "storage") {
          fillStorageData(response);
        }
        if(requestType == "hardwareInfo") {
          fillHardwareData(response.list.node);
        }
        if(requestType == "command") {
          $("#command-response").html(response);
        }
      },
      error: function(xhr, status, error) {
        var err = eval("(" + xhr.responseText + ")");
        alert(err.Message);
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

  function fillHardwareData(data) {
    var content = $.trim($("#hardware-container").html());

    content = content.replace(/{{name}}/ig, data.product);
    content = content.replace(/{{description}}/ig, data.description);
    content = content.replace(/{{serial}}/ig, data.serial);
    content = content.replace(/{{pretty-print}}/ig, JSON.stringify(data, null, 2));

    $("#hardware-container").html(content);

    // Highlight syntax with highlight.js
    hljs.highlightAll();
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
                    callback: function(value, index, values) {
                        return value + " f";
                    }
                }
            },
            x: {
                ticks: {
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

  // -------- EXTERNAL METHODS --------
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
});
