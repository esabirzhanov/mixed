const endpoint = `api`;

const getGroups = cb => {
  return fetch(`${endpoint}/groups`, {
    accept: "application/json"
  })
    .then(checkStatus)
    .then(parseJSON)
    .then(cb);
}

const getGroup = (groupId, cb) => {
    return fetch(`${endpoint}/groups/${groupId}`, {
    accept: "application/json"
  })
    .then(checkStatus)
    .then(parseJSON)
    .then(cb);
}

const remove = (groupId, cb) => {
  return fetch(`${endpoint}/groups/${groupId}`, {
    method: 'DELETE'
  })
  .then(checkStatus)
  .then(cb);
}

const update = (data, cb) => {
  return fetch(`form-encoded`, {
    method: 'PUT',
    body: data
  })
  .then(checkStatus)
  .then(cb);
}

const update2 = (data, cb) => {
  return fetch(`${endpoint}/groups`, {
    method: 'PUT',
    body: JSON.stringify(data), // data can be `string` or {object}!
    headers:{
      'Content-Type': 'application/json'
    }
  })
  .then(checkStatus)
  .then(cb);
}

const checkStatus = response => {
  if (response.status >= 200 && response.status < 300) {
    return response;
  }
  const error = new Error(`HTTP Error ${response.statusText}`);
  error.status = response.statusText;
  error.response = response;
  console.log(error); // eslint-disable-line no-console
  throw error;
}

const parseJSON = response =>  {
  return response.json();
}

const api = { getGroups, getGroup, remove, update, update2  };
export default api;
