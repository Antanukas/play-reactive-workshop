var repositoryId, repoOwner, repoName;

function init() {
  showUsername();
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
  var currentUserParam = getCurrentUser() ? '?currentUserId=' + getCurrentUser().id : '';
  window.prw.apiCall("GET",
    '/api/repositories/' + repositoryId + '/comments' + currentUserParam,
    null,
    function(comments) {
      updateCommentsList(comments);
      //Register SSE listener
      var commentSource = new EventSource('/api/repositories/' + repositoryId + '/comments' + currentUserParam);
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
  var renderedComponents = comments.map(function(comment) {
    var isLikeVisible = getCurrentUser() && !comment.isUserLiked;
    return '' +
      '<div class="media">' +
      '  <p class="pull-right"><small>' + formatDate(new Date(comment.createdOn)) + '</small></p>' +
      '  <p class="pull-right">' +
      '    <span class="glyphicon glyphicon-thumbs-up"></span><span> ('+ comment.likeCount +')</span>' +
      '  </p>' +
      (isLikeVisible ? '<p class="pull-right"><a class="pull-right" onclick="like('+ comment.id +')"> Like</a></p>' : '') +
      '  <div class="media-body">' +
      '    <h4 class="media-heading user_name">' + comment.username + '</h4>' +
      '    <p>' + comment.text + '</p>' +
      '  </div>' +
      '  </div>' +
      '</div>';
  });
  $('.comments-list').html(renderedComponents);
}

function like(commentId) {
    window.prw.apiCall(
      "POST",
      '/api/comments/' + commentId + '/likes',
      {
        userId: getCurrentUser().id,
        commentId: '' + commentId
      },
      function() {});
    return false;
}

function formatDate(date)  {
  //Best ever date formatting fn
  return date.toISOString().slice(0, 10) + ' '+ date.toString().slice(16, 24);
}
function getCurrentUser() {
  return JSON.parse(localStorage.getItem("currentUser"));
}

function showUsername() {
  var username = getCurrentUser() && getCurrentUser().username;
  if (username) {
    $("#username").text(username);
    $("#username").show();
  } else {
    $("#username").hide();
  }
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
        showUsername();
        location.reload();
      }
    );
  }
}

function logout() {
  localStorage.removeItem('currentUser');
  adjustFormsVisibility();
  showUsername();
  location.reload();
}
init();
