
$('#doSearch').click(function(event) {
    $('#searchResultPanel').show();
    var searchText = $('#search').val();
    $('#loading').show();
    $('#repositoriesList').hide();
    window.prw.apiCall("GET",
      '/api/repositories?search=' + searchText,
      null,
      function(repositories) {
        if (repositories && repositories.length > 0) {
          var index = 0;
          var renderedRepositories = repositories.map(function(repository){
            index = index + 1;
            return _renderRepository(repository, index);
          });
          $('#repositoriesList').html(renderedRepositories);
          $('#repositoriesNotFound').hide();
          $('#repositoriesList').show();
        } else {
          $('#repositoriesNotFound').show();
          $('#repositoriesList').hide();
        }
        $('#loading').hide();
        }
      );
});

function _renderRepository(repository, index) {
  return '<li id="' + _repoId(index) + '" class="list-group-item">' +
  '         <img class="avatar-img img-circle" src="' + repository.avatarUrl + '"></img>'  +
            '<div class="repo-title"><h4>' + repository.name + '</h4>  <a href="/comments/' + repository.fullName + '">' + repository.fullName + '</a></div>' +
  '         <span class="badge">' + repository.openIssueCount + ' open issues</span>' +
  '         <span class="badge">'+ repository.commentCount + ' comments</span>'  +
  '       </li>';
}

function _repoId(index) {
  return 'repositoryList' + index;
}