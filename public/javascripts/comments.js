console.log('This is comments js loaded only in comments page')

function adjustFormsVisibility() {
  if (getCurrentUser()) {
    $('#loginForm').hide();
    $('#messageForm').show();
  } else {
    $('#loginForm').show();
    $('#messageForm').hide();
  }
}

function init() {
  adjustFormsVisibility();
  $('#loginButton').click(login);
  $('#logoutButton').click(logout);
}

function getCurrentUser() {
  return localStorage.getItem("currentUser");
}

function login() {
  var username = $('#usernameInput').val();
  localStorage.setItem('currentUser', username);
  adjustFormsVisibility();
}

function logout() {
  localStorage.removeItem('currentUser');
  adjustFormsVisibility();
}
init();
