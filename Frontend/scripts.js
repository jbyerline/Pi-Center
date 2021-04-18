let myObj = {
  username: "pi",
  ipAddress: "byerline.me",
  port: 2223,
  password: "demo123"
};

function test() {
  var xhttp = new XMLHttpRequest();
  var request = "http://byerline.me:8081/stats/cpu";
  xhttp.open("POST", request);
  xhttp.setRequestHeader("Content-type", "application/json");
  xhttp.send(JSON.stringify(myObj));
  xhttp.onreadystatechange = function () {
    console.log(xhttp.responseText);
  }
}
