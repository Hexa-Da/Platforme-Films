export const API_BASE = import.meta.env.DEV
  ? 'http://localhost:8080/api/v1'
  : (import.meta.env.VITE_API_BASE || 'http://localhost:8080/api/v1');

/** Origine du backend sans /api/v1 (OAuth2 est à la racine du serveur Spring). */
export function getGoogleOAuthUrl() {
  const origin = API_BASE.replace(/\/api\/v1\/?$/, '');
  return `${origin}/oauth2/authorization/google`;
}

function getToken() {
  return localStorage.getItem('token');
}

function authHeaders() {
  const token = getToken();
  return {
    'Content-Type': 'application/json',
    ...(token && { Authorization: `Bearer ${token}` }),
  };
}

export async function login(username, password) {
  const res = await fetch(`${API_BASE}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password }),
  });
  if (!res.ok) throw new Error('Login failed');
  const data = await res.json();
  return data.token;
}

export async function register(username, email, password) {
  const res = await fetch(`${API_BASE}/auth/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, email, password }),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.message || 'Registration failed');
  }
  const data = await res.json();
  return data.token;
}

/**
 * @param {{ title?: string, genre?: string }} [params] — si title et genre sont renseignés, le backend priorise title
 */
export async function getMovies(params = {}) {
  const search = new URLSearchParams();
  const t = params.title?.trim();
  const g = params.genre?.trim();
  if (t) search.set('title', t);
  else if (g) search.set('genre', g);
  const qs = search.toString();
  const url = qs ? `${API_BASE}/movies?${qs}` : `${API_BASE}/movies`;
  const res = await fetch(url, { headers: authHeaders() });
  if (!res.ok) throw new Error('Failed to fetch movies');
  return res.json();
}

export async function getMovie(id) {
  const res = await fetch(`${API_BASE}/movies/${id}`, { headers: authHeaders() });
  if (!res.ok) throw new Error('Failed to fetch movie');
  return res.json();
}

/** @param {{ title: string, director: string, releaseYear: number, genre: string, synopsis: string }} body */
export async function createMovie(body) {
  const res = await fetch(`${API_BASE}/movies`, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify(body),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.message || 'Échec de la création du film');
  }
  return res.json();
}

/** @param {{ title: string, director: string, releaseYear: number, genre: string, synopsis: string }} body */
export async function updateMovie(id, body) {
  const res = await fetch(`${API_BASE}/movies/${id}`, {
    method: 'PUT',
    headers: authHeaders(),
    body: JSON.stringify(body),
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.message || 'Échec de la mise à jour');
  }
  return res.json();
}

export async function deleteMovie(id) {
  const res = await fetch(`${API_BASE}/movies/${id}`, {
    method: 'DELETE',
    headers: authHeaders(),
  });
  if (!res.ok && res.status !== 204) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.message || 'Échec de la suppression');
  }
}

export async function getReviewsByMovie(movieId) {
  const res = await fetch(`${API_BASE}/movies/${movieId}/reviews`);
  if (!res.ok) throw new Error('Impossible de charger les critiques');
  return res.json();
}

export async function getAverageRating(movieId) {
  const res = await fetch(`${API_BASE}/movies/${movieId}/ratings/average`);
  if (!res.ok) throw new Error('Impossible de charger la note moyenne');
  return res.json();
}

export async function getMyProfile() {
  const token = getToken();
  if (!token) throw new Error('Non connecté');
  const res = await fetch(`${API_BASE}/users/me`, {
    headers: authHeaders(),
  });
  if (res.status === 401) throw new Error('Session expirée ou non autorisée');
  if (!res.ok) throw new Error('Impossible de charger le profil');
  return res.json();
}

export function logout() {
  localStorage.removeItem('token');
}
