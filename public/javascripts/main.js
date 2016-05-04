window.prw = window.prw || {};
window.prw.apiCall = apiCall;

function apiCall(method, url, body, onSuccess) {
  var r = new XMLHttpRequest();
  r.open(method, url, true);
  r.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
  var deferred = $.Deferred();
  r.onreadystatechange = function () {
    if (r.readyState != 4 || r.status != 200) return;
    var result = onSuccess(JSON.parse(r.responseText));
    deferred.resolve(result);
  };
  r.send(JSON.stringify(body));
  return deferred;
}
