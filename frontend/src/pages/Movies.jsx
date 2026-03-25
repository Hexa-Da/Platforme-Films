import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { getMovies, logout } from '../api'
import './Movies.css'
import SearchBar from '../components/SearchBar/SearchBar';

export default function Movies() {
  // 1. LA RÈGLE D'OR : TOUS LES HOOKS SONT TOUT EN HAUT
  const [movies, setMovies] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [token] = useState(() => localStorage.getItem('token'))

  // NOUVEAU : On place la mémoire de la recherche ICI, avant les 'return'
  const [recherche, setRecherche] = useState('')

  // Appel au Back-end (inchangé)
  useEffect(() => {
    getMovies()
      .then(setMovies)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  // 2. LES CONDITIONS D'ARRÊT (Early returns)
  if (loading) return <p>Chargement...</p>
  if (error) return <p className="error">Erreur: {error}</p>

  // 3. LA LOGIQUE DE LA PAGE
  const gererLaRecherche = (texte) => {
    setRecherche(texte);
  };

  // On filtre la VRAIE liste venant du Back-end (movies)
  const filmsFiltres = movies.filter((movie) =>
    movie.title.toLowerCase().includes(recherche.toLowerCase())
  );

  // 4. LE RENDU (JSX)
  return (
    <div className="movies-page">
      <nav>
        <Link to="/movies">Films</Link>
        {token ? (
          <button onClick={() => { logout(); window.location.reload(); }}>
            Déconnexion
          </button>
        ) : (
          <>
            <Link to="/login">Connexion</Link>
            <Link to="/register">Inscription</Link>
          </>
        )}
      </nav>

      <h1>Liste des films</h1>

      {/* On insère la barre de recherche ici */}
      <SearchBar onSearch={gererLaRecherche} />

      {/* Si la recherche ne donne rien, on affiche un petit message */}
      {filmsFiltres.length === 0 && <p style={{textAlign: 'center'}}>Aucun film ne correspond à votre recherche.</p>}

      <div className="movies-grid">
        {/* On utilise "filmsFiltres.map" au lieu de "movies.map" ! */}
        {filmsFiltres.map((movie) => (
          <Link key={movie.id} to={`/movies/${movie.id}`} className="movie-card">
            <h3>{movie.title}</h3>
            <p>{movie.director} ({movie.releaseYear})</p>
            <p className="genre">{movie.genre}</p>
          </Link>
        ))}
      </div>
    </div>
  )
}