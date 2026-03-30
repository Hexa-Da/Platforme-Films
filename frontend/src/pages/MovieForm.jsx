import { useState, useEffect } from 'react'
import { Link, Navigate, useNavigate, useParams } from 'react-router-dom'
import { createMovie, getMovie, updateMovie } from '../api'
import './MovieForm.css'

const emptyForm = {
  title: '',
  director: '',
  releaseYear: '',
  genre: '',
  synopsis: '',
}

export default function MovieForm() {
  const { id } = useParams()
  const navigate = useNavigate()
  const isEdit = id != null

  const [form, setForm] = useState(emptyForm)
  const [loading, setLoading] = useState(isEdit)
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    if (!isEdit) return
    let cancelled = false
    setLoading(true)
    getMovie(id)
      .then((m) => {
        if (cancelled) return
        setForm({
          title: m.title ?? '',
          director: m.director ?? '',
          releaseYear: m.releaseYear != null ? String(m.releaseYear) : '',
          genre: m.genre ?? '',
          synopsis: m.synopsis ?? '',
        })
      })
      .catch((e) => {
        if (!cancelled) setError(e.message)
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })
    return () => {
      cancelled = true
    }
  }, [id, isEdit])

  const token = localStorage.getItem('token')
  if (!token) {
    return <Navigate to="/login" replace />
  }

  const onChange = (field) => (e) => {
    setForm((f) => ({ ...f, [field]: e.target.value }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    const releaseYear = parseInt(form.releaseYear, 10)
    if (Number.isNaN(releaseYear)) {
      setError('Année invalide')
      return
    }
    const body = {
      title: form.title.trim(),
      director: form.director.trim(),
      releaseYear,
      genre: form.genre.trim(),
      synopsis: form.synopsis.trim(),
    }
    setSaving(true)
    try {
      if (isEdit) {
        await updateMovie(id, body)
        navigate(`/movies/${id}`)
      } else {
        await createMovie(body)
        navigate('/movies')
      }
    } catch (err) {
      setError(err.message || 'Erreur')
    } finally {
      setSaving(false)
    }
  }

  if (loading) {
    return (
      <div className="movie-form-page">
        <p>Chargement...</p>
      </div>
    )
  }

  return (
    <div className="movie-form-page">
      <nav className="movie-form-nav">
        <Link to="/movies">← Films</Link>
        {isEdit && (
          <Link to={`/movies/${id}`}>Voir le film</Link>
        )}
      </nav>

      <h1>{isEdit ? 'Modifier le film' : 'Nouveau film'}</h1>

      {error && <p className="error">{error}</p>}

      <form onSubmit={handleSubmit} className="movie-form">
        <label>
          Titre
          <input value={form.title} onChange={onChange('title')} required />
        </label>
        <label>
          Réalisateur
          <input value={form.director} onChange={onChange('director')} required />
        </label>
        <label>
          Année
          <input
            type="number"
            value={form.releaseYear}
            onChange={onChange('releaseYear')}
            required
            min="1888"
            max="2100"
          />
        </label>
        <label>
          Genre
          <input value={form.genre} onChange={onChange('genre')} required />
        </label>
        <label>
          Synopsis
          <textarea value={form.synopsis} onChange={onChange('synopsis')} rows={5} required />
        </label>
        <div className="movie-form-actions">
          <button type="submit" disabled={saving}>
            {saving ? 'Enregistrement…' : isEdit ? 'Mettre à jour' : 'Créer'}
          </button>
          <Link to={isEdit ? `/movies/${id}` : '/movies'}>Annuler</Link>
        </div>
      </form>
    </div>
  )
}
