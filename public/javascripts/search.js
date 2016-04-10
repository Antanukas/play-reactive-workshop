console.log('This is search js loaded only in search page');

$('#doSearch').click(function(event) {
    console.log(event);
    $('#searchResultPanel').show();
    var searchText = $('#search').val();
    $.ajax({
      method: "GET",
      data: { search : searchText },
      url: window.prw.searchUrl
    }).done(function(repositories) {
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
    });
});

function _renderRepository(repository, index) {
  return '<li id="' + _repoId(index) + '" class="list-group-item">' +
  '         <span class="badge">' + repository.commentCount + '</span>'  +
            '<a href="/comments/' + repository.gitHubId + '">' + repository.name + '</a>' +
  '       </li>';
}

function _repoId(index) {
  return 'repositoryList' + index;
}