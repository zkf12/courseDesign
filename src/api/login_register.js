import request from '../utils/request';

export function login(username, password) {
  return request({
    url: '/user/login',
    method: 'post',
    data: {
      username,
      password
    }
  });
}

export function register(username, password,confirmPassword) {
    return request({
      url: '/user/register',
      method: 'post',
      data: {
        username,
        password,
        confirmPassword
    }
  });
}

export function logout(){
  return request({
    url: 'user/logout',
    method: 'post'
  });
}