import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import Login from './pages/Login'
import Register from './pages/Register'
import Movies from './pages/Movies'
import MovieDetail from './pages/MovieDetail'
import MovieForm from './pages/MovieForm'
import Profile from './pages/Profile'
import OAuth2Callback from './pages/OAuth2Callback'
import './App.css'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/movies" replace />} />
        <Route path="/movies" element={<Movies />} />
        <Route path="/movies/new" element={<MovieForm />} />
        <Route path="/movies/:id/edit" element={<MovieForm />} />
        <Route path="/movies/:id" element={<MovieDetail />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/oauth2/callback" element={<OAuth2Callback />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
