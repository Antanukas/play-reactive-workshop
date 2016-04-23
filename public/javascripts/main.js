window.prw = window.prw || {};
window.prw.apiCall = apiCall;

function apiCall(method, url, body, onSuccess) {
  var r = new XMLHttpRequest();
  r.open(method, url, true);
  r.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
  r.onreadystatechange = function () {
    if (r.readyState != 4 || r.status != 200) return;
    onSuccess(JSON.parse(r.responseText));
  };
  r.send(JSON.stringify(body));
}
