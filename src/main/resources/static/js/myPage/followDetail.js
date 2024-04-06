function clickButton(event) {
  const button = event.target;
  const userIdx = button.getAttribute('data-userid');
  const isFollow = button.classList.contains('un-follow-button');

  const url = isFollow ? `/api/unfollow/${userIdx}` : `/api/follow/${userIdx}`;
  const method = isFollow ? 'DELETE' : 'POST';

  fetch(url, {
    method: method,
    headers: { 'Content-Type': 'application/json' },
  })
  .then(response => {
    if (response.ok) {
      if (isFollow) {
        button.classList.remove('un-follow-button');
        button.classList.add('follow-button');
        button.textContent = '팔로우';
      } else {
        button.classList.remove('follow-button');
        button.classList.add('un-follow-button');
        button.textContent = '언팔로우';
      }
    } else {
      response.text().then(text => {
        alert(text);
      });
    }
  })
  .catch(error => {
    console.error('Error:', error);
  });
}
