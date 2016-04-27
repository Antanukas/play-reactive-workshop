var repositoryId, repoOwner, repoName;

function init() {
  repoOwner = $('#repoOwner').text();
  repoName = $('#repoName').text();
  repositoryId = repoOwner + '/' + repoName;
  adjustFormsVisibility();
  $('#loginButton').click(login);
  $('#logoutButton').click(logout);
  window.prw.apiCall(
    "GET",
    '/api/repositories/' + repositoryId,
    null,
    function(repository) {
      $('#repositoryName').text(repository.name);
      $('#repositoryIssueCount').text(repository.openIssueCount);
    });
  window.prw.apiCall("GET",
    '/api/repositories/' + repositoryId + '/comments',
    null,
    function(comments) {
      updateCommentsList(comments);
      //Register SSE listener
      var commentSource = new EventSource('/api/repositories/' + repositoryId + '/comments');
      commentSource.onmessage = function(event) {
        var responseJson = JSON.parse(event.data);
        updateCommentsList(responseJson);
      };
    }
  );
  $('#sendMessageButton').click(postMessage);
}

function adjustFormsVisibility() {
  if (getCurrentUser()) {
    $('#loginForm').hide();
    $('#messageForm').show();
  } else {
    $('#loginForm').show();
    $('#messageForm').hide();
  }
}

function postMessage() {
  var userId = getCurrentUser().id;
  var text = $('#messageText').val();
  var reqBody = {
    userId: userId,
    gitHubId: {owner: repoOwner, name: repoName},
    text: text
  };
  window.prw.apiCall(
    "POST",
    '/api/repositories/' + repositoryId + '/comments',
    reqBody,
    function(response){
      $('#messageText').text('');
    }
  );
}

function updateCommentsList(comments) {
  $('#commentCount').text(comments.length);
  var renderedComments = comments.map(function(comment){
    return '<div class="media">' +
          '<p class="pull-right"><small>' + formatDate(new Date(comment.createdOn)) + '</small></p>' +
          '<div class="media-body">' +
            '<h4 class="media-heading user_name">' + comment.username + '</h4>' +
            '<p>' + comment.text + '</p>' +
          '</div>' +
        '</div>' +
       '</div>';
  });
  $('.comments-list').html(renderedComments);
}

function formatDate(date)  {
  //Best ever date formatting fn
  return date.toISOString().slice(0, 10) + ' '+ date.toString().slice(16, 24);
}
function getCurrentUser() {
  return JSON.parse(localStorage.getItem("currentUser"));
}

function login() {
  var username = $('#usernameInput').val();
  var reqBody = {
    username: username
  };
  if (username) {
    window.prw.apiCall(
      "POST",
      '/api/users/actions/login',
      reqBody,
      function(user) {
        localStorage.setItem('currentUser', JSON.stringify(user));
        adjustFormsVisibility();
      }
    );
  }
}

function logout() {
  localStorage.removeItem('currentUser');
  adjustFormsVisibility();
}
init();
